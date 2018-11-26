/*

Name: Andres Vazquez (#40007182)
Course: SOEN 423

*/

package App.Remote;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;

import Interface.Corba.Project;
import Models.AddressBook;
import Models.Location;
import Models.RegisteredReplica;
import UDP.Message;
import UDP.OperationCode;
import UDP.RequestListener;
import UDP.RequestListener.Processor;
import Utility.ClientRequest;
import Utility.ReplicaResponse;

public class Replica {
	
	private RegisteredReplica replicaID = RegisteredReplica.ReplicaS1;
	private static HashMap<String, CenterServer> centerServers = new HashMap<>();
	private HashMap<Integer, Message> messagesQueue = new HashMap<>();	
	private int nextSequenceID = 0;
	
	private class ReplicaListerner implements Processor {
    	final private RequestListener m_Listener;
    	private Thread m_ListenerThread;
    	
    	public ReplicaListerner() {
    		m_Listener = new RequestListener(this, AddressBook.REPLICAS);
    	}
    	
		public void launch(){
			m_ListenerThread = new Thread(m_Listener);
			m_ListenerThread.start();
//			m_Listener.Wait(); // Make sure it's running before getting any farther ( optional )
		}
		
		public void shutdown() {
			m_Listener.Stop();
			
			try {
				m_ListenerThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		/**
	 	 *  Operation Handled:
	 	 *  SERIALIZE(1011)
	 	 */
		@Override
		public String handleRequestMessage(Message msg) throws Exception {
			if (msg.getOpCode() == OperationCode.SERIALIZE && msg.getData().contains("ClientRequest")) {
				
				int sequenceID = msg.getSeqNum();
				
				if(sequenceID == nextSequenceID) {
					nextSequenceID++;
				}
				else if(sequenceID > nextSequenceID) {
					messagesQueue.put(sequenceID, msg);
				}
				
				ReplicaResponse replicaResponse = Replica.this.processRequest(msg);
				
				return replicaResponse;
				// Check if next request is in messagesQueue
				if(messagesQueue.get(nextSequenceID) != null) {
					handleRequestMessage(messagesQueue.get(nextSequenceID));
				}
            }
            else {
            	throw new IOException("The request received (" + msg.getSeqNum() + ") is not valid.\n" + 
            							"Requests need to hava the OperationCode `" + OperationCode.SERIALIZE + "` and " +
            							"a serialized ClientRequest object as data.");
            }
			return null;
		}
    }
    
	public static void main(String[] args) {
            for (Location loc: Location.values()) {
            	if (loc == Location.INVALID) {
                    continue;
                }
                CenterServer location_server = new CenterServer(loc);
                Thread location_server_thread = new Thread(location_server);
                location_server_thread.start();
                
                centerServers.put(loc.getPrefix(), location_server);
            }

    }
	
	private ReplicaResponse processRequest(Message message) {
		ClientRequest clientRequest = null;
		CenterServer server = null;
		
		try {
			byte b[] = message.getData().getBytes(); 
		     ByteArrayInputStream bi = new ByteArrayInputStream(b);
		     ObjectInputStream si = new ObjectInputStream(bi);
		     clientRequest = (ClientRequest) si.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		ReplicaResponse replicaResponse = null;
		
		switch (clientRequest.getMethod()) {
		case "createMRecord":
			String managerID = (String) clientRequest.getData().get("managerID");
			String firstName = (String) clientRequest.getData().get("firstName");
			String lastName = (String) clientRequest.getData().get("lastName");
			int employeeID = (int) clientRequest.getData().get("employeeID");
			String mailID = (String) clientRequest.getData().get("mailID");
			Project project = (Project) clientRequest.getData().get("project");
			String location = (String) clientRequest.getData().get("location");
			
			server = centerServers.get(clientRequest.getLocation());
			replicaResponse = server.createMRecord(managerID, firstName, lastName, employeeID, mailID, project, location);
			
			replicaResponse.setResponse("New manager created successfully");
			break;
		case "createERecord":
			String managerID2 = (String) clientRequest.getData().get("managerID");
			String firstName2 = (String) clientRequest.getData().get("firstName");
			String lastName2 = (String) clientRequest.getData().get("lastName");
			int employeeID2 = (int) clientRequest.getData().get("employeeID");
			String mailID2 = (String) clientRequest.getData().get("mailID");
			String projectID = (String) clientRequest.getData().get("projectID");
			
			server = centerServers.get(clientRequest.getLocation());
			replicaResponse = server.createERecord(managerID2, firstName2, lastName2, employeeID2, mailID2, projectID);
			
			replicaResponse.setResponse("New employee created successfully");
			break;
		case "getRecordCounts":
			String managerID3 = (String) clientRequest.getData().get("managerID");
			
			server = centerServers.get(clientRequest.getLocation());
			replicaResponse = server.getRecordCounts(managerID3);
			break;
		case "editRecord":
			String managerID4 = (String) clientRequest.getData().get("managerID");
			String recordID = (String) clientRequest.getData().get("recordID");
			String fieldName = (String) clientRequest.getData().get("fieldName");
			String newValue = (String) clientRequest.getData().get("newValue");
			
			server = centerServers.get(clientRequest.getLocation());
			replicaResponse = server.editRecord(managerID4, recordID, fieldName, newValue);
			break;
		case "transferRecord":
			String managerID5 = (String) clientRequest.getData().get("managerID");
			String recordID2 = (String) clientRequest.getData().get("recordID");
			String location2 = (String) clientRequest.getData().get("location");
			
			server = centerServers.get(clientRequest.getLocation());
			replicaResponse = server.transferRecord(managerID5, recordID2, location2);
			break;
		case "softwareFailure":
			// TODO: Do something to get a wrong answer only for 1 replica
			break;
		case "replicaCrash":
			// TODO: Do something to "crash" the replica
			break;
		default:
			System.out.println("The method name received is not known.");
			break;
		}
		
		replicaResponse.setSequenceID(message.getSeqNum());
		
		return replicaResponse;
	}
	
	// Starts the UDP RequestListener to receive the responses from the Replicas
    public void startUDPListener() {
    	ReplicaListerner frontEndListerner = new ReplicaListerner();
    	frontEndListerner.launch();

    	System.out.println("Replica (ReplicaS1) started to listen for UDP requests on port: " + AddressBook.REPLICAS.getPort());
    }
}
