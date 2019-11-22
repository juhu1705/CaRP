package de.juhu.util;

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
