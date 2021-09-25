package de.juhu.util;

import de.juhu.guiFX.GUILoader;
import de.juhu.guiFX.GUIManager;
import de.juhu.util.events.*;
import de.noisruker.config.ConfigElement;
import de.noisruker.config.ConfigManager;
import de.noisruker.config.event.ConfigEntryChangeEvent;
import de.noisruker.event.EventManager;
import de.noisruker.logger.PrintFormat;
import de.noisruker.logger.Settings;
import javafx.application.Platform;
import javafx.scene.Scene;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.logging.Level;

import static de.noisruker.config.ConfigElementType.*;
import static de.noisruker.logger.Logger.LOGGER;

/**
 * Diese Klasse enthält alle Konfigurationsvariablen.
 *
 * @author Juhu1705
 * @version 2.3
 * @category Config
 */
public class Config {

    @ConfigElement(defaultValue = "OFF", type = TEXT, description = "loglevel.description", name = "loglevel.text", location = "config", visible = false)
    public static String maxPrintLevel = Level.ALL.toString();

    @ConfigElement(defaultValue = "", type = TEXT, description = "inputfile.description", name = "inputfile.text", location = "config.import", visible = false)
    public static String inputFile;

    @ConfigElement(defaultValue = ".xlsx", type = CHOOSE, description = "outputfiletype.description", name = "outputfiletype.text", location = "config.export", visible = true)
    public static String outputFileType = ".xlsx";

    @ConfigElement(defaultValue = "user.home\\Local\\.CaRP", type = TEXT, description = "outputdirectory.description", name = "outputdirectory.text", location = "config.export", visible = false)
    public static String outputFile;
    @ConfigElement(defaultValue = "PJK", type = TEXT, description = "ignoremark.description", name = "ignoremark.text", location = "config.import", visible = true)
    public static String ignoreStudent;
    @ConfigElement(defaultValue = "Kurs", type = TEXT, description = "coursemark.description", name = "coursemark.text", location = "config.import", visible = true)
    public static String newCourse;
    @ConfigElement(defaultValue = "Schüler", type = TEXT, description = "studentmark.description", name = "studentmark.text", location = "config.import", visible = true)
    public static String newStudent;
    @ConfigElement(defaultValue = "#", type = TEXT, description = "commentmark.description", name = "commentmark.text", location = "config.import", visible = true)
    public static String commentLine;
    @ConfigElement(defaultValue = "ALL", type = TEXT, description = "printformat.description", name = "printformat.text", location = "config", visible = false)
    public static String printFormat;
    @ConfigElement(defaultValue = "Lehrerliste", type = TEXT, description = "courseHeader.description", name = "courseHeader.text", location = "config.export", visible = true)
    public static String courseHeader;
    @ConfigElement(defaultValue = "Schülerliste", type = TEXT, description = "studentHeader.description", name = "studentHeader.text", location = "config.export", visible = true)
    public static String studentHeader;
    @ConfigElement(defaultValue = "false", type = CHECK, description = "hasHeaderOutput.description", name = "hasHeaderOutput.text", location = "config.export", visible = true)
    public static boolean hasHeaderOutput = true;
    @ConfigElement(defaultValue = "false", type = CHECK, description = "shouldMaximize.description", name = "shouldMaximize.text", location = "config", visible = true)
    public static boolean shouldMaximize = false;
    @ConfigElement(defaultValue = "false", type = CHECK, description = "shouldImport.description", name = "shouldImport.text", location = "config.import", visible = true)
    public static boolean shouldImportAutomatic = false;
    @ConfigElement(defaultValue = "false", type = CHECK, description = "shortnames.description", name = "shortnames.text", location = "config.export", visible = true)
    public static boolean shortNames;
    @ConfigElement(defaultValue = "true", type = CHECK, description = "firstprename.description", name = "firstprename.text", location = "config.export", visible = true)
    public static boolean firstPrename;
    @ConfigElement(defaultValue = "true", type = CHECK, description = "usenewgoodness.description", name = "usenewgoodness.text", location = "config.calculation", visible = true)
    public static boolean useNewGoodness = true;
    @ConfigElement(defaultValue = "true", type = CHECK, description = "clearcalculationdata.description", name = "clearcalculationdata.text", location = "config.import", visible = true)
    public static boolean clear = true;

//	@ConfigElement(defaultValue = "true", type = CHECK, description = "newimproving.description", name = "newimproving.text", location = "config.calculation", visible = true)
//	public static boolean newImproving = true;
    @ConfigElement(defaultValue = "false", type = CHECK, description = "dontask.description", name = "dontask.text", location = "config.import", visible = true)
    public static boolean rememberDecision = false;
    @ConfigElement(defaultValue = "true", type = CHECK, description = "allowDoubles.description", name = "allowDoubles.text", location = "config.import", visible = true)
    public static boolean allowDoubleStudents = true;
    @ConfigElement(defaultValue = "100", type = COUNT, description = "runcount.description", name = "runcount.text", location = "config.calculation", visible = true)
    public static int runs = 100;
    @ConfigElement(defaultValue = "2", type = COUNT, description = "addForUnallocatedStudents.description", name = "addForUnallocatedStudents.text", location = "config.calculation", visible = true)
    public static int addForUnallocatedStudents = 2;
    @ConfigElement(defaultValue = "5", type = COUNT, description = "newcalculating.description", name = "newcalculating.text", location = "config.calculation", visible = true)
    public static int newCalculating = 5;
    @ConfigElement(defaultValue = "5", type = COUNT, description = "improvecalculation.description", name = "improvecalculation.text", location = "config.calculation", visible = true)
    public static int improvingOfCalculation = 5;
    @ConfigElement(defaultValue = "3", type = COUNT, description = "coosemaximum.description", name = "coosemaximum.text", location = "config.import", visible = true)
    public static int maxChooses = 3;
    @ConfigElement(defaultValue = "3", type = COUNT, description = "studentlimit.description", name = "studentlimit.text", location = "config.import", visible = true)
    public static int normalStudentLimit = 3;
    @ConfigElement(defaultValue = "3", type = COUNT, description = "rateindex.description", name = "rateindex.text", location = "config.calculation", visible = true)
    public static int powValue = 3;
    @ConfigElement(defaultValue = "GERMAN", type = CHOOSE, description = "language.description", name = "language.text", location = "config", visible = true)
    public static String language;
    @ConfigElement(defaultValue = "DARK", type = CHOOSE, description = "theme.description", name = "theme.text", location = "config", visible = true)
    public static String theme;

