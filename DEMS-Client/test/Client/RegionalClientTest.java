/* 
    MIT License

    Copyright (c) 2018 Chris Mc, prince.chrismc(at)gmail(dot)com

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
package Client;

import Interface.Corba.Project;
import Models.Feild;
import Models.Location;
import java.util.HashSet;
import java.util.Set;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;

/**
 *
 * @author cmcarthur
 */
public class RegionalClientTest {

    static private ORB orb;
    static private Set<String> ListOfIDs;

    private RegionalClient Primary;
    private RegionalClient EmptyServer;
    private RegionalClient Secondary;

    public RegionalClientTest() {
    }

    @BeforeClass
    static public void setupRegistry() throws AdapterInactive, InvalidName {
        String[] args = {"-ORBInitialPort", "1050", "-ORBInitialHost", "localhost"};
        orb = ORB.init(args, null);

        ListOfIDs = new HashSet<>();
    }

    @Before
    public void setUp() throws Exception {
        Primary = new RegionalClient(orb, "CA1234");
        Secondary = new RegionalClient(orb, "UK3698");
        EmptyServer = new RegionalClient(orb, "US9874");
    }

    /**
     * Test of createManagerRecord method, of class RegionalClient.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testCreateManagerRecord() throws Exception {
        int startNumberOfRecords = Primary.getRegionalRecordCount();
        String managerOne = Primary.createManagerRecord("john", "smith", 1001, "johm.smith@example.com", new Project("P0", "Huge Project", "Rich Client"), Location.CA.toString());
        assertEquals("Should only be one new record", startNumberOfRecords + 1, Primary.getRegionalRecordCount());
        assertTrue("New IDs must be unique", ListOfIDs.add(managerOne));
        String managerTwo = Primary.createManagerRecord("jane", "doe", 36978, "jane.dow@example.com", new Project("P23", "Huge Project", "Rich Client"), Location.US.toString());
        assertEquals("Should only be two new records", startNumberOfRecords + 2, Primary.getRegionalRecordCount());
        assertTrue("New IDs must be unique", ListOfIDs.add(managerTwo));
        assertNotEquals("Manager IDs should be unique", managerOne, managerTwo);
    }

    /**
     * Test of createEmployeeRecord method, of class RegionalClient.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testCreateEmployeeRecord() throws Exception {
        int startNumberOfRecords = Primary.getRegionalRecordCount();
        String newEmployee = Primary.createEmployeeRecord("james", "bond", 1001, "johm.smith@example.com", "P23001");
        assertEquals("Should only be one new record", startNumberOfRecords + 1, Primary.getRegionalRecordCount());
        assertTrue("New IDs must be unique", ListOfIDs.add(newEmployee));
    }

    /**
     * Test of editRecord method, of class RegionalClient.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void canEditMangerRecords() throws Exception {
        int startNumberOfRecords = Primary.getRegionalRecordCount();
        String recordId = Primary.createManagerRecord("john", "smith", 1001, "johm.smith@example.com", new Project("P0", "Huge Project", "Rich Client"), Location.CA.toString());
        assertEquals("Should only be one new record", ++startNumberOfRecords, Primary.getRegionalRecordCount());
        assertTrue("New IDs must be unique", ListOfIDs.add(recordId));

        assertEquals("Record ID should not change", recordId, Primary.editRecord(recordId, Feild.LOCATION.toString(), Location.UK.toString()));
        assertEquals("Record ID should not change", recordId, Primary.editRecord(recordId, Feild.EMPLOYEE_ID.toString(), String.valueOf(98765)));

        assertEquals("Should only be same number of records", startNumberOfRecords, Primary.getRegionalRecordCount());
    }

    @Test
    public void canEditEmployeeRecords() throws Exception {
        int startNumberOfRecords = Primary.getRegionalRecordCount();
        String recordId = Primary.createEmployeeRecord("james", "bond", 1001, "johm.smith@example.com", "P23001");
        assertEquals("Should only be one new record", ++startNumberOfRecords, Primary.getRegionalRecordCount());
        assertTrue("New IDs must be unique", ListOfIDs.add(recordId));

        assertEquals("Record ID should not change", recordId, Primary.editRecord(recordId, Feild.FIRST_NAME.toString(), "James"));
        assertEquals("Record ID should not change", recordId, Primary.editRecord(recordId, Feild.LAST_NAME.toString(), "BOND"));
        assertEquals("Record ID should not change", recordId, Primary.editRecord(recordId, Feild.EMPLOYEE_ID.toString(), String.valueOf(9007)));

        assertEquals("Should only be same number of records", startNumberOfRecords, Primary.getRegionalRecordCount());
    }

    @Test
    public void canTransferRecord() throws Exception {
        int PrimaryRecordCounter = Primary.getRegionalRecordCount();
        int SecondaryRecordCounter = Secondary.getRegionalRecordCount();
        int EmptyRecordCounter = EmptyServer.getRegionalRecordCount();

        String recordId = Primary.createEmployeeRecord("james", "bond", 1001, "johm.smith@example.com", "P23001");
        assertTrue("New IDs must be unique", ListOfIDs.add(recordId));
        assertEquals("Should only be one new record", ++PrimaryRecordCounter, Primary.getRegionalRecordCount());
        assertEquals("Should not be a new record", SecondaryRecordCounter, Secondary.getRegionalRecordCount());
        assertEquals("Should not be a new record", EmptyRecordCounter, EmptyServer.getRegionalRecordCount());

        assertEquals("Record ID should not change", recordId, Primary.transferRecord(recordId, Location.UK.toString()));
        assertEquals("Should only be one less record", --PrimaryRecordCounter, Primary.getRegionalRecordCount());
        assertEquals("Should only be one new record", ++SecondaryRecordCounter, Secondary.getRegionalRecordCount());
        assertEquals("Should not be a new record", EmptyRecordCounter, EmptyServer.getRegionalRecordCount());

        assertEquals("Record ID should not change", recordId, Secondary.editRecord(recordId, Feild.FIRST_NAME.toString(), "James"));
        assertEquals("Record ID should not change", recordId, Secondary.editRecord(recordId, Feild.LAST_NAME.toString(), "BOND"));
        assertEquals("Record ID should not change", recordId, Secondary.editRecord(recordId, Feild.EMPLOYEE_ID.toString(), String.valueOf(9007)));

        assertEquals("Should only be same number of records", PrimaryRecordCounter, Primary.getRegionalRecordCount());
        assertEquals("Should only be same number of records", SecondaryRecordCounter, Secondary.getRegionalRecordCount());
        assertEquals("Should not be a new record", EmptyRecordCounter, EmptyServer.getRegionalRecordCount());
    }

    @Test
    public void testCreateManagerRecordWithBadValues() throws Exception {
        int startNumberOfRecords = EmptyServer.getRegionalRecordCount();

        // Bad Region
        assertTrue("Should return ERROR message",
                EmptyServer.createManagerRecord("john", "smith", 116546841, "johm.smith@example.com",
                        new Project("P001", "Huge Project", "Rich Client"), "Republic of McArthur").startsWith("ERROR"));
        assertEquals("Should not be a new record", startNumberOfRecords, EmptyServer.getRegionalRecordCount());
    }

    @Test
    public void testCreateEmployeeRecordWithBadValues() throws Exception {
        int startNumberOfRecords = EmptyServer.getRegionalRecordCount();

        // Bad format project ID
        assertTrue("Should return ERROR message",
                EmptyServer.createEmployeeRecord("john", "smith", 5479, "johm.smith@example.com", "P35+6").startsWith("ERROR"));
        assertEquals("Should not be a new record", startNumberOfRecords, EmptyServer.getRegionalRecordCount());
        assertTrue("Should return ERROR message",
                EmptyServer.createEmployeeRecord("john", "smith", 5479, "johm.smith@example.com", "54687").startsWith("ERROR"));
        assertEquals("Should not be a new record", startNumberOfRecords, EmptyServer.getRegionalRecordCount());

        // Project ID too long
        assertTrue("Should return ERROR message",
                EmptyServer.createEmployeeRecord("john", "smith", 5479, "johm.smith@example.com", "P458754687").startsWith("ERROR"));
        assertEquals("Should not be a new record", startNumberOfRecords, EmptyServer.getRegionalRecordCount());
    }
}
