package Manager;

import java.net.SocketException;
import java.util.Stack;

import Models.AddressBook;
import UDP.Message;
import UDP.OperationCode;
import UDP.Socket;
import model.RegisteredReplica;

public class Manager {
	
	//The replica linked to the manager
	private RegisteredReplica associatedReplica;
	private static final int nonByzantineFailureTolerance = 3;
	private Stack<Integer> nonByzantineFailStack;

	public Manager(RegisteredReplica associatedReplica) {
		this.associatedReplica = associatedReplica;
		
		// Create a place to store non-byz. failure

		this.nonByzantineFailStack = new Stack<Integer>();
		nonByzantineFailStack.setSize(nonByzantineFailureTolerance);
	
	}
	
	public  String getAssociatedReplicaName() {
		return this.associatedReplica.toString();
	}
	/**
	 * Should return message with FAULY_RESP_NOTIFICATION
	 * @param seqId
	 */
	public synchronized String registerNonByzFailure(int seqId) {
		try {
			//TODO: Add logging here
			nonByzantineFailStack.push(seqId);
			return "Failure registrered";
		}catch(StackOverflowError stackOver) {
			// It means the stack is full we should restart the Replica
			nonByzantineFailStack.empty();
			return restartReplica(seqId);
			
		}catch(Exception ee) {
			System.out.println("Error while registerNonByzFailure " + ee.getMessage());
		}
		return null;
	}
	/**
	 * Should return message with ACK_NO_RESP_NOTIFICATION
	 * @param seqId
	 */
	public synchronized String registerCrashFailure(int seqId) {
		try {
			//TODO: Add logging here
			return restoreReplicaBack(seqId);
		}catch(Exception ee) {
			System.out.println("Error while registerCrashFailure " + ee.getMessage());
			return ee.getMessage();
		}
	}
	/**
	 * Will Call the right replica to restore from a log file
	 */
	public String restoreReplicaBack(int seqId) {
		//TODO: We need clarify which data to send and the format
		try {
			Socket instance = new Socket();
			Message sendMessa = new Message(
					OperationCode.RESTORE_ORDER_NOTIFICATION,
					seqId,
					"targetReplica: " + this.getAssociatedReplicaName() + "order: restore from log" + ", log: blabla",
					AddressBook.REPLICAS);
			
			if(!instance.send(sendMessa, 10, 1000)) {
				throw new Exception("Failed to send Restore Order to Replica");
			}else {
				return "Restore order sent";
			}
			
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return e.getMessage();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return e.getMessage();
		}
	}
	
	/**
	 * Will simply restart replica attempts to repair it...
	 */
	public String restartReplica(int seqId) {
		//TODO: We need clarify which data to send
		try {
			Socket instance = new Socket();
			Message sendMessa = new Message(
					OperationCode.RESTORE_ORDER_NOTIFICATION,
					seqId,
					"targetReplica: " + this.getAssociatedReplicaName() + "order: restart replica",
					AddressBook.REPLICAS);
			
			if(!instance.send(sendMessa, 10, 1000)) {
				throw new Exception("Failed to send Restore Order to Replica");
			}else {
				return "Restart order sent";
			}
			
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return e.getMessage();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return e.getMessage();
		}
	
	}

	
	

}
