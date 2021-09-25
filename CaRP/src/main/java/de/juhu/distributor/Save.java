package de.juhu.distributor;

import de.juhu.util.Config;
import de.juhu.util.MergeSort;
import de.juhu.util.References;
import de.juhu.util.Util;
import de.noisruker.filemanager.Vec2i;
import de.noisruker.filemanager.WriteableContent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;

import static de.noisruker.logger.Logger.LOGGER;

/**
 * Diese Klasse dient zur Speicherung berechneter Zuweisungen des
 * {@link Distributor Berechners} im Arbeitsspeicher und stellt zudem noch
 * Methoden zur Bearbeitung der gespeicherten Daten, sowie zum Exportieren der
 * Daten bereit.
 *
 * @author Juhu1705
 * @version 1.0
 * @category Distribution
 * @implements {@link Comparable}, {@link Serializable}
 * @since 0.0.2
 */
public class Save implements Comparable<Save>, Serializable {

    /**
     * Der {@link InformationSave Informationsspeicher} dieser Klasse, hier werden
     * interressante Zusatzinformationen hinterlegt.
     */
    private final InformationSave informations;

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
     */
    public Save(List<Student> editedStudents, List<Student> ignoredStudents, List<Course> allCourses) {

        this.allCourses = Save.sortCourse((allCourses));

        this.allStudents = new ArrayList<>(editedStudents);
        this.allStudents.addAll(ignoredStudents);

        this.allStudents = Save.sortStudents((this.allStudents));

        (this.informations = new InformationSave(this)).update();
    }

    /**
     * Sortiert die mitgegebende Liste der Kurse nach ihren Namen über die
     * {@link Course#compareTo(Course)} Methode des Kurses.
     *
     * @param courseToSort Die Liste der Kurse, die Sortiert werden soll.
     * @return Die sortierte Liste der Kurse.
     */
    private static List<Course> sortCourse(List<Course> courseToSort) {
        ExecutorService pool = Executors.newFixedThreadPool(courseToSort.size() / 2 + 10);
        Future<ArrayList<Course>> sortedStudents = pool.submit(new MergeSort<Course>((ArrayList<Course>) courseToSort, pool));
        try {
            ArrayList<Course> compute = new ArrayList<Course>(sortedStudents.get());
            pool.shutdownNow();
            return compute;
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.log(Level.SEVERE, "No data saved", e);
        }
        pool.shutdownNow();
        return null;
    }

