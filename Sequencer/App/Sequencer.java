package App;

import Models.AddressBook;
import UDP.RequestListener;

public class Sequencer {
    public static void main(String[] args) {
    	Processor p = new Processor();
    	RequestListener ln = new RequestListener(p, AddressBook.SEQUENCER);
    	
    	Thread t = new Thread(ln);
    	t.start();
    }
}
