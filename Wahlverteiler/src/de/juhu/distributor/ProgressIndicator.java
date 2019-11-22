package de.juhu.distributor;

import de.juhu.guiFX.FullProgress;
import de.juhu.guiFX.PartProgress;

public class ProgressIndicator {

	private int fProgressMax = 0, fProgressValue = 0, aProgressMax = 0, aProgressValue = 0;

	private static ProgressIndicator instance;

	public static ProgressIndicator getInstance() {
		return instance == null ? new ProgressIndicator() : instance;
	}

	protected ProgressIndicator() {
		// References.LOGGER.info(p0 + "|" + p1);
		instance = this;
	}

	public ProgressIndicator setfProgressMax(int value) {
		this.fProgressMax = value;
		return this;
	}

	public ProgressIndicator setfProgressValue(int value) {
		this.fProgressValue = value;

		if (this.fProgressValue == -1)
			FullProgress.getInstance().setProgress(-1);
		else
			FullProgress.getInstance().setProgress((double) this.fProgressValue / (double) this.fProgressMax);

		return this;
	}

	public ProgressIndicator addfProgressValue(int value) {
		return this.setfProgressValue(this.fProgressValue + value);
	}

	public ProgressIndicator setaProgressMax(int value) {
		this.aProgressMax = value;
		return this;
	}

	public ProgressIndicator setaProgressValue(int value) {
		this.aProgressValue = value;

		PartProgress.getInstance().setProgress((double) this.aProgressValue / (double) this.aProgressMax);

		return this;
	}

	public ProgressIndicator addaProgressValue(int value) {
		return this.setaProgressValue(this.aProgressValue + value);
	}

	/**
	 * @return the fProgressMax
	 */
	public int getfProgressMax() {
		return fProgressMax;
	}

	/**
	 * @return the fProgressValue
	 */
	public int getfProgressValue() {
		return fProgressValue;
	}

	/**
	 * @return the aProgressMax
	 */
	public int getaProgressMax() {
		return aProgressMax;
	}

	/**
	 * @return the aProgressValue
	 */
	public int getaProgressValue() {
		return aProgressValue;
	}

}
