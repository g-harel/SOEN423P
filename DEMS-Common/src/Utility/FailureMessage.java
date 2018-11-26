package Utility;

import java.net.InetAddress;

public class FailureMessage {
	private InetAddress replicaAddress;
    private int replicaPort;
    
	public FailureMessage(InetAddress replicaAddress, int replicaPort) {
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
