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

import Interface.Corba.IFrontEnd;
import Interface.Corba.IFrontEndHelper;
import Interface.Corba.Project;
import Models.AddressBook;
import Models.Location;
import Utility.LogEntry;
import Utility.OperationLogger;

import java.io.File;
import java.io.IOException;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

/**
 *
 * @author cmcarthur
 */
public class RegionalClient {

    final private ORB orb;
    final private String m_HRID;
    final private Location m_Region;
    final private IFrontEnd m_Remote;

    public RegionalClient(ORB orb, String id) throws IOException, Exception {
        this.orb = orb;
        m_HRID = id.toUpperCase();
        m_Region = Location.fromString(id.substring(0, 2));

        org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
        NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
        m_Remote = IFrontEndHelper.narrow(ncRef.resolve_str(AddressBook.FRONTEND.getShortHandName()));

        LogEntry logEntry = new LogEntry(m_HRID, "Login", true);
        addHRLogEntry(logEntry.toString());
    }

    public String createManagerRecord(String firstName, String lastName, int employeeID, String mailID, Project projects, String location) {
        String logEntry = m_Remote.createMRecord(m_HRID, firstName, lastName, employeeID, mailID, projects, location);

        addHRLogEntry(logEntry);
        return logEntry;
    }

    public String createEmployeeRecord(String firstName, String lastName, int employeeID, String mailID, String projectId) {
        String logEntry = m_Remote.createERecord(m_HRID, firstName, lastName, employeeID, mailID, projectId);

        addHRLogEntry(logEntry);
        return logEntry;
    }

    public String editRecord(String recordID, String feildName, String newValue) {
        String logEntry = m_Remote.editRecord(m_HRID, recordID, feildName, newValue);

        addHRLogEntry(logEntry);
        return logEntry;
    }

    public String transferRecord(String recordID, String location) {
        String logEntry = m_Remote.transferRecord(m_HRID, recordID, location);

        addHRLogEntry(logEntry);
        return logEntry;
    }

    public String getRecordCounts() {
        String logEntry = m_Remote.getRecordCounts(m_HRID);

        addHRLogEntry(logEntry);
        return logEntry;
    }

    public int getRegionalRecordCount() {
        String allDesc = m_Remote.getRecordCounts(m_HRID);

        allDesc = allDesc.substring(allDesc.indexOf(m_Region.getPrefix()) + 3);
        allDesc = allDesc.substring(0, allDesc.indexOf(" "));

        return Integer.parseInt(allDesc);
    }

    public void softwareFailure() {
        m_Remote.softwareFailure(m_HRID);
    }

    public void replicaCrash() {
        m_Remote.replicaCrash(m_HRID);
    }

    public void clearLog() {
        OperationLogger.deleteLogFile(new File("Logs/HR/" + m_HRID + ".txt"));
    }

    private void addHRLogEntry(String... data) {
        String hrLogFilePath = "Logs/HR/" + m_HRID + ".txt";
        String dataToLog = String.join("\n", data) + "\n";

        OperationLogger.log(hrLogFilePath, dataToLog);
    }
}
