package de.juhu.distributor;

import java.io.Serializable;
import java.util.ArrayList;

import de.juhu.util.Config;

/**
 * Diese Klasse bildet einen Kurs ab und beinhaltet alle diesbezüglich wichtigen
 * Informationen.
 * 
 * @version 1.0
 * @category Distribution
 * @author Juhu1705
 * @implNote Ist {@link Comparable vergleichbar} mit anderen {@link Course
 *           Kursen}.
 */
public class Course implements Comparable<Course>, Serializable {

	/**
	 * Alle Schüler, die dem Kurs zugeordnet sind.
	 */
	private ArrayList<Student> students = new ArrayList<>();

	/**
	 * Der Lehrer des Kurses / Das Lehrerkürzel
	 */
	private String teacher;

	/**
	 * Das Fach des Kurses
	 */
	private String subject;

	/**
	 * Die Maximale Schüleranzahl, die dieser Kurs beinhalten darf.
	 */
	private int maxStudents;

	/**
	 * Erzeugt einen Kurs, indem das maximale Schülerlimit, der Lehrer und das Fach
	 * gesetzt sind.
	 * 
	 * @param subject     Das Fach des Kurses
	 * @param teacher     Der Lehrer des Kurses
	 * @param maxStudents Die maximale Anzahl an Schülern, die in diesen Kurs passen
	 */
	public Course(String subject, String teacher, int maxStudents) {
		this(subject, teacher);

		this.maxStudents = maxStudents;
	}

	/**
	 * Erzeugt einen Kurs, in dem der Lehrer und das Fach mitgegeben werden. Die
	 * maximale Schülerzahl wird auf den in der Config mitgegebenen Standartwert
	 * gesetzt.
	 * 
	 * @param subject Das Fach des Kurses
	 * @param teacher Der Schüler des Kurses
	 */
	public Course(String subject, String teacher) {
		this.teacher = teacher.toUpperCase();
		this.subject = subject.toUpperCase();
		this.maxStudents = Config.normalStudentLimit;
	}

	/**
	 * Erzeugt einen Kurs aus einem zwei langen Array, indem der Kurs und der Lehrer
	 * gesetzt wird und die maximale Schülerzahl auf den in der Config mitgegebenen
	 * Standartwert gesetzt wird.
	 * 
	 * @param split An Position 0 das Fach und an Position 1 der Lehrer
	 * @throws NullPointerException Wenn das gegebene Array weniger als zwei Strings
	 *                              beinhaltet.
	 */
	public Course(String[] split) throws NullPointerException {
		this(split[0], split[1]);
	}

	/**
	 * @return Der Lehrer des Kurses / Das Lehrerkürzel
	 */
	public String getTeacher() {
		return this.teacher;
	}

	/**
	 * @return Das Fach des Kurses
	 */
	public String getSubject() {
		return this.subject;
	}

