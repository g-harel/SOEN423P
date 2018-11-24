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
import java.util.concurrent.ThreadLocalRandom;
import java.net.InetAddress;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author cmcarthur
 */
public class SocketTest implements RequestListener.Processor {

    final private RequestListener m_Listener;
    private Thread m_ListenerThread;
    Message msg;

    public static AddressBook TEST_ADDR = AddressBook.REPLICAS;

    public SocketTest() {
        m_Listener = new RequestListener(this, TEST_ADDR);
    }

    /**
     * Test of send method, of class Socket.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testSendGetsAck() throws Exception {
        m_ListenerThread = new Thread(m_Listener);
        m_ListenerThread.start();
        m_Listener.Wait();

        Message send = new Message(OperationCode.TRANSFER_RECORD, 5654, "TESTING", TEST_ADDR);
        Socket instance = new Socket();

        assertEquals(true, instance.send(send, 10, 1000));

        // Make sure capture message was what we sent!
        assertEquals(msg.getOpCode(), send.getOpCode());
        assertEquals(msg.getData(), "TESTING");
        assertEquals(instance.getResponse().getSeqNum(), 5654);
        assertEquals(instance.getResponse().getData(), "RETVAL");
        assertEquals(instance.getResponse().getOpCode(), OperationCode.ACK_TRANSFER_RECORD);

        m_Listener.Stop();
        m_ListenerThread.join();
    }

    @Test
    public void testSendTimesOut() throws Exception {
        System.out.println("send timeout");

        InetAddress addr = InetAddress.getLoopbackAddress();

        // using invalid port 'ensures' we wont get an answer
        Message send = new Message(OperationCode.TRANSFER_RECORD, 0, "TESTING", addr, 46873);
        Socket instance = new Socket();

        assertEquals(false, instance.send(send, 5, 500));
    }

    @Override
    public String handleRequestMessage(Message msg) throws Exception {
        this.msg = msg;

        int randomNum = ThreadLocalRandom.current().nextInt(0, 3);
        if (randomNum == 0) {
            throw new Exception("Dummy Exception");
        }

        return "RETVAL";
    }
}
