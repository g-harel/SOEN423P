/*

Author: Andres Vazquez
Course: SOEN 423

*/

package Utility;

import java.util.Date;

public class LogEntry {
    private Date timestamp;
    private String managerID;
    private String operation;
    private boolean success;
    
    public LogEntry(String managerID, String operation, boolean wasSuccessful) {
        this.timestamp = new Date();
        this.managerID = managerID;
        this.operation = operation;
        this.success = wasSuccessful;
    }
    
    public Date getTimestamp() {
        return timestamp;
    }
    
    public String getManagerID() {
        return managerID;
    }
    
    public void setManagerID(String managerID) {
        this.managerID = managerID;
    }
    
    public String getOperation() {
        return operation;
    }
    
    public void setOperation(String operation) {
        this.operation = operation;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    protected String getIfSuccess() {
        if (success) {
            return "Succeeded";
        }
        else {
            return "Failed";
        }
    }
    
    @Override
    public String toString() {
        return getTimestamp() + " - " + managerID + " - " + getOperation() + " - " + getIfSuccess();
    }
}

