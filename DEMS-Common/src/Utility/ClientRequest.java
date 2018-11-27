/*

Author: Andres Vazquez
Course: SOEN 423

*/

package Utility;

import java.io.Serializable;
import java.util.HashMap;


/**
 * Front-end will send:
 * 		method (e.g.: createMRecord)
 * 		location (e.g.: CA)
 * 		data (method parameters)
 * 			e.g.:
 * 				data = {
 * 					managerID: CA1234,
 * 					firstName: John,
 * 					lastName: Smith,
 * 					employeeID: 12345,
 * 					mailID: john@smith.com,
 * 					project: Project(...),
 * 					location: CA
 * 				}
 *
 */
public class ClientRequest implements Serializable {
	private String method;
	private String location;
	private HashMap<String, Object> data = new HashMap<>();
	
	
	public ClientRequest(String method, String location) {
		this.method = method;
		this.location = location;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public HashMap<String, Object> getData() {
		return data;
	}
	
	public void addRequestDataEntry(String fieldName, Object value) {
		data.put(fieldName, value);
	}

	@Override
	public String toString() {
		return "ClientRequest [method=" + method + ", location=" + location + ", data=" + data + "]";
	}
}
