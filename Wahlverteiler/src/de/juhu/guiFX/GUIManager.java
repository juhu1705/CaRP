package de.juhu.guiFX;

import static de.juhu.util.References.LOGGER;
import static de.juhu.util.References.LOGGING_HANDLER;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.logging.Level;

import de.juhu.dateimanager.CSVExporter;
import de.juhu.dateimanager.ConfigElement;
import de.juhu.dateimanager.ConfigManager;
import de.juhu.dateimanager.ExcelExporter;
import de.juhu.dateimanager.LogWriter;
import de.juhu.distributor.Course;
import de.juhu.distributor.Distributor;
import de.juhu.distributor.Save;
import de.juhu.distributor.Student;
import de.juhu.guiFX.lists.CourseView;
import de.juhu.guiFX.lists.InputView;
import de.juhu.guiFX.lists.OutputCourseView;
import de.juhu.guiFX.lists.OutputInformationView;
import de.juhu.guiFX.lists.OutputStudentsView;
import de.juhu.util.Config;
import de.juhu.util.PrintFormat;
import de.juhu.util.References;
import de.juhu.util.Util;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class GUIManager implements Initializable {

	/*
	 * TODO - Write a Save
	 */

	private static GUIManager instance;

	public static GUIManager getInstance() {
		return instance;
	}

	@FXML
	public ImageView i0;

	@FXML
	public ProgressBar p0;

	@FXML
	public TextArea ta1;

	@FXML
	public TextField t1, t2;

	@FXML
	public Button r1, r2, r3, r4, r5;

	@FXML
	public ComboBox<Level> cb1;

	@FXML
	public ComboBox<String> cb2;

	@FXML
	public ComboBox<PrintFormat> cb0;

	@FXML
	public TableView<Student> tv0, tv1, tv2, unallocatedStudents, bStudents;

	@FXML
	public TableView<Entry<String, Integer>> rates;

	@FXML
	public TableView<Entry<Integer, Integer>> priorities;

	@FXML
	public TableView<Course> tvc;

	@FXML
	public TableColumn<Student, String> vtc, ntc, k1tc, k1stc, k1ttc, cvtc, cntc, cptc, ckstc, ckttc, unallocatedName,
			unallocatedPrename, bName, bPrename, bSubject, bTeacher, bPriority;

	@FXML
	public TableColumn<Course, String> subject, teacher, oSubject, oTeacher;

	@FXML
	public TableColumn<Entry<String, Integer>, String> rate, rateV;

	@FXML
	public TableColumn<Entry<Integer, Integer>, String> priority, swpriority;

	@FXML
	public Tab students, teachers, statistics;

	public ArrayList<TableColumn<Student, String>> atci = new ArrayList<TableColumn<Student, String>>();

	public InputView inputView;
	public CourseView cView;

	public OutputStudentsView outputSView;
	public OutputCourseView outputCView;
	public OutputInformationView outputIView;

	@FXML
	public VBox config;

	@FXML
	public ListView<String> lv0;

	public void onDragOver(DragEvent event) {
		event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
	}

	@FXML
	public CheckMenuItem mb0, mb1;

	private CheckMenuItem last, lastSwitch;

	public void addCourse(ActionEvent event) {

		if (Distributor.calculate) {
			GUIManager.getInstance().startErrorFrame("Cannot add course while calculating!",
					"Please wait until the actual running calculation is finished.");
			return;
		}

		LOGGER.config("Starting Add Student Window");

		Stage primaryStage = new Stage();

		Image i;

		if (new File("./resources/assets/textures/logo/KuFA.png").exists())
			i = new Image(new File("./resources/assets/textures/logo/KuFA.png").toURI().toString());
		else
			i = new Image("/assets/textures/logo/KuFA.png");

		Parent root = null;
		try {
			root = FXMLLoader.load(getClass().getResource("/de/juhu/guiFX/AddCourse.fxml"));
		} catch (IOException e) {
			return;
		}
		Scene s = new Scene(root);

		if (mb1.isSelected()) {
			s.getStylesheets().add("/assets/styles/dark_theme.css");
		}

		primaryStage.setMinWidth(200);
		primaryStage.setMinHeight(158);
		primaryStage.setTitle("Add Course");
		primaryStage.setScene(s);
		primaryStage.initModality(Modality.WINDOW_MODAL);
		primaryStage.initOwner(GUILoader.getPrimaryStage());

		primaryStage.getIcons().add(i);

		primaryStage.show();
	}

	public void onClearCalculations(ActionEvent event) {
		if (Distributor.calculate) {
			GUIManager.getInstance().startErrorFrame("Cannot delete calculation while calculating!",
					"Please wait until the actual running calculation is finished.");
			return;
		}
		Distributor.getInstance().calculated.clear();

		boolean hold = Config.clear;

		Distributor.getInstance().reset();

		GUIManager.getInstance().teachers.setDisable(true);
		GUIManager.getInstance().students.setDisable(true);
		GUIManager.getInstance().statistics.setDisable(true);
	}

	public void onThemeChange(ActionEvent event) {
		if (last == null)
			last = mb1;
		if (lastSwitch == null)
			lastSwitch = mb1;
		last.setSelected(false);
		if (mb0.isSelected()) {

			GUILoader.scene.getStylesheets().clear();
			// GUILoader.scene.getStylesheets().add(StyleSheet.DEFAULT_STYLE);
			last = mb0;
		} else if (mb1.isSelected()) {

			GUILoader.scene.getStylesheets().add("/assets/styles/dark_theme.css");
			last = mb1;
		} else {
			if (lastSwitch.equals(mb0)) {

				GUILoader.scene.getStylesheets().clear();
				// GUILoader.scene.getStylesheets().add(StyleSheet.DEFAULT_STYLE);
				last = mb0;
				mb1.setSelected(true);
			} else if (lastSwitch.equals(mb1)) {

				GUILoader.scene.getStylesheets().add("/assets/styles/dark_theme.css");
				last = mb1;
				mb0.setSelected(true);
			}

		}
	}

	public void moveBadStudentToFirst(MouseEvent event) {
		if (Distributor.calculate) {
			GUIManager.getInstance().startErrorFrame("Cannot move student while calculating!",
					"Please wait until the actual running calculation is finished.");
			return;
		}

		Student s = this.bStudents.getSelectionModel().getSelectedItem();

		if (s == null)
			return;

		if (s.getCourses().length <= 0)
			return;

		Distributor.calculated.peek().getAllStudents()
				.get(Distributor.getInstance().calculated.peek().getAllStudents().indexOf(s))
				.setActiveCourse(Distributor.calculated.peek().getAllCourses()
						.get(Distributor.calculated.peek().getAllCourses().indexOf(s.getCourses()[0])));

		Distributor.calculated.peek().getInformation().getBStudents().remove(s);
		Distributor.calculated.peek().getInformation().update();
		Platform.runLater(GUIManager.getInstance().outputSView);
		Platform.runLater(GUIManager.getInstance().outputCView);
		Platform.runLater(GUIManager.getInstance().outputIView);
	}

	public void moveUnallocatedStudentToFirst(MouseEvent event) {
		if (Distributor.calculate) {
			GUIManager.getInstance().startErrorFrame("Cannot move student while calculating!",
					"Please wait until the actual running calculation is finished.");
			return;
		}

		Student s = this.unallocatedStudents.getSelectionModel().getSelectedItem();

		if (s == null)
			return;

		if (s.getCourses().length > 0)
			s.setActiveCourse(s.getCourses()[0]);

		Distributor.calculated.peek().getInformation().getunallocatedStudents().remove(s);
		Distributor.calculated.peek().getInformation().update();
		Platform.runLater(GUIManager.getInstance().outputSView);
		Platform.runLater(GUIManager.getInstance().outputCView);
		Platform.runLater(GUIManager.getInstance().outputIView);
	}

	public void onDeleteCourse(ActionEvent event) {
		if (Distributor.calculate) {
			GUIManager.getInstance().startErrorFrame("Cannot modify course data while calculating!",
					"Please wait until the actual running calculation is finished.");
			return;
		}

		Course course = this.tvc.getSelectionModel().getSelectedItem();

		if (course == null || (course.getSubject() == null || course.getTeacher() == null)
				|| (course.getSubject().isEmpty() && course.getTeacher().isEmpty()))
			return;

		Distributor.getInstance().removeCourse(course);
		GUIManager.getInstance().inputView.fill();
		GUIManager.getInstance().cView.fill();
	}

	public void onEditCourse(ActionEvent event) {
		if (Distributor.calculate) {
			GUIManager.getInstance().startErrorFrame("Cannot modify course data while calculating!",
					"Please wait until the actual running calculation is finished.");
			return;
		}

		Course course = this.tvc.getSelectionModel().getSelectedItem();

		if (course == null || (course.getSubject() == null || course.getTeacher() == null)
				|| (course.getSubject().isEmpty() && course.getTeacher().isEmpty()))
			return;

		LOGGER.config("Starting Add Student Window");

		AddCourseManager.s = course.getSubject();
		AddCourseManager.t = course.getTeacher();
		AddCourseManager.mS = course.getMaxStudentCount();

		Stage primaryStage = new Stage();

		Image i;

		if (new File("./resources/assets/textures/logo/KuFA.png").exists())
			i = new Image(new File("./resources/assets/textures/logo/KuFA.png").toURI().toString());
		else
			i = new Image("/assets/textures/logo/KuFA.png");

		Parent root = null;
		try {
			root = FXMLLoader.load(getClass().getResource("/de/juhu/guiFX/AddCourse.fxml"));
		} catch (IOException e) {
			return;
		}
		Scene s = new Scene(root);

		if (mb1.isSelected()) {
			s.getStylesheets().add("/assets/styles/dark_theme.css");
		}

		primaryStage.setMinWidth(200);
		primaryStage.setMinHeight(158);
		primaryStage.setTitle("Add Course");
		primaryStage.setScene(s);
		primaryStage.initModality(Modality.WINDOW_MODAL);
		primaryStage.initOwner(GUILoader.getPrimaryStage());

		primaryStage.getIcons().add(i);

		primaryStage.show();
	}

	public void onCourseChangeRequest(MouseEvent event) {
		if (Distributor.calculate) {
			GUIManager.getInstance().startErrorFrame("Cannot modify course data while calculating!",
					"Please wait until the actual running calculation is finished.");
			return;
		}

		if (event.getButton().equals(MouseButton.MIDDLE)) {
			Course course = this.tvc.getSelectionModel().getSelectedItem();

			if (course == null || (course.getSubject() == null || course.getTeacher() == null)
					|| (course.getSubject().isEmpty() && course.getTeacher().isEmpty()))
				return;

			Distributor.getInstance().removeCourse(course);
			GUIManager.getInstance().inputView.fill();
			GUIManager.getInstance().cView.fill();
		} else if (event.getButton().equals(MouseButton.SECONDARY)) {

			Course course = this.tvc.getSelectionModel().getSelectedItem();

			if (course == null || (course.getSubject() == null || course.getTeacher() == null)
					|| (course.getSubject().isEmpty() && course.getTeacher().isEmpty()))
				return;

			LOGGER.config("Starting Add Student Window");

			AddCourseManager.s = course.getSubject();
			AddCourseManager.t = course.getTeacher();
			AddCourseManager.mS = course.getMaxStudentCount();

			Stage primaryStage = new Stage();

			Image i;

			if (new File("./resources/assets/textures/logo/KuFA.png").exists())
				i = new Image(new File("./resources/assets/textures/logo/KuFA.png").toURI().toString());
			else
				i = new Image("/assets/textures/logo/KuFA.png");

			Parent root = null;
			try {
				root = FXMLLoader.load(getClass().getResource("/de/juhu/guiFX/AddCourse.fxml"));
			} catch (IOException e) {
				return;
			}
			Scene s = new Scene(root);

			if (mb1.isSelected()) {
				s.getStylesheets().add("/assets/styles/dark_theme.css");
			}

			primaryStage.setMinWidth(200);
			primaryStage.setMinHeight(158);
			primaryStage.setTitle("Add Course");
			primaryStage.setScene(s);
			primaryStage.initModality(Modality.WINDOW_MODAL);
			primaryStage.initOwner(GUILoader.getPrimaryStage());

			primaryStage.getIcons().add(i);

			primaryStage.show();
		}
	}

	public void onDeleteStudent(ActionEvent event) {
		if (Distributor.calculate) {
			GUIManager.getInstance().startErrorFrame("Cannot modify student data while calculating!",
					"Please wait until the actual running calculation is finished.");
			return;
		}

		Student student = this.tv0.getSelectionModel().getSelectedItem();

		if (student == null || (student.getName() == null) || student.getPrename() == null
				|| (student.getName().isEmpty() && student.getPrename().isEmpty()))
			return;

		Distributor.getInstance().removeStudent(student);
		GUIManager.getInstance().inputView.fill();
		GUIManager.getInstance().cView.fill();
	}

	public void onSwitchCourse(ActionEvent event) {
		if (Distributor.calculate) {
			GUIManager.getInstance().startErrorFrame("Cannot modify student data while calculating!",
					"Please wait until the actual running calculation is finished.");
			return;
		}

		Student student;
		if (this.statistics.isSelected())
			student = this.bStudents.getSelectionModel().getSelectedItem();
		else
			student = this.tv1.getSelectionModel().getSelectedItem();

		if (student == null || (student.getName() == null) || student.getPrename() == null
				|| (student.getName().isEmpty() && student.getPrename().isEmpty()))
			return;

		SwitchCourseManager.student = student;

		LOGGER.config("Starting Switch Course Window");

		Stage primaryStage = new Stage();

		Image i;

		if (new File("./resources/assets/textures/logo/KuFA.png").exists())
			i = new Image(new File("./resources/assets/textures/logo/KuFA.png").toURI().toString());
		else
			i = new Image("/assets/textures/logo/KuFA.png");

		Parent root = null;
		try {
			root = FXMLLoader.load(getClass().getResource("/de/juhu/guiFX/SwitchCourse.fxml"));
		} catch (IOException e) {
			return;
		}
		Scene s = new Scene(root);

		if (mb1.isSelected()) {
			s.getStylesheets().add("/assets/styles/dark_theme.css");
		}

		primaryStage.setMinWidth(200);
		primaryStage.setMinHeight(158);
		primaryStage.setTitle("Add Student");
		primaryStage.setScene(s);
		primaryStage.initModality(Modality.WINDOW_MODAL);
		primaryStage.initOwner(GUILoader.getPrimaryStage());

		primaryStage.getIcons().add(i);

		primaryStage.show();
	}

	public void onSwitchCourseUnallocatedStudent(ActionEvent event) {
		if (Distributor.calculate) {
			GUIManager.getInstance().startErrorFrame("Cannot modify student data while calculating!",
					"Please wait until the actual running calculation is finished.");
			return;
		}

		Student student = this.unallocatedStudents.getSelectionModel().getSelectedItem();

		if (student == null || (student.getName() == null) || student.getPrename() == null
				|| (student.getName().isEmpty() && student.getPrename().isEmpty()))
			return;

		SwitchCourseManager.student = student;

		LOGGER.config("Starting Switch Course Window");

		Stage primaryStage = new Stage();

		Image i;

		if (new File("./resources/assets/textures/logo/KuFA.png").exists())
			i = new Image(new File("./resources/assets/textures/logo/KuFA.png").toURI().toString());
		else
			i = new Image("/assets/textures/logo/KuFA.png");

		Parent root = null;
		try {
			root = FXMLLoader.load(getClass().getResource("/de/juhu/guiFX/SwitchCourse.fxml"));
		} catch (IOException e) {
			return;
		}
		Scene s = new Scene(root);

		if (mb1.isSelected()) {
			s.getStylesheets().add("/assets/styles/dark_theme.css");
		}

		primaryStage.setMinWidth(200);
		primaryStage.setMinHeight(158);
		primaryStage.setTitle("Add Student");
		primaryStage.setScene(s);
		primaryStage.initModality(Modality.WINDOW_MODAL);
		primaryStage.initOwner(GUILoader.getPrimaryStage());

		primaryStage.getIcons().add(i);

		primaryStage.show();
	}

	public void onEditStudent(ActionEvent event) {
		if (Distributor.calculate) {
			GUIManager.getInstance().startErrorFrame("Cannot modify student data while calculating!",
					"Please wait until the actual running calculation is finished.");
			return;
		}

		Student student = this.tv0.getSelectionModel().getSelectedItem();

		if (student == null || (student.getName() == null) || student.getPrename() == null
				|| (student.getName().isEmpty() && student.getPrename().isEmpty()))
			return;

		AddStudentManager.studentID = student.getID();

		LOGGER.config("Starting Add Student Window");

		Stage primaryStage = new Stage();

		Image i;

		if (new File("./resources/assets/textures/logo/KuFA.png").exists())
			i = new Image(new File("./resources/assets/textures/logo/KuFA.png").toURI().toString());
		else
			i = new Image("/assets/textures/logo/KuFA.png");

		Parent root = null;
		try {
			root = FXMLLoader.load(getClass().getResource("/de/juhu/guiFX/AddStudent.fxml"));
		} catch (IOException e) {
			return;
		}
		Scene s = new Scene(root);

		if (mb1.isSelected()) {
			s.getStylesheets().add("/assets/styles/dark_theme.css");
		}

		primaryStage.setMinWidth(200);
		primaryStage.setMinHeight(158);
		primaryStage.setTitle("Add Student");
		primaryStage.setScene(s);
		primaryStage.initModality(Modality.WINDOW_MODAL);
		primaryStage.initOwner(GUILoader.getPrimaryStage());

		primaryStage.getIcons().add(i);

		AddStudentManager.stage = primaryStage;

		primaryStage.show();
	}

	public void onStudentChangeRequest(MouseEvent event) {
		if (Distributor.calculate) {
			GUIManager.getInstance().startErrorFrame("Cannot modify student data while calculating!",
					"Please wait until the actual running calculation is finished.");
			return;
		}

		Student student = this.tv0.getSelectionModel().getSelectedItem();

		if (student == null || (student.getName() == null) || student.getPrename() == null
				|| (student.getName().isEmpty() && student.getPrename().isEmpty()))
			return;

		if (event.getButton().equals(MouseButton.MIDDLE)) {
			Distributor.getInstance().removeStudent(student);
			GUIManager.getInstance().inputView.fill();
			GUIManager.getInstance().cView.fill();
		} else if (event.getButton().equals(MouseButton.SECONDARY)) {

			LOGGER.config("Starting Add Student Window");

			Stage primaryStage = new Stage();

			Image i;

			if (new File("./resources/assets/textures/logo/KuFA.png").exists())
				i = new Image(new File("./resources/assets/textures/logo/KuFA.png").toURI().toString());
			else
				i = new Image("/assets/textures/logo/KuFA.png");

			Parent root = null;
			try {
				root = FXMLLoader.load(getClass().getResource("/de/juhu/guiFX/AddStudent.fxml"));
			} catch (IOException e) {
				return;
			}
			Scene s = new Scene(root);

			if (mb1.isSelected()) {
				s.getStylesheets().add("/assets/styles/dark_theme.css");
			}

			primaryStage.setMinWidth(200);
			primaryStage.setMinHeight(158);
			primaryStage.setTitle("Add Student");
			primaryStage.setScene(s);
			primaryStage.initModality(Modality.WINDOW_MODAL);
			primaryStage.initOwner(GUILoader.getPrimaryStage());

			primaryStage.getIcons().add(i);

			AddStudentManager.stage = primaryStage;
			AddStudentManager.studentID = student.getID();

			primaryStage.show();
		}
	}

	public void onDragDroppedInput(DragEvent event) {
		if (Distributor.calculate) {
			GUIManager.getInstance().startErrorFrame("Cannot import data while calculating!",
					"Please wait until the actual running calculation is finished.");
			return;
		}

		Dragboard d = event.getDragboard();
		if (d.hasFiles()) {
			for (File f : d.getFiles()) {
				if (Util.endsWith(f.getPath(), ".csv", ".xlsx", ".xls")) {
					new Distributor(f.getPath());
					this.inputView.fill();
					this.cView.fill();
					t1.setText(f.getPath());
				}
			}
		}

	}

	public void addStudent(ActionEvent event) {
		if (Distributor.calculate) {
			GUIManager.getInstance().startErrorFrame("Cannot add student while calculating!",
					"Please wait until the actual running calculation is finished.");
			return;
		}

		LOGGER.config("Starting Add Student Window");

		Stage primaryStage = new Stage();

		Image i;

		if (new File("./resources/assets/textures/logo/KuFA.png").exists())
			i = new Image(new File("./resources/assets/textures/logo/KuFA.png").toURI().toString());
		else
			i = new Image("/assets/textures/logo/KuFA.png");

		Parent root = null;
		try {
			root = FXMLLoader.load(getClass().getResource("/de/juhu/guiFX/AddStudent.fxml"));
		} catch (IOException e) {
			return;
		}
		Scene s = new Scene(root);

		if (mb1.isSelected()) {
			s.getStylesheets().add("/assets/styles/dark_theme.css");
		}

		primaryStage.setMinWidth(200);
		primaryStage.setMinHeight(158);
		primaryStage.setTitle("Add Student");
		primaryStage.setScene(s);
		primaryStage.initModality(Modality.WINDOW_MODAL);
		primaryStage.initOwner(GUILoader.getPrimaryStage());

		primaryStage.getIcons().add(i);

		AddStudentManager.stage = primaryStage;
		AddStudentManager.studentID = -1;

		primaryStage.show();
	}

	public void onFileTypeChanged(ActionEvent event) {
		Config.outputFileType = cb2.getValue();
	}

	public void searchActionInput(ActionEvent event) {
		if (Distributor.calculate) {
			GUIManager.getInstance().startErrorFrame("Cannot import data while calculating!",
					"Please wait until the actual running calculation is finished.");
			return;
		}

		FileChooser fc = new FileChooser();
		fc.getExtensionFilters().addAll(new ExtensionFilter("Grid Data", "*.csv", "*.xls", "*.xlsx"));
		fc.setTitle("Choose File");

		File toAdd = new File(t1.getText());

		if (!Util.isBlank(t1.getText()) && new File(t1.getText()).exists()
				&& new File(new File(t1.getText()).getParent()).exists())
			fc.setInitialDirectory(new File(new File(t1.getText()).getParent()));

		File selected = fc.showOpenDialog(null);

		if (selected.exists() && Util.endsWith(selected.getPath(), ".csv", ".xlsx", ".xls")) {
			Config.inputFile = selected.getPath();
			new Distributor(selected.getPath());
			this.inputView.fill();
			this.cView.fill();
			t1.setText(selected.getPath());
		}
	}

	public void onAbout(ActionEvent event) {
		LOGGER.config("Starting About Window");

		Stage primaryStage = new Stage();

		Image i;

		if (new File("./resources/assets/textures/logo/KuFA.png").exists())
			i = new Image(new File("./resources/assets/textures/logo/KuFA.png").toURI().toString());
		else
			i = new Image("/assets/textures/logo/KuFA.png");

		Parent root = null;
		try {
			root = FXMLLoader.load(getClass().getResource("/de/juhu/guiFX/About.fxml"));
		} catch (IOException e) {
			return;
		}
		Scene s = new Scene(root);

		if (mb1.isSelected()) {
			s.getStylesheets().add("/assets/styles/dark_theme.css");
		}

		primaryStage.setMinWidth(200);
		primaryStage.setMinHeight(158);
		primaryStage.setTitle("About");
		primaryStage.setScene(s);

		primaryStage.getIcons().add(i);

		primaryStage.show();
	}

	public void clearConsole(ActionEvent event) {
		LOGGING_HANDLER.clear();
	}

	public void printFormatChanged(ActionEvent event) {
		Config.printFormat = cb0.getValue().toString();
		LOGGING_HANDLER.updateLog();
	}

	public void levelChanges(ActionEvent event) {
		Config.maxPrintLevel = cb1.getValue();
		LOGGING_HANDLER.updateLog();
	}

	@FXML
	public RadioButton cf1, cf2;

	public void searchActionOutput(ActionEvent event) {
		if (cf1.isSelected()) {
			DirectoryChooser fc = new DirectoryChooser();
			fc.setTitle("Choose Directory");
			if (!Util.isBlank(t2.getText()) && new File(t1.getText()).exists()
					&& new File(new File(t2.getText()).getParent()).exists())
				fc.setInitialDirectory(new File(t2.getText()));

			File selected = fc.showDialog(null);

			t2.setText(selected.getParent() + "\\" + selected.getName());
			cb2.setValue("FOLDER");
			Config.outputFile = selected.getParent() + "\\" + selected.getName();
			Config.outputFileType = "FOLDER";
		}

		if (cf2.isSelected()) {
			FileChooser fc = new FileChooser();
			fc.getExtensionFilters().addAll(new ExtensionFilter("Grid Data", "*.csv", "*.xls", "*.xlsx"));
			fc.setTitle("Choose File");

			File toAdd = new File(t2.getText());

			if (!Util.isBlank(t2.getText()) && new File(t1.getText()).exists()
					&& new File(new File(t2.getText()).getParent()).exists())
				fc.setInitialDirectory(new File(new File(t2.getText()).getParent()));

			File selected = fc.showSaveDialog(null);

			if (selected.getParentFile().exists() && Util.endsWith(selected.getPath(), ".csv", ".xlsx", ".xls")) {
				t2.setText(selected.getParent());

				LOGGER.config(selected.getName());
				String[] strings = selected.getName().split("\\.");
				t2.setText(selected.getParent() + "\\" + strings[0]);
				cb2.setValue("." + strings[1]);
			}
		}
	}

	public void onDragDroppedOutput(DragEvent event) {
		Dragboard d = event.getDragboard();
		if (d.hasFiles()) {
			for (File f : d.getFiles()) {
				if (f.isDirectory()) {
					t2.setText(f.getParent() + "\\" + f.getName());
					cb2.setValue("FOLDER");
					Config.outputFile = f.getParent() + "\\" + f.getName();
					Config.outputFileType = "FOLDER";
				} else {
					if (f.exists() && Util.endsWith(f.getPath(), ".csv", ".xlsx", ".xls")) {
						// t2.setText(f.getParent());

						LOGGER.config(f.getName());
						String[] strings = f.getName().split("\\.");

						t2.setText(f.getParent() + "\\" + strings[0]);
						cb2.setValue("." + strings[1]);
					}
				}
			}
		}

	}

	public void runAction(ActionEvent event) {
		LOGGER.info("Start Distributor");

		p0.setVisible(true);

		GUIManager.getInstance().r1.setDisable(true);
		GUIManager.getInstance().r2.setDisable(true);
		GUIManager.getInstance().r3.setDisable(true);

		Thread t = new Thread(Distributor.getInstance(), "Calculator");
		t.start();
	}

	public void startErrorFrame(String headline, String message) {

		LOGGER.config("Starting Error Window");

		ErrorGuiController.headline = headline;
		ErrorGuiController.information = message;
		Stage primaryStage = new Stage();

		Image i;

		if (new File("./resources/assets/textures/logo/KuFA.png").exists())
			i = new Image(new File("./resources/assets/textures/logo/KuFA.png").toURI().toString());
		else
			i = new Image("/assets/textures/logo/KuFA.png");
		Parent root = null;
		try {
			root = FXMLLoader.load(getClass().getResource("/de/juhu/guiFX/Error.fxml"));
		} catch (IOException e) {
			return;
		}
		Scene s = new Scene(root);
		if (mb1.isSelected()) {
			s.getStylesheets().add("/assets/styles/dark_theme.css");
		}

		primaryStage.setMinWidth(200);
		primaryStage.setMinHeight(158);
		primaryStage.setTitle("ERROR");
		primaryStage.setScene(s);
		primaryStage.initModality(Modality.WINDOW_MODAL);
		primaryStage.initOwner(GUILoader.getPrimaryStage());
		primaryStage.initStyle(StageStyle.UNDECORATED);

		primaryStage.getIcons().add(i);

		primaryStage.show();
		LOGGER.info("Error Window Started");

	}

	public void onClearDistributor(ActionEvent event) {
		Distributor.getInstance().clear();

		GUIManager.getInstance().inputView.fill();
		GUIManager.getInstance().cView.fill();
	}

	public void saveAction(ActionEvent event) {
		if (Distributor.calculate) {
			GUIManager.getInstance().startErrorFrame("Cannot save calculation while calculating!",
					"Please wait until the actual running calculation is finished.");
			return;
		}

		LOGGER.info("Start Saving files");
		LOGGER.info("Try to save to " + Config.outputFile + Config.outputFileType);

		Save save = Distributor.getInstance().calculated.poll();
		if (save == null) {
			this.startErrorFrame("No calculation found!",
					"There are no data in the distributor. \n Please ensure, to run the calculator and assign the given data, before exporting them!");
			return;
		}

		Distributor.calculate = true;

		p0.setVisible(true);

		GUIManager.getInstance().r1.setDisable(true);
		GUIManager.getInstance().r2.setDisable(true);
		GUIManager.getInstance().r3.setDisable(true);

		de.juhu.distributor.ProgressIndicator.getInstance().setfProgressMax(Config.runs).setfProgressValue(-1);

		Distributor.getInstance().calculated.add(save);
		String s;
		if (Util.isBlank((s = Config.outputFile)) || s.endsWith("/") || s.endsWith("\\"))
			return;

		boolean xls = false, xlsx = false, csv = false;

		switch (Config.outputFileType) {
		case ".xls":
			xls = true;
			break;
		case ".xlsx":
			xlsx = true;
			break;
		case ".csv":
			csv = true;
			break;
		case "FOLDER":
			ExcelExporter.writeXLS(s + "/Excel_OLD", save.writeInformation());
			ExcelExporter.writeXLSX(s + "/KuFA-Zuweiser Ergebnisse", save.writeInformation());
			CSVExporter.writeCSV(s + "/course", save.writeCourseInformation());
			CSVExporter.writeCSV(s + "/student", save.writeStudentInformation());
			LogWriter.writeLog(s + "/logging");
			break;
		default:
			xlsx = true;
			break;
		}

		if (xls) {
			ExcelExporter.writeXLS(s, save.writeInformation());
		}
		if (xlsx) {
			ExcelExporter.writeXLSX(s, save.writeInformation());
		}
		if (csv) {
			CSVExporter.writeCSV(s + "course", save.writeCourseInformation());
			CSVExporter.writeCSV(s + "student", save.writeStudentInformation());
		}
		LOGGER.info("Finished Saving files");

		Distributor.calculate = false;

		de.juhu.distributor.ProgressIndicator.getInstance().setfProgressMax(Config.runs).setfProgressValue(0);

		p0.setVisible(false);

		GUIManager.getInstance().r1.setDisable(false);
		GUIManager.getInstance().r2.setDisable(false);
		GUIManager.getInstance().r3.setDisable(false);
	}

	public void runAndSaveAction(ActionEvent event) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				runAction(event);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				while (Distributor.calculate) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				saveAction(event);
			}
		}, "R and S Progress").start();
	}

	public void onSaveLog(ActionEvent event) {
		DirectoryChooser fc = new DirectoryChooser();
		fc.setTitle("Choose Directory");
		if (!Util.isBlank(t2.getText()) && new File(new File(t2.getText()).getParent()).exists())
			fc.setInitialDirectory(new File(t2.getText()));

		File selected = fc.showDialog(null);

		LogWriter.writeLog(selected.getPath() + "/logging");
	}

	public void getInformation(ActionEvent event) {
		if (t1.isVisible())
			t1.setVisible(false);
		else
			t1.setVisible(true);
	}

	public void onSaveConfig(ActionEvent event) {
		LOGGER.info("Start saving config");

		if (!Files.exists(FileSystems.getDefault().getPath(System.getenv("localappdata") + "/CaRP/"),
				LinkOption.NOFOLLOW_LINKS))
			new File(System.getenv("localappdata") + "/CaRP/").mkdir();

		ConfigManager.getInstance().save(new File(System.getenv("localappdata") + "/CaRP/config.cfg"));

		LOGGER.info("Finished saving config");
	}

	public void close(ActionEvent event) {
		this.onSaveConfig(null);

		LOGGER.info("Close " + References.PROJECT_NAME);
		System.exit(0);
	}

	public void onShowInExcel(ActionEvent event) {

		if (Distributor.calculate) {
			GUIManager.getInstance().startErrorFrame("Cannot show data while calculating!",
					"Please wait until the actual running calculation is finished.");
			return;
		}

		Save save = Distributor.getInstance().calculated.peek();

		if (save == null) {
			GUIManager.getInstance().startErrorFrame("Cannot show data without calculating before!",
					"Please first run a calculation.");
			return;
		}

		ExcelExporter.writeXLSX("test", save.writeInformation());

		try {
			Desktop.getDesktop().browse(new File("test.xlsx").toURI());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void updateIV() {
		while (Config.maxChooses > this.atci.size() / 3) {
			int number = (this.atci.size() / 3 + 1);

			TableColumn<Student, String> k = new TableColumn<>("Course " + number), s = new TableColumn<>("Subject"),
					t = new TableColumn<>("Teacher");
			k.getColumns().addAll(s, t);
			this.atci.add(k);
			this.atci.add(s);
			this.atci.add(t);

			s.setCellValueFactory(e -> {
				if (e.getValue().getCourses().length > number - 1)
					return new SimpleStringProperty(e.getValue().getCourses()[number - 1].getSubject() == null ? "-"
							: e.getValue().getCourses()[number - 1].getSubject());
				else
					return new SimpleStringProperty("-");
			});

			t.setCellValueFactory(e -> {
				if (e.getValue().getCourses().length > number - 1)
					return new SimpleStringProperty(e.getValue().getCourses()[number - 1].getTeacher() == null ? "-"
							: e.getValue().getCourses()[number - 1].getTeacher());
				else
					return new SimpleStringProperty("-");
			});

			this.tv0.getColumns().add(k);
		}
		while (Config.maxChooses < this.atci.size() / 3 && Config.maxChooses != -1 && Config.maxChooses != 0) {
			this.tv0.getColumns().remove(tv0.getColumns().size() - 1);
			this.atci.remove(this.atci.size() - 3);
			this.atci.remove(this.atci.size() - 2);
			this.atci.remove(this.atci.size() - 1);
		}
		if (Config.maxChooses == -1) {
			while (3 > this.atci.size() / 3) {
				int number = (this.atci.size() / 3 + 1);

				TableColumn<Student, String> k = new TableColumn<>("Course " + number),
						s = new TableColumn<>("Subject"), t = new TableColumn<>("Teacher");
				k.getColumns().addAll(s, t);
				this.atci.add(k);
				this.atci.add(s);
				this.atci.add(t);

				s.setCellValueFactory(e -> {
					if (e.getValue().getCourses().length > number - 1)
						return new SimpleStringProperty(e.getValue().getCourses()[number - 1].getSubject() == null ? "-"
								: e.getValue().getCourses()[number - 1].getSubject());
					else
						return new SimpleStringProperty("-");
				});

				t.setCellValueFactory(e -> {
					if (e.getValue().getCourses().length > number - 1)
						return new SimpleStringProperty(e.getValue().getCourses()[number - 1].getTeacher() == null ? "-"
								: e.getValue().getCourses()[number - 1].getTeacher());
					else
						return new SimpleStringProperty("-");
				});

				this.tv0.getColumns().add(k);
			}
		}
	}

	public void onConfigChanged() {
		this.updateIV();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		ProgressIndicator pi = new ProgressIndicator();

		this.instance = this;

		cb0.setItems(FXCollections.observableArrayList(PrintFormat.values()));

		cb1.setItems(FXCollections.observableArrayList(Level.ALL, Level.FINEST, Level.FINER, Level.FINE, Level.CONFIG,
				Level.INFO, Level.WARNING, Level.SEVERE, Level.OFF));
		cb2.setItems(FXCollections.observableArrayList(".csv", ".xlsx", ".xls", "FOLDER"));

		p0.progressProperty().bind(FullProgress.getInstance().progressProperty());
		p0.setVisible(false);

		// Config Stuff

		ConfigManager.getInstance().register(Config.class);

		if (!Files.exists(FileSystems.getDefault().getPath(System.getenv("localappdata") + "/CaRP/config.cfg"),
				LinkOption.NOFOLLOW_LINKS))
			ConfigManager.getInstance().loadDefault();
		else
			ConfigManager.getInstance().load(System.getenv("localappdata") + "/CaRP/config.cfg");

		for (Field f : Config.class.getFields()) {
			if (f.getAnnotation(ConfigElement.class) == null)
				continue;
			ConfigElement e = f.getAnnotation(ConfigElement.class);

			if (e.elementClass().equals(Boolean.class)) {
				CheckBox cb = new CheckBox(f.getName());
				cb.setTooltip(new Tooltip(e.description()));
				cb.addEventHandler(ActionEvent.ANY, r -> {
					this.onConfigChanged();
				});
				try {
					cb.setSelected(f.getBoolean(null));
				} catch (IllegalArgumentException e2) {
					e2.printStackTrace();
				} catch (IllegalAccessException e2) {
					e2.printStackTrace();
				}
				cb.addEventHandler(ActionEvent.ACTION, event -> {
					try {
						f.setBoolean(null, cb.isSelected());
					} catch (IllegalArgumentException e1) {
						e1.printStackTrace();
					} catch (IllegalAccessException e1) {
						e1.printStackTrace();
					}
				});
				this.config.getChildren().addAll(new Label(e.name() + ":"), cb);
			} else if (e.elementClass().equals(Integer.class)) {
				Spinner cb = new Spinner();
				cb.setTooltip(new Tooltip(e.description()));
				cb.setEditable(true);
				try {
					if (f.getName().equals("runs") || f.getName().equals("newCalculating")
							|| f.getName().equals("improvingOfCalculation"))
						cb.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE,
								f.getInt(null)));
					else
						cb.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(-1, Integer.MAX_VALUE,
								f.getInt(null)));
					cb.getValueFactory().valueProperty().addListener((o, oldValue, newValue) -> {
						try {
							f.set(null, newValue);
						} catch (IllegalArgumentException e1) {
							e1.printStackTrace();
						} catch (IllegalAccessException e1) {
							e1.printStackTrace();
						}
						this.onConfigChanged();
					});
				} catch (IllegalArgumentException | IllegalAccessException e3) {
					e3.printStackTrace();
				}
				this.config.getChildren().addAll(new Label(e.name() + ":"), cb);
			} else if (e.elementClass().equals(String.class)) {
				TextField cb = new TextField();
				cb.setTooltip(new Tooltip(e.description()));

				try {
					cb.setText((String) f.get(null));
				} catch (IllegalArgumentException e2) {
					e2.printStackTrace();
				} catch (IllegalAccessException e2) {
					e2.printStackTrace();
				}
				cb.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
					try {
						f.set(null, cb.getText());
						this.onConfigChanged();
					} catch (IllegalArgumentException e1) {
						e1.printStackTrace();
					} catch (IllegalAccessException e1) {

						e1.printStackTrace();
					}
				});

				this.config.getChildren().addAll(new Label(e.name() + ":"), cb);
			}

		}

		t2.setText(Config.outputFile);

		LOGGING_HANDLER.bindTextArea(ta1);

		// bp_preview.setCenter(new HTMLEditor());

