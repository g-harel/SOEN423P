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

import Interface.Corba.Project;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author cmcarthur
 */


public class FrontEndTest {
    
    public FrontEndTest() {
    }

    /**
     * Test of main method, of class FrontEnd.
     */
    @Test
    public void testMain() {
        System.out.println("main");
        String[] args = null;
        FrontEnd.main(args);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of createMRecord method, of class FrontEnd.
     */
    @Test
    public void testCreateMRecord() {
        System.out.println("createMRecord");
        String managerID = "";
        String firstName = "";
        String lastName = "";
        int employeeID = 0;
        String mailID = "";
        Project project = null;
        String location = "";
        FrontEnd instance = new FrontEnd();
        String expResult = "";
        String result = instance.createMRecord(managerID, firstName, lastName, employeeID, mailID, project, location);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of createERecord method, of class FrontEnd.
     */
    @Test
    public void testCreateERecord() {
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
    public void testGetRecordCounts() {
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
    public void testEditRecord() {
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

    /**
     * Test of transferRecord method, of class FrontEnd.
     */
    @Test
    public void testTransferRecord() {
        System.out.println("transferRecord");
        String managerID = "";
        String recordID = "";
        String location = "";
        FrontEnd instance = new FrontEnd();
        String expResult = "";
        String result = instance.transferRecord(managerID, recordID, location);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of softwareFailure method, of class FrontEnd.
     */
    @Test
    public void testSoftwareFailure() {
        System.out.println("softwareFailure");
        String managerID = "";
        FrontEnd instance = new FrontEnd();
        instance.softwareFailure(managerID);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of replicaCrash method, of class FrontEnd.
     */
    @Test
    public void testReplicaCrash() {
        System.out.println("replicaCrash");
        String managerID = "";
        FrontEnd instance = new FrontEnd();
        instance.replicaCrash(managerID);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
