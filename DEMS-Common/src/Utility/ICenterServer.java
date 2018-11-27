package Utility;

import Interface.Corba.Project;

public interface ICenterServer {
	ReplicaResponse createMRecord (String managerID, String firstName, String lastName, int employeeID, String mailID, Project project, String location);
	
	ReplicaResponse createERecord (String managerID, String firstName, String lastName, int employeeID, String mailID, String projectID);
	
	ReplicaResponse getRecordCounts (String managerID);
	
	ReplicaResponse editRecord (String managerID, String recordID, String fieldName, String newValue);
	
	ReplicaResponse transferRecord (String managerID, String recordID, String location);
	
	void softwareFailure (String managerID);
	
	void replicaCrash (String managerID);
}
