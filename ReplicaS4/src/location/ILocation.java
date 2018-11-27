package location;

public interface ILocation {
	public String createMRecord(String managerID, String firstName, String lastName, int employeeID, String mailID, String project, String location);
	public String createERecord(String managerID, String firstName, String lastName, int employeeID, String mailID, String projectID);
	public String getRecordCounts(String managerID);
	public String editRecord(String managerID, String recordID, String fieldName, String newValue);
	public String transferRecord(String managerID, String recordID, String remoteCenterServerName);
}
