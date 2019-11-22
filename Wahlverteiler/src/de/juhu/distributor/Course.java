package de.juhu.distributor;

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
public class Course implements Comparable<Course> {

	private ArrayList<Student> students = new ArrayList<>();

	private String teacher;
	private String subject;

	private int maxStudents;

	public Course(String subject, String teacher, int maxStudents) {
		this(subject, teacher);

		this.maxStudents = maxStudents;
	}

	public Course(String subject, String teacher) {
		this.teacher = teacher.toUpperCase();
		this.subject = subject.toUpperCase();
		this.maxStudents = Config.normalStudentLimit;
	}

	public Course(String[] split) {
		this(split[0], split[1]);
	}

	public String getTeacher() {
		return this.teacher;
	}

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

	public boolean isFull() {
		return ((this.maxStudents < this.students.size()) && (this.maxStudents != -1));
	}

	@Override
	public String toString() {
		return this.subject + "|" + this.teacher;
	}

	public ArrayList<Student> getStudents() {
		return this.students;
	}

	public boolean contains(Student student) {
		return this.students.contains(student);
	}

	public boolean addStudent(Student student) {
		if (student == null || this.contains(student))
			return false;

		return this.students.add(student);
	}

	public Student removeStudent(Student student) {
		return this.students.remove(this.students.indexOf(student));
	}

	public Student getStudent(int i) {
		return this.students.get(i);
	}

	public int size() {
		return this.students.size();
	}

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

	public int getMaxStudentCount() {
		return this.maxStudents;
	}

	public void setTeacher(String teacher) {
		this.teacher = teacher.toUpperCase();
	}

	public void setSubject(String subject) {
		this.subject = subject.toUpperCase();
	}

	public void setStudentMax(int intValue) {
		this.maxStudents = intValue;
	}

}
