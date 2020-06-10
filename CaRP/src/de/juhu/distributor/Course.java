package de.juhu.distributor;

import java.io.Serializable;
import java.util.ArrayList;

import de.juhu.util.Config;

/**
 * Diese Klasse bildet einen Kurs ab und beinhaltet alle diesbez�glich wichtigen
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
	 * Alle Sch�ler, die dem Kurs zugeordnet sind.
	 */
	private ArrayList<Student> students = new ArrayList<>();

	/**
	 * Der Lehrer des Kurses / Das Lehrerk�rzel
	 */
	private String teacher;

	/**
	 * Das Fach des Kurses
	 */
	private String subject;

	/**
	 * Die Maximale Sch�leranzahl, die dieser Kurs beinhalten darf.
	 */
	private int maxStudents;

	/**
	 * Erzeugt einen Kurs, indem das maximale Sch�lerlimit, der Lehrer und das Fach
	 * gesetzt sind.
	 * 
	 * @param subject     Das Fach des Kurses
	 * @param teacher     Der Lehrer des Kurses
	 * @param maxStudents Die maximale Anzahl an Sch�lern, die in diesen Kurs passen
	 */
	public Course(String subject, String teacher, int maxStudents) {
		this(subject, teacher);

		this.maxStudents = maxStudents;
	}

	/**
	 * Erzeugt einen Kurs, in dem der Lehrer und das Fach mitgegeben werden. Die
	 * maximale Sch�lerzahl wird auf den in der Config mitgegebenen Standartwert
	 * gesetzt.
	 * 
	 * @param subject Das Fach des Kurses
	 * @param teacher Der Sch�ler des Kurses
	 */
	public Course(String subject, String teacher) {
		this.teacher = teacher.toUpperCase();
		this.subject = subject.toUpperCase();
		this.maxStudents = Config.normalStudentLimit;
	}

	/**
	 * Erzeugt einen Kurs aus einem zwei langen Array, indem der Kurs und der Lehrer
	 * gesetzt wird und die maximale Sch�lerzahl auf den in der Config mitgegebenen
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
	 * @return Der Lehrer des Kurses / Das Lehrerk�rzel
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
	 * TODO Bitte �berpr�fen
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
	 * �berpr�ft, ob mehr Sch�ler im Kurs sind, als das {@link #maxStudents
	 * Sch�lerlimit} zul�sst. Ist das {@link #maxStudents Sch�lerlimit} {@code -1}
	 * besitzt der Kurs kein Sch�lerlimit.
	 * 
	 * @return {@code true}, wenn mehr Sch�ler im Kurs sind, als das Sch�lerlimit
	 *         erlaubt.
	 */
	public boolean isFull() {
		return ((this.maxStudents < this.students.size()) && (this.maxStudents != -1));
	}

	/**
	 * Gibt den Kurs als String zur�ck. Dazu wird das {@link #subject Fach} durch
	 * einen "|" vom {@link #teacher Lehrer} getrennt. Der zur�ckgegebene String
	 * sieht dann wiefolgt aus: {@link #subject
	 * Fach}{@code  + "|" + }{@link #teacher Lehrer}.
	 */
	@Override
	public String toString() {
		return this.subject + "|" + this.teacher;
	}

	/**
	 * @return Gibt eine Liste aller Sch�ler im Kurs zur�ck.
	 */
	public ArrayList<Student> getStudents() {
		return this.students;
	}

	/**
	 * �berpr�ft, ob dieser Sch�ler diesem Kurs zugeordnet ist.
	 * 
	 * @param student Der Sch�ler, bei dem �berpr�ft werden soll, ob er im Kurs ist.
	 * @return Ob der Sch�ler im Kurs ist.
	 * @implNote Verwendet die Methode {@link ArrayList#contains(Object)}
	 */
	public boolean contains(Student student) {
		return this.students.contains(student);
	}

	/**
	 * F�gt einen Sch�ler hinzu, wenn er nicht bereits im Kurs ist, oder der
	 * hinzuzuf�gene Sch�ler nicht {@code null} ist.
	 * 
	 * @param student Der Sch�ler, der hinzugef�gt werden soll.
	 * @return Ob der Sch�ler erfolgreich hinzugef�gt werden konnte.
	 * @implNote Verwendet die Methode {@link ArrayList#add(Object)}
	 */
	public boolean addStudent(Student student) {
		if (student == null || this.contains(student))
			return false;

		return this.students.add(student);
	}

	/**
	 * Entfernt einen Sch�ler aus diesem Kurs.
	 * 
	 * @param student Der Sch�ler, der entfernt werden soll.
	 * @return Der Sch�ler, der entfernt wurde.
	 * @implNote Verwendet die Methode {@link ArrayList#remove(int)}
	 */
	public Student removeStudent(Student student) {
		return this.students.remove(this.students.indexOf(student));
	}

	/**
	 * Gibt den Sch�ler zur�ck, der dem mitgegebene Index in der {@link #students
	 * Liste der Sch�ler des Kurses} zugeordnet ist.
	 * 
	 * @param i Der Index von dem der Sch�ler zur�ckgegeben werden soll.
	 * @return Der Sch�ler, der an der Position i der Liste steht.
	 */
	public Student getStudent(int i) {
		return this.students.get(i);
	}

	/**
	 * @return {@link ArrayList#size()} der {@link #students Liste der Sch�ler des
	 *         Kurses}.
	 */
	public int size() {
		return this.students.size();
	}

	/**
	 * Gibt die Liste als String zur�ck. Die Sch�ler werden �ber
	 * {@link Student#toString()} in einen String �berf�hrt.
	 * 
	 * @return {@link ArrayList#toString()} der {@link #students Liste der Sch�ler
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

		return 0;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return new Course(subject, teacher, maxStudents);
	}

	/**
	 * @return Die maximale Anzahl an Sch�lern, die diesem Kurs zugewiesen werden
	 *         d�rfen.
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
	 * Setzt das {@link #maxStudents Sch�lerlimit} des Kurses
	 * 
	 * @param maxStudents Die maximale Anzahl an Sch�lern, die diesem Kurs
	 *                    zugeordnet werden d�rfen.
	 */
	public void setStudentMax(int maxStudents) {
		this.maxStudents = maxStudents;
	}

}
