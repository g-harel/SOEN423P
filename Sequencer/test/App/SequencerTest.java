/* 
    MIT License

    Copyright (c) 2018

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.
 */
package App;

import Models.AddressBook;
import UDP.Message;
import UDP.OperationCode;
import UDP.RequestListener;
import UDP.Socket;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 *
 * @author cmcarthur
 */
public class SequencerTest implements RequestListener.Processor {

    private RequestListener m_Listener;
    private Thread m_ListenerThread;
    private List<Message> m_ListOfMessages;

    public static AddressBook TEST_ADDR = AddressBook.REPLICAS; // For mocking purpose

    public SequencerTest() {
    }

    @BeforeClass
    static public void setup() {
        // TO DO: While most liekly have to be a seperate thread !
        Sequencer.main(null); // Run the whole sequencer application
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

    @Test
    public void testSequencer() throws Exception {
        Socket frontendMock = new Socket();
        Message forward = new Message(OperationCode.TRANSFER_RECORD, 0, "TESTING ABC", AddressBook.SEQUENCER);
        
        assertTrue("Seq should ACK request", frontendMock.send(forward, 5, 500) );

        Message response = frontendMock.getResponse();

        assertEquals("response should be ACK_OPERATIOIN", OperationCode.ACK_TRANSFER_RECORD, response.getOpCode());
        assertEquals("response should be SeqNum=0", 0, response.getSeqNum()); // Unmodified by RUDP server
        assertTrue("response paylod should be SEQ=#", response.getData().startsWith("SEQ="));

        int seqNumber = Integer.valueOf(response.getData().substring("SEQ=".length()));
        assertNotEquals("SeqNum should be greater the Zero", 0, seqNumber);

        assertEquals(m_ListOfMessages.size(), 1);

        for (Message msg : m_ListOfMessages) {
            assertEquals(msg.getOpCode(), forward.getOpCode());
            assertEquals(msg.getData(), "TESTING ABC");
            assertEquals(msg.getSeqNum(), seqNumber);
        }
    }

    @Test
    public void testPlayback() throws Exception {
        Socket frontendMock = new Socket();
        Message[] forward = {
            new Message(OperationCode.CREATE_EMPLOYEE_RECORD, 0, "TESTING E1", AddressBook.SEQUENCER),
            new Message(OperationCode.CREATE_MANAGER_RECORD, 0, "TESTING M1", AddressBook.SEQUENCER),
            new Message(OperationCode.GET_RECORD_COUNT, 0, "TESTING ABC", AddressBook.SEQUENCER),
            new Message(OperationCode.CREATE_EMPLOYEE_RECORD, 0, "TESTING E1", AddressBook.SEQUENCER),
            new Message(OperationCode.TRANSFER_RECORD, 0, "TESTING TR", AddressBook.SEQUENCER),};

        for (Message msg : forward) {
            assertTrue("Seq should ACK request", frontendMock.send(msg, 5, 500) );
        }
        
        String response = frontendMock.getResponse().getData();
        assertTrue("response paylod should be SEQ=#", response.startsWith("SEQ="));
        
        int seqNumber = Integer.valueOf(response.substring("SEQ=".length()));
        assertNotEquals("SeqNum should be greater the Zero", 0, seqNumber);
        
        assertEquals(m_ListOfMessages.size(), forward.length);
        
        m_ListOfMessages.clear();
        // TO DO: Evaluate what the ordering should be of the recieved messages =?
        
        Socket managerMock = new Socket();
        Message seqResendCommand = new Message ( OperationCode.DUMP, 0, "RETRANSMIT THE WHOLE WORLD !", AddressBook.SEQUENCER );
        
        assertTrue("Seq should ACK request", managerMock.send(seqResendCommand, 5, 500));
        
        Thread.sleep(1250); // Wait for retransmission to complete!
        
        assertEquals(m_ListOfMessages.size(), forward.length);
    }

    @Override
    public String handleRequestMessage(Message msg) throws Exception {
        int randomNum = ThreadLocalRandom.current().nextInt(0, 3);
        if (randomNum == 0) {
            throw new Exception("Dummy Error");
        }

        this.m_ListOfMessages.add(msg);
        return "RETVAL";
    }

}
