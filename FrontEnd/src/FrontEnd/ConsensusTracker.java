package FrontEnd;

import Models.RegisteredReplica;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;

public class ConsensusTracker {

    final private HashMap<RegisteredReplica, String> answers = new HashMap<>();
    final private int sequenceNumber;
    final private Semaphore complete;

    final private LinkedList<RegisteredReplica> inError = new LinkedList<>();
    private RegisteredReplica currentAswer;

    public ConsensusTracker(int consensusCountNeeded, int sequenceID) {
        complete = new Semaphore(consensusCountNeeded);
        sequenceNumber = sequenceID;
    }

    public void addRequestConsensus(RegisteredReplica replica, int sequenceID, String answer) {
        if (answers.isEmpty()) {
            currentAswer = replica; // save the current answer
        }

        if (sequenceNumber == sequenceID && !answers.containsKey(replica)) {
            answers.put(replica, answer);
            complete.release();
        } else {
            inError.add(replica); // bad seq or duplicate =?
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
