/*
    MIT License

    Copyright (c) 2018 Chris Mc, prince.chrismc(at)gmail(dot)com

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.
 */
package FrontEnd;

import Models.AddressBook;
import UDP.Message;
import UDP.OperationCode;
import UDP.Socket;
import Utility.ClientRequest;
import Interface.Corba.IFrontEndPOA;
import Interface.Corba.Project;
import Models.RegisteredReplica;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.SocketException;
import java.util.LinkedList;

/**
 *
 * @author cmcarthur
 */
public class FrontEnd extends IFrontEndPOA {

    final private Socket socket;
    final private Listener m_RequestListener;
    private int requiredAnswersForAgreement;

    public FrontEnd() throws SocketException {
        this.socket = new Socket();
        requiredAnswersForAgreement = 1; //3;
        m_RequestListener = new Listener();
        m_RequestListener.launch();
    }

    @Override
    public String createMRecord(String managerID, String firstName, String lastName, int employeeID, String mailID, Project project, String location) {
        String mangerPrefix = managerID.substring(0, 2);
        ClientRequest request = new ClientRequest("createMRecord", mangerPrefix);

        request.addRequestDataEntry("managerID", managerID);
        request.addRequestDataEntry("firstName", firstName);
        request.addRequestDataEntry("lastName", lastName);
        request.addRequestDataEntry("employeeID", employeeID);
        request.addRequestDataEntry("mailID", mailID);
        request.addRequestDataEntry("project", project);
        request.addRequestDataEntry("location", location);

        try {
            sendRequestToSequencer(OperationCode.CREATE_MANAGER_RECORD, request);
        } catch (Exception ex) {
            return ex.getMessage();
        }
        return getResults();
    }

    @Override
    public String createERecord(String managerID, String firstName, String lastName, int employeeID, String mailID, String projectID) {
        String mangerPrefix = managerID.substring(0, 2);
        ClientRequest request = new ClientRequest("createERecord", mangerPrefix);

        request.addRequestDataEntry("managerID", managerID);
        request.addRequestDataEntry("firstName", firstName);
        request.addRequestDataEntry("lastName", lastName);
        request.addRequestDataEntry("employeeID", employeeID);
        request.addRequestDataEntry("mailID", mailID);
        request.addRequestDataEntry("projectID", projectID);

        try {
            sendRequestToSequencer(OperationCode.CREATE_EMPLOYEE_RECORD, request);
        } catch (Exception ex) {
            return ex.getMessage();
        }
        return getResults();
    }

    @Override
    public String getRecordCounts(String managerID) {
        String mangerPrefix = managerID.substring(0, 2);
        ClientRequest request = new ClientRequest("getRecordCounts", mangerPrefix);

        try {
            sendRequestToSequencer(OperationCode.GET_RECORD_COUNT, request);
        } catch (Exception ex) {
            return ex.getMessage();
        }
        
        return getResults();
    }

    @Override
    public String editRecord(String managerID, String recordID, String fieldName, String newValue) {
        String mangerPrefix = managerID.substring(0, 2);
        ClientRequest request = new ClientRequest("editRecord", mangerPrefix);

        request.addRequestDataEntry("managerID", managerID);
        request.addRequestDataEntry("recordID", recordID);
        request.addRequestDataEntry("fieldName", fieldName);
        request.addRequestDataEntry("newValue", newValue);

        try {
            sendRequestToSequencer(OperationCode.EDIT_RECORD, request);
        } catch (Exception ex) {
            return ex.getMessage();
        }

        return getResults();
    }

    @Override
    public String transferRecord(String managerID, String recordID, String location) {
        String mangerPrefix = managerID.substring(0, 2);
        ClientRequest request = new ClientRequest("transferRecord", mangerPrefix);

        request.addRequestDataEntry("managerID", managerID);
        request.addRequestDataEntry("recordID", recordID);
        request.addRequestDataEntry("location", location);

        try {
            sendRequestToSequencer(OperationCode.TRANSFER_RECORD, request);
        } catch (Exception ex) {
            return ex.getMessage();
        }

        return getResults();
    }

    @Override
    public void softwareFailure(String managerID) {
        requiredAnswersForAgreement--;
    }

    @Override
    public void replicaCrash(String managerID) {
        requiredAnswersForAgreement--;
    }

    private void sendRequestToSequencer(OperationCode method, ClientRequest clientRequest) throws Exception {
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        ObjectOutput oo = new ObjectOutputStream(bStream);
        oo.writeObject(clientRequest);
        oo.close();

        byte[] serializedClientRequest = bStream.toByteArray();
        String payload = new String(serializedClientRequest);

        Message messageToSend = null;
        try {
            messageToSend = new Message(method, 0, payload, AddressBook.SEQUENCER);
        } catch (Exception ex) {
            System.out.println("Message was too big!");
        }

        if (messageToSend != null) {
            if (!socket.send(messageToSend, 5, 1000)) {
                throw new Exception("Unable to process message within system!");
            }
        }
    }
    
    private String getResults(){
        int seqNumber = Integer.valueOf(socket.getResponse().getData().substring("SEQ=".length()));

        ConsensusTracker tracker = new ConsensusTracker(requiredAnswersForAgreement, seqNumber);

        m_RequestListener.setTracker(tracker);

        try {
            tracker.Wait();
        } catch (InterruptedException ex) {
            return "Error: could not obtain answer";
        }

        m_RequestListener.setTracker(null);

        processesFailures(tracker);

        return tracker.getAnswer();
    }
    

    private void processesFailures(ConsensusTracker tracker) {
        processesFailuresBadResponses(tracker);
        processesMissingResponses(tracker);
    }

    private void processesFailuresBadResponses(ConsensusTracker tracker) {
        LinkedList<RegisteredReplica> inError = tracker.getFailures();

        RegisteredReplica errors[] = new RegisteredReplica[inError.size()];
        errors = inError.toArray(errors);

        Message notice = null;
        try {
            notice = new Message(OperationCode.FAULY_RESP_NOTIFICATION, 0, "Your replica Errored!", AddressBook.MANAGER);
        } catch (Exception ex) {
            // This is impossible
        }

        try {
            socket.sendTo(errors, notice, 10, 1000);
        } catch (Exception ex) {
            System.out.println("Unable to notify Replica Manager of failure due to " + ex.getMessage());
        }
    }

    private void processesMissingResponses(ConsensusTracker tracker) {
        LinkedList<RegisteredReplica> inError = tracker.getMissingAnswers();

        RegisteredReplica errors[] = new RegisteredReplica[inError.size()];
        errors = inError.toArray(errors);

        Message notice = null;
        try {
            notice = new Message(OperationCode.NO_RESP_NOTIFICATION, 0, "Your replica Errored!", AddressBook.MANAGER);
        } catch (Exception ex) {
            // This is impossible
        }

        try {
            socket.sendTo(errors, notice, 10, 1000);
        } catch (Exception ex) {
            System.out.println("Unable to notify Replica Manager of failure due to " + ex.getMessage());
        }
    }

    public void shutdown() throws InterruptedException {
        m_RequestListener.shutdown();
    }
}
