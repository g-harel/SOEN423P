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
package UDP;

import Models.AddressBook;
import Models.RegisteredReplica;
import java.util.concurrent.ThreadLocalRandom;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.List;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author cmcarthur
 */
public class SocketTest implements RequestListener.Processor {

    final private Socket m_Socket;
    static private RequestListener m_Listener;
    private Thread m_ListenerThread;
    private List<Message> m_ListOfMessages;

    public static AddressBook TEST_ADDR = AddressBook.REPLICAS;

    public SocketTest() throws SocketException {
        m_Socket = new Socket();
        m_ListOfMessages = new LinkedList<>();
    }

    @Before
    public void setUp() {
        m_Listener = new RequestListener(this, TEST_ADDR);
        m_ListenerThread = new Thread(m_Listener);
        m_ListenerThread.start();
        m_Listener.Wait();
    }

    @After
    public void tearDown() throws InterruptedException {
        m_Listener.Stop();
        m_ListenerThread.join();
    }

    /**
     * Test of send method, of class Socket.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testSendGetsAck() throws Exception {
        Message send = new Message(OperationCode.TRANSFER_RECORD, 5654, "TESTING", TEST_ADDR);

        assertEquals(true, m_Socket.send(send, 10, 1000));

        // Make sure capture message was what we sent!
        assertEquals(m_ListOfMessages.size(), 1);
        
        for (Message msg : m_ListOfMessages) {
            assertEquals(msg.getOpCode(), send.getOpCode());
            assertEquals(msg.getData(), "TESTING");
        }

        assertEquals(m_Socket.getResponse().getSeqNum(), 5654);
        assertEquals(m_Socket.getResponse().getData(), "RETVAL");
        assertEquals(m_Socket.getResponse().getOpCode(), OperationCode.ACK_TRANSFER_RECORD);
    }

    @Test
    public void testSendTimesOut() throws Exception {
        System.out.println("send timeout");

        InetAddress addr = InetAddress.getLoopbackAddress();

        // using invalid port 'ensures' we wont get an answer
        Message send = new Message(OperationCode.TRANSFER_RECORD, 0, RegisteredReplica.INVALID, "TESTING", addr, 46873);
        Socket instance = new Socket();

        assertEquals(false, instance.send(send, 5, 500));
        assertEquals(m_ListOfMessages.size(), 0);
        assertEquals(m_Socket.getResponse(), null);
    }

    @Test
    public void testSendWithLocation() throws Exception {
        Message send = new Message(OperationCode.GET_RECORD_COUNT, 456874, "LOCATION", TEST_ADDR);
        send.setLocation(RegisteredReplica.ReplicaS1);

        assertEquals(true, m_Socket.send(send, 10, 1000));

        // Make sure capture message was what we sent!
        assertEquals(m_ListOfMessages.size(), 1);

        for (Message msg : m_ListOfMessages) {
            assertEquals(msg.getOpCode(), send.getOpCode());
            assertEquals(msg.getData(), "LOCATION");
            assertEquals(msg.getLocation(), RegisteredReplica.ReplicaS1);
        }

        assertEquals(m_Socket.getResponse().getSeqNum(), 456874);
        assertEquals(m_Socket.getResponse().getData(), "RETVAL");
        assertEquals(m_Socket.getResponse().getOpCode(), OperationCode.ACK_GET_RECORD_COUNT);
        assertEquals(m_Socket.getResponse().getLocation(), RegisteredReplica.ReplicaS1);
    }

    @Test
    public void testSendToLocation() throws Exception {
        Message send = new Message(OperationCode.DOES_RECORD_EXIST, 98465, "EVERYWHERE", TEST_ADDR);

        assertEquals(true, m_Socket.sendTo(RegisteredReplica.values(), send, 10, 1000));

        // Make sure capture message was what we sent!
        for (Message msg : m_ListOfMessages) {
            assertEquals(msg.getOpCode(), send.getOpCode());
            assertEquals(msg.getData(), "EVERYWHERE");
            assertNotEquals(msg.getLocation(), RegisteredReplica.INVALID);
        }

        assertEquals(m_Socket.getResponse().getSeqNum(), 98465);
        assertEquals(m_Socket.getResponse().getData(), "RETVAL");
        assertEquals(m_Socket.getResponse().getOpCode(), OperationCode.ACK_DOES_RECORD_EXIST);
        //assertEquals(m_Socket.getResponse().getLocation(), Location.CA);
    }

    @Override
    public String handleRequestMessage(Message msg) throws Exception {
        int randomNum = ThreadLocalRandom.current().nextInt(0, 3);
        if (randomNum == 0) {
            throw new Exception("Dummy Exception");
        }
        
        this.m_ListOfMessages.add(msg);
        return "RETVAL";
    }
}
