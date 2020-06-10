package de.juhu.filemanager;

import static de.juhu.util.References.LOGGER;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Verwaltet das Exportieren eines {@link WriteableContent} in ein Excel Format
 * in den gegebenen Pfad.
 * 
 * @author Juhu1705
 * @category Export
 */
public class ExcelExporter {

	public static void writeXLS(String pathfile, WriteableContent toWrite) {
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet(toWrite.getName());

		int rownumber = 0;

		toWrite.writeXLS(workbook, sheet, rownumber);

		File file = new File(pathfile + ".xls");
		file.getParentFile().mkdirs();

		try {
			FileOutputStream outFile = new FileOutputStream(file);
			workbook.write(outFile);
			outFile.close();
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Fehler beim Exportieren einer .xls Datei", e);
		}

		try {
			workbook.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void writeXLSX(String pathfile, WriteableContent toWrite) {
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet(toWrite.getName());

		int rownumber = 0;

		toWrite.writeXLSX(workbook, sheet, rownumber);

		File file = new File(pathfile + ".xlsx");
		file.getParentFile().mkdirs();

		try {
			FileOutputStream outFile = new FileOutputStream(file);
			workbook.write(outFile);
			outFile.close();
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Fehler beim Exportieren einer .xlsx Datei", e);
		}

		try {
			workbook.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void writeXLS(String pathfile, WriteableContent... toWrite) {
		HSSFWorkbook workbook = new HSSFWorkbook();

		int rownumber = 0;

		for (WriteableContent writeable : toWrite) {
			HSSFSheet sheet = workbook.createSheet(writeable.getName());
			writeable.writeXLS(workbook, sheet, rownumber);
		}

		File file = new File(pathfile + ".xls");
		file.getParentFile().mkdirs();

		try {
			FileOutputStream outFile = new FileOutputStream(file);
			workbook.write(outFile);
			outFile.close();
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Fehler beim Exportieren einer .xls Datei", e);
		}

		try {
			workbook.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void writeXLS(String pathfile, List<WriteableContent> toWrite) throws IOException {
		HSSFWorkbook workbook = new HSSFWorkbook();

		int rownumber = 0;

		for (WriteableContent writeable : toWrite) {
			HSSFSheet sheet = workbook.createSheet(writeable.getName());
			writeable.writeXLS(workbook, sheet, rownumber);
		}

		File file = new File(pathfile + ".xls");
		file.getParentFile().mkdirs();

		try {
			FileOutputStream outFile = new FileOutputStream(file);
			workbook.write(outFile);
			outFile.close();
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Fehler beim Exportieren einer .xls Datei", e);
		}

		workbook.close();

	}

	public static void writeXLSX(String pathfile, WriteableContent... toWrite) throws IOException {
		XSSFWorkbook workbook = new XSSFWorkbook();

		int rownumber = 0;

		for (WriteableContent writeable : toWrite) {
			XSSFSheet sheet = workbook.createSheet(writeable.getName());
			writeable.writeXLSX(workbook, sheet, rownumber);
		}

		File file = new File(pathfile + ".xlsx");
		file.getParentFile().mkdirs();

		try {
			FileOutputStream outFile = new FileOutputStream(file);
			workbook.write(outFile);
			outFile.close();
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Fehler beim Exportieren einer .xlsx Datei", e);
		}

		workbook.close();

	}

	public static void writeXLSX(String pathfile, List<WriteableContent> toWrite) throws IOException {
		XSSFWorkbook workbook = new XSSFWorkbook();

		int rownumber = 0;

		for (WriteableContent writeable : toWrite) {
			XSSFSheet sheet = workbook.createSheet(writeable.getName());
			writeable.writeXLSX(workbook, sheet, rownumber);
		}

		workbook.setForceFormulaRecalculation(true);

		File file = new File(pathfile + ".xlsx");

		// file.getParentFile().mkdirs();

		try {
			FileOutputStream outFile = new FileOutputStream(file);
			workbook.write(outFile);
			outFile.close();
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Fehler beim Exportieren einer .xlsx Datei", e);
		}

		workbook.close();

	}
}
