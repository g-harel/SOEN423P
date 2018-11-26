package backEnd;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import Config.PortConfiguration;
import Config.StorageConfig;
import HrCenterApp.DEMS;
import HrCenterApp.DEMSHelper;
import model.Location;
import shared.HRActions;
import shared.UDP.RecordCounterUDP;
import shared.UDP.ServerUDP;
import shared.UDP.TransfertServerUDP;
import storage.IStore;
import storage.Logger;

public class ServerConfigurator {
	

	
	private IStore configStoring;
	private ORB orb;
	public ServerConfigurator() {
		 configStoring	= 
				new Logger("ServerConfigurator", StorageConfig.CENTRAL_REPO_LOCATION);
	}
	
	 void configureCenter(String[] args) {
		
		orb = ORB.init(args, null);

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
		
		String startingMessage = "The CORBA Server: " +
				" is on port:  " + PortConfiguration.getDEFAULT_CORBA_PORT();

		System.out.println(startingMessage);

		configStoring.writeLog(startingMessage, "CentralRepo.txt");
		orb.run();
		System.out.println("CORBA Server is going down..." );
		

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
			
			//Creating a CORBA object
			POA rootPoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
			rootPoa.the_POAManager().activate();
			
			instanceHRAction.setORB(orb);
			org.omg.CORBA.Object ref = rootPoa.servant_to_reference(instanceHRAction);
			DEMS href = DEMSHelper.narrow(ref);
			
			// Name the service same as location
			org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
			
			NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
			String name = loca.toString();
			NameComponent path[] = ncRef.to_name(name);
			ncRef.rebind(path, href);
			
			System.out.println("CORBA Setup finished for Server " + loca.toString());		
			

		}catch(Exception ee) {
			System.out.println(" \n\n ****WARNING ****** Problem while starting udp servers of " + loca.toString()
					+ " " + ee.getMessage());
		}
		
		
		
	}
}
