package Manager;

import java.util.Stack;

import model.RegisteredReplica;

public class Manager {
	
	//The replica linked to the manager
	private RegisteredReplica associatedReplica;
	private static final int nonByzantineFailureTolerance = 3;
	private static final int crashFailureTolerance = 1;
	private Stack<Integer> nonByzantineFailStack;
	private Stack<Integer> crashFailStack;

	public Manager(RegisteredReplica associatedReplica) {
		this.associatedReplica = associatedReplica;
		
		// Create a place to store non-byz. failure
		nonByzantineFailStack.setSize(nonByzantineFailureTolerance);
		this.nonByzantineFailStack = new Stack<Integer>();
		
		// Create a place to store crash failure
		this.crashFailStack.setSize(crashFailureTolerance);
		this.crashFailStack = new Stack<Integer>();
	}
	
	public String getAssociatedReplicaName() {
		return this.associatedReplica.toString();
	}
	/**
	 * Should return message with FAULY_RESP_NOTIFICATION
	 * @param seqId
	 */
	public void registerNonByzFailure(int seqId) {
		//TODO: Add failure to stack, if stack full launch reset and empty stack
	}
	/**
	 * Should return message with ACK_NO_RESP_NOTIFICATION
	 * @param seqId
	 */
	public void registerCrashFailure(int seqId) {
		//TODO: Register failure, if stack full launch launch reset and empty stack
	}
	/**
	 * Will Call the right replica to restore from a log file
	 */
	public void restoreReplicaBack() {
		//TODO: Ask sequencer to restore replica from log file
	}
	
	/**
	 * Will simply restart replica attempts to repair it...
	 */
	public void restartReplica() {
		//TODO: Send message to the target replica to restart
	}

	
	

}
