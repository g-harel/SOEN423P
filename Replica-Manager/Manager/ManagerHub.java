package Manager;

import Models.AddressBook;
import UDP.Message;
import UDP.RequestListener;
/**
 * This class is receiving data and redirect to the right manager
 * @author winterhart
 *
 */
public class ManagerHub  implements RequestListener.Processor {
	
	final private RequestListener managerListener;
	private Thread managerListenerThread;
	public ManagerHub(AddressBook listenForAddress) {
		this.managerListener = new RequestListener(this, listenForAddress);
	}
	
	/**
	 *  Operation Handled:
	 *  NO_RESP_NOTIFICATION(1201),
	 *  ACK_NO_RESP_NOTIFICATION(3201),
	 *  FAULY_RESP_NOTIFICATION(1202),
	 *  ACK_FAULY_RESP_NOTIFICATION(3202);
	 */
		@Override
		public String handleRequestMessage(Message msg) throws Exception {
			switch(msg.getOpCode()) {
			case NO_RESP_NOTIFICATION:
				System.out.println("No Resp Notif");
				msg.get
				break;
			case ACK_NO_RESP_NOTIFICATION:
				System.out.println("Ack Resp");
				break;
			case FAULY_RESP_NOTIFICATION:
				System.out.println("FAULY RESP Notification");
				break;
			case ACK_FAULY_RESP_NOTIFICATION:
				System.out.println("Acknowledge Fauly Resp. Notif");
				break;
			default:
				System.out.println("Operation not recognized");
				break;
			}
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
