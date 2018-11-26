package Utility;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import Interface.Corba.Project;
import Models.AddressBook;
import Models.RegisteredReplica;
import UDP.Message;
import UDP.OperationCode;
import UDP.RequestListener;
import UDP.RequestListener.Processor;

public abstract class AbstractReplica {
	protected RegisteredReplica replicaID;
	protected static HashMap<String, ICenterServer> centerServers = new HashMap<>();
	protected HashMap<Integer, Message> messagesQueue = new HashMap<>();	
	protected int nextSequenceID = 0;
	
	protected class ReplicaListerner implements Processor {
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
				
				ReplicaResponse replicaResponse = AbstractReplica.this.processRequest(msg);
				String serializedResponse = serializeReplicaResponse(replicaResponse);
				
				return serializedResponse;
				
				// Check if next request is in messagesQueue
//				if(messagesQueue.get(nextSequenceID) != null) {
//					handleRequestMessage(messagesQueue.get(nextSequenceID));
//				}
            }
            else {
            	throw new IOException("The request received (" + msg.getSeqNum() + ") is not valid.\n" + 
            							"Requests need to hava the OperationCode `" + OperationCode.SERIALIZE + "` and " +
            							"a serialized ClientRequest object as data.");
            }
		}
		
		private String serializeReplicaResponse(ReplicaResponse response) {
			String serializedResponse = null;
			
	        try {
	            ByteArrayOutputStream bStream = new ByteArrayOutputStream();
	            ObjectOutput oo = new ObjectOutputStream(bStream);
	            oo.writeObject(response);
	            oo.close();

	            serializedResponse = new String(bStream.toByteArray());
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        
	        return serializedResponse;
		}
    }
	
	
	
	public AbstractReplica(RegisteredReplica replicaID) {
		this.replicaID = replicaID;
	}
	
	protected ReplicaResponse processRequest(Message message) {
		ClientRequest clientRequest = null;
		ICenterServer server = null;
		
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
		
//		replicaResponse.setReplicaID(replicaID);
		replicaResponse.setSequenceID(message.getSeqNum());
		
		return replicaResponse;
	}
}
