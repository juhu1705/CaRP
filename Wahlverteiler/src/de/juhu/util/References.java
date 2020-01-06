package de.juhu.util;

import static java.util.logging.Level.ALL;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.PropertyResourceBundle;
import java.util.Random;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

public class References {

	public static StreamHandler sh;
	public static LoggingFormatter lf;

	public static PropertyResourceBundle language;

	public static final LoggingHandler LOGGING_HANDLER;
	static {
		LOGGING_HANDLER = new LoggingHandler();
	}

	public static final String HOME_FOLDER;
	static {
		HOME_FOLDER = System.getProperty("user.home") + "/.CaRP/";
	}

	public static final Logger LOGGER;
	static {
		LOGGER = Logger.getLogger("Wahlverteiler");
		LOGGER.setUseParentHandlers(false);
		Handler handler = LOGGING_HANDLER;
		handler.setFormatter(lf = new LoggingFormatter());
		handler.setLevel(ALL);
		LOGGER.addHandler(handler);
		LOGGER.setLevel(ALL);
	}

	public static final String VERSION;
	static {
		MavenXpp3Reader reader = new MavenXpp3Reader();
		Model model = null;

		try {
			if ((new File("pom.xml")).exists())
				model = reader.read(new FileReader("pom.xml"));
			else
				model = reader.read(new InputStreamReader(References.class
						.getResourceAsStream("/META-INF/maven/de.juhu/Course_and_Research_Paper-Assigner/pom.xml")));
		} catch (FileNotFoundException e) {

		} catch (IOException e) {

		} catch (XmlPullParserException e) {

		}

		if (model == null)
			VERSION = "0.1.0";
		else
			VERSION = model.getVersion();

		// VERSION = "Snapshot-0.0.1";
	}

	public static final String PROJECT_NAME;
	static {
		MavenXpp3Reader reader = new MavenXpp3Reader();
		Model model = null;
		try {
			if ((new File("pom.xml")).exists())
				model = reader.read(new FileReader("pom.xml"));
			else
				model = reader.read(new InputStreamReader(References.class
						.getResourceAsStream("/META-INF/maven/de.juhu/Course_and_Research_Paper-Assigner/pom.xml")));
		} catch (FileNotFoundException e) {

		} catch (IOException e) {

		} catch (XmlPullParserException e) {

		}
		if (model == null)
			PROJECT_NAME = "CaRP Assigner";
		else
			PROJECT_NAME = model.getName();
		// PROJECT_NAME = "KuFa Zuweiser";
	}

	/**
	 * Zufallsgenerator
	 */
	public static final Random RAND_GEN = new Random();

}
