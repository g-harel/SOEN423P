package backEnd;

import java.util.ArrayList;
import java.util.List;

import shared.UDP.ServerUDP;

public class InternalServers {
	private static List<ServerUDP> internalServers = null;
	
	public static List<ServerUDP> getServers(){
		if(internalServers == null) {
			internalServers = new ArrayList<ServerUDP>();
		}
		
		return internalServers;
	}
	
	public static void addServer(ServerUDP server) {
		getServers().add(server);
	}

}
