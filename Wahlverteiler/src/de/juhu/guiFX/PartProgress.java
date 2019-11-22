package de.juhu.guiFX;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class PartProgress {

	private static PartProgress instance;

	public static PartProgress getInstance() {
		return instance == null ? instance = new PartProgress() : instance;
	}

	private DoubleProperty progress;

	public final double getProgress() {
		return progress == null ? 0 : progress.get();
	}

	public final void setProgress(double progress) {
		// this.progress.set(progress);
	}

	public final DoubleProperty progressProperty() {
		return this.progress == null ? this.progress = new SimpleDoubleProperty(0) : this.progress;
	}

}
