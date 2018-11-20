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

/**
 *
 * @author cmcarthur
 */
public enum OperationCode {
    INVALID(-1),
    UPDATE_RECORD_INDEX(1001),
    ACK_UPDATE_RECORD_INDEX(3001),
    GET_RECORD_COUNT(1002),
    ACK_GET_RECORD_COUNT(3002),
    DOES_RECORD_EXIST(1003),
    ACK_DOES_RECORD_EXIST(3003),
    TRANSFER_RECORD(1004),
    ACK_TRANSFER_RECORD(3004);

    private OperationCode(int val) {
        m_Value = val;
    }

    @Override
    public String toString() {
        return "" + m_Value;
    }
    
    public OperationCode toAck(){
        if( m_Value < 3000 ){
            switch( this ){
                case UPDATE_RECORD_INDEX: return ACK_UPDATE_RECORD_INDEX;
                case GET_RECORD_COUNT: return ACK_GET_RECORD_COUNT;
                case DOES_RECORD_EXIST: return ACK_DOES_RECORD_EXIST;
                case TRANSFER_RECORD: return ACK_TRANSFER_RECORD;
                default: return INVALID;
            }
        }
        return this;
    }

    final private int m_Value;

    static public OperationCode fromString(String val) {
        int newVal = Integer.parseInt(val);

        for (OperationCode opcode : OperationCode.values()) {
            if (newVal == opcode.m_Value) {
                return opcode;
            }
        }

        return INVALID;
    }
}
