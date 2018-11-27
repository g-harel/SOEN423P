/*

Name: Andres Vazquez (#40007182)
Course: SOEN 423

*/

package App.Models;

import java.io.Serializable;

public class EmployeeRecord extends Record implements Serializable {
    private String projectID;
    
    public EmployeeRecord(String fName, String lName, int empId, String mailId, String projectID) {
        super("ER", fName, lName, empId, mailId);
        
        this.projectID = projectID;
    }
    
    public EmployeeRecord(String fName, String lName, int empId, String mailId) {
        super("ER", fName, lName, empId, mailId);

        this.projectID = "";
    }
    
    public String getProjectID() {
        return projectID;
    }
    
    public synchronized void setProjectID(String projectID) {
        this.projectID = projectID;
    }
    
    @Override
    public String toString() {
        return "EmployeeRecord{" +
                super.toString() +
                ", projectID='" + projectID + '\'' +
                '}';
    }
}
