/*
 * The MIT License
 *
 * Copyright 2018
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package UDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 *
 * @author cmcarthur
 * 
 * EXAMPLE USAGE:
 * 
 * public void foo() {
 *     Socket instance = new Socket();
 *     Message send = new Message(OperationCode.TRANSFER_RECORD, // The operation you'd like to perform
 *                                0,                             // The Sequence Number, will only be Zero between FE and SEQ, otherwise copy from request
 *                                "TESTING",                     // The beautiful message you'd like pass ( as a string )
 *                                TEST_ADDR);                    // You the message is intended for
 * }
 * 
 */
public class Socket {

    private DatagramSocket socket;
    private Message response;

    public Socket() throws SocketException {
        this.socket = new DatagramSocket();
    }

    public Message getResponse() {
        return response;
    }

    /*
    @msg the message
    @retryCounter Number of times to retry, (ie 10 for 10 attempts)
    @timeout time to wait for ack to arrive
    @return true is send was answered with ack otherwise false
    @throws error when failing to send ( should not occure )
     */
    public boolean send(Message msg, int retryCounter, int timeout) throws IOException {
        sendRaw(msg); // Dont catch this exception, likely to be the internal socket is bad

        try {
            Message hopefulAck = receiveRaw(timeout);

            if (hopefulAck.getOpCode() != msg.getOpCode().toAck()) {
                throw new Exception("RUDP: Rx a message but wasnt the correct ACK OpCode");
            }

            response = hopefulAck;

        } catch (Exception ex) {
            if (--retryCounter > 0) {
                System.out.println(" Attempt #" + retryCounter + " failed due to: " + ex.getMessage());
                return send(msg, retryCounter, timeout);
            } else {
                return false;
            }
        }

        return true;
    }

    private void sendRaw(Message request) throws IOException {
        socket.send(request.getPacket());
    }

    private Message receiveRaw(int timeout) throws IOException {
        byte[] buf = new byte[256];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);

        socket.setSoTimeout(timeout); // Set timeout in case packet is lost
        socket.receive(packet);

        return new Message(packet);
    }

}
