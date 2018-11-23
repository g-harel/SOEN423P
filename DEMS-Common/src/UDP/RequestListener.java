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
import java.net.UnknownHostException;

/**
 *
 * @author cmcarthur
 */
public class RequestListener implements Runnable {

    public interface Processor {

        /*
        @msg the new incomming request
        @return the payload/data to respond
         */
        public String handleRequestMessage(Message msg);
    }

    private int m_Port;
    private String m_Address;

    final private Processor m_Handler;

    private boolean m_ShouldContinueWorking;
    private boolean m_ProcessingHasBegun;

    private DatagramSocket m_Socket;

    public RequestListener(Processor handler, String address, int port) {
        m_Handler = handler;
        m_Address = address;
        m_Port = port;
        m_ShouldContinueWorking = false;
        m_ProcessingHasBegun = false;
    }

    public void Stop() {
        m_ShouldContinueWorking = false;
        m_ProcessingHasBegun = false;
        m_Socket.close();
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
        createSocket();

        if (m_ShouldContinueWorking) {
            System.out.println("Ready...");
            m_ProcessingHasBegun = true;
        }

        while (m_ShouldContinueWorking) {
            Message request = waitForIncommingMessage();

            if (request == null) {
                break; // We are closing so exit
            }

            Message response = processRequest(request);
            
            try {
                m_Socket.send(response.getPacket());
            } catch (IOException ex) {
                System.out.println("Failed to send message: " + ex.getMessage());
            }
        }

        m_Socket.close();
    }

    private void createSocket() {
        try {
            InetAddress addr = InetAddress.getByName(m_Address);
            m_Socket = new DatagramSocket(m_Port, addr);
            m_ShouldContinueWorking = true;
        } catch (SocketException | UnknownHostException ex) {
            m_ShouldContinueWorking = false;
            System.out.println("Failed to create socket due to: " + ex.getMessage());
        }
    }

    private Message waitForIncommingMessage() {
        byte[] buf = new byte[256];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        try {
            m_Socket.receive(packet);
        } catch (IOException ex) {
            System.out.println("Failed to receive message due to: " + ex.getMessage());
            return null;
        }

        return new Message(packet);
    }
    
    private Message processRequest(Message request){
            System.out.println("Processing new request...");
            String responsePayload = m_Handler.handleRequestMessage(request);
            OperationCode responseCode = request.getOpCode().toAck();
            InetAddress address = request.getAddress();
            int port = request.getPort();
            return new Message(responseCode, 0, responsePayload, address, port);
        
    }
}
