package location;

import java.util.ArrayList;

class LocationEntry {
	public String locationCode;
	public int port;
	public int index;

	public LocationEntry(String locationCode, int startPort, int index) {
		this.locationCode = locationCode;
		this.port = startPort + index;
		this.index = index;
	}
}

public class AddressBook {
	private static int basePort = 3331;

	private static LocationEntry[] all = new LocationEntry[] {
		new LocationEntry("CA", AddressBook.basePort, 0),
		new LocationEntry("US", AddressBook.basePort, 1),
		new LocationEntry("UK", AddressBook.basePort, 2),
	};

	public static String locationPattern() {
		if (AddressBook.all.length == 0) {
			return "";
		}
		String res = "(" + AddressBook.all[0].locationCode;
		for (int i = 1; i < AddressBook.all.length; i++) {
			res += "|" + AddressBook.all[i].locationCode;
		}
		return res + ")";
	}

	///

	private ArrayList<LocationEntry> peers;
	private LocationEntry self;

	public AddressBook(String selfLocationCode) {
		this.peers = new ArrayList<LocationEntry>();
		for (LocationEntry location : AddressBook.all) {
			if (!selfLocationCode.equals(location.locationCode)) {
				this.peers.add(location);
			} else {
				this.self = location;
			}
		}
	}

	public String selfName() {
		return this.self.locationCode;
	}

	public int selfPort() {
		return this.self.port;
	}

	public String selfAddr() {
		return "http://localhost:" + this.selfPort() + "/";
	}

	public int selfIndex() {
		return this.self.index;
	}

	public int total() {
		return AddressBook.all.length;
	}

	public String[] names() {
		String[] res = new String[this.peers.size()];
		int i = 0;
		for (LocationEntry location : this.peers) {
			res[i] = location.locationCode;
			i++;
		}
		return res;
	}

	public int port(String locationCode) {
		for (LocationEntry location : AddressBook.all) {
			if (locationCode.equals(location.locationCode)) {
				return location.port;
			}
		}
		return 0;
	}

	public int[] ports() {
		int[] res = new int[this.peers.size()];
		int i = 0;
		for (LocationEntry location : this.peers) {
			res[i] = location.port;
			i++;
		}
		return res;
	}
}
