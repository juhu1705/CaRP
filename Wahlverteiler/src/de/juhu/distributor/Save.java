package de.juhu.distributor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import de.juhu.dateimanager.WriteableContent;
import de.juhu.math.Vec2i;
import de.juhu.util.Config;
import de.juhu.util.MergeSort;
import de.juhu.util.References;
import de.juhu.util.Util;

/**
 * Diese Klasse dient zur Speicherung berechneter Zuweisungen des
 * {@link Distributor Berechners} im Arbeitsspeicher und stellt zudem noch
 * Methoden zur Bearbeitung der gespeicherten Daten, sowie zum Exportieren der
 * Daten bereit.
 * 
 * 
 * @version 1.0
 * @category Distribution
 * @author Juhu1705
 * @implements {@link Comparable}, {@link Serializable}
 * @since 0.0.2
 */
public class Save implements Comparable<Save>, Serializable {

	/**
	 * Der {@link InformationSave Informationsspeicher} dieser Klasse, hier werden
	 * interressante Zusatzinformationen hinterlegt.
	 */
	private InformationSave informations;

	/**
	 * Die Liste aller Schüler einer Berechnung inklusive der ignorierten Schüler.
	 */
	private List<Student> allStudents;

	/**
	 * Die Liste aller Kurse einer Berechnung.
	 */
	private List<Course> allCourses;

	/**
	 * Erzeugt einen neuen Speicher, indem die "editedStudents" mit den
	 * "ignoredStudents" gekreuzt und dann in die {@link #allStudents Liste aller
	 * Schüler} dieses Speichers gespeichert werden. Dabei wird die mitgegebenen
	 * Listen über einen MergeSort allgorithmus sortiert. Dem {@link #informations
	 * Informations Speicher} wird dieser Speicher als {@link InformationSave#parent
	 * Elternklasse} gesetzt und dieser wird noch einmal
	 * {@link InformationSave#update() aktualisiert}.
	 * 
	 * @param editedStudents  Alle berechneten Schüler -
	 *                        {@link Distributor#allStudents}
	 * @param ignoredStudents Alle nicht mitberechneten Schüler -
	 *                        {@link Distributor#ignoredStudents}
	 * @param allCourses      Alle Kurse - {@link Distributor#allCourses}
	 * @param informations    Weitere Informationen über die Berechnung -
	 *                        {@link InformationSave#InformationSave(int, int, int[], ArrayList, ArrayList)}
	 */
	public Save(List<Student> editedStudents, List<Student> ignoredStudents, List<Course> allCourses,
			InformationSave informations) {
		this.informations = informations;
		this.informations.parent = this;

		this.allCourses = Save.sortCourse((allCourses));

		this.allStudents = new ArrayList<>(editedStudents);
		this.allStudents.addAll(ignoredStudents);

		this.allStudents = Save.sortStudents((this.allStudents));

		this.informations.update();
	}

	/**
	 * @return Die höchste Priorität der in diesem Speicher gespeicherten
	 *         Berechnung.
	 */
	public int getHighestPriority() {
		int highest = 0;
		for (Student s : this.allStudents)
			if (s.getActiveCourse() != null && !s.getActiveCourse().equals(Distributor.getInstance().ignore()))
				highest = highest >= s.getPriority() ? highest : s.getPriority();
			else if (s.getActiveCourse() == null)
				highest = highest >= s.getPriority() ? highest : s.getPriority();
		return highest;
	}

	/**
	 * @return Die Anzahl der Schüler mit den einzelnen Prioritäten der in diesem
	 *         Speicher gespeicherten Berechnung.
	 */
	public int[] getStudentPriorities() {
		int[] studentPriorities = new int[this.getHighestPriorityWhithoutIntegerMax() + 1];
		int i1 = 0;
		for (Student s : this.getStudentsWithPriority(1))
			if (!s.getActiveCourse().equals(Distributor.getInstance().ignoredCourse))
				i1++;

		studentPriorities[0] = i1;

		for (int i = 1; i < studentPriorities.length - 1; i++) {
			studentPriorities[i] = this.getStudentsWithPriority(i + 1).size();
		}
		studentPriorities[studentPriorities.length - 1] = this.getInformation().getunallocatedStudents().size();
		return studentPriorities;
	}

