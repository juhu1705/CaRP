package de.juhu.util;

import java.nio.file.Paths;
import java.util.PropertyResourceBundle;
import java.util.Random;

import de.noisruker.logger.Settings;

/**
 * Beinhaltet alle Referenzobjekte
 * 
 * @author Juhu1705
 * @category Util
 */
public class References {

	/**
	 * Die Sprachdatei, die Momentan benutzt wird.
	 */
	public static PropertyResourceBundle language;

	/**
	 * Der Speicherpfad des Carp-Assigners
	 */
	public static final String HOME_FOLDER;
	static {
		HOME_FOLDER = Paths.get(Settings.HOME_FOLDER, Settings.PROGRAMM_FOLDER).toString();
	}

	/**
	 * Der Versionsnummer, wie sie in der pom.xml Datei angegeben wurde.
	 */
	public static final String VERSION;
	static {
		VERSION = "1.0.3";
	}

	/**
	 * Der Projektname, wie er in der pom.xml Datei angegeben wurde.
	 */
	public static final String PROJECT_NAME;
	static {
		PROJECT_NAME = "CaRP Assigner";
	}

	/**
	 * Der Zufallsgenerator
	 */
	public static final Random RAND_GEN = new Random();

}