    /**
     * Sortiert die mitgegebende Liste der Schüler nach ihren Namen über die
     * {@link Student#compareTo(Student)} Methode des Schülers.
     *
     * @param studentsToSort Die Liste der Schüler, die sortiert werden soll.
     * @return Die sortierte Liste der Schüler.
     */
    public static List<Student> sortStudents(List<Student> studentsToSort) {
        ExecutorService pool = Executors.newFixedThreadPool(studentsToSort.size() / 2 + 10);
        Future<ArrayList<Student>> sortedStudents = pool.submit(new MergeSort<Student>((ArrayList<Student>) (studentsToSort), pool));
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

    /**
     * @return Die höchste Priorität der in diesem Speicher gespeicherten
     * Berechnung.
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
     * Speicher gespeicherten Berechnung.
     */
    public int[] getStudentPriorities() {
        int[] studentPriorities = new int[this.getHighestPriorityWhithoutIntegerMax() + 1];
        int i1 = 0;
        for (Student s : this.getStudentsWithPriority(1))
            if (s.getActiveCourse() != null && !s.getActiveCourse().equals(Distributor.getInstance().ignoredCourse))
                i1++;

        studentPriorities[0] = i1;

        for (int i = 1; i < studentPriorities.length - 1; i++) {
            studentPriorities[i] = this.getStudentsWithPriority(i + 1).size();
        }
        studentPriorities[studentPriorities.length - 1] = this.getInformation().getUStudents().size();
        return studentPriorities;
    }

    /**
     * Bewertet die hier gespeicherte Berechnung indem die Summe aller Schüler
     * {@link Student#getRate(int) Raten} zurückgegeben wird. Sollte diese aus dem
     * zugelassenden Bereich herausfallen, wird {@link Integer#MAX_VALUE}
     * zurückgegeben.
     *
     * @param highestPriority Die höchste Priorität dieser Berechnung - Über
     *                        {@link #getHighestPriorityWhithoutIntegerMax()} zu
     *                        erhalten.
     * @return Die Rate dieser Berechnung.
     */
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

    /**
     * Gibt den Kurs aus der {@link #allCourses Liste aller Kurse} zurück, der
     * gleich benannt ist. Dazu wird {@link Course#toString()} mit dem mitgegebenen
     * Namen verglichen.
     *
     * @param name Der Name des Kurses, der gesucht wird.
     * @return Der Kurs mit dem entsprechenden Namen oder {@code null}, wenn der
     * Kurs nicht gefunden werden konnte.
     */
    public Course getCourseByName(String name) {
        for (Course c : this.allCourses) {
            if (name.equalsIgnoreCase(c.toString()))
                return c;
        }
        return null;
    }

    /**
     * @return Die in diesem Speicher vorhandende höchste Schülerpriorität zurück,
     * wobei nicht zugewiesenden Schüler nicht beachtet werden.
     */
    public int getHighestPriorityWhithoutIntegerMax() {
        int highest = 0;
        for (Student s : this.allStudents)
            if (s.getPriority() != Integer.MAX_VALUE)
                highest = highest >= s.getPriority() ? highest : s.getPriority();
        return highest;
    }

    /**
     * Sucht alle Schüler mit der gegebenden {@link Student#priority Priorität}
     * heraus und gibt diese als Liste zurück.
     *
     * @param priority Die Priorität nach der gesucht wird.
     * @return Eine Liste aller Schüler mit dieser Priorität.
     */
    public ArrayList<Student> getStudentsWithPriority(int priority) {
        ArrayList<Student> pStudents = new ArrayList<>();

        for (Student s : this.allStudents)
            if (s.getPriority() == priority)
                pStudents.add(s);

        return pStudents;
    }

    /**
     * @return Die {@link #allStudents Liste aller Schüler} dieses Speichers zurück.
     */
    public List<Student> getAllStudents() {
        return this.allStudents;
    }

    /**
     * @return Die {@link #allCourses Liste aller Kurse} dieses Speichers zurück.
     */
    public List<Course> getAllCourses() {
        return this.allCourses;
    }

    /**
     * @return Gibt die {@link #allCourses Liste aller Kurse} als Array zurück.
     */
    public Course[] getAllCoursesAsArray() {
        Course[] courses = new Course[this.allCourses.size()];
        int i = 0;
        for (Course c : this.allCourses)
            courses[i++] = c;

        return courses;
    }

    /**
     * @return Der mit diesem Speicher verknüpfte {@link #informations
     * Informationsspeicher}.
     */
    public InformationSave getInformation() {
        return this.informations;
    }

    /**
     * Erstellt {@link WriteableContent Tabellen}, indenen die unterschiedlichen
     * Informationen geschrieben werden. Unter dem Element 1 sind die Informationen
     * von der Schülerseite aus, in dem zweiten Listenelement befinden sich die
     * Informationen von der Kursseite aus und an letzter Stelle findet man die
     * weiteren Informationen zu dieser gespeicherten Berechnung.
     *
     * @return Liste der Tabellen
     */
    public List<WriteableContent> writeInformation() {
        List<WriteableContent> export = new ArrayList<WriteableContent>(3);

        // Write students
        LOGGER.info("Write Students");
        WriteableContent students = this.writeStudentInformation();
        export.add(students);

        LOGGER.info("Write Courses");
        WriteableContent courses = this.writeCourseInformation();
        export.add(courses);

        LOGGER.info("Write Information");
        WriteableContent information = this.informations.write();
        export.add(information);

        return export;
    }

    /**
     * Schreibt alle Informationen von Schülerseite aus in eine
     * {@link WriteableContent Tabelle}.
     *
     * @return Eine Tabelle mit den schülerseitigen Informationen dieses Speichers.
     */
    public WriteableContent writeStudentInformation() {
        WriteableContent students = new WriteableContent(References.language.getString("coursedistribution.text"));

        int lineCounter = 0;
        if (Config.hasHeaderOutput) {
            students.addLine(new Vec2i(0, lineCounter), Config.studentHeader);
            lineCounter += 2;
        }

        students.setStartTable(lineCounter);

        students.addLine(new Vec2i(0, lineCounter), "Name", "Vorname", "Kurs", "Lehrer", "Priorität");
        lineCounter += 2;
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

            students.addLine(new Vec2i(0, i + lineCounter), line);
        }

        return students;
    }

