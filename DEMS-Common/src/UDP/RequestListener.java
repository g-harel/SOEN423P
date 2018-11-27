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

import Models.AddressBook;
import Models.RegisteredReplica;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 *
 * @author cmcarthur
 *
 * EXAMPLE USAGE:
 *
 * public class Example implements RequestListener.Processor {
 *
 * final private RequestListener m_Listener; private Thread m_ListenerThread;
 *
 * public Example() { m_Listener = new RequestListener(this, TEST_ADDR); }
 *
 * public void Launch(){ m_ListenerThread = new Thread(m_Listener);
 * m_ListenerThread.start(); m_Listener.Wait(); // Make sure it's running before
 * getting any farther ( optional ) }
 *
 * public void Shutdown() { m_Listener.Stop(); m_ListenerThread.join(); }
 *
 * @Override public String handleRequestMessage(Message msg) throws Exception {
 * // Do some magical work with msg ( aka save a copy or switch on operation
 * code ) if (msg == incorrect) { throw new Exception("Explination why its wrong
 * here"); } return "SUCCESS MESSSAGE - HELLO WORLD!"; }
 *
 */
public class RequestListener implements Runnable {

    public interface Processor {

        /**
         * @param msg the new incomming request
         * @return the payload/data to respond
         * @throws Exception the error message to reply
         */
        public String handleRequestMessage(Message msg) throws Exception;
    }

    private AddressBook m_Address;
    private RegisteredReplica m_InstanceId;

    final private Processor m_Handler;

    private boolean m_ShouldContinueWorking;
    private boolean m_ProcessingHasBegun;

    private MulticastSocket m_Socket;

    /**
     * @param handler the callback to process new request messages
     * @param address your local address you want to listen from
     */
    public RequestListener(Processor handler, AddressBook address) {
        m_Handler = handler;
        m_Address = address;
        m_InstanceId = RegisteredReplica.EVERYONE;
        m_ShouldContinueWorking = false;
        m_ProcessingHasBegun = false;
    }

    /**
     * @param handler the callback to process new request messages
     * @param address your local address you want to listen from
     * @param instanceId the specific location which packets should be for;
     * packets dst for other locs will be dropped
     */
    public RequestListener(Processor handler, AddressBook address, RegisteredReplica instanceId) {
        m_Handler = handler;
        m_Address = address;
        m_InstanceId = instanceId;
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

            if (response == null) {
                continue; // Nothing to do!
            }

            try {
                System.out.println("Replying... " + response);
                m_Socket.send(response.getPacket());
            } catch (IOException ex) {
                System.out.println("Failed to send message: " + ex.getMessage());
            }
        }

        m_Socket.close();
    }

    private void createSocket() {
        try {
                            m_Socket = new MulticastSocket(m_Address.getPort());

            //if( m_Address != AddressBook.REPLICAS){
            //} else {
              //  m_Socket = new MulticastSocket();
            //}
            
            m_Socket.joinGroup(m_Address.getAddr());
            m_ShouldContinueWorking = true;
        } catch (IOException ex) {
            m_ShouldContinueWorking = false;
            System.out.println("Failed to create socket due to: " + ex.getMessage());
        }
    }

    private Message waitForIncommingMessage() {
        byte[] buf = new byte[2048];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        try {
            m_Socket.receive(packet);
            System.out.println("UDP.RequestListener.waitForIncommingMessage()");
        } catch (IOException ex) {
            System.out.println("Failed to receive message due to: " + ex.getMessage());
            return null;
        }

        return new Message(packet);
    }

    private Message processRequest(Message request) {
        boolean isLocationImportant = (m_InstanceId != RegisteredReplica.EVERYONE);
        boolean isForSomeone = (request.getLocation() != RegisteredReplica.EVERYONE);
        boolean isForNotMe = (request.getLocation() != m_InstanceId);
        if (isLocationImportant && isForSomeone && isForNotMe) {
            System.out.println("Dropping request for [ " + request.getLocation().toString() + " ]");
            return null;
        }

        System.out.println("Processing new request...");
        String responsePayload;
        OperationCode responseCode = request.getOpCode().toAck();

        try {
            responsePayload = m_Handler.handleRequestMessage(request);
        } catch (Exception ex) {
            System.out.println("Handler failed to process request!");
            responseCode = OperationCode.INVALID;
            responsePayload = ex.getMessage();
        }

        InetAddress address = request.getAddress();
        int port = request.getPort();
        return new Message(responseCode, request.getSeqNum(), request.getLocation(), responsePayload, address, port);
    }
}
