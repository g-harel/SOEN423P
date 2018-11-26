package backEnd;

public class ServerLauncher {

	
	public static void main(String[] args) {
			ServerConfigurator config = new ServerConfigurator();
			config.configureCenter(args);
	}
}
