/* 
    MIT License

    Copyright (c) 2018 Chris Mc, prince.chrismc(at)gmail(dot)com

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.
 */
package UDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * @author cmcarthur
 */
public class RequestListener implements Runnable {

    public interface Processor {

        public void copyMessage(Message msg);
    }
    
    public static int TEST_PORT = 35646;

    final private Processor m_Handler;

    private boolean m_ShouldContinueWorking;
    private boolean m_ProcessingHasBegun;

    private DatagramSocket socket;

    public RequestListener(Processor handler) {
        m_Handler = handler;
        m_ShouldContinueWorking = false;
        m_ProcessingHasBegun = false;
    }

    public void Stop() {
        m_ShouldContinueWorking = false;
        m_ProcessingHasBegun = false;
        socket.close();
    }

    public void Wait() {
        while (m_ProcessingHasBegun == false) {
            try {
                Thread.sleep(50); // No need to poll super hard...
                //System.out.println("Waiting for Processing to be available");
            } catch (InterruptedException ex) {
                Wait();
            }
        }
    }

    @Override
    public void run() {
        try {
            socket = new DatagramSocket(TEST_PORT);
            m_ShouldContinueWorking = true;
        } catch (SocketException ex) {
            m_ShouldContinueWorking = false;
            System.out.println("Failed to create socket due to: " + ex.getMessage());
        }

        if (m_ShouldContinueWorking) {
            System.out.println("Ready...");
            m_ProcessingHasBegun = true;
        }

        while (m_ShouldContinueWorking) {
            byte[] buf = new byte[256];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            try {
                socket.receive(packet);
            } catch (IOException ex) {
                System.out.println("Failed to receive message due to: " + ex.getMessage());
                return;
            }

            System.out.println("Processing new request...");
            Message request = new Message(packet);

            String responsePayload = "ERROR";
            
            int randomNum = ThreadLocalRandom.current().nextInt(0, 3) ;
            OperationCode responseCode = ( randomNum == 0 ) ? request.getOpCode().toAck() : OperationCode.INVALID;

            m_Handler.copyMessage(request);

            InetAddress address = packet.getAddress();
            int port = packet.getPort();
            Message response = new Message(responseCode, 0, responsePayload, address, port);

            try {
                socket.send(response.getPacket());
            } catch (IOException ex) {
                System.out.println("Failed to send message");
                System.out.println(ex);
            }
        }

        socket.close();
    }
}
