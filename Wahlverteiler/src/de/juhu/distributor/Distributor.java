package de.juhu.distributor;

import static de.juhu.dateimanager.CSVImporter.readCSV;
import static de.juhu.dateimanager.ExcelImporter.readXLSImproved;
import static de.juhu.dateimanager.ExcelImporter.readXLSXImproved;
import static de.juhu.util.References.LOGGER;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import de.juhu.dateimanager.WriteableContent;
import de.juhu.guiFX.GUIManager;
import de.juhu.util.Config;
import de.juhu.util.PriorityQueue;
import de.juhu.util.References;
import de.juhu.util.Util;
import javafx.application.Platform;

/**
 * Diese Klasse Verwaltet und Berechnet die eingegebenen Daten und stellt die
 * besten Ergebnisse zum Auslesen bereit.
 * 
 * @version 2.0
 * @category Distribution
 * @author Juhu1705
 * @implements Runnable
 * @since BETA-0.0.1
 */
public class Distributor implements Runnable {

	// Ausgabe
	/**
	 * Diese Liste stellt die besten Ergebnisse zum Auslesen bereit.
	 */
	public static PriorityQueue<Save> calculated = new PriorityQueue<Save>(100);

	/**
	 * Zeigt an, ob fertigberechnete Ergebnisse ausgegeben werden können.
	 */
	public static boolean calculate = false;

	// Berechnungsinformationen
	/**
	 * Speichert die zu berechnenden Schüler.
	 */
	ArrayList<Student> loadedallStudents = new ArrayList<>();

	/**
	 * Speichert die zu berechnenden Kurse.
	 */
	ArrayList<Course> loadedallCourses = new ArrayList<>();

	/**
	 * Diese Liste hält alle für die Kalkulation irrelevanten Schüler fest und
	 * stellt sie zur Nachträglichen Einfügung in das Ergebnis bereit.
	 */
	ArrayList<Student> ignoredStudents = new ArrayList<>();

	/**
	 * Dieser Kurs beinhaltet alle ignore Students
	 */
	Course ignoredCourse = new Course(Config.ignoreStudent, "", -1);

	/**
	 * Dient zur Berechnung der Schüler.
	 * 
	 * @info Verändert sich während der Laufzeit.
	 */
	ArrayList<Student> allStudents = new ArrayList<>();

	/**
	 * Dient zur Berechnung der Kurse.
	 * 
	 * @info Verändert sich während der Laufzeit.
	 */
	ArrayList<Course> allCourses = new ArrayList<>();

	/**
	 * Enthält alle nicht zuteilbaren Schüler.
	 * 
	 * @deprecated Unused - Nur in älteren Fehlerhaften Zuweisungsmethoden benutzt,
	 *             die nicht mehr aufgerufen werden
	 */
	@Deprecated
	ArrayList<Student> problems = new ArrayList<>();

	// INFO: Instance

	/**
	 * Die Aktuelle Instanz dieser Klasse, auf die Zugegriffen werden kann.
	 */
	private static Distributor instance = null;

	/**
	 * 
	 * @return Die letzte Erstellte Instanz dieser Klasse, oder eine neue Instanz,
	 *         falls noch keine Vorhanden war.
	 */
	public static Distributor getInstance() {
		if (instance == null)
			new Distributor();

		return instance;
	}

	// INFO: Konstruktoren

	/**
	 * Erstellt eine Instanz dieser Klasse ohne weitere Eigenschaften. Wird nur
	 * ausgeführt, wenn noch keine Instanz vorhanden ist und {@link #getInstance()}
	 * aufgerufen wird.
	 */
	protected Distributor() {
		if (instance != null && !Config.clear) {
			this.allStudents = Distributor.getInstance().loadedallStudents;
			this.allCourses = Distributor.getInstance().loadedallCourses;
			this.ignoredStudents = Distributor.getInstance().ignoredStudents;
		}

		instance = this;

		this.loadedallStudents = this.allStudents;
		this.loadedallCourses = this.allCourses;
		this.loadReaders();
	}

