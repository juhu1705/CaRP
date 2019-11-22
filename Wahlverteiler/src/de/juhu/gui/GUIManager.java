package de.juhu.gui;

import static de.juhu.util.References.LOGGER;

import java.util.ArrayList;

import de.juhu.util.Constants;
import de.juhu.util.Interfaces.Renderable;

public class GUIManager {

	private static GUIManager instance;

	private boolean isRunning = false;
	private boolean shouldClose = false;
	private long time, prevTime, sleepTime;

	private ArrayList<Renderable> frames = new ArrayList<>();

	public static GUIManager getInstance() {
		return instance == null ? instance = new GUIManager() : instance;
	}

	/**
	 * Empty Protected Constructor, to avoid that a new GuiManager is Createt.
	 */
	protected GUIManager() {

	}

	public void addRenderable(Renderable r) {
		frames.add(r);
	}

	/**
	 * Starts the GUI.
	 */
	public void start() {
		if (this.isRunning)
			LOGGER.warning("Es sollten nicht zwei Windows gleichzeitig geöfnet sein!");
		this.isRunning = true;

		this.init();

		this.time = System.nanoTime();

		while (!this.shouldClose) {
			this.prevTime = System.nanoTime();

			this.render();

			this.time = System.nanoTime();

			long toSleep = 10;

			this.sleepTime = this.time - this.prevTime;
			if (this.sleepTime > Constants.FPS * 1000000000) {
				toSleep = ((Constants.FPS * 1000000000) - this.sleepTime) / 1000000;
			}

			try {
				Thread.sleep(toSleep);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		this.close();
	}

	/**
	 * Render and update the GUI
	 */
	private void render() {
		this.frames.forEach(f -> f.render());
	}

	/**
	 * Initial the GUI
	 */
	private void init() {
		this.frames.forEach(f -> f.init());
	}

	/**
	 * Delete the GUI
	 */
	private void close() {
		this.frames.forEach(f -> f.close());
	}

}
