package App;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestConsensus {
	private class ConsensusEntry {
		String answer;
		int count;
		
		public ConsensusEntry(String answer, int count) {
			this.answer = answer;
			this.count = count;
		}
	}
	
	
	private HashMap<String, List<ReplicaInfo>> answersMap = new HashMap<>();
	private int consensusCountNeeded;
	private String consensusAnswer;
	
	public RequestConsensus(String answer, InetAddress replicaAddress, int replicaPort) {
		this.answersMap.put(answer, new ArrayList<>());
		
		addAnswer(answer, replicaAddress, replicaPort);
	}
	
	public void addAnswer(String answer, InetAddress replicaAddress, int replicaPort) {
		ReplicaInfo replicaInfo  = new ReplicaInfo(replicaAddress, replicaPort);
		
		if(!answersMap.get(answer).contains(replicaInfo)) {			
			answersMap.get(answer).add(replicaInfo);
		}
	}
	
	public boolean shouldSendConsensus() {
		if(consensusAnswer != null) {
			return false;
		}
		
		ConsensusEntry maximumAnswer = null;
		
		for (Map.Entry<String, List<ReplicaInfo>> entry : answersMap.entrySet()) {
			if(consensusAnswer == null || maximumAnswer.count < entry.getValue().size()) {
				maximumAnswer = new ConsensusEntry(entry.getKey(), entry.getValue().size());
			}
		}
		
		if(maximumAnswer.count >= consensusCountNeeded) {
			consensusAnswer = maximumAnswer.answer;
		}
		
		return consensusAnswer != null;
	}
	
	public List<ReplicaInfo> getSoftwareFailures() {
		if(consensusAnswer == null) {
			return null;
		}
		
		List<ReplicaInfo> softwareFailures = new ArrayList<>();
		
		for (Map.Entry<String, List<ReplicaInfo>> entry : answersMap.entrySet()) {
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
