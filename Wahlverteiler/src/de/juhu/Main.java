package de.juhu;

import static de.juhu.util.References.LOGGER;
import static de.juhu.util.References.PROJECT_NAME;
import static de.juhu.util.References.VERSION;

import de.juhu.gui.GUIManager;
import de.juhu.gui.frames.GUIFrame;

public class Main {

	public static void main(String[] args) {
		LOGGER.info("Starte: " + PROJECT_NAME + " | Version: " + VERSION);

//		Test in and exports
//		WriteableContent c = null;
//
//		try {
//			c = CSVImporter.readCSV("C:\\Users\\Fabiu\\Desktop\\datei.csv");
//		} catch (IOException e) {
//			LOGGER.log(Level.SEVERE, "Fehler beim Verarbeiten einer .xlsx Datei", e);
//		}
//
//		CSVExporter.writeCSV("C:\\Users\\Fabiu\\Desktop\\dateiout", c);
//		ExcelExporter.writeXLSX("C:\\Users\\Fabiu\\Desktop\\dateix", c);

		GUIManager.getInstance().addRenderable(new GUIFrame("assets/guiStructure/gui_information.xlsx"));

		GUIManager.getInstance().start();
	}

}