	/**
	 * Erstellt eine Neue Instanz dieser Klasse und setzt die Vorhandende Instanz
	 * auf die hier erstellte. Sollte {@link Config#clear} false sein, so werden die
	 * Gespeicherten Schüler und Kurse der vorherigen in {@link #instance}
	 * gespeicherten Instanz automatisch in die neue miteingerechnet.
	 * 
	 * Gleichzeitig werden die Schüler und Kursdaten aus dem mitgegebenen Pfad über
	 * die Methode {@link #readFile(String)} eingelesen und in die Bestehenden Daten
	 * eingespeißt.
	 * 
	 * @param filename Der Pfad, aus dem die benötigten Schüler und Kurs Daten
	 *                 herausgelesen werden.
	 */
	public Distributor(String filename) {
		this.loadReaders();

		// Lädt, falls gewünscht die Daten aus der alten Instanz in die Neue.
		if (!Config.clear) {
			this.allStudents = Distributor.getInstance().loadedallStudents;
			this.allCourses = Distributor.getInstance().loadedallCourses;
			this.ignoredStudents = Distributor.getInstance().ignoredStudents;
		}

		// Setzt die aktuelle Instanz auf diese.
		Distributor.instance = this;

		// Ließt die Dateien in das System ein.
		this.readFile(filename);
		this.loadedallStudents = this.allStudents;
		this.loadedallCourses = this.allCourses;

	}

	/**
	 * Lädt die Daten des ersten mitgegebenen {@link Save Speichers} in den
	 * {@link Distributor Berechner}. Fügt die anderen beiden Speicherungen und die
	 * geladene in die {@link Distributor#calculated Liste der Kalkulationen} ein.
	 * 
	 * @implNote Alle vorherigen Daten werden gelöscht.
	 * @param actual      Der {@link Save Speicher}, welcher geladen und dann in die
	 *                    {@link Distributor#calculated Liste der Kalkulationen}
	 *                    eingefügt wird.
	 * @param readObject  Der erste {@link Save Speicher}, der in die
	 *                    {@link Distributor#calculated Liste der Kalkulationen}
	 *                    eingefügt wird.
	 * @param readObject2 Der zweite {@link Save Speicher}, der in die
	 *                    {@link Distributor#calculated Liste der Kalkulationen}
	 *                    eingefügt wird.
	 */
	public Distributor(Save actual, Save... readObject) {
		this.loadReaders();

		// Setzt die aktuelle Instanz auf diese.
		Distributor.instance = this;

		// Ließt die Saves in das System ein.
		this.loadDataFromSave(actual);
		this.loadedallStudents = this.allStudents;
		this.loadedallCourses = this.allCourses;

		// Fügt alle drei Speicherungen zu den Kalkulationen hinzu.
		Distributor.calculated.add(actual);
		for (Save s : readObject)
			Distributor.calculated.add(s);
	}

