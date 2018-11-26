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
import UDP.Socket;
import Utility.ReplicaResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.SocketException;
import java.util.List;

/**
 *
 * @author cmcarthur
 */
public class Listener implements RequestListener.Processor {

    final private ConsensusTracker m_ConsensusTracker;
    final private RequestListener m_Listener;
    private Thread m_ListenerThread;
    final private Socket m_Socket;


    public Listener() throws SocketException {
        m_Listener = new RequestListener(this, AddressBook.FRONTEND);
        m_ConsensusTracker = new ConsensusTracker(3);
        m_Socket = new Socket();
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

            RequestConsensus requestConsensus = m_ConsensusTracker.getRequestConsensus(sequenceID);

            if (requestConsensus == null) {
                requestConsensus = new RequestConsensus(answer, replicaID);
                m_ConsensusTracker.addRequestConsensus(sequenceID, requestConsensus);
            } else {
                requestConsensus.addAnswer(answer, replicaID);

                if (requestConsensus.shouldSendConsensus()) {
                    List<RegisteredReplica> softwareFailures = requestConsensus.getSoftwareFailures();

                    if (softwareFailures.size() > 0) {
                        for (RegisteredReplica replica : softwareFailures) {
                            
                            Message messageToSend = new Message(OperationCode.ACK_FAULY_RESP_NOTIFICATION, 0, replicaID.toString(), AddressBook.MANAGER);
                            m_Socket.send(messageToSend, 5, 1000);
                        }
                    }

                    return requestConsensus.getConsensusAnswer();
                }
            }
        } else {
            throw new IOException("The response received is not valid.\n"
                    + "Responses need to hava the OperationCode `" + OperationCode.OPERATION_RETVAL + "` and "
                    + "a serialized ReplicaResponse object as data.");
        }
        return null;
    }
}
