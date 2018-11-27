package FrontEnd;

import Models.RegisteredReplica;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author cmcarthur
 */
public class ConsensusTrackerTest {

    ConsensusTracker instance;

    public ConsensusTrackerTest() {
        instance = new ConsensusTracker(3, 5);
    }

    /**
     * Test of addRequestConsensus method, of class ConsensusTracker.
     */
    @Test
    public void testAddRequestConsensus() {
        System.out.println("addRequestConsensus");
        int sequenceID = 5;
        String answer = "ABC";

        for (RegisteredReplica replica : RegisteredReplica.values()) {
            if (replica == RegisteredReplica.EVERYONE) {
                continue;
            }

            instance.addRequestConsensus(replica, sequenceID, answer);
        }

        assertEquals("Agree Answer should be constant", answer, instance.getAnswer());
    }

    @Test
    public void testFailureDectection() {
        System.out.println("addRequestConsensus");
        int sequenceID = 5;
        String answer = "ABC";

        for (RegisteredReplica replica : RegisteredReplica.values()) {
            if (replica == RegisteredReplica.EVERYONE) {
                continue;
            }

            if (replica == RegisteredReplica.ReplicaS1) {
                instance.addRequestConsensus(replica, sequenceID, "BAD");
            } else {
                instance.addRequestConsensus(replica, sequenceID, answer);
            }
        }

        assertEquals("Agree Answer should be constant", answer, instance.getAnswer());
        assertEquals("Should report S1 as in error", RegisteredReplica.ReplicaS1, instance.getFailures().getFirst());
    }
}
