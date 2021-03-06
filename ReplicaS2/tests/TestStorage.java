package tests;

import org.junit.Test;
import java.util.ArrayList;
import java.util.List;
import model.Employee;
import model.Manager;
import model.InternProject;
import model.Record;
import storage.IStore;
import storage.Logger;
import model.Location;

public class TestStorage {

	@Test
	public void test() {
		String MAIN_TREE_FOLDER = 
				"/home/winterhart/DEV/SOEN423/java-rmi-simulation/storage/";
		IStore storingEngine = new Logger("CA", MAIN_TREE_FOLDER + "CA" + "/");
		
		
		Employee emp = new Employee("Vivian", 
				"Bobinson", "ER45453", "Vivi@vlv.com", "P22221");
		
		InternProject proj = new InternProject("P22221", "SuperClient", "AnotherBlockChain");
		InternProject proj2 = new InternProject("P23441", "SuperClient2", "AnotherBlockChain2");
		List<InternProject> projList = new ArrayList<InternProject>();
		
		projList.add(proj);
		projList.add(proj2);
		
		Manager mana = new Manager("Micheal", "Scott", "MR33232", 
				"DataMike@dunderM.com", projList, Location.CA, "CA3333");
		
		storingEngine.addProject(proj);
		storingEngine.addProject(proj2);
		
		List<InternProject> projFromStorage = storingEngine.restoreProject();
		if(!projFromStorage.contains(proj)) {
			//fail("No able to restore project");
		}
		
		storingEngine.addRecord(mana);
		storingEngine.addRecord(emp);
		
		List<Record> recordFromStorage = storingEngine.restoreRecord();
		
		if(!recordFromStorage.contains(mana)) {
			//fail("No able to restore form storage");
		}
		
		

	}

}
