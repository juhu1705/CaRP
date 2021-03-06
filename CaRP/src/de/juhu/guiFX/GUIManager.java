package de.juhu.guiFX;

import static de.juhu.util.References.LOGGER;
import static de.juhu.util.References.LOGGING_HANDLER;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.logging.Level;

import org.xml.sax.SAXException;

import de.juhu.config.ConfigManager;
import de.juhu.distributor.Course;
import de.juhu.distributor.Distributor;
import de.juhu.distributor.Save;
import de.juhu.distributor.Student;
import de.juhu.filemanager.CSVExporter;
import de.juhu.filemanager.ExcelExporter;
import de.juhu.filemanager.LogWriter;
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
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.CacheHint;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Duration;

/**
 * Diese Klasse verwaltet alle Aktionen des Haupt-GUIs.
 * 
 * @author Juhu1705
 * @category GUI
 * @version 2.1
 */
public class GUIManager implements Initializable {

	private static GUIManager instance;

	public static GUIManager getInstance() {
		return instance;
	}

	public static Save actual;

	@FXML
	public ImageView i0;

	@FXML
	public ProgressBar p0;

	@FXML
	public TextArea ta1;

	@FXML
	public TreeView<String> configurationTree;

	@FXML
	public Label counter, textPrevious, textNext, textActual;

	@FXML
	public TextField t1, t2;

	@FXML
	public Button r1, r2, r3, r4, r5, b1, b2, b3, b4, b5, b6, b7;

	@FXML
	public ComboBox<Level> cb1;

	@FXML
	public ComboBox<String> cb2;

	@FXML
	public ComboBox<PrintFormat> cb0;

	@FXML
	public TableView<Student> tv0, tv1, unallocatedStudents, bStudents;

	@FXML
	public TableView<Entry<String, String>> rates;

	@FXML
	public TableView<Entry<String, Integer>> priorities;

	@FXML
	public TableView<Course> tvc, tv2;

	@FXML
	public TableColumn<Student, String> vtc, ntc, k1tc, k1stc, k1ttc, cvtc, cntc, cptc, ckstc, ckttc, unallocatedName,
			unallocatedPrename, bName, bPrename, bSubject, bTeacher, bPriority;

	@FXML
	public TableColumn<Course, String> subject, teacher, oSubject, oTeacher, maxStudentCount;

	@FXML
	public TableColumn<Entry<String, String>, String> rate, rateV;

	@FXML
	public TableColumn<Entry<String, Integer>, String> priority, swpriority, percentualPriorities;

	@FXML
	public Tab students, teachers, statistics, tabStudents, tabCourses, tabInput, tabOutput;

	@FXML
	public TabPane masterTabPane;

	@FXML
	public SeparatorMenuItem seperator, seperator1;

	@FXML
	public Menu menuStudent, menuCourse, menuStudent1, menuCourse1;

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

	public HashMap<Theme, CheckMenuItem> checks = new HashMap<>();

	public Theme theme;

	private CheckMenuItem last, lastSwitch;

	public void onFullScreen(ActionEvent event) {
		GUILoader.getPrimaryStage().setFullScreen(!GUILoader.getPrimaryStage().isFullScreen());
	}

	public void onDeleteStudentFromActualSave(ActionEvent event) {
		if (actual == null)
			return;

		Student student = this.tv1.getSelectionModel().getSelectedItem();

		this.actual.removeStudent(student);

		GUIManager.actual.getInformation().update();
		Platform.runLater(GUIManager.getInstance().outputSView);
		Platform.runLater(GUIManager.getInstance().outputCView);
		Platform.runLater(GUIManager.getInstance().outputIView);
	}

	public void onRemoveUnusedCourses(ActionEvent event) {
		if (actual == null)
			return;

		for (Course c : actual.getAllCoursesAsArray())
			if (c.getStudents().isEmpty())
				this.actual.removeCourse(c);

		GUIManager.actual.getInformation().update();
		Platform.runLater(GUIManager.getInstance().outputSView);
		Platform.runLater(GUIManager.getInstance().outputCView);
		Platform.runLater(GUIManager.getInstance().outputIView);
	}

	public void onDeleteCourseFromActualSave(ActionEvent event) {
		if (actual == null)
			return;

		Course course = this.tv2.getSelectionModel().getSelectedItem();

		if (!course.getStudents().isEmpty()) {
			this.startErrorFrame(References.language.getString("add.course_fail.title"),
					References.language.getString("add.course_fail.description"));
			return;
		}

		this.actual.removeCourse(course);

		GUIManager.actual.getInformation().update();
		Platform.runLater(GUIManager.getInstance().outputSView);
		Platform.runLater(GUIManager.getInstance().outputCView);
		Platform.runLater(GUIManager.getInstance().outputIView);
	}

	public void onAddStudentToActualSave(ActionEvent event) {
		if (Distributor.calculate) {
			GUIManager.getInstance().startErrorFrame(References.language.getString("calculation.edit_data_fail.title"),
					References.language.getString("calculation.edit_data_fail.description"));
			return;
		}

		AddStudentToSaveManager.student = null;

		LOGGER.config("Starting Add Student Window");

		Util.openWindow("/assets/layouts/AddStudentToCalculation.fxml",
				References.language.getString("addstudent.text"), GUILoader.getPrimaryStage(), this.theme);
	}

