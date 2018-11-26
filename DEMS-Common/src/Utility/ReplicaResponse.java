/*

Author: Andres Vazquez
Course: SOEN 423

*/

package Utility;

import java.io.Serializable;

import Models.RegisteredReplica;

/**
 * Replicas will send:
 * 		replicaID (The RegisteredReplica ID)
 * 		success (true/false)
 * 		response (Optional): Message to be logged and shown to the user.
 *
 */
public class ReplicaResponse implements Serializable {
	private RegisteredReplica replicaID;
	private boolean success;
	private String response;


	public ReplicaResponse(RegisteredReplica replicaID, boolean success) {
		this.replicaID = replicaID;
		this.success = success;
	}

	public ReplicaResponse(RegisteredReplica replicaID, boolean success, String response) {
		this.replicaID = replicaID;
		this.success = success;
		this.response = response;
	}

	public ReplicaResponse(boolean success, String response) {
		this.success = success;
		this.response = response;
	}

	public RegisteredReplica getReplicaID() {
		return replicaID;
	}

	public void setReplicaID(RegisteredReplica replicaID) {
		this.replicaID = replicaID;
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
		return "ReplicaResponse [replicaID=" + replicaID + ", success=" + success + ", response=" + response + "]";
	}
}
