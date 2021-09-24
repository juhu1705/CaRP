package de.juhu.util;

import java.nio.file.Paths;
import java.util.*;

import de.noisruker.event.EventManager;
import de.noisruker.logger.Settings;
import de.noisruker.logger.events.LogReceivedMessageEvent;
import javafx.application.Platform;
import javafx.scene.CacheHint;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
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
	public static final TextArea LOGGING_AREA;
	static {
		LOGGING_AREA = new TextArea();
		LOGGING_AREA.setWrapText(true);
		LOGGING_AREA.setEditable(false);
		LOGGING_AREA.setCacheHint(CacheHint.SPEED);
		VBox.setVgrow(LOGGING_AREA, Priority.ALWAYS);
		LOGGING_AREA.setMaxHeight(Double.MAX_VALUE);
		EventManager.getInstance().registerEventListener(LogReceivedMessageEvent.class, event -> Platform.runLater(() -> LOGGING_AREA.appendText(event.getConsoleMessage())));
	}
	public static final List<JMetro> OTHER_PAGES_WITH_THEMES = new ArrayList<>();
	public static final String THEME_IMPROVEMENTS = Objects.requireNonNull(References.class.getResource("/assets/styles/theme.css")).toExternalForm();
	public static final String DARK_THEME_FIXES = Objects.requireNonNull(References.class.getResource("/assets/styles/dark.css")).toExternalForm();

	/**
	 * Der Zufallsgenerator
	 */
	public static final Random RAND_GEN = new Random();

}
