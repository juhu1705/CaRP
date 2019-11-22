package de.juhu.gui.models;

import java.util.HashMap;

public class TableModel {

	private static HashMap<String, TableModel> tableModels = new HashMap<>();

	public static TableModel getInstance(String id) {
		TableModel tm = tableModels.get(id);
		if (tm == null)
			tableModels.put(id, tm = new TableModel());

		return tm;
	}

}
