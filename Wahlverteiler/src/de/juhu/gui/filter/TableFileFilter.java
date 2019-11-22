package de.juhu.gui.filter;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class TableFileFilter extends FileFilter {

	private static TableFileFilter instance;

	public static TableFileFilter getInstance() {
		return instance == null ? instance = new TableFileFilter() : instance;
	}

	protected TableFileFilter() {

	}

	@Override
	public String getDescription() {
		return "Table (*.csv, *.xls, *.xlsx)";
	}

	@Override
	public boolean accept(File f) {
		if (f.isDirectory())
			return true;
		else {
			String filename = f.getName().toLowerCase();
			return filename.endsWith(".csv") || filename.endsWith(".xls") || filename.endsWith(".xlsx");
		}
	}
}
