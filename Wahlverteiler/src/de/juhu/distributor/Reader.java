package de.juhu.distributor;

public abstract class Reader {

	String key;

	public Reader(String key) {
		this.key = key;
	}

	public boolean isKey(String key) {
		return this.key.equalsIgnoreCase(key);
	}

	public abstract void read(String[] line, int lineNumber);

}
