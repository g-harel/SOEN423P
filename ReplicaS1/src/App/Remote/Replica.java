/*

Name: Andres Vazquez (#40007182)
Course: SOEN 423

*/

package App.Remote;

import Models.AddressBook;
import Models.Location;
import Models.RegisteredReplica;
import Utility.AbstractReplica;

public class Replica extends AbstractReplica {
	
	public Replica() {
		super(RegisteredReplica.ReplicaS1);
	}
	
	public static void main(String[] args) {
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
