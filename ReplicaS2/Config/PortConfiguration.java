package Config;

import java.util.HashMap;
import model.Location;

public class PortConfiguration {
		
		private static HashMap<Location, Integer> serverConfig;
		private static HashMap<Location, Integer> udpServerConfig;
		private static HashMap<Location, Integer> udpTransfertConfig;
		private static final int DEFAULT_CA_PORT = 5555;
		private static final  int DEFAULT_US_PORT = 7777;
		private static final int DEFAULT_UK_PORT = 4444;
		private static final  int DEFAULT_CORBA_PORT = 1050;
		
		
		public static  int getDEFAULT_CA_PORT() {
			return DEFAULT_CA_PORT;
		}
		public static  int getDEFAULT_US_PORT() {
			return DEFAULT_US_PORT;
		}
		public static  int getDEFAULT_UK_PORT() {
			return DEFAULT_UK_PORT;
		}
		public static int getDEFAULT_CORBA_PORT() {
			return DEFAULT_CORBA_PORT;
		}


		
		private PortConfiguration() {}
		public static HashMap<Location, Integer> getConfig(){
			if(serverConfig == null) {
				serverConfig = new HashMap<Location, Integer>();
			}
			return serverConfig;
		}

		public static HashMap<Location, Integer> getUdpConfig(){
			if(udpServerConfig == null){
				udpServerConfig = new HashMap<Location, Integer>();
			}

			return udpServerConfig;
		}

		public static HashMap<Location, Integer> getUdpTransfertConfig(){
			if(udpTransfertConfig == null){
				udpTransfertConfig = new HashMap<Location, Integer>();
			}

			return udpTransfertConfig;
		}
		
		public static void updateConfig(HashMap<Location, Integer> updateInput) {
			serverConfig = updateInput;
		}

		public static void updateUdpConfig(HashMap<Location, Integer> updatedInput){
			udpServerConfig = updatedInput;
		}

		public static void updateUdpTransfert(HashMap<Location, Integer> updateUdp){
			udpTransfertConfig = updateUdp;
		}
		
		public static void addConfig(Location loc, Integer port) {
			getConfig().put(loc, port);
		}
		
		public static void addConfigUDP(Location loc, Integer port) {
			getUdpConfig().put(loc, port);
		}
		
		public static void addConfigUDPTransfert(Location loc, Integer port) {
			getUdpTransfertConfig().put(loc, port);
		}
		
		public static Location getLocationFromPort(HashMap<Location, Integer> mapping, Integer port) {
			    for (Location loc : mapping.keySet()) {
			      if (mapping.get(loc) == (port)) {
			        return loc;
			      }
			    }
			    return null;
		}
		
}
