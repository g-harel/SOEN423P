package Manager;

import java.util.Stack;

public class Manager {
	
	//The replica linked to the manager
	private String replicaName;
	private static final int nonByzantineFailureTolerance = 3;
	private static final int crashFailureTolerance = 1;
	private Stack<Integer> nonByzantineFailStack;
	private Stack<Integer> crashFailStack;

	public Manager(String assignedReplica) {
		this.replicaName = assignedReplica;
		initErrorStack();
		
	}
	
	private void initErrorStack() {

		nonByzantineFailStack.setSize(nonByzantineFailureTolerance);
		this.nonByzantineFailStack = new Stack<Integer>();
		
		this.crashFailStack.setSize(crashFailureTolerance);
		this.crashFailStack = new Stack<Integer>();
		
		
	}
	
	public String getReplicaName() {
		return replicaName;
	}
	
	public void registerNonByzFailure(int seqId) {
		//TODO: Add failure to stack, if stack full launch reset and empty stack
	}
	
	public void registerCrashFailure(int seqId) {
		//TODO: Register failure, if stack full launch launch reset and empty stack
	}
	
	public void restoreReplicaBack() {
		//TODO: Ask sequencer to restore replica from log file
	}
	
	public void restartReplica() {
		//TODO: Send message to the target replica to restart
	}

	
	

}
