/*

Name: Andres Vazquez (#40007182)
Course: SOEN 423

*/

package App.Remote;

import Models.*;
import Utility.ICenterServer;
import Utility.LogEntry;
import Utility.OperationLogger;
import Utility.ReplicaResponse;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;

import App.Models.EmployeeRecord;
import App.Models.ManagerRecord;
import App.Models.Record;
import Interface.Corba.Project;

public class CenterServer implements ICenterServer, Runnable {
    private HashMap<Character, List<Record>> records = new HashMap<>();
    private HashMap<Location, Integer> centerServerPorts = new HashMap<>();
    private Location centerLocation;
    
    public CenterServer(Location location) {
        super();
        
        centerServerPorts.put(Location.CA, 8000);
        centerServerPorts.put(Location.US, 8001);
        centerServerPorts.put(Location.UK, 8002);
        
        this.centerLocation = location;
        clearLogs();
    }
    
    @Override
    public synchronized ReplicaResponse createMRecord(String managerID, String firstName, String lastName, int employeeID, String mailID, Project project, String location) {
        Record newManager = null;
		try {
			newManager = new ManagerRecord(firstName, lastName, employeeID, mailID, project, Location.fromString(location));
		} catch (Exception e) {
			e.printStackTrace();
		}
        
        return createRecord(managerID, newManager, "createMRecord");
    }
    
    @Override
    public synchronized ReplicaResponse createERecord(String managerID, String firstName, String lastName, int employeeID, String mailID, String projectID) {
        Record newEmployee = new EmployeeRecord(firstName, lastName, employeeID, mailID, projectID);
    
        return createRecord(managerID, newEmployee, "createERecord");
    }
    
    private synchronized ReplicaResponse createRecord(String managerID, Record newRecord, String operationPerformed) {
        String operation = operationPerformed;
        String outputLog;
        
        addRecord(newRecord);
//        System.out.println("A new record was added: " + newRecord.getRecordID());
        printData();
        
        LogEntry logEntry = new LogEntry(managerID, operation, true);
        outputLog = addLogEntry(logEntry.toString(), newRecord.toString());
        
        return new ReplicaResponse(true, "");
    }
    
    public ReplicaResponse getRecordCounts(String managerID) {
        Map<Location, Integer> recordCount = new HashMap<>();
        boolean didThrowException = false;
    
        recordCount.put(centerLocation, getCenterCount());
        
        // UDP Client
        for (Location location: Location.values()) {
            if (!location.equals(centerLocation)) {
                byte[] message = "getRecordCounts".getBytes();
                
                try {
                    String response = sendUDPRequest(message, InetAddress.getByName("localhost"), centerServerPorts.get(location));
                    recordCount.put(location, Integer.parseInt(response));
                }
                catch (Exception e) {
                    // Error
                    String errorMsg = "The UDP Client threw an exception when getting the Records count for " + location +"(" + e.getMessage() + ")";
                    LogEntry logEntry = new LogEntry(managerID, "getRecordCounts", false);
    
                    addLogEntry(logEntry.toString(), "Error Message: " + errorMsg);
    
                    System.out.println(errorMsg);
                    didThrowException = true;
                }
            }
        }
        
        if (!didThrowException) {
            LogEntry logEntry = new LogEntry(managerID, "getRecordCounts", true);
            addLogEntry(logEntry.toString(), recordCount.toString());
        }
        
        return new ReplicaResponse(true, recordCount.toString());
    }
    
