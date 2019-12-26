package de.juhu.dateimanager;

import static de.juhu.util.References.LOGGER;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.logging.Level;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Hier werden alle {@link ConfigElement Konfigurations Elemente} gespeichert
 * und verwaltet. Diese Klasse ermöglicht das Laden der initialisierten Werte,
 * einer Konfigurations-Datei, sowie das Schreiben einer Konfigurationsdatei.
 * 
 * @author Fabius
 * @category Config
 * @version 1.0
 */
public class ConfigManager {

	/**
	 * Die Instanz des {@link ConfigManager Konfigurations Managers}.
	 */
	private static ConfigManager instance;

	/**
	 * @return Die aktive Instanz des Konfigurations-Managers.
	 */
	public static ConfigManager getInstance() {
		return instance == null ? instance = new ConfigManager() : instance;
	}

	/**
	 * Alle {@link ConfigElement Konfigurations-Elemente}.
	 */
	private ArrayList<Field> fields = new ArrayList<>();

	/**
	 * Gibt das {@code Field} was für den
	 * 
	 * @param name Die Bennenung des {@link ConfigElement Konfigurations-Element}
	 * @return Das gleichnamige {@link ConfigElement Konfigurations-Element}, oder
	 *         {@code null}, falls keines Vorhanden.
	 */
	public Field getField(String name) {
		for (Field f : fields) {
			if (f.getName().equals(name))
				return f;
		}
		return null;
	}

	public void register(Field f) throws Exception {
		if (f.getAnnotation(ConfigElement.class) == null)
			throw new IOException("Not the right annotation argument.");
		fields.add(f);
	}

	public void register(Class c) {
		for (Field f : c.getFields()) {
			try {
				this.register(f);
			} catch (Exception e) {
			}
		}
	}

	public void loadDefault() {
		this.fields.forEach(r -> {
			boolean a = r.isAccessible();
			r.setAccessible(true);
			try {
				if (r.get(this) == null) {
					ConfigElement e = r.getAnnotation(ConfigElement.class);
					String dv = e.defaultValue();

					if (e.elementClass().equals(Level.class))
						r.set(this, Level.parse(dv));
					else if (e.elementClass().equals(Boolean.class))
						r.set(this, Boolean.parseBoolean(dv));
					else if (e.elementClass().equals(Integer.class))
						r.set(this, Integer.parseInt(dv));
					else if (e.elementClass().equals(String.class))
						r.set(this, dv);
					else
						r.set(this, e.elementClass().cast(dv));
				}
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			r.setAccessible(a);
		});
	}

	public void load(String input) {
		try {
			XMLReader xmlReader = XMLReaderFactory.createXMLReader();
			InputSource inputSource = new InputSource(new FileReader(input));

			xmlReader.setContentHandler(new FieldHandler());
			xmlReader.parse(inputSource);
		} catch (SAXException e) {

		} catch (FileNotFoundException e) {

		} catch (IOException e) {

		}
	}

	public void save(File output) {
		FileWriter fw;
		BufferedWriter bw;

		try {
			fw = new FileWriter(output);
			bw = new BufferedWriter(fw);

			bw.append("<config>");
			bw.newLine();
			bw.append(" <fields>");
			bw.newLine();

			fields.forEach(e -> {
				try {
					bw.append("  <field>");
					bw.newLine();

					bw.append("   <parameter>");
					bw.newLine();

					bw.append("    <name>" + e.getName() + "</name>");
					bw.newLine();
					bw.append("    <value>" + e.get(this) + "</value>");
					bw.newLine();
					bw.append("    <default>" + e.getAnnotation(ConfigElement.class).defaultValue() + "</default>");
					bw.newLine();
					bw.append("    <type>" + e.getAnnotation(ConfigElement.class).elementClass() + "</type>");
					bw.newLine();

					bw.append("   </parameter>");
					bw.newLine();

					bw.append("  </field>");
					bw.newLine();
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (IllegalArgumentException e1) {
					e1.printStackTrace();
				} catch (IllegalAccessException e1) {
					e1.printStackTrace();
				}
			});

			bw.append(" </fields>");
			bw.newLine();
			bw.append("</config>");

			bw.close();
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Fehler beim Erstellen der Config datei!", e);
			return;
		}
	}

	public static boolean ignore(String line) {
		return line.replaceAll(" ", "").startsWith("#");
	}

	public static boolean open(String line) {
		return line.replaceAll(" ", "").startsWith("<") && line.replaceAll(" ", "").endsWith(">");
	}

	public static boolean close(String line) {
		return line.replaceAll(" ", "").startsWith("</") && line.replaceAll(" ", "").endsWith(">");
	}

}
