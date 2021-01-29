package de.juhu.config;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Diese Annotation verifiziert ein Attribut, als Konfigurationsattribut. Dabei
 * muss das Attribut {@code public static}, also öffentlich und statisch sein.
 * Das Config Elements wird über die Klasse {@link ConfigManager Konfigurations
 * Manager} registriert. Konfigurationsattribute, die ein {@code String},
 * {@code Integer}, oder {@code Boolean} als Wert aufweisen, werden automatisch
 * im GUI unter dem Reiter Einstellungen zu finden sein. Dabei ist
 * {@link #description() die Beschreibung} als Hovertext und {@link #name() der
 * Name} als Benennung eingefügt. Hierbei werden diese beiden eingegebenen
 * Strings durch den String aus der verwendeten Sprachdatei ersetzt. Alle
 * registrierten Konfigurationselemente werden in der Config-Datei unter
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
	 * @return Den Standartmäßig gesetzte Initialwert.
	 * @implNote Nur Strings werden automatisch richtig initialisiert. Bitte den
	 *           gewünschten Wert standartmäßig einprogrammieren. Dieser wird
	 *           überschrieben, sobald die Konfigurationsdatei geladen wird.
	 */
	public String defaultValue();

	/**
	 * @return Den Objekttypen dieser Klasse. Wenn primäre Datentypen wie int
	 *         benutzt werden, dann kann hier die Klasse Integer.class verwendet
	 *         werden.
	 */
	public Class elementClass();

	/**
	 * @return Den in den Sprachdateien hinterlegten Übersetzungsstring für die
	 *         Beschreibung der Konfiguration
	 * @implNote Der hinterlegte String muss, damit dass Programm läuft, in den
	 *           Sprachdateien hinterlegt sein.
	 */
	public String description();

	/**
	 * @return Den in den Sprachdateien hinterlegten Key zum Übersetzten des Namens.
	 * @implNote Der hinterlegte String muss, damit dass Programm läuft in den
	 *           Sprachdateien hinterlegt sein.
	 */
	public String name();

	/**
	 * @return Die Position im Baumsystem, unter der die Config zu finden ist.
	 * @implNote Der hinterlegte String muss, damit dass Programm läuft in den
	 *           Sprachdateien hinterlegt sein. Ein "." trennt die Strings. Jeder
	 *           Einzelstring wird mit "String".location gesucht.
	 */
	public String location();

}