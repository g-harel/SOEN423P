package shared.UDP;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import Config.PortConfiguration;
import Config.StorageConfig;
import model.Location;
import shared.IHRActions;
import storage.IStore;
import storage.Logger;

public class TransfertServerUDP extends ServerUDP implements Runnable {

	public TransfertServerUDP(IHRActions serverInstance, int portUDP) {
		super(serverInstance, portUDP);
		
	}

	@Override
	public void run() {
		DatagramSocket sock = null;
		try {
			byte[] buffer = new byte[1000];
			sock = new DatagramSocket(super.getAssignedPort());
	        while (listen) {
	        	DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
	        	sock.receive(packet);
	        	InetAddress add = packet.getAddress();
	        	String record = new String(packet.getData(), StandardCharsets.UTF_8);
	        	String status = localInstance.receiveNewRecord(record);
	        	buffer = status.getBytes();
	        	DatagramPacket reply = new DatagramPacket(buffer, buffer.length,
	        			add, packet.getPort());
	        	sock.send(reply);
	        }
			
		}catch(Exception ee) {
			ee.printStackTrace();
			super.serverStore.writeLog("UDP Server: " + ee.getMessage(), 
					StorageConfig.CENTRAL_REPO_LOCATION);
		}
		
	}

	
	

}