    @Override
    public synchronized ReplicaResponse editRecord(String managerID, String recordID, String fieldName, String newValue) {
        boolean errorFound = false;
        String outputLog = "";
        Record record = findRecord(recordID);
        
        if (record != null) {
            if (recordID.substring(0, 2).equalsIgnoreCase("MR")) {
                ManagerRecord manager = (ManagerRecord) record;
        
                switch (fieldName) {
                    case "mailID":
                        manager.setMailID(newValue);
                        break;
                    case "projectID":
                        manager.getProject().projectId = newValue;
                        break;
                    case "clientName":
                        manager.getProject().clientName = newValue;
                        break;
                    case "projectName":
                        manager.getProject().projectName = newValue;
                        break;
                    case "location":
                        manager.setLocation(newValue);
                        break;
                    default:
                        // Error
                        errorFound = true;
                        String errorMsg = "The field name entered is not valid (" + fieldName + ")";
                        LogEntry logEntry = new LogEntry(managerID, "editRecord", false);
    
                        outputLog = addLogEntry(logEntry.toString(), "Error Message: " + errorMsg, "User Input (Field name): " + recordID);
                
                        System.out.println(errorMsg);
                }
            }
            else {
                EmployeeRecord employee = (EmployeeRecord) record;
        
                switch (fieldName) {
                    case "mailID":
                        employee.setMailID(newValue);
                        break;
                    case "projectID":
                        employee.setProjectID(newValue);
                        break;
                    default:
                        // Error
                        errorFound = true;
                        String errorMsg = "The field name entered is not valid (" + fieldName + ")";
                        LogEntry logEntry = new LogEntry(managerID, "editRecord", false);
    
                        outputLog = addLogEntry(logEntry.toString(), "Error Message: " + errorMsg, "User Input (Field name): " + recordID);
                
                        System.out.println(errorMsg);
                }
            }
        }
        else {
            errorFound = true;
            String errorMsg = "The Record ID entered (" + recordID + ") was not found in the '" + centerLocation + "' CenterServer.";
            LogEntry logEntry = new LogEntry(managerID, "editRecord", false);
            outputLog = addLogEntry(logEntry.toString(),  "Error Message: " + errorMsg);
            
            System.out.println(errorMsg);
        }
        
        // If the edit was made
        if (!errorFound) {
            LogEntry logEntry = new LogEntry(managerID, "editRecord", true);
            outputLog = addLogEntry(logEntry.toString(), "Edit Record: " + "{recordID=" + recordID + ", fieldName=" + fieldName + ", newValue=" + newValue + "}");
    
            System.out.println("A record was edited: " + recordID);
            printData();
        }
        
        return new ReplicaResponse(!errorFound, outputLog);
    }
    
    private Record findRecord(String recordID) {
        Record desiredRecord = null;
        
        searchRecordID:
        for (List<Record> recordsList: records.values()) {
            for (Record record: recordsList) {
                if (record.getRecordID().equalsIgnoreCase(recordID)) {
                    desiredRecord = record;
                    break searchRecordID;
                }
            }
        }
        
        return desiredRecord;
    }
    
    @Override
    public ReplicaResponse transferRecord(String managerID, String recordID, String remoteCenterServerName) {
        Record record = findRecord(recordID);
        boolean errorFound = false;
        String outputLog = "";
        
        if (record != null) {
            System.out.println("transferRecord: The record (" + recordID + ") was found in the '" + centerLocation + "' CenterServer");
            
            byte[] message = ("transferRecord: " + recordID).getBytes();
            
            try {
                String response = sendUDPRequest(message, InetAddress.getLocalHost(), centerServerPorts.get(Location.fromString(remoteCenterServerName)));

                if (!Boolean.parseBoolean(response)) {
                    System.out.println("transferRecord: UDP Check - The record (" + recordID + ") doesn't exists in the '" + remoteCenterServerName + "' CenterServer");
                    Map<String, Object> dataToSend = new HashMap<>();
                    
                    dataToSend.put("managerID", managerID);
                    dataToSend.put("record", record);
                    
                    ByteArrayOutputStream bStream = new ByteArrayOutputStream();
                    ObjectOutput oo = new ObjectOutputStream(bStream);
                    oo.writeObject(dataToSend);
                    oo.close();

                    byte[] serializedMessage = bStream.toByteArray();
                
                    outputLog = sendUDPRequest(serializedMessage, InetAddress.getLocalHost(), centerServerPorts.get(Location.fromString(remoteCenterServerName)));
                    
                    LogEntry logEntry = new LogEntry(managerID, "transferRecord (OUT) to " + remoteCenterServerName + " server", true);
                    outputLog = addLogEntry(logEntry.toString(), record.toString());
                    
                    // Delete Record from current CenterServer
                    char letter = Character.toUpperCase(record.getlName().charAt(0));
                    records.get(letter).remove(record);
                }
                else {
                	errorFound = true;
                    String errorMsg = "The Record ID entered (" + recordID + ") was found on both CenterServers. The record will not be transferred.";
                    LogEntry logEntry = new LogEntry(managerID, "transferRecord", false);
                    outputLog = addLogEntry(logEntry.toString(),  "Error Message: " + errorMsg);
    
                    System.out.println(errorMsg);
                }
            }
            catch (Exception e) {
            	errorFound = true;
                e.printStackTrace();
            }
        }
        else {
        	errorFound = true;
            String errorMsg = "The Record ID entered (" + recordID + ") was not found in the '" + centerLocation + "' CenterServer.";
            LogEntry logEntry = new LogEntry(managerID, "transferRecord", false);
            outputLog = addLogEntry(logEntry.toString(),  "Error Message: " + errorMsg);
    
            System.out.println(errorMsg);
        }
        
        return new ReplicaResponse(!errorFound, outputLog);
    }
    
