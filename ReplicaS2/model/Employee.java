package model;


public class Employee extends Record {
	private String firstName;
	private String lastName;
	private String mailID;
	private String projectID;
	private String employeeID;
	
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getMailID() {
		return mailID;
	}

	public void setMailID(String mailID) {
		this.mailID = mailID;
	}

	public String getProjectID() {
		return projectID;
	}

	public void setProjectID(String projectID) {
		this.projectID = projectID;
	}

	public String getEmployeeID() {
		return employeeID;
	}

	public void setEmployeeID(String employeeID) {
		this.employeeID = employeeID;
	}

	public Employee(String firstName, String lastName, String employeeId, String mailID, String projectID) {
		super(employeeId);
		this.employeeID = employeeId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.mailID = mailID;
		this.projectID = projectID;
		this.setRecordID(employeeId);
	}
	
	@Override
	public String toString() {
		return "Record:" + employeeID + "|" + firstName + "|"
				+ lastName + "|" + mailID + "|" + projectID; 
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((employeeID == null) ? 0 : employeeID.hashCode());
		result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
		result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
		result = prime * result + ((mailID == null) ? 0 : mailID.hashCode());
		result = prime * result + ((projectID == null) ? 0 : projectID.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Employee other = (Employee) obj;
		if (employeeID == null) {
			if (other.employeeID != null)
				return false;
		} else if (!employeeID.equals(other.employeeID))
			return false;
		if (firstName == null) {
			if (other.firstName != null)
				return false;
		} else if (!firstName.equals(other.firstName))
			return false;
		if (lastName == null) {
			if (other.lastName != null)
				return false;
		} else if (!lastName.equals(other.lastName))
			return false;
		if (mailID == null) {
			if (other.mailID != null)
				return false;
		} else if (!mailID.equals(other.mailID))
			return false;
		if (projectID == null) {
			if (other.projectID != null)
				return false;
		} else if (!projectID.equals(other.projectID))
			return false;
		return true;
	}
	
	@Override
	public int getRecordIndex() {
		String lastNa = this.getLastName().toLowerCase();
		char firstLetter = lastNa.charAt(0);
		int index = 0;
		for(char alpha = 'a'; alpha <= 'z'; alpha++) {
			if(alpha == firstLetter) {
				return index;
			}
			
			index++;
		}
		return index;
	}
	
	@Override
	public String getRecordID() {
		return this.getEmployeeID();
	}
	

}
