package Manager;

public interface IManager {
	
	/**
	 * Each Replica have a Replica Manager manager, identified by replica Name
	 * @return replica name
	 */
	
	public String getAssociatedReplicaName();
	
	/**
	 * Register a non byzantine failure, can trigger a restart (if stack is full)
	 * @param seqId
	 */
	public String registerNonByzFailure(int seqId);
	/**
	 * Register a crash failure, trigger a restart
	 * @param seqId
	 */
	public String registerCrashFailure(int seqId) ;
	/**
	 * Ask the sequencer to replay the log to its replica for CATCH UP
	 */
	public String restoreReplicaBack();
	/**
	 * Ask Replica to restart
	 */
	public String restartReplica();
	
	

}
