package Manager;

import Models.AddressBook;
import Models.RegisteredReplica;
import UDP.Message;
import UDP.RequestListener;
import model.ManagersMap;
/**
 * This class is receiving data and redirect to the right manager
 * @author winterhart
 *
 */
public class ManagerHub  implements RequestListener.Processor {
	
	final private RequestListener managerListener;
	private Thread managerListenerThread;
	private ManagerMessageHandler handler;
	private static final AddressBook defaultAddressReceiving = AddressBook.MANAGER;
	public ManagerHub() {
		this.managerListener = new RequestListener(this,defaultAddressReceiving  );
		handler = new ManagerMessageHandler();
		
	}
	
	/**
	 *  Operation Handled:
	 *  NO_RESP_NOTIFICATION(1201),
	 *  FAULY_RESP_NOTIFICATION(1202),
	 */
		@Override
		public String handleRequestMessage(Message msg) throws Exception {
				return handler.HandleRequest(msg);
		}
		
		public void launch() {
			
			//TODO: Should we restore saved state Manager 
			// Add all Manager to the ManagersMap
			for(RegisteredReplica replicaName : RegisteredReplica.values()) {
				Manager manager = new Manager(replicaName);
				ManagersMap.addManager(replicaName, manager);
			}
			managerListenerThread = new Thread(managerListener);
			managerListenerThread.start();
			//managerListenerThread.wait();
		}
		
		public void shutdown() {
			//TODO: Save state of the Manager in ManagersMap before quit ???
			
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
