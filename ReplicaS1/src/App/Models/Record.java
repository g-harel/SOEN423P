/*

Name: Andres Vazquez (#40007182)
Course: SOEN 423

*/

package App.Models;

import java.io.Serializable;

public abstract class Record implements Serializable {
    private static int idNumber = 10000;
    private String id;
    private String fName;
    private String lName;
    private int empID;
    private String mailID;
    
    public Record(String empTypeCode, String fName, String lName, int empID, String mailID) {
        this.id = createRecordID(empTypeCode);
        this.fName = fName;
        this.lName = lName;
        this.empID = empID;
        this.mailID = mailID;
    }
    
    /*
    * Starting with MR (for ManagerRecord) or ER (for EmployeeRecord) and ending with a 5 digits number (e.g. MR10000 for a ManagerRecord or ER10001 for an EmployeeRecord).
    */
    private String createRecordID(String empTypeCode) {
        String output;
    
        synchronized (Record.class) {
            output = empTypeCode + idNumber;
            idNumber++;
        }
        
        return output;
    }
    
    public String getRecordID() {
        return id;
    }
    
    public String getfName() {
        return fName;
    }
    
    public void setfName(String fName) {
        this.fName = fName;
    }
    
    public String getlName() {
        return lName;
    }
    
    public void setlName(String lName) {
        this.lName = lName;
    }
    
    public int getEmpID() {
        return empID;
    }
    
    public void setEmpID(int empID) {
        this.empID = empID;
    }
    
    public String getMailID() {
        return mailID;
    }
    
    public synchronized void setMailID(String mailID) {
        this.mailID = mailID;
    }
    
    @Override
    public String toString() {
        return "id='" + id + '\'' +
                ", fName='" + fName + '\'' +
                ", lName='" + lName + '\'' +
                ", empID=" + empID +
                ", mailID='" + mailID + '\'';
    }
}
