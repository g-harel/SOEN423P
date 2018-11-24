package Manager;

import Models.AddressBook;
import UDP.Message;
import UDP.RequestListener;

public class Manager implements RequestListener.Processor {
	
	//The replica linked to the manager
	private String replicaName;
	final private RequestListener managerListener;
	private Thread managerListenerThread;
	public Manager(String assignedReplica, AddressBook listenForAddress) {
		
		this.replicaName = assignedReplica;
		this.managerListener = new RequestListener(this, listenForAddress);
		
	}

	@Override
	public String handleRequestMessage(Message msg) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void Launch() {
		managerListenerThread = new Thread(managerListener);
		managerListenerThread.start();
		//managerListenerThread.wait();
	}
	
	
	
	public void Shutdown() {
		managerListener.Stop();
		try {
			managerListenerThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Can't stop the thread " + this.managerListenerThread.getName());
		}
	}
	
	

}
