package s4;

import Interface.Corba.Project;
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
		return new ReplicaResponse(true, this.location.createMRecord(managerID, firstName, lastName, employeeID, mailID, project.toString(), location));
	}

	@Override
	public ReplicaResponse createERecord(String managerID, String firstName, String lastName, int employeeID,
			String mailID, String projectID) {
		return new ReplicaResponse(true, this.location.createERecord(managerID, firstName, lastName, employeeID, mailID, projectID));
	}

	@Override
	public ReplicaResponse getRecordCounts(String managerID) {
		return new ReplicaResponse(true, this.location.getRecordCounts(managerID));
	}

	@Override
	public ReplicaResponse editRecord(String managerID, String recordID, String fieldName, String newValue) {
		return new ReplicaResponse(true, this.location.editRecord(managerID, recordID, fieldName, newValue));
	}

	@Override
	public ReplicaResponse transferRecord(String managerID, String recordID, String location) {
		return new ReplicaResponse(true, this.location.transferRecord(managerID, recordID, location));
	}

	@Override
	public void softwareFailure(String managerID) {
		this.server.empty();
	}

	@Override
	public void replicaCrash(String managerID) {
		System.exit(1);
	}

}