package App;

import java.util.HashMap;
import Manager.Manager;

public class ManagersMap extends HashMap<String, Manager> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static ManagersMap inst = null;
	private ManagersMap() {};
	
	public static void addManager(Manager manager) {
		ManagersMap local = getManagers();
		local.put(manager.getManagerName(), manager);
	}
	
	public static ManagersMap getManagers() {
		if(inst == null) {
			inst = (ManagersMap) new HashMap<String, Manager>();
		}
		return inst;
	}
	
	
	
	

}
