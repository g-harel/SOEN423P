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
import UDP.Socket;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author cmcarthur
 */


public class SequencerTest {
    
    public SequencerTest() {
    }

    /**
     * Test of main method, of class Sequencer.
     */
    @Test
    public void testMain() throws Exception {
        String[] args = null;
        Sequencer.main(args);

        Socket instance = new Socket();
        
        Message msg = new Message(OperationCode.TRANSFER_RECORD, 0, "TESTING", AddressBook.SEQUENCER);
        instance.send(msg, 5, 500);
        
        Message response = instance.getResponse();
        
        // TO DO: how to determine expected Sequence number =?

        fail("The test case is a prototype.");
    }
    
}
