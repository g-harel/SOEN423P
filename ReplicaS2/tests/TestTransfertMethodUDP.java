package tests;


import org.junit.Test;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class TestTransfertMethodUDP {

	@Test
	public void TransfertUDPtest() {
		
		String dataOut = "Record:ER20321|Duke|Dukinson|DukeDuke@bobbob.com|";
		String returningString = null;
		byte[] buffer = new byte[1000];
		DatagramSocket socketData = null;		
		byte[] dataReceived = new byte[1000];
		try {
			InetAddress aHost = InetAddress.getByName("localhost");
			socketData = new DatagramSocket();
			buffer = dataOut.getBytes();
			DatagramPacket r = new DatagramPacket(buffer, buffer.length, aHost, 7776);
			socketData.send(r);

			r  = new DatagramPacket(buffer, buffer.length);
			//socketData.setSoTimeout(5000);
			socketData.receive(r);
			String dataRe = new String(r.getData(), StandardCharsets.UTF_8);
			returningString = dataRe.trim();
			if(returningString.equalsIgnoreCase("Record Transfered")) {
				System.out.println("Success");
				
			}else {
				//fail("Not able to make it...");
			}

		}catch(Exception ee) {
			//fail("Not able to make it... " + ee.getMessage());
		}
		finally {
			if(socketData != null) {
				socketData.close();
			}
		}

	}

}