//		PrintStream ps = new PrintStream(System.out) {
//
//			private StringBuilder buffer = new StringBuilder();
//
//			@Override
//			public void print(String s) {
//				if (s == null)
//					return;
//
//				buffer.append(s);
//
//				if (buffer.length() >= 1000) {
//					ta1.appendText(buffer.toString());
//					ta1.end();
//					buffer.setLength(0);
//				}
//
//			}
//		};
//		System.setOut(ps);
//		System.setErr(ps);
		this.startPicture();

		// INFO: Tabellen Initierung

		// Input Tabelle

		this.inputView = new InputView(this.tv0);
		this.cView = new CourseView(this.tvc);

		this.vtc.setCellValueFactory(s -> {
			return new SimpleStringProperty(s.getValue().getPrename());
		});

		this.ntc.setCellValueFactory(s -> {
			return new SimpleStringProperty(s.getValue().getName());
		});

		this.k1stc.setCellValueFactory(s -> {
			if (s.getValue().getCourses().length > 0)
				return new SimpleStringProperty(s.getValue().getCourses()[0].getSubject());
			return new SimpleStringProperty("-");
		});

		this.k1ttc.setCellValueFactory(s -> {
			if (s.getValue().getCourses().length > 0)
				return new SimpleStringProperty(s.getValue().getCourses()[0].getTeacher());
			return new SimpleStringProperty("-");
		});

		this.atci.add(k1tc);
		this.atci.add(k1stc);
		this.atci.add(k1ttc);

		this.subject.setCellValueFactory(c -> {
			return new SimpleStringProperty(c.getValue().getSubject());
		});

		this.teacher.setCellValueFactory(c -> {
			return new SimpleStringProperty(c.getValue().getTeacher());
		});

