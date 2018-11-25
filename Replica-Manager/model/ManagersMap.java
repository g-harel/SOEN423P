package model;

import java.util.HashMap;

import Manager.Manager;

public class ManagersMap extends HashMap<RegisteredReplica, Manager> {
	private static final long serialVersionUID = 1L;
	private static ManagersMap inst = null;
	private ManagersMap() {};
	
	public synchronized static void addManager(RegisteredReplica replicaName, Manager manager) {
		getManagers();
		inst.put(replicaName, manager);
	}
	
	public synchronized static ManagersMap getManagers() {
		if(inst == null) {
			inst = (ManagersMap) new HashMap<RegisteredReplica, Manager>();
		}
		return inst;
	}
	
	
	
	

}
