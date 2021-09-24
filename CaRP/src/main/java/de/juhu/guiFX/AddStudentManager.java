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
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import static de.noisruker.logger.Logger.LOGGER;

/**
 * Behandelt alle Aktionen des Fensters zum Hinzufügen und Bearbeiten von
 * Schülern zur Liste.
 * 
 * @author Juhu1705
 * @category GUI
 */
public class AddStudentManager implements Initializable {

	public ArrayList<ComboBox<String>> cources = new ArrayList<>((Config.maxChooses) * 2);

	public static int studentID = -1;

	@FXML
	public TextField prename, name;
	public ComboBox<String> c1f, c1t;

	@FXML
	public VBox vBox;

	public ComboBox<String> last;

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
		if (c1f.getEditor().getText().isEmpty()) {
			c1f.setPromptText(References.language.getString("valuemissing.text"));
			missingInformation = true;
		}
		if (c1t.getEditor().getText().isEmpty() && !Util.isIgnoreCourse(c1f.getEditor().getText())) {
			c1t.setPromptText(References.language.getString("valuemissing.text"));
			missingInformation = true;
		}
		if (missingInformation)
			return;

		if (studentID == -1) {
			Course[] c = new Course[cources.size() / 2 + 1];

			c[0] = Distributor.getInstance()
					.getOrCreateCourseByName(c1f.getEditor().getText() + "|" + c1t.getEditor().getText());
			for (int i = 0, n = 1; i + 1 < cources.size(); i += 2, n++) {
				if (Util.isBlank(cources.get(i).getEditor().getText() + cources.get(i + 1).getEditor().getText()))
					continue;
				c[n] = Distributor.getInstance().getOrCreateCourseByName(
						cources.get(i).getEditor().getText() + "|" + cources.get(i + 1).getEditor().getText());
			}

			Student s = new Student(name.getText(), prename.getText(), c);

			if (c1f.getEditor().getText().contains(Config.ignoreStudent))
				s = new Student(name.getText(), prename.getText(), Distributor.getInstance().ignore());

			Distributor.getInstance().addStudent(s);
		} else {
			Student s = Distributor.getInstance().getStudentByID(studentID);
			s.setName(name.getText());
			s.setPrename(prename.getText());

			Course[] c = new Course[cources.size() / 2 + 1];

			c[0] = Distributor.getInstance()
					.getOrCreateCourseByName(c1f.getEditor().getText() + "|" + c1t.getEditor().getText());
			for (int i = 0, n = 1; i + 1 < cources.size(); i += 2, n++) {
				if (Util.isBlank(cources.get(i).getEditor().getText() + cources.get(i + 1).getEditor().getText()))
					continue;
				c[n] = Distributor.getInstance().getOrCreateCourseByName(
						cources.get(i).getEditor().getText() + "|" + cources.get(i + 1).getEditor().getText());
			}

			s.setCourses(c);

			Distributor.getInstance().addStudent(s);

		}

		GUIManager.getInstance().inputView.fill();
		GUIManager.getInstance().cView.fill();

		((Stage) ((Button) event.getSource()).getScene().getWindow()).close();

		studentID = -1;
	}

	public void filledAction(ActionEvent event) {
		filled(null);
	}

	public void filled(KeyEvent event) {
		LOGGER.config(last.getEditor().getText());
		if (!last.getEditor().getText().equals("") && i < Config.maxChooses) {
			i++;
			HBox hBox = new HBox(10);
			hBox.setPadding(new Insets(10));
			Label label = new Label(References.language.getString("course.text") + " " + i + ":"),
					subject = new Label(References.language.getString("subject.text") + ":"),
					teacher = new Label(References.language.getString("teacher.text") + ":");
			ComboBox st = new ComboBox(), tt = new ComboBox();

			st.setEditable(true);
			tt.setEditable(true);

			st.setFocusTraversable(true);
			tt.setFocusTraversable(true);

			ArrayList<String> teachers = new ArrayList<String>(), subjects = new ArrayList<String>();

			for (Course c : Distributor.getInstance().getCourses()) {
				if (!teachers.contains(c.getTeacher()))
					teachers.add(c.getTeacher());
				if (!subjects.contains(c.getSubject()))
					subjects.add(c.getSubject());
			}

			tt.setItems(FXCollections.observableArrayList(teachers));
			st.setItems(FXCollections.observableArrayList(subjects));

			this.cources.add(st);
			this.cources.add(tt);

			tt.setOnKeyReleased(k -> {
				filled(k);
			});

			tt.setOnAction(k -> {
				filled(null);
			});
			last = tt;
			hBox.getChildren().addAll(subject, st, teacher, tt);
			vBox.getChildren().addAll(label, hBox);
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		LOGGER.info("Initialize Add Student Manager");

		ArrayList<String> teachers = new ArrayList<String>(), subjects = new ArrayList<String>();

		for (Course c : Distributor.getInstance().getCourses()) {
			if (!teachers.contains(c.getTeacher()))
				teachers.add(c.getTeacher());
			if (!subjects.contains(c.getSubject()))
				subjects.add(c.getSubject());
		}

		c1t.setItems(FXCollections.observableArrayList(teachers));
		c1f.setItems(FXCollections.observableArrayList(subjects));

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
					c1f.getEditor().setText(c.getSubject());
					c1t.getEditor().setText(c.getTeacher());
					filled(null);
				} else {
					if (this.cources.size() < i + 1)
						break;

					if (this.cources.get(i) == null || this.cources.get(i + 1) == null)
						break;

					this.cources.get(i++).getEditor().setText(c.getSubject());
					this.cources.get(i++).getEditor().setText(c.getTeacher());
					filled(null);
				}
			}
		}
	}
}
