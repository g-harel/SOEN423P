package Manager;

import UDP.Message;

/**
 * Handles message , parse if needed and redirect to the right Replica Manager
 * @author winterhart
 *
 */
public class ManagerMessageHandler {
	
	public String HandleRequest(Message msg) {
		
		RegisteredReplica replicaFound = parseFindReplicaName(msg.getData());
		if(replicaFound == null) {
			//TODO: log this, error
			System.out.println("Was not able to find the replica name";);
			return null;
		}
		switch(msg.getOpCode()) {
		case NO_RESP_NOTIFICATION:
			System.out.println("No Resp Notif");
			
			break;
		case ACK_NO_RESP_NOTIFICATION:
			System.out.println("Ack Resp");
			break;
		case FAULY_RESP_NOTIFICATION:
			System.out.println("FAULY RESP Notification");
			break;
		case ACK_FAULY_RESP_NOTIFICATION:
			System.out.println("Acknowledge Fauly Resp. Notif");
			break;
		default:
			System.out.println("Operation not recognized");
			break;
		}
		return null;
	}
	
	
	private RegisteredReplica parseFindReplicaName(String data) {
		
		for(RegisteredReplica replicaName: RegisteredReplica.values()) {
			if(data.contains(replicaName.toString())) {
				return replicaName;
			}
		}
		
		return null;
	}

}