	public void onEditCourseActualSave(ActionEvent event) {
		if (Distributor.calculate) {
			GUIManager.getInstance().startErrorFrame(References.language.getString("calculation.edit_data_fail.title"),
					References.language.getString("calculation.edit_data_fail.description"));
			return;
		}

		Course course = this.tv2.getSelectionModel().getSelectedItem();

		if (course == null || (course.getSubject() == null || course.getTeacher() == null)
				|| (course.getSubject().isEmpty() && course.getTeacher().isEmpty()))
			return;

		LOGGER.config("Starting Add Course Window");

		AddCourseToSaveManager.s = course.getSubject();
		AddCourseToSaveManager.t = course.getTeacher();
		AddCourseToSaveManager.mS = course.getMaxStudentCount();

		Util.openWindow("/assets/layouts/AddCourseToCalculation.fxml", References.language.getString("editing.text"),
				GUILoader.getPrimaryStage(), this.theme);
	}

	public void onAddCourseToActualSave(ActionEvent event) {
		if (Distributor.calculate) {
			GUIManager.getInstance().startErrorFrame(References.language.getString("calculation.edit_data_fail.title"),
					References.language.getString("calculation.edit_data_fail.description"));
			return;
		}

		AddCourseToSaveManager.s = null;
		AddCourseToSaveManager.t = null;
		AddCourseToSaveManager.mS = Config.normalStudentLimit;

		LOGGER.config("Starting Add Course Window");

		Util.openWindow("/assets/layouts/AddCourseToCalculation.fxml", References.language.getString("addcourse.text"),
				GUILoader.getPrimaryStage(), this.theme);
	}

	public void onNextSave(ActionEvent event) {
		GUIManager.actual = Distributor.calculated.next(actual);

		Platform.runLater(GUIManager.getInstance().outputSView);
		Platform.runLater(GUIManager.getInstance().outputCView);
		Platform.runLater(GUIManager.getInstance().outputIView);

		if (GUIManager.actual.equals(Distributor.calculated.next(actual))) {
			this.b4.setDisable(true);
			this.textNext.setText("");
		} else
			this.textNext.setText(References.language.getString("nextgoodness.text")
					+ Double.toString(Util.round(Distributor.calculated.next(actual).getInformation().getGuete(), 3)));
		this.b1.setDisable(false);
		this.textPrevious.setText(References.language.getString("previousgoodness.text")
				+ Double.toString(Util.round(Distributor.calculated.previous(actual).getInformation().getGuete(), 3)));

		this.counter.setText(References.language.getString("distribution.text") + ": "
				+ Integer.toString((Distributor.calculated.indexOf(actual) + 1)));
		this.textActual.setText(References.language.getString("calculationgoodness.text") + ": "
				+ Double.toString(Util.round(actual.getInformation().getGuete(), 3)));
		// INFO: Hi
	}

	public void onPreviousSave(ActionEvent event) {
		GUIManager.actual = Distributor.calculated.previous(actual);

		Platform.runLater(GUIManager.getInstance().outputSView);
		Platform.runLater(GUIManager.getInstance().outputCView);
		Platform.runLater(GUIManager.getInstance().outputIView);

		if (GUIManager.actual.equals(Distributor.calculated.previous(actual))) {
			this.b1.setDisable(true);
			this.textPrevious.setText("");
		} else
			this.textPrevious.setText(References.language.getString("previousgoodness.text") + Double
					.toString(Util.round(Distributor.calculated.previous(actual).getInformation().getGuete(), 3)));
		this.b4.setDisable(false);
		this.textNext.setText(References.language.getString("nextgoodness.text")
				+ Double.toString(Util.round(Distributor.calculated.next(actual).getInformation().getGuete(), 3)));

		this.counter.setText(References.language.getString("distribution.text") + ": "
				+ Integer.toString((Distributor.calculated.indexOf(actual) + 1)));
		this.textActual.setText(References.language.getString("calculationgoodness.text") + ": "
				+ Double.toString(Util.round(actual.getInformation().getGuete(), 3)));
	}

	public void addCourse(ActionEvent event) {

		if (Distributor.calculate) {
			GUIManager.getInstance().startErrorFrame(References.language.getString("calculation.edit_data_fail.title"),
					References.language.getString("calculation.edit_data_fail.description"));
			return;
		}

		LOGGER.config("Starting Add Student Window");

		AddCourseManager.mS = -2;
		AddCourseManager.s = null;
		AddCourseManager.t = null;

		Util.openWindow("/assets/layouts/AddCourse.fxml", References.language.getString("addcourse.text"),
				GUILoader.getPrimaryStage(), this.theme);
	}

	public void onClearCalculations(ActionEvent event) {
		if (Distributor.calculate) {
			GUIManager.getInstance().startErrorFrame(References.language.getString("calculation.edit_data_fail.title"),
					References.language.getString("calculation.edit_data_fail.description"));
			return;
		}
		Distributor.getInstance().calculated.clear();

		boolean hold = Config.clear;

		Distributor.getInstance().reset();
		this.rates.getItems().clear();
		GUIManager.getInstance().priorities.getItems().clear();
		GUIManager.getInstance().unallocatedStudents.getItems().clear();
		GUIManager.getInstance().bStudents.getItems().clear();
		this.tv1.getItems().clear();
		this.tv2.getItems().clear();

		GUIManager.getInstance().teachers.setDisable(true);
		GUIManager.getInstance().students.setDisable(true);
		GUIManager.getInstance().statistics.setDisable(true);
		GUIManager.getInstance().b1.setDisable(true);
		GUIManager.getInstance().b2.setDisable(true);
		GUIManager.getInstance().b3.setDisable(true);
		GUIManager.getInstance().b4.setDisable(true);
		GUIManager.getInstance().b5.setDisable(true);
		GUIManager.getInstance().b6.setDisable(true);
		GUIManager.getInstance().b7.setDisable(true);
	}

