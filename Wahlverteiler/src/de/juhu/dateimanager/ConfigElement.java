package de.juhu.dateimanager;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Diese Annotation verifiziert ein Attribut, als Konfigurations Attribut. Dabei
 * muss das Attribut {@code public static}, also �ffentlich und statisch sein.
 * Das Config Elements wird �ber die Klasse {@link ConfigManager Konfigurations
 * Manager} registriert. Konfigurationsattribute, die ein {@code String},
 * {@code Integer}, oder {@code Boolean} als Wert aufweisen, werden automatisch
 * im GUI unter dem Reiter Einstellungen zu finden sein. Dabei ist
 * {@link #description() die Beschreibung} als Hovertext und {@link #name() der
 * Name} als Bennenung eingef�gt. Hierbei werden diese beiden eingegebenen
 * Strings durch den String aus der verwendeten Sprachdatei ersetzt. Alle
 * registrierten Konfigelements werden in der Config-Datei unter
 * "%localappdata%/CaRP/config.cfg" gespeichert.
 * 
 * @author Juhu1705
 * @category Config
 * @version 1.0
 */
@Retention(RUNTIME)
@Target(FIELD)
public @interface ConfigElement {

	/**
	 * @return Den Standartm��ig gesetzte Initialwert.
	 * @implNote Nur Strings werden Automatisch richtig initialisiert. Bitte den
	 *           gew�nschten Wert standartm��ig einprogrammieren. Dieser wird
	 *           �berschrieben, sobald die Konfigurationsdatei geladen wird.
	 */
	public String defaultValue();

	/**
	 * @return Den Objekttypen dieser Klasse. Wenn prim�re Datentypen wie int
	 *         benutzt werden, dann kann hier die Klasse Integer.class verwendet
	 *         werden.
	 */
	public Class elementClass();

	/**
	 * @return Den in den Sprachdateien hintelegten �bersetzungsstring f�r die
	 *         Beschreibung der Konfiguration
	 * @implNote Der hintelegte String muss, damit dass Programm l�uft in den
	 *           Sprachdateien hinterlegt sein.
	 */
	public String description();

	/**
	 * @return Den in den Sprachdateien hinterlegten Key zum �bersetzten des Namens.
	 * @implNote Der hintelegte String muss, damit dass Programm l�uft in den
	 *           Sprachdateien hinterlegt sein.
	 */
	public String name();

	/**
	 * @return Die Position im Baumsystem, unter der die Config zu finden ist.
	 * @implNote Der hintelegte String muss, damit dass Programm l�uft in den
	 *           Sprachdateien hinterlegt sein. Ein "." trennt die Strings. Jeder
	 *           einzelstring wird mit "String".location gesucht.
	 */
	public String location();

}
