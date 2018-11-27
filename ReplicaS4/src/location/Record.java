package location;

import java.io.Serializable;

public abstract class Record implements Serializable {
	public abstract String getType();

	public String recordID;
	public String firstName;
	public String lastName;
	public int employeeID;
	public String mailID;
}
