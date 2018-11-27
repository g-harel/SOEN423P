package s4;

import Models.Location;
import Models.RegisteredReplica;
import Utility.AbstractReplica;

public class ReplicaS4 extends AbstractReplica {
	public ReplicaS4() {
		super(RegisteredReplica.ReplicaS4);
	}

	public static void main(String[] args) {
		for (Location loc: Location.values()) {
			CenterServer s = new CenterServer(loc.getPrefix());
			Thread t = new Thread(s);
			t.start();

			centerServers.put(loc.getPrefix(), s);
		}
    }
}
