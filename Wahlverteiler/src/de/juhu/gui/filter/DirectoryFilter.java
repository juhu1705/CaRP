package de.juhu.gui.filter;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class DirectoryFilter extends FileFilter {

	private static DirectoryFilter instance;

	public static DirectoryFilter getInstance() {
		return instance == null ? instance = new DirectoryFilter() : instance;
	}

	protected DirectoryFilter() {

	}

	@Override
	public boolean accept(File f) {
		if (f.isDirectory())
			return true;
		return false;
	}

	@Override
	public String getDescription() {
		return "Directories Only";
	}

}
