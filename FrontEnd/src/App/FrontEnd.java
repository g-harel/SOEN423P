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

import Models.AddressBook;
import Models.Location;
import UDP.Message;
import UDP.RequestListener;
import UDP.RequestListener.Processor;
import UDP.Socket;
import Utility.ClientRequest;
import Utility.ReplicaResponse;
import Interface.Corba.IFrontEnd;
import Interface.Corba.IFrontEndHelper;
import Interface.Corba.IFrontEndPOA;
import Interface.Corba.Project;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

/**
 *
 * @author cmcarthur
 */
public class FrontEnd extends IFrontEndPOA {

	private ConsensusTracker consensusTracker = new ConsensusTracker(3);
    private InetAddress sequencerIP = AddressBook.SEQUENCER.getAddr();
    private int sequencerPort = AddressBook.SEQUENCER.getPort();
    private int frontEndPort = AddressBook.FRONTEND.getPort();

    private class ReplicaResponseProcessor implements Processor {
		@Override
		public String handleRequestMessage(Message msg) throws Exception {
			if (msg.getData().contains("ReplicaResponse")) {
                ObjectInputStream iStream = new ObjectInputStream(new ByteArrayInputStream(msg.getData().getBytes()));
                Object input = iStream.readObject();
                ReplicaResponse replicaResponse;
                iStream.close();

                if (input instanceof ReplicaResponse) {
                	replicaResponse = (ReplicaResponse) input;
                }
                else {
                    throw new IOException("Data received is not valid.");
                }

                // TODO: What to do with ReplicaResponse
                int sequenceID = msg.getSeqNum();
                String answer = replicaResponse.getResponse();

                RequestConsensus requestConsensus = consensusTracker.getRequestConsensus(sequenceID);

                if(requestConsensus == null) {
                	requestConsensus = new RequestConsensus(answer, msg.getAddress(), msg.getPort());
                	consensusTracker.addRequestConsensus(sequenceID, requestConsensus);
                }
                else {
                	requestConsensus.addAnswer(answer, msg.getAddress(), msg.getPort());

                	if(requestConsensus.shouldSendConsensus()) {
                		List<ReplicaInfo> softwareFailures = requestConsensus.getSoftwareFailures();

                		if(softwareFailures.size() > 0) {
                			for(ReplicaInfo replica: softwareFailures) {
                				sendMulticastToRMs(replica.getReplicaAddress(), replica.getReplicaPort());
                			}
                    	}

                		return requestConsensus.getConsensusAnswer();
                	}
                }
            }
            else {
            	throw new IOException("Response is not a ReplicaResponse.");
            }
			return null;
		}
    }

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
            frontEnd.startUDPListener();	// Start Front-end UDP server

            org.omg.CORBA.Object ref = rootpoa.servant_to_reference(frontEnd);
            IFrontEnd href = IFrontEndHelper.narrow(ref);

            // bind the Object Reference in Naming
            NameComponent path[] = ncRef.to_name(AddressBook.FRONTEND.getShortHandName());
            ncRef.rebind(path, href);

            System.out.println("The Front-end (CORBA) is now running on port 1050 ...");

            // wait for invocations from clients
            orb.run();

        } catch (Exception e) {
            System.out.println("Failed to start the Front-End!");
            e.printStackTrace();
        }
    }

    @Override
    public synchronized String createMRecord(String managerID, String firstName, String lastName, int employeeID, String mailID, Project project, String location) {
        ClientRequest request = setupClientRequest(managerID);

        request.addRequestDataEntry("managerID", managerID);
        request.addRequestDataEntry("firstName", firstName);
        request.addRequestDataEntry("lastName", lastName);
        request.addRequestDataEntry("employeeID", employeeID);
        request.addRequestDataEntry("mailID", mailID);
        request.addRequestDataEntry("project", project);
        request.addRequestDataEntry("location", location);

        sendRequestToSequencer(request);
        return "";
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

        sendRequestToSequencer(request);
        return "";
    }

    @Override
    public String getRecordCounts(String managerID) {
        Map<Location, Integer> recordCount = new HashMap<>();

        return recordCount.toString();
    }

    @Override
    public synchronized String editRecord(String managerID, String recordID, String fieldName, String newValue) {
        ClientRequest request = setupClientRequest(managerID);

        request.addRequestDataEntry("managerID", managerID);
        request.addRequestDataEntry("recordID", recordID);
        request.addRequestDataEntry("fieldName", fieldName);
        request.addRequestDataEntry("newValue", newValue);

        sendRequestToSequencer(request);
        return "";
    }

    @Override
    public String transferRecord(String managerID, String recordID, String location) {
        ClientRequest request = setupClientRequest(managerID);

        request.addRequestDataEntry("managerID", managerID);
        request.addRequestDataEntry("recordID", recordID);
        request.addRequestDataEntry("location", location);

        sendRequestToSequencer(request);
        return "";
    }

	@Override
	public void softwareFailure(String managerID) {
		ClientRequest request = setupClientRequest(managerID);
		sendRequestToSequencer(request);
	}

	@Override
	public void replicaCrash(String managerID) {
		consensusTracker.decrementConsensusCountNeeded();

		ClientRequest request = setupClientRequest(managerID);
		sendRequestToSequencer(request);
	}

	private ClientRequest setupClientRequest(String managerID) {
		String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
		String location = managerID.substring(0, 2);

		return new ClientRequest(methodName, location);
	}

	private void sendRequestToSequencer(ClientRequest clientRequest) {
		System.out.println(clientRequest);

    	ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        ObjectOutput oo;

		try {
			oo = new ObjectOutputStream(bStream);
			oo.writeObject(clientRequest);
	        oo.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

        byte[] serializedMessage = bStream.toByteArray();

        DatagramPacket requestPacket = new DatagramPacket(serializedMessage, serializedMessage.length, sequencerIP, sequencerPort);

		sendUDPRequest(requestPacket);
	}

	private void sendMulticastToRMs(InetAddress replicaIP, int replicaPort) {

	}

    private void sendUDPRequest(DatagramPacket requestPacket) {
    	Message messageToSend = new Message(requestPacket);

    	try {
    		Socket socket = new Socket();
			socket.send(messageToSend, 5, 2000);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    // Starts the UDP RequestListener to receive the responses from the Replicas
    public void startUDPListener() {
    	Processor requestProcessor = new ReplicaResponseProcessor();
    	RequestListener requestListener = new RequestListener(requestProcessor, AddressBook.REPLICAS);
    	requestListener.run();

    	System.out.println("Front-end started to listen for UDP requests on port: " + frontEndPort);
    }
}
