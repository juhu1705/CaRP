package de.juhu.distributor;

import de.juhu.guiFX.GUIDoubleStudentManager;
import de.juhu.guiFX.GUILoader;
import de.juhu.guiFX.GUIManager;
import de.juhu.guiFX.ProgressIndicator;
import de.juhu.util.Config;
import de.juhu.util.PriorityQueue;
import de.juhu.util.References;
import de.juhu.util.Util;
import de.noisruker.filemanager.WriteableContent;
import javafx.application.Platform;
import javafx.event.Event;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import static de.noisruker.filemanager.CSVImporter.readCSV;
import static de.noisruker.filemanager.ExcelImporter.readXLSImproved;
import static de.noisruker.filemanager.ExcelImporter.readXLSXImproved;
import static de.noisruker.logger.Logger.LOGGER;

/**
 * Diese Klasse Verwaltet und Berechnet die eingegebenen Daten und stellt die
 * besten Ergebnisse zum Auslesen bereit.
 *
 * @author Juhu1705
 * @version 2.0
 * @category Distribution
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
     * Zeigt an, ob fertigberechnete Ergebnisse ausgegeben werden können.
     */
    public static boolean calculate = false;

    // INFO: Berechnungsinformationen
    /**
     * Die Aktuelle Instanz dieser Klasse, auf die Zugegriffen werden kann.
     */
    private static Distributor instance = null;
    /**
     * Die nächste Freie ID, die an einen Schüler vergeben werden kann.
     */
    private static int nextID = 0;
    /**
     * Speichert die zu berechnenden Schüler.
     */
    ArrayList<Student> loadedallStudents;
    /**
     * Speichert die zu berechnenden Kurse.
     */
    ArrayList<Course> loadedallCourses;
    /**
     * Diese Liste hält alle für die Berechnung irrelevanten Schüler fest und stellt
     * sie zum nachträglichen Einfügen in das Ergebnis bereit.
     */
    ArrayList<Student> ignoredStudents = new ArrayList<>();
    /**
     * Dieser Kurs beinhaltet alle ignore Students. Er dient als Referenzkurs für
     * diese.
     */
    Course ignoredCourse = new Course(Config.ignoreStudent, "", -1);
    /**
     * Dient zur Berechnung der Schüler.
     *
     * @info Verändert sich während der Laufzeit.
     */
    ArrayList<Student> allStudents = new ArrayList<>();

    // INFO: Instance
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
     * die nicht mehr aufgerufen werden
     */
    @Deprecated
    ArrayList<Student> problems = new ArrayList<>();

    // INFO: Konstruktoren
    /**
     * Spiechert alle {@link Reader}, die beim Einlesen der Daten aktiv sind.
     */
    private ArrayList<Reader> readers = new ArrayList<>();

    /**
     * Erstellt eine Instanz dieser Klasse ohne weitere Eigenschaften. Wird nur
     * ausgeführt, wenn noch keine Instanz vorhanden ist und {@link #getInstance()}
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
     * Gespeicherten Schüler und Kurse der vorherigen in {@link #instance}
     * gespeicherten Instanz automatisch in die neue miteingerechnet.
     * <p>
     * Gleichzeitig werden die Schüler und Kursdaten aus dem mitgegebenen Pfad über
     * die Methode {@link #readFile(String)} eingelesen und in die Bestehenden Daten
     * eingespeißt.
     *
     * @param filename Der Pfad, aus dem die benötigten Schüler und Kurs Daten
     *                 herausgelesen werden.
     */
    public Distributor(String filename) {
        if (instance != null)
            this.readers = instance.readers;
        else
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
     * @param actual     Der {@link Save Speicher}, welcher geladen und dann in die
     *                   {@link Distributor#calculated Liste der Kalkulationen}
     *                   eingefügt wird.
     * @param readObject Der erste {@link Save Speicher}, der in die
     *                   {@link Distributor#calculated Liste der Kalkulationen}
     *                   eingefügt wird.
     * @implNote Alle vorherigen Daten werden gelöscht.
     */
    public Distributor(Save actual, Save... readObject) {
        if (instance != null)
            this.readers = instance.readers;
        else
            this.loadReaders();

        // Setzt die aktuelle Instanz auf diese.
        Distributor.instance = this;

        // Ließt die Saves in das System ein.
        this.loadDataFromSave(actual);
        this.loadedallStudents = this.allStudents;
        this.loadedallCourses = this.allCourses;

        // Fügt alle drei Speicherungen zu den Kalkulationen hinzu.
        Distributor.calculated.add(actual);
        Distributor.calculated.addAll(Arrays.asList(readObject));
    }

    /**
     * @return Die letzte Erstellte Instanz dieser Klasse, oder eine neue Instanz,
     * falls noch keine Vorhanden war.
     */
    public static Distributor getInstance() {
        if (instance == null)
            new Distributor();

        return instance;
    }

    /**
     * Gibt die {@link #nextID nächste freie ID} zurück und zählt diese um einen
     * Wert hoch.
     *
     * @return Die ID, die angefordert wurde
     */
    public static int getStudentID() {
        // References.LOGGER.info(nextID + "");
        return Distributor.nextID++;
    }

    public boolean isEmpty() {
        return this.allCourses.isEmpty() && this.allStudents.isEmpty() && this.ignoredStudents.isEmpty();
    }

    /**
     * Startet den Gewünschten Berechnungs- und Zuweisungsprozess. Überwacht, dass
     * nur eine Berechnung zur gleichen Zeit abläuft. Lädt, wenn keine Schülerdaten
     * vorhanden sind und ein Pfad angegeben ist, welcher auf eine existierende
     * Tabellen-Datei verweist diese Tabellen-Datei in das Programm und berechnet
     * für die neu geladenen Daten eine Zuweisung.
     * <p>
     * Macht zuletzt die fertige Berechnung im GUI sichtbar.
     */
    @Override
    public void run() {
        /*
         * Überprüft, ob bereits eine Instanz geöfnet ist und bricht, wenn bereits eine
         * Instanz geöffnet ist den Prozess ab.
         */
        if (calculate) {
            Platform.runLater(() -> {
                GUIManager.getInstance().startErrorFrame(References.language.getString("double_calculation_fail.title"),
                        References.language.getString("calculation.edit_data_fail.description"));
//				GUIManager.getInstance().r1.setDisable(false);
//				GUIManager.getInstance().r2.setDisable(false);
//				GUIManager.getInstance().r3.setDisable(false);
            });
            return;
        }

        /*
         * Lädt falls keine Daten geladen sind und eine Datei angegeben ist, diese in
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
                        GUIManager.getInstance().startErrorFrame(References.language.getString("no_data_fail.title"),
                                References.language.getString("no_data_fail.description"));
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
        LOGGER.config("Distributor Started with this specifies: ");
        LOGGER.config("--Basic Student limit: " + Config.normalStudentLimit);
        LOGGER.config("--The chooses of the Students: " + Config.maxChooses);
        LOGGER.config("--The number of students to Calculate: " + this.allStudents.size());

        calculate = true;

        calculated = new PriorityQueue<>(100);

        for (Student s : this.allStudents)
            s.setActiveCourse(null);

        this.assign();

        for (Save s : Distributor.calculated.list)
            s.getInformation().update();

        GUIManager.actual = Distributor.calculated.peek();

        /*
         * Lädt die Daten in die Ausgabe-Vorschau
         */

        Platform.runLater(() -> {
            GUIManager.getInstance().counter.setText(References.language.getString("distribution.text") + ": "
                    + (Distributor.calculated.indexOf(GUIManager.actual) + 1));

            LOGGER.config(GUIManager.getInstance().textActual + "");

            GUIManager.getInstance().textActual.setText(References.language.getString("calculationgoodness.text") + ": "
                    + Util.round(GUIManager.actual.getInformation().getGuete(), 3));

            GUIManager.getInstance().b1.setDisable(true);
            if (Distributor.calculated.size() > 1) {
                GUIManager.getInstance().b4.setDisable(false);
                GUIManager.getInstance().textNext.setText(References.language.getString("nextgoodness.text")
                        + Util.round(Distributor.calculated.get(2).getInformation().getGuete(), 3));
            } else
                GUIManager.getInstance().b4.setDisable(true);

            // GUIManager.getInstance().masterTabPane.getSelectionModel().select(GUIManager.getInstance().tabOutput);
        });

        Platform.runLater(GUIManager.getInstance().outputSView);
        Platform.runLater(GUIManager.getInstance().outputCView);
        Platform.runLater(GUIManager.getInstance().outputIView);

        Platform.runLater(() -> {
            if (!GUIManager.getInstance().tabOC)
                GUIManager.getInstance().tabOS = true;

            GUIManager.getInstance().onTabOutput(
                    new Event(GUIManager.getInstance().tabOutput, GUIManager.getInstance().tabOutput, null));
        });

        calculate = false;

        this.printRate();
    }

    /**
     * Lädt die Daten eines {@link Save Speichers} in den {@link Distributor}.
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

        for (int i = 0; i < this.allStudents.size(); ) {

            if ((this.allStudents.get(i).getActiveCourse() != null
                    && this.allStudents.get(i).getActiveCourse().equals(this.ignore()))
                    || this.allStudents.get(i).getCoursesAsList().contains(this.ignore()))
                this.ignoredStudents.add(this.allStudents.remove(i));
            else
                i++;
        }
    }

    /**
     * Gibt die Rate der Berechnung mit der Priorität info in der Konsole aus.
     */
    public void printRate() {
        for (int i = Config.maxChooses; i-- > 0; ) {
            LOGGER.info("Students that get their " + (i + 1) + ". choise: " + this.getStudentsWithRate(i));
        }
    }

    /**
     * Ermittelt die Anzahl der Schüler mit der entsprechenden Rate.
     *
     * @param rate Die Rate nach der gesucht wird.
     * @return Die Anzahl der Schüler mit der entsprechenden Rate.
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
     * <p>
     * Wei&szlig;t die im {@link Distributor} hinterlegten Daten bestm&ouml;glich zu. Dabei
     * wird versucht die {@link Student Sch&uuml;ler} aus der {@link #allStudents Liste
     * der Sch&uuml;ler} so auf die {@link Course Kurse} aus der {@link #allCourses Liste
     * der Kurse} zu verteilen, dass m&ouml;glichst viele Sch&uuml;ler einen m&ouml;glichst guten
     * Kurs aus ihren Kurswahlen zugewiesen bekommen.
     * </p>
     * <p>
     * Um die bestm&ouml;gliche Verteilung zu erreichen wird nach einem kontrollierten
     * Zufalls und Verbesserungsprinzip vorgegangen. Daher besteht jeder der unter
     * {@link Config#runs} angegebenen durchl&auml;ufe aus folgenden zwei Schritten:
     * <ul>
     * <li>Dem neu Zuweisen</li>
     * <li>und dem Verbessern.</li>
     * </ul>
     * </p>
     * <p>
     * Bevor die Berechnung starten kann, werden die Sch&uuml;ler und Kursdaten zun&auml;chst
     * einmal in die {@link #loadedallStudents} und {@link #loadedallCourses} Listen
     * gesichert, so dass diese Daten immer zur unverf&auml;lschten neuberechnung genutzt
     * werden k&ouml;nnen.
     * </p>
     * <p>
     * Um eine graphische &Uuml;bersicht zu bekommen wird die {@link ProgressIndicator
     * Prozess Leiste} mit den entsprechenden Werten initialisiert.
     * </p>
     * <p>
     * Nun werden die {@link Config#runs} vielen Versuche gestartet. Mit jedem
     * durchlaufendem Versuch wird die {@link ProgressIndicator Prozess Leiste} um
     * einen Wert erh&ouml;ht, sowie eine Nachricht mit der aktuellen Durchlaufsnummer
     * ausgegeben.
     * </p>
     * <p>
     * Dann werden versuche einer Neuzuweisung gestartet. Die Menge dieser neu
     * Zuweisungsversuche ist &uuml;ber {@link Config#newCalculating} einstellbar.
     * <p>
     * Zur Neuzuweisung werden die Daten aus den Sicherheitsspeichern des
     * {@link Distributor Berechners} geladen und dann wird die {@link #allStudents
     * Liste der Sch&uuml;ler} gemischt. F&uuml;r jeden Sch&uuml;ler wird dann &uuml;ber die Methode
     * {@link Student#next()} versucht ein Kurs zu finden. Kann kein Kurs gefunden
     * werden wird der Sch&uuml;ler &uuml;ber {@link Student#mark()} markiert. Nach dieser
     * Zuweisung werden die Daten &uuml;ber {@link #save()} gespeichert.
     * </p>
     * <p>
     * Nach den Versuchen der Neuzuweisung werden noch
     * {@link Config#improvingOfCalculation} viele Versuche unternommen eine
     * zuf&auml;llig ausgew&auml;hlte Kalkulation zu verbessern. Hierzu wird eine Zuf&auml;llige
     * gespeicherte Zuweisung aus der {@link #calculated Liste der Speicher} geladen
     * und dann eine Neuberechnung an diesem durchgef&uuml;hrt. Dazu wird allen nicht
     * zugewiesenden (also markierten) Sch&uuml;ler deren erster Kurs gew&auml;hrt und im
     * Anschluss allen Sch&uuml;lern mit der schlechtesten Priorit&auml;t das Gleiche gew&auml;hrt.
     * Nach dem Mischen der Kurse wird nun &uuml;berpr&uuml;ft, welche Kurse zu voll sind.
     * Sollte ein Kurs zu viele Sch&uuml;ler beinhalten werden, wenn
     * Config#newImproving aktiviert ist, Sch&uuml;ler aus diesem Kurs zuf&auml;llig
     * in deren n&auml;chsten Gew&uuml;nschten Kurs verschoben. Ist
     * Config#newImproving deaktiviert, so werden aus dem Kurs zuf&auml;llig
     * Sch&uuml;ler in deren n&auml;chsten Freien Kurs verschoben.
     * </p>
     * <p>
     * Nach Beendigung der {@link Config#runs} vielen durchl&auml;ufe wird noch die
     * {@link ProgressIndicator Prozess Leiste} zur&uuml;ckgesetzt.
     * </p>
     *
     * @implNote Diese Methode wird in Verkn&uuml;pfung mit der graphischen Oberfl&auml;che
     * und weiteren kleinen Zus&auml;tzlichen Eigenschaften &uuml;ber {@link #run()}
     * ausgef&uuml;hrt.
     * @since BETA-0.1.0
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
     * Geht die {@link #allCourses Liste aller Kurse} durch und überprüft, ob die
     * Methode {@link Course#isFull()} {@code true} zurückgibt. Sollte dies der Fall
     * sein, so wird {@code true} zurückgegeben, ansonsten wird {@code false}
     * zurückgegeben.
     *
     * @return Ob <b><u>ein</u></b> Kurs aus der {@link #allCourses Liste aller
     * Kurse} überfüllt ist.
     */
    private boolean isAnyCourseFull() {
        for (Course c : this.allCourses)
            if (c.isFull())
                return true;
        return false;
    }

    /**
     * Erzeugt zwei von den alten {@link Student Schüler} und {@link Course Kurs}
     * Listen unabhängige Listen, die in selber Weise verknüpft sind. Dabei ist das
     * erste Element im Array, die kopierte Liste der Schüler und das zweite die
     * kopierte Liste der Lehrer.
     *
     * @param oldStudents    Die Liste der Schüler die Kopiert werden soll.
     * @param oldCourses     Die verknüpfte Liste der Kurse die Kopiert werden soll.
     * @param ignoredCourse2 Der Kurs für nicht zugeordnete Schüler.
     * @return Die Kopierten Daten.
     * <ul>
     * <li>Element 1: Liste der Schüler (kopiert)</li>
     * <li>Element 2: Liste der Kurse (kopiert)</li>
     * </ul>
     */
    public ArrayList[] copyData(ArrayList<Student> oldStudents, ArrayList<Course> oldCourses, Course ignoredCourse2) {

        // Erzeugung der neuen Arrays

        /*
         * Die neue Schüler-Liste, in die die Kurse Kopiert werden.
         */
        ArrayList<Student> newStudents = new ArrayList<Student>();

        /*
         * Die neue Kurs-Liste, in die die Kurse Kopiert werden.
         */
        ArrayList<Course> newCourses = new ArrayList<Course>();

        /*
         * Kopieren der Schüler ohne Verknüpfungen.
         */
        for (Student s : oldStudents)
            try {
                newStudents.add((Student) s.clone());
            } catch (CloneNotSupportedException e) {
                LOGGER.log(Level.SEVERE, "Error while copy Student Data", e);
            }

        /*
         * Kopieren der Kurse ohne Verknüpfungen
         */
        for (Course c : oldCourses)
            try {
                newCourses.add((Course) c.clone());
            } catch (CloneNotSupportedException e) {
                LOGGER.log(Level.SEVERE, "Error while copy Course Data", e);
            }

        /*
         * Erstellen der benötigten Verknüpfungen
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
         * Rückgabe der neu erstellten Listen
         */
        return new ArrayList[]{newStudents, newCourses};
    }

    /**
     * Speichert die aktuellen Daten im Distributor in einen {@link Save neuen
     * Speicher} und fügt diesen in {@link #calculated Liste der Speicher} ein.
     * Dabei werden die Daten über
     * {@link Distributor#copyData(ArrayList, ArrayList, Course)} kopiert und die
     * Kopierten Daten in den Speicher geladen, so dass die im Speicher enthaltenen
     * Daten nicht mehr mit den Daten im {@link Distributor} verknüpft sind.
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
     * Ermittelt alle Schüler aus der {@link #allStudents Liste der zu berechnenden
     * Schüler}, deren {@link Student#priority Priorität} mit der mitgegebenen
     * Priorität übereinstimmt.
     *
     * @param priority Die Priorität, nach der gesucht wird.
     * @return Eine Liste aller Schüler, deren {@link Student#priority Priorität}
     * mit der gegebenen Priorität übereinstimmt.
     */
    private ArrayList<Student> getStudentsWithPriority(int priority) {
        ArrayList<Student> pStudents = new ArrayList<>();

        for (Student s : this.allStudents)
            if (s.getPriority() == priority)
                pStudents.add(s);

        return pStudents;
    }

    /**
     * Ermittelt alle Schüler aus der {@link #allStudents Liste der zu berechnenden
     * Schüler}, die keinem Kurs zugeordnet sind.
     *
     * @return Eine Liste aller nicht zugewiesenden Schüler
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
     * Ermittelt die Anzahl der Schüler jeder Priorität. Dabei ist der letzte Index
     * der Liste gefüllt mit der Anzahl der Schüler, die nicht zugewiesen werden
     * konnten.
     *
     * @return Eine Liste mit der (Priorität - 1) und der Zugehörigen Anzahl der
     * Schüler.
     */
    private int[] getPriorities() {
        int[] priorities = new int[this.getHighestPriorityWhithoutIntegerMax() + 1];

        for (int i = 0; i < priorities.length - 1; i++)
            priorities[i] = this.countPriority(i + 1);

        priorities[priorities.length - 1] = this.countPriority(Integer.MAX_VALUE);
        return priorities;
    }

    /**
     * Gibt die Anzahl der Schüler aus der {@link #allStudents Liste aller Schüler}
     * zurück, deren {@link Student#priority Priorität} mit der gegebenen Priorität
     * übereinstimmt.
     *
     * @param priority Die Priorität, nach der gesucht wird.
     * @return Die Anzahl der Schüler mit der mitgegebenden Priorität.
     */
    private int countPriority(int priority) {
        int count = 0;
        for (Student s : this.allStudents)
            if (s.getPriority() == priority)
                count++;
        return count;
    }

    /**
     * Ermittelt die höchste Priorität, wobei alle nicht Zugewiesenden Schüler
     * unbeachtet bleiben.
     *
     * @return Die höchste Priorität, ohne nicht zugewiesende Schüler.
     */
    public int getHighestPriorityWhithoutIntegerMax() {
        int highest = 0;
        for (Student s : this.allStudents)
            if (s.getPriority() != Integer.MAX_VALUE)
                highest = highest >= s.getPriority() ? highest : s.getPriority();
        return highest;
    }

    /**
     * Ermittelt die höchste Priorität
     *
     * @return Die höchste Priorität
     * @implNote {@link Integer#MAX_VALUE}, wenn Schüler nicht zugewiesen werden
     * konnten.
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
     * Überprüft, ob ein Kurs zu voll ist.
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
     * Gibt alle Kurse mit ihren Schüler im {@link de.noisruker.logger.Logger#LOGGER Log} aus.
     */
    private void print() {
        for (Course c : this.allCourses) {
            LOGGER.info(c.toString());
            LOGGER.info(c.studentsToString());
        }
    }

    /**
     * Überprüft, ob ein Kurs mit dem gleichen Namen ({@link Course#getSubject()
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
     * @return Die {@link #allStudents Liste der Schüler}.
     */
    public ArrayList getCalcStudents() {
        return this.allStudents;
    }

    // INFO: Kurs

    /**
     * @return Die {@link #ignoredStudents Liste der für die Berechnung irrelevanten
     * Schüler}
     */
    public ArrayList getIgnoreStudents() {
        return this.ignoredStudents;
    }

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

        if (Util.isIgnoreCourse(name.split("\\|")))
            return this.ignoredCourse;

        Course c = this.getCourseByName(name);
        if (c == null)
            this.addCourse(c = new Course(name.split("\\|")));
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

    /**
     * Fügt einen {@link Student Schüler} zur {@link #allStudents Liste der Schüler}
     * hinzu.
     *
     * @param s Der {@link Student Schüler} der hinzugefügt werden soll.
     */
    public void addStudent(Student s) {
        if (s == null)
            return;

        this.allStudents.remove(s);

        if (this.allStudents.add(s))
            LOGGER.fine("The Student with the name " + s.getPrename() + " " + s.getName() + " was added.");

    }

    // INFO: Import

    /**
     * @param student    The student to Add to the course
     * @param courseName The c
     * @deprecated Directly add the Student to the course instead of use this Method
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

    /**
     * <p>
     * Verwaltet das Einlesen der Dateien in den {@link Distributor}. Es gibt den
     * Status des Einlesens im LOG aus.
     * </p>
     *
     * <p>
     * Zunächst wird der File in eine List des Zwischenformates
     * {@link WriteableContent} umgewandelt. Dazu wird #importFile(path)
     * aufgerufen. Sollte ein Fehler auftreten so wird dieser mit dem
     * {@link Level#SEVERE Log-Level SEVERE} und dem erklärenden Text: "Unable to
     * load data!", sowie der Fehlermeldung ausgegeben und das Einlesen der Datei
     * wird abgebrochen.
     * </p>
     *
     * <p>
     * Im folgenden wird dann jeder {@link WriteableContent} in den Distributor über
     * die Methode #readGrid(Tabellen_Überschrift, Tabelle, path)
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

        LOGGER.info("All Courses: " + this.allCourses.toString());
        LOGGER.info("All Students: " + this.allStudents.toString());
    }

    /**
     * Lässt die Datei je nach Dateityp auslesen und gibt die Tabellen in Form einer
     * {@link List Liste} aus {@link WriteableContent
     * WriteableContents} zurück.
     *
     * @param path Der Dateipfad der Ausgelesen werden soll.
     * @return Eine {@link List Liste} aus {@link WriteableContent
     * WriteableContents}.
     * @throws IOException        Wenn die Datei nicht verarbeitet werden kann.
     * @throws URISyntaxException Wenn der filename nicht existiert.
     */
    private List<WriteableContent> importFile(String path) throws IOException, URISyntaxException {
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

    // INFO: IMPORT READER

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
        this.readers.add(0, new Reader(Config.newStudent) {
            @Override
            public void read(String[] line, int lineNumber) {
                if (line == null || line.length < 2 || Util.isBlank(line[0]) || Util.isBlank(line[1])) {
                    LOGGER.warning(
                            "Not enough arguments for student. This line will be skiped: [Line: " + lineNumber + "]");
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
                                        GUILoader.getPrimaryStage());
                            });

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
                        if (line[i] == null || line[i].equals("") || line[i + 1] == null)
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
                if (line.length <= 2 || Util.isBlank(line[0]) || Util.isBlank(line[1]) || Util.isBlank(line[2])) {
                    LOGGER.info("Not enough arguments for a new Course. This line will be skiped: [Line: " + lineNumber
                            + "]");
                    return;
                }
                int countStudents;
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

                LOGGER.fine("The course " + c + " was added to the courses with the student limit "
                        + countStudents + ".");
            }
        });
    }

    /**
     * Aktualisiert die Standartmäßig eingefügten Leser.
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
     * Sucht einen {@link Student Schüler} in der {@link #allStudents Liste aller
     * Schüler} und {@link #ignoredStudents Liste aller für die Berechnung
     * irrelevanten Schüler} nach übereinstimmung mit der mitgegebenen ID heraus.
     *
     * @param studentID Die ID, dessen Schüler gesucht werden soll.
     * @return Der gefundene Schüler, {@code null}, wenn kein Schüler mit dieser ID
     * existiert.
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
     * Fügt einen {@link Course Kurs} in die {@link #allCourses Liste aller Kurse}
     * ein. Falls der Kurs bereits existiert, wird dieser aus der Liste gelöscht und
     * dann der neue Kurs eingefügt.
     *
     * @param c Der Kurs der eingefügt werden soll.
     */
    public void addCourse(Course c) {
        this.allCourses.remove(c);
        if (this.allCourses.add(c))
            LOGGER.fine("The " + c.getSubject() + "-Course teached by " + c.getTeacher() + " was added.");
    }

    /**
     * Setzt den aktiven Kurs aller Schüler auf {@code null}.
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
     * Entfernt einen Schüler aus der {@link #allStudents Liste aller zu Berechnenden Schüler},
     * oder aus der {@link #ignoredStudents Liste aller nicht zu berechnenden
     * Schüler}.
     *
     * @param student Der zu entfernende Schüler
     */
    public void removeStudent(Student student) {
        if (student == null)
            return;
        this.allStudents.remove(student);
        this.ignoredStudents.remove(student);
    }

    /**
     * Entfernt einen Kurs aus der {@link #allCourses Liste aller Kurse}.
     *
     * @param course Der zu entfernende Kurs.
     */
    public void removeCourse(Course course) {
        if (course == null)
            return;
        this.allCourses.remove(course);
    }

    /**
     * Überschreibt die ignorier Marke im {@link #ignoredCourse Referenzkurs des
     * nicht berechneten Schüler}.
     *
     * @param ignoreStudent die neue ignorier Marke
     */
    public void setIgnoreMark(String ignoreStudent) {
        this.ignoredCourse.setSubject(ignoreStudent);
    }

}