package de.juhu.guiFX;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import de.juhu.distributor.Course;
import de.juhu.distributor.Distributor;
import de.juhu.distributor.Student;
import de.juhu.util.Config;
import de.juhu.util.References;
import de.juhu.util.Util;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Behandelt alle Aktionen des Fensters zum hinzufügen und bearbeiten von
 * Schülern zur Liste.
 * 
 * @author Juhu1705
 * @category GUI
 */
public class AddStudentManager implements Initializable {

	public ArrayList<TextField> cources = new ArrayList<>((Config.maxChooses) * 2);

	public static int studentID = -1;

	@FXML
	public TextField prename, name, c1f, c1t;

	@FXML
	public VBox vBox;

	public TextField last;

	public int i = 1;

	public void onAdd(ActionEvent event) {
		boolean missingInformation = false;
		if (prename.getText().isEmpty()) {
			prename.setPromptText(References.language.getString("valuemissing.text"));
			missingInformation = true;
		}
		if (name.getText().isEmpty()) {
			name.setPromptText(References.language.getString("valuemissing.text"));
			missingInformation = true;
		}
		if (c1f.getText().isEmpty()) {
			c1f.setPromptText(References.language.getString("valuemissing.text"));
			missingInformation = true;
		}
		if (c1t.getText().isEmpty() && !Util.isIgnoreCourse(c1f.getText())) {
			c1t.setPromptText(References.language.getString("valuemissing.text"));
			missingInformation = true;
		}
		if (missingInformation)
			return;

		if (studentID == -1) {
			Course[] c = new Course[cources.size() / 2 + 1];

			c[0] = Distributor.getInstance().getOrCreateCourseByName(c1f.getText() + "|" + c1t.getText());
			for (int i = 0, n = 1; i + 1 < cources.size(); i += 2, n++) {
				if (Util.isBlank(cources.get(i).getText() + cources.get(i + 1).getText()))
					continue;
				c[n] = Distributor.getInstance()
						.getOrCreateCourseByName(cources.get(i).getText() + "|" + cources.get(i + 1).getText());
			}

			Student s = new Student(name.getText(), prename.getText(), c);

			if (c1f.getText().contains(Config.ignoreStudent))
				s = new Student(name.getText(), prename.getText(), Distributor.getInstance().ignore());

			Distributor.getInstance().addStudent(s);
		} else {
			Student s = Distributor.getInstance().getStudentByID(studentID);
			s.setName(name.getText());
			s.setPrename(prename.getText());

			Course[] c = new Course[cources.size() / 2 + 1];

			c[0] = Distributor.getInstance().getOrCreateCourseByName(c1f.getText() + "|" + c1t.getText());
			for (int i = 0, n = 1; i + 1 < cources.size(); i += 2, n++) {
				if (Util.isBlank(cources.get(i).getText() + cources.get(i + 1).getText()))
					continue;
				c[n] = Distributor.getInstance()
						.getOrCreateCourseByName(cources.get(i).getText() + "|" + cources.get(i + 1).getText());
			}

			s.setCourses(c);

			Distributor.getInstance().addStudent(s);

		}

		GUIManager.getInstance().inputView.fill();
		GUIManager.getInstance().cView.fill();

		((Stage) ((Button) event.getSource()).getScene().getWindow()).close();

		studentID = -1;
	}

	public void filled(KeyEvent event) {
		if (!last.getText().equals("") && i < Config.maxChooses) {
			i++;
			HBox hBox = new HBox(10);
			hBox.setPadding(new Insets(10));
			Label label = new Label(References.language.getString("course.text") + " " + i + ":"),
					subject = new Label(References.language.getString("subject.text") + ":"),
					teacher = new Label(References.language.getString("teacher.text") + ":");
			TextField st = new TextField(), tt = new TextField();
			this.cources.add(st);
			this.cources.add(tt);

			tt.setOnKeyReleased(k -> {
				filled(k);
			});
			last = tt;
			hBox.getChildren().addAll(subject, st, teacher, tt);
			vBox.getChildren().addAll(label, hBox);
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		References.LOGGER.info("Initialize Add Student Manager");

		this.last = c1t;

		Student s = Distributor.getInstance().getStudentByID(studentID);
		int i = 0;
		if (s != null) {
			prename.setText(s.getPrename());
			name.setText(s.getName());
			boolean first = true;
			for (Course c : s.getCourses()) {
				if (first) {
					first = false;
					c1f.setText(c.getSubject());
					c1t.setText(c.getTeacher());
					filled(null);
				} else {
					if (this.cources.size() < i + 1)
						break;

					if (this.cources.get(i) == null || this.cources.get(i + 1) == null)
						break;

					this.cources.get(i++).setText(c.getSubject());
					this.cources.get(i++).setText(c.getTeacher());
					filled(null);
				}
			}
		}
	}
}
