package config;

import java.nio.file.Path;
import java.nio.file.Paths;

public class StorageConfig {
	
	public static final String MAIN_TREE_FOLDER = 
			getCurrentPath() + "/db/";
	
	public static final String CENTRAL_REPO_LOCATION =
			getCurrentPath() + "/db/CENTRAL/";
	public static final String CENTRAL_REPO_CLIENT =
			getCurrentPath() + "/db/CLIENT/";
	private StorageConfig() {};
	
	private static String getCurrentPath() {
		Path currentRelativePath = Paths.get("");
		String s = currentRelativePath.toAbsolutePath().toString();
		System.out.println("Current relative path is: " + s);
		return s;
	}
}
