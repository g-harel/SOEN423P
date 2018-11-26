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
package App;

import FrontEnd.Listener;
import Models.AddressBook;
import Models.Location;
import UDP.Message;
import UDP.OperationCode;
import UDP.Socket;
import Utility.ClientRequest;
import Interface.Corba.IFrontEnd;
import Interface.Corba.IFrontEndHelper;
import Interface.Corba.IFrontEndPOA;
import Interface.Corba.Project;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;

/**
 *
 * @author cmcarthur
 */
public class FrontEnd extends IFrontEndPOA {

    final private Socket socket;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length < 4) {
            args = Stream.of("-ORBInitialPort", "1050", "-ORBInitialHost", "localhost").toArray(String[]::new);
        }
        try {
            // create and initialize the ORB
            ORB orb = ORB.init(args, null);

            // get reference to rootpoa & activate the POAManager
            POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            rootpoa.the_POAManager().activate();

            // get the root naming context
            // NameService invokes the name service
            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");

            // Use NamingContextExt which is part of the Interoperable
            // Naming Service (INS) specification.
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

            // get object reference from the servant
            FrontEnd frontEnd = new FrontEnd();

            org.omg.CORBA.Object ref = rootpoa.servant_to_reference(frontEnd);
            IFrontEnd href = IFrontEndHelper.narrow(ref);

            // bind the Object Reference in Naming
            NameComponent path[] = ncRef.to_name(AddressBook.FRONTEND.getShortHandName());
            ncRef.rebind(path, href);

            System.out.println("The Front-end (CORBA) is now running on port 1050 ...");

            Listener requestListener = new Listener();
            requestListener.launch();

            // wait for invocations from clients
            orb.run();

            requestListener.shutdown();

        } catch (InterruptedException | SocketException | InvalidName | CannotProceed | org.omg.CosNaming.NamingContextPackage.InvalidName | NotFound | AdapterInactive | ServantNotActive | WrongPolicy e) {
            System.out.println("Failed to start the Front-End!");
            e.printStackTrace();
        }
    }

    public FrontEnd() throws SocketException {
        this.socket = new Socket();
    }

    @Override
    public String createMRecord(String managerID, String firstName, String lastName, int employeeID, String mailID, Project project, String location) {
        ClientRequest request = setupClientRequest(managerID);

        request.addRequestDataEntry("managerID", managerID);
        request.addRequestDataEntry("firstName", firstName);
        request.addRequestDataEntry("lastName", lastName);
        request.addRequestDataEntry("employeeID", employeeID);
        request.addRequestDataEntry("mailID", mailID);
        request.addRequestDataEntry("project", project);
        request.addRequestDataEntry("location", location);

        try {
            sendRequestToSequencer(request);
        } catch (Exception ex) {
            return ex.getMessage();
        }
        
        return socket.getResponse().getData();
    }

    @Override
    public synchronized String createERecord(String managerID, String firstName, String lastName, int employeeID, String mailID, String projectID) {
        ClientRequest request = setupClientRequest(managerID);

        request.addRequestDataEntry("managerID", managerID);
        request.addRequestDataEntry("firstName", firstName);
        request.addRequestDataEntry("lastName", lastName);
        request.addRequestDataEntry("employeeID", employeeID);
        request.addRequestDataEntry("mailID", mailID);
        request.addRequestDataEntry("projectID", projectID);

        try {
            sendRequestToSequencer(request);
        } catch (Exception ex) {
            return ex.getMessage();
        }
        
        return socket.getResponse().getData();
    }

    @Override
    public String getRecordCounts(String managerID) {
        Map<Location, Integer> recordCount = new HashMap<>();

        return recordCount.toString();
    }

    @Override
    public String editRecord(String managerID, String recordID, String fieldName, String newValue) {
        ClientRequest request = setupClientRequest(managerID);

        request.addRequestDataEntry("managerID", managerID);
        request.addRequestDataEntry("recordID", recordID);
        request.addRequestDataEntry("fieldName", fieldName);
        request.addRequestDataEntry("newValue", newValue);

        try {
            sendRequestToSequencer(request);
        } catch (Exception ex) {
            return ex.getMessage();
        }
        
        return socket.getResponse().getData();
    }

    @Override
    public String transferRecord(String managerID, String recordID, String location) {
        ClientRequest request = setupClientRequest(managerID);

        request.addRequestDataEntry("managerID", managerID);
        request.addRequestDataEntry("recordID", recordID);
        request.addRequestDataEntry("location", location);

        try {
            sendRequestToSequencer(request);
        } catch (Exception ex) {
            return ex.getMessage();
        }
        
        return socket.getResponse().getData();
    }

    @Override
    public void softwareFailure(String managerID) {
        ClientRequest request = setupClientRequest(managerID);
        try {
            sendRequestToSequencer(request);
        } catch (Exception ex) {
            Logger.getLogger(FrontEnd.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void replicaCrash(String managerID) {
        //m_ConsensusTracker.decrementConsensusCountNeeded();

        ClientRequest request = setupClientRequest(managerID);
        try {
            sendRequestToSequencer(request);
        } catch (Exception ex) {
            Logger.getLogger(FrontEnd.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private ClientRequest setupClientRequest(String managerID) {
        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
        String location = managerID.substring(0, 2);

        return new ClientRequest(methodName, location);
    }

    private void sendRequestToSequencer(ClientRequest clientRequest) throws Exception {
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        ObjectOutput oo = new ObjectOutputStream(bStream);
        oo.writeObject(clientRequest);
        oo.close();

        byte[] serializedClientRequest = bStream.toByteArray();
        String payload = new String(serializedClientRequest);

        Message messageToSend = null;
        try {
            messageToSend = new Message(OperationCode.SERIALIZE, 0, payload, AddressBook.SEQUENCER);
        } catch (Exception ex) {
            System.out.println("Message was too big!");
        }

        if (messageToSend != null) {
            if( ! socket.send(messageToSend, 5, 1000) )
                throw new Exception("Unable to process message within system!");
        }
    }
}
