package de.juhu.util;

import java.util.logging.Level;

import de.juhu.dateimanager.ConfigElement;

public class Config {

	@ConfigElement(defaultValue = "OFF", elementClass = Level.class, description = "What kind of messages are printed in the log.", name = "Log Level")
	public static Level maxPrintLevel = Level.ALL;

	@ConfigElement(defaultValue = "", elementClass = String.class, description = "The filename of the input file.", name = "Input File")
	public static String inputFile;

	@ConfigElement(defaultValue = ".xlsx", elementClass = String.class, description = "Sets the ending of the output file.", name = "Ending of Outputfile")
	public static String outputFileType = ".xlsx";

//	@ConfigElement(defaultValue = "config.cfg", elementClass = String.class, description = "The location of the config file.", name = "Config File")
//	public static String configFile;

	@ConfigElement(defaultValue = "%localappdata%\\Local\\CaRP", elementClass = String.class, description = "The directory of the output file.", name = "Output Directory")
	public static String outputFile;
	static {
		outputFile = (System.getenv("localappdata") + "\\CaRP\\");
	}

	@ConfigElement(defaultValue = "@PJK", elementClass = String.class, description = "The mark for the programm to ignore the student with it as course.", name = "Ignore Mark")
	public static String ignoreStudent;

	@ConfigElement(defaultValue = "@Course", elementClass = String.class, description = "The mark to handle the readed line as Course.", name = "Course Mark")
	public static String newCourse;

	@ConfigElement(defaultValue = "Student", elementClass = String.class, description = "The mark to handle the readed line as Student.", name = "Student Mark")
	public static String newStudent;

	@ConfigElement(defaultValue = "#", elementClass = String.class, description = "The mark to handle the readed line as comment.", name = "Comment Mark")
	public static String commentLine;

	@ConfigElement(defaultValue = "ALL", elementClass = String.class, description = "The format to display the console with.", name = "Print Format")
	public static String printFormat;

	@ConfigElement(defaultValue = "false", elementClass = Boolean.class, description = "If the students are outputet only with the first letter of there first name.", name = "Short names")
	public static boolean shortNames;

	@ConfigElement(defaultValue = "false", elementClass = Boolean.class, description = "", name = "First prename")
	public static boolean firstPrename;

	@ConfigElement(defaultValue = "false", elementClass = Boolean.class, description = "", name = "Sort out")
	public static boolean sortUnallocatedFirstOut;

	@ConfigElement(defaultValue = "false", elementClass = Boolean.class, description = "No funktion yet (placeholder)", name = "NO FUNKTION")
	public static boolean complexHandling;

	@ConfigElement(defaultValue = "false", elementClass = Boolean.class, description = "", name = "Compare First Priorities")
	public static boolean compareFirstPriority;

	@ConfigElement(defaultValue = "true", elementClass = Boolean.class, description = "", name = "Compare First Priorities")
	public static boolean compareGuete = true;

	@ConfigElement(defaultValue = "true", elementClass = Boolean.class, description = "", name = "Compare First Priorities")
	public static boolean newImproving = true;

	@ConfigElement(defaultValue = "true", elementClass = Boolean.class, description = "If all courses and students are reseted, when importing a new file.", name = "Clear Data by importing new File")
	public static boolean clear;

	@ConfigElement(defaultValue = "100", elementClass = Integer.class, description = "The amount of sorting tries in one run.", name = "Run Count")
	public static int runs = 100;

	@ConfigElement(defaultValue = "5", elementClass = Integer.class, description = "How often the first part of one run progress of the calculation is repeated.", name = "Calculation 1 Count")
	public static int newCalculating = 5;

	@ConfigElement(defaultValue = "5", elementClass = Integer.class, description = "How often the second part of one run progress of the calculation is repeated.", name = "Calculation 2 Count")
	public static int improvingOfCalculation = 5;

	@ConfigElement(defaultValue = "3", elementClass = Integer.class, description = "The maximum of courses a student could choose.", name = "Choose Maximum")
	public static int maxChooses = 3;

	@ConfigElement(defaultValue = "3", elementClass = Integer.class, description = "The maximum amount of students that should be in one course.", name = "Student Limit")
	public static int normalStudentLimit = 3;

	@ConfigElement(defaultValue = "3", elementClass = Integer.class, description = "How often the pow was used on the priority to get the students rate.", name = "Rate Index")
	public static int powValue = 3;

}
