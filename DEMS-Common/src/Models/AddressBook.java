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
package Models;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 *
 * @author cmcarthur
 */
public enum AddressBook {
    FRONTEND("DEMS-Front-End", "FE", "239.101.10.10", 13547),
    SEQUENCER("DEMS-Sequencer", "SEQ", "239.101.20.20", 45794),
    MANAGER("DEMS-Replica-Manager", "RM", "239.101.30.30", 25897),
    REPLICAS("DEMS-Replica-Instance", "PI", "239.101.40.40", 34268);

    private AddressBook(String name, String shortHand, String addr, int port) {
        m_Name = name;
        m_ShortHand = shortHand;
        try {
            m_Addr = InetAddress.getByName(addr);
        } catch (UnknownHostException ex) {
            System.out.println("This is impossible... " + ex.getMessage());
            m_Addr = InetAddress.getLoopbackAddress();
        }
        m_Port = port;
    }

    @Override
    public String toString() {
        return m_Name;
    }

    public String getShortHandName() {
        return m_ShortHand;
    }

    public InetAddress getAddr() {
        return m_Addr;
    }

    public int getPort() {
        return m_Port;
    }

    private InetAddress m_Addr;
    private final int m_Port;
    private final String m_Name;
    private final String m_ShortHand;
}
