package de.juhu.dateimanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import de.juhu.util.References;

/**
 * Importiert eine Excel Datei aus dem mitgegebenen Pfad.
 * 
 * 
 * @author Juhu1705
 * @category Import
 *
 */
public class ExcelImporter {

	public static WriteableContent readXLS(String pathfile) throws IOException, URISyntaxException {

		WriteableContent input = new WriteableContent();

		InputStream inputStream = getInput(pathfile);

		HSSFWorkbook w;

		HSSFSheet sheet = (w = new HSSFWorkbook(inputStream)).getSheetAt(0);

		for (Row row : sheet) {
			for (Cell cell : row) {
				switch (cell.getCellType()) {
				case BLANK:
					input.addCell(new Vec2i(cell.getAddress().getColumn(), cell.getRowIndex()), "");
					break;
				case BOOLEAN:
					input.addCell(new Vec2i(cell.getAddress().getColumn(), cell.getRowIndex()),
							"" + cell.getBooleanCellValue());
					break;
				case ERROR:
					input.addCell(new Vec2i(cell.getAddress().getColumn(), cell.getRowIndex()), "");
					break;
				case FORMULA:
					input.addCell(new Vec2i(cell.getAddress().getColumn(), cell.getRowIndex()),
							"" + w.getCreationHelper().createFormulaEvaluator().evaluate(cell).getNumberValue());
					break;
				case NUMERIC:
					input.addCell(new Vec2i(cell.getAddress().getColumn(), cell.getRowIndex()),
							"" + cell.getNumericCellValue());
					break;
				case STRING:
					input.addCell(new Vec2i(cell.getAddress().getColumn(), cell.getRowIndex()),
							cell.getStringCellValue());
					break;
				case _NONE:
					input.addCell(new Vec2i(cell.getAddress().getColumn(), cell.getRowIndex()), "");
					break;
				default:
					input.addCell(new Vec2i(cell.getAddress().getColumn(), cell.getRowIndex()), "");
					break;
				}
			}
		}
		inputStream.close();
		w.close();

		return input;
	}

	public static WriteableContent readXLSX(String pathfile) throws IOException, URISyntaxException {

		WriteableContent input = new WriteableContent();

		InputStream inputStream = getInput(pathfile);

		XSSFWorkbook w;

		XSSFSheet sheet = (w = new XSSFWorkbook(inputStream)).getSheetAt(0);

		for (Row row : sheet) {
			for (Cell cell : row) {
				switch (cell.getCellType()) {
				case BLANK:
					input.addCell(new Vec2i(cell.getAddress().getColumn(), cell.getRowIndex()), "");
					break;
				case BOOLEAN:
					input.addCell(new Vec2i(cell.getAddress().getColumn(), cell.getRowIndex()),
							"" + cell.getBooleanCellValue());
					break;
				case ERROR:
					input.addCell(new Vec2i(cell.getAddress().getColumn(), cell.getRowIndex()), "");
					break;
				case FORMULA:
					input.addCell(new Vec2i(cell.getAddress().getColumn(), cell.getRowIndex()),
							"" + w.getCreationHelper().createFormulaEvaluator().evaluate(cell).getNumberValue());
					break;
				case NUMERIC:
					input.addCell(new Vec2i(cell.getAddress().getColumn(), cell.getRowIndex()),
							"" + cell.getNumericCellValue());
					break;
				case STRING:
					input.addCell(new Vec2i(cell.getAddress().getColumn(), cell.getRowIndex()),
							cell.getStringCellValue());
					break;
				case _NONE:
					input.addCell(new Vec2i(cell.getAddress().getColumn(), cell.getRowIndex()), "");
					break;
				default:
					input.addCell(new Vec2i(cell.getAddress().getColumn(), cell.getRowIndex()), "");
					break;
				}
			}
		}
		inputStream.close();
		w.close();

		return input;
	}

