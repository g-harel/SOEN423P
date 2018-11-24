package App;

import java.util.HashMap;

public class ConsensusTracker {
	private HashMap<Integer, RequestConsensus> requests = new HashMap<>();
	private int consensusCountNeeded;
	
	public ConsensusTracker(int consensusCountNeeded) {
		this.consensusCountNeeded = consensusCountNeeded;
	}

	public int getConsensusCountNeeded() {
		return consensusCountNeeded;
	}

	public void setConsensusCountNeeded(int consensusCountNeeded) {
		this.consensusCountNeeded = consensusCountNeeded;
	}
	
	public void decrementConsensusCountNeeded() {
		consensusCountNeeded--;
	}
	
	public RequestConsensus getRequestConsensus(int sequenceID) {
		return requests.get(sequenceID);
	}
	
	public void addRequestConsensus(int sequenceID, RequestConsensus requestConsensus) {
		requestConsensus.setConsensusCountNeeded(consensusCountNeeded);
		requests.put(sequenceID, requestConsensus);
	}
}
