package de.juhu.distributor;

/**
 * Ein Leser ist für das Einlesen einer Tabellenzeile verantwortlich. Er wird
 * über die Methode {@link Distributor#addReader(Reader)} im aktuellen Verteiler
 * registriert und dann beim Einlesen einer Datei verwendet. Dabei wird die
 * Methode {@link #read(String[], int)} ausgeführt, sobald die Zeile über den
 * {@link #key Schlüssel} des Lesers verfügt.
 *
 * @author Juhu1705
 * @version 1.0
 * @category Distribution
 * @since 0.1.0
 */
public abstract class Reader {

    /**
     * Der Schlüssel dieses Lesers, auf den dieser Leser beim Einlesen anspringen
     * soll.
     */
    String key;

    /**
     * Erzeugt einen neuen Leser mit dem gewünschten Schlüssel
     *
     * @param key Der Schlüssel des neuen Lesers.
     */
    public Reader(String key) {
        this.key = key;
    }

    /**
     * Überprüft, ob der gegebene Schlüssel dem Schlüssel dieses Lesers entspricht.
     *
     * @param key Der Schlüssel, der vergichen werden soll.
     * @return Ob der Schlüssel diesem Schlüssel entspricht. Zum Vergleichen wird
     * die Methode {@link String#equalsIgnoreCase(String)} verwendet.
     */
    public boolean isKey(String key) {
        return this.key.equalsIgnoreCase(key);
    }

    /**
     * Ließt die entsprechende Zeile ein.
     *
     * @param line       Die Zeile die eingelesen werden soll.
     * @param lineNumber Die Zeilennummer der Zeile im Programm.
     * @implNote Die Zeile die eingelesen werden soll besitzt nun nicht mehr ihren
     * Schlüssel, es beginnt direkt mit der ersten Argumentzelle dieser
     * Zeile.
     */
    public abstract void read(String[] line, int lineNumber);

}
