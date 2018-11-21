package adapter;


public interface ISharedHRActions {
	
	/**
	 * 
	 * When a manager invokes this method from his/her center through a client program
	 *	called ManagerClient, the server associated with this manager
	 *	attempts to create a ManagerRecord with the information
	 *	passed
	 * 
	 * @param firstName
	 * @param lastName
	 * @param employeeId: MR12345 must contain 'MR'
	 * @param mailId
	 * @param projects: Projects are sent in this format:  ["P22221|ClientName|ProjectName"]
	 * @param location: Simply String
	 * @param authorOfRequest : Could use Employee Id of the manager creating a manager...
	 * @return The server returns information to the manager whether the operation
	 *	was successful or not and both the server
	 */
	 public boolean createMRecord (String firstName, String lastName, String employeeID, 
			 String mailID, String[] projects, String location, String authorOfRequest);
		/**
		 * When a manager invokes this method from a ManagerClient, the server associated with
		 *	this manager
		 * @param firstName
		 * @param lastName
		 * @param employeeIDString createMRecord(String firstName, String lastName, 
			String employeeID, String mailID, String managerID,  String location)
		 * @param mailId
		 * @param ProjectId
		 * @return The server returns information to the manager whether the operation was successful or not
		 */
	 public boolean createERecord (String firstName, String lastName, String employeeID, String mailID, String projectID, String managerID);
		/**
		 * Finds out the number of records (both MR and ER)
		 * 
		 * @return it should return the following: "CA: 6, US: 7, UK: 8"
		 */
	 public String getRecordCounts (String managerID);
		
		/**
		 * searches in the hash map to find the recordID and change the
		 *	value of the field identified by “field name” to the newValue, if it is found.
		 * 
		 * This is how I did it for update...
		 * 
		 * @param recordID
		 * @param fieldName
		 * @param value
		 * @return Upon success
			or failure, it returns a message to the manager and the logs are updated
		 */
	 public boolean editRecord (String recordID, String fieldName, String newValue, String managerID);
	 /**
	  * When a HR manager invokes this method from his/her center, the server associated
	  * with this manager (determined by the managerID prefix) searches its hash map to find
	  *	if the record with recordID exists. If it exists, then it checks with the
	  *	remoteCenterServer if a record with recordID does not exist in that
	  *	remoteCenterServer. If the record does not exist in the remoteCenterServer, then the
	  *	entire record is transferred to the remoteCenterServer.
	  *
	  *@return true if transfered
	  */
	 public boolean transferRecord (String managerID, String recordID, 
			 String location);
	 /**
	  * Shut down the entire system...
	  */
	 public void shutdown (String managerID);
}
