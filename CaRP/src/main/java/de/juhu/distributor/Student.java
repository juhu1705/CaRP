package de.juhu.distributor;

import de.juhu.util.Config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Diese Klasse bildet einen Schüler ab und beinhaltet alle diesbezüglich
 * wichtigen Informationen.
 *
 * @author Juhu1705
 * @version 1.1
 * @category Distribution
 * @implNote Ist {@link Comparable vergleichbar} mit anderen {@link Student
 * Schülern}.
 */
public class Student implements Comparable<Student>, Serializable {

    // INFO: Attribute

    /**
     * Speichert alle vom {@link Student Schüler} gewählten {@link Course Kurse}
     * nach ihrer Priorität. An Position 0 steht der Kurs mit der geringsten
     * Priorität.
     */
    private final ArrayList<Course> courses = new ArrayList<>();
    /**
     * <p>
     * Die Priorität die dem {@link #activeCourse aktiven Kurs} nach der
     * {@link #courses Liste der Kurse} zugeordnet ist.
     * </p>
     * Kann über {@link #refreshPriority()} aktualisiert werden.
     */
    protected int priority;
    /**
     * Der Kurs, in dem sich der Schüler momentan befindet. Ist {@code null}, wenn
     * der Schüler in keinem Kurs ist.
     */
    private Course activeCourse;
    /**
     * Die ID des Schülers. Sie ist für jeden unterschiedlichen Schüler einzigartig.
     */
    private int id;

    /**
     * Der Nachname des Schülers.
     */
    private String name;

    /**
     * Der Vorname des Schülers.
     */
    private String prename;

    /**
     * Ein Indikator, der anzeigt, ob dieser Schüler einem Kurs zugeordnet ist.
     */
    private boolean mark;

    // INFO: Konstruktoren

    /**
     * Erzeugt einen Schüler mit einer neuen ID, der noch keinen Namen besitzt.
     */
    protected Student() {
        this.generateID();
        this.unmark();
    }

    /**
     * Erzeugt einen neuen Schüler, mit einer neuen ID. Der Name und der Vorname
     * werden gespeichert und die mitgegebenen Kurse werden in gleicher Reihenfolge
     * in die {@link #courses Liste der gewünschten Kurse} gespeichert. Der erste
     * Kurs der Liste wird dabei zum {@link #activeCourse aktiven Kurs} des
     * Schülers.
     *
     * @param name    Der Nachname des Schülers
     * @param prename Der Vorname des Schülers
     * @param courses Die Kurswünsche des Schülers
     */
    public Student(String name, String prename, Course... courses) {
        this();
        for (Course courseName : courses) {
            this.addCourse(courseName);
        }

        if (this.courses.size() == 1 && this.courses.get(0).equals(Distributor.getInstance().ignore()))
            this.activeCourse = this.courses.get(0);

        this.setName(name);
        this.setPrename(prename);
        this.priority = 0;
    }

    /**
     * Erstellt einen Schüler mit dem mitgegebenen Vor- und Nachnamen, sowie der
     * mitgegebenen ID. Nur zum Kopieren verwendet. Kurse müssen manuell eingefügt,
     * oder beim kopieren synchronisiert werden. Hierzu eignet sich die Methode
     * {@link Distributor#copyData(ArrayList, ArrayList, Course)}
     *
     * @param name    Der Nachname des Schülers
     * @param prename Der Vorname des Schülers
     * @param id      Die ID des Schülers
     */
    private Student(String name, String prename, int id) {
        this.setName(name);
        this.setPrename(prename);
        this.priority = 0;
        this.id = id;
        this.unmark();
    }

    // INFO: Methoden

    /**
     * Generiert eine ID für den Schüler. Wird immer beim erstellen eines Schülers
     * aufgerufen. Greift auf die Methode {@link Distributor#getStudentID()} zurück
     * um eine ID zu generieren und setzt die ID auf den von dieser Methode
     * zurückgegebenen Wert.
     */
    private void generateID() {
        this.id = Distributor.getStudentID();
    }

