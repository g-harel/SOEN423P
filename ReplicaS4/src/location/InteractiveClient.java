package location;

public class InteractiveClient {
	public static boolean start(ILocation loc, String managerID) {
		String command = Prompt.forValue("command (ls, er, mr, ed, mv, q)");

		if (command.equals("ls")) {
			Logger.log("record counts: %s", loc.getRecordCounts(managerID));
			return true;
		}

		if (command.equals("er")) {
			String firstName = InteractiveClient.firstName();
			String lastName = InteractiveClient.lastName();
			int employeeID = InteractiveClient.employeeID();
			String mailID = InteractiveClient.mailID();
			String projectID = InteractiveClient.projectID();

			Logger.log("sending employee creation request for \"%s %s\"", firstName, lastName);
			Logger.log(loc.createERecord(managerID, firstName, lastName, employeeID, mailID, projectID));
			return true;
		}

		if (command.equals("mr")) {
			String firstName = InteractiveClient.firstName();
			String lastName = InteractiveClient.lastName();
			int employeeID = InteractiveClient.employeeID();
			String mailID = InteractiveClient.mailID();
			Project project = new Project();
			project.id = InteractiveClient.projectID();
			project.client = InteractiveClient.clientName();
			project.name = InteractiveClient.projectName();
			String locationCode = InteractiveClient.locationCode();

			Logger.log("sending manager creation request for \"%s %s\"", firstName, lastName);
			Logger.log(loc.createMRecord(managerID, firstName, lastName, employeeID, mailID, project.toString(), locationCode));
			return true;
		}

		if (command.equals("ed")) {
			String recordID = InteractiveClient.recordID();
			String fieldName = InteractiveClient.fieldName();
			String newValue = Prompt.forValue("new value");

			Logger.log("sending record edit request for record with ID '%s'", recordID);
			Logger.log(loc.editRecord(managerID, recordID, fieldName, newValue));
			return true;
		}

		if (command.equals("mv")) {
			String recordID = InteractiveClient.recordID();
			String locationCode = InteractiveClient.locationCode();

			Logger.log("sending record transfer request for record with ID '%s' to '%s'", recordID, locationCode);
			Logger.log(loc.transferRecord(managerID, recordID, locationCode));
			return true;
		}

		if (command.equals("q")) {
			Logger.log("exiting");
			return false;
		}

		Logger.err("invalid command '%s'", command);
		return true;
	}

	private static String firstName() {
		String val;
		while (true) {
			val = Prompt.forValue("first name");
			if (Validator.isFirstName(val)) break;
			Logger.err("invalid first name '%s'", val);
		}
		return val;
	}

	private static String lastName() {
		String val;
		while (true) {
			val = Prompt.forValue("last name");
			if (Validator.isLastName(val)) break;
			Logger.err("invalid last name '%s'", val);
		}
		return val;
	}

	private static int employeeID() {
		int val;
		while (true) {
			String raw = Prompt.forValue("employee ID");
			try {
				val = Integer.parseInt(raw);
				if (!Validator.isEmployeeID(val)) {
					throw new Exception();
				}
				break;
			} catch (Exception e) {
				Logger.err("invalid employee ID '%s'", raw);
			}
		}
		return val;
	}

	private static String mailID() {
		String val;
		while (true) {
			val = Prompt.forValue("mail ID");
			if (Validator.isEmail(val)) break;
			Logger.err("invalid mail ID '%s'", val);
		}
		return val;
	}

	private static String projectID() {
		String val;
		while (true) {
			val = Prompt.forValue("project ID");
			if (Validator.isProjectID(val)) break;
			Logger.err("invalid project ID '%s'", val);
		}
		return val;
	}

	private static String clientName() {
		String val;
		while (true) {
			val = Prompt.forValue("client name");
			if (Validator.isClientName(val)) break;
			Logger.err("invalid client name '%s'", val);
		}
		return val;
	}

	private static String projectName() {
		String val;
		while (true) {
			val = Prompt.forValue("project name");
			if (Validator.isProjectName(val)) break;
			Logger.err("invalid project name '%s'", val);
		}
		return val;
	}

	private static String locationCode() {
		String val;
		while (true) {
			val = Prompt.forValue("location");
			if (Validator.isLocationCode(val)) break;
			Logger.err("invalid location '%s'", val);
		}
		return val;
	}

	private static String recordID() {
		String val;
		while (true) {
			val = Prompt.forValue("record ID");
			if (Validator.isRecordID(val)) break;
			Logger.err("invalid record ID '%s'", val);
		}
		return val;
	}

	private static String fieldName() {
		String val;
		while (true) {
			val = Prompt.forValue("field name");
			if (Validator.isFieldName(val)) break;
			Logger.err("invalid field name '%s'", val);
		}
		return val;
	}
}
