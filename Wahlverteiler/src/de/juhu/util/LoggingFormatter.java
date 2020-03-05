package de.juhu.util;

import static de.juhu.util.PrintFormat.ALL;
import static de.juhu.util.PrintFormat.CLASS;
import static de.juhu.util.PrintFormat.LEVEL;
import static de.juhu.util.PrintFormat.LOGGER;
import static de.juhu.util.PrintFormat.ONLY_MESSAGE;
import static de.juhu.util.PrintFormat.TIME;
import static java.time.LocalDateTime.now;

import java.time.format.DateTimeFormatter;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Formatiert den Log entsprechend den Configeinstellungen.
 * 
 * @author Juhu1705
 * @category Log
 */
public class LoggingFormatter extends Formatter {

	private static final DateTimeFormatter timeformatter = DateTimeFormatter.ofPattern("HH:mm:ss");

	@Override
	public String format(LogRecord record) {
		StringBuilder sb = new StringBuilder();

		if (record == null || Util.isBlank(record.getSourceClassName()))
			return "";

		String[] className = record.getSourceClassName().split("\\.");

		PrintFormat format = Config.printFormat != null ? PrintFormat.valueOf(Config.printFormat) : ALL;

		if (format.getLevel() >= TIME.getLevel())
			sb.append("[" + now().format(timeformatter) + "] ");
		if (format.getLevel() >= LEVEL.getLevel())
			sb.append("[" + record.getLevel() + "] ");
		if (format.getLevel() >= CLASS.getLevel())
			sb.append("[" + Thread.currentThread().getName() + ":" + className[className.length - 1] + ":"
					+ record.getSourceMethodName() + "] ");
		if (format.getLevel() >= LOGGER.getLevel()) {
			if (record.getLoggerName() != null)
				sb.append("[" + record.getLoggerName() + "] ");
		}
		if (format.getLevel() >= ONLY_MESSAGE.getLevel())
			sb.append(record.getMessage());
		sb.append("\n");

		Throwable thrown = record.getThrown();

		if (thrown != null) {
			sb.append(thrown);
			sb.append("\n");

			for (StackTraceElement ste : thrown.getStackTrace()) {
				sb.append("    at ");
				sb.append(ste);
				sb.append("\n");
			}
		}

//		sb.append("[" + now().format(timeformatter) + "] [" + record.getLevel() + "|"
//				+ Thread.currentThread().getName() + ":" + className[className.length - 1] + ":"
//				+ record.getSourceMethodName() + "] " + record.getMessage() + "\n");

		return sb.toString();
	}

}
