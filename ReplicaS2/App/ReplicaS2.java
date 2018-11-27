package App;

import Models.AddressBook;
import Models.Location;
import Models.RegisteredReplica;
import Utility.AbstractReplica;
import backEnd.ServerConfigurator;

public class ReplicaS2 extends AbstractReplica {
	
	public ReplicaS2() {
		super(RegisteredReplica.ReplicaS2);
	}
	
	public static void main(String[] args) {
		ServerConfigurator config = new ServerConfigurator();
		config.configureCenter();
            for (Location loc: Location.values()) {
                CenterServer location_server = new CenterServer(loc);
                Thread location_server_thread = new Thread(location_server);
                location_server_thread.start();
                
                centerServers.put(loc.getPrefix(), location_server);
            }
    }
	
	// Starts the UDP RequestListener to receive the responses from the Replicas
    public void startUDPListener() {
    	ReplicaListerner frontEndListerner = new ReplicaListerner();
    	frontEndListerner.launch();

    	System.out.println("Replica (" + replicaID + ") started to listen for UDP requests on port: " + AddressBook.REPLICAS.getPort());
    }
}