	@Deprecated
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
		} else if (mb0.isSelected()) {

			GUILoader.scene.getStylesheets().add("/assets/styles/dark_theme.css");
			last = mb1;
		} else {
			if (last.equals(mb0)) {

				GUILoader.scene.getStylesheets().clear();
				// GUILoader.scene.getStylesheets().add(StyleSheet.DEFAULT_STYLE);
				last = mb0;
				mb1.setSelected(true);
			} else if (last.equals(mb1)) {

				GUILoader.scene.getStylesheets().add("/assets/styles/dark_theme.css");
				last = mb1;
				mb0.setSelected(true);
			}

		}
	}

	public void onSetLightTheme(ActionEvent event) {
		this.onSetTheme(Theme.LIGHT);
	}

	public void onSetDarkTheme(ActionEvent event) {
		this.onSetTheme(Theme.DARK);
	}

	public void onSetTheme(Theme theme) {
		if (theme.getLocation().equalsIgnoreCase("remove"))
			GUILoader.scene.getStylesheets().clear();
		else
			GUILoader.scene.getStylesheets().add(theme.getLocation());

		this.theme = theme;

		this.checks.forEach((themes, checkbox) -> {
			checkbox.setSelected(false);
		});

		this.checks.get(theme).setSelected(true);
	}

	public void moveBadStudentToFirst(MouseEvent event) {
		if (Distributor.calculate) {
			GUIManager.getInstance().startErrorFrame(References.language.getString("calculation.edit_data_fail.title"),
					References.language.getString("calculation.edit_data_fail.description"));
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
			GUIManager.getInstance().startErrorFrame(References.language.getString("calculation.edit_data_fail.title"),
					References.language.getString("calculation.edit_data_fail.description"));
			return;
		}

		Student s = this.unallocatedStudents.getSelectionModel().getSelectedItem();

		if (s == null)
			return;

		if (s.getCourses().length > 0)
			s.setActiveCourse(s.getCourses()[0]);

		Distributor.calculated.peek().getInformation().getUStudents().remove(s);
		Distributor.calculated.peek().getInformation().update();
		Platform.runLater(GUIManager.getInstance().outputSView);
		Platform.runLater(GUIManager.getInstance().outputCView);
		Platform.runLater(GUIManager.getInstance().outputIView);
	}

	public void onDeleteCourse(ActionEvent event) {
		if (Distributor.calculate) {
			GUIManager.getInstance().startErrorFrame(References.language.getString("calculation.edit_data_fail.title"),
					References.language.getString("calculation.edit_data_fail.description"));
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
			GUIManager.getInstance().startErrorFrame(References.language.getString("calculation.edit_data_fail.title"),
					References.language.getString("calculation.edit_data_fail.description"));
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

		Util.openWindow("/assets/layouts/AddCourse.fxml", References.language.getString("editing.text"),
				GUILoader.getPrimaryStage(), this.theme);
	}

	public void onCourseChangeRequest(MouseEvent event) {
		if (Distributor.calculate) {
			GUIManager.getInstance().startErrorFrame(References.language.getString("calculation.edit_data_fail.title"),
					References.language.getString("calculation.edit_data_fail.description"));
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

			Util.openWindow("/assets/layouts/AddCourse.fxml", References.language.getString("editing.text"),
					GUILoader.getPrimaryStage(), this.theme);

		}
	}

	public void onDeleteStudent(ActionEvent event) {
		if (Distributor.calculate) {
			GUIManager.getInstance().startErrorFrame(References.language.getString("calculation.edit_data_fail.title"),
					References.language.getString("calculation.edit_data_fail.description"));
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
			GUIManager.getInstance().startErrorFrame(References.language.getString("calculation.edit_data_fail.title"),
					References.language.getString("calculation.edit_data_fail.description"));
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

		Util.openWindow("/assets/layouts/SwitchCourse.fxml", References.language.getString("editing.text"),
				GUILoader.getPrimaryStage(), this.theme);

	}

	public void onSwitchCourseUnallocatedStudent(ActionEvent event) {
		if (Distributor.calculate) {
			GUIManager.getInstance().startErrorFrame(References.language.getString("calculation.edit_data_fail.title"),
					References.language.getString("calculation.edit_data_fail.description"));
			return;
		}

		Student student = this.unallocatedStudents.getSelectionModel().getSelectedItem();

		if (student == null || (student.getName() == null) || student.getPrename() == null
				|| (student.getName().isEmpty() && student.getPrename().isEmpty()))
			return;

		SwitchCourseManager.student = student;

		LOGGER.config("Starting Switch Course Window");

		Util.openWindow("/assets/layouts/SwitchCourse.fxml", References.language.getString("editing.text"),
				GUILoader.getPrimaryStage(), this.theme);
	}

	public void onEditStudent(ActionEvent event) {
		if (Distributor.calculate) {
			GUIManager.getInstance().startErrorFrame(References.language.getString("calculation.edit_data_fail.title"),
					References.language.getString("calculation.edit_data_fail.description"));
			return;
		}

		Student student = this.tv0.getSelectionModel().getSelectedItem();

		if (student == null || (student.getName() == null) || student.getPrename() == null
				|| (student.getName().isEmpty() && student.getPrename().isEmpty()))
			return;

		AddStudentManager.studentID = student.getID();

		LOGGER.config("Starting Add Student Window");

		Util.openWindow("/assets/layouts/AddStudent.fxml", References.language.getString("editing.text"),
				GUILoader.getPrimaryStage(), this.theme);
	}

	public void onStudentChangeRequest(MouseEvent event) {
		if (Distributor.calculate) {
			GUIManager.getInstance().startErrorFrame(References.language.getString("calculation.edit_data_fail.title"),
					References.language.getString("calculation.edit_data_fail.description"));
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

			AddStudentManager.studentID = student.getID();

			Util.openWindow("/assets/layouts/AddStudent.fxml", References.language.getString("editing.text"),
					GUILoader.getPrimaryStage(), this.theme);
		}
	}

	public void onDragDroppedInput(DragEvent event) {
		if (Distributor.calculate) {
			GUIManager.getInstance().startErrorFrame(References.language.getString("calculation.import_fail.title"),
					References.language.getString("calculation.import_fail.description"));
			return;
		}

		Dragboard d = event.getDragboard();
		if (d.hasFiles()) {
			for (File f : d.getFiles()) {
				if (Util.endsWith(f.getPath(), ".csv", ".xlsx", ".xls")) {
					new Thread(() -> {
						new Distributor(f.getPath());
						this.inputView.fill();
						this.cView.fill();
						t1.setText(f.getPath());
					}).start();
				}
			}
		}

	}

	public void addStudent(ActionEvent event) {
		if (Distributor.calculate) {
			GUIManager.getInstance().startErrorFrame(References.language.getString("calculation.edit_data_fail.title"),
					References.language.getString("calculation.edit_data_fail.description"));
			return;
		}

		LOGGER.config("Starting Add Student Window");

		AddStudentManager.studentID = -1;

		Util.openWindow("/assets/layouts/AddStudent.fxml", References.language.getString("addstudent.text"),
				GUILoader.getPrimaryStage(), this.theme);
	}

	public void onFileTypeChanged(ActionEvent event) {
		Config.outputFileType = cb2.getValue();
	}

	public boolean tabS = true, tabOS = true, tabOC = false;

	public void onSelectionChangedCourse(Event event) {

		if (tabCourses == null) {
			return;
		}

		if (((Tab) event.getSource()).isSelected()) {
			menuCourse.setVisible(false);
			tabS = true;
		} else {
			tabS = false;
			menuCourse.setVisible(true);
		}

	}

	public void onTabIn(Event event) {

		if (tabInput == null || menuStudent == null || menuCourse == null)
			return;

		if (((Tab) event.getSource()).isSelected()) {
			if (tabS)
				menuStudent.setVisible(true);
			else
				menuCourse.setVisible(true);

			seperator.setVisible(true);
		} else {

			seperator.setVisible(false);

			menuCourse.setVisible(false);

			menuStudent.setVisible(false);
		}
	}

	public void onShowImportedData(ActionEvent event) {
		masterTabPane.getSelectionModel().select(tabInput);
	}

	public void onSelectionChangedStudent(Event event) {
		if (tabStudents == null)
			return;

		if (!((Tab) event.getSource()).isSelected())
			menuStudent.setVisible(true);
		else
			menuStudent.setVisible(false);

	}

	public void onSelectionOutputCourse(Event event) {

		if (teachers == null)
			return;

		if (((Tab) event.getSource()).isSelected()) {
			menuCourse1.setVisible(true);
			seperator1.setVisible(true);
			b5.setDisable(false);
			tabOC = true;
		} else {
			tabOC = false;
			seperator1.setVisible(false);
			menuCourse1.setVisible(false);
			b5.setDisable(true);
		}

	}

	public void onTabOutput(Event event) {

		if (tabOutput == null || menuStudent1 == null || menuCourse1 == null)
			return;

		if (((Tab) event.getSource()).isSelected() && !students.isDisable() && !teachers.isDisable()) {
			if (tabOS) {
				menuStudent1.setVisible(true);
				b2.setDisable(false);
				b5.setDisable(true);
			} else if (tabOC) {
				menuCourse1.setVisible(true);
				b2.setDisable(true);
				b5.setDisable(false);
			}
			seperator1.setVisible(true);
		} else {
			seperator1.setVisible(false);

			menuCourse1.setVisible(false);

			menuStudent1.setVisible(false);

			b2.setDisable(true);
		}
	}

	public void onSelectOutputStudent(Event event) {
		if (students == null)
			return;

		if (((Tab) event.getSource()).isSelected() && tabOutput.isSelected()) {
			menuStudent1.setVisible(true);
			seperator1.setVisible(true);
			b2.setDisable(false);
			tabOS = true;
		} else {
			menuStudent1.setVisible(false);
			seperator1.setVisible(false);
			b2.setDisable(true);
			tabOS = false;
		}
	}

	public void searchActionInput(ActionEvent event) {
		if (Distributor.calculate) {
			GUIManager.getInstance().startErrorFrame(References.language.getString("calculation.import_fail.title"),
					References.language.getString("calculation.import_fail.description"));
			return;
		}

		FileChooser fc = new FileChooser();
		fc.getExtensionFilters().addAll(new ExtensionFilter("Grid Data", "*.csv", "*.xls", "*.xlsx"));
		fc.setTitle(References.language.getString("choosefile.text"));

		File toAdd = new File(t1.getText());

		if (!Util.isBlank(t1.getText()) && new File(t1.getText()).exists()
				&& new File(new File(t1.getText()).getParent()).exists())
			fc.setInitialDirectory(new File(new File(t1.getText()).getParent()));

		File selected = fc.showOpenDialog(null);

		if (selected == null)
			return;

		if (selected.exists() && Util.endsWith(selected.getPath(), ".csv", ".xlsx", ".xls")) {
			Config.inputFile = selected.getPath();
			new Thread(() -> {
				new Distributor(selected.getPath());
				this.inputView.fill();
				this.cView.fill();
				t1.setText(selected.getPath());
			}).start();
		}
	}

	public void onAbout(ActionEvent event) {
		LOGGER.config("Starting About Window");

		Util.openWindow("/assets/layouts/About.fxml", References.language.getString("about.text"),
				GUILoader.getPrimaryStage(), this.theme).setResizable(false);
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

//	@FXML
//	public RadioButton cf1, cf2;

	public void searchActionOutput(ActionEvent event) {
//		if (cf1.isSelected()) {
		DirectoryChooser fc = new DirectoryChooser();
		fc.setTitle(References.language.getString("choosedirectory.text"));
		if (!Util.isBlank(t2.getText()) && new File(t1.getText()).exists()
				&& new File(new File(t2.getText()).getParent()).exists())
			fc.setInitialDirectory(new File(t2.getText()));

		File selected = fc.showDialog(null);

		if (selected == null)
			return;

		t2.setText(selected.getParent() + "\\" + selected.getName());

		Config.outputFile = selected.getParent() + "\\" + selected.getName();

//		}

//		if (cf2.isSelected()) {
//			FileChooser fc = new FileChooser();
//			fc.getExtensionFilters().addAll(new ExtensionFilter("Grid Data", "*.csv", "*.xls", "*.xlsx"));
//			fc.setTitle("Choose File");
//
//			File toAdd = new File(t2.getText());
//
//			if (!Util.isBlank(t2.getText()) && new File(t1.getText()).exists()
//					&& new File(new File(t2.getText()).getParent()).exists())
//				fc.setInitialDirectory(new File(new File(t2.getText()).getParent()));
//
//			File selected = fc.showSaveDialog(null);
//
//			if (selected.getParentFile().exists() && Util.endsWith(selected.getPath(), ".csv", ".xlsx", ".xls")) {
//				t2.setText(selected.getParent());
//
//				LOGGER.config(selected.getName());
//				String[] strings = selected.getName().split("\\.");
//				t2.setText(selected.getParent() + "\\" + strings[0]);
//				cb2.setValue("." + strings[1]);
//			}
//		}
	}

	public void onDragDroppedOutput(DragEvent event) {
		Dragboard d = event.getDragboard();
		if (d.hasFiles()) {
			for (File f : d.getFiles()) {
				if (f.isDirectory()) {
					t2.setText(f.getParent() + "\\" + f.getName());
					// cb2.setValue("FOLDER");
					Config.outputFile = f.getParent() + "\\" + f.getName();
					// Config.outputFileType = "FOLDER";
				}
				/*
				 * else { if (f.exists() && Util.endsWith(f.getPath(), ".csv", ".xlsx", ".xls"))
				 * { // t2.setText(f.getParent());
				 * 
				 * LOGGER.config(f.getName()); String[] strings = f.getName().split("\\.");
				 * 
				 * t2.setText(f.getParent() + "\\" + strings[0]); cb2.setValue("." +
				 * strings[1]); } }
				 */
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

		Util.openWindow("/assets/layouts/Error.fxml", "ERROR", GUILoader.getPrimaryStage(), this.theme);
		LOGGER.info("Error Window Started");

	}

	public void addCourseToActual(ActionEvent event) {
		if (GUIManager.actual == null) {
			GUIManager.getInstance().startErrorFrame(References.language.getString("calculation.no_save.title"),
					References.language.getString("calculation.no_save.description"));
			return;
		}

	}

	public void onClearDistributor(ActionEvent event) {
		Distributor.getInstance().clear();

		GUIManager.getInstance().inputView.fill();
		GUIManager.getInstance().cView.fill();
	}

	public void saveAction(ActionEvent event) {
		if (Distributor.calculate) {
			GUIManager.getInstance().startErrorFrame(References.language.getString("calculation.save_fail.title"),
					References.language.getString("calculation.save_fail.description"));
			return;
		}

		Timestamp timestamp = new Timestamp(System.currentTimeMillis());

		LOGGER.info("Start Saving files");
		LOGGER.info("Try to save to " + Config.outputFile + "/calculation" + Config.outputFileType);

		Save save = this.actual;
		if (save == null) {
			this.startErrorFrame(References.language.getString("calculation.no_calculation.title"),
					References.language.getString("calculation.no_calculation.description"));
			return;
		}

		save.sortAll();

		Distributor.calculate = true;

		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				p0.setVisible(true);

				GUIManager.getInstance().r1.setDisable(true);
				GUIManager.getInstance().r2.setDisable(true);
				GUIManager.getInstance().r3.setDisable(true);

				de.juhu.guiFX.ProgressIndicator.getInstance().setfProgressMax(Config.runs).setfProgressValue(-1);
			}
		});

		String s;
		if (Util.isBlank((s = Config.outputFile))) {
			this.startErrorFrame(References.language.getString("no_output_file.title"),
					References.language.getString("no_output_file.description"));

			Platform.runLater(new Runnable() {

				@Override
				public void run() {
					de.juhu.guiFX.ProgressIndicator.getInstance().setfProgressMax(Config.runs).setfProgressValue(0);

					p0.setVisible(false);

					GUIManager.getInstance().r1.setDisable(false);
					GUIManager.getInstance().r2.setDisable(false);
					GUIManager.getInstance().r3.setDisable(false);
				}
			});

			return;
		}

		if (s.endsWith("\\"))
			s.substring(0, s.length() - 2);
		else if (s.endsWith("/"))
			s.substring(0, s.length() - 1);

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
//		case "FOLDER":
//			ExcelExporter.writeXLS(s + "/Excel_OLD", save.writeInformation());
//			ExcelExporter.writeXLSX(s + "/KuFA-Zuweiser Ergebnisse", save.writeInformation());
//			CSVExporter.writeCSV(s + "/course", save.writeCourseInformation());
//			CSVExporter.writeCSV(s + "/student", save.writeStudentInformation());
//			LogWriter.writeLog(s + "/logging");
//			break;
		default:
			xlsx = true;
			break;
		}

		try {
			if (xls) {
				ExcelExporter.writeXLS(s + "/calculation" + timestamp.getTime(), save.writeInformation());
			}
			if (xlsx) {
				ExcelExporter.writeXLSX(s + "/calculation" + timestamp.getTime(), save.writeInformation());
			}
			if (csv) {
				CSVExporter.writeCSV(s + "/course" + timestamp.getTime(), save.writeCourseInformation());
				CSVExporter.writeCSV(s + "/student" + timestamp.getTime(), save.writeStudentInformation());
			}
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Error while exporting data", e);
		}

		LogWriter.writeLog(s + "/logging" + timestamp.getTime());
		this.save(s + "/save" + timestamp.getTime());

		LOGGER.info("Finished Saving files");

		Distributor.calculate = false;

		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				de.juhu.guiFX.ProgressIndicator.getInstance().setfProgressMax(Config.runs).setfProgressValue(0);

				p0.setVisible(false);

				GUIManager.getInstance().r1.setDisable(false);
				GUIManager.getInstance().r2.setDisable(false);
				GUIManager.getInstance().r3.setDisable(false);
			}
		});

	}

	public void saveActualAction(ActionEvent event) {
		if (Distributor.calculate) {
			GUIManager.getInstance().startErrorFrame(References.language.getString("calculation.save_fail.title"),
					References.language.getString("calculation.save_fail.description"));
			return;
		}

		Timestamp timestamp = new Timestamp(System.currentTimeMillis());

		LOGGER.info("Start Saving files");
		LOGGER.info("Try to save to " + Config.outputFile + Config.outputFileType);

		Save save = actual;
		if (save == null) {
			this.startErrorFrame(References.language.getString("calculation.no_calculation.title"),
					References.language.getString("calculation.no_calculation.description"));
			return;
		}

		Distributor.calculate = true;

		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				p0.setVisible(true);

				GUIManager.getInstance().r1.setDisable(true);
				GUIManager.getInstance().r2.setDisable(true);
				GUIManager.getInstance().r3.setDisable(true);

				de.juhu.guiFX.ProgressIndicator.getInstance().setfProgressMax(Config.runs).setfProgressValue(-1);
			}
		});

		String s;
		if (Util.isBlank((s = Config.outputFile))) {
			this.startErrorFrame(References.language.getString("no_output_file.title"),
					References.language.getString("no_output_file.description"));

			Platform.runLater(new Runnable() {

				@Override
				public void run() {
					de.juhu.guiFX.ProgressIndicator.getInstance().setfProgressMax(Config.runs).setfProgressValue(0);

					p0.setVisible(false);

					GUIManager.getInstance().r1.setDisable(false);
					GUIManager.getInstance().r2.setDisable(false);
					GUIManager.getInstance().r3.setDisable(false);
				}
			});

			return;
		}

		if (s.endsWith("\\"))
			s.substring(0, s.length() - 2);
		else if (s.endsWith("/"))
			s.substring(0, s.length() - 1);
