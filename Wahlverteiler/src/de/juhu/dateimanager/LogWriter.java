package de.juhu.dateimanager;

import static de.juhu.util.References.LOGGER;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;

import de.juhu.util.References;

/**
 * Schreibt den Log in den angegebenen Pfad.
 * 
 * @author Juhu1705
 * @category Export
 *
 */
public class LogWriter {

	public static void writeLog(String pathfile) {
		FileWriter fileWriter = null;
		BufferedWriter writer = null;

		try {
			fileWriter = new FileWriter(new File(pathfile + ".log"));
			writer = new BufferedWriter(fileWriter);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Fehler beim Erstellen einer .log Datei", e);
		}

		StringBuilder sw = References.LOGGING_HANDLER.getLog();

		String[] strings = sw.toString().split(" \n");

		try {
			for (String s : strings) {
				writer.write(sw.toString());
				writer.newLine();
			}
		} catch (IOException e1) {
			LOGGER.log(Level.SEVERE, "Fehler beim Erstellen einer .log Datei", e1);
		}

		try {
			writer.close();
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Fehler beim Verarbeiten einer .log Datei", e);
		}
	}

}
