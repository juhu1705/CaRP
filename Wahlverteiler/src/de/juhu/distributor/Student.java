package de.juhu.distributor;

import java.util.ArrayList;
import java.util.List;

import de.juhu.util.Config;

/**
 * Diese Klasse bildet einen Schüler ab und beinhaltet alle diesbezüglich
 * wichtigen Informationen.
 * 
 * @version 1.0
 * @category Distribution
 * @author Juhu1705
 * @implNote Ist {@link Comparable vergleichbar} mit anderen {@link Student
 *           Schülern}.
 */
public class Student implements Comparable<Student> {

	// INFO: Attribute

	/**
	 * Speichert alle vom {@link Student Schüler} gewählten {@link Course Kurse}
	 * nach ihrer Priorität. An Position 0 steht der Kurs mit der geringsten
	 * Priorität.
	 */
	private ArrayList<Course> courses = new ArrayList<>();

	/**
	 * Der Kurs, in dem sich der Schüler momentan befindet. Ist {@code null}, wenn
	 * der Schüler in keinem Kurs ist.
	 */
	private Course activeCourse;

	/**
	 * <p>
	 * Die Priorität die dem {@link #activeCourse aktiven Kurs} nach der
	 * {@link #courses Liste der Kurse} zugeordnet ist.
	 * </p>
	 * Kann über {@link #refreshPriority()} aktualisiert werden.
	 */
	private int priority;

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
	 * Ein Indikator, der anzeigt, ob diesem Schüler ein Kurs zugeordnet werden
	 * konnte.
	 */
	private boolean mark;

	// INFO: Konstruktoren

	public Student() {
		this.generateID();
		this.unmark();
	}

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
	 * Schüler der {@link Course#students Liste der Schüler des Kurses} hinzugefügt.
	 * Anschließend wird der {@link #priority Prioritätswert} des Schülers durch die
	 * Methode {@link #refreshPriority()} aktualisiert. Dann wird die Methode mit
	 * der Rückgabe {@code true} beendet.</li>
	 * </ul>
	 * 
	 * @return Ob ein neuer Kurs zugewiesen werden konnte. Wenn {@code false}
	 *         zurückgegeben wird, so ist der {@link #activeCourse aktive Kurs} dann
	 *         {@code null}.
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
	 * @see #next() Hier ist die Funktion genau beschrieben. Anstatt der
	 *      {@code while}-Schleife wird hier lediglich ein {@code if}-Case genutzt,
	 *      der mit einer {@code else} Bedingung mit der vorgeschalteten Überprüfung
	 *      verbunden ist. Ansonsten sind die Methoden identisch aufgebaut.
	 * @implNote Die Methode fügt einen Schüler auch zu einem Bereits vollen Kurs
	 *           hinzu und lässt ihn dort verweilen.
	 * @return Ob der Schüler einem Kurs zugeordnet werden konnte.
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
	 * Setzt den {@link #name Nachnamen} des Schülers auf den mitgegebenen String.
	 * 
	 * @param name Den Nachnamen, den der Schüler annehmen soll.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gibt den {@link #name Nachnamen} des Schülers zurück.
	 * 
	 * @return Der Nachname des Schülers.
	 */
	public String getName() {
		return this.name;
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
	 * Gibt den {@link #prename Vornamen} des Schülers zurück.
	 * 
	 * @return Der Vorname des Schülers.
	 */
	public String getPrename() {
		return this.prename;
	}

	/**
	 * Fügt einen Kurs ans Ende der {@link #courses Liste der gewünschten Kurse} des
	 * Schülers hinzu.
	 * 
	 * Wenn der Kurs den Wert {@code null} darstellt, so wird dieser Ignoriert.
	 * 
	 * @param course Der Kurs, der hinzugefügt werden soll.
	 */
	public void addCourse(Course course) {
		if (course == null)
			return;
		if (this.courses.contains(course)) {
			this.courses.remove(course);
		}

		this.courses.add(course);
	}

	public void addCourse(int index, Course course) {
		if (course == null)
			return;
		if (this.courses.contains(course)) {
			this.courses.remove(course);
		}

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
	 * Setzt den {@link #activeCourse aktiven Kurs} auf den nächsten Kurs aus der
	 * {@link #courses Liste der gewünschten Kurse}.
	 * 
	 * @return Der neu gesetzte {@link #activeCourse Kurs}.
	 */
	private Course setNextCourse() {
		return this.setCourse(this.getNextCourse());
	}

	/**
	 * 
	 * @param course
	 * @return
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
	 *      getNextCourse({@link #activeCourse}{@link #getNextCourse(Course, int) ,
	 *      1)}
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
	 *      getNextCourse({@link #activeCourse}{@link #getNextCourse(Course, int) ,
	 *      iterator)}
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
	 *         Kurs liegt. Gibt {@code null} zurück wenn der Kurs kein Element der
	 *         {@link #courses Liste der gewünschten Kurse} ist. Gibt den ersten
	 *         Kurs der Liste zurück wenn der mitgegebene Kurs {@code null} ist.
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
	 * 
	 * @param string
	 * @return
	 */
	public int getCourseAmount(Course string) {
		for (int i = 0; i < this.courses.size(); i++) {
			if (this.courses.get(i).equals(string)) {
				// References.LOGGER.info(this.toString() + ": " + i);
				return i * 2;
			}
		}
		return Integer.MAX_VALUE;
	}

	/**
	 * @return the activeCourse
	 */
	public Course getActiveCourse() {
		return activeCourse;
	}

	/**
	 * @param activeCourse the activeCourse to set
	 */
	public void setActiveCourse(Course activeCourse) {
		if (activeCourse == null) {
			if (this.activeCourse != null && this.activeCourse.contains(this))
				this.activeCourse.removeStudent(this);
			this.activeCourse = activeCourse;
			this.priority = 0;
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
	 * 
	 * @return
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

	public int getRate() {
		return (int) (this.priority == Integer.MAX_VALUE
				? Math.pow(Distributor.getInstance().getHighestPriority() + 1, Config.powValue)
				: Math.pow(this.priority, Config.powValue));
	}

	public void mark() {
		this.mark = true;
	}

	public void unmark() {
		this.mark = false;
	}

	public boolean isMarked() {
		return this.mark;
	}

	@Override
	public int compareTo(Student s) {
		int i = this.getName().compareToIgnoreCase(s.getName());

		if (i == 0)
			i = this.prename.compareToIgnoreCase(s.prename);

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

	public int getPosition(Course c) {
		return this.courses.indexOf(c);
	}

	public boolean idequals(int studentID) {
		return this.id == studentID;
	}

	public int getID() {
		return this.id;
	}

	public void setCourses(Course... c) {
		this.courses.clear();

		for (Course courseName : c)
			this.addCourse(courseName);
	}

	public List<Course> getCoursesAsList() {
		return this.courses;
	}
}
