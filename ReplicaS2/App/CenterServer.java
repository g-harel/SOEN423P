package App;

import Models.*;
import Utility.ICenterServer;
import Utility.LogEntry;
import Utility.OperationLogger;
import Utility.ReplicaResponse;
import backEnd.CenterMap;
import backEnd.InternalServers;
import backEnd.ServerConfigurator;
import model.Record;
import shared.HRActions;
import shared.UDP.ServerUDP;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;

import model.InternProject;
import model.Location;
import Interface.Corba.Project;

public class CenterServer implements ICenterServer, Runnable {
    private Models.Location currentLoca = null;
    public CenterServer(Models.Location location) {
        super();
        currentLoca = location;
        
        
    }
    
    @Override
    public synchronized ReplicaResponse createMRecord(String managerID, String firstName, String lastName, int employeeID, String mailID, Project project, String location) {
    	HRActions tmpCenter = CenterMap.getByLocationName(location.toString());
    	model.InternProject[] localProj  = convertProjectToInternalProject(project);
    	model.Location loc = convertToInternalLocation(location);
    	String convertedEmptId = convertEmployeeIdToString(true, employeeID);
    	String result = tmpCenter.createMRecord(firstName, lastName, convertedEmptId, mailID, localProj, loc, managerID);
    	
    	if(result.contains("created")) {
    		return new ReplicaResponse(true, result);
    	}else {
    		return new ReplicaResponse(false, result);
    	}
        

    }
    
    @Override
    public synchronized ReplicaResponse createERecord(String managerID, String firstName, String lastName, int employeeID, String mailID, String projectID) {
    	
    	HRActions tmpCenter = CenterMap.getByLocationName(currentLoca.toString());
    	String convertedEmptId = convertEmployeeIdToString(false, employeeID);
    	String result = tmpCenter.createERecord(firstName, lastName, convertedEmptId,mailID, projectID, managerID);
    	
    	if(result.contains("created")) {
    		return new ReplicaResponse(true, result);
    	}else {
    		return new ReplicaResponse(false, result);
    	}
    }
    
    @Override
    public synchronized ReplicaResponse editRecord(String managerID, String recordID, String fieldName, String newValue) {
    	HRActions tmpCenter = CenterMap.getByLocationName(currentLoca.toString());
    	String result = tmpCenter.editRecord(recordID, fieldName, newValue, managerID);
    	result = result.toLowerCase();
    	if(result.contains("updated")) {
    		return new ReplicaResponse(true, result);
    	}else {
    		return new ReplicaResponse(false, result);
    	}
    }
    

    
    @Override
    public ReplicaResponse transferRecord(String managerID, String recordID, String remoteCenterServerName) {
    	HRActions tmpCenter = CenterMap.getByLocationName(currentLoca.toString());
    	model.Location loc = convertToInternalLocation(remoteCenterServerName);
    	String result = tmpCenter.transferRecord(managerID, recordID, loc);
    	result = result.toLowerCase();
    	if(result.contains("transfered")) {
    		return new ReplicaResponse(true, result);
    	}else {
    		return new ReplicaResponse(false, result);
    	}
    }
    
    @Override
	public void softwareFailure(String managerID) {
		// TODO Auto-generated method stub
    	HRActions tmpCenter = CenterMap.getByLocationName(currentLoca.toString());
    	tmpCenter.getStore().writeLog("Software Failure Detected...with " + managerID, "Log.txt");
	}


	@Override
	public void replicaCrash(String managerID) {
		// TODO Auto-generated method stub
		for(ServerUDP udp : InternalServers.getServers()) {
			udp.setListen(false);
		}
		//Attempt to exit...
    	HRActions tmpCenter = CenterMap.getByLocationName(currentLoca.toString());
    	tmpCenter.shutdown(managerID);
		System.exit(1);
	}

    
    // Runs UDP Server
    public void run() {
    	//Start server for transfer Record...
    	//Already Started earlier...
    }

	@Override
	public ReplicaResponse getRecordCounts(String managerID) {
    	HRActions tmpCenter = CenterMap.getByLocationName(currentLoca.toString());
    	String result = tmpCenter.getRecordCounts(managerID);
    	result = result.toLowerCase();
    	if(result.contains("ca")) {
    		return new ReplicaResponse(true, result);
    	}else {
    		return new ReplicaResponse(false, result);
    	}
	}
	
	private InternProject[] convertProjectToInternalProject(Project projects) {
		return null;
	}
	
	private model.Location convertToInternalLocation(String location){
		for(model.Location loc: model.Location.values()) {
			if(loc.toString().equals(location)) {
				return loc;
			}
		}
		return null;
	}
	
	private String convertEmployeeIdToString(Boolean isManager, int empId) {
		if(isManager) {
			return "MR"+empId;
		}else {
			return "ER"+empId;
		}
	}
}