	public static List<WriteableContent> readXLSImproved(String pathfile) throws IOException, URISyntaxException {

		ArrayList<WriteableContent> dataContent = new ArrayList<>();

		InputStream inputStream = getInput(pathfile);

		HSSFWorkbook w;

		for (Sheet sheetIn : (w = new HSSFWorkbook(inputStream))) {

			WriteableContent input = new WriteableContent(sheetIn.getSheetName());

			for (Row row : sheetIn) {
				for (Cell cell : row) {
					switch (cell.getCellType()) {
					case BLANK:
						input.addCell(new Vec2i(cell.getAddress().getColumn(), cell.getRowIndex()), "");
						break;
					case BOOLEAN:
						input.addCell(new Vec2i(cell.getAddress().getColumn(), cell.getRowIndex()),
								"" + cell.getBooleanCellValue());
						break;
					case ERROR:
						input.addCell(new Vec2i(cell.getAddress().getColumn(), cell.getRowIndex()), "");
						break;
					case FORMULA:
						input.addCell(new Vec2i(cell.getAddress().getColumn(), cell.getRowIndex()),
								"" + w.getCreationHelper().createFormulaEvaluator().evaluate(cell).getNumberValue());
						break;
					case NUMERIC:
						input.addCell(new Vec2i(cell.getAddress().getColumn(), cell.getRowIndex()),
								"" + cell.getNumericCellValue());
						break;
					case STRING:
						input.addCell(new Vec2i(cell.getAddress().getColumn(), cell.getRowIndex()),
								cell.getStringCellValue());
						break;
					case _NONE:
						input.addCell(new Vec2i(cell.getAddress().getColumn(), cell.getRowIndex()), "");
						break;
					default:
						input.addCell(new Vec2i(cell.getAddress().getColumn(), cell.getRowIndex()), "");
						break;
					}
				}
			}
			dataContent.add(input);
		}
		inputStream.close();
		w.close();

		return dataContent;
	}

	public static List<WriteableContent> readXLSXImproved(String pathfile) throws IOException, URISyntaxException {

		ArrayList<WriteableContent> dataContent = new ArrayList<>();

		InputStream inputStream = getInput(pathfile);

		XSSFWorkbook w;

		for (Sheet sheet : (w = new XSSFWorkbook(inputStream))) {

			WriteableContent input = new WriteableContent(sheet.getSheetName());
			for (Row row : sheet) {
				for (Cell cell : row) {
					switch (cell.getCellType()) {
					case BLANK:
						input.addCell(new Vec2i(cell.getAddress().getColumn(), cell.getRowIndex()), "");
						break;
					case BOOLEAN:
						input.addCell(new Vec2i(cell.getAddress().getColumn(), cell.getRowIndex()),
								"" + cell.getBooleanCellValue());
						break;
					case ERROR:
						input.addCell(new Vec2i(cell.getAddress().getColumn(), cell.getRowIndex()), "");
						break;
					case FORMULA:
						input.addCell(new Vec2i(cell.getAddress().getColumn(), cell.getRowIndex()),
								"" + w.getCreationHelper().createFormulaEvaluator().evaluate(cell).getNumberValue());
						break;
					case NUMERIC:
						input.addCell(new Vec2i(cell.getAddress().getColumn(), cell.getRowIndex()),
								"" + cell.getNumericCellValue());
						break;
					case STRING:
						input.addCell(new Vec2i(cell.getAddress().getColumn(), cell.getRowIndex()),
								cell.getStringCellValue());
						break;
					case _NONE:
						input.addCell(new Vec2i(cell.getAddress().getColumn(), cell.getRowIndex()), "");
						break;
					default:
						input.addCell(new Vec2i(cell.getAddress().getColumn(), cell.getRowIndex()), "");
						break;
					}
				}
			}
			dataContent.add(input);
		}
		inputStream.close();
		w.close();

		return dataContent;
	}

	private static InputStream getInput(String name) {
		InputStream output;
		output = ExcelImporter.class.getClassLoader().getResourceAsStream(name);

		if (output == null) {
			try {
				output = new FileInputStream(new File(name));
			} catch (FileNotFoundException e) {
				References.LOGGER.log(Level.SEVERE, "", e);
			}
		}

		return output;
	}
}