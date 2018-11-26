package backEnd;

import Config.PortConfiguration;
import Config.StorageConfig;
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
	
	 void configureCenter(String[] args) {

		for(Location loc: Location.values()) {
			//TODO: Refactor this switch
			switch(loc){
			case CA:
				buildCenter(loc, PortConfiguration.getDEFAULT_CA_PORT(), args);
				// Save configuration in an object, in a real server we will save this to a .env file or other
				PortConfiguration.addConfig(loc, PortConfiguration.getDEFAULT_CORBA_PORT());
				break;
			case US:
				buildCenter(loc, PortConfiguration.getDEFAULT_US_PORT(), args);
				// Save configuration in an object, in a real server we will save this to a .env file or other
				PortConfiguration.addConfig(loc, PortConfiguration.getDEFAULT_US_PORT());
				break;
			case UK:
				buildCenter(loc, PortConfiguration.getDEFAULT_UK_PORT(), args);
				// Save configuration in an object, in a real server we will save this to a .env file or other
				PortConfiguration.addConfig(loc, PortConfiguration.getDEFAULT_UK_PORT());
				break;
			}
	
		}
		
		String startingMessage = "The ReplicaS3 Server: " +
				" is launch";

		System.out.println(startingMessage);

		configStoring.writeLog(startingMessage, "CentralRepo.txt");
		
	

	}
	
	private void buildCenter(Location loca, int port, String[] args) {
		
		// Create and pass a storing engine to the HRAction
		IStore storingEngine = new Logger(loca.toString(), StorageConfig.MAIN_TREE_FOLDER + loca.toString() + "/");
		
		int udpPortCounter = port + 1;
		int udpPortTransfert = port -1;
		try {

			HRActions instanceHRAction = new HRActions(storingEngine);
			ServerUDP udpObj = new RecordCounterUDP(instanceHRAction, udpPortCounter);
			ServerUDP udpObjTransfer = new TransfertServerUDP(instanceHRAction, udpPortTransfert);
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
			

			
			System.out.println("CORBA Setup finished for Server " + loca.toString());		
			

		}catch(Exception ee) {
			System.out.println(" \n\n ****WARNING ****** Problem while starting udp servers of " + loca.toString()
					+ " " + ee.getMessage());
		}
		
		
		
	}
}
