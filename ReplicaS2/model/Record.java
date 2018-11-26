package model;

public class Record {
	
	private String recordID;
	
	public Record(String recordId) {
		this.recordID = recordId;
	}
	public String getRecordID() {
		return recordID;
	}

	public void setRecordID(String recordId) {

		this.recordID = recordId;
	}
	
	@Override
	public String toString() {
		return "Record:" + recordID + "|";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((recordID == null) ? 0 : recordID.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Record other = (Record) obj;
		if (recordID == null) {
			if (other.recordID != null)
				return false;
		} else if (!recordID.equals(other.recordID))
			return false;
		return true;
	}
	
	public int getRecordIndex() {
		return 66;
	}
	

	
	
	

}
