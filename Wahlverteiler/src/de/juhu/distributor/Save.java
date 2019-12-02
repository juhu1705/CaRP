package de.juhu.distributor;

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

public class Save implements Comparable<Save> {

	private InformationSave informations;

	private List<Student> allStudents;
	private List<Course> allCourses;

	public Save(List<Student> editedStudents, List<Student> ignoredStudents, List<Course> allCourses,
			InformationSave informations) {
		this.informations = informations;
		this.informations.parent = this;

		this.allCourses = this.sortCourse((allCourses));

		this.allStudents = new ArrayList<>(editedStudents);
		this.allStudents.addAll(ignoredStudents);

		this.allStudents = Save.sortStudents((this.allStudents));

		this.informations.update();
	}

	public int getHighestPriority() {
		int highest = 0;
		for (Student s : this.allStudents)
			if (s.getActiveCourse() != null && !s.getActiveCourse().equals(Distributor.getInstance().ignore()))
				highest = highest >= s.getPriority() ? highest : s.getPriority();
			else if (s.getActiveCourse() == null)
				highest = highest >= s.getPriority() ? highest : s.getPriority();
		return highest;
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

	private List<Course> sortCourse(List<Course> courseToSort) {
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
		WriteableContent students = new WriteableContent("Students");

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
		WriteableContent courses = new WriteableContent("Courses");

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
		if (Config.sortUnallocatedFirstOut)
			if (this.informations.getHighestPriority() == Integer.MAX_VALUE
					&& s.getInformation().getHighestPriority() != Integer.MAX_VALUE)
				return -1;
			else if (this.informations.getHighestPriority() != Integer.MAX_VALUE
					&& s.getInformation().getHighestPriority() == Integer.MAX_VALUE)
				return 1;

		if (Config.compareGuete)
			return this.informations.getGuete() - s.informations.getGuete() >= 0 ? -1 : 1;

		if (!Config.compareFirstPriority
				|| this.informations.getHighestPriority() == s.informations.getHighestPriority())
			return (this.informations.getRate() - s.informations.getRate()) * -1;
		return (this.informations.getHighestPriority() - s.informations.getHighestPriority()) * -1;
	}

	public int compareTo(int rate) {
		return (this.informations.getHighestPriority() - rate) * -1;
	}

}
