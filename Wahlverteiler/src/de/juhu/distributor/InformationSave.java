package de.juhu.distributor;

import java.io.Serializable;
import java.util.ArrayList;

import de.juhu.dateimanager.WriteableContent;
import de.juhu.math.Vec2i;
import de.juhu.util.Config;

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

		WriteableContent information = new WriteableContent("Information");

		int line = 0;

		information.addLine(new Vec2i(0, line++), new String[] { "Information", "Value" });

		information.addLine(new Vec2i(0, ++line),
				new String[] { "Highest Priority: ", Integer.toString(this.highestPriority) });
		information.addLine(new Vec2i(0, ++line), new String[] { "Calculation Rate: ", Integer.toString(this.rate) });

		line -= -2;

		information.addLine(new Vec2i(0, line++), new String[] { "Priority", "Count of Students with Priority" });
		for (int i = 0; i < studentPriorities.length - 1; i++) {
			information.addLine(new Vec2i(0, line++),
					new String[] { Integer.toString(i + 1), Integer.toString(this.studentPriorities[i]) });
		}

		line += 1;

		if (this.studentPriorities[this.studentPriorities.length - 1] != 0) {
			information.addLine(new Vec2i(0, line++), new String[] { "Unallocated Students",
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

		information.addLine(new Vec2i(0, line++), new String[] { "Students with bad priority" });
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

		for (int i = 0; i < this.unallocatedStudents.size();) {

			if (!this.unallocatedStudents.get(i).isMarked())
				this.unallocatedStudents.remove(i).refreshPriority();
			else
				i++;

		}

		parent.getAllStudents().forEach(s -> s.refreshPriority());

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

			for (Student s : this.parent.getAllStudents())
				if (s.getActiveCourse() == null || !s.getActiveCourse().equals(Distributor.getInstance().ignoredCourse))
					i += this.translatePriority(s.getPriority());

			int highest = this.parent.getHighestPriority();
			if (highest == Integer.MAX_VALUE)
				highest = this.parent.getHighestPriorityWhithoutIntegerMax() + 1;

			int studentCount = 0;
			for (Student s : this.parent.getAllStudents())
				if (s.getActiveCourse() == null || !s.getActiveCourse().equals(Distributor.getInstance().ignoredCourse))
					studentCount++;

			this.guete = i / ((double) highest * (double) studentCount);

		}
	}

	private int translatePriority(int priority) {
		if (priority == Integer.MAX_VALUE)
			return 1;

		return this.parent.getHighestPriority() == Integer.MAX_VALUE ? this.parent.getHighestPriority() - priority + 2
				: this.parent.getHighestPriority() - priority + 1;
	}

	public double getGuete() {
		return this.guete;
	}

}
