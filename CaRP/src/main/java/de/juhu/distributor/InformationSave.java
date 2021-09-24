package de.juhu.distributor;

import java.io.Serializable;
import java.util.ArrayList;

import de.juhu.util.Config;
import de.juhu.util.References;
import de.noisruker.filemanager.Vec2i;
import de.noisruker.filemanager.WriteableContent;

/**
 * Diese Klasse hällt alle weiteren für eine Verteilung interressanten von dem
 * ihr zugeordneten {@link #parent Speicher} zum abrufen bereit.
 *
 * @version 1.0
 * @category Distribution
 * @author Juhu1705
 * @implements {@link Serializable}
 * @since 0.0.2
 *
 */
public class InformationSave implements Serializable {

	/**
	 * Der diesem Informationsspeicher zugeordnete {@link Save Speicher}.
	 */
	Save parent;

	/**
	 * Die höchste Priorität, die im {@link #parent zugeordneten Speicher} vergeben
	 * wurde.
	 */
	private int highestPriority;

	/**
	 * Die Rate der Berechnung des {@link #parent zugeordneten Speichers}.
	 */
	private int rate;

	/**
	 * Die Guete der Berechnung des {@link #parent zugeordneten Speichers}.
	 */
	private double guete;

	/**
	 * Der Erwartungswert des {@link #parent zugeordneten Speichers}.
	 */
	private double expectation;

	/**
	 * Die Varianz der Ergebnisse des {@link #parent zugeordneten Speichers}.
	 */
	private double variance;

	/**
	 * Die Standartabweichung vom Erwartungswert.
	 */
	private double standardDeviation;

	/**
	 * Die Anzahl der zugewiesenden Schüler, die im {@link #parent zugeordneten
	 * Speicher} gespeichert sind. Alle Schüler, die nicht verteilt werden mussten
	 * werden hier nicht mit aufgeführt.
	 */
	private int studentCount;

	/**
	 * Alle Schüler aus dem {@link #parent zugeordneten Speicher}, die nicht
	 * zugewiesen werden konnten.
	 */
	private final ArrayList<Student> unallocatedStudents = new ArrayList<Student>();

	/**
	 * Alle Schüler aus dem {@link #parent zugeordneten Speicher}, die die
	 * schlechteste Priorität besitzen, aber zugeordnet werden konnten.
	 */
	private ArrayList<Student> badPriorityStudents = new ArrayList<Student>();

	/**
	 * <p>
	 * Die Anzahl der Schüler aus dem {@link #parent zugeordneten Speicher} mit den
	 * entsprechenden Prioritäten.
	 * </p>
	 * <ul>
	 * <li>Prio 1: studentPriorities[0]</li>
	 * <li>Prio 2: studentPriorities[1]</li>
	 * <li>Prio n: studentPriorities[n - 1]</li>
	 * <li>Anzahl der nicht zugewiesenden Schüler:
	 * studentPriorities[studentPriorities.lenght - 1]</li>
	 * </ul>
	 * 
	 */
	private int[] studentPriorities;

	/**
	 * Erzeugt einen Informationsspeicher, der an den mitgegebenen Speicher gebunden
	 * ist. Nach dem erstellen dieses Speichers, kann die {@link #update()} funktion
	 * genutzt werden, um alle anderen gespeicherten Werte den Werten des Speichers
	 * anzugleichen.
	 * 
	 * @param parent Der diesem Informationsspeicher zugeordnete {@link Save
	 *               Speicher}.
	 */
	InformationSave(Save parent) {
		this.parent = parent;
	}

