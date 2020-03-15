package de.juhu.util;

import java.util.logging.Level;

import de.juhu.config.ConfigElement;

/**
 * Diese Klasse enthält alle Konfigurationsvariablen.
 * 
 * @author Juhu1705
 * @category Config
 * @version 2.3
 */
public class Config {

	@ConfigElement(defaultValue = "OFF", elementClass = Level.class, description = "loglevel.description", name = "loglevel.text", location = "config")
	public static Level maxPrintLevel = Level.ALL;

	@ConfigElement(defaultValue = "", elementClass = String.class, description = "inputfile.description", name = "inputfile.text", location = "config.import")
	public static String inputFile;

	@ConfigElement(defaultValue = ".xlsx", elementClass = String.class, description = "outputfiletype.description", name = "outputfiletype.text", location = "config.export")
	public static String outputFileType = ".xlsx";

	@ConfigElement(defaultValue = "%localappdata%\\Local\\CaRP", elementClass = String.class, description = "outputdirectory.description", name = "outputdirectory.text", location = "config.export")
	public static String outputFile;
	static {
		outputFile = (System.getenv("localappdata") + "\\CaRP\\");
	}

	@ConfigElement(defaultValue = "@PJK", elementClass = String.class, description = "ignoremark.description", name = "ignoremark.text", location = "config.import")
	public static String ignoreStudent;

	@ConfigElement(defaultValue = "@Course", elementClass = String.class, description = "coursemark.description", name = "coursemark.text", location = "config.import")
	public static String newCourse;

	@ConfigElement(defaultValue = "Student", elementClass = String.class, description = "studentmark.description", name = "studentmark.text", location = "config.import")
	public static String newStudent;

	@ConfigElement(defaultValue = "#", elementClass = String.class, description = "commentmark.description", name = "commentmark.text", location = "config.import")
	public static String commentLine;

	@ConfigElement(defaultValue = "ALL", elementClass = String.class, description = "printformat.description", name = "printformat.text", location = "config")
	public static String printFormat;

	@ConfigElement(defaultValue = "false", elementClass = Boolean.class, description = "shouldMaximize.description", name = "shouldMaximize.text", location = "config")
	public static boolean shouldMaximize = false;

	@ConfigElement(defaultValue = "false", elementClass = Boolean.class, description = "shortnames.description", name = "shortnames.text", location = "config.export")
	public static boolean shortNames;

	@ConfigElement(defaultValue = "true", elementClass = Boolean.class, description = "firstprename.description", name = "firstprename.text", location = "config.export")
	public static boolean firstPrename;

	@ConfigElement(defaultValue = "true", elementClass = Boolean.class, description = "usenewgoodness.description", name = "usenewgoodness.text", location = "config.calculation")
	public static boolean useNewGoodness = true;

//	@ConfigElement(defaultValue = "true", elementClass = Boolean.class, description = "newimproving.description", name = "newimproving.text", location = "config.calculation")
//	public static boolean newImproving = true;

	@ConfigElement(defaultValue = "true", elementClass = Boolean.class, description = "clearcalculationdata.description", name = "clearcalculationdata.text", location = "config.import")
	public static boolean clear = true;

	@ConfigElement(defaultValue = "false", elementClass = Boolean.class, description = "dontask.description", name = "dontask.text", location = "config.import")
	public static boolean rememberDecision = false;

	@ConfigElement(defaultValue = "true", elementClass = Boolean.class, description = "allowDoubles.description", name = "allowDoubles.text", location = "config.import")
	public static boolean allowDoubleStudents = true;

	@ConfigElement(defaultValue = "100", elementClass = Integer.class, description = "runcount.description", name = "runcount.text", location = "config.calculation")
	public static int runs = 100;

	@ConfigElement(defaultValue = "5", elementClass = Integer.class, description = "newcalculating.description", name = "newcalculating.text", location = "config.calculation")
	public static int newCalculating = 5;

	@ConfigElement(defaultValue = "5", elementClass = Integer.class, description = "improvecalculation.description", name = "improvecalculation.text", location = "config.calculation")
	public static int improvingOfCalculation = 5;

	@ConfigElement(defaultValue = "3", elementClass = Integer.class, description = "coosemaximum.description", name = "coosemaximum.text", location = "config.import")
	public static int maxChooses = 3;

	@ConfigElement(defaultValue = "3", elementClass = Integer.class, description = "studentlimit.description", name = "studentlimit.text", location = "config.import")
	public static int normalStudentLimit = 3;

	@ConfigElement(defaultValue = "3", elementClass = Integer.class, description = "rateindex.description", name = "rateindex.text", location = "config.calculation")
	public static int powValue = 3;

}
