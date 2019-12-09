package de.juhu.distributor;

import java.io.Serializable;
import java.util.ArrayList;

import de.juhu.dateimanager.WriteableContent;
import de.juhu.math.Vec2i;
import de.juhu.util.Config;
import de.juhu.util.References;

public class InformationSave implements Serializable {

	Save parent;

	private int highestPriority, rate;
	private double guete;

	private ArrayList<Student> unallocatedStudents = new ArrayList<Student>();
	private ArrayList<Student> badPriorityStudents = new ArrayList<Student>();
	private int[] studentPriorities;

	public InformationSave(int highestPriority, int rate, int[] studentPriorities) {
		this.highestPriority = highestPriority;
		this.rate = rate;
		this.studentPriorities = studentPriorities;
	}

	public InformationSave(int highestPriority, int rate, int[] priorities, ArrayList<Student> unallocatedStudents,
			ArrayList<Student> studentsWithBadPriority) {
		this(highestPriority, rate, priorities);

		this.unallocatedStudents = unallocatedStudents;
		this.badPriorityStudents = studentsWithBadPriority;
	}

	public WriteableContent write() {
		this.update();

		WriteableContent information = new WriteableContent(References.language.getString("statistics.text"));

		int line = 0;

		information.addLine(new Vec2i(0, line++), new String[] { References.language.getString("informations.text"),
				References.language.getString("value.text") });

		information.addLine(new Vec2i(0, ++line), new String[] {
				References.language.getString("highestpriority.text") + ": ", Integer.toString(this.highestPriority) });
		information.addLine(new Vec2i(0, ++line), new String[] {
				References.language.getString("calculationrate.text") + ": ", Integer.toString(this.rate) });
		information.addLine(new Vec2i(0, ++line), new String[] {
				References.language.getString("calculationgoodness.text") + ": ", Double.toString(this.guete) });

		line -= -2;

		information.addLine(new Vec2i(0, line++), new String[] { References.language.getString("priorities.text"),
				References.language.getString("countofstudents.text") });
		for (int i = 0; i < studentPriorities.length - 1; i++) {
			information.addLine(new Vec2i(0, line++),
					new String[] { Integer.toString(i + 1), Integer.toString(this.studentPriorities[i]) });
		}

		line += 1;

		if (this.studentPriorities[this.studentPriorities.length - 1] != 0) {
			information.addLine(new Vec2i(0, line++),
					new String[] { References.language.getString("unallocatedstudents.text"),
							Integer.toString(this.studentPriorities[this.studentPriorities.length - 1]) });

			information.addLine(new Vec2i(0, line++), new String[] { "Name", "First Choice" });

			for (Student s : this.unallocatedStudents)
				if (s.getCourses().length > 1)
					information.addLine(new Vec2i(0, line++),
							new String[] { s.toString(), s.getCourses()[0].toString() });

		} else {
			information.addLine(new Vec2i(0, line++), new String[] { "No unallocated Students" });
		}

		line++;

		information.addLine(new Vec2i(0, line++),
				new String[] { References.language.getString("studentswithbadpriority") });
		information.addLine(new Vec2i(0, line++), new String[] { "Name", "First Choice" });

		for (Student s : this.badPriorityStudents)
			if (s.getCourses().length > 1)
				information.addLine(new Vec2i(0, line++), new String[] { s.toString(), s.getCourses()[0].toString() });

		return information;
	}

	public int getHighestPriority() {
		return this.highestPriority;
	}

	public int getRate() {
		return this.rate;
	}

	public int[] getStudentPriorities() {
		return this.studentPriorities;
	}

	public ArrayList<Student> getBStudents() {
		return this.badPriorityStudents;
	}

	public ArrayList<Student> getunallocatedStudents() {
		return this.unallocatedStudents;
	}

	public void update() {

		parent.getAllStudents().forEach(s -> s.refreshPriority());

		this.unallocatedStudents.clear();

		for (Student s : this.parent.getAllStudents())
			if (s.isMarked() || s.getPriority() > this.parent.getHighestPriorityWhithoutIntegerMax()
					|| s.getPriority() < 0) {
				s.mark();
				this.unallocatedStudents.add(s);
			}

		if (this.badPriorityStudents.isEmpty()) {
			this.highestPriority--;
			this.studentPriorities[this.studentPriorities.length - 2] = 0;

			int[] newSP = new int[this.studentPriorities.length - 1];

			for (int i = 0; i < newSP.length - 1; i++) {
				newSP[i] = this.studentPriorities[i];
			}

			newSP[newSP.length - 1] = this.studentPriorities[this.highestPriority - 1];

			this.studentPriorities = newSP;
		}

		this.badPriorityStudents = parent.getStudentsWithPriority(parent.getHighestPriorityWhithoutIntegerMax());

		if (this.unallocatedStudents.size() != this.studentPriorities[this.studentPriorities.length - 1])
			this.studentPriorities[this.studentPriorities.length - 1] = this.unallocatedStudents.size();

		if (!this.badPriorityStudents.isEmpty()
				&& this.studentPriorities[this.studentPriorities.length - 2] != this.badPriorityStudents.size())
			this.studentPriorities[this.studentPriorities.length - 2] = this.badPriorityStudents.size();

		this.highestPriority = parent.getHighestPriority();
		this.rate = parent.rate(this.highestPriority);

		this.studentPriorities = this.parent.getStudentPriorities();

		this.updateGuete();
	}

	private void updateGuete() {
		if (Config.useNewGoodness)
			this.guete = (double) this.parent.getAllStudents().size() / (double) this.rate;
		else {
			int i = 0;

			for (Student s : this.parent.getAllStudents()) {
				if (s.getActiveCourse() == null) {
					continue;
				}

				if (s.getActiveCourse() == null || !s.getActiveCourse().equals(Distributor.getInstance().ignoredCourse))
					i += this.translatePriority(s.getPriority());

			}

			int highest = this.parent.getHighestPriority();
			if (highest > this.parent.getHighestPriorityWhithoutIntegerMax() || highest < 0)
				highest = this.parent.getHighestPriorityWhithoutIntegerMax() + 10;

			int studentCount = 0;
			for (Student s : this.parent.getAllStudents())
				if (s.getActiveCourse() == null || !s.getActiveCourse().equals(Distributor.getInstance().ignoredCourse))
					studentCount++;

			this.guete = i / ((double) highest * (double) studentCount);

		}
	}

	private int translatePriority(int priority) {
		if (priority > this.parent.getHighestPriorityWhithoutIntegerMax() || priority < 0)
			return 0;

		return this.parent.getHighestPriority() > this.parent.getHighestPriorityWhithoutIntegerMax()
				|| this.parent.getHighestPriority() < 0
						? this.parent.getHighestPriorityWhithoutIntegerMax() - priority + 2
						: this.parent.getHighestPriority() - priority + 1;
	}

	public double getGuete() {
		return this.guete;
	}

}
