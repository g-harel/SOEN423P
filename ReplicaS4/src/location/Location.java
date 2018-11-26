package location;

public class Location implements ILocation {
	private RecordServer rs;

	public Location(RecordServer rs) {
		this.rs = rs;
	}

	public String createMRecord(String managerID, String firstName, String lastName, int employeeID, String mailID, String project, String location) {
		if (!Validator.isFirstName(firstName)) {
			return Logger.err("[%s] invalid manager first name '%s'", managerID, firstName);
		}
		if (!Validator.isLastName(lastName)) {
			return Logger.err("[%s] invalid manager last name '%s'", managerID, lastName);
		}
		if (!Validator.isEmployeeID(employeeID)) {
			return Logger.err("[%s] invalid manager employee ID \"%d\"", managerID, employeeID);
		}
		if (!Validator.isEmail(mailID)) {
			return Logger.err("[%s] invalid manager mail ID '%s'", managerID, mailID);
		}
		if (!Validator.isLocationCode(location)) {
			return Logger.err("[%s] invalid manager location code '%s'", managerID, location);
		}

		Project p = new Project(project);
		if (!Validator.isProjectID(p.id)) {
			return Logger.err("[%s] invalid manager project ID '%s'", managerID, p.id);
		}
		if (!Validator.isClientName(p.client)) {
			return Logger.err("[%s] invalid manager client name '%s'", managerID, p.client);
		}
		if (!Validator.isProjectName(p.name)) {
			return Logger.err("[%s] invalid manager project name '%s'", managerID, p.name);
		}

		ManagerRecord record = new ManagerRecord();
		record.firstName = firstName;
		record.lastName = lastName;
		record.employeeID = employeeID;
		record.mailID = mailID;
		record.projectInfo = p;
		record.location = location;

		this.rs.add(record);

		return Logger.log("[%s] manager record with id '%s' created for \"%s %s\"", managerID, record.recordID, firstName, lastName);
	}

	public String createERecord(String managerID, String firstName, String lastName, int employeeID, String mailID, String projectID) {
		if (!Validator.isFirstName(firstName)) {
			return Logger.err("[%s] invalid employee first name '%s'", managerID, firstName);
		}
		if (!Validator.isLastName(lastName)) {
			return Logger.err("[%s] invalid employee last name '%s'", managerID, lastName);
		}
		if (!Validator.isEmployeeID(employeeID)) {
			return Logger.err("[%s] invalid employee employee ID \"%d\"", managerID, employeeID);
		}
		if (!Validator.isEmail(mailID)) {
			return Logger.err("[%s] invalid employee mail ID '%s'", managerID, mailID);
		}
		if (!Validator.isProjectID(projectID)) {
			return Logger.err("[%s] invalid employee project ID '%s'", managerID, projectID);
		}

		EmployeeRecord record = new EmployeeRecord();
		record.firstName = firstName;
		record.lastName = lastName;
		record.employeeID = employeeID;
		record.mailID = mailID;
		record.projectID = projectID;

		this.rs.add(record);

		return Logger.log("[%s] employee record with id '%s' created for \"%s %s\"", managerID, record.recordID, firstName, lastName);
	}

	public String getRecordCounts(String managerID) {
		Logger.log("[%s] counting records in all peers", managerID);
		return this.rs.sendListAll();
	}

	public String editRecord(String managerID, String recordID, String fieldName, String newValue) {
		Logger.log("[%s] editing field '%s' on record with ID '%s'", managerID, fieldName, recordID);

		Record record = this.rs.read(recordID);
		if (record == null) {
			return Logger.err("[%s] no record with id '%s' found", managerID, recordID);
		}

		if (fieldName.equals("mailID")) {
			if (!Validator.isEmail(newValue)) {
				return Logger.err("[%s] invalid mail ID", managerID);
			}
			record.mailID = newValue;
			return Logger.log("[%s] updated mail ID", managerID);
		}

		if (Validator.isEmployeeRecordID(recordID)) {
			if (fieldName.equals("projectID")) {
				if (!Validator.isProjectID(newValue)) {
					return Logger.err("[%s] invalid project ID", managerID);
				}
				((EmployeeRecord)record).projectID = newValue;
				return Logger.log("[%s] updated projectID", managerID);
			}
			return Logger.err("no editable field '%s' on employee record", fieldName);
		}

		if (Validator.isManagerRecordID(recordID)) {
			if (fieldName.equals("location")) {
				if (!Validator.isLocationCode(newValue)) {
					return Logger.err("[%s] invalid location", managerID);
				}
				((ManagerRecord)record).location = newValue;
				return Logger.log("[%s] updated location");
			}
			if (fieldName.equals("project.client")) {
				if (!Validator.isClientName(newValue)) {
					return Logger.err("[%s] invalid client name", managerID);
				}
				((ManagerRecord)record).projectInfo.client = newValue;
				return Logger.log("[%s] updated project client's name", managerID);
			}
			if (fieldName.equals("project.name")) {
				if (!Validator.isFirstName(newValue)) {
					return Logger.err("[%s] invalid client name", managerID);
				}
				((ManagerRecord)record).projectInfo.name = newValue;
				return Logger.log("[%s] updated project name", managerID);
			}
		}
		return Logger.err("no editable field '%s' on manager record", fieldName);
	}

	public String transferRecord(String managerID, String recordID, String remoteCenterServerName) {
		Logger.log("[%s] transferring record with id '%s' to '%s'", managerID, recordID, remoteCenterServerName);

		if (!Validator.isLocationCode(remoteCenterServerName)) {
			return Logger.err("[%s] invalid destination '%s'", managerID, remoteCenterServerName);
		}
		if (!Validator.isRecordID(recordID)) {
			return Logger.err("[%s] invalid record ID '%s'", managerID, recordID);
		}

		boolean success = this.rs.transferRecord(remoteCenterServerName, recordID);
		if (success) {
			return Logger.log("[%s] transfered record with id '%s' to '%s'", managerID, recordID, remoteCenterServerName);
		} else {
			return Logger.log("[%s] transfer to '%s' failed for record with id '%s'", managerID, remoteCenterServerName, recordID);
		}
	}
}
