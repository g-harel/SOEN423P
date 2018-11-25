package Manager;

public interface IManager {
	
	/**
	 * Each Replica have a Replica Manager manager, identified by replica Name
	 * @return replica name
	 */
	public String getReplicaName();
	
	/**
	 * Register a non byzantine failure, can trigger a restart (if stack is full)
	 * @param seqId
	 */
	public void registerNonByzFailure(int seqId);
	/**
	 * Register a crash failure, trigger a restart
	 * @param seqId
	 */
	public void registerCrashFailure(int seqId) ;
	/**
	 * Ask the sequencer to replay the log to its replica for CATCH UP
	 */
	public void restoreReplicaBack();
	/**
	 * Ask Replica to restart
	 */
	public void restartReplica();
	
	

}
