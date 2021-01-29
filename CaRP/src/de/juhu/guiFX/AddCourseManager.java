package de.juhu.guiFX;

import java.net.URL;
import java.util.ResourceBundle;

import de.juhu.distributor.Course;
import de.juhu.distributor.Distributor;
import de.juhu.util.Config;
import de.juhu.util.References;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Behandelt alle Aktionen des Fensters zum Hinzufügen und Bearbeiten von Kursen
 * zur Liste.
 * 
 * @author Juhu1705
 * @category GUI
 */
public class AddCourseManager implements Initializable {

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
			this.subject.setPromptText(References.language.getString("valuemissing.text"));
			missingInformation = true;
		}
		if (this.teacher.getText().isEmpty()) {
			this.teacher.setPromptText(References.language.getString("valuemissing.text"));
			missingInformation = true;
		}
		if (missingInformation)
			return;

		if (s != null && t != null) {
			References.LOGGER.info("Try to configure Course");
			Course c = Distributor.getInstance().getCourseByName(s + "|" + t);
			c.setTeacher(teacher.getText());
			c.setSubject(subject.getText());
			c.setStudentMax(this.maxStudents.getValue().intValue());

			Distributor.getInstance().addCourse(c);
		} else {
			References.LOGGER.info("Try to add new Course");
			Course c = new Course(this.subject.getText(), this.teacher.getText(),
					this.maxStudents.getValue().intValue());

			Distributor.getInstance().addCourse(c);
		}

		GUIManager.getInstance().inputView.fill();
		GUIManager.getInstance().cView.fill();

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
