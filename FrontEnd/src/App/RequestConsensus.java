package App;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Models.RegisteredReplica;

public class RequestConsensus {
	private class ConsensusEntry {
		String answer;
		int count;
		
		public ConsensusEntry(String answer, int count) {
			this.answer = answer;
			this.count = count;
		}
	}
	
	
	private HashMap<String, List<RegisteredReplica>> answersMap = new HashMap<>();
	private int consensusCountNeeded;
	private String consensusAnswer;
	
	public RequestConsensus(String answer, RegisteredReplica replicaID) {
		this.answersMap.put(answer, new ArrayList<>());
		
		addAnswer(answer, replicaID);
	}
	
	public void addAnswer(String answer, RegisteredReplica replicaID) {
		if(!answersMap.get(answer).contains(replicaID)) {			
			answersMap.get(answer).add(replicaID);
		}
	}
	
	public boolean shouldSendConsensus() {
		if(consensusAnswer != null) {
			return false;
		}
		
		ConsensusEntry maximumAnswer = null;
		
		for (Map.Entry<String, List<RegisteredReplica>> entry : answersMap.entrySet()) {
			if(consensusAnswer == null || maximumAnswer.count < entry.getValue().size()) {
				maximumAnswer = new ConsensusEntry(entry.getKey(), entry.getValue().size());
			}
		}
		
		if(maximumAnswer.count >= consensusCountNeeded) {
			consensusAnswer = maximumAnswer.answer;
		}
		
		return consensusAnswer != null;
	}
	
	public List<RegisteredReplica> getSoftwareFailures() {
		if(consensusAnswer == null) {
			return null;
		}
		
		List<RegisteredReplica> softwareFailures = new ArrayList<>();
		
		for (Map.Entry<String, List<RegisteredReplica>> entry : answersMap.entrySet()) {
			if(entry.getKey() != consensusAnswer) {
				softwareFailures.addAll(entry.getValue());
			}
		}
		
		return softwareFailures;
	}
	
	public String getConsensusAnswer() {
		return consensusAnswer;
	}
	
	public int getConsensusCountNeeded() {
		return consensusCountNeeded;
	}
	
	protected void setConsensusCountNeeded(int consensusCountNeeded) {
		this.consensusCountNeeded = consensusCountNeeded;
	}
}