    @Override
	public void softwareFailure(String managerID) {
		// TODO Auto-generated method stub
	}


	@Override
	public void replicaCrash(String managerID) {
		// TODO Auto-generated method stub
	}
    
    public synchronized void printData() {
        List<Record> recordList;
        String out = "";
        
        out += centerLocation + " Records:\n";
        
        for (char letter: records.keySet()) {
    
            out += letter + " = [\n";
            recordList = records.get(letter);
            
            for (Record record: recordList) {
                out += "    " + record +"\n";
            }
            out += "]\n";
        }
    
        out += "----------------------------------------------------------------------------------------------------\n\n";
        System.out.println(out);
    }
    
    private synchronized void addRecord(Record record) {
        char letter = Character.toUpperCase(record.getlName().charAt(0));
        List<Record> recordsForLetter = records.get(letter);
    
        // Check if list exists (if there is at least one record starting with the same Last Name letter)
        if(recordsForLetter == null) {
            recordsForLetter = new ArrayList<>();
            recordsForLetter.add(record);
            records.put(letter, recordsForLetter);
        }
        else {
            recordsForLetter.add(record);
        }
    }
    
    private synchronized int getCenterCount() {
        int count = 0;
        
        for (List<Record> recordsList : records.values()) {
            count += recordsList.size();
        }
        
        return count;
    }
    
    private void clearLogs() {
        OperationLogger.clearLogs(new File("Logs/Servers/"));
    }
    
    private String addLogEntry(String... data) {
        String serverLogPath = "Logs/Servers/" + centerLocation + ".txt";
        String dataToLog = String.join("\n", data) + "\n";
        
        OperationLogger.log(serverLogPath, dataToLog);
        
        return dataToLog;
    }
    
    private String sendUDPRequest(byte[] message, InetAddress host, int port) throws IOException {
        DatagramSocket aSocket = new DatagramSocket();
        
        DatagramPacket request = new DatagramPacket(message, message.length, host, port);
        aSocket.send(request);
    
        byte[] buffer = new byte[1000];
        DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
        aSocket.receive(reply);
    
        aSocket.close();
        
        return new String(reply.getData()).trim();
    }
    
    // Runs UDP Server
    public void run() {
        DatagramSocket aSocket = null;
    
        try {
            System.out.println(centerLocation + " started to listen for UDP requests on port: " + centerServerPorts.get(centerLocation));
            
            aSocket = new DatagramSocket(centerServerPorts.get(centerLocation));
            byte[] buffer = new byte[1000];
        
            while (true) {
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                String requestData;
                byte[] response;
                
                aSocket.receive(request);
                requestData = new String(request.getData()).trim();
                
                if (requestData.contains("getRecordCounts")) {
                    response = Integer.toString(getCenterCount()).getBytes();
                }
                else if (requestData.contains("transferRecord")) {
                    String recordID = requestData.substring(requestData.indexOf(": "));
                    Record record = findRecord(recordID);
                    boolean result = record != null;
                    
                    
                    response = Boolean.toString(result).getBytes();
    
                }
                else if (requestData.contains("Models.Record")) {
                    ObjectInputStream iStream = new ObjectInputStream(new ByteArrayInputStream(request.getData()));
                    Object input = iStream.readObject();
                    Map<String, Object> dataReceived;
                    iStream.close();
                    
                    if (input instanceof Map) {
                        dataReceived = (Map<String, Object>) input;
                    }
                    else {
                        throw new IOException("Data received is not valid.");
                    }
                    
                    String managerID = (String) dataReceived.get("managerID");
                    Record record = (Record) dataReceived.get("record");
    
                    response = createRecord(managerID, record, "transferRecord (IN)").getResponse().getBytes();
                }
                else {
                    response = "Request not supported".getBytes();
                }
                
                DatagramPacket reply = new DatagramPacket(response, response.length, request.getAddress(), request.getPort());
                aSocket.send(reply);
            }
        }
        catch (Exception e) {
            // Error
            String errorMsg = "The UDP Server threw an exception when getting the Records count." + "(" + e.getMessage() + ")";
            LogEntry logEntry = new LogEntry("", "getRecordCounts", false);
    
            addLogEntry(logEntry.toString(), "Error Message: " + errorMsg);
    
            System.out.println(errorMsg);
        }
        finally {
            if (aSocket != null) aSocket.close();
        }
    }
}
