package de.juhu.dateimanager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.logging.Level;

import de.juhu.util.References;

public class CSVImporter {

	public static WriteableContent readCSV(String pathfile) throws IOException, URISyntaxException {
		WriteableContent writeable = new WriteableContent();

		InputStreamReader fileReader = new InputStreamReader(getInput(pathfile), "UTF8");
		BufferedReader reader = new BufferedReader(fileReader);

		String zeile = "";
		int y = 0, x;

		while (reader.ready() && (zeile = reader.readLine()) != null) {
			x = 0;

			String parameter = "";
			for (char c : zeile.toCharArray()) {
				if (c == ';') {
					writeable.addCell(new Vec2i(x++, y), parameter);
					parameter = "";
				} else
					parameter += c;
			}
			writeable.addCell(new Vec2i(x++, y), parameter);
			y++;
		}

		reader.close();

		return writeable;
	}

	private static InputStream getInput(String name) throws URISyntaxException, FileNotFoundException {
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
