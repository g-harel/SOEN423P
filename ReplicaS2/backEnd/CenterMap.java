package backEnd;

import java.util.HashMap;

import model.Location;
import shared.HRActions;

public class CenterMap {
	public static HashMap<Location, HRActions> map = null;
	private CenterMap() {};
	
	public static HashMap<Location, HRActions> getMap() {
		if(map ==null) {
			map = new HashMap<Location, HRActions>();
		}
		
		return map;
	}
	
	public static void addHRAction(Location loc, HRActions store) {
		getMap().put(loc, store);
	}
	
	public static HRActions getByLocationName(String locName) {
		for(Location loc : map.keySet()) {
			if(loc.toString().equalsIgnoreCase(locName)) {
				return map.get(loc);
			}
		}
		return null;
	}
	
	

}
