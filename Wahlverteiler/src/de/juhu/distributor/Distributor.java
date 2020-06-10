package de.juhu.distributor;

import static de.juhu.filemanager.CSVImporter.readCSV;
import static de.juhu.filemanager.ExcelImporter.readXLSImproved;
import static de.juhu.filemanager.ExcelImporter.readXLSXImproved;
import static de.juhu.util.References.LOGGER;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import de.juhu.filemanager.WriteableContent;
import de.juhu.guiFX.GUIDoubleStudentManager;
import de.juhu.guiFX.GUILoader;
import de.juhu.guiFX.GUIManager;
import de.juhu.guiFX.ProgressIndicator;
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
 * @implements {@link Runnable}
 * @since BETA-0.0.1
 */
public class Distributor implements Runnable {

	// INFO: Ausgabe
	/**
	 * Diese Liste stellt die besten Ergebnisse zum Auslesen bereit.
	 */
	public static PriorityQueue<Save> calculated = new PriorityQueue<Save>(100);

	/**
	 * Zeigt an, ob fertigberechnete Ergebnisse ausgegeben werden k�nnen.
	 */
	public static boolean calculate = false;

	// INFO: Berechnungsinformationen
	/**
	 * Speichert die zu berechnenden Sch�ler.
	 */
	ArrayList<Student> loadedallStudents = new ArrayList<>();

	/**
	 * Speichert die zu berechnenden Kurse.
	 */
	ArrayList<Course> loadedallCourses = new ArrayList<>();

	/**
	 * Diese Liste h�lt alle f�r die Berechnung irrelevanten Sch�ler fest und stellt
	 * sie zum nachtr�glichen Einf�gen in das Ergebnis bereit.
	 */
	ArrayList<Student> ignoredStudents = new ArrayList<>();

	/**
	 * Dieser Kurs beinhaltet alle ignore Students. Er dient als Referenzkurs f�r
	 * diese.
	 */
	Course ignoredCourse = new Course(Config.ignoreStudent, "", -1);

	/**
	 * Dient zur Berechnung der Sch�ler.
	 * 
	 * @info Ver�ndert sich w�hrend der Laufzeit.
	 */
	ArrayList<Student> allStudents = new ArrayList<>();

	/**
	 * Dient zur Berechnung der Kurse.
	 * 
	 * @info Ver�ndert sich w�hrend der Laufzeit.
	 */
	ArrayList<Course> allCourses = new ArrayList<>();

	/**
	 * Enth�lt alle nicht zuteilbaren Sch�ler.
	 * 
	 * @deprecated Unused - Nur in �lteren Fehlerhaften Zuweisungsmethoden benutzt,
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
	 * ausgef�hrt, wenn noch keine Instanz vorhanden ist und {@link #getInstance()}
	 * aufgerufen wird, oder alle Berechnungen geleert werden sollen, sowie die
	 * eingelesenden Daten.
	 */
	protected Distributor() {
		if (instance != null && !Config.clear) {
			this.allStudents = Distributor.getInstance().loadedallStudents;
			this.allCourses = Distributor.getInstance().loadedallCourses;
			this.ignoredStudents = Distributor.getInstance().ignoredStudents;
		}
		if (instance != null)
			this.readers = instance.readers;
		else
			this.loadReaders();

		instance = this;

		this.loadedallStudents = this.allStudents;
		this.loadedallCourses = this.allCourses;
	}

	/**
	 * Erstellt eine Neue Instanz dieser Klasse und setzt die Vorhandende Instanz
	 * auf die hier erstellte. Sollte {@link Config#clear} false sein, so werden die
	 * Gespeicherten Sch�ler und Kurse der vorherigen in {@link #instance}
	 * gespeicherten Instanz automatisch in die neue miteingerechnet.
	 * 
	 * Gleichzeitig werden die Sch�ler und Kursdaten aus dem mitgegebenen Pfad �ber
	 * die Methode {@link #readFile(String)} eingelesen und in die Bestehenden Daten
	 * eingespei�t.
	 * 
	 * @param filename Der Pfad, aus dem die ben�tigten Sch�ler und Kurs Daten
	 *                 herausgelesen werden.
	 */
	public Distributor(String filename) {
		if (instance != null)
			this.readers = instance.readers;
		else
			this.loadReaders();

		// L�dt, falls gew�nscht die Daten aus der alten Instanz in die Neue.
		if (!Config.clear) {
			this.allStudents = Distributor.getInstance().loadedallStudents;
			this.allCourses = Distributor.getInstance().loadedallCourses;
			this.ignoredStudents = Distributor.getInstance().ignoredStudents;
		}

		// Setzt die aktuelle Instanz auf diese.
		Distributor.instance = this;

		// Lie�t die Dateien in das System ein.
		this.readFile(filename);
		this.loadedallStudents = this.allStudents;
		this.loadedallCourses = this.allCourses;

	}

