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

import Models.AddressBook;
import Models.RegisteredReplica;
import UDP.Message;
import UDP.OperationCode;
import UDP.RequestListener;
import Utility.ReplicaResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.SocketException;

/**
 *
 * @author cmcarthur
 */
public class Listener implements RequestListener.Processor {

    final private RequestListener m_Listener;
    private ConsensusTracker m_ConsensusTracker;
    private Thread m_ListenerThread;

    public Listener() throws SocketException {
        m_Listener = new RequestListener(this, AddressBook.FRONTEND);
    }

    public void launch() {
        m_ListenerThread = new Thread(m_Listener);
        m_ListenerThread.start();
        m_Listener.Wait(); // Make sure it's running before getting any farther
        System.out.println("Front-End started to listen for UDP requests on port: " + AddressBook.FRONTEND.getPort());
    }

    public void shutdown() throws InterruptedException {
        m_Listener.Stop();
        m_ListenerThread.join();
    }

    /**
     * Operation Handled: OPERATION_RETVAL(1005)
     */
    @Override
    public String handleRequestMessage(Message msg) throws Exception {
        if (msg.getOpCode() == OperationCode.OPERATION_RETVAL && msg.getData().contains("ReplicaResponse")) {
            ObjectInputStream iStream = new ObjectInputStream(new ByteArrayInputStream(msg.getData().getBytes()));
            Object input = iStream.readObject();
            ReplicaResponse replicaResponse;
            iStream.close();

            if (input instanceof ReplicaResponse) {
                replicaResponse = (ReplicaResponse) input;
            } else {
                throw new IOException("Data received is not valid.");
            }

            int sequenceID = msg.getSeqNum();
            String answer = replicaResponse.getResponse();
            RegisteredReplica replicaID = replicaResponse.getReplicaID();

            if (m_ConsensusTracker != null) {
                m_ConsensusTracker.addRequestConsensus(replicaID, sequenceID, answer);
            }
        }
        return "";
    }

    /**
     *
     * @param tracker
     */
    public void setTracker(ConsensusTracker tracker) {
        m_ConsensusTracker = tracker;
    }
}
