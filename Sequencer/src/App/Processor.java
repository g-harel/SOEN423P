package App;

import java.util.ArrayList;

import UDP.Message;
import UDP.OperationCode;
import UDP.RequestListener;

public class Processor implements RequestListener.Processor {
	private int sequence = 0;
	private ArrayList<Message> history = new ArrayList<Message>();

	public synchronized String handleRequestMessage(Message msg) throws Exception {
		this.sequence++;
		msg.setSeqNum(this.sequence);
		this.history.add(msg);

		OperationCode op = msg.getOpCode();

		if (op == OperationCode.DUMP) {
			// TODO dump history into single replica
			return "Hello World!";
		}

		// TODO multicast to all replicas

		return "Hello World!";
	}
}
