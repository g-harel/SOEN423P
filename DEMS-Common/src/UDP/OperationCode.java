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

    // For RM-SEQ
    REPLAY(1131),
    ACK_REPLAY(3131),

    // For FE-PI Communication
    CREATE_MANAGER_RECORD(1001),
    ACK_CREATE_MANAGER_RECORD(3001),
    CREATE_EMPLOYEE_RECORD(1005),
    ACK_CREATE_EMPLOYEE_RECORD(3005),
    GET_RECORD_COUNT(1002),
    ACK_GET_RECORD_COUNT(3002),
    DOES_RECORD_EXIST(1003),
    ACK_DOES_RECORD_EXIST(3003),
    TRANSFER_RECORD(1004),
    ACK_TRANSFER_RECORD(3004),

    // For PI-FE
    OPERATION_RETVAL(1025),
    ACK_OPERATION_RETVAL(3025),

    // For FE-RM
    NO_RESP_NOTIFICATION(1201),
    ACK_NO_RESP_NOTIFICATION(3201),
    FAULY_RESP_NOTIFICATION(1202),
    ACK_FAULY_RESP_NOTIFICATION(3202),

	//For RM-RE
	RESTORE_ORDER_NOTIFICATION(1666),
	ACK_RESTORE_ORDER_NOTIFICATION(3666),
	RESTART_ORDER_NOTIFICATION(1007),
	ACK_RESTART_ORDER_NOTIFICATION(3007);

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
                case REPLAY: return ACK_REPLAY;

                case CREATE_MANAGER_RECORD: return ACK_CREATE_MANAGER_RECORD;
                case CREATE_EMPLOYEE_RECORD: return ACK_CREATE_EMPLOYEE_RECORD;
                case GET_RECORD_COUNT: return ACK_GET_RECORD_COUNT;
                case DOES_RECORD_EXIST: return ACK_DOES_RECORD_EXIST;
                case TRANSFER_RECORD: return ACK_TRANSFER_RECORD;

                case OPERATION_RETVAL: return ACK_OPERATION_RETVAL;

                case NO_RESP_NOTIFICATION: return ACK_NO_RESP_NOTIFICATION;
                case FAULY_RESP_NOTIFICATION: return ACK_FAULY_RESP_NOTIFICATION;
                case RESTORE_ORDER_NOTIFICATION: return ACK_RESTORE_ORDER_NOTIFICATION;
                case RESTART_ORDER_NOTIFICATION: return ACK_RESTART_ORDER_NOTIFICATION;

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
