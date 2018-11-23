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
 * 		response (Optional): Needed for getRecordCounts()
 *
 */
public class ReplicaResponse implements Serializable {
	private int sequenceID;
	private boolean success;
	private String response;
	
	
	public ReplicaResponse(int sequenceID, boolean success) {
		super();
		this.sequenceID = sequenceID;
		this.success = success;
	}
	
	public ReplicaResponse(int sequenceID, boolean success, String response) {
		super();
		this.sequenceID = sequenceID;
		this.success = success;
		this.response = response;
	}

	public int getSequenceID() {
		return sequenceID;
	}

	public void setSequenceID(int sequenceID) {
		this.sequenceID = sequenceID;
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
		return "ReplicaResponse [sequenceID=" + sequenceID + ", success=" + success + ", response=" + response + "]";
	}
}