	public int rate(int highestPriority) {
		int count = 0;

		for (Student s : this.allStudents) {
			if (s.getActiveCourse() != null && s.getActiveCourse().equals(Distributor.getInstance().ignore()))
				continue;

			count += s.getRate(highestPriority);
		}

		if (count > Integer.MAX_VALUE || count < 0)
			return Integer.MAX_VALUE;
		return count;
	}

	public Course getCourseByName(String name) {
		for (Course c : this.allCourses) {
			if (name.equalsIgnoreCase(c.toString()))
				return c;
		}
		return null;
	}

	public int getHighestPriorityWhithoutIntegerMax() {
		int highest = 0;
		for (Student s : this.allStudents)
			if (s.getPriority() != Integer.MAX_VALUE)
				highest = highest >= s.getPriority() ? highest : s.getPriority();
		return highest;
	}

	public ArrayList<Student> getStudentsWithPriority(int priority) {
		ArrayList<Student> pStudents = new ArrayList<>();

		for (Student s : this.allStudents)
			if (s.getPriority() == priority)
				pStudents.add(s);

		return pStudents;
	}

	private static List<Course> sortCourse(List<Course> courseToSort) {
		ExecutorService pool = Executors.newFixedThreadPool(courseToSort.size() / 2 + 10);
		Future<ArrayList<Course>> sortedStudents = pool
				.submit(new MergeSort<Course>((ArrayList<Course>) courseToSort, pool));
		try {
			ArrayList<Course> compute = new ArrayList<Course>(sortedStudents.get());
			pool.shutdownNow();
			return compute;
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		pool.shutdownNow();
		return null;
	}

	public static List<Student> sortStudents(List<Student> studentsToSort) {
		ExecutorService pool = Executors.newFixedThreadPool(studentsToSort.size() / 2 + 10);
		Future<ArrayList<Student>> sortedStudents = pool
				.submit(new MergeSort<Student>((ArrayList<Student>) (studentsToSort), pool));
		try {
			ArrayList<Student> compute = new ArrayList<Student>(sortedStudents.get());
			pool.shutdownNow();
			return compute;
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		pool.shutdownNow();
		return null;
	}

	public List<Student> getAllStudents() {
		return this.allStudents;
	}

	public List<Course> getAllCourses() {
		return this.allCourses;
	}

	public Course[] getAllCoursesAsArray() {
		Course[] courses = new Course[this.allCourses.size()];
		int i = 0;
		for (Course c : this.allCourses)
			courses[i++] = c;

		return courses;
	}

	public InformationSave getInformation() {
		return this.informations;
	}

	public List<WriteableContent> writeInformation() {
		List<WriteableContent> export = new ArrayList<WriteableContent>(3);

		// Write students
		WriteableContent students = this.writeStudentInformation();
		export.add(students);

		WriteableContent courses = this.writeCourseInformation();
		export.add(courses);

		WriteableContent information = this.informations.write();
		export.add(information);

		return export;
	}

	public WriteableContent writeStudentInformation() {
		WriteableContent students = new WriteableContent(References.language.getString("coursedistribution.text"));

		students.addLine(new Vec2i(0, 0), new String[] { "Name", "Vorname", "Kurs", "Lehrer", "Priorität" });

		for (int i = 0; i < this.allStudents.size(); i++) {
			String[] line = new String[5];
			line[0] = this.allStudents.get(i).getName();
			line[1] = this.allStudents.get(i).getPrename();
			if (this.allStudents.get(i).getActiveCourse() != null
					&& !Util.isBlank(this.allStudents.get(i).getActiveCourse().toString())) {
				line[2] = this.allStudents.get(i).getActiveCourse().getSubject();
				line[3] = this.allStudents.get(i).getActiveCourse().getTeacher();
				line[4] = this.allStudents.get(i).getPriority() + "";
			} else
				line[2] = "@PJK";

			students.addLine(new Vec2i(0, i + 1), line);
		}

		return students;
	}

	public WriteableContent writeCourseInformation() {
		WriteableContent courses = new WriteableContent(References.language.getString("studentdistribution.text"));

		courses.addLine(new Vec2i(0, 0), new String[] { "Kurs", "Lehrer", "Anzahl Schüler", "Schüler" });

		int line = 1;
		for (Course c : this.allCourses) {
			String[] parameter = new String[c.size() + 3];

			References.LOGGER.info("Kursname: " + c.toString() + "; Schülerzahl: " + Integer.toString(c.size())
					+ "; Parameterlänge: " + Integer.toString(parameter.length));

			parameter[0] = c.getSubject();
			parameter[1] = c.getTeacher();
			int lineAdd = 3;
			int anzahl = 0;
			for (int i = 0; i < c.size(); i++, anzahl++) {
				References.LOGGER.info(i + "");
				if (Config.firstPrename)
					if (Config.shortNames)
						parameter[i + lineAdd] = c.getStudent(i).getPrename().toCharArray()[0] + ". "
								+ c.getStudent(i).getName();
					else
						parameter[i + lineAdd] = c.getStudent(i).getPrename() + " " + c.getStudent(i).getName();
				else if (Config.shortNames)
					parameter[i + lineAdd] = c.getStudent(i).getName() + ", "
							+ c.getStudent(i).getPrename().toCharArray()[0];
				else
					parameter[i + lineAdd] = c.getStudent(i).getName() + ", " + c.getStudent(i).getPrename();
			}
			parameter[2] = Integer.toString(anzahl);

			courses.addLine(new Vec2i(0, line++), parameter);
		}

		return courses;
	}

	@Override
	public int compareTo(Save s) {
		if (s.getInformation().getGuete() > 1)
			return 1;

		if (this.informations.getGuete() > 1)
			return -1;

		if (this.informations.getGuete() < 0)
			return -1;

		if (s.getInformation().getGuete() < 0)
			return 1;

		if (this.getInformation().getGuete() == s.getInformation().getGuete())
			return 0;

		if (this.sameCalculation(s))
			return 0;

		return this.informations.getGuete() - s.informations.getGuete() >= 0 ? 1 : -1;
	}

	private boolean sameCalculation(Save s) {
		if (this.getHighestPriority() == s.getHighestPriority()) {
			int[] thispriorities = this.getStudentPriorities();
			int[] spriorities = s.getStudentPriorities();
			if (thispriorities.length != spriorities.length)
				return false;
			for (int i = 0; i < thispriorities.length; i++)
				if (thispriorities[i] != spriorities[i])
					return false;

			return true;
		}

		return false;
	}

	public int compareTo(int guete) {
		return this.informations.getGuete() - guete >= 0 ? -1 : 1;
	}

	public void addCourse(Course c) {
		if (this.allCourses.contains(c))
			this.allCourses.remove(c);
		this.allCourses.add(c);
	}

	public Student getStudentByID(int studentID) {
		for (Student s : this.allStudents)
			if (s.idequals(studentID))
				return s;
		return null;
	}

	public boolean addStudent(Student student) {
		if (student == null || this.allStudents.contains(student))
			return false;

		return this.allStudents.add(student);
	}

	public void removeStudent(Student student) {
		if (student == null)
			return;
		if (this.allStudents.contains(student)) {
			student.getActiveCourse().removeStudent(student);
			this.allStudents.remove(this.allStudents.indexOf(student));
		}
	}

	public void removeCourse(Course course) {
		if (course == null)
			return;
		if (this.allCourses.contains(course)) {
			for (Student s : course.getStudents()) {
				s.setActiveCourse(null);
				s.refreshPriority();
				s.mark();
			}
			this.allCourses.remove(this.allCourses.indexOf(course));
		}
	}

}
