package de.juhu.util;

/**
 * Das Print Format ist wichtig für die Logger Klasse und bestimmt die
 * angegebenen zusatzinformationen. Je kleiner der Wert, desto weniger
 * zusatzinformationen werden mitgegeben.
 * 
 * @author Juhu1705
 * @category Log
 */
public enum PrintFormat {
	LOGGER(3), CLASS(4), ONLY_MESSAGE(0), TIME(1), LEVEL(2), ALL(100);

	private int level;

	PrintFormat(int level) {
		this.level = level;
	}

	public int getLevel() {
		return this.level;
	}
}
