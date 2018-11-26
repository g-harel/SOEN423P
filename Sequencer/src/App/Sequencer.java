package App;

import Models.AddressBook;
import UDP.RequestListener;

public class Sequencer {
    public static void main(String[] args) {
    	// Start request listener using local processor in own thread.s
		RequestListener ln = new RequestListener(new Processor(), AddressBook.SEQUENCER);
    	Thread t = new Thread(ln);
    	t.start();
    	ln.Wait();
    }
}
