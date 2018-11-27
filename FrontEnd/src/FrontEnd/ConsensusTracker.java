package FrontEnd;

import Models.RegisteredReplica;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;

public class ConsensusTracker {
   

    final private HashMap<RegisteredReplica, String> answers;
    final private int sequenceNumber;
    final private Semaphore complete;

    final private LinkedList<RegisteredReplica> inError = new LinkedList<>();
    private String currentAswer;

    public ConsensusTracker(int consensusCountNeeded, int sequenceID) {
        this.answers = new HashMap<>();
        complete = new Semaphore(consensusCountNeeded);
        sequenceNumber = sequenceID;
    }

    public void addRequestConsensus(RegisteredReplica replica, int sequenceID, String answer) {
        if (answers.isEmpty()) {
            currentAswer = answer; // save the current answer
        }

        if (sequenceNumber == sequenceID && !answers.containsKey(replica)) {
            answers.put(replica, answer);
            complete.release();
        } else {
            inError.add(replica); // bad seq or duplicate =?
        }

        int index = 0;
        int counter[] = new int[answers.size()];
        for (String potential : answers.values()) {
            for (String suspect : answers.values()) {
                if (potential.equals(suspect)) {
                    counter[index] += 1;
                }
            }
            index++;
        }
        
        int max = 0;
        for( int i = 0; i < answers.size(); i++){
            if( max < counter[i]){
                max = counter[i];
            }
        }
        
        int ticker = 0;
        for (String potential : answers.values()) {
            if( max == counter[ticker]){
                
                currentAswer = potential;
                break;
            }
            
            ticker++;
            
        }

    }

    /**
     *
     * @throws java.lang.InterruptedException
     */
    public void Wait() throws InterruptedException {
        complete.acquire();
    }

    public boolean contains(RegisteredReplica instance) {
        return answers.containsKey(instance);
    }
}
