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
package App;

import Interface.Corba.Project;
import Models.AddressBook;
import Models.Location;
import UDP.Message;
import UDP.OperationCode;
import UDP.RequestListener;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author cmcarthur
 */
public class FrontEndTest implements RequestListener.Processor {

    static private RequestListener m_Listener;
    static private Thread m_ListenerThread;
    private List<Message> m_ListOfMessages;

    public static AddressBook TEST_ADDR = AddressBook.SEQUENCER; // For mocking purpose

    public FrontEndTest() {
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
     * Test of createMRecord method, of class FrontEnd.
     * @throws java.net.SocketException
     */
    @Test
    public void testCreateMRecord() throws SocketException {
        System.out.println("createMRecord");
        FrontEnd instance = new FrontEnd();
        String result = instance.createMRecord("CA134", "john", "smith", 1001, "johm.smith@example.com", new Project("P0", "Huge Project", "Rich Client"), Location.CA.toString());
        assertTrue( result.contains("MR") );
        
         assertEquals(m_ListOfMessages.size(), 1);

        for (Message msg : m_ListOfMessages) {
            assertEquals(msg.getOpCode(), OperationCode.CREATE_MANAGER_RECORD);
            assertEquals(msg.getData(), "TESTING ABC");
            assertEquals(msg.getSeqNum(), 1);
}
    }

    /**
     * Test of createERecord method, of class FrontEnd.
     */
    @Test
    public void testCreateERecord() throws SocketException {
        System.out.println("createERecord");
        String managerID = "";
        String firstName = "";
        String lastName = "";
        int employeeID = 0;
        String mailID = "";
        String projectID = "";
        FrontEnd instance = new FrontEnd();
        String expResult = "";
        String result = instance.createERecord(managerID, firstName, lastName, employeeID, mailID, projectID);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getRecordCounts method, of class FrontEnd.
     */
    @Test
    public void testGetRecordCounts() throws SocketException {
        System.out.println("getRecordCounts");
        String managerID = "";
        FrontEnd instance = new FrontEnd();
        String expResult = "";
        String result = instance.getRecordCounts(managerID);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of editRecord method, of class FrontEnd.
     */
    @Test
    public void testEditRecord() throws SocketException {
        System.out.println("editRecord");
        String managerID = "";
        String recordID = "";
        String fieldName = "";
        String newValue = "";
        FrontEnd instance = new FrontEnd();
        String expResult = "";
        String result = instance.editRecord(managerID, recordID, fieldName, newValue);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    @Override
    public String handleRequestMessage(Message msg) throws Exception {
        int randomNum = ThreadLocalRandom.current().nextInt(0, 3);
        if (randomNum == 0) {
            throw new Exception("Dummy Error");
        }

        this.m_ListOfMessages.add(msg);
        return "Seq=1";
}

}
