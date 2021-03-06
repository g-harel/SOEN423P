package backEnd;

import config.PortConfiguration;
import config.StorageConfig;
import model.Location;
import shared.HRActions;
import shared.UDP.RecordCounterUDP;
import shared.UDP.ServerUDP;
import shared.UDP.TransfertServerUDP;
import storage.IStore;
import storage.Logger;

public class ServerConfigurator {
	

	
	private IStore configStoring;
	public ServerConfigurator() {
		 configStoring	= 
				new Logger("ServerConfigurator", StorageConfig.CENTRAL_REPO_LOCATION);
	}
	
	 public void configureCenter() {

		for(Location loc: Location.values()) {
			//TODO: Refactor this switch
			switch(loc){
			case CA:
				buildCenter(loc, PortConfiguration.getDEFAULT_CA_PORT());
				// Save configuration in an object, in a real server we will save this to a .env file or other
				PortConfiguration.addConfig(loc, PortConfiguration.getDEFAULT_CA_PORT());
				break;
			case US:
				buildCenter(loc, PortConfiguration.getDEFAULT_US_PORT());
				// Save configuration in an object, in a real server we will save this to a .env file or other
				PortConfiguration.addConfig(loc, PortConfiguration.getDEFAULT_US_PORT());
				break;
			case UK:
				buildCenter(loc, PortConfiguration.getDEFAULT_UK_PORT());
				// Save configuration in an object, in a real server we will save this to a .env file or other
				PortConfiguration.addConfig(loc, PortConfiguration.getDEFAULT_UK_PORT());
				break;
			}
	
		}
		
		String startingMessage = "The ReplicaS2 Server: " +
				" is launch";

		System.out.println(startingMessage);

		configStoring.writeLog(startingMessage, "CentralRepo.txt");
		
	

	}
	
	private void buildCenter(Location loca, int port) {
		
		// Create and pass a storing engine to the HRAction
		IStore storingEngine = new Logger(loca.toString(), StorageConfig.MAIN_TREE_FOLDER + loca.toString() + "/");
		
		
		int udpPortCounter = port + 1;
		int udpPortTransfert = port -1;
		try {

			HRActions instanceHRAction = new HRActions(storingEngine);
			CenterMap.addHRAction(loca, instanceHRAction);
			ServerUDP udpObj = new RecordCounterUDP(instanceHRAction, udpPortCounter);
			ServerUDP udpObjTransfer = new TransfertServerUDP(instanceHRAction, udpPortTransfert);
			
			InternalServers.addServer(udpObj);
			InternalServers.addServer(udpObjTransfer);
			
			Thread UDPCounterThread = new Thread(udpObj);
			Thread UDPTransfertThread  = new Thread(udpObjTransfer);
			
			// Starting the UDP process
			String udpStartingMessage = "The UDP Server for: " + loca.toString() + 
					" is start on port " + udpPortCounter;
			String udpTransfertMessage = "The UDP Server for transfert record on: " + loca.toString() + 
					" is started on port " + udpPortTransfert;
			
			UDPCounterThread.start();
			UDPTransfertThread.start();
			
			System.out.println(udpStartingMessage);
			System.out.println(udpTransfertMessage);
			
			//Log starting
			configStoring.writeLog(udpStartingMessage, "CentralRepo.txt");
			configStoring.writeLog(udpTransfertMessage, "CentralRepo.txt");			
			
			// Create the ORB Object in the CORBA System
			PortConfiguration.addConfigUDP(loca, udpPortCounter);
			PortConfiguration.addConfigUDPTransfert(loca, udpPortTransfert);
			
			

		}catch(Exception ee) {
			System.out.println(" \n\n ****WARNING ****** Problem while starting udp servers of " + loca.toString()
					+ " " + ee.getMessage());
		}
		
		
		
	}
}
