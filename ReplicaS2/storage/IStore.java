package storage;

import java.util.List;

import model.Project;
import model.Record;

public interface IStore {
	
	String storeName = null;

	public void writeLog(String message, String fileName);
	
	public String getStorageName();
	
	public void addRecord(Record record);
	
	public void addProject(Project project);
	
	public String readAllProject();
	
	public String readAllRecord();

	public void removeRecord(Record mrecord);

	public void removeProject(Project proj);
	
	public List<Project> restoreProject();
	
	public List<Record> restoreRecord();
	
	public Record restoreRecordFromLine(String lineData);
	
	
}
