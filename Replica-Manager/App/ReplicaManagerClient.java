package App;

import Manager.ManagerHub;

/**
 * Class used to start the replica Manager
 * @author winterhart
 *
 */

public class ReplicaManagerClient {
	
	public static void main(String[] args) {
		System.out.println("Launching the Replica Manager...");
		ManagerHub managerHub = new ManagerHub();
		managerHub.launch();
		
	}
	
	

}
