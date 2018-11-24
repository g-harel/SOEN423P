package App;

import java.util.List;
import Manager.Manager;

/**
 * Class used to start the replica Manager
 * @author winterhart
 *
 */

public class App {
	public App() {
		super();
	}
	
	public void registerNewManager(String managerName) {
		//TOOD: Add Paremeter to Manager...
		Manager manager = new Manager();
		ManagersMap.addManager(manager);
		
	}
	
	public void start() {
		//TODO: Start a thread for each Manager registered...for each start	
	}
	
	

}
