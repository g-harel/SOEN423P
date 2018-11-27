package App;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import Models.AddressBook;
import Models.RegisteredReplica;
import UDP.Message;
import UDP.OperationCode;
import UDP.RequestListener;
import UDP.Socket;

public class Processor implements RequestListener.Processor {

    private int sequence = 0;
    private ArrayList<Message> history = new ArrayList<>();

    @Override
    public String handleRequestMessage(Message msg) throws Exception {
        if (msg.getOpCode() == OperationCode.REPLAY) {
            // Extract target replica from request.
            RegisteredReplica destination = RegisteredReplica.valueOf(msg.getData());

            // Replay all messages in history.
            try {
                Socket socket = new Socket();
                for (Message m : this.history) {
                    // Only the target replica will receive replayed messages.
                    m.setLocation(destination);
                    socket.send(m, 2, 100);
                }
            } catch (Exception e) {
                // Respond with error status and error stack trace.
                StringWriter stack = new StringWriter();
                e.printStackTrace(new PrintWriter(stack));
                return "ERROR\n" + stack.toString();
            }

            return "SUCCESS";
        }

        // Create a forwarded message with an assigned sequence number.
        Message fwd = new Message(msg.getOpCode(), 15634, msg.getData(), AddressBook.REPLICAS);

        // Store message for future replays.
        this.history.add(fwd);

        // Send forwarded message to the replicas.
        Socket socket = new Socket();
        if( ! socket.send(msg, 5, 750)){
            return "ERROR";
        }

        // Sequence number is given back to the frontend.
        return "SEQ=" + this.sequence;
    }
}
