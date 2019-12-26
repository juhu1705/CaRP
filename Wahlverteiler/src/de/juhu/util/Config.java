package de.juhu.util;

import java.util.logging.Level;

import de.juhu.dateimanager.ConfigElement;

/**
 * Diese Klasse enthält alle Konfigurationsattribute des Kurs- und
 * Facharbeitszuweisers
 * 
 * @author Fabius
 * @category Config
 * @version 2.3
 */
public class Config {

	@ConfigElement(defaultValue = "OFF", elementClass = Level.class, description = "loglevel.description", name = "loglevel.text")
	public static Level maxPrintLevel = Level.ALL;

	@ConfigElement(defaultValue = "", elementClass = String.class, description = "inputfile.description", name = "inputfile.text")
	public static String inputFile;

	@ConfigElement(defaultValue = ".xlsx", elementClass = String.class, description = "outputfiletype.description", name = "outputfiletype.text")
	public static String outputFileType = ".xlsx";

	@ConfigElement(defaultValue = "%localappdata%\\Local\\CaRP", elementClass = String.class, description = "outputdirectory.description", name = "outputdirectory.text")
	public static String outputFile;
	static {
		outputFile = (System.getenv("localappdata") + "\\CaRP\\");
	}

	@ConfigElement(defaultValue = "@PJK", elementClass = String.class, description = "ignoremark.description", name = "ignoremark.text")
	public static String ignoreStudent;

	@ConfigElement(defaultValue = "@Course", elementClass = String.class, description = "coursemark.description", name = "coursemark.text")
	public static String newCourse;

	@ConfigElement(defaultValue = "Student", elementClass = String.class, description = "studentmark.description", name = "studentmark.text")
	public static String newStudent;

	@ConfigElement(defaultValue = "#", elementClass = String.class, description = "commentmark.description", name = "commentmark.text")
	public static String commentLine;

	@ConfigElement(defaultValue = "ALL", elementClass = String.class, description = "printformat.description", name = "printformat.text")
	public static String printFormat;

	@ConfigElement(defaultValue = "false", elementClass = Boolean.class, description = "shortnames.description", name = "shortnames.text")
	public static boolean shortNames;

	@ConfigElement(defaultValue = "false", elementClass = Boolean.class, description = "firstprename.description", name = "firstprename.text")
	public static boolean firstPrename;

	@ConfigElement(defaultValue = "false", elementClass = Boolean.class, description = "usenewgoodness.description", name = "usenewgoodness.text")
	public static boolean useNewGoodness = false;

	@ConfigElement(defaultValue = "true", elementClass = Boolean.class, description = "newimproving.description", name = "newimproving.text")
	public static boolean newImproving = true;

	@ConfigElement(defaultValue = "true", elementClass = Boolean.class, description = "clearcalculationdata.description", name = "clearcalculationdata.text")
	public static boolean clear;

	@ConfigElement(defaultValue = "100", elementClass = Integer.class, description = "runcount.description", name = "runcount.text")
	public static int runs = 100;

	@ConfigElement(defaultValue = "5", elementClass = Integer.class, description = "newcalculating.description", name = "newcalculating.text")
	public static int newCalculating = 5;

	@ConfigElement(defaultValue = "5", elementClass = Integer.class, description = "improvecalculation.description", name = "improvecalculation.text")
	public static int improvingOfCalculation = 5;

	@ConfigElement(defaultValue = "3", elementClass = Integer.class, description = "coosemaximum.description", name = "coosemaximum.text")
	public static int maxChooses = 3;

	@ConfigElement(defaultValue = "3", elementClass = Integer.class, description = "studentlimit.description", name = "studentlimit.text")
	public static int normalStudentLimit = 3;

	@ConfigElement(defaultValue = "3", elementClass = Integer.class, description = "rateindex.description", name = "rateindex.text")
	public static int powValue = 3;

}
