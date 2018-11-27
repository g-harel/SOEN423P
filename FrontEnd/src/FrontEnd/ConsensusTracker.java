/*
 * The MIT License
 *
 * Copyright 2018
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
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
        for (int i = 0; i < answers.size(); i++) {
            if (max < counter[i]) {
                max = counter[i];
            }
        }

        int ticker = 0;
        for (String potential : answers.values()) {
            if (max == counter[ticker]) {
                if (!currentAswer.equals(potential)) {

                    for (RegisteredReplica suspect : RegisteredReplica.values()) {

                        if (answers.containsKey(suspect) && answers.get(suspect).equals(currentAswer)) {
                            inError.add(suspect);
                        }
                    }
                    currentAswer = potential;
                }

                break;
            }

            ticker++;
        }

        if (answers.size() > 2
                && !answer.equals(currentAswer)) {
            inError.add(replica);
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

    public String getAnswer() {
        return currentAswer;
    }

    public LinkedList<RegisteredReplica> getFailures() {
        return inError;
    }

    public LinkedList<RegisteredReplica> getAnswerees() {
        LinkedList<RegisteredReplica> respondents = new LinkedList<>();

        for (RegisteredReplica suspect : RegisteredReplica.values()) {
            if (suspect == RegisteredReplica.EVERYONE) {
                continue;
            }

            if (answers.containsKey(suspect)) {
                respondents.add(suspect);
            }
        }

        return respondents;
    }

    public LinkedList<RegisteredReplica> getMissingAnswers() {
        LinkedList<RegisteredReplica> missing = new LinkedList<>();

        for (RegisteredReplica suspect : RegisteredReplica.values()) {
            if (suspect == RegisteredReplica.EVERYONE) {
                continue;
            }

            if (!answers.containsKey(suspect)) {
                missing.add(suspect);
            }
        }

        return missing;
    }
}