//		this.k1tc.setCellValueFactory(s -> {
//			if (s.getValue().getCourseNames().length < 1)
//				return new SimpleStringProperty("-");
//			return new SimpleStringProperty(s.getValue().getCourseNames()[0]);
//		});
//
//		this.k2tc.setCellValueFactory(s -> {
//			if (s.getValue().getCourseNames().length < 2)
//				return new SimpleStringProperty("-");
//			return new SimpleStringProperty(s.getValue().getCourseNames()[1]);
//		});
//
//		this.k3tc.setCellValueFactory(s -> {
//			if (s.getValue().getCourseNames().length < 3)
//				return new SimpleStringProperty("-");
//			return new SimpleStringProperty(s.getValue().getCourseNames()[2]);
//		});

		// INFO: Output Tabellen

		// Output Student View

		this.outputSView = new OutputStudentsView(this.tv1);

		this.cvtc.setCellValueFactory(s -> {
			return new SimpleStringProperty(s.getValue().getPrename());
		});

		this.cntc.setCellValueFactory(s -> {
			return new SimpleStringProperty(s.getValue().getName());
		});

		this.ckstc.setCellValueFactory(s -> {
			if (s.getValue().getActiveCourse() == null)
				return new SimpleStringProperty("No course found");

			return new SimpleStringProperty(s.getValue().getActiveCourse().getSubject());
		});

		this.ckttc.setCellValueFactory(s -> {
			if (s.getValue().getActiveCourse() == null)
				return new SimpleStringProperty("");

			return new SimpleStringProperty(s.getValue().getActiveCourse().getTeacher());
		});

		this.cptc.setCellValueFactory(s -> {
			return new SimpleStringProperty(Integer.toString(s.getValue().getPriority()));
		});

		// Output Course View

		this.outputCView = new OutputCourseView(this.tv2);

		this.oSubject.setCellValueFactory(s -> {
			return new SimpleStringProperty(s.getValue().getSubject());
		});

		this.oTeacher.setCellValueFactory(s -> {
			return new SimpleStringProperty(s.getValue().getTeacher());
		});

		// Output Information View

		this.outputIView = new OutputInformationView();

		this.bPrename.setCellValueFactory(s -> {
			return new SimpleStringProperty(s.getValue().getPrename());
		});

		this.bName.setCellValueFactory(s -> {
			return new SimpleStringProperty(s.getValue().getName());
		});

		this.bSubject.setCellValueFactory(s -> {
			if (s.getValue().getActiveCourse() != null)
				return new SimpleStringProperty(s.getValue().getActiveCourse().getSubject());
			return new SimpleStringProperty("-");
		});

		this.bTeacher.setCellValueFactory(s -> {
			if (s.getValue().getActiveCourse() != null)
				return new SimpleStringProperty(s.getValue().getActiveCourse().getTeacher());
			return new SimpleStringProperty("-");
		});

		this.bPriority.setCellValueFactory(s -> {
			return new SimpleStringProperty(Integer.toString(s.getValue().getPriority()));
		});

		this.rate.setCellValueFactory(s -> {
			return new SimpleStringProperty(s.getValue().getKey());
		});

		this.rateV.setCellValueFactory(s -> {
			return new SimpleStringProperty(s.getValue().getValue().toString());
		});

		this.priority.setCellValueFactory(s -> {
			return new SimpleStringProperty(s.getValue().getKey().toString());
		});

		this.swpriority.setCellValueFactory(s -> {
			return new SimpleStringProperty(s.getValue().getValue().toString());
		});

		this.unallocatedPrename.setCellValueFactory(s -> {
			return new SimpleStringProperty(s.getValue().getPrename());
		});

		this.unallocatedName.setCellValueFactory(s -> {
			return new SimpleStringProperty(s.getValue().getName());
		});

		// Update Config

		this.onConfigChanged();

		t1.setText(Config.inputFile);

		File file = new File(Config.inputFile);
		if (file.exists()) {
			new Distributor(Config.inputFile);
			this.inputView.fill();
			this.cView.fill();
		}
	}

	@FXML
	public AnchorPane ap0;

	FadeTransition ft;
	TranslateTransition tt;
	ScaleTransition st;

	public void startPicture() {

		if (ft == null) {
			ft = new FadeTransition(Duration.millis(5000 / 2), i0);
			ft.setFromValue(0);
			ft.setToValue(1);
			ft.setOnFinished(e -> {
				tt.playFromStart();
				st.playFromStart();
			});
		}

		if (tt == null) {
			tt = new TranslateTransition(Duration.millis(10000 / 2), i0);
			tt.setFromX(0);
			tt.setFromY(0);
			tt.setByX(0);
			tt.setByY(0);
			tt.setToX(-180);
			tt.setToY(-65);
		}

		if (st == null) {
			st = new ScaleTransition(Duration.millis(10000 / 2), i0);

			st.setFromX(1);
			st.setFromY(1);
			st.setToX(0.3);
			st.setToY(0.3);
		}

		tt.stop();
		st.stop();
		ft.stop();

		i0.setScaleX(1);
		i0.setScaleY(1);

		ft.playFromStart();
	}

}
