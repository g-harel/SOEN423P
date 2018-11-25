package Manager;

import UDP.Message;
import model.ManagersMap;
import model.RegisteredReplica;

/**
 * Handles message , parse if needed and redirect to the right Replica Manager
 * @author winterhart
 *
 */
public class ManagerMessageHandler {
	
	public ManagerMessageHandler() {
		
	}
	public String HandleRequest(Message msg) {
		
		//Find out which Replica is targeted
		RegisteredReplica replicaFound = parseFindReplicaName(msg.getData());
		if(replicaFound == null) {
			//TODO: log this, error
			System.out.println("Was not able to find the replica name");
			return null;
		}
		
		// Grab the manager current instance
		ManagersMap currentManagers = ManagersMap.getManagers();
		Manager currentManager = currentManagers.get(replicaFound);
		
		// Do action based on msg...
		switch(msg.getOpCode()) {
		case NO_RESP_NOTIFICATION:
			System.out.println("No Resp Notif");
			//TODO: Add logging here
			currentManager.registerCrashFailure(msg.getSeqNum());
			break;
		case FAULY_RESP_NOTIFICATION:
			System.out.println("FAULY RESP Notification");
			currentManager.registerNonByzFailure(msg.getSeqNum());
			//TODO: Add logging here
			break;
		default:
			System.out.println("Operation not recognized");
			//TODO: Add logging here
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
