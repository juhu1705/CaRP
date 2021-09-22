package de.juhu.util;

import java.nio.file.Paths;
import java.util.*;

import de.noisruker.logger.Settings;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;

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

	public static final JMetro J_METRO = new JMetro(Style.DARK);
	public static final List<JMetro> OTHER_PAGES_WITH_THEMES = new ArrayList<>();
	public static final String THEME_IMPROVEMENTS = Objects.requireNonNull(References.class.getResource("/assets/styles/theme.css")).toExternalForm();
	public static final String DARK_THEME_FIXES = Objects.requireNonNull(References.class.getResource("/assets/styles/dark.css")).toExternalForm();

	/**
	 * Der Zufallsgenerator
	 */
	public static final Random RAND_GEN = new Random();

}