	/*
	 * TODO Bitte Überprüfen
	 */

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof String) {
			return ((String) obj).equals(this.toString());
		}
		if (obj instanceof Course) {
			return ((Course) obj).toString().equals(this.toString());
		}

		return super.equals(obj);
	}

	/**
	 * Überprüft, ob mehr Schüler im Kurs sind, als das {@link #maxStudents
	 * Schülerlimit} zulässt. Ist das {@link #maxStudents Schülerlimit} {@code -1}
	 * besitzt der Kurs kein Schülerlimit.
	 * 
	 * @return {@code true}, wenn mehr Schüler im Kurs sind, als das Schülerlimit
	 *         erlaubt.
	 */
	public boolean isFull() {
		return ((this.maxStudents < this.students.size()) && (this.maxStudents != -1));
	}

	/**
	 * Gibt den Kurs als String zurück. Dazu wird das {@link #subject Fach} durch
	 * einen "|" vom {@link #teacher Lehrer} getrennt. Der zurückgegebene String
	 * sieht dann wiefolgt aus: {@link #subject
	 * Fach}{@code  + "|" + }{@link #teacher Lehrer}.
	 */
	@Override
	public String toString() {
		return this.subject + "|" + this.teacher;
	}

	/**
	 * @return Gibt eine Liste aller Schüler im Kurs zurück.
	 */
	public ArrayList<Student> getStudents() {
		return this.students;
	}

	/**
	 * Überprüft, ob dieser Schüler diesem Kurs zugeordnet ist.
	 * 
	 * @param student Der Schüler, bei dem überprüft werden soll, ob er im Kurs ist.
	 * @return Ob der Schüler im Kurs ist.
	 * @implNote Verwendet die Methode {@link ArrayList#contains(Object)}
	 */
	public boolean contains(Student student) {
		return this.students.contains(student);
	}

	/**
	 * Fügt einen Schüler hinzu, wenn er nicht bereits im Kurs ist, oder der
	 * hinzuzufügene Schüler nicht {@code null} ist.
	 * 
	 * @param student Der Schüler, der hinzugefügt werden soll.
	 * @return Ob der Schüler erfolgreich hinzugefügt werden konnte.
	 * @implNote Verwendet die Methode {@link ArrayList#add(Object)}
	 */
	public boolean addStudent(Student student) {
		if (student == null || this.contains(student))
			return false;

		return this.students.add(student);
	}

	/**
	 * Entfernt einen Schüler aus diesem Kurs.
	 * 
	 * @param student Der Schüler, der entfernt werden soll.
	 * @return Der Schüler, der entfernt wurde.
	 * @implNote Verwendet die Methode {@link ArrayList#remove(int)}
	 */
	public Student removeStudent(Student student) {
		return this.students.remove(this.students.indexOf(student));
	}

	/**
	 * Gibt den Schüler zurück, der dem mitgegebene Index in der {@link #students
	 * Liste der Schüler des Kurses} zugeordnet ist.
	 * 
	 * @param i Der Index von dem der Schüler zurückgegeben werden soll.
	 * @return Der Schüler, der an der Position i der Liste steht.
	 */
	public Student getStudent(int i) {
		return this.students.get(i);
	}

	/**
	 * @return {@link ArrayList#size()} der {@link #students Liste der Schüler des
	 *         Kurses}.
	 */
	public int size() {
		return this.students.size();
	}

	/**
	 * Gibt die Liste als String zurück. Die Schüler werden über
	 * {@link Student#toString()} in einen String überführt.
	 * 
	 * @return {@link ArrayList#toString()} der {@link #students Liste der Schüler
	 *         des Kurses}.
	 */
	public String studentsToString() {
		return this.students.toString();
	}

	@Override
	public int compareTo(Course c) {
		int i = this.subject.compareTo(c.subject);

		if (i == 0)
			i = this.teacher.compareTo(c.teacher);

		return i;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return new Course(subject, teacher, maxStudents);
	}

	/**
	 * @return Die maximale Anzahl an Schülern, die diesem Kurs zugewiesen werden
	 *         dürfen.
	 */
	public int getMaxStudentCount() {
		return this.maxStudents;
	}

	/**
	 * Setzt den {@link #teacher Lehrer} des Kurses.
	 * 
	 * @param teacher Der Lehrer des Kurses.
	 */
	public void setTeacher(String teacher) {
		this.teacher = teacher.toUpperCase();
	}

	/**
	 * Setzt das {@link #subject Fach} des Kurses.
	 * 
	 * @param subject Das Fach des Kurses.
	 */
	public void setSubject(String subject) {
		this.subject = subject.toUpperCase();
	}

	/**
	 * Setzt das {@link #maxStudents Schülerlimit} des Kurses
	 * 
	 * @param maxStudents Die maximale Anzahl an Schülern, die diesem Kurs
	 *                    zugeordnet werden dürfen.
	 */
	public void setStudentMax(int maxStudents) {
		this.maxStudents = maxStudents;
	}

}
