package App;

import java.net.InetAddress;

public class ReplicaInfo {
	private InetAddress replicaAddress;
    private int replicaPort;
    
	public ReplicaInfo(InetAddress replicaAddress, int replicaPort) {
		this.replicaAddress = replicaAddress;
		this.replicaPort = replicaPort;
	}
	
	public InetAddress getReplicaAddress() {
		return replicaAddress;
	}
	
	public int getReplicaPort() {
		return replicaPort;
	}
}