    /**
     * <p>
     * Setzt den {@link #activeCourse aktive Kurs} auf den nächsten noch freien
     * {@link Course Kurs} aus der {@link #courses Liste der gewünschten Kurse} des
     * Schülers.
     * </p>
     * <ul>
     * <li>Dazu wird zunächst geguckt, ob der Schüler {@link #courses gewünschten
     * Kurse} besitzt. Sollte dies nicht der Fall sein, so wird {@code false}
     * zurückgegeben, der Prioritätswert durch Aufruf der Methode
     * {@link #refreshPriority()} aktualisiert und die Methode beendet.</li>
     *
     * <li>Danach wird geschaut, ob der {@link #activeCourse aktive Kurs} momentan
     * den Wert {@code null} beinhaltet. Sollte dies der Fall sein, so wird der
     * aktive Kurs, auf den ersten Kurs aus der Liste der {@link #courses
     * gewünschten Kurse} gesetzt und der Schüler dem Kurs hinzugefügt.</li>
     *
     * <li>Im folgenden wird in einer {@code while}-Schleife geschaut, ob der aktive
     * Kurs bereits mit seiner maximalen Schüleranzahl besetzt ist. Wenn ja, so wird
     * der nächste Kurs aus der {@link #courses Liste der gewünschten Kurse}
     * ermittelt und bei diesem das gleiche ermittelt. Sollten alle Kurse die der
     * Schüler sich wünscht an dieser Stelle voll sein, so wird die Methode mit dem
     * Rückgabewert {@code false} abgebrochen, sowie der {@link #activeCourse aktive
     * Kurs} auf {@code null} gesetzt.</li>
     *
     * <li>Sollte ein Kurs gefunden werden, der noch frei ist, so wird dieser
     * Schüler der Liste der Schüler des Kurses hinzugefügt.
     * Anschließend wird der {@link #priority Prioritätswert} des Schülers durch die
     * Methode {@link #refreshPriority()} aktualisiert. Dann wird die Methode mit
     * der Rückgabe {@code true} beendet.</li>
     * </ul>
     *
     * @return Ob ein neuer Kurs zugewiesen werden konnte. Wenn {@code false}
     * zurückgegeben wird, so ist der {@link #activeCourse aktive Kurs} dann
     * {@code null}.
     */
    public boolean next() {
        if (this.courses.isEmpty())
            return false;

        if (this.activeCourse == null) {
            this.activeCourse = this.courses.get(0);
            this.activeCourse.addStudent(this);
        }

        while (this.activeCourse.isFull()) {
            this.setNextCourse();
            if (this.activeCourse == null) {
                this.refreshPriority();
                return false;
            }
        }

        this.refreshPriority();
        return true;
    }

    /**
     * Setzt den {@link #activeCourse Aktiven Kurs} auf den nächsten Kurs aus der
     * {@link #courses Liste der gewünschten Kurse} des Schülers.
     *
     * @return Ob der Schüler einem Kurs zugeordnet werden konnte.
     * @implNote Die Methode fügt einen Schüler auch zu einem Bereits vollen Kurs
     * hinzu und lässt ihn dort verweilen.
     * @see #next() Hier ist die Funktion genau beschrieben. Anstatt der
     * {@code while}-Schleife wird hier lediglich ein {@code if}-Case genutzt,
     * der mit einer {@code else} Bedingung mit der vorgeschalteten Überprüfung
     * verbunden ist. Ansonsten sind die Methoden identisch aufgebaut.
     */
    public boolean onlyNext() {
        if (this.courses.isEmpty())
            return false;

        if (this.activeCourse == null) {
            this.activeCourse = this.courses.get(0);
            this.activeCourse.addStudent(this);
        } else if (this.activeCourse.isFull()) {
            this.setNextCourse();
            if (this.activeCourse == null) {
                this.refreshPriority();
                return false;
            }
        }

        this.refreshPriority();
        return true;
    }

    // INFO: Getter und Setter

    /**
     * Gibt den {@link #name Nachnamen} des Schülers zurück.
     *
     * @return Der Nachname des Schülers.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Setzt den {@link #name Nachnamen} des Schülers auf den mitgegebenen String.
     *
     * @param name Den Nachnamen, den der Schüler annehmen soll.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gibt den {@link #prename Vornamen} des Schülers zurück.
     *
     * @return Der Vorname des Schülers.
     */
    public String getPrename() {
        return this.prename;
    }

    /**
     * Setzt den {@link #prename Vornamen} des Schülers auf den mitgegebenen String.
     *
     * @param prename Den Vornamen, den der Schüler annehmen soll.
     */
    public void setPrename(String prename) {
        this.prename = prename;
    }

