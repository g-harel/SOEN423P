package location;

public class UDPException extends Exception {
	public UDPException(String msg) {
		super("[UDP] " + msg);
	}
}
