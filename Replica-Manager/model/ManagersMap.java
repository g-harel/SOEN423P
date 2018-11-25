package model;

import java.util.HashMap;

import Manager.Manager;
import Manager.RegisteredReplica;

public class ManagersMap extends HashMap<RegisteredReplica, Manager> {
	private static final long serialVersionUID = 1L;
	private static ManagersMap inst = null;
	private ManagersMap() {};
	
	public static void addManager(RegisteredReplica replicaName, Manager manager) {
		getManagers();
		inst.put(replicaName, manager);
	}
	
	public static ManagersMap getManagers() {
		if(inst == null) {
			inst = (ManagersMap) new HashMap<RegisteredReplica, Manager>();
		}
		return inst;
	}
	
	
	
	

}
