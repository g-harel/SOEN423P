package App;

import Models.*;
import Utility.ICenterServer;
import Utility.LogEntry;
import Utility.OperationLogger;
import Utility.ReplicaResponse;
import backEnd.ServerConfigurator;
import model.Record;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;

import Interface.Corba.Project;

public class CenterServer implements ICenterServer, Runnable {
    private HashMap<Character, List<Record>> records = new HashMap<>();
    private HashMap<Location, Integer> centerServerPorts = new HashMap<>();
    private Location centerLocation;
    
    public CenterServer(Location location) {
        super();
        
        centerServerPorts.put(Location.CA, 9000);
        centerServerPorts.put(Location.US, 9001);
        centerServerPorts.put(Location.UK, 9002);
        
        this.centerLocation = location;
        
    }
    
    @Override
    public synchronized ReplicaResponse createMRecord(String managerID, String firstName, String lastName, int employeeID, String mailID, Project project, String location) {
		return null;
        

    }
    
    @Override
    public synchronized ReplicaResponse createERecord(String managerID, String firstName, String lastName, int employeeID, String mailID, String projectID) {
    	return null;
    }
    
    private synchronized ReplicaResponse createRecord(String managerID, Record newRecord, String operationPerformed) {
    	return null;
    }
    
    @Override
    public synchronized ReplicaResponse editRecord(String managerID, String recordID, String fieldName, String newValue) {
    	return null;
    }
    

    
    @Override
    public ReplicaResponse transferRecord(String managerID, String recordID, String remoteCenterServerName) {
       return null;
    }
    
    @Override
	public void softwareFailure(String managerID) {
		// TODO Auto-generated method stub
	}


	@Override
	public void replicaCrash(String managerID) {
		// TODO Auto-generated method stub
	}

    
    // Runs UDP Server
    public void run() {
    	//Start server for transfert Record...
		ServerConfigurator config = new ServerConfigurator();
		config.configureCenter();
    

    }

	@Override
	public ReplicaResponse getRecordCounts(String managerID) {
		// TODO Auto-generated method stub
		return null;
	}
}
