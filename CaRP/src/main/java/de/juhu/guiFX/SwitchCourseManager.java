package de.juhu.guiFX;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import de.juhu.distributor.Course;
import de.juhu.distributor.Distributor;
import de.juhu.distributor.Student;
import de.juhu.util.References;
import de.juhu.util.Util;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import static de.noisruker.logger.Logger.LOGGER;

/**
 * Behandelt alle Aktionen des Fensters zum Bearbeiten von Kursen in der
 * Verteilung.
 * 
 * @author Juhu1705
 * @category GUI
 */
public class SwitchCourseManager implements Initializable {

	public static Student student;

//	@FXML
//	private TableView<Course> courses;

	@FXML
	private TextField prename;

	@FXML
	private TextField name;

	@FXML
	private ComboBox<String> comboBox;

//	@FXML
//	private TableColumn<Course, String> teacher, subject;

//	private SwitchCourseView scw;
//
//	public void onSetActive(ActionEvent event) {
//		Course c = this.courses.getSelectionModel().getSelectedItem();
//
//		if (c == null || (c.getSubject() == null || c.getTeacher() == null)
//				|| (c.getSubject().isEmpty() && c.getTeacher().isEmpty()))
//			return;
//
//		student.setActiveCourse(c);
//
//		this.scw.run();
//	}

	public void onFinished(ActionEvent event) {
		boolean missingInformation = false;
		if (this.prename.getText().isEmpty()) {
			this.prename.setPromptText(References.language.getString("valuemissing.text"));
			missingInformation = true;
		}
		if (this.name.getText().isEmpty()) {
			this.name.setPromptText(References.language.getString("valuemissing.text"));
			missingInformation = true;
		}
		if (this.comboBox.getValue() == null
				|| this.comboBox.getValue().equals(References.language.getString("course.text"))
				|| this.comboBox.getValue().equals(References.language.getString("valuemissing.text"))) {
			this.comboBox.setPromptText(References.language.getString("valuemissing.text"));
			missingInformation = true;
		}

		if (missingInformation)
			return;

		student.setName(this.name.getText());
		student.setPrename(this.prename.getText());

		for (Course c : GUIManager.actual.getAllCoursesAsArray()) {
			if (this.comboBox.getValue().endsWith(c.getSubject() + ", " + c.getTeacher() + " | "
					+ Integer.toString(c.size()) + "/" + Integer.toString(c.getMaxStudentCount())))
				student.setActiveCourse(c);
		}

		Distributor.calculated.peek().getInformation().update();
		Platform.runLater(GUIManager.getInstance().outputSView);
		Platform.runLater(GUIManager.getInstance().outputCView);
		Platform.runLater(GUIManager.getInstance().outputIView);

		((Stage) ((Button) event.getSource()).getScene().getWindow()).close();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		if (student == null)
			Thread.currentThread().interrupt();

		LOGGER.info("Student: " + student.toString());

		ArrayList<String> courses = new ArrayList<>();

		int i = 1;

		for (Course c : student.getCourses()) {
			if (student.getActiveCourse().equals(c))
				courses.add("(" + i++ + ".) " + c.getSubject() + ", " + c.getTeacher() + " | "
						+ Integer.toString(c.size()) + "/" + Integer.toString(c.getMaxStudentCount()));
			else
				courses.add(i++ + ". " + c.getSubject() + ", " + c.getTeacher() + " | " + Integer.toString(c.size())
						+ "/" + Integer.toString(c.getMaxStudentCount()));
		}

		comboBox.setItems(FXCollections.observableArrayList(courses));

		Course c = student.getActiveCourse();
		i = student.getPosition(c) + 1;

		if (student.getActiveCourse() != null)
			comboBox.setValue("(" + i++ + ".) " + c.getSubject() + ", " + c.getTeacher() + " | "
					+ Integer.toString(c.size()) + "/" + Integer.toString(c.getMaxStudentCount()));
//		this.teacher.setCellValueFactory(s -> {
//			return new SimpleStringProperty(s.getValue().getTeacher());
//		});
//
//		this.subject.setCellValueFactory(s -> {
//			return new SimpleStringProperty(s.getValue().getSubject());
//		});

		if (!Util.isBlank(student.getName()))
			this.name.setText(student.getName());

		if (!Util.isBlank(student.getPrename()))
			this.prename.setText(student.getPrename());

//		this.scw = new SwitchCourseView(this.courses, student);
//		this.scw.run();

	}
}