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
import java.net.SocketException;
import java.util.PriorityQueue;
import java.util.concurrent.Semaphore;

public class Processor implements RequestListener.Processor, Runnable {

    boolean running = true;
    Socket socket;
    final PriorityQueue<Message> queue;
    final Semaphore sem;

    Thread sendinfThread;

    private int sequence = 0;
    private ArrayList<Message> history = new ArrayList<>();

    public Processor() throws SocketException {
        this.socket = new Socket();
        queue = new PriorityQueue<>();
        sem = new Semaphore(0);

        sendinfThread = new Thread(this);
        sendinfThread.start();
    }

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
        Message fwd = new Message(msg.getOpCode(), ++this.sequence, msg.getData(), AddressBook.REPLICAS);

        // Store message for future replays.
        this.history.add(fwd);

        synchronized (queue) {
            queue.add(fwd);
        }

        sem.release();

        // Sequence number is given back to the frontend.
        return "SEQ=" + this.sequence;
    }

    @Override
    public void run() {
        System.out.println("``available Semaphore permits now: "  + sem.availablePermits());
        while (running) {
            try {
                sem.acquire();
            } catch (InterruptedException ex) {
                System.out.println(ex);
                break; // oh oh!
            }

            synchronized (queue) {
                try {
                    System.out.println("Sequencer.App.Processor.run() Sendering... " + queue.peek());
                    if (!socket.send(queue.remove(), 5, 750)) {
                        throw new Exception();
                    }
                } catch (Exception ex) {
                    System.out.println("Unable to use socket to send " + ex.getMessage());
                }
            }
        }
    }

    public void Stop() {
        running = false;
    }
}