	/**
	 * Startet den Gewünschten Berechnungs- und Zuweisungsprozess. Überwacht, dass
	 * nur eine Berechnung zur gleichen Zeit abläuft. Lädt, wenn keine Schülerdaten
	 * vorhanden sind und ein Pfad angegeben ist, welcher auf eine existierende
	 * Tabellen-Datei verweist diese Tabellen-Datei in das Programm und berechnet
	 * für die neu geladenen Daten eine Zuweisung.
	 */
	@Override
	public void run() {
		/*
		 * Überprüft, ob bereits eine Instanz geöfnet ist und bricht, wenn bereits eine
		 * Instanz geöffnet ist den Prozess ab.
		 */
		if (calculate) {
			Platform.runLater(() -> {
				GUIManager.getInstance().startErrorFrame("Cannot start calculation while calculating!",
						"Please wait until the actual running calculation is finished.");
//				GUIManager.getInstance().r1.setDisable(false);
//				GUIManager.getInstance().r2.setDisable(false);
//				GUIManager.getInstance().r3.setDisable(false);
			});
			return;
		}

		if (this.allStudents.isEmpty()) {
			File file = new File(Config.inputFile);
			if (file.exists()) {
				new Thread(new Distributor(Config.inputFile)).start();

				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						GUIManager.getInstance().inputView.fill();
						GUIManager.getInstance().cView.fill();
					}
				});
			} else
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						GUIManager.getInstance().startErrorFrame("No Data for Calculating!",
								"Please import data in the programm.");
						GUIManager.getInstance().r1.setDisable(false);
						GUIManager.getInstance().r2.setDisable(false);
						GUIManager.getInstance().r3.setDisable(false);
					}
				});

			return;
		}

		LOGGER.config("Distributor Started whith this specificies: ");
		LOGGER.config("--Basic Student limit: " + Integer.toString(Config.normalStudentLimit));
		LOGGER.config("--The chooses of the Students: " + Integer.toString(Config.maxChooses));
		LOGGER.config("--The number of students to Calculate: " + Integer.toString(this.allStudents.size()));

		calculate = true;

		calculated = new PriorityQueue<>();

		this.assigner();

		for (Save s : Distributor.calculated.list)
			s.getInformation().update();

		GUIManager.actual = Distributor.calculated.peek();

		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				GUIManager.getInstance().counter
						.setText(Integer.toString(Distributor.calculated.indexOf(GUIManager.actual) + 1));
			}
		});

		Platform.runLater(GUIManager.getInstance().outputSView);
		Platform.runLater(GUIManager.getInstance().outputCView);
		Platform.runLater(GUIManager.getInstance().outputIView);

		calculate = false;
	}

	private void alg1() {

		LOGGER.info("Count: " + this.allCourses.size() + 1 + "; " + this.allStudents.size()
				+ this.ignoredStudents.size() + 2 + ";");

		ProgressIndicator.getInstance().setfProgressMax(Config.runs).setfProgressValue(0);

		this.loadedallStudents = this.allStudents;
		this.loadedallCourses = this.allCourses;

		for (int i = 0; i < Config.runs; i++, ProgressIndicator.getInstance().addfProgressValue(1)) {

			this.allCourses = (ArrayList<Course>) this.loadedallCourses.clone();
			this.allStudents = (ArrayList<Student>) this.loadedallStudents.clone();

			this.synchroniseStudentAndCourses();

			LOGGER.info("Start Calculation Number " + (i + 1));

			this.allgorithmus1();

			this.printRate();

			Save toSave;
			if (!calculated.isEmpty() && calculated.size() > 5) {
				Save best = calculated.peek();
				int rate = this.rate();
				if (best.compareTo(rate) > 0) {
					toSave = best;
				} else {
					toSave = new Save((List<Student>) allStudents.clone(), (List<Student>) ignoredStudents.clone(),
							(List<Course>) allCourses.clone(), new InformationSave(rate, 0, null));
				}
			} else {
				toSave = new Save(allStudents, ignoredStudents, allCourses, new InformationSave(this.rate(), 0, null));
			}
			LOGGER.info("Finished Calculation with rate " + (this.rate()));
			this.print();
			calculated.add(toSave);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		calculate = false;
		ProgressIndicator.getInstance().setfProgressMax(1).setfProgressValue(0);
	}

	private void loadDataFromSave(Save save) {
		this.allCourses.clear();
		this.allStudents.clear();

		this.ignoredStudents.clear();

		this.allCourses = new ArrayList<Course>(save.getAllCourses());

		ArrayList<Student> students = new ArrayList<>(save.getAllStudents());

//		for (int i = 0; i < students.size(); i++) {
//			if (students.get(i).getActiveCourse() != null && students.get(i).getActiveCourse().equals(this.ignore())) {
//				this.ignoredStudents.add(students.remove(i));
//			}
//		}

		this.ignoredCourse.getStudents().clear();

		ArrayList[] copiedData = this.copyData(students, (ArrayList) save.getAllCourses(), this.ignoredCourse);

		this.allStudents = copiedData[0];
		this.allCourses = copiedData[1];

		for (int i = 0; i < this.allStudents.size();) {

			if ((this.allStudents.get(i).getActiveCourse() != null
					&& this.allStudents.get(i).getActiveCourse().equals(this.ignore()))
					|| this.allStudents.get(i).getCoursesAsList().contains(this.ignore())) {
				this.ignoredStudents.add(this.allStudents.remove(i));
			} else
				i++;
		}
	}

	/**
	 * Gibt die Rate der Berechnung mit der Priorität info in der Konsole aus.
	 */
	public void printRate() {
		for (int i = 0; i <= Config.maxChooses; i++) {
			LOGGER.info("Students that get their " + i + " coise: " + this.getStudentsWithRate(i));
		}
	}

	/**
	 * Ermittelt die Anzahl der Schüler mit der entsprechenden Rate
	 * 
	 * @param rate Die Rate nach der gesucht wird TODO - Besser Formulieren
	 * @return Die Anzahl der Schüler mit der entsprechenden Rate
	 */
	public int getStudentsWithRate(int rate) {
		rate *= 2;
		int count = 0;

		for (Course c : this.allCourses) {
			for (Student s : c.getStudents()) {
				if (s.getCourseAmount(c) == rate)
					count++;
			}
		}

		return count;
	}

	/**
	 * Ermittelt wie gut die Kalkulation war.
	 * 
	 * @return Der Güte Wert der Berechnung
	 */
	public int rate() {
		int count = 0;

		for (Student s : this.allStudents) {
			// LOGGER.info("Rate: " + s.getRate());
			count += s.getRate();
		}

		return count;
	}

	/**
	 * TODO - Programm Method
	 * 
	 * @since BETA-0.1.0
	 */
	public void assigner() {
		LOGGER.info("Start Calculating");

		this.loadedallStudents = this.allStudents;
		this.loadedallCourses = this.allCourses;

		ArrayList[] copiedData0 = this.copyData(this.allStudents, this.allCourses, ignoredCourse);

		loadedallStudents = copiedData0[0];
		loadedallCourses = copiedData0[1];

		ProgressIndicator.getInstance().setfProgressMax(Config.runs).setfProgressValue(0);
		for (int ij = 0; ij < Config.runs; ij++, ProgressIndicator.getInstance().addfProgressValue(1)) {

			LOGGER.info("Start calculation " + ij + " of " + Config.runs);

			this.allCourses.clear();
			this.allStudents.clear();

			ArrayList[] copiedData1 = this.copyData(this.loadedallStudents, this.loadedallCourses, ignoredCourse);

			allStudents = copiedData1[0];
			allCourses = copiedData1[1];

			// this.synchroniseStudentAndCourses();

			for (int i = 0; i < Config.newCalculating; i++) {
				ProgressIndicator.getInstance().setaProgressMax(this.allCourses.size()).setaProgressValue(0);
				Collections.shuffle(this.allStudents);
				for (Student s : this.allStudents)
					if (!s.next())
						s.mark();

				int priority = this.rate();

				this.save();
			}

			for (int i = 0; i < Config.improvingOfCalculation; i++) {
				this.loadDataFromSave(
						Distributor.calculated.get(References.RAND_GEN.nextInt(Distributor.calculated.size())));

				int priority = 0;

				for (Student s : this.allStudents)
					if (s.isMarked() || s.getPriority() == Integer.MAX_VALUE)
						s.setActiveCourse(s.getCourses()[0]);
					else
						priority = priority < s.getPriority() ? s.getPriority() : priority;

				for (Student s : this.allStudents)
					if (s.getPriority() == priority) {
						if (s.getActiveCourse().contains(s))
							s.getActiveCourse().removeStudent(s);
						s.setActiveCourse(s.getCourses()[0]);
					}

				Collections.shuffle(allCourses);

				if (Config.newImproving) {
					while (this.isAnyCourseFull()) {
						for (Course c : this.allCourses) {
							if (c.isFull()) {
								ArrayList<Student> students = new ArrayList<Student>(c.getStudents());
								Collections.shuffle(students);

								for (Student s : students)
									if (!s.onlyNext())
										s.mark();
							}

						}
					}

				} else
					while (this.isAnyCourseFull()) {
						for (Course c : this.allCourses) {
							Student first = null;
							while (c.isFull()) {
								ArrayList<Student> students = new ArrayList<Student>(c.getStudents());
								Collections.shuffle(students);
								for (Student s : students) {

									if (first == null)
										first = s;
									if (first.equals(s) && c.isFull()) {
										if (!s.next())
											s.mark();
										first = null;
									}
//								if (!s.onlyNext())
//									s.mark();
									if (!c.isFull())
										break;
								}
							}
						}
					}
				this.save();

			}

//			for (Save s : Distributor.calculated.list)
//				s.getInformation().update();
		}
		ProgressIndicator.getInstance().setfProgressMax(Config.runs).setfProgressValue(0);

		LOGGER.info("Finished Calculating");
	}

	/**
	 * Geht die {@link #allCourses List aller Kurse} durch und überprüft, ob die
	 * Methode {@link Course#isFull()} {@code true} zurückgibt. Sollte dies der Fall
	 * sein, so wird {@code true} zurückgegeben, ansonsten wird {@code false}
	 * zurückgegeben.
	 * 
	 * @return Ob <b><u>ein</u></b> Kurs aus der {@link #allCourses Liste aller
	 *         Kurse} überfüllt ist.
	 */
	private boolean isAnyCourseFull() {
		for (Course c : this.allCourses)
			if (c.isFull())
				return true;
		return false;
	}

	public ArrayList[] copyData(ArrayList<Student> oldStudents, ArrayList<Course> oldCourses, Course ignoredCourse2) {

		ArrayList<Student> newStudents = new ArrayList<Student>();
		ArrayList<Course> newCourses = new ArrayList<Course>();

		for (Student s : oldStudents)
			try {
				newStudents.add((Student) s.clone());
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}

		for (Course c : oldCourses)
			try {
				newCourses.add((Course) c.clone());
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}

		for (Student s : oldStudents) {
			for (Student news : newStudents) {
				if (s.equals(news)) {
					for (Course cc : s.getCourses()) {
						for (Course c : newCourses) {
							if (cc.equals(c)) {
								news.addCourse(s.getPosition(c), c);
								if (s.getActiveCourse() != null && s.getActiveCourse().equals(c))
									news.setActiveCourse(c);
							}
						}
						if (cc.equals(ignoredCourse2)) {
							news.addCourse(s.getPosition(ignoredCourse2), ignoredCourse2);
							news.setActiveCourse(ignoredCourse2);
						}
					}
				}
			}
		}
		return new ArrayList[] { newStudents, newCourses };
	}

	public void save() {
		ArrayList<Student> students = new ArrayList();
		ArrayList<Student> ignorestudents = (ArrayList<Student>) this.ignoredStudents.clone();
		ArrayList<Course> courses = new ArrayList();

		for (Student s : ignorestudents) {
			s.addCourse(this.ignore());
			s.setActiveCourse(this.ignoredCourse);
		}

		ArrayList[] copiedData = this.copyData(this.allStudents, this.allCourses, ignoredCourse);

		students = copiedData[0];
		courses = copiedData[1];

		Save save = new Save(students, ignorestudents, courses,
				new InformationSave(this.getHighestPriority(), this.rate(), this.getPriorities(),
						this.getUnallocatedStudents(),
						this.getStudentsWithPriority(this.getHighestPriorityWhithoutIntegerMax())));

		Distributor.calculated.add(save);
	}

	private ArrayList<Student> getStudentsWithPriority(int priority) {
		ArrayList<Student> pStudents = new ArrayList<>();

		for (Student s : this.allStudents)
			if (s.getPriority() == priority)
				pStudents.add(s);

		return pStudents;
	}

	private ArrayList<Student> getUnallocatedStudents() {
		ArrayList<Student> pStudents = new ArrayList<>();

		int highestPriority = this.getHighestPriorityWhithoutIntegerMax();

		this.allStudents.forEach(s -> s.checkMarkt(highestPriority));

		for (Student s : this.allStudents)
			if (s.isMarked())
				pStudents.add(s);

		return pStudents;
	}

	private int[] getPriorities() {
		int[] priorities = new int[this.getHighestPriorityWhithoutIntegerMax() + 1];

		for (int i = 0; i < priorities.length - 1; i++)
			priorities[i] = this.countPriority(i + 1);

		priorities[priorities.length - 1] = this.countPriority(Integer.MAX_VALUE);
		return priorities;
	}

	private int countPriority(int priority) {
		int count = 0;
		for (Student s : this.allStudents)
			if (s.getPriority() == priority)
				count++;
		return count;
	}

	/**
	 * TODO Comment
	 * 
	 * @return
	 */
	private int getHighestPriorityWhithoutIntegerMax() {
		int highest = 0;
		for (Student s : this.allStudents)
			if (s.getPriority() != Integer.MAX_VALUE)
				highest = highest >= s.getPriority() ? highest : s.getPriority();
		return highest;
	}

	public int getHighestPriority() {
		int highest = 0;
		for (Student s : this.allStudents)
			highest = highest >= s.getPriority() ? highest : s.getPriority();
		return highest;
	}

	/**
	 * @since 0.1
	 */
	private void allgorithmus1() {
		ProgressIndicator.getInstance().setaProgressMax(this.allCourses.size()).setaProgressValue(0);

		for (Course c : this.allCourses) {
			// LOGGER.info("Check course: " + c.toString());

			int iterator = 1;
			boolean active = true, shouldRun = c.isFull();
			while (shouldRun) {
				ArrayList<Student> students = Util.randomize(c.getStudents());
				boolean testActive = false;
				for (Student s : students) {

					if (!c.isFull())
						break;
					// LOGGER.info("Check student: " + s.toString());
					Course nextCourse = s.getNextCourse(c, iterator);

					if (nextCourse == null)
						continue;

					if (active) {
						if (!nextCourse.isFull()) {
							nextCourse.addStudent(s);
							s.setActiveCourse(nextCourse);
							c.removeStudent(s);

							// LOGGER.info("Remove1 " + c.toString());
							testActive = true;
						}
					} else {
						nextCourse.addStudent(s);
						s.setActiveCourse(nextCourse);
						c.removeStudent(s);
						// LOGGER.info("Remove2 " + c.toString());
					}
				}

				active = testActive;
				shouldRun = c.isFull();
				iterator++;

			}
			ProgressIndicator.getInstance().addaProgressValue(1);
		}
		ProgressIndicator.getInstance().setaProgressMax(1).setaProgressValue(0);
	}

	private void allgorithm3() {
		ArrayList<Student> randomized = Util.randomize(this.allStudents);
		presort(randomized);

	}

	public int areCoursesCorrect() {
		int toReturn = 0;
		boolean all = true;
		for (Course c : this.allCourses) {
			if (!c.isFull())
				continue;
			LOGGER.info(c.toString());
			toReturn = 2;
			for (Student s : c.getStudents()) {
				if (s.getPriority() == Integer.MAX_VALUE)
					break;
				if (s.getPriority() >= Config.normalStudentLimit)
					all = false;
			}

		}
		if (all == false)
			toReturn = 1;
		return toReturn;
	}

	/**
	 * 
	 * @since 1.0
	 */
	public void algorithmus3() {
		for (Course c : allCourses) {
			if (!c.isFull())
				continue;

		}
	}

	private void presort(ArrayList<Student> students) {
		for (Student s : students) {
			if (!s.next()) {
				this.problems.add(s);
				this.allStudents.remove(s);
			}
		}
	}

	private void synchroniseStudentAndCourses() {
		this.allStudents.forEach(s -> {
			for (Course c : s.getCourses()) {
				if (!this.allCourses.contains(c))
					this.allCourses.add(c);
			}
		});
	}

	private void print() {
		for (Course c : this.allCourses) {
			LOGGER.info(c.toString());
			LOGGER.info(c.studentsToString());
		}
	}

	private boolean doesCourseExist(String name) {
		for (Course c : this.allCourses) {
			if (name.equals(c.toString()))
				return true;
		}
		return false;
	}

	public ArrayList getCalcStudents() {
		return this.allStudents;
	}

	public ArrayList getIgnoreStudents() {
		return this.ignoredStudents;
	}

	private static int nextID = 0;

	public static int getStudentID() {
		References.LOGGER.info(nextID + "");
		return Distributor.nextID++;
	}

	// INFO: Kurs

	/**
	 * Sucht nach dem {@link Course Kurs} mit dem selben Namen in der
	 * {@link #allCourses Kursliste} und gibt diesen zurück. Sollte der
	 * {@link Course Kurs} nicht existieren, so wird ein neuer Kurs mit dem
	 * angegebenen Namen erstellt.
	 * 
	 * @param name Der Name des Kurses
	 * @return Den Kurs
	 */
	public Course getOrCreateCourseByName(String name) {
		if (name == null || Util.isBlank(name))
			return null;

		// Überprüft ob der Kurs dem ignoredCourse entspricht.

		if (Util.isIgnoreCourse(name.replace("|", "")))
			return this.ignoredCourse;

		Course c = this.getCourseByName(name);
		if (c == null)
			this.allCourses.add(c = new Course(name.split("\\|")));
		return c;
	}

	/**
	 * Sucht nach dem {@link Course Kurs} mit dem selben Namen in der
	 * {@link #allCourses Kursliste} und gibt diesen zurück. Sollte der
	 * {@link Course Kurs} nicht existieren, so wird {@code null} zurückgegeben.
	 * 
	 * @param name Der Name des gesuchten Kurses.
	 * @return Der gesuchte {@link Course Kurs}.
	 */
	public Course getCourseByName(String name) {
		for (Course c : this.allCourses) {
			if (name.equalsIgnoreCase(c.toString()))
				return c;
		}
		return null;
	}

	/**
	 * Gibt alle Kurse zurück
	 * 
	 * @return Eine {@link ArrayList} aller Kurse.
	 */
	public ArrayList<Course> getCourses() {
		return this.allCourses;
	}

	/**
	 * Gibt den Kurs zurück, der nicht in die Berechnung des Distributors
	 * miteinbezogen wird
	 * 
	 * @return {@link #ignoredCourse}
	 */
	public Course ignore() {
		return this.ignoredCourse;
	}

	// INFO: DEPRECATED

	/**
	 * Fügt einen {@link Student Schüler} zur {@link #allStudents Liste der Schüler}
	 * hinzu.
	 * 
	 * @param s Der {@link Student Schüler} der hinzugefügt werden soll.
	 */
	public void addStudent(Student s) {
		// FIXME
		if (s == null)
			return;

		if (this.allStudents.contains(s))
			this.allStudents.remove(s);

		this.allStudents.add(s);

	}

	/**
	 * @deprecated Directly add the Student to the course instead of use this Method
	 * @param student    The student to Add to the course
	 * @param courseName The c
	 */
	@Deprecated
	private void addStudentToCourse(Student student, String courseName) {
		for (Course c : this.allCourses) {
			if (courseName.equals(c.toString())) {
				c.addStudent(student);
				student.setActiveCourse(c);
			}
		}
	}

	// INFO: Import

	/**
	 * <p>
	 * Verwaltet das Einlesen der Dateien in den {@link Distributor}. Es gibt den
	 * Status des Einlesens im LOG aus.
	 * </p>
	 * 
	 * <p>
	 * Zunächst wird der File in eine List des Zwischenformates
	 * {@link WriteableContent} umgewandelt. Dazu wird {@link #importFile(path)}
	 * aufgerufen. Sollte ein Fehler auftreten so wird dieser mit dem
	 * {@link Level#SEVERE Log-Level SEVERE} und dem erklärenden Text: "Unable to
	 * load data!", sowie der Fehlermeldung ausgegeben und das Einlesen der Datei
	 * wird abgebrochen.
	 * </p>
	 * 
	 * <p>
	 * Im folgenden wird dann jeder {@link WriteableContent} in den Distributor über
	 * die Methode {@linkplain #readGrid(Tabellen_Überschrift, Tabelle, path)}
	 * eingelesen. Die Tabelle wird hirzu über {@link WriteableContent#getGrid()
	 * getGrid()} und die Tabellen Überschrift durch die Methode
	 * {@link WriteableContent#getName() getName()} weitergegeben.
	 * </p>
	 * 
	 * @param path Der Pfad zur Datei, die Eingelesen werden soll.
	 */
	private void readFile(String path) {
		LOGGER.info("Start importing data from " + path + ".");
		List<WriteableContent> wcs;
		try {
			wcs = this.importFile(path);
		} catch (IOException | URISyntaxException e) {
			LOGGER.log(Level.SEVERE, "Unable to load data!", e);
			return;
		}
		LOGGER.info("Finish importing data from " + path + ".");

		wcs.forEach(wc -> this.readGrid(wc.getName(), wc.getGrid(), path));

//		LOGGER.info("Start matching data!");
//		this.synchroniseStudentAndCourses();
//		LOGGER.info("Finish matching data!");
		LOGGER.config("Finished importing Data in the Distributor!");
	}

	/**
	 * Lässt die Datei je nach Dateityp auslesen und gibt die Tabellen in Form einer
	 * {@link List Liste} aus {@link de.juhu.dateimanager.WriteableContent
	 * WriteableContents} zurück.
	 * 
	 * @param path Der Dateipfad der Ausgelesen werden soll.
	 * @return Eine {@link List Liste} aus {@link WriteableContent
	 *         WriteableContents}.
	 * @throws IOException        Wenn die Datei nicht verarbeitet werden kann.
	 * @throws URISyntaxException Wenn der filename nicht existiert.
	 */
	private List<WriteableContent> importFile(String path) throws IOException, URISyntaxException {
		path = path.toLowerCase();
		List<WriteableContent> c = new ArrayList<>();
		if (path.endsWith(".xls"))
			c = readXLSImproved(path);
		else if (path.endsWith(".xlsx"))
			c = readXLSXImproved(path);
		else if (path.endsWith(".csv"))
			c.add(readCSV(path));
		else
			c.add(readCSV(path + ".csv"));
		return c;
	}

	/**
	 * Gibt die importierten Daten in den entsprechenden
	 * {@link de.juhu.distributor.Reader Reader} weiter zum importieren in den
	 * {@link Distributor}
	 * 
	 * @param gridName Der Name der Tabelle. Entscheidet darüber an welchen
	 *                 {@link de.juhu.distributor.Reader Reader} die Daten bei einer
	 *                 unspezifischen Angabe weitergegeben werden.
	 * @param grid     Die importierten Daten in einem {@link String[][]}
	 * @param filename Der Name der Datei die eingelesen wurde
	 */
	private void readGrid(String gridName, String[][] grid, String filename) {
		LOGGER.info("Start to load data from " + filename + ".");

		/*
		 * Zählt die Zeilen der Tabelle mit.
		 */
		int lineNumber = 0;

		this.loadReaders();

		/*
		 * Hauptschleife:
		 */
		for (String[] line : grid) {

			if (line.length < 1 || line[0] == null) {
				lineNumber++;
				continue;
			}

			final boolean isCommand = this.isReaderKey(line[0]);

			if (line[0].startsWith(Config.commentLine)) {
				String information = "";
				for (String s : line) {
					if (s != null)
						information += "|" + s;
				}
				LOGGER.info(
						"The commentation-Line was ignored. Here the information of this Commentation: " + information);
				lineNumber++;
				continue;
			}

			for (Reader r : readers) {
				LOGGER.config(line[0] + " | " + r.key);

				if (r.isKey(line[0]))
					r.read(Util.removeFirst(line), lineNumber);
				else if (r.isKey(gridName) && !isCommand)
					r.read(line, lineNumber);
			}

			lineNumber++;
		}

		LOGGER.info("File data loaded!");
	}

	// INFO: IMPORT READER

	/**
	 * Spiechert alle {@link Reader}, die beim Einlesen der Daten aktiv sind.
	 */
	private ArrayList<Reader> readers = new ArrayList<>();

	/**
	 * Überprüft, ob der eingegebene String auf einen
	 * {@link de.juhu.distributor.Reader Reader} aus der Liste
	 * {@link Distributor#readers reader} verweist.
	 * 
	 * @param input Der zu überprüfende Key.
	 * @return Ob der Key zu einem {@link de.juhu.distributor.Reader Reader} passt.
	 */
	private boolean isReaderKey(String input) {
		for (Reader r : readers) {
			if (r.isKey(input))
				return true;
		}
		return false;
	}

	/**
	 * Fügt einen {@link Reader} zu den {@link #readers aktiven Readern} hinzu.
	 * 
	 * @param reader Der {@link Reader}, der hinzugefügt werden soll.
	 * @return Ob der {@link Reader} hinzugefügt werden konnte.
	 */
	public boolean addReader(Reader reader) {
		if (reader == null || this.isReaderKey(reader.key))
			return false;

		this.readers.add(reader);
		return true;
	}

	/**
	 * Lädt die Standart {@link Reader} in {@link #readers}.
	 */
	private void loadReaders() {
		this.addReader(new Reader(Config.newStudent) {
			@Override
			public void read(String[] line, int lineNumber) {
				if (line.length < 2) {
					LOGGER.warning(
							"Not enough arguments for student. This line will be skiped: [Line: " + lineNumber + "]");
					lineNumber++;
					return;
				}
				// TODO - Mehrere Ignor MArks möglich machen
				if (line[2].equals(Config.ignoreStudent)) {
					Distributor.getInstance().ignoredStudents
							.add(new Student(line[0], line[1], Distributor.getInstance().ignore()));
					LOGGER.fine("Student with name " + line[1] + " " + line[0]
							+ " was added to the not relevant students.");
				} else {
					Course[] chooses = new Course[(line.length - 2) / 2];
					for (int i = 2, c = 0; i + 1 < line.length; i += 2, c++) {
						if (line == null || line[i] == null || line[i].equals(null))
							continue;
						chooses[c] = Distributor.getInstance().getOrCreateCourseByName(line[i] + "|" + line[i + 1]);
					}
					Student s = new Student(line[0], line[1], chooses);
					Distributor.getInstance().allStudents.add(s);
					LOGGER.fine("Student with name " + line[2] + " " + line[1] + " was created.");
				}
			}
		});

		this.addReader(new Reader(Config.newCourse) {
			@Override
			public void read(String[] line, int lineNumber) {
				LOGGER.info("Try to Add Course " + lineNumber);
				if (line.length <= 2) {
					LOGGER.info("Not enough arguments for a new Course. This line will be skiped: [Line: " + lineNumber
							+ "]");
					lineNumber++;
					return;
				}
				int countStudents = Config.normalStudentLimit;
				try {
					countStudents = Integer.parseInt(line[2]);
				} catch (NumberFormatException e) {
					LOGGER.warning("There was no expliciet limit of Students in course " + line[1] + "|" + line[2]
							+ ". The limit is set to the default value " + Config.normalStudentLimit + "!");
					countStudents = Config.normalStudentLimit;
				}
				Course c = Distributor.getInstance()
						.getOrCreateCourseByName(line[0].replaceAll(" ", "") + "|" + line[1].replaceAll(" ", ""));
				c.setStudentMax(countStudents);

				LOGGER.fine("The course " + c.toString() + " was added to the courses with the student limit "
						+ countStudents + ".");
			}
		});
	}

	public Student getStudentByID(int studentID) {
		for (Student s : this.allStudents)
			if (s.idequals(studentID))
				return s;
		return null;
	}

	public void addCourse(Course c) {
		if (this.allCourses.contains(c))
			this.allCourses.remove(c);
		this.allCourses.add(c);
	}

	public void reset() {
		for (Student s : this.allStudents)
			s.setActiveCourse(null);
	}

	public void clear() {
		boolean clear = Config.clear;

		Config.clear = true;

		this.allStudents = new ArrayList<>();
		this.allCourses = new ArrayList<>();

		new Distributor();

		Config.clear = clear;
	}

	public void removeStudent(Student student) {
		if (student == null)
			return;
		if (this.allStudents.contains(student))
			this.allStudents.remove(this.allStudents.indexOf(student));
	}

	public void removeCourse(Course course) {
		if (course == null)
			return;
		if (this.allCourses.contains(course))
			this.allCourses.remove(this.allCourses.indexOf(course));
	}

}
