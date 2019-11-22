package de.juhu.dateimanager;

import static de.juhu.util.References.LOGGER;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;

public class CSVExporter {

	public static void writeCSV(String pathfile, WriteableContent toWrite) {

		FileWriter fileWriter = null;
		BufferedWriter writer = null;

		try {
			fileWriter = new FileWriter(new File(pathfile + ".csv"));
			writer = new BufferedWriter(fileWriter);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Fehler beim Erstellen einer .csv Datei", e);
		}

		toWrite.writeCSV(writer);

		try {
			writer.close();
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Fehler beim Verarbeiten einer .csv Datei", e);
		}
	}

	public static void writeCSV(String pathfile, WriteableContent... toWrite) {

		FileWriter fileWriter = null;
		BufferedWriter writer = null;

		try {
			fileWriter = new FileWriter(new File(pathfile + ".csv"));
			writer = new BufferedWriter(fileWriter);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Fehler beim Erstellen einer .csv Datei", e);
		}

		for (WriteableContent writeable : toWrite) {
			writeable.writeCSV(writer);
			try {
				writer.newLine();
				writer.newLine();
			} catch (IOException e) {
				LOGGER.log(Level.WARNING, "Exception caused while exporting data: ", e);
			}

		}

		try {
			writer.close();
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Fehler beim Verarbeiten einer .csv Datei", e);
		}
	}

}
