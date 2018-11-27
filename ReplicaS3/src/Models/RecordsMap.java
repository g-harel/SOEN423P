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
package Models;

import java.util.HashMap;
import java.util.LinkedList;

/**
 *
 * @author cmcarthur
 */
public class RecordsMap {

    private HashMap<String, LinkedList<Record>> m_MapOfRecords;

    public RecordsMap() {
        m_MapOfRecords = new HashMap<>();
    }

    public void addRecord(Record record) {
        LinkedList<Record> listOfRecords = m_MapOfRecords.get(record.getHashIndex());

        if (listOfRecords == null) {
            listOfRecords = new LinkedList<>();
            listOfRecords.add(record);
            m_MapOfRecords.put(record.getHashIndex(), listOfRecords);
        } else {
            listOfRecords.add(record);
            m_MapOfRecords.replace(record.getHashIndex(), listOfRecords);
        }
    }

    @Override
    public String toString() {
        return m_MapOfRecords.toString();
    }

    public int count() {
        int counter = 0;

        for (HashMap.Entry<String, LinkedList<Record>> entry : m_MapOfRecords.entrySet()) {
            counter += entry.getValue().size();
        }

        return counter;
    }

    public Record removeRecord(String recordID) {
        for (HashMap.Entry<String, LinkedList<Record>> entry : m_MapOfRecords.entrySet()) {
            for(Record record : entry.getValue()) {
                if(record.equals( recordID)){
                    return entry.getValue().remove(record) ? record : null;
                }
            }
        }
        
        return null;
    }
}
