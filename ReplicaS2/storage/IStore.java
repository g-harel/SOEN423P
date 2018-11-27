package storage;

import java.util.List;

import model.InternProject;
import model.Record;

public interface IStore {
	
	String storeName = null;

	public void writeLog(String message, String fileName);
	
	public String getStorageName();
	
	public void addRecord(Record record);
	
	public void addProject(InternProject project);
	
	public String readAllProject();
	
	public String readAllRecord();

	public void removeRecord(Record mrecord);

	public void removeProject(InternProject proj);
	
	public List<InternProject> restoreProject();
	
	public List<Record> restoreRecord();
	
	public Record restoreRecordFromLine(String lineData);
	
	
}
