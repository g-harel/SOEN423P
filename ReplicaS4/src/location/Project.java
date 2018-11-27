package location;

import java.io.Serializable;

public class Project implements Serializable {
	public String id;
	public String client;
	public String name;

	public Project() {}

	public Project(String raw) {
		String data[] = raw.split(";", 3);
		if (data.length > 0) {
			this.id = data[0].trim();
		}
		if (data.length > 1) {
			this.client = data[1].trim();
		}
		if (data.length > 2) {
			this.name = data[2].trim();
		}
	}

	public String toString() {
		return String.format("%s;%s;%s", id, client, name);
	}
}
