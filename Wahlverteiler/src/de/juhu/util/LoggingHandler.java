package de.juhu.util;

import static java.lang.System.err;
import static java.lang.System.out;
import static java.util.logging.Level.WARNING;

import java.util.ArrayList;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import javafx.scene.control.TextArea;

/**
 * Behandelt Log Nachrichten und speichert sie zwischen.
 * 
 * @author Juhu1705
 * @category Log
 */
public class LoggingHandler extends Handler implements Runnable {

	private StringBuilder log = new StringBuilder();
	private ArrayList<LogRecord> history = new ArrayList<>();
	private ArrayList<TextArea> logPrinter = new ArrayList<>();
	private boolean isRunning = false, changed = false;

	public StringBuilder getLog() {
		return this.log;
	}

	public void clear() {
		log.setLength(0);
		changed = true;
	}

	public void updateLog() {
		this.clear();

		ArrayList<LogRecord> copy = new ArrayList<>(history);

		this.history.clear();

		copy.forEach(h -> this.publish(h));
	}

	public void bindTextArea(TextArea textArea) {
		this.logPrinter.add(textArea);
		if (!this.isRunning)
			new Thread(this, "Log Updater").start();
	}

	@Override
	public void run() {
		this.isRunning = true;
		while (!logPrinter.isEmpty()) {
			if (changed) {
				this.logPrinter.forEach(printer -> {
					printer.setText(log.toString());
					printer.end();
				});
				this.changed = false;
			}

			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				References.LOGGER.log(Level.WARNING, "Error while sleeping!", e);
			}
		}
		this.isRunning = false;
	}

	@Override
	public void publish(LogRecord record) {
		this.history.add(record);

		if (Config.maxPrintLevel != null && Config.maxPrintLevel.intValue() > record.getLevel().intValue())
			return;

		String output = this.getFormatter().format(record);

		this.changed = true;

		this.log.append(output);

		if (record.getLevel().intValue() >= WARNING.intValue()) {
			err.print(output);
		} else {
			out.print(output);
		}
	}

	@Override
	public void flush() {

	}

	@Override
	public void close() throws SecurityException {

	}

}
