package location;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Base64;

public class RecordServer extends RecordStore implements Runnable {
	private AddressBook ab;
	private int recordIDCount = 0;
	private boolean running = true;
	private DatagramSocket serverSocket;
	private DatagramSocket clientSocket;
	private int requestTimeout = 100;

	public RecordServer(AddressBook ab) {
		super();
		this.ab = ab;

		int serverPort = this.ab.selfPort();
		try {
			this.serverSocket = new DatagramSocket(serverPort);
		} catch (SocketException e) {
			Logger.err("could not bind to server socket '%s': %s", serverPort, e);
		}
		Logger.log("bound to server port '%s'", serverPort);

		int clientPort = this.ab.total() + this.ab.selfPort();
		try {
			this.clientSocket = new DatagramSocket(clientPort);
			this.clientSocket.setSoTimeout(1000);
		} catch (SocketException e) {
			Logger.err("could not bind to client socket '%s': %s", clientPort, e);
		}
		Logger.log("bound to client port '%s'", clientPort);
	}

	public void add(Record record) {
		int ID = this.ab.selfIndex() + (this.ab.total() * this.recordIDCount++);
		record.recordID = record.getType() + String.format("%05d", ID);
		this.write(record);
	}

	public void kill() {
		this.running = false;
		this.serverSocket.close();
		this.clientSocket.close();
		Thread.currentThread().interrupt();
	}

	public void run() {
		byte[] buffer;

		while (this.running) {
			buffer = new byte[UDPMessage.size];
			DatagramPacket requestPacket = new DatagramPacket(buffer, buffer.length);
			try {
				this.serverSocket.receive(requestPacket);
			} catch (IOException e) {
				Logger.err("could not receive request: %s", e);
				continue;
			}
			UDPMessage request = new UDPMessage(new String(requestPacket.getData(), 0, requestPacket.getLength()));

			Logger.log("[UDP] <<< '%s'", request.type);

			UDPMessage response = null;
			switch(request.type) {
			case RecordServer.typeList:
				response = this.handleList();
				break;
			case RecordServer.typeTransfer:
				response = this.handleTransfer(request.body);
				break;
			case RecordServer.typeExists:
				response = this.handleExists(request.body);
				break;
			}

			if (response == null) {
				Logger.err("no configured handler for request type '%s'", request.type);
				continue;
			}

			response.type = "response";
			buffer = response.toBuffer();
			DatagramPacket responsePacket = new DatagramPacket(buffer, buffer.length, requestPacket.getAddress(), requestPacket.getPort());
			try {
				this.serverSocket.send(responsePacket);
			} catch (IOException e) {
				Logger.err("could not send response: %s", e);
				continue;
			}

			Logger.log("[UDP] >>> '%s'", response.type);
		}
	}

	public UDPMessage send(String locationCode, UDPMessage msg) throws UDPException {
		DatagramSocket socket;
		byte[] buffer;

		int port = this.ab.port(locationCode);
		InetAddress addr;

		try {
			addr = InetAddress.getByName("localhost");
		} catch (UnknownHostException e) {
			throw new UDPException(Logger.err("could not send request: %s", e));
		}

		buffer = msg.toBuffer();
		DatagramPacket requestPacket = new DatagramPacket(buffer, buffer.length, addr, port);
		try {
			this.clientSocket.send(requestPacket);
		} catch (IOException e) {
			throw new UDPException(Logger.err("could not send request: %s", e));
		}

		Logger.log("[UDP] >>> '%s' '%s'", locationCode, msg.type);

		buffer = new byte[UDPMessage.size];
		DatagramPacket responsePacket = new DatagramPacket(buffer, buffer.length);
		try {
			this.clientSocket.receive(responsePacket);
		} catch (SocketTimeoutException e) {
			Logger.log("[UDP] --- '%s' '%s'", locationCode, msg.type);
			throw new UDPException(Logger.err("request to '%s' timed out", locationCode));
		} catch (IOException e) {
			throw new UDPException(Logger.err("could not receive request: %s", e));
		}

		UDPMessage response = new UDPMessage(new String(responsePacket.getData(), 0, responsePacket.getLength()));

		Logger.log("[UDP] <<< '%s' '%s'", locationCode, response.type);

		return response;
	}

	///

	private static final String typeList = "list";

	private UDPMessage handleList() {
		UDPMessage res = new UDPMessage();
		res.body = ab.selfName() + " " + this.count();
		return res;
	}

	private String sendList(String locationCode) throws Exception {
		UDPMessage response = this.send(locationCode, new UDPMessage(RecordServer.typeList));
		if (response == null) {
			throw new Exception("unknown status");
		}
		return response.body;
	}

	///

	private static final String typeExists = "exists";

	private UDPMessage handleExists(String recordID) {
		UDPMessage res = new UDPMessage();
		res.body = String.format("%b", this.read(recordID) != null);
		return res;
	}

	private boolean sendExists(String locationCode, String recordID) throws UDPException {
		UDPMessage request = new UDPMessage();
		request.type = RecordServer.typeExists;
		request.body = recordID;
		UDPMessage response = this.send(locationCode, request);
		return response.body.equals("true");
	}

	///

	private static final String typeTransfer = "transfer";

	private UDPMessage handleTransfer(String raw) {
		UDPMessage res = new UDPMessage();
		res.body = "true";
		try {
			byte b[] = Base64.getDecoder().decode(raw);
			ByteArrayInputStream bi = new ByteArrayInputStream(b);
			ObjectInputStream si = new ObjectInputStream(bi);
			Record record = (Record) si.readObject();
			this.write(record);
		} catch (Exception e) {
			Logger.err("could not deserialize record: %s\n", e);
			e.printStackTrace();
			res.body = "false";
		}
		return res;
	}

	private boolean sendTransfer(String locationCode, Record record) throws UDPException {
		UDPMessage request = new UDPMessage();
		request.type = RecordServer.typeTransfer;
		try {
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			ObjectOutputStream so = new ObjectOutputStream(bo);
			so.writeObject(record);
			so.flush();
			request.body = Base64.getEncoder().encodeToString(bo.toByteArray());
		} catch (Exception e) {
			throw new UDPException(Logger.err("could not serialize record: %s", e));
		}
		UDPMessage response = this.send(locationCode, request);
		return response.body.equals("true");
	}

	///

	public String sendListAll() {
		String res = this.handleList().body;
		for (String val : this.ab.names()) {
			try {
				res += ", " + sendList(val);
			} catch (Exception e) {}
		}
		return res;
	}

	public boolean transferRecord(String locationCode, String recordID) {
		Record record = this.read(recordID);

		if (record == null) {
			Logger.err("no local record with ID '%s'", recordID);
			return false;
		}
		try {
			if (this.sendExists(locationCode, recordID)) {
				Logger.err("destination already contains record with ID '%s'", recordID);
				return false;
			}
		} catch (Exception e) {
			Logger.err("could not check destination for record with ID '%s'", recordID);
			return false;
		}

		try {
			boolean success = this.sendTransfer(locationCode, record);
			if (!success) return false;
		} catch (UDPException e) {
			return false;
		}

		this.delete(recordID);
		return true;
	}
}