    static {
        outputFile = (References.HOME_FOLDER);
    }

    public static void register() {
        LOGGER.info("Reading config");
        try {
            ConfigManager.getInstance().register(Config.class);

            EventManager.getInstance().registerEventListener(LanguageRegisterEvent.class, event -> {
                event.registerNewLanguage("GERMAN");
                event.registerNewLanguage("ENGLISH");
            });

            EventManager.getInstance().registerEventListener(ThemeRegisterEvent.class, event -> {
                event.registerNewTheme("DARK");
                event.registerNewTheme("LIGHT");
            });

            EventManager.getInstance().registerEventListener(LanguageLocationSearchEvent.class, event -> {
                switch (event.getLanguageName()) {
                    case "GERMAN":
                        event.setLocation("/assets/language/de.properties");
                        break;
                    case "ENGLISH":
                        event.setLocation("/assets/language/en.properties");
                        break;
                }
            });

            EventManager.getInstance().registerEventListener(WindowUpdateEvent.class, event -> {
                Scene s = event.getScene();

                if ("DARK".equals(Config.theme) || "LIGHT".equals(Config.theme)) {
                    References.J_METRO.setScene(s);

                    s.getStylesheets().add(References.THEME_IMPROVEMENTS);

                    if ("DARK".equals(Config.theme))
                        s.getStylesheets().add(References.DARK_THEME_FIXES);
                }
            });

            EventManager.getInstance().registerEventListener(WindowUpdateEvent.class, event -> {
                Scene s = event.getScene();

                if ("DARK".equals(Config.theme) || "LIGHT".equals(Config.theme)) {
                    References.J_METRO.setScene(s);

                    s.getStylesheets().add(References.THEME_IMPROVEMENTS);

                    if ("DARK".equals(Config.theme))
                        s.getStylesheets().add(References.DARK_THEME_FIXES);
                }
            });

            EventManager.getInstance().registerEventListener(WindowCreatedEvent.class, event -> {
                Scene s = event.getScene();

                if ("DARK".equals(Config.theme) || "LIGHT".equals(Config.theme)) {
                    JMetro theme = new JMetro(s, "DARK".equals(Config.theme) ? Style.DARK : Style.LIGHT);
                    References.OTHER_PAGES_WITH_THEMES.add(theme);
                    event.getStage().setOnCloseRequest(event2 -> References.OTHER_PAGES_WITH_THEMES.remove(theme));

                    if ("DARK".equals(Config.theme))
                        s.getStylesheets().add(References.DARK_THEME_FIXES);

                    s.getStylesheets().add(References.THEME_IMPROVEMENTS);
                }
            });

            Object out = EventManager.getInstance().triggerEvent(new LanguageRegisterEvent());
            if (out instanceof List) {
                Object[] values = ((List<?>) out).toArray();
                String[] languages = new String[values.length];

                for (int i = 0, i1 = 0; i < values.length; i++) {
                    if (values[i] instanceof String)
                        languages[i1++] = (String) values[i];
                }
                ConfigManager.getInstance().registerOptionParameters("language.text", languages);
            }

            ConfigManager.getInstance().registerOptionParameters("outputfiletype.text", ".xlsx", ".xls", ".csv", "all");

            out = EventManager.getInstance().triggerEvent(new ThemeRegisterEvent());
            if (out instanceof List) {
                Object[] values = ((List<?>) out).toArray();
                String[] themes = new String[values.length];

                for (int i = 0, i1 = 0; i < values.length; i++) {
                    if (values[i] instanceof String)
                        themes[i1++] = (String) values[i];
                }
                ConfigManager.getInstance().registerOptionParameters("theme.text", themes);
            }

            EventManager.getInstance().registerEventListener(ConfigEntryChangeEvent.class, event -> {
                if (event.getEntryName().equals("language.text")) {
                    try {
                        InputStreamReader r;

                        Object location = EventManager.getInstance().triggerEvent(new LanguageLocationSearchEvent(event.getEntryValue()));

                        if (location == null)
                            location = EventManager.getInstance().triggerEvent(new LanguageLocationSearchEvent("GERMAN"));
                        InputStream stream = GUILoader.class.getResourceAsStream((String) location);

                        if (stream == null) {
                            LOGGER.log(Level.WARNING, "The requested language could not be loaded", new Exception("Language not found"));
                            return;
                        }

                        References.language = new PropertyResourceBundle(r = new InputStreamReader(stream, StandardCharsets.UTF_8));
                        r.close();
                    } catch (Exception e) {
                        LOGGER.log(Level.WARNING, "The requested language could not be loaded", e);
                    }
                    if (GUILoader.getPrimaryStage() != null && GUIManager.getInstance() != null) {
                        Platform.runLater(() -> Util.updateWindow(GUILoader.getPrimaryStage(), "/assets/layouts/GUI.fxml"));
                    }
                } else if ("theme.text".equals(event.getEntryName()) && ("DARK".equals(Config.theme) || "LIGHT".equals(Config.theme))) {
                    References.J_METRO.setStyle("DARK".equals(Config.theme) ? Style.DARK : Style.LIGHT);
                    if (!"DARK".equals(theme) && References.J_METRO.getScene() != null)
                        while (References.J_METRO.getScene().getStylesheets().contains(References.DARK_THEME_FIXES))
                            References.J_METRO.getScene().getStylesheets().remove(References.DARK_THEME_FIXES);
                    if ("DARK".equals(Config.theme) && References.J_METRO.getScene() != null && !References.J_METRO.getScene().getStylesheets().contains(References.DARK_THEME_FIXES))
                        References.J_METRO.getScene().getStylesheets().add(References.DARK_THEME_FIXES);
                } else if ("loglevel.text".equals(event.getEntryName())) {
                    LOGGER.setLevel(Level.parse(event.getEntryValue()));
                } else if ("printformat.text".equals(event.getEntryName())) {
                    Settings.PRINT_FORMAT = PrintFormat.valueOf(event.getEntryValue());
                }
            });

            if (!Files.exists(FileSystems.getDefault().getPath(References.HOME_FOLDER + "config.cfg"),
                    LinkOption.NOFOLLOW_LINKS)) {
                ConfigManager.getInstance().loadDefault();
            } else {
                ConfigManager.getInstance().load(References.HOME_FOLDER + "config.cfg");
            }
        } catch (IOException | SAXException e4) {
            LOGGER.log(Level.SEVERE, "Error while register Configuration Elements", e4);
        }

    }

}
