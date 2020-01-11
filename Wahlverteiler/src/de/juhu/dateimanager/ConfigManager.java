package de.juhu.dateimanager;

import static de.juhu.util.References.LOGGER;

import java.io.BufferedWriter;
import java.io.File;
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
 * @author Juhu1705
 * @category Config
 * @version 1.0
 */
public class ConfigManager {

	/**
	 * Die Instanz des {@link ConfigManager Konfigurations Managers}.
	 */
	private static ConfigManager instance;

	/**
	 * @return Die {@link #instance aktive Instanz} des Konfigurations-Managers.
	 */
	public static ConfigManager getInstance() {
		return instance == null ? instance = new ConfigManager() : instance;
	}

	/**
	 * Alle {@link ConfigElement Konfigurations-Elemente}, die registriert wurden.
	 */
	private ArrayList<Field> fields = new ArrayList<>();

	/**
	 * Gibt das erste {@code Field} aus {@link #fields der Liste aller
	 * Konfigurations-Elemente} aus, dessen Name mit dem mitgegebenen {@link String}
	 * übereinstimmt zurück.
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

	/**
	 * Fügt das zu registrierende {@link ConfigElement} in {@link #fields die Liste
	 * aller Konfigurations-Elemente} ein, wenn es über die Annotation
	 * {@link ConfigElement} verfügt.
	 * 
	 * 
	 * @param configElement Das zu registrierende {@link ConfigElement}
	 * @throws IOException Sollte die Datei, die zu registrieren versucht wird,
	 *                     nicht die Annotation {@link ConfigElement} besitzen, wird
	 *                     eine IOException mit der Nachricht: "Not the right
	 *                     annotation argument.", ausgegeben.
	 */
	public void register(Field configElement) throws IOException {
		if (configElement.getAnnotation(ConfigElement.class) == null)
			throw new IOException("Not the right annotation argument.");
		fields.add(configElement);
	}

	/**
	 * Registriert alle {@link Field Felder} der {@link Class Klasse}, die über die
	 * Annotation {@link ConfigElement} verfügen über die Methode
	 * {@link #register(Field)}.
	 * 
	 * @param c Die {@link Class Klasse}, deren {@link Field Felder}, welche die
	 *          Annotation {@link ConfigElement} tragen, registriert werden sollen.
	 * @throws IOException Sollte ein Fehler beim Registrieren der Felder auftreten.
	 */
	public void register(Class c) throws IOException {
		for (Field f : c.getFields()) {

			if (f.getAnnotation(ConfigElement.class) != null)
				this.register(f);

		}
	}

	/**
	 * Lädt die unter {@link ConfigElement#defaultValue() dem Standartwert}
	 * mitgegebenen Werte in die jeweiligen Felder, sollten diese nicht
	 * standartmäßig über einen Wert verfügen.
	 * 
	 * @implNote Diese Methode Funktioniert nur bedingt. Daher ist es ratsam, die
	 *           Felder direkt zu initialisieren.
	 */
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

	/**
	 * <p>
	 * Lädt eine Konfigurations Datei ein. Dabei werden die {@link #fields Elemente
	 * aus der Liste aller Konfigurations-Elemente} auf den für sie vermerkten Wert
	 * gesetzt.
	 * </p>
	 * 
	 * <p>
	 * Die Konfigurationsdatei ist in {@code XML} zu schreiben. Dabei umschließt der
	 * Parameter {@code config} die gesammte Konfigurationsdatei. Unter dem
	 * Parameter {@code fields} können mithilfe des Parameters {@code field} und dem
	 * folgenden Parameter {@code parameter} die einzelnen Konfigurationswerte
	 * gesetzt werden. Dabei ist {@code name} der Name des zu setztenden Feldes.
	 * {@code value} gibt den Wert an, auf den es gesetzt wird. Unter
	 * {@code default} kann der Standartwert als orientierung angegeben werden und
	 * unter {@code type} ist die Klasse vermerkt, in welche das {@code value}
	 * konvertiert wird.
	 * </p>
	 * 
	 * @param input Der Pfad zu der einzulesenden Datei.
	 * @throws SAXException Sollte ein Fehler in der Struktur der Datei vorliegen.
	 * @throws IOException  Sollte ein Fehler beim Einlesen der Datei aufgtreten
	 */
	public void load(String input) throws SAXException, IOException {

		XMLReader xmlReader = XMLReaderFactory.createXMLReader();
		InputSource inputSource = new InputSource(new FileReader(input));

		xmlReader.setContentHandler(new FieldHandler());
		xmlReader.parse(inputSource);

	}

	/**
	 * Exportiert die Daten der {@link #fields Elemente aus der Liste aller
	 * Konfigurations-Elemente} in eine Datei nach dem unter {@link #load(String)}
	 * erklärten Aufbau. Diese Datei ist von der Methode {@link #load(String)}
	 * wieder einlesbar.
	 * 
	 * @param output Der Pfad zu dem Exportiert wird.
	 * @throws IOException Sollte es nicht möglixh sein an den angegebenen Pfad zu
	 *                     Schreiben, oder sollte output gleich {@code null} sein
	 */
	public void save(File output) throws IOException {
		if (output == null)
			throw new IOException("No file to write to!");

		FileWriter fw;
		BufferedWriter bw;

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
			} catch (IllegalArgumentException | IOException | IllegalAccessException e1) {
				LOGGER.log(Level.SEVERE, "Fehler beim Erstellen der Config datei!", e1);
			}
		});

		bw.append(" </fields>");
		bw.newLine();
		bw.append("</config>");

		bw.close();

	}

}
