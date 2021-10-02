package de.juhu.util;

import de.noisruker.event.EventManager;
import de.noisruker.logger.Settings;
import de.noisruker.logger.events.LogReceivedErrorEvent;
import de.noisruker.logger.events.LogReceivedMessageEvent;
import javafx.application.Platform;
import javafx.scene.CacheHint;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.*;

/**
 * Beinhaltet alle Referenzobjekte
 *
 * @author Juhu1705
 * @category Util
 */
public class References {

    /**
     * Der Speicherpfad des Carp-Assigners
     */
    public static final String HOME_FOLDER;
    /**
     * Der Versionsnummer, wie sie in der pom.xml Datei angegeben wurde.
     */
    public static final String VERSION;
    /**
     * Der Projektname, wie er in der pom.xml Datei angegeben wurde.
     */
    public static final String PROJECT_NAME;
    public static final JMetro J_METRO = new JMetro(Style.DARK);
    public static final TextArea LOGGING_AREA;
    public static final List<JMetro> OTHER_PAGES_WITH_THEMES = new ArrayList<>();
    public static final String THEME_IMPROVEMENTS = Objects.requireNonNull(References.class.getResource("/assets/styles/theme.css")).toExternalForm();
    public static final String DARK_THEME_FIXES = Objects.requireNonNull(References.class.getResource("/assets/styles/dark.css")).toExternalForm();
    /**
     * Der Zufallsgenerator
     */
    public static final Random RAND_GEN = new Random();
    /**
     * Die Sprachdatei, die Momentan benutzt wird.
     */
    public static PropertyResourceBundle language;

    static {
        HOME_FOLDER = Paths.get(Settings.HOME_FOLDER, Settings.PROGRAMM_FOLDER).toString();
    }

    static {
        String version = null;
        String name = null;

        InputStream inputStream = References.class.getClassLoader().getResourceAsStream("/version.properties");

        if(inputStream == null) {
            try {
                inputStream = new FileInputStream("bin/main/version.properties");
            } catch (FileNotFoundException ignored) {}
        }
        if(inputStream != null) {
            Properties versionProperties = new Properties();
            try {
                versionProperties.load(inputStream);
                version = versionProperties.getProperty("version", "No Version");
                name = versionProperties.getProperty("name", "CaRP-Assigner");
            } catch (IOException ignored) {}
        }
        if(version == null)
            version = "No Version";
        if(name == null)
            name = "CaRP-Assigner";

        VERSION = version;
        PROJECT_NAME = name;
    }

    static {
        LOGGING_AREA = new TextArea();
        LOGGING_AREA.setWrapText(true);
        LOGGING_AREA.setEditable(false);
        LOGGING_AREA.setCacheHint(CacheHint.SPEED);
        VBox.setVgrow(LOGGING_AREA, Priority.ALWAYS);
        LOGGING_AREA.setMaxHeight(Double.MAX_VALUE);
        EventManager.getInstance().registerEventListener(LogReceivedMessageEvent.class, event -> Platform.runLater(() -> LOGGING_AREA.appendText(event.getConsoleMessage())));
        EventManager.getInstance().registerEventListener(LogReceivedErrorEvent.class, event -> Platform.runLater(() -> LOGGING_AREA.appendText(event.getConsoleMessage())));
    }

}