    /**
     * Fügt einen Kurs ans Ende der {@link #courses Liste der gewünschten Kurse} des
     * Schülers hinzu.
     * <p>
     * Wenn der Kurs den Wert {@code null} darstellt, so wird dieser Ignoriert.
     *
     * @param course Der Kurs, der hinzugefügt werden soll.
     */
    public void addCourse(Course course) {
        if (course == null)
            return;
        this.courses.remove(course);

        this.courses.add(course);
    }

    /**
     * Fügt einen Kurs an der gewünschten Stelle zu den {@link #courses
     * Wunschkursen} hinzu. Dabei werden die nachfolgenden Kurse und der Kurs an
     * diesem Index um einen Listenplatz nach hinten verschoben.
     *
     * @param index  Der Listenplatz, an den der Kurs eingefügt werden soll.
     * @param course Der Kurs, der eingefügt werden soll.
     * @throws IndexOutOfBoundsException Wenn der Index nicht innerhalb der Liste
     *                                   liegt.
     */
    public void addCourse(int index, Course course) throws IndexOutOfBoundsException {
        if (course == null)
            return;
        this.courses.remove(course);

        this.courses.add(index, course);
    }

    /**
     * Gibt die Liste aller {@link #courses gewünschten Kurse} des Schülers zurück.
     *
     * @return Ein Array der gewünschten Kurse des Schülers.
     */
    public Course[] getCourses() {
        return this.courses.toArray(new Course[this.courses.size()]);
    }

    /**
     * Fügt die mitgegebene Kurse in gleicher Reihenfolge ans Ende der
     * {@link #courses Liste der Wunschkurse} des Schülers an.
     *
     * @param c Die Kurse sie eingefügt werden sollen.
     */
    public void setCourses(Course... c) {
        this.courses.clear();

        for (Course course : c)
            this.addCourse(course);
    }

    /**
     * Setzt den {@link #activeCourse aktiven Kurs} auf den nächsten Kurs aus der
     * {@link #courses Liste der gewünschten Kurse}.
     *
     * @return Der neu gesetzte {@link #activeCourse Kurs}.
     */
    private Course setNextCourse() {
        return this.setCourse(this.getNextCourse());
    }

    /**
     * Setzt den {@link #activeCourse aktiven Kurs} auf den gewünschten Kurs. Dabei
     * wird der letzte aktive Kurs in einen entfernt.
     *
     * @param course Der Kurs der zum aktiven Kurs werden soll.
     * @return Der aktive Kurs.
     */
    private Course setCourse(Course course) {
        if (this.activeCourse.contains(this))
            this.activeCourse.removeStudent(this);

        if (course == null)
            return this.activeCourse = null;

        this.activeCourse = course;
        this.activeCourse.addStudent(this);

        return course;
    }

    /**
     * Gibt denjenigen Kurs aus der {@link #courses Liste der gewünschten Kurse des
     * Schülers} zurück, welcher dem {@link #activeCourse aktiven Kurs} folgt.
     *
     * @return Der dem aktiven Kurs folgende Kurs.
     * @see #getNextCourse(Course, int)
     * getNextCourse({@link #activeCourse}{@link #getNextCourse(Course, int) ,
     * 1)}
     */
    public Course getNextCourse() {
        return this.getNextCourse(this.activeCourse, 1);
    }

    /**
     * Gibt den Kurs zurück der iterator viele Positionen hinter dem
     * {@link #activeCourse aktiven Kurs} in der {@link #courses Liste der
     * gewünschten Kurse} steht.
     *
     * @param iterator Anzahl der Positionen die zwischen dem aktiven Kurs und dem
     *                 zu ermittelnden Kurs liegen sollen.
     * @return Den iterator-viele Positionen hinter dem aktiven Kurs liegende Kurs.
     * @see #getNextCourse(Course, int)
     * getNextCourse({@link #activeCourse}{@link #getNextCourse(Course, int) ,
     * iterator)}
     */
    public Course getNextCourse(int iterator) {
        return this.getNextCourse(this.activeCourse, iterator);
    }