	/**
	 * Schreibt die im Informationsspeicher gespeicherten Daten in eine
	 * {@link de.noisruker.filemanager.WriteableContent exportierbare Tabelle} und gibt diese zurück.
	 * 
	 * @return Eine exportierbare Tabelle mit den im Informationsspeicher
	 *         gespeicherten Daten.
	 */
	public WriteableContent write() {
		this.update();

		WriteableContent information = new WriteableContent(References.language.getString("statistics.text"));

		information.setStartTable(0);

		int line = 0;

		if (Config.hasHeaderOutput) {
			information.addLine(new Vec2i(0, line),
					References.language.getString("informations.text"));
			line += 2;
		}

		information.setStartTable(line);

		information.addLine(new Vec2i(0, line++), References.language.getString("informations.text"),
				References.language.getString("value.text"));

		information.addLine(new Vec2i(0, ++line), References.language.getString("calculationgoodness.text") + ": ", Double.toString(this.guete));
		information.addLine(new Vec2i(0, ++line),
				References.language.getString("highestpriority.text") + ": ",
				Integer.toString(this.parent.getHighestPriorityWhithoutIntegerMax()));
//		information.addLine(new Vec2i(0, ++line), new String[] {
//				References.language.getString("expectation.text") + ": ", Double.toString(this.getExpectation()) });
//		information.addLine(new Vec2i(0, ++line),
//				new String[] { References.language.getString("standartDeviation.text") + ": ",
//						Double.toString(this.getStandartDeviation()) });
		information.addLine(new Vec2i(0, ++line),
				References.language.getString("studentcount.text") + ": ",
				Integer.toString(this.parent.getAllStudents().size()));
		information.addLine(new Vec2i(0, ++line),
				References.language.getString("coursecount.text") + ": ",
				Integer.toString(this.parent.getAllCourses().size()));
		information.addLine(new Vec2i(0, ++line),
				References.language.getString("calculatedstudentcount.text") + ": ",
				Integer.toString(this.getStudentCount()));

		line -= -2;

		information.addLine(new Vec2i(0, line++), References.language.getString("priorities.text"),
				References.language.getString("countofstudents.text"));
		for (int i = 0; i < studentPriorities.length - 1; i++) {
			information.addLine(new Vec2i(0, line++),
					Integer.toString(i + 1), Integer.toString(this.studentPriorities[i]),
					Double.toString((double) this.studentPriorities[i] / (double) this.getStudentCount()));
		}

		line += 1;

		if (this.studentPriorities[this.studentPriorities.length - 1] != 0) {
			information.addLine(new Vec2i(0, line++),
					References.language.getString("unallocatedstudents.text"),
					Integer.toString(this.studentPriorities[this.studentPriorities.length - 1]));

			information.addLine(new Vec2i(0, line++), References.language.getString("student.text"),
					References.language.getString("firstchoise.text"));

			for (Student s : this.unallocatedStudents)
				if (s.getCourses().length > 1)
					information.addLine(new Vec2i(0, line++),
							s.toString(), s.getCourses()[0].toString());

		} else {
			information.addLine(new Vec2i(0, line++),
					References.language.getString("nounallocated.text"));
		}

		line = line % 2 == 0 ? line + (line - 1) % 2 : line + line % 2;

		information.addLine(new Vec2i(0, line++),
				References.language.getString("studentswithbadpriority"));
		information.addLine(new Vec2i(0, line++), References.language.getString("student.text"),
				References.language.getString("firstchoise.text"));

		for (Student s : this.badPriorityStudents)
			if (s.getCourses().length > 1)
				information.addLine(new Vec2i(0, line++), s.toString(), s.getCourses()[0].toString());

		return information;
	}

	/**
	 * @return {@link #highestPriority Die höchste Priorität} der gespeicherten
	 *         Berechnung.
	 */
	public int getHighestPriority() {
		return this.highestPriority;
	}

	/**
	 * @return {@link #rate Die Rate} der gespeicherten Berechnung
	 */
	public int getRate() {
		return this.rate;
	}

	/**
	 * @return Die Anzahl der Schüler mit den zugeordneten Prioritäten.
	 * @see Save#getStudentPriorities()
	 * @see InformationSave#studentPriorities
	 */
	public int[] getStudentPriorities() {
		return this.studentPriorities;
	}

	/**
	 * @return Gibt alle Schüler mit der schlechtesten Priorität der gespeicherten
	 *         Berechnung zurück.
	 */
	public ArrayList<Student> getBStudents() {
		return this.badPriorityStudents;
	}

	/**
	 * @return Gibt alle Schüler zurück, die nicht zugewiesen werden konnten.
	 */
	public ArrayList<Student> getUStudents() {
		return this.unallocatedStudents;
	}

	/**
	 * @return Gibt die Anzahl der Berechneten Schüler zurück.
	 */
	public int getStudentCount() {
		return this.studentCount;
	}

