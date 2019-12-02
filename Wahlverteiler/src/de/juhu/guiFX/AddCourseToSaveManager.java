package de.juhu.guiFX;

import java.net.URL;
import java.util.ResourceBundle;

import de.juhu.distributor.Course;
import de.juhu.util.Config;
import de.juhu.util.References;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddCourseToSaveManager implements Initializable {

	public static String s, t;
	public static int mS = -2;

	@FXML
	private TextField subject;

	@FXML
	private TextField teacher;

	@FXML
	private Spinner<Integer> maxStudents;

	public void onAdd(ActionEvent event) {
		boolean missingInformation = false;
		if (this.subject.getText().isEmpty()) {
			this.subject.setPromptText("Value Missing");
			missingInformation = true;
		}
		if (this.teacher.getText().isEmpty()) {
			this.teacher.setPromptText("Value Missing");
			missingInformation = true;
		}
		if (missingInformation)
			return;

		if (s != null && t != null) {
			References.LOGGER.info("Try to configure Course");
			Course c = GUIManager.actual.getCourseByName(s + "|" + t);
			c.setTeacher(teacher.getText());
			c.setSubject(subject.getText());
			c.setStudentMax(this.maxStudents.getValue().intValue());

			GUIManager.actual.addCourse(c);
		} else {
			References.LOGGER.info("Try to add new Course");
			Course c = new Course(this.subject.getText(), this.teacher.getText(),
					this.maxStudents.getValue().intValue());

			GUIManager.actual.addCourse(c);
		}

		Platform.runLater(GUIManager.getInstance().outputSView);
		Platform.runLater(GUIManager.getInstance().outputCView);
		Platform.runLater(GUIManager.getInstance().outputIView);

		References.LOGGER.info("Course was added");

		s = null;
		t = null;
		mS = -2;

		((Stage) ((Button) event.getSource()).getScene().getWindow()).close();

	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		maxStudents.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(-1, Integer.MAX_VALUE,
				mS == -2 ? Config.normalStudentLimit : mS));

		if (s != null)
			subject.setText(s);

		if (t != null)
			teacher.setText(t);

	}

}