    /**
     * Ermittelt den Kurs, der iterator viele Positionen hinter dem mitgegebenen
     * Kurs in der {@link #courses Liste der gewünschten Kurse} liegt.
     *
     * @param course   Der Kurs, von dem gezählt wird.
     * @param iterator Die Anzahl an Positionen, die vom mitgegebenen Kurs
     *                 weitergegangen werden soll.
     * @return Der Kurs der entsprechend viele Positionen hinter dem mitgegebenen
     * Kurs liegt. Gibt {@code null} zurück wenn der Kurs kein Element der
     * {@link #courses Liste der gewünschten Kurse} ist. Gibt den ersten
     * Kurs der Liste zurück wenn der mitgegebene Kurs {@code null} ist.
     */
    public Course getNextCourse(Course course, int iterator) {
        if (course == null)
            return this.courses.get(0);
        if (this.courses.contains(course)) {
            int i = this.courses.indexOf(course);
            if (i + iterator >= this.courses.size())
                return null;
            else
                return this.courses.get(i + iterator);
        }
        return null;
    }

    /**
     * Ermittelt den Index an dem der Kurs in der {@link #courses Liste der
     * gewünschten Kurse} steht multipliziert mit 2.
     *
     * @param course Der Kurs dessen Wert ermittelt werden soll
     * @return Der Index multipliziert mit 2, oder {@link Integer#MAX_VALUE}, wenn
     * der Kurs nicht in der {@link #courses Liste der gewünschten Kurse}
     * existiert.
     */
    public int getCourseAmount(Course course) {
        for (int i = 0; i < this.courses.size(); i++) {
            if (this.courses.get(i).equals(course)) {
                // References.LOGGER.info(this.toString() + ": " + i);
                return i * 2;
            }
        }
        return Integer.MAX_VALUE;
    }

    /**
     * @return Den {@link #activeCourse aktiven Kurs} des Schülers
     */
    public Course getActiveCourse() {
        return activeCourse;
    }

    /**
     * Setzt den aktiven Kurs des Schülers auf den gewünschten Kurs, wenn dieser in
     * der {@link #courses Liste der Wunschkurse} vorhanden ist. Danach sorgt die
     * Methode dafür, das die {@link #priority Priorität} des Kurses aktualisiert
     * wird.
     *
     * @param activeCourse Der Kurs, welcher zum aktiven Kurs gemacht werden soll.
     *                     Wenn er {@code null} entspricht wird der
     *                     {@link #activeCourse aktive Kurs} auf {@link null}
     *                     gesetzt.
     */
    public void setActiveCourse(Course activeCourse) {
        if (this.isMarked())
            this.unmark();

        if (activeCourse == null) {
            if (this.activeCourse != null && this.activeCourse.contains(this))
                this.activeCourse.removeStudent(this);
            this.activeCourse = activeCourse;
            this.refreshPriority();
            return;
        }

        if (this.courses.contains(activeCourse)) {
            if (this.isMarked())
                this.mark = false;
            if (this.activeCourse != null && this.activeCourse.contains(this))
                this.activeCourse.removeStudent(this);
            this.activeCourse = activeCourse;
            if (!this.activeCourse.contains(this))
                this.activeCourse.addStudent(this);
            this.refreshPriority();
        }
    }

    /**
     * Setzt den {@link #priority Prioritätswert} auf den, durch die Methode
     * {@link #calculatePriority()} ermittelten Wert.
     */
    public void refreshPriority() {
        this.priority = this.calculatePriority();
    }

    /**
     * Berechnet die Priorität die der {@link #activeCourse aktive Kurs} besitzt.
     *
     * @return Die Priorität die der aktive Kurs besitzt, oder
     * {@link Integer#MAX_VALUE} wenn der aktive Kurs {@code null} ist, oder
     * nicht in der {@link #courses Liste der Wunschkurse} existiert.
     */
    private int calculatePriority() {
        if (this.activeCourse == null)
            return Integer.MAX_VALUE;
        if (this.courses.contains(this.activeCourse))
            return (this.courses.indexOf(this.activeCourse) + 1);

        return Integer.MAX_VALUE;
    }

    /**
     * @return Gibt die zuletzt ermittelte {@link #priority Priorität} zurück.
     */
    public int getPriority() {
        return this.priority;
    }

