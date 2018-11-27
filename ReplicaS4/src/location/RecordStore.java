package location;

import java.util.ArrayList;
import java.util.HashMap;

public class RecordStore {
	private HashMap<Character, ArrayList<Record>> records;

	public RecordStore() {
		this.records = new HashMap<Character, ArrayList<Record>>();
	}

	public String toString() {
		String res = "";
		for (ArrayList<Record> records : this.records.values()) {
			for (Record record : records) {
				res += String.format("[%s] %s %s (%s)\n", record.recordID, record.firstName, record.lastName, record.mailID);
			}
		}
		if (res.length() != 0) {
			res = res.substring(0, res.length() - 1);
		}
		return res;
	}

	public synchronized int count() {
		int size = 0;
		for (ArrayList<Record> records : this.records.values()) {
			size += records.size();
		}
		return size;
	}

	public synchronized Record read(String recordID) {
		for (ArrayList<Record> records : this.records.values()) {
			for (Record record : records) {
				if (record.recordID.equals(recordID)) {
					return record;
				}
			}
		}
		return null;
	}

	public synchronized void write(Record record) {
		Character index = record.lastName.toUpperCase().charAt(0);
		ArrayList<Record> records = null;
		if (!this.records.containsKey(index)) {
			records = new ArrayList<Record>();
			this.records.put(index, records);
		} else {
			records = this.records.get(index);
		}
		records.add(record);
	}

	public synchronized void delete(String recordID) {
		for (ArrayList<Record> records : this.records.values()) {
			int count = 0;
			for (Record record : records) {
				if (record.recordID.equals(recordID)) {
					records.remove(count);
					return;
				}
				count++;
			}
		}
	}
}
