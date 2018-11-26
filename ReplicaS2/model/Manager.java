package model;

import java.util.List;
public class Manager extends Record {

	private String managerID;
	private Location location;
	private List<Project> currentProjects;
	private String firstName;
	private String lastName;
	private String mailID;
	private String employeeID;
	
	public String getEmployeeID() {
		return employeeID;
	}

	public void setEmployeeID(String employeeID) {
		this.employeeID = employeeID;
	}

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

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public List<Project> getCurrentProjects() {
		return currentProjects;
	}

	public void setCurrentProjects(List<Project> currentProjects) {
		this.currentProjects = currentProjects;
	}


	
	public Manager(String firstName, 
			String lastName, 
			String recordId,
			String mailID, 
			List<Project> projects, 
			Location location, 
			String managerId) {
		
		super(recordId);
		this.firstName = firstName;
		this.lastName = lastName;
		this.employeeID = recordId;
		this.mailID = mailID;
		this.setManagerID(managerId);
		this.setRecordID(recordId);
		this.location = location;
		this.currentProjects = projects;
	}

	public String getManagerID() {
		return managerID;
	}

	public void setManagerID(String managerID) {
		this.managerID = managerID;
	}
	
	@Override
	public String toString() {
		return "Record:" + getEmployeeID() + "|" + getManagerID() + "|" + getFirstName() + 
				"|" + getLastName() + "|" + getMailID() 
				+ "|" + getAllProjectId() + "|" + location;
	}

	private String getAllProjectId() {
		StringBuilder allProjects = new StringBuilder();
		for(Project proj : currentProjects) {
			allProjects.append(proj.getProjectID());
			allProjects.append(",");
		}
		return allProjects.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((currentProjects == null) ? 0 : currentProjects.hashCode());
		result = prime * result + ((location == null) ? 0 : location.hashCode());
		result = prime * result + ((managerID == null) ? 0 : managerID.hashCode());
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
		Manager other = (Manager) obj;
		if (currentProjects == null) {
			if (other.currentProjects != null)
				return false;
		} else if (!currentProjects.equals(other.currentProjects))
			return false;
		if (location != other.location)
			return false;
		if (managerID == null) {
			if (other.managerID != null)
				return false;
		} else if (!managerID.equals(other.managerID))
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
