package de.juhu.guiFX.lists;

import java.util.ArrayList;

import de.juhu.distributor.Course;
import de.juhu.distributor.Student;
import de.juhu.util.References;
import de.juhu.util.Util;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class SwitchCourseView implements Runnable {

	TableView tv;

	ArrayList<TableColumn<Course, String>> students = new ArrayList<>();

	Student student;

	public SwitchCourseView(TableView<Course> inputTable, Student student) {
		this.tv = inputTable;
		this.student = student;
	}

	public void fill() {
		References.LOGGER.config("Loading Courses Data!");

		this.tv.setItems(FXCollections.observableArrayList(this.student.getCoursesAsList()));
	}

	private void loadMaxStudents() {
		for (TableColumn<Course, String> t : students) {
			tv.getColumns().remove(t);
		}
		students.clear();

		int maxCount = Util.maxStudentCount(this.student.getCoursesAsList());

		for (int i = 0; i < maxCount; i++) {
			TableColumn<Course, String> prename = new TableColumn<>("Prename");
			TableColumn<Course, String> name = new TableColumn<>("Name");
			TableColumn<Course, String> student = new TableColumn<>("Student " + Integer.toString(i + 1));

			final int position = i;

			student.getColumns().addAll(prename, name);

			prename.setCellValueFactory(s -> {
				if (s.getValue().getStudents().size() > position)
					return new SimpleStringProperty(s.getValue().getStudent(position).getPrename());
				return new SimpleStringProperty("-");
			});

			name.setCellValueFactory(s -> {
				if (s.getValue().getStudents().size() > position)
					return new SimpleStringProperty(s.getValue().getStudent(position).getName());
				return new SimpleStringProperty("-");
			});

			this.students.add(student);

			this.tv.getColumns().addAll(student);
		}
	}

	@Override
	public void run() {
		this.loadMaxStudents();

		fill();
	}

}
