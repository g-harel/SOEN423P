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
import java.net.DatagramPacket;
import java.net.InetAddress;

/**
 *
 * @author cmcarthur
 */
public class Message {

    /*
    
    Simple message definition for server-server communication
    inspired by HTTP:
        OperationCode + "\r\n" +  Sequence Number + "\r\n" + { data }   
    
     */
    private OperationCode m_Code;
    private int m_SeqNum;
    private RegisteredReplica m_Location;
    private AddressBook m_Dest;
    private String m_Data;
    private InetAddress m_Addr;
    private int m_Port;

    // @warning max data.length is 2048 bytes !
    // Main entry point for initiating communication, See Socket for an example
    public Message(OperationCode code, int seq, String data, AddressBook dst) throws Exception {
        if (data.length() >= 2048) {
            throw new Exception("data payload is too large!");
        }

        this.m_Code = code;
        this.m_SeqNum = seq;
        this.m_Location = RegisteredReplica.EVERYONE;
        this.m_Data = data;
        this.m_Addr = dst.getAddr();
        this.m_Port = dst.getPort();
        m_Dest = dst;
    }

    // This should not be called!
    // Used for decomposing correctly formatted requests
    protected Message(DatagramPacket packet) {

        String payload = new String(packet.getData(), 0, packet.getLength());

        this.m_Code = OperationCode.fromString(payload.substring(0, payload.indexOf("\r\n")));
        payload = payload.substring(payload.indexOf("\r\n") + 2);

        this.m_SeqNum = Integer.valueOf(payload.substring(0, payload.indexOf("\r\n")));
        payload = payload.substring(payload.indexOf("\r\n") + 2);
        
        this.m_Location = RegisteredReplica.valueOf(payload.substring(0, payload.indexOf("\r\n")));
        payload = payload.substring(payload.indexOf("\r\n") + 2);
        
        m_Dest = AddressBook.valueOf(payload.substring(0, payload.indexOf("\r\n")));

        this.m_Data = payload.substring(payload.indexOf("\r\n") + 2);
        this.m_Addr = packet.getAddress();
        this.m_Port = packet.getPort();
    }

    // This should not be called!
    // This should only be used by RequestListener
    protected Message(OperationCode code, int seq, RegisteredReplica loc, String data, InetAddress addr, int port) {       
        this.m_Code = code;
        this.m_SeqNum = seq;
        this.m_Location = loc;
        this.m_Data = data;
        this.m_Addr = addr;
        this.m_Port = port;
    }

    // This should not be called!
    // This should only be used by RequestListener
    protected Message(OperationCode code, int seq, RegisteredReplica location, String data, AddressBook dest) {
        this.m_Code = code;
        this.m_SeqNum = seq;
        this.m_Location = location;
        this.m_Data = data;
        this.m_Addr = dest.getAddr();
        this.m_Port = dest.getPort();
        m_Dest = dest;
    }

    public DatagramPacket getPacket() {
        String payload = m_Code.toString() + "\r\n" + String.valueOf(m_SeqNum) + "\r\n"
                + m_Location.toString() + "\r\n" + m_Dest.toString() + "\r\n" + m_Data;
        return new DatagramPacket(payload.getBytes(), payload.length(), m_Addr, m_Port);
    }

    public String getData() {
        return m_Data;
    }

    public OperationCode getOpCode() {
        return m_Code;
    }

    public AddressBook getDest() {
        return m_Dest;
    }

    @Override
    public String toString() {
        return "Message{" + "code=" + m_Code + ", seq=" + m_SeqNum + ", loc=" + m_Location + ", data=" + m_Data +
                ", dst=" + m_Dest +  ", addr=" + m_Addr + ", port=" + m_Port + '}';
    }

    public int getSeqNum() {
        return m_SeqNum;
    }

    public void setSeqNum(int seqNum) {
        this.m_SeqNum = seqNum;
    }

    public RegisteredReplica getLocation() {
        return m_Location;
    }

    public void setLocation(RegisteredReplica location) {
        this.m_Location = location;
    }
}
