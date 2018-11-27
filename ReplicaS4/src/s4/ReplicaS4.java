package s4;

import Models.Location;
import Models.RegisteredReplica;
import Utility.AbstractReplica;
import location.AddressBook;

public class ReplicaS4 extends AbstractReplica {
	public ReplicaS4() {
		super(RegisteredReplica.ReplicaS4);
	}

	public static void main(String[] args) {
		for (Location loc: Location.values()) {
			CenterServer s = new CenterServer(loc);
			Thread t = new Thread(s);
			t.start();

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
