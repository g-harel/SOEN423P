package shared.UDP;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import Config.StorageConfig;
import shared.IHRActions;
import storage.IStore;
import storage.Logger;
/**
 * This code is inspired by this tutorial: https://www.baeldung.com/udp-in-java
 * @author winterhart
 *
 */
public abstract class ServerUDP implements Runnable  {
	
	IHRActions localInstance;
	IStore serverStore;
	private static final String SERVER_ADDRESS = "locahost";
	public boolean listen = true;
	protected int assignedPort = 0;
	
	public ServerUDP(IHRActions serverInstance, int portUDP) {
		this.localInstance = serverInstance;
		this.serverStore = serverInstance.store;
		this.setAssignedPort(portUDP);
	}
	public int getAssignedPort() {
		return assignedPort;
	}
	public void setAssignedPort(int assignedPort) {
		this.assignedPort = assignedPort;
	}
}
