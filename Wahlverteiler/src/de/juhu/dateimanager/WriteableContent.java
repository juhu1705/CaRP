package de.juhu.dateimanager;

import static de.juhu.util.References.LOGGER;
import static org.apache.poi.ss.usermodel.CellType.STRING;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import de.juhu.math.Vec2i;
import de.juhu.util.CellStyles;

public class WriteableContent {

	private HashMap<Vec2i, String> lines = new HashMap<Vec2i, String>();
	private String name;

	public WriteableContent() {
		this.name = "";
	}

	public WriteableContent(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public WriteableContent setName(String name) {
		this.name = name;
		return this;
	}

	public WriteableContent addCell(Vec2i position, String content) {
		lines.put(position, content);
		return this;
	}

	public WriteableContent removeCell(Vec2i position) {
		if (lines.containsKey(position))
			lines.remove(position);
		return this;
	}

	public WriteableContent addLine(Vec2i startPosition, String[] contents) {
		for (int x = startPosition.x, i = 0; i < contents.length; x++, i++) {
			lines.put(new Vec2i(x, startPosition.y), contents[i]);
		}
		return this;
	}

	public WriteableContent addGrid(Vec2i startPosition, String[][] grid) {
		for (int x = startPosition.x, i = 0; i < grid.length; x++, i++) {
			this.addLine(new Vec2i(x, startPosition.y), grid[i]);
		}
		return this;
	}

	public WriteableContent addLine(Vec2i startPosition, List<String> contents) {
		for (int x = startPosition.x, i = 0; i < contents.size(); x++, i++) {
			lines.put(new Vec2i(x, startPosition.y), contents.get(i));
		}
		return this;
	}

	public WriteableContent addListGrid(Vec2i startPosition, List<List<String>> contents) {
		for (int x = startPosition.x, i = 0; i < contents.size(); x++, i++) {
			this.addLine(new Vec2i(x, startPosition.y), contents.get(i));
		}
		return this;
	}

	public WriteableContent removeLine(Vec2i startPosition, int length) {
		for (int x = startPosition.x, i = 0; i < length; x++, i++) {
			lines.remove(new Vec2i(x, startPosition.y));
		}
		return this;
	}

	public String[][] getReverseGrid() {
		Vec2i maxLength = this.getMaxLength();
		String[][] cells = new String[maxLength.x + 1][maxLength.y + 1];
		for (Entry<Vec2i, String> cell : lines.entrySet()) {
			cells[cell.getKey().x][cell.getKey().y] = cell.getValue();
		}
		return cells;
	}

	public String[][] getGrid() {
		Vec2i maxLength = this.getMaxLength();
		String[][] cells = new String[maxLength.y + 1][maxLength.x + 1];
		for (Entry<Vec2i, String> cell : lines.entrySet()) {
			cells[cell.getKey().y][cell.getKey().x] = cell.getValue();
		}
		return cells;
	}

	public String getStringAt(Vec2i position) {
		return this.lines.get(position);
	}

	public void writeXLS(HSSFWorkbook workbook, HSSFSheet sheet, int startingLineY) {
		Vec2i maxLength = this.getMaxLength();
		String[][] cells = this.getReverseGrid();

		int rownumber = startingLineY + 1;

		Row row;

		Cell cell;

		HSSFCellStyle style = CellStyles.title(workbook);
		row = sheet.createRow(0);
		for (int x = 0; x <= maxLength.x; x++) {
			cell = row.createCell(x, STRING);
			cell.setCellValue(cells[x][0] == null ? "" : cells[x][0]);
			cell.setCellStyle(style);

		}

		style = CellStyles.normal2(workbook);
		row = sheet.createRow(1);
		for (int x = 0; x <= maxLength.x; x++) {
			cell = row.createCell(x, STRING);
			cell.setCellValue("");
			cell.setCellStyle(style);
		}

		for (int y = 1; y <= maxLength.y; y++) {
			if (y % 2 == 0) {
				style = CellStyles.normal2(workbook);
				row = sheet.createRow(rownumber + y);
				for (int x = 0; x <= maxLength.x; x++) {
					cell = row.createCell(x, STRING);
					cell.setCellValue(cells[x][y] == null ? "" : cells[x][y]);
					cell.setCellStyle(style);
				}
			} else {
				style = CellStyles.normal1(workbook);
				row = sheet.createRow(rownumber + y);
				for (int x = 0; x <= maxLength.x; x++) {
					cell = row.createCell(x, STRING);
					cell.setCellValue(cells[x][y] == null ? "" : cells[x][y]);
					cell.setCellStyle(style);
				}
			}
		}

		style = CellStyles.up(workbook);
		row = sheet.createRow(maxLength.y + 1 + rownumber);
		for (int x = 0; x <= maxLength.x; x++) {
			cell = row.createCell(x, STRING);
			cell.setCellValue("");
			cell.setCellStyle(style);
		}
	}

	public void writeCSV(BufferedWriter writer) {
		Vec2i maxLength = this.getMaxLength();
		String[][] cells = this.getReverseGrid();

		for (int y = 0; y <= maxLength.y; y++) {
			try {
				for (int x = 0; x <= maxLength.x; x++) {
					writer.append(cells[x][y] + ";");

				}
				writer.newLine();
			} catch (IOException e) {
				LOGGER.log(Level.SEVERE, "Failed to write CSV", e);
			}
		}
	}

	public void writeXLSX(XSSFWorkbook workbook, XSSFSheet sheet, int startingLineY) {
		Vec2i maxLength = this.getMaxLength();
		String[][] cells = this.getReverseGrid();

		int rownumber = startingLineY + 1;

		Row row;

		Cell cell;

		XSSFCellStyle style = CellStyles.title(workbook);
		row = sheet.createRow(0);
		for (int x = 0; x <= maxLength.x; x++) {
			cell = row.createCell(x, STRING);
			cell.setCellValue(cells[x][0] == null ? "" : cells[x][0]);
			cell.setCellStyle(style);

		}

		style = CellStyles.normal2(workbook);
		row = sheet.createRow(1);
		for (int x = 0; x <= maxLength.x; x++) {
			cell = row.createCell(x, STRING);
			cell.setCellValue("");
			cell.setCellStyle(style);
		}

		for (int y = 1; y <= maxLength.y; y++) {
			if (y % 2 == 0) {
				style = CellStyles.normal2(workbook);
				row = sheet.createRow(rownumber + y);
				for (int x = 0; x <= maxLength.x; x++) {
					cell = row.createCell(x, STRING);
					cell.setCellValue(cells[x][y] == null ? "" : cells[x][y]);
					cell.setCellStyle(style);

				}
			} else {
				style = CellStyles.normal1(workbook);
				row = sheet.createRow(rownumber + y);
				for (int x = 0; x <= maxLength.x; x++) {
					cell = row.createCell(x, STRING);
					cell.setCellValue(cells[x][y] == null ? "" : cells[x][y]);
					cell.setCellStyle(style);

				}
			}
		}
		style = CellStyles.up(workbook);
		row = sheet.createRow(maxLength.y + 1 + rownumber);
		for (int x = 0; x <= maxLength.x; x++) {
			cell = row.createCell(x, STRING);
			cell.setCellValue("");
			cell.setCellStyle(style);
		}
	}

	private Vec2i getMaxLength() {
		Vec2i maxLength = new Vec2i();
		for (Entry<Vec2i, String> cell : lines.entrySet()) {
			maxLength.x = cell.getKey().x > maxLength.x ? cell.getKey().x : maxLength.x;
			maxLength.y = cell.getKey().y > maxLength.y ? cell.getKey().y : maxLength.y;
		}
		return maxLength;
	}

}