	/**
	 * Aktualisiert die Daten dieses Inforationsspeichers mit den im {@link #parent
	 * zugeordneten Speicher zu findenden Daten}.
	 */
	public void update() {

		/*
		 * Aktualisiert die Prioritäten aller Schüler.
		 */
		parent.getAllStudents().forEach(Student::refreshPriority);

		/*
		 * Aktualisiert die Liste der nicht zugewiesenden Schüler und berechnet die Zahö
		 * der Schüler, die verteilt wurden.
		 */
		this.unallocatedStudents.clear();

		int sCount = 0;

		for (Student s : this.parent.getAllStudents()) {
			if (s.isMarked() || s.getPriority() > this.parent.getHighestPriorityWhithoutIntegerMax()
					|| s.getPriority() < 0) {
				s.mark();
				this.unallocatedStudents.add(s);
			}
			if (s.getActiveCourse() == null)
				++sCount;
			if (s.getActiveCourse() != null && !s.getActiveCourse().equals(Distributor.getInstance().ignoredCourse))
				sCount -= -1;
		}

		this.studentCount = sCount;

		/*
		 * Aktualisiert die Anzahlen der Schüler mit den jeweiligen Prioritäten.
		 */
		this.studentPriorities = this.parent.getStudentPriorities();

		/*
		 * Aktualisiert die höchste Priorität dieser Verteilung.
		 */
		this.highestPriority = parent.getHighestPriority() == Integer.MAX_VALUE ? -1 : parent.getHighestPriority();

		/*
		 * Aktualisiert die Rate dieser Verteilung.
		 */
		this.rate = parent.rate(this.highestPriority);

		/*
		 * Aktualisiert die Liste der Schüler mit der schlechtesten Priorität.
		 */
		this.badPriorityStudents = parent.getStudentsWithPriority(parent.getHighestPriorityWhithoutIntegerMax());

		/*
		 * Gleicht die Anzahl der Schüler mit den Prioritäten veränderten Werten an.
		 */
		if (this.unallocatedStudents.size() != this.studentPriorities[this.studentPriorities.length - 1])
			this.studentPriorities[this.studentPriorities.length - 1] = this.unallocatedStudents.size();

		if (!this.badPriorityStudents.isEmpty()
				&& this.studentPriorities[this.studentPriorities.length - 2] != this.badPriorityStudents.size())
			this.studentPriorities[this.studentPriorities.length - 2] = this.badPriorityStudents.size();

		/*
		 * Aktualisiert die Güte der Verteilung.
		 */
		this.updateGuete();

		this.calculateExpectation();

		this.calculateVariance();

		this.calculateStandardDeviation();
	}

	/**
	 * Aktualisiert die Güte dieses Speichers.
	 */
	private void updateGuete() {
		if (!Config.useNewGoodness)
			this.guete = (double) this.parent.getAllStudents().size() / (double) this.rate;
		else {
			/*
			 * Addiert die Prioritäten der Schüler mit umgekehrten Prioritätszuordnung.
			 */
			int studentPriorities = 0;

			for (Student s : this.parent.getAllStudents()) {
				if (s.getActiveCourse() == null) {
					continue;
				}

				if (s.getActiveCourse() == null || !s.getActiveCourse().equals(Distributor.getInstance().ignoredCourse))
					studentPriorities += this.translatePriority(s.getPriority());

			}

			/*
			 * Ermittelt die genutzte höchste Priorität.
			 */
			int highest = this.parent.getHighestPriority();
			if (highest > this.parent.getHighestPriorityWhithoutIntegerMax() || highest < 0)
				highest = this.parent.getHighestPriorityWhithoutIntegerMax() + Config.addForUnallocatedStudents;

			/*
			 * Ermittelt die Anzahl der Schüler.
			 */
			int studentCount = 0;
			for (Student s : this.parent.getAllStudents())
				if (s.getActiveCourse() == null || !s.getActiveCourse().equals(Distributor.getInstance().ignoredCourse))
					studentCount++;

			/*
			 * Berechnet die Güte aus den ermittelten Werten.
			 */
			this.guete = studentPriorities / ((double) highest * (double) studentCount);

		}
	}

	/**
	 * Wandelt die gegebene Priorität um. Die kleinste Prioritätszahl wird dabei zum
	 * größten Wert und umgekehrt.
	 * 
	 * @param priority Die Priorität, die umgewandelt werden soll.
	 * @return Die umgewandelte Priorität.
	 */
	private int translatePriority(int priority) {
		if (priority > this.parent.getHighestPriorityWhithoutIntegerMax() || priority < 0)
			return 0;

		return this.parent.getHighestPriority() > this.parent.getHighestPriorityWhithoutIntegerMax()
				|| this.parent.getHighestPriority() < 0
						? this.parent.getHighestPriorityWhithoutIntegerMax() - priority + 2
						: this.parent.getHighestPriority() - priority + 1;
	}

	/**
	 * @return Die Güte dieser Berechnung.
	 */
	public double getGuete() {
		return this.guete;
	}

	public void calculateExpectation() {
		this.expectation = 0;

		for (int i = 0; i < this.parent.getStudentPriorities().length; i++) {
			this.expectation += (i + 1) * this.getP(i);
		}

	}

	public void calculateVariance() {
		this.variance = 0;

		for (int i = 0; i < this.parent.getStudentPriorities().length; i++) {
			this.variance += ((i + 1) - this.getExpectation()) * ((i + 1) - this.getExpectation()) * getP(i);
		}

	}

	public void calculateStandardDeviation() {
		this.standardDeviation = Math.sqrt(this.getVariance());
	}

	public double getVariance() {
		return this.variance;
	}

	public double getStandartDeviation() {
		return this.standardDeviation;
	}

	public double getExpectation() {
		return this.expectation;
	}

	public double getP(int priority) {
		return ((double) this.parent.getStudentPriorities()[priority]) / ((double) this.getStudentCount());
	}

}