	/**
	 * L�dt die Daten des ersten mitgegebenen {@link Save Speichers} in den
	 * {@link Distributor Berechner}. F�gt die anderen beiden Speicherungen und die
	 * geladene in die {@link Distributor#calculated Liste der Kalkulationen} ein.
	 * 
	 * @implNote Alle vorherigen Daten werden gel�scht.
	 * @param actual      Der {@link Save Speicher}, welcher geladen und dann in die
	 *                    {@link Distributor#calculated Liste der Kalkulationen}
	 *                    eingef�gt wird.
	 * @param readObject  Der erste {@link Save Speicher}, der in die
	 *                    {@link Distributor#calculated Liste der Kalkulationen}
	 *                    eingef�gt wird.
	 * @param readObject2 Der zweite {@link Save Speicher}, der in die
	 *                    {@link Distributor#calculated Liste der Kalkulationen}
	 *                    eingef�gt wird.
	 */
	public Distributor(Save actual, Save... readObject) {
		if (instance != null)
			this.readers = instance.readers;
		else
			this.loadReaders();

		// Setzt die aktuelle Instanz auf diese.
		Distributor.instance = this;

		// Lie�t die Saves in das System ein.
		this.loadDataFromSave(actual);
		this.loadedallStudents = this.allStudents;
		this.loadedallCourses = this.allCourses;

		// F�gt alle drei Speicherungen zu den Kalkulationen hinzu.
		Distributor.calculated.add(actual);
		for (Save s : readObject)
			Distributor.calculated.add(s);
	}

	/**
	 * Startet den Gew�nschten Berechnungs- und Zuweisungsprozess. �berwacht, dass
	 * nur eine Berechnung zur gleichen Zeit abl�uft. L�dt, wenn keine Sch�lerdaten
	 * vorhanden sind und ein Pfad angegeben ist, welcher auf eine existierende
	 * Tabellen-Datei verweist diese Tabellen-Datei in das Programm und berechnet
	 * f�r die neu geladenen Daten eine Zuweisung.
	 * 
	 * Macht zuletzt die fertige Berechnung im GUI sichtbar.
	 */
	@Override
	public void run() {
		/*
		 * �berpr�ft, ob bereits eine Instanz ge�fnet ist und bricht, wenn bereits eine
		 * Instanz ge�ffnet ist den Prozess ab.
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

		/*
		 * L�dt falls keine Daten geladen sind und eine Datei angegeben ist, diese in
		 * den Zuweiser.
		 */
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

		/*
		 * Gibt relevante Informationen in den Log aus.
		 */
		LOGGER.config("Distributor Started whith this specificies: ");
		LOGGER.config("--Basic Student limit: " + Integer.toString(Config.normalStudentLimit));
		LOGGER.config("--The chooses of the Students: " + Integer.toString(Config.maxChooses));
		LOGGER.config("--The number of students to Calculate: " + Integer.toString(this.allStudents.size()));

		calculate = true;

		calculated = new PriorityQueue<>(100);

		for (Student s : this.allStudents)
			s.setActiveCourse(null);

		this.assign();

		for (Save s : Distributor.calculated.list)
			s.getInformation().update();

		GUIManager.actual = Distributor.calculated.peek();

		/*
		 * L�dt die Daten in die Ausgabe-Vorschau
		 */

		Platform.runLater(() -> {
			GUIManager.getInstance().counter
					.setText(Integer.toString(Distributor.calculated.indexOf(GUIManager.actual) + 1));
			GUIManager.getInstance().b1.setDisable(true);
			GUIManager.getInstance().b4.setDisable(false);
			GUIManager.getInstance().masterTabPane.getSelectionModel().select(GUIManager.getInstance().tabOutput);
		});

		Platform.runLater(GUIManager.getInstance().outputSView);
		Platform.runLater(GUIManager.getInstance().outputCView);
		Platform.runLater(GUIManager.getInstance().outputIView);

		calculate = false;

		this.printRate();
	}