    /**
     * Ermittelt die Rate, die dieser Schüler besitzt: {@link #priority Priorität}
     * hoch {@link Config#powValue}. Sollte die Priorität {@link Integer#MAX_VALUE}
     * entsprechen, wird die durch
     * {@link Distributor#getHighestPriorityWhithoutIntegerMax()} ermittelte
     * Priorität plus eins als Priorität angenommen.
     *
     * @return Die Rate des Schülers
     */
    public int getRate() {
        return (int) (this.priority == Integer.MAX_VALUE
                ? Math.pow(Distributor.getInstance().getHighestPriorityWhithoutIntegerMax() + 1, Config.powValue)
                : Math.pow(this.priority, Config.powValue));
    }

    /**
     * Ermittelt die Rate, die dieser Schüler besitzt: {@link #priority Priorität}
     * hoch {@link Config#powValue}. Sollte die Priorität {@link Integer#MAX_VALUE}
     * entsprechen, wird die durch highestPriority plus eins ersetzt.
     *
     * @param highestPriority Die höchste Priorität der Berechnung
     * @return Die Rate des Schülers
     */
    public int getRate(int highestPriority) {
        return (int) (this.priority == Integer.MAX_VALUE ? Math.pow(highestPriority + 1, Config.powValue + 3)
                : Math.pow(this.priority, Config.powValue));
    }

    /**
     * Setzt die {@link #mark Markierung} des Schülers auf {@code true}.
     */
    public void mark() {
        this.mark = true;
    }

    /**
     * Setzt die {@link #mark Markierung} des Schülers auf {@code false}.
     */
    public void unmark() {
        this.mark = false;
    }

    /**
     * @return Die {@link #mark Markierung} des Schülers.
     */
    public boolean isMarked() {
        return this.mark;
    }

    @Override
    public int compareTo(Student s) {
        int i = this.getName().compareToIgnoreCase(s.getName());

        if (i == 0)
            i = this.getPrename().compareToIgnoreCase(s.getPrename());

        return i;
    }

    /**
     * Schaut ob das zu vergleichende Objekt ein Schüler ist, wenn nicht wird der
     * Rückgabewert der {@link Object#equals(Object) super-Methode} zurückgegeben.
     * Zum vergleichen der beiden Schüler wird die {@link #id ID} der Schüler
     * miteinander verglichen.
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Student))
            return false;

        Student student = (Student) obj;

        return student.id == this.id;

    }

    /**
     * Gibt als representativen String den {@link #prename Vornamen} verbunden mit
     * dem {@link #name Nachnamen} zurück.
     */
    @Override
    public String toString() {
        return this.prename + " " + this.name;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        Student s = new Student(this.name, this.prename, this.id);
        return s;
    }

    /**
     * Der Index des Kurses in der {@link #courses Liste der Wunschkurse}
     *
     * @see {@link ArrayList#indexOf(Object)}
     */
    public int getPosition(Course c) {
        return this.courses.indexOf(c);
    }

    /**
     * Überprüft, ob die ID mit der ID dieses Schülers übereinstimmt.
     *
     * @param studentID Die ID, deren Übereinstimmung geprüft wird.
     * @return Ob die ID mit der {@link #id ID} dieses Schülers übereinstimmt.
     */
    public boolean idequals(int studentID) {
        return this.id == studentID;
    }

    /**
     * @return Die ID dieses Schülers
     */
    public int getID() {
        return this.id;
    }

    /**
     * @return Die {@link #courses Wunschkurse} dieses Schülers.
     */
    public List<Course> getCoursesAsList() {
        return this.courses;
    }

    /**
     * Aktualisiert die Marke des Schülers in Bezug auf dessen {@link #priority
     * Priorität}. Ist die {@link #priority Priorität} des Schülers kleiner 0, oder
     * größer als die highestPriority wird der Schüler {@link #mark() markiert}, ist
     * der Schüler {@link #mark markiert}, obwohl die vornestehenden Bedingungen
     * nicht erfüllt sind, wird die {@link #unmark() markierung zurückgezogen}.
     *
     * @param highestPriority Die höchste Priorität, die in dem zu vergleichenden
     *                        Prozess vorliegt.
     */
    public void checkMarkt(int highestPriority) {

        if (this.getPriority() > highestPriority || this.getPriority() < 0)
            this.mark();
        else if (this.mark)
            this.unmark();
    }
}
