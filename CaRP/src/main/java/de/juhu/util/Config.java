package de.juhu.util;

import de.noisruker.config.ConfigElement;
import de.noisruker.config.ConfigManager;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.util.logging.Level;

import static de.noisruker.config.ConfigElementType.*;
import static de.noisruker.logger.Logger.LOGGER;

/**
 * Diese Klasse enthält alle Konfigurationsvariablen.
 * 
 * @author Juhu1705
 * @category Config
 * @version 2.3
 */
public class Config {

	@ConfigElement(defaultValue = "OFF", type = TEXT, description = "loglevel.description", name = "loglevel.text", location = "config", visible = false)
	public static String maxPrintLevel = Level.ALL.toString();

	@ConfigElement(defaultValue = "", type = TEXT, description = "inputfile.description", name = "inputfile.text", location = "config.import", visible = false)
	public static String inputFile;

	@ConfigElement(defaultValue = ".xlsx", type = TEXT, description = "outputfiletype.description", name = "outputfiletype.text", location = "config.export", visible = false)
	public static String outputFileType = ".xlsx";

	@ConfigElement(defaultValue = "user.home\\Local\\.CaRP", type = TEXT, description = "outputdirectory.description", name = "outputdirectory.text", location = "config.export", visible = false)
	public static String outputFile;
	static {
		outputFile = (References.HOME_FOLDER);
	}

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

//	@ConfigElement(defaultValue = "true", type = CHECK, description = "newimproving.description", name = "newimproving.text", location = "config.calculation", visible = true)
//	public static boolean newImproving = true;

	@ConfigElement(defaultValue = "true", type = CHECK, description = "clearcalculationdata.description", name = "clearcalculationdata.text", location = "config.import", visible = true)
	public static boolean clear = true;

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

	static {
		try {
			ConfigManager.getInstance().register(Config.class);
		} catch (IOException e4) {
			LOGGER.log(Level.SEVERE, "Error while register Configuration Elements", e4);
		}
		if (!Files.exists(FileSystems.getDefault().getPath(References.HOME_FOLDER + "config.cfg"),
				LinkOption.NOFOLLOW_LINKS)) {
			ConfigManager.getInstance().loadDefault();
		}
		else {
			try {
				ConfigManager.getInstance().load(References.HOME_FOLDER + "config.cfg");
			} catch (SAXException | IOException e4) {
				LOGGER.log(Level.SEVERE, "Error while loading Config", e4);
			}
		}
	}

}
