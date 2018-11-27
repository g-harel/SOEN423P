package tests;


import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;
import model.Location;
import model.InternProject;
import shared.HRActions;
import shared.IHRActions;
import storage.IStore;
import storage.Logger;

public class TestBackEnd {
	
	private String MAIN_TREE_FOLDER = 
			"/home/winterhart/DEV/SOEN423/java-rmi-simulation/storage/";
	
	@Test
	public void CreateManagerTest() {
		IStore storingEngine = new Logger("CA", MAIN_TREE_FOLDER + "CA" + "/");
		IHRActions actions = new HRActions(storingEngine);
		InternProject p = new InternProject("P00222", "TestClient", "TestProjName");
		InternProject p2 = new InternProject("P00221", "TestClient2", "TestProjName2");
		Location loc = Location.CA;
		InternProject[] projects = {p, p2};
		String message = actions.createMRecord("SuperTest", "BBo", "MR10342"
				, "goade@gma.com", projects, loc , "CA3841");
		System.out.println(message);
		
		
	}
	
	@Test
	public void dynamicPathTest() {
		Path currentRelativePath = Paths.get("");
		String s = currentRelativePath.toAbsolutePath().toString();
		System.out.println("Current relative path is: " + s);
	}
	


}