//
//		boolean xls = false, xlsx = false, csv = false;
//
//		switch (Config.outputFileType) {
//		case ".xls":
//			xls = true;
//			break;
//		case ".xlsx":
//			xlsx = true;
//			break;
//		case ".csv":
//			csv = true;
//			break;
//		case "FOLDER":
//			ExcelExporter.writeXLS(s + "/Excel_OLD", save.writeInformation());
//			ExcelExporter.writeXLSX(s + "/KuFA-Zuweiser Ergebnisse", save.writeInformation());
//			CSVExporter.writeCSV(s + "/course", save.writeCourseInformation());
//			CSVExporter.writeCSV(s + "/student", save.writeStudentInformation());
//			LogWriter.writeLog(s + "/logging");
//			break;
//		default:
//			xlsx = true;
//			break;
//		}
//
//		if (xls) {
//			ExcelExporter.writeXLS(s + "/calculation" + timestamp.getTime(), save.writeInformation());
//		}
//		if (xlsx) {
//			ExcelExporter.writeXLSX(s + "/calculation" + timestamp.getTime(), save.writeInformation());
//		}
//		if (csv) {
//			CSVExporter.writeCSV(s + "/course" + timestamp.getTime(), save.writeCourseInformation());
//			CSVExporter.writeCSV(s + "/student" + timestamp.getTime(), save.writeStudentInformation());
//		}

		// LogWriter.writeLog(s + "/logging" + timestamp.getTime());
		this.save(s + "/save" + timestamp.getTime());

		LOGGER.info("Finished Saving files");

		Distributor.calculate = false;

		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				de.juhu.guiFX.ProgressIndicator.getInstance().setfProgressMax(Config.runs).setfProgressValue(0);

				p0.setVisible(false);

				GUIManager.getInstance().r1.setDisable(false);
				GUIManager.getInstance().r2.setDisable(false);
				GUIManager.getInstance().r3.setDisable(false);
			}
		});

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

		if (selected == null)
			return;

		LogWriter.writeLog(selected.getPath() + "/logging");
	}

	public void getInformation(ActionEvent event) {
		if (t1.isVisible())
			t1.setVisible(false);
		else
			t1.setVisible(true);
	}

	public void onSaveDueToWindowChange(Event event) {
		onSaveConfig(null);
	}

	public void onSaveConfig(ActionEvent event) {
		LOGGER.fine("Start saving config");

		if (!Files.exists(FileSystems.getDefault().getPath(References.HOME_FOLDER), LinkOption.NOFOLLOW_LINKS))
			new File(References.HOME_FOLDER).mkdir();

		try {
			ConfigManager.getInstance().save(new File(References.HOME_FOLDER + "config.cfg"));
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Error due to write config data!", e);
		}

		LOGGER.fine("Finished saving config");
	}

	public void close(ActionEvent event) {
		ConfigManager.getInstance().onConfigChanged();

		this.onSaveConfig(null);

		LOGGER.info("Close " + References.PROJECT_NAME);
		System.exit(0);
	}

	public void onHelp(ActionEvent event) {

		try {
			if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
				Desktop.getDesktop().browse(new URI("https://github.com/juhu1705/CaRP/wiki"));
			}
		} catch (IOException | URISyntaxException e) {
			References.LOGGER.info("https://github.com/juhu1705/CaRP/wiki");
		}

	}

	public void openHelpFile(ActionEvent event) {

		try {
			if (!Files.exists(FileSystems.getDefault().getPath(References.HOME_FOLDER), LinkOption.NOFOLLOW_LINKS))
				new File(References.HOME_FOLDER).mkdir();

			if (Files.exists(FileSystems.getDefault().getPath(References.HOME_FOLDER + "help.pdf"),
					LinkOption.NOFOLLOW_LINKS))
				Files.delete(FileSystems.getDefault().getPath(References.HOME_FOLDER + "help.pdf"));

			Files.copy(getClass().getResourceAsStream("/assets/Der Course and Research Paper Assinger.pdf"),
					FileSystems.getDefault().getPath(References.HOME_FOLDER + "help.pdf"));

			Desktop.getDesktop().browse(FileSystems.getDefault().getPath(References.HOME_FOLDER + "help.pdf").toUri());
		} catch (IOException e) {

		}

	}

	public void onShowInExcel(ActionEvent event) {

		if (Distributor.calculate) {
			GUIManager.getInstance().startErrorFrame(References.language.getString("calculation.save_fail.title"),
					References.language.getString("calculation.save_fail.description"));
			return;
		}

		Save save = GUIManager.actual;

		if (save == null) {
			GUIManager.getInstance().startErrorFrame(References.language.getString("calculation.no_calculation.title"),
					References.language.getString("calculation.no_calculation.description"));
			return;
		}

		try {
			ExcelExporter.writeXLSX("test", save.writeInformation());

			Desktop.getDesktop().browse(new File("test.xlsx").toURI());
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Error due to open the preview Excel file.");
		}
	}

	public void updateInputView() {
		while (Config.maxChooses > this.atci.size() / 3) {
			int number = (this.atci.size() / 3 + 1);

			TableColumn<Student, String> k = new TableColumn<>(
					References.language.getString("course.text") + " " + number),
					s = new TableColumn<>(References.language.getString("subject.text")),
					t = new TableColumn<>(References.language.getString("teacher.text"));
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

				TableColumn<Student, String> k = new TableColumn<>(
						References.language.getString("course.text") + " " + number),
						s = new TableColumn<>(References.language.getString("subject.text")),
						t = new TableColumn<>(References.language.getString("teacher.text"));
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

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		ProgressIndicator pi = new ProgressIndicator();

		GUIManager.instance = this;

		this.ta1.setCacheHint(CacheHint.SPEED);

		cb0.setItems(FXCollections.observableArrayList(PrintFormat.values()));

		cb1.setItems(FXCollections.observableArrayList(Level.ALL, Level.FINEST, Level.FINER, Level.FINE, Level.CONFIG,
				Level.INFO, Level.WARNING, Level.SEVERE, Level.OFF));
		cb2.setItems(FXCollections.observableArrayList(".csv", ".xlsx", ".xls"));

		p0.progressProperty().bind(FullProgress.getInstance().progressProperty());
		p0.setVisible(false);

		// Config Stuff

		try {
			ConfigManager.getInstance().register(Config.class);
		} catch (IOException e4) {
			LOGGER.log(Level.SEVERE, "Error while register Configuration Elements", e4);
		}

		if (!Files.exists(FileSystems.getDefault().getPath(References.HOME_FOLDER + "config.cfg"),
				LinkOption.NOFOLLOW_LINKS))
			ConfigManager.getInstance().loadDefault();
		else
			try {
				ConfigManager.getInstance().load(References.HOME_FOLDER + "config.cfg");
			} catch (SAXException | IOException e4) {
				LOGGER.log(Level.SEVERE, "Error while loading Config", e4);
			}

		ConfigManager.getInstance().createMenuTree(this.configurationTree, this.config);

		GUILoader.getPrimaryStage().setMaximized(Config.shouldMaximize);

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
		// this.startPicture();

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

		this.maxStudentCount.setCellValueFactory(c -> {
			return new SimpleStringProperty(Integer.toString(c.getValue().getMaxStudentCount()));
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
			if (s.getValue().getActiveCourse() != null
					&& Util.isIgnoreCourse(s.getValue().getActiveCourse().getSubject().split("|")))
				return new SimpleStringProperty("-");
			return new SimpleStringProperty(s.getValue().getPriority() == Integer.MAX_VALUE ? "Inf."
					: Integer.toString(s.getValue().getPriority()));
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

		this.percentualPriorities.setCellValueFactory(s -> new SimpleStringProperty(Double
				.toString(s.getValue().getValue().doubleValue() / this.actual.getInformation().getStudentCount())));

		this.unallocatedPrename.setCellValueFactory(s -> {
			return new SimpleStringProperty(s.getValue().getPrename());
		});

		this.unallocatedName.setCellValueFactory(s -> {
			return new SimpleStringProperty(s.getValue().getName());
		});

		// Theme

		this.checks.put(Theme.LIGHT, mb0);
		this.checks.put(Theme.DARK, mb1);

		// Update Config

		ConfigManager.getInstance().onConfigChanged();

		if (Config.shouldImportAutomatic) {
			t1.setText(Config.inputFile);

			new Thread(() -> {
				File file = new File(Config.inputFile);
				if (file.exists()) {
					new Distributor(Config.inputFile);
					this.inputView.fill();
					this.cView.fill();
				}
			}).start();
		}

		// Check for activate Student options

		if (tabInput.isSelected() && tabStudents.isSelected()) {
			menuStudent.setVisible(true);
			menuStudent1.setVisible(false);
			seperator.setVisible(true);
			seperator1.setVisible(false);
			menuCourse.setVisible(false);
			menuCourse1.setVisible(false);
		}

		this.counter.setText(References.language.getString("distribution.text") + ": " + Integer.toString(0));

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

	public void save(String location) {
		ObjectOutputStream objOut;
		try {
			Save next = GUIManager.actual;
			objOut = new ObjectOutputStream(new FileOutputStream(location + ".carp"));
			objOut.writeObject(next);
			objOut.writeObject(next = Distributor.calculated.next(next));
			objOut.writeObject(next = Distributor.calculated.next(next));
			objOut.writeObject(next = Distributor.calculated.next(next));
			objOut.writeObject(next = Distributor.calculated.next(next));

			objOut.close();
		} catch (IOException e) {
			References.LOGGER.warning("Can not save the save, please try another location!");
		}
	}

	public void onNew(ActionEvent event) {
		this.onClearCalculations(event);
		this.onClearDistributor(event);
	}

	public void onLoad(ActionEvent event) {
		if (Distributor.calculate) {
			GUIManager.getInstance().startErrorFrame(References.language.getString("calculation.import_fail.title"),
					References.language.getString("calculation.import_fail.description"));
			return;
		}

		FileChooser fc = new FileChooser();
		fc.getExtensionFilters().addAll(new ExtensionFilter("Grid Data", "*.carp"));
		fc.setTitle(References.language.getString("choosefile.text"));

		File toAdd = new File(t1.getText());

		if (!Util.isBlank(t1.getText()) && new File(t1.getText()).exists()
				&& new File(new File(t1.getText()).getParent()).exists())
			fc.setInitialDirectory(new File(new File(t1.getText()).getParent()));

		File selected = fc.showOpenDialog(null);

		if (selected == null)
			return;

		if (selected.exists() && Util.endsWith(selected.getPath(), ".carp")) {
			this.load(selected.getPath());
			this.inputView.fill();
			this.cView.fill();
		}
	}

	public void load(String location) {
		ObjectInputStream objIn;
		try {
			objIn = new ObjectInputStream(new File(location).toURI().toURL().openConnection().getInputStream());

			new Distributor((GUIManager.actual = (Save) objIn.readObject()), (Save) objIn.readObject(),
					(Save) objIn.readObject(), (Save) objIn.readObject(), (Save) objIn.readObject());

			Platform.runLater(new Runnable() {

				@Override
				public void run() {
					GUIManager.getInstance().counter
							.setText(Integer.toString(Distributor.calculated.indexOf(GUIManager.actual)));
				}
			});

			Platform.runLater(GUIManager.getInstance().outputSView);
			Platform.runLater(GUIManager.getInstance().outputCView);
			Platform.runLater(GUIManager.getInstance().outputIView);

			objIn.close();

		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void onSetEnglish(ActionEvent event) {
		try {
			if (Files.exists(FileSystems.getDefault().getPath(References.HOME_FOLDER + "language.properties"),
					LinkOption.NOFOLLOW_LINKS))
				Files.delete(FileSystems.getDefault().getPath(References.HOME_FOLDER + "language.properties"));

			Files.copy(getClass().getResourceAsStream("/assets/language/en.properties"),
					FileSystems.getDefault().getPath(References.HOME_FOLDER + "language.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		GUIManager.getInstance().startErrorFrame(References.language.getString("restart.title"),
				References.language.getString("restart.description"));
	}

	public void onSetGerman(ActionEvent event) {
		try {
			if (Files.exists(FileSystems.getDefault().getPath(References.HOME_FOLDER + "language.properties"),
					LinkOption.NOFOLLOW_LINKS))
				Files.delete(FileSystems.getDefault().getPath(References.HOME_FOLDER + "language.properties"));

			Files.copy(getClass().getResourceAsStream("/assets/language/de.properties"),
					FileSystems.getDefault().getPath(References.HOME_FOLDER + "language.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		GUIManager.getInstance().startErrorFrame(References.language.getString("restart.title"),
				References.language.getString("restart.description"));
	}

}