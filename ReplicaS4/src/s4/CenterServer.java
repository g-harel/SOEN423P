package s4;

import Utility.ICenterServer;
import Utility.ReplicaResponse;
import location.AddressBook;
import location.Location;
import location.RecordServer;

public class CenterServer implements ICenterServer, Runnable {
	RecordServer server;
	Location location;
	
	public CenterServer(String code) {
		AddressBook ab = new AddressBook(code);
		this.server = new RecordServer(ab);
		this.location = new Location(this.server);
	}

	@Override
	public void run() {
		this.server.run();
	}

	@Override
	public ReplicaResponse createMRecord(String managerID, String firstName, String lastName, int employeeID,
			String mailID, Project project, String location) {
		this.location.createMRecord(managerID, firstName, lastName, employeeID, mailID, project, location);
		return new ReplicaResponse(true, location);
	}

	@Override
	public ReplicaResponse createERecord(String managerID, String firstName, String lastName, int employeeID,
			String mailID, String projectID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ReplicaResponse getRecordCounts(String managerID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ReplicaResponse editRecord(String managerID, String recordID, String fieldName, String newValue) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ReplicaResponse transferRecord(String managerID, String recordID, String location) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void softwareFailure(String managerID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void replicaCrash(String managerID) {
		System.exit(1);
	}

}