	/**
	 * L�dt die Daten eines {@link Save Speichers} in den {@link Distributor}.
	 * 
	 * @param save Der zu ladene {@link Save Speicher}.
	 */
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
					|| this.allStudents.get(i).getCoursesAsList().contains(this.ignore()))
				this.ignoredStudents.add(this.allStudents.remove(i));
			else
				i++;
		}
	}

	/**
	 * Gibt die Rate der Berechnung mit der Priorit�t info in der Konsole aus.
	 */
	public void printRate() {
		for (int i = Config.maxChooses; i-- > 0;) {
			LOGGER.info("Students that get their " + (i + 1) + ". choise: " + this.getStudentsWithRate(i));
		}
	}

	/**
	 * Ermittelt die Anzahl der Sch�ler mit der entsprechenden Rate.
	 * 
	 * @param rate Die Rate nach der gesucht wird.
	 * @return Die Anzahl der Sch�ler mit der entsprechenden Rate.
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
	 * @return Der G�te Wert der Berechnung
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
	 * <p>
	 * Wei�t die im {@link Distributor} hinterlegten Daten bestm�glich zu. Dabei
	 * wird versucht die {@link Student Sch�ler} aus der {@link #allStudents Liste
	 * der Sch�ler} so auf die {@link Course Kurse} aus der {@link #allCourses Liste
	 * der Kurse} zu verteilen, dass m�glichst viele Sch�ler einen m�glichst guten
	 * Kurs aus ihren {@link Student#courses Kurswahlen} zugewiesen bekommen.
	 * </p>
	 * <p>
	 * Um die bestm�gliche Verteilung zu erreichen wird nach einem kontrollierten
	 * Zufalls und Verbesserungsprinzip vorgegangen. Daher besteht jeder der unter
	 * {@link Config#runs} angegebenen durchl�ufe aus folgenden zwei Schritten:
	 * <ul>
	 * <li>Dem neu Zuweisen</li>
	 * <li>und dem Verbessern.</li>
	 * </ul>
	 * </p>
	 * <p>
	 * Bevor die Berechnung starten kann, werden die Sch�ler und Kursdaten zun�chst
	 * einmal in die {@link #loadedallStudents} und {@link #loadedallCourses} Listen
	 * gesichert, so dass diese Daten immer zur unverf�lschten neuberechnung genutzt
	 * werden k�nnen.
	 * </p>
	 * <p>
	 * Um eine graphische �bersicht zu bekommen wird die {@link ProgressIndicator
	 * Prozess Leiste} mit den entsprechenden Werten initialisiert.
	 * </p>
	 * <p>
	 * Nun werden die {@link Config#runs} vielen Versuche gestartet. Mit jedem
	 * durchlaufendem Versuch wird die {@link ProgressIndicator Prozess Leiste} um
	 * einen Wert erh�ht, sowie eine Nachricht mit der aktuellen Durchlaufsnummer
	 * ausgegeben.
	 * </p>
	 * <p>
	 * Dann werden versuche einer Neuzuweisung gestartet. Die Menge dieser neu
	 * Zuweisungsversuche ist �ber {@link Config#newCalculating} einstellbar.
	 * 
	 * Zur Neuzuweisung werden die Daten aus den Sicherheitsspeichern des
	 * {@link Distributor Berechners} geladen und dann wird die {@link #allStudents
	 * Liste der Sch�ler} gemischt. F�r jeden Sch�ler wird dann �ber die Methode
	 * {@link Student#next()} versucht ein Kurs zu finden. Kann kein Kurs gefunden
	 * werden wird der Sch�ler �ber {@link Student#mark()} markiert. Nach dieser
	 * Zuweisung werden die Daten �ber {@link #save()} gespeichert.
	 * </p>
	 * <p>
	 * Nach den Versuchen der Neuzuweisung werden noch
	 * {@link Config#improvingOfCalculation} viele Versuche unternommen eine
	 * zuf�llig ausgew�hlte Kalkulation zu verbessern. Hierzu wird eine Zuf�llige
	 * gespeicherte Zuweisung aus der {@link #calculated Liste der Speicher} geladen
	 * und dann eine Neuberechnung an diesem durchgef�hrt. Dazu wird allen nicht
	 * zugewiesenden (also markierten) Sch�ler deren erster Kurs gew�hrt und im
	 * Anschluss allen Sch�lern mit der schlechtesten Priorit�t das Gleiche gew�hrt.
	 * Nach dem Mischen der Kurse wird nun �berpr�ft, welche Kurse zu voll sind.
	 * Sollte ein Kurs zu viele Sch�ler beinhalten werden, wenn
	 * {@link Config#newImproving} aktiviert ist, Sch�ler aus diesem Kurs zuf�llig
	 * in deren n�chsten Gew�nschten Kurs verschoben. Ist
	 * {@link Config#newImproving} deaktiviert, so werden aus dem Kurs zuf�llig
	 * Sch�ler in deren n�chsten Freien Kurs verschoben.
	 * </p>
	 * <p>
	 * Nach Beendigung der {@link Config#runs} vielen durchl�ufe wird noch die
	 * {@link ProgressIndicator Prozess Leiste} zur�ckgesetzt.
	 * </p>
	 * 
	 * 
	 * @since BETA-0.1.0
	 * @implNote Diese Methode wird in Verkn�pfung mit der graphischen Oberfl�che
	 *           und weiteren kleinen Zus�tzlichen Eigenschaften �ber {@link #run()}
	 *           ausgef�hrt.
	 */
	public void assign() {
		LOGGER.info("Start Calculating");

		this.loadedallStudents = this.allStudents;
		this.loadedallCourses = this.allCourses;

		ArrayList[] copiedData0 = this.copyData(this.allStudents, this.allCourses, ignoredCourse);

		loadedallStudents = copiedData0[0];
		loadedallCourses = copiedData0[1];

		ProgressIndicator.getInstance().setfProgressMax(Config.runs).setfProgressValue(0);
		for (int ij = 0; ij < Config.runs; ij++, ProgressIndicator.getInstance().addfProgressValue(1)) {

			LOGGER.info("Start calculation " + ij + " of " + Config.runs);

			// this.synchroniseStudentAndCourses();

			for (int i = 0; i < Config.newCalculating; i++) {
				this.allCourses.clear();
				this.allStudents.clear();

				ArrayList[] copiedData1 = this.copyData(this.loadedallStudents, this.loadedallCourses, ignoredCourse);

				allStudents = copiedData1[0];
				allCourses = copiedData1[1];

				ProgressIndicator.getInstance().setaProgressMax(this.allCourses.size()).setaProgressValue(0);

				Collections.shuffle(this.allStudents);
				for (Student s : this.allStudents)
					if (!s.next())
						s.mark();

				// int priority = this.rate();

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

				this.save();

			}

//			for (Save s : Distributor.calculated.list)
//				s.getInformation().update();
		}
		ProgressIndicator.getInstance().setfProgressMax(Config.runs).setfProgressValue(0);

		LOGGER.info("Finished Calculating");
	}

	/**
	 * Geht die {@link #allCourses Liste aller Kurse} durch und �berpr�ft, ob die
	 * Methode {@link Course#isFull()} {@code true} zur�ckgibt. Sollte dies der Fall
	 * sein, so wird {@code true} zur�ckgegeben, ansonsten wird {@code false}
	 * zur�ckgegeben.
	 * 
	 * @return Ob <b><u>ein</u></b> Kurs aus der {@link #allCourses Liste aller
	 *         Kurse} �berf�llt ist.
	 */
	private boolean isAnyCourseFull() {
		for (Course c : this.allCourses)
			if (c.isFull())
				return true;
		return false;
	}

	/**
	 * Erzeugt zwei von den alten {@link Student Sch�ler} und {@link Course Kurs}
	 * Listen unabh�ngige Listen, die in selber Weise verkn�pft sind. Dabei ist das
	 * erste Element im Array, die kopierte Liste der Sch�ler und das zweite die
	 * kopierte Liste der Lehrer.
	 * 
	 * @param oldStudents    Die Liste der Sch�ler die Kopiert werden soll.
	 * @param oldCourses     Die verkn�pfte Liste der Kurse die Kopiert werden soll.
	 * @param ignoredCourse2 Der Kurs f�r nicht zugeordnete Sch�ler.
	 * @return Die Kopierten Daten.
	 *         <ul>
	 *         <li>Element 1: Liste der Sch�ler (kopiert)</li>
	 *         <li>Element 2: Liste der Kurse (kopiert)</li>
	 *         </ul>
	 */
	public ArrayList[] copyData(ArrayList<Student> oldStudents, ArrayList<Course> oldCourses, Course ignoredCourse2) {

		// Erzeugung der neuen Arrays

		/*
		 * Die neue Sch�ler-Liste, in die die Kurse Kopiert werden.
		 */
		ArrayList<Student> newStudents = new ArrayList<Student>();

		/*
		 * Die neue Kurs-Liste, in die die Kurse Kopiert werden.
		 */
		ArrayList<Course> newCourses = new ArrayList<Course>();

		/*
		 * Kopieren der Sch�ler ohne Verkn�pfungen.
		 */
		for (Student s : oldStudents)
			try {
				newStudents.add((Student) s.clone());
			} catch (CloneNotSupportedException e) {
				LOGGER.log(Level.SEVERE, "Error while copy Student Data", e);
			}

		/*
		 * Kopieren der Kurse ohne Verkn�pfungen
		 */
		for (Course c : oldCourses)
			try {
				newCourses.add((Course) c.clone());
			} catch (CloneNotSupportedException e) {
				LOGGER.log(Level.SEVERE, "Error while copy Course Data", e);
			}

		/*
		 * Erstellen der ben�tigten Verkn�pfungen
		 */
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

		/*
		 * R�ckgabe der neu erstellten Listen
		 */
		return new ArrayList[] { newStudents, newCourses };
	}

	/**
	 * Speichert die aktuellen Daten im Distributor in einen {@link Save neuen
	 * Speicher} und f�gt diesen in {@link #calculated Liste der Speicher} ein.
	 * Dabei werden die Daten �ber
	 * {@link Distributor#copyData(ArrayList, ArrayList, Course)} kopiert und die
	 * Kopierten Daten in den Speicher geladen, so dass die im Speicher enthaltenen
	 * Daten nicht mehr mit den Daten im {@link Distributor} verkn�pft sind.
	 */
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

		Save save = new Save(students, ignorestudents, courses
		/*
		 * new InformationSave(this.getHighestPriority(), this.rate(),
		 * this.getPriorities(), this.getUnallocatedStudents(),
		 * this.getStudentsWithPriority(this.getHighestPriorityWhithoutIntegerMax()))
		 */);

		Distributor.calculated.add(save);
	}

	/**
	 * Ermittelt alle Sch�ler aus der {@link #allStudents Liste der zu berechnenden
	 * Sch�ler}, deren {@link Student#priority Priorit�t} mit der mitgegebenen
	 * Priorit�t �bereinstimmt.
	 * 
	 * @param priority Die Priorit�t, nach der gesucht wird.
	 * @return Eine Liste aller Sch�ler, deren {@link Student#priority Priorit�t}
	 *         mit der gegebenen Priorit�t �bereinstimmt.
	 */
	private ArrayList<Student> getStudentsWithPriority(int priority) {
		ArrayList<Student> pStudents = new ArrayList<>();

		for (Student s : this.allStudents)
			if (s.getPriority() == priority)
				pStudents.add(s);

		return pStudents;
	}

	/**
	 * Ermittelt alle Sch�ler aus der {@link #allStudents Liste der zu berechnenden
	 * Sch�ler}, die keinem Kurs zugeordnet sind.
	 * 
	 * @return Eine Liste aller nicht zugewiesenden Sch�ler
	 */
	private ArrayList<Student> getUnallocatedStudents() {
		ArrayList<Student> pStudents = new ArrayList<>();

		int highestPriority = this.getHighestPriorityWhithoutIntegerMax();

		this.allStudents.forEach(s -> s.checkMarkt(highestPriority));

		for (Student s : this.allStudents)
			if (s.isMarked())
				pStudents.add(s);

		return pStudents;
	}

	/**
	 * Ermittelt die Anzahl der Sch�ler jeder Priorit�t. Dabei ist der letzte Index
	 * der Liste gef�llt mit der Anzahl der Sch�ler, die nicht zugewiesen werden
	 * konnten.
	 * 
	 * @return Eine Liste mit der (Priorit�t - 1) und der Zugeh�rigen Anzahl der
	 *         Sch�ler.
	 */
	private int[] getPriorities() {
		int[] priorities = new int[this.getHighestPriorityWhithoutIntegerMax() + 1];

		for (int i = 0; i < priorities.length - 1; i++)
			priorities[i] = this.countPriority(i + 1);

		priorities[priorities.length - 1] = this.countPriority(Integer.MAX_VALUE);
		return priorities;
	}

	/**
	 * Gibt die Anzahl der Sch�ler aus der {@link #allStudents Liste aller Sch�ler}
	 * zur�ck, deren {@link Student#priority Priorit�t} mit der gegebenen Priorit�t
	 * �bereinstimmt.
	 * 
	 * @param priority Die Priorit�t, nach der gesucht wird.
	 * @return Die Anzahl der Sch�ler mit der mitgegebenden Priorit�t.
	 */
	private int countPriority(int priority) {
		int count = 0;
		for (Student s : this.allStudents)
			if (s.getPriority() == priority)
				count++;
		return count;
	}

	/**
	 * Ermittelt die h�chste Priorit�t, wobei alle nicht Zugewiesenden Sch�ler
	 * unbeachtet bleiben.
	 * 
	 * @return Die h�chste Priorit�t, ohne nicht zugewiesende Sch�ler.
	 */
	public int getHighestPriorityWhithoutIntegerMax() {
		int highest = 0;
		for (Student s : this.allStudents)
			if (s.getPriority() != Integer.MAX_VALUE)
				highest = highest >= s.getPriority() ? highest : s.getPriority();
		return highest;
	}

	/**
	 * Ermittelt die h�chste Priorit�t
	 * 
	 * @return Die h�chste Priorit�t
	 * @implNote {@link Integer#MAX_VALUE}, wenn Sch�ler nicht zugewiesen werden
	 *           konnten.
	 */
	public int getHighestPriority() {
		int highest = 0;
		for (Student s : this.allStudents)
			highest = highest >= s.getPriority() ? highest : s.getPriority();
		return highest;
	}

	/**
	 * @since 0.1
	 * @deprecated Alte Version, nutze {@link #assign()}!
	 */
	@Deprecated
	private void allgorithmus1() {
		ProgressIndicator.getInstance().setaProgressMax(this.allCourses.size()).setaProgressValue(0);

		for (Course c : this.allCourses) {
			// LOGGER.info("Check course: " + c.toString());

			int iterator = 1;
			boolean active = true, shouldRun = c.isFull();
			while (shouldRun) {
				ArrayList<Student> students;
				Collections.shuffle((students = c.getStudents()));
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

	/**
	 * �berpr�ft, ob ein Kurs zu voll ist.
	 * 
	 * @return
	 * @deprecated Unused
	 */
	@Deprecated
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
	 * Gibt alle Kurse mit ihren Sch�ler im {@link References#LOGGER Log} aus.
	 */
	private void print() {
		for (Course c : this.allCourses) {
			LOGGER.info(c.toString());
			LOGGER.info(c.studentsToString());
		}
	}

	/**
	 * �berpr�ft, ob ein Kurs mit dem gleichen Namen ({@link Course#getSubject()
	 * Fach}{@code  + "|" + }{@link Course#getTeacher() Lehrer}) bereits unter den
	 * vorhandenen Kursen existiert.
	 * 
	 * @param name Der Name des Kurses.
	 * @return Ob der Kurs existiert.
	 */
	private boolean doesCourseExist(String name) {
		for (Course c : this.allCourses) {
			if (name.equals(c.toString()))
				return true;
		}
		return false;
	}

	/**
	 * @return Die {@link #allStudents Liste der Sch�ler}.
	 */
	public ArrayList getCalcStudents() {
		return this.allStudents;
	}

	/**
	 * @return Die {@link #ignoredStudents Liste der f�r die Berechnung irrelevanten
	 *         Sch�ler}
	 */
	public ArrayList getIgnoreStudents() {
		return this.ignoredStudents;
	}

	/**
	 * Die n�chste Freie ID, die an einen Sch�ler vergeben werden kann.
	 */
	private static int nextID = 0;

	/**
	 * Gibt die {@link #nextID n�chste freie ID} zur�ck und z�hlt diese um einen
	 * Wert hoch.
	 * 
	 * @return Die ID, die angefordert wurde
	 */
	public static int getStudentID() {
		// References.LOGGER.info(nextID + "");
		return Distributor.nextID++;
	}

	// INFO: Kurs

	/**
	 * Sucht nach dem {@link Course Kurs} mit dem selben Namen in der
	 * {@link #allCourses Kursliste} und gibt diesen zur�ck. Sollte der
	 * {@link Course Kurs} nicht existieren, so wird ein neuer Kurs mit dem
	 * angegebenen Namen erstellt.
	 * 
	 * @param name Der Name des Kurses
	 * @return Den Kurs
	 */
	public Course getOrCreateCourseByName(String name) {
		if (name == null || Util.isBlank(name))
			return null;

		// �berpr�ft ob der Kurs dem ignoredCourse entspricht.

		if (Util.isIgnoreCourse(name.split("\\|")))
			return this.ignoredCourse;

		Course c = this.getCourseByName(name);
		if (c == null)
			this.addCourse(c = new Course(name.split("\\|")));
		return c;
	}

	/**
	 * Sucht nach dem {@link Course Kurs} mit dem selben Namen in der
	 * {@link #allCourses Kursliste} und gibt diesen zur�ck. Sollte der
	 * {@link Course Kurs} nicht existieren, so wird {@code null} zur�ckgegeben.
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
	 * Gibt alle Kurse zur�ck
	 * 
	 * @return Eine {@link ArrayList} aller Kurse.
	 */
	public ArrayList<Course> getCourses() {
		return this.allCourses;
	}

	/**
	 * Gibt den Kurs zur�ck, der nicht in die Berechnung des Distributors
	 * miteinbezogen wird
	 * 
	 * @return {@link #ignoredCourse}
	 */
	public Course ignore() {
		return this.ignoredCourse;
	}

	/**
	 * F�gt einen {@link Student Sch�ler} zur {@link #allStudents Liste der Sch�ler}
	 * hinzu.
	 * 
	 * @param s Der {@link Student Sch�ler} der hinzugef�gt werden soll.
	 */
	public void addStudent(Student s) {
		if (s == null)
			return;

		if (this.allStudents.contains(s))
			this.allStudents.remove(s);

		if (this.allStudents.add(s))
			LOGGER.fine("The Student with the name " + s.getPrename() + " " + s.getName() + " was added.");

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
	 * Zun�chst wird der File in eine List des Zwischenformates
	 * {@link WriteableContent} umgewandelt. Dazu wird {@link #importFile(path)}
	 * aufgerufen. Sollte ein Fehler auftreten so wird dieser mit dem
	 * {@link Level#SEVERE Log-Level SEVERE} und dem erkl�renden Text: "Unable to
	 * load data!", sowie der Fehlermeldung ausgegeben und das Einlesen der Datei
	 * wird abgebrochen.
	 * </p>
	 * 
	 * <p>
	 * Im folgenden wird dann jeder {@link WriteableContent} in den Distributor �ber
	 * die Methode {@linkplain #readGrid(Tabellen_�berschrift, Tabelle, path)}
	 * eingelesen. Die Tabelle wird hirzu �ber {@link WriteableContent#getGrid()
	 * getGrid()} und die Tabellen �berschrift durch die Methode
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

		LOGGER.info("All Courses: " + this.allCourses.toString());
		LOGGER.info("All Students: " + this.allStudents.toString());
	}

	/**
	 * L�sst die Datei je nach Dateityp auslesen und gibt die Tabellen in Form einer
	 * {@link List Liste} aus {@link de.juhu.filemanager.WriteableContent
	 * WriteableContents} zur�ck.
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
	 * @param gridName Der Name der Tabelle. Entscheidet dar�ber an welchen
	 *                 {@link de.juhu.distributor.Reader Reader} die Daten bei einer
	 *                 unspezifischen Angabe weitergegeben werden.
	 * @param grid     Die importierten Daten in einem {@link String[][]}
	 * @param filename Der Name der Datei die eingelesen wurde
	 */
	private void readGrid(String gridName, String[][] grid, String filename) {
		LOGGER.info("Start to load data from " + filename + ".");

		/*
		 * Z�hlt die Zeilen der Tabelle mit.
		 */
		int lineNumber = 0;

		this.updateStandartReaders();

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
	 * �berpr�ft, ob der eingegebene String auf einen
	 * {@link de.juhu.distributor.Reader Reader} aus der Liste
	 * {@link Distributor#readers reader} verweist.
	 * 
	 * @param input Der zu �berpr�fende Key.
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
	 * F�gt einen {@link Reader} zu den {@link #readers aktiven Readern} hinzu.
	 * 
	 * @param reader Der {@link Reader}, der hinzugef�gt werden soll.
	 * @return Ob der {@link Reader} hinzugef�gt werden konnte.
	 */
	public boolean addReader(Reader reader) {
		if (reader == null || this.isReaderKey(reader.key))
			return false;

		this.readers.add(reader);
		return true;
	}

	/**
	 * L�dt die Standart {@link Reader} in {@link #readers}.
	 */
	private void loadReaders() {
		this.readers.add(0, new Reader(Config.newStudent) {
			@Override
			public void read(String[] line, int lineNumber) {
				if (line.length < 2) {
					LOGGER.warning(
							"Not enough arguments for student. This line will be skiped: [Line: " + lineNumber + "]");
					lineNumber++;
					return;
				}

				for (Student s : Distributor.getInstance().allStudents) {
					if (s.getPrename().equalsIgnoreCase(line[1]) && s.getName().equalsIgnoreCase(line[0])) {

						if (!Config.rememberDecision) {

							GUIDoubleStudentManager.finished = false;

							Platform.runLater(() -> {
								GUIDoubleStudentManager.sName = s.getName();
								GUIDoubleStudentManager.sPrename = s.getPrename();

								Util.openWindow("/assets/layouts/DoubleStudent.fxml",
										References.language.getString("doubleStudent.text"),
										GUILoader.getPrimaryStage(), GUIManager.getInstance().theme);
							});

							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								LOGGER.log(Level.SEVERE, "Error during sleeping!", e);
							}

							while (!GUIDoubleStudentManager.finished) {
								try {
									Thread.sleep(100);
								} catch (InterruptedException e) {
									LOGGER.log(Level.SEVERE, "Error during sleeping!", e);
								}
							}
						}
						if (!Config.allowDoubleStudents)
							return;
					}
				}

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
					LOGGER.fine("Student with name " + line[1] + " " + line[0] + " was created.");
				}
			}
		});

		this.readers.add(1, new Reader(Config.newCourse) {
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
					countStudents = Double.valueOf(line[2]).intValue();
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

	/**
	 * Aktualisiert die Standartm��ig eingef�gten Leser.
	 */
	public void updateStandartReaders() {
		if (this.readers.size() < 2) {
			this.loadReaders();
			return;
		}

		this.readers.remove(1);
		this.readers.remove(0);

		this.loadReaders();
	}

	/**
	 * Sucht einen {@link Student Sch�ler} in der {@link #allStudents Liste aller
	 * Sch�ler} und {@link #ignoredStudents Liste aller f�r die Berechnung
	 * irrelevanten Sch�ler} nach �bereinstimmung mit der mitgegebenen ID heraus.
	 * 
	 * @param studentID Die ID, dessen Sch�ler gesucht werden soll.
	 * @return Der gefundene Sch�ler, {@code null}, wenn kein Sch�ler mit dieser ID
	 *         existiert.
	 */
	public Student getStudentByID(int studentID) {
		for (Student s : this.allStudents)
			if (s.idequals(studentID))
				return s;
		for (Student s : this.ignoredStudents)
			if (s.idequals(studentID))
				return s;
		return null;
	}

	/**
	 * F�gt einen {@link Course Kurs} in die {@link #allCourses Liste aller Kurse}
	 * ein. Falls der Kurs bereits existiert, wird dieser aus der Liste gel�scht und
	 * dann der neue Kurs eingef�gt.
	 * 
	 * @param c Der Kurs der eingef�gt werden soll.
	 */
	public void addCourse(Course c) {
		if (this.allCourses.contains(c))
			this.allCourses.remove(c);
		if (this.allCourses.add(c))
			LOGGER.fine("The " + c.getSubject() + "-Course teached by " + c.getTeacher() + " was added.");
	}

	/**
	 * Setzt den aktiven Kurs aller Sch�ler auf {@code null}.
	 */
	public void reset() {
		for (Student s : this.allStudents)
			s.setActiveCourse(null);
	}

	/**
	 * Leert alle Eingabedaten dieser Klasse. Die berechneten Saves bleiben
	 * erhalten.
	 */
	public void clear() {
		boolean clear = Config.clear;

		Config.clear = true;

		this.allStudents = new ArrayList<>();
		this.allCourses = new ArrayList<>();

		new Distributor();

		Config.clear = clear;
	}

	/**
	 * Entfernt einen Sch�ler aus der {@link Liste aller zu Berechnenden Sch�ler},
	 * oder aus der {@link #ignoredStudents Liste aller nicht zu berechnenden
	 * Sch�ler}.
	 * 
	 * @param student Der zu entfernende Sch�ler
	 */
	public void removeStudent(Student student) {
		if (student == null)
			return;
		if (this.allStudents.contains(student))
			this.allStudents.remove(this.allStudents.indexOf(student));
		if (this.ignoredStudents.contains(student))
			this.ignoredStudents.remove(this.ignoredStudents.indexOf(student));
	}

	/**
	 * Entfernt einen Kurs aus der {@link #allCourses Liste aller Kurse}.
	 * 
	 * @param course Der zu entfernende Kurs.
	 */
	public void removeCourse(Course course) {
		if (course == null)
			return;
		if (this.allCourses.contains(course))
			this.allCourses.remove(this.allCourses.indexOf(course));
	}

	/**
	 * �berschreibt die ignorier Marke im {@link #ignoredCourse Referenzkurs des
	 * nicht berechneten Sch�ler}.
	 * 
	 * @param ignoreStudent die neue ignorier Marke
	 */
	public void setIgnoreMark(String ignoreStudent) {
		this.ignoredCourse.setSubject(ignoreStudent);
	}

}