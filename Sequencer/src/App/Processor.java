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
    private ArrayList<Message> history = new ArrayList<Message>();

    public String handleRequestMessage(Message msg) throws Exception {
        System.out.println("hello from seq");
        if (msg.getOpCode() == OperationCode.REPLAY) {
            System.out.println("replay");
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
        System.out.println("forwards");

        // Create a forwarded message with an assigned sequence number.
        Message fwd = new Message(msg.getOpCode(), this.sequence++, msg.getData(), AddressBook.REPLICAS);

        System.out.println("new msg");
        // Store message for future replays.
        this.history.add(fwd);

        // Send forwarded message to the replicas.
        Socket socket = new Socket();

        try {
                System.out.println("sending");
            if (socket.send(msg, 5, 200)) {

                System.out.println("send complete");
            }
        } catch (Exception e) {
            System.out.println("send failed" + e.getMessage());
        }
        
        
        // Sequence number is given back to the frontend.
        System.out.println("returning seq");
        return "SEQ=" + this.sequence;
    }
}
