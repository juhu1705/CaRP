package de.juhu.distributor;

import java.io.Serializable;
import java.util.ArrayList;

import de.juhu.filemanager.Vec2i;
import de.juhu.filemanager.WriteableContent;
import de.juhu.util.Config;
import de.juhu.util.References;

/**
 * Diese Klasse h�llt alle weiteren f�r eine Verteilung interressanten von dem
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
	 * Die h�chste Priorit�t, die im {@link #parent zugeordneten Speicher} vergeben
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
	 * Die Anzahl der zugewiesenden Sch�ler, die im {@link #parent zugeordneten
	 * Speicher} gespeichert sind. Alle Sch�ler, die nicht verteilt werden mussten
	 * werden hier nicht mit aufgef�hrt.
	 */
	private int studentCount;

	/**
	 * Alle Sch�ler aus dem {@link #parent zugeordneten Speicher}, die nicht
	 * zugewiesen werden konnten.
	 */
	private ArrayList<Student> unallocatedStudents = new ArrayList<Student>();

	/**
	 * Alle Sch�ler aus dem {@link #parent zugeordneten Speicher}, die die
	 * schlechteste Priorit�t besitzen, aber zugeordnet werden konnten.
	 */
	private ArrayList<Student> badPriorityStudents = new ArrayList<Student>();

	/**
	 * <p>
	 * Die Anzahl der Sch�ler aus dem {@link #parent zugeordneten Speicher} mit den
	 * entsprechenden Priorit�ten.
	 * </p>
	 * <ul>
	 * <li>Prio 1: studentPriorities[0]</li>
	 * <li>Prio 2: studentPriorities[1]</li>
	 * <li>Prio n: studentPriorities[n - 1]</li>
	 * <li>Anzahl der nicht zugewiesenden Sch�ler:
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
	 * {@link WriteableContent exportierbare Tabelle} und gibt diese zur�ck.
	 * 
	 * @return Eine exportierbare Tabelle mit den im Informationsspeicher
	 *         gespeicherten Daten.
	 */
	public WriteableContent write() {
		this.update();

		WriteableContent information = new WriteableContent(References.language.getString("statistics.text"));

		information.setStartTable(0);

		int line = 0;

		information.addLine(new Vec2i(0, line++), new String[] { References.language.getString("informations.text"),
				References.language.getString("value.text") });

		information.addLine(new Vec2i(0, ++line), new String[] {
				References.language.getString("calculationgoodness.text") + ": ", Double.toString(this.guete) });
		information.addLine(new Vec2i(0, ++line),
				new String[] { References.language.getString("highestpriority.text") + ": ",
						Integer.toString(this.parent.getHighestPriorityWhithoutIntegerMax()) });
//		information.addLine(new Vec2i(0, ++line), new String[] {
//				References.language.getString("expectation.text") + ": ", Double.toString(this.getExpectation()) });
//		information.addLine(new Vec2i(0, ++line),
//				new String[] { References.language.getString("standartDeviation.text") + ": ",
//						Double.toString(this.getStandartDeviation()) });
		information.addLine(new Vec2i(0, ++line),
				new String[] { References.language.getString("studentcount.text") + ": ",
						Integer.toString(this.parent.getAllStudents().size()) });
		information.addLine(new Vec2i(0, ++line),
				new String[] { References.language.getString("coursecount.text") + ": ",
						Integer.toString(this.parent.getAllCourses().size()) });
		information.addLine(new Vec2i(0, ++line),
				new String[] { References.language.getString("calculatedstudentcount.text") + ": ",
						Integer.toString(this.getStudentCount()) });

		line -= -2;

		information.addLine(new Vec2i(0, line++), new String[] { References.language.getString("priorities.text"),
				References.language.getString("countofstudents.text") });
		for (int i = 0; i < studentPriorities.length - 1; i++) {
			information.addLine(new Vec2i(0, line++),
					new String[] { Integer.toString(i + 1), Integer.toString(this.studentPriorities[i]),
							Double.toString((double) this.studentPriorities[i] / (double) this.getStudentCount()) });
		}

		line += 1;

		if (this.studentPriorities[this.studentPriorities.length - 1] != 0) {
			information.addLine(new Vec2i(0, line++),
					new String[] { References.language.getString("unallocatedstudents.text"),
							Integer.toString(this.studentPriorities[this.studentPriorities.length - 1]) });

			information.addLine(new Vec2i(0, line++), new String[] { References.language.getString("student.text"),
					References.language.getString("firstchoise.text") });

			for (Student s : this.unallocatedStudents)
				if (s.getCourses().length > 1)
					information.addLine(new Vec2i(0, line++),
							new String[] { s.toString(), s.getCourses()[0].toString() });

		} else {
			information.addLine(new Vec2i(0, line++),
					new String[] { References.language.getString("nounallocated.text") });
		}

		line = line % 2 == 0 ? line + (line - 1) % 2 : line + line % 2;

		information.addLine(new Vec2i(0, line++),
				new String[] { References.language.getString("studentswithbadpriority") });
		information.addLine(new Vec2i(0, line++), new String[] { References.language.getString("student.text"),
				References.language.getString("firstchoise.text") });

		for (Student s : this.badPriorityStudents)
			if (s.getCourses().length > 1)
				information.addLine(new Vec2i(0, line++), new String[] { s.toString(), s.getCourses()[0].toString() });

		return information;
	}

	/**
	 * @return {@link #highestPriority Die h�chste Priorit�t} der gespeicherten
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
	 * @return Die Anzahl der Sch�ler mit den zugeordneten Priorit�ten.
	 * @see Save#getStudentPriorities()
	 * @see InformationSave#studentPriorities
	 */
	public int[] getStudentPriorities() {
		return this.studentPriorities;
	}

	/**
	 * @return Gibt alle Sch�ler mit der schlechtesten Priorit�t der gespeicherten
	 *         Berechnung zur�ck.
	 */
	public ArrayList<Student> getBStudents() {
		return this.badPriorityStudents;
	}

	/**
	 * @return Gibt alle Sch�ler zur�ck, die nicht zugewiesen werden konnten.
	 */
	public ArrayList<Student> getUStudents() {
		return this.unallocatedStudents;
	}

	/**
	 * @return Gibt die Anzahl der Berechneten Sch�ler zur�ck.
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
		 * Aktualisiert die Priorit�ten aller Sch�ler.
		 */
		parent.getAllStudents().forEach(s -> s.refreshPriority());

		/*
		 * Aktualisiert die Liste der nicht zugewiesenden Sch�ler und berechnet die Zah�
		 * der Sch�ler, die verteilt wurden.
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
		 * Aktualisiert die Anzahlen der Sch�ler mit den jeweiligen Priorit�ten.
		 */
		this.studentPriorities = this.parent.getStudentPriorities();

		/*
		 * Aktualisiert die h�chste Priorit�t dieser Verteilung.
		 */
		this.highestPriority = parent.getHighestPriority() == Integer.MAX_VALUE ? -1 : parent.getHighestPriority();

		/*
		 * Aktualisiert die Rate dieser Verteilung.
		 */
		this.rate = parent.rate(this.highestPriority);

		/*
		 * Aktualisiert die Liste der Sch�ler mit der schlechtesten Priorit�t.
		 */
		this.badPriorityStudents = parent.getStudentsWithPriority(parent.getHighestPriorityWhithoutIntegerMax());

		/*
		 * Gleicht die Anzahl der Sch�ler mit den Priorit�ten ver�nderten Werten an.
		 */
		if (this.unallocatedStudents.size() != this.studentPriorities[this.studentPriorities.length - 1])
			this.studentPriorities[this.studentPriorities.length - 1] = this.unallocatedStudents.size();

		if (!this.badPriorityStudents.isEmpty()
				&& this.studentPriorities[this.studentPriorities.length - 2] != this.badPriorityStudents.size())
			this.studentPriorities[this.studentPriorities.length - 2] = this.badPriorityStudents.size();

		/*
		 * Aktualisiert die G�te der Verteilung.
		 */
		this.updateGuete();

		this.calculateExpectation();

		this.calculateVariance();

		this.calculateStandardDeviation();
	}

	/**
	 * Aktualisiert die G�te dieses Speichers.
	 */
	private void updateGuete() {
		if (!Config.useNewGoodness)
			this.guete = (double) this.parent.getAllStudents().size() / (double) this.rate;
		else {
			/*
			 * Addiert die Priorit�ten der Sch�ler mit umgekehrten Priorit�tszuordnung.
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
			 * Ermittelt die genutzte h�chste Priorit�t.
			 */
			int highest = this.parent.getHighestPriority();
			if (highest > this.parent.getHighestPriorityWhithoutIntegerMax() || highest < 0)
				highest = this.parent.getHighestPriorityWhithoutIntegerMax() + Config.addForUnallocatedStudents;

			/*
			 * Ermittelt die Anzahl der Sch�ler.
			 */
			int studentCount = 0;
			for (Student s : this.parent.getAllStudents())
				if (s.getActiveCourse() == null || !s.getActiveCourse().equals(Distributor.getInstance().ignoredCourse))
					studentCount++;

			/*
			 * Berechnet die G�te aus den ermittelten Werten.
			 */
			this.guete = studentPriorities / ((double) highest * (double) studentCount);

		}
	}

	/**
	 * Wandelt die gegebene Priorit�t um. Die kleinste Priorit�tszahl wird dabei zum
	 * gr��ten Wert und umgekehrt.
	 * 
	 * @param priority Die Priorit�t, die umgewandelt werden soll.
	 * @return Die umgewandelte Priorit�t.
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
	 * @return Die G�te dieser Berechnung.
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
