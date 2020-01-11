package de.juhu.distributor;

/**
 * Ein Leser ist f�r das Einlesen einer Tabellenzeile verantwortlich. Er wird
 * �ber die Methode {@link Distributor#addReader(Reader)} im aktuellen Verteiler
 * registriert und dann beim Einlesen einer Datei verwendet. Dabei wird die
 * Methode {@link #read(String[], int)} ausgef�hrt, sobald die Zeile �ber den
 * {@link #key Schl�ssel} des Lesers verf�gt.
 * 
 * @version 1.0
 * @category Distribution
 * @author Juhu1705
 * @since 0.1.0
 *
 */
public abstract class Reader {

	/**
	 * Der Schl�ssel dieses Lesers, auf den dieser Leser beim Einlesen anspringen
	 * soll.
	 */
	String key;

	/**
	 * Erzeugt einen neuen Leser mit dem gew�nschten Schl�ssel
	 * 
	 * @param key Der Schl�ssel des neuen Lesers.
	 */
	public Reader(String key) {
		this.key = key;
	}

	/**
	 * �berpr�ft, ob der gegebene Schl�ssel dem Schl�ssel dieses Lesers entspricht.
	 * 
	 * @param key Der Schl�ssel, der vergichen werden soll.
	 * @return Ob der Schl�ssel diesem Schl�ssel entspricht. Zum Vergleichen wird
	 *         die Methode {@link String#equalsIgnoreCase(String)} verwendet.
	 */
	public boolean isKey(String key) {
		return this.key.equalsIgnoreCase(key);
	}

	/**
	 * Lie�t die entsprechende Zeile ein.
	 * 
	 * @param line       Die Zeile die eingelesen werden soll.
	 * @param lineNumber Die Zeilennummer der Zeile im Programm.
	 * @implNote Die Zeile die eingelesen werden soll besitzt nun nicht mehr ihren
	 *           Schl�ssel, es beginnt direkt mit der ersten Argumentzelle dieser
	 *           Zeile.
	 */
	public abstract void read(String[] line, int lineNumber);

}
