/*

Author: Andres Vazquez
Course: SOEN 423

*/

package Utility;

import java.io.Serializable;

/**
 * Replicas will send:
 * 		sequenceID (same ID from the ClientRequest. Set by the Sequencer)
 * 		success (true/false)
 * 		response (Optional): Message to be logged and shown to the user.
 *
 */
public class ReplicaResponse implements Serializable {
	private boolean success;
	private String response;
	
	
	public ReplicaResponse(boolean success) {
		this.success = success;
	}
	
	public ReplicaResponse(boolean success, String response) {
		this.success = success;
		this.response = response;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	@Override
	public String toString() {
		return "ReplicaResponse [success=" + success + ", response=" + response + "]";
	}
}