    /**
     * Schreibt alle Informationen von Kursseite aus in eine {@link WriteableContent
     * Tabelle}.
     *
     * @return Eine Tabelle mit den kursseitigen Informationen dieses Speichers.
     */
    public WriteableContent writeCourseInformation() {
        WriteableContent courses = new WriteableContent(References.language.getString("studentdistribution.text"));

        int line = 0;
        if (Config.hasHeaderOutput) {
            courses.addLine(new Vec2i(0, line), Config.courseHeader);
            line += 2;
        }

        courses.setStartTable(line);

        courses.addLine(new Vec2i(0, line), "Kurs", "Lehrer", "Anzahl Schüler", "Schüler");

        line += 2;

        for (Course c : this.allCourses) {
            String[] parameter = new String[c.size() + 3];

            parameter[0] = c.getSubject();
            parameter[1] = c.getTeacher();
            int lineAdd = 3;
            int anzahl = 0;
            for (int i = 0; i < c.size(); i++, anzahl++) {
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

    /**
     * Vergleicht diesen Speicher mit einem anderen. Dazu wird zunächst die
     * {@link InformationSave#getGuete() Guete} der beiden Speicher miteinander
     * verglichen. Wird 0 zurückgegeben, beinhalten die beiden Speicher die selben
     * Informationen.
     */
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

    /**
     * Überprüft, ob es sich bei diesem Speicher und dem gegebenen Speicher, um
     * Speicher mit den selben Informationen handelt.
     *
     * @param s Der Speicher, der mit diesem verglichen werden soll.
     * @return Ob die Speicher Identische Werte aufweisen.
     */
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

    /**
     * Verqleicht die gegebene Guete mit der {@link InformationSave#getGuete()
     * Guete} dieses Speichers. Es wird auf gleiche Weise verglichen wie in der
     * Methode {@link #compareTo(Save)} die Guete verglichen wurde.
     *
     * @implNote Es ist besser die Methode {@link #compareTo(Save)} anstelle von
     * dieser hier zu verwenden.
     */
    public int compareTo(int guete) {
        return this.informations.getGuete() - guete >= 0 ? -1 : 1;
    }

    /**
     * Fügt einen Kurs zu diesem Speicher hinzu. Existiert dieser Kurs bereits, wird
     * der existierende Kurs gelöscht und der mitgegebende anschließend eingefügt.
     *
     * @param c Der Kurs der hinzugefügt werden soll.
     */
    public void addCourse(Course c) {
        this.allCourses.remove(c);
        this.allCourses.add(c);
    }

    /**
     * Ermittelt den Schüler mit der mitgegebenen ID
     *
     * @param studentID Die ID nach der gesucht werden soll.
     * @return Der Schüler mit dieser ID oder {@code null}, wenn kein Schüler mit
     * dieser ID gefunden werden konnte.
     */
    public Student getStudentByID(int studentID) {
        for (Student s : this.allStudents)
            if (s.idequals(studentID))
                return s;
        return null;
    }

    /**
     * Fügt einen Schüler zu diesem Speicher hinzu. Existiert dieser Schüler
     * bereits, wird der vorgang abgebrochen und {@code false} zurückgegeben.
     *
     * @param student Der Schüler der hinzugefügt werden soll.
     * @return Ob der Schüler erfolgreich hinzugefügt werden konnte.
     */
    public boolean addStudent(Student student) {
        if (student == null || this.allStudents.contains(student))
            return false;

        return this.allStudents.add(student);
    }

    /**
     * Löscht einen Schüler aus der {@link #allStudents Liste aller Schüler}.
     *
     * @param student Der Schüler der gelöscht werden soll.
     */
    public void removeStudent(Student student) {
        if (student == null)
            return;
        if (this.allStudents.contains(student)) {
            student.getActiveCourse().removeStudent(student);
            this.allStudents.remove(student);
        }
    }

    /**
     * Löscht einen Kurs aus der {@link #allCourses Liste aller Kurse}.
     *
     * @param course Der Kurs der gelöscht werden soll.
     */
    public void removeCourse(Course course) {
        if (course == null)
            return;
        if (this.allCourses.contains(course)) {
            for (Student s : course.getStudents()) {
                s.setActiveCourse(null);
                s.refreshPriority();
                s.mark();
            }
            this.allCourses.remove(course);
        }
    }

    public void sortAll() {
        this.allStudents = Save.sortStudents((this.allStudents));
        this.allCourses = Save.sortCourse((this.allCourses));
    }

}
