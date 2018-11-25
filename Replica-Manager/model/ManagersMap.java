package model;

import java.util.HashMap;

import Manager.Manager;

public class ManagersMap {
	private static final long serialVersionUID = 1L;
	private static HashMap<RegisteredReplica, Manager> map; 
	private static ManagersMap inst = null;
	private ManagersMap() {};
	
	public synchronized static void addManager(RegisteredReplica replicaName, Manager manager) {
		getManagersMap();
		map.put(replicaName, manager);
		
		
	}
	
	public synchronized static HashMap<RegisteredReplica, Manager> getManagersMap() {
		if(inst == null) {
			inst = new ManagersMap();
		}
		
		if(map == null) {
			map = new HashMap<RegisteredReplica, Manager>();
		}
		
		return map;
	}
	
	
	
	

}
