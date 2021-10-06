package de.juhu.guiFX;

import de.juhu.distributor.Course;
import de.juhu.distributor.Distributor;
import de.juhu.distributor.ProgressIndicator;
import de.juhu.distributor.Save;
import de.juhu.distributor.Student;
import de.juhu.distributor.events.ProgressUpdateEvent;
import de.juhu.guiFX.lists.*;
import de.juhu.util.Config;
import de.juhu.util.References;
import de.juhu.util.Util;
import de.noisruker.config.ConfigManager;
import de.noisruker.config.event.ConfigEntryChangeEvent;
import de.noisruker.event.EventManager;
import de.noisruker.filemanager.CSVExporter;
import de.noisruker.filemanager.ExcelExporter;
import de.noisruker.logger.PrintFormat;
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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.sql.Timestamp;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;

import static de.noisruker.logger.Logger.LOGGER;

/**
 * Diese Klasse verwaltet alle Aktionen des Haupt-GUIs.
 *
 * @author Juhu1705
 * @version 2.1
 * @category GUI
 */
public class GUIManager implements Initializable {

    public static Save actual;
    private static GUIManager instance;
    @FXML
    public ImageView i0;
    @FXML
    public ProgressBar p0;
    @FXML
    public TreeView<String> configurationTree;
    @FXML
    public Label counter, textPrevious, textNext, textActual, progressPercent;
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
    public VBox config, loggingPage, progressContainer;
    @FXML
    public ListView<String> lv0;
    @FXML
    public CheckMenuItem mb0, mb1;
    public HashMap<String, CheckMenuItem> checks = new HashMap<>();
    public boolean tabS = true, tabOS = true, tabOC = false;
    @FXML
    public AnchorPane ap0;
    FadeTransition ft;
    TranslateTransition tt;
    ScaleTransition st;
    private CheckMenuItem last, lastSwitch;

    public static GUIManager getInstance() {
        return instance;
    }

    public void onDragOver(DragEvent event) {
        event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
    }

    public void onFullScreen(ActionEvent event) {
        GUILoader.getPrimaryStage().setFullScreen(!GUILoader.getPrimaryStage().isFullScreen());
    }

    public void onDeleteStudentFromActualSave(ActionEvent event) {
        if (actual == null)
            return;

        Student student = this.tv1.getSelectionModel().getSelectedItem();

        actual.removeStudent(student);

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
                actual.removeCourse(c);

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

        actual.removeCourse(course);

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
                References.language.getString("addstudent.text"), GUILoader.getPrimaryStage());
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
                GUILoader.getPrimaryStage());
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
                GUILoader.getPrimaryStage());
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
                    + Util.round(Distributor.calculated.next(actual).getInformation().getGuete(), 3));
        this.b1.setDisable(false);
        this.textPrevious.setText(References.language.getString("previousgoodness.text")
                + Util.round(Distributor.calculated.previous(actual).getInformation().getGuete(), 3));

        this.counter.setText(References.language.getString("distribution.text") + ": "
                + (Distributor.calculated.indexOf(actual) + 1));
        this.textActual.setText(References.language.getString("calculationgoodness.text") + ": "
                + Util.round(actual.getInformation().getGuete(), 3));
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
            this.textPrevious.setText(References.language.getString("previousgoodness.text") + Util.round(Distributor.calculated.previous(actual).getInformation().getGuete(), 3));
        this.b4.setDisable(false);
        this.textNext.setText(References.language.getString("nextgoodness.text")
                + Util.round(Distributor.calculated.next(actual).getInformation().getGuete(), 3));

        this.counter.setText(References.language.getString("distribution.text") + ": "
                + (Distributor.calculated.indexOf(actual) + 1));
        this.textActual.setText(References.language.getString("calculationgoodness.text") + ": "
                + Util.round(actual.getInformation().getGuete(), 3));
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
                GUILoader.getPrimaryStage());
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

    public void onSetLightTheme(ActionEvent event) {
        this.onSetTheme("LIGHT");
    }

    public void onSetDarkTheme(ActionEvent event) {
        this.onSetTheme("DARK");
    }

    public void onSetTheme(String theme) {
        Config.theme = theme;
        ConfigManager.getInstance().onConfigChanged("theme.text", Config.theme);
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
                GUILoader.getPrimaryStage());
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
                    GUILoader.getPrimaryStage());

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
                GUILoader.getPrimaryStage());

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
                GUILoader.getPrimaryStage());
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
                GUILoader.getPrimaryStage());
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
                    GUILoader.getPrimaryStage());
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
                GUILoader.getPrimaryStage());
    }

    public void onFileTypeChanged(ActionEvent event) {
        Config.outputFileType = cb2.getValue();
        ConfigManager.getInstance().onConfigChanged("outputfiletype.text", Config.outputFileType);
    }

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

        menuStudent.setVisible(!((Tab) event.getSource()).isSelected());

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

//	@FXML
//	public RadioButton cf1, cf2;

    public void onAbout(ActionEvent event) {
        LOGGER.config("Starting About Window");

        Stage s = Util.openWindow("/assets/layouts/About.fxml", References.language.getString("about.text"),
                GUILoader.getPrimaryStage());
        if(s != null) {
            s.setMinWidth(s.getWidth());
            s.setMaxWidth(s.getWidth());
        }
    }

    public void clearConsole(ActionEvent event) {
        References.LOGGING_AREA.setText("");
    }

    public void printFormatChanged(ActionEvent event) {
        Config.printFormat = cb0.getValue().toString();
        ConfigManager.getInstance().onConfigChanged("printformat.text", Config.printFormat);
    }

    public void levelChanges(ActionEvent event) {
        Config.maxPrintLevel = cb1.getValue().toString();
        ConfigManager.getInstance().onConfigChanged("loglevel.text", Config.maxPrintLevel);
    }

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

        progressContainer.setVisible(true);

        GUIManager.getInstance().r1.setDisable(true);
        GUIManager.getInstance().r2.setDisable(true);
        GUIManager.getInstance().r3.setDisable(true);

        Thread t = new Thread(Distributor.getInstance(), "Calculator");
        t.start();
    }

    public void startErrorFrame(String headline, String message) {
        LOGGER.log(Level.SEVERE, message, new Exception(headline));
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
        new Thread(() -> {
            if (Distributor.calculate) {
                GUIManager.getInstance().startErrorFrame(References.language.getString("calculation.save_fail.title"),
                        References.language.getString("calculation.save_fail.description"));
                return;
            }

            Timestamp timestamp = new Timestamp(System.currentTimeMillis());

            LOGGER.info("Start Saving files");
            LOGGER.info("Try to save to " + Config.outputFile + "/calculation" + Config.outputFileType);

            Save save = actual;
            if (save == null) {
                this.startErrorFrame(References.language.getString("calculation.no_calculation.title"),
                        References.language.getString("calculation.no_calculation.description"));
                return;
            }

            save.sortAll();

            Distributor.calculate = true;

            Platform.runLater(() -> {
                progressContainer.setVisible(true);

                GUIManager.getInstance().r1.setDisable(true);
                GUIManager.getInstance().r2.setDisable(true);
                GUIManager.getInstance().r3.setDisable(true);

                ProgressIndicator.getInstance().setfProgressMax(Config.runs).setfProgressValue(-1);
            });

            String s;
            if (Util.isBlank((s = Config.outputFile))) {
                this.startErrorFrame(References.language.getString("no_output_file.title"),
                        References.language.getString("no_output_file.description"));

                Platform.runLater(() -> {
                    ProgressIndicator.getInstance().setfProgressMax(Config.runs).setfProgressValue(0);

                    progressContainer.setVisible(false);

                    GUIManager.getInstance().r1.setDisable(false);
                    GUIManager.getInstance().r2.setDisable(false);
                    GUIManager.getInstance().r3.setDisable(false);
                });

                return;
            }

            if (s.endsWith("\\"))
                s = s.substring(0, s.length() - 2);
            else if (s.endsWith("/"))
                s = s.substring(0, s.length() - 1);

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
                case "all":
                    xls = true;
                    csv = true;
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

            this.writeLog(s + "/carp_log" + timestamp.getTime());
            this.save(s + "/carp_save" + timestamp.getTime());

            LOGGER.info("Finished Saving files");

            Distributor.calculate = false;

            Platform.runLater(() -> {
                ProgressIndicator.getInstance().setfProgressMax(Config.runs).setfProgressValue(0);

                progressContainer.setVisible(false);

                GUIManager.getInstance().r1.setDisable(false);
                GUIManager.getInstance().r2.setDisable(false);
                GUIManager.getInstance().r3.setDisable(false);
            });
        }).start();
    }

    public void writeLog(String pathfile) {
        FileWriter fileWriter = null;
        BufferedWriter writer = null;

        try {
            fileWriter = new FileWriter(pathfile + ".log");
            writer = new BufferedWriter(fileWriter);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Fehler beim Erstellen einer .log Datei", e);
        }

        String sw = References.LOGGING_AREA.getText();

        String[] strings = sw.split(" \n");

        try {
            for (String s : strings) {
                writer.write(sw);
                writer.newLine();
            }
        } catch (IOException e1) {
            LOGGER.log(Level.SEVERE, "Fehler beim Erstellen einer .log Datei", e1);
        }

        try {
            writer.close();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Fehler beim Verarbeiten einer .log Datei", e);
        }
    }

    public void saveActualAction(ActionEvent event) {
        new Thread(() -> {
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

            Platform.runLater(() -> {
                progressContainer.setVisible(true);

                GUIManager.getInstance().r1.setDisable(true);
                GUIManager.getInstance().r2.setDisable(true);
                GUIManager.getInstance().r3.setDisable(true);

                ProgressIndicator.getInstance().setfProgressMax(Config.runs).setfProgressValue(-1);
            });

            String s;
            if (Util.isBlank((s = Config.outputFile))) {
                this.startErrorFrame(References.language.getString("no_output_file.title"),
                        References.language.getString("no_output_file.description"));

                Platform.runLater(() -> {
                    ProgressIndicator.getInstance().setfProgressMax(Config.runs).setfProgressValue(0);

                    progressContainer.setVisible(false);

                    GUIManager.getInstance().r1.setDisable(false);
                    GUIManager.getInstance().r2.setDisable(false);
                    GUIManager.getInstance().r3.setDisable(false);
                });

                return;
            }

            if (s.endsWith("\\"))
                s = s.substring(0, s.length() - 2);
            else if (s.endsWith("/"))
                s = s.substring(0, s.length() - 1);

            this.writeLog(s + "/carp_log" + timestamp.getTime());
            this.save(s + "/carp_save" + timestamp.getTime());

            LOGGER.info("Finished Saving files");

            Distributor.calculate = false;

            Platform.runLater(() -> {
                ProgressIndicator.getInstance().setfProgressMax(Config.runs).setfProgressValue(0);

                progressContainer.setVisible(false);

                GUIManager.getInstance().r1.setDisable(false);
                GUIManager.getInstance().r2.setDisable(false);
                GUIManager.getInstance().r3.setDisable(false);
            });
        });
    }

    public void runAndSaveAction(ActionEvent event) {
        new Thread(() -> {
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


        this.writeLog(selected.getPath() + "/carp_log");
    }

    public void getInformation(ActionEvent event) {
        t1.setVisible(!t1.isVisible());
    }

    public void onSaveDueToWindowChange(Event event) {
        //onSaveConfig(null);
    }

    public void onSaveConfig(ActionEvent event) {
        LOGGER.fine("Start saving config");

        if (!Files.exists(FileSystems.getDefault().getPath(References.HOME_FOLDER), LinkOption.NOFOLLOW_LINKS))
            new File(References.HOME_FOLDER).mkdir();

        try {
            ConfigManager.getInstance().save(new File(References.HOME_FOLDER + "/config.cfg"));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error due to write config data!", e);
        }

        LOGGER.fine("Finished saving config");
    }

    public void close(ActionEvent event) {
        ConfigManager.getInstance().onConfigChangedGeneral();

        this.onSaveConfig(null);

        LOGGER.info("Close " + References.PROJECT_NAME);
        System.exit(0);
    }

    public void onHelp(ActionEvent event) {
        Util.openLink("https://github.com/juhu1705/CaRP/wiki");
    }

    public void openHelpFile(ActionEvent event) {
        try {
            File tmpFile = File.createTempFile("CaRP_Help", ".pdf");
            getClass().getResourceAsStream("/assets/Der Course and Research Paper Assinger.pdf").transferTo(new FileOutputStream(tmpFile));

            tmpFile.deleteOnExit();

            Util.openLink(tmpFile.getAbsolutePath());
        } catch (IOException ignored) {

        }

    }

    public void onShowInExcel(ActionEvent event) {
        String tempFilePath = System.getProperty("java.io.tmpdir");

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
            String fileName = tempFilePath + "/Carp_test-" + Math.abs(References.RAND_GEN.nextInt());

            ExcelExporter.writeXLSX(fileName, save.writeInformation());

            File f = new File(fileName + ".xlsx");

            f.deleteOnExit();

            Util.openLink(f.getAbsolutePath());
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error due to open the preview Excel file.", e);
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
        GUIManager.instance = this;

        cb0.setItems(FXCollections.observableArrayList(PrintFormat.values()));

        cb0.setValue(PrintFormat.valueOf(Config.printFormat));

        cb1.setItems(FXCollections.observableArrayList(Level.ALL, Level.FINEST, Level.FINER, Level.FINE, Level.CONFIG,
                Level.INFO, Level.WARNING, Level.SEVERE, Level.OFF));

        cb1.setValue(Level.parse(Config.maxPrintLevel));

        cb2.setItems(FXCollections.observableArrayList(ConfigManager.getInstance().getRegisteredOptions("outputfiletype.text")));

        p0.progressProperty().bind(FullProgress.getInstance().progressProperty());
        progressContainer.setVisible(false);

        // Config Stuff

        ConfigManager.getInstance().createMenuTree(this.configurationTree, this.config, References.language);

        GUILoader.getPrimaryStage().setMaximized(Config.shouldMaximize);

        t2.setText(Config.outputFile);

        this.loggingPage.getChildren().add(References.LOGGING_AREA);

        // INFO: Tabellen Initierung

        // Input Tabelle

        this.inputView = new InputView(this.tv0);
        this.cView = new CourseView(this.tvc);

        this.vtc.setCellValueFactory(s -> new SimpleStringProperty(s.getValue().getPrename()));

        this.ntc.setCellValueFactory(s -> new SimpleStringProperty(s.getValue().getName()));

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

        this.subject.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSubject()));

        this.teacher.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTeacher()));

        this.maxStudentCount.setCellValueFactory(c -> new SimpleStringProperty(Integer.toString(c.getValue().getMaxStudentCount())));

        // INFO: Output Tabellen

        // Output Student View

        this.outputSView = new OutputStudentsView(this.tv1);

        this.cvtc.setCellValueFactory(s -> new SimpleStringProperty(s.getValue().getPrename()));

        this.cntc.setCellValueFactory(s -> new SimpleStringProperty(s.getValue().getName()));

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
                    && Util.isIgnoreCourse(s.getValue().getActiveCourse().getSubject().split("\\|")))
                return new SimpleStringProperty("-");
            return new SimpleStringProperty(s.getValue().getPriority() == Integer.MAX_VALUE ? "Inf."
                    : Integer.toString(s.getValue().getPriority()));
        });

        // Output Course View

        this.outputCView = new OutputCourseView(this.tv2);

        this.oSubject.setCellValueFactory(s -> new SimpleStringProperty(s.getValue().getSubject()));

        this.oTeacher.setCellValueFactory(s -> new SimpleStringProperty(s.getValue().getTeacher()));

        // Output Information View

        this.outputIView = new OutputInformationView();

        this.bPrename.setCellValueFactory(s -> new SimpleStringProperty(s.getValue().getPrename()));

        this.bName.setCellValueFactory(s -> new SimpleStringProperty(s.getValue().getName()));

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

        this.bPriority.setCellValueFactory(s -> new SimpleStringProperty(Integer.toString(s.getValue().getPriority())));

        this.rate.setCellValueFactory(s -> new SimpleStringProperty(s.getValue().getKey()));

        this.rateV.setCellValueFactory(s -> new SimpleStringProperty(s.getValue().getValue()));

        this.priority.setCellValueFactory(s -> new SimpleStringProperty(s.getValue().getKey()));

        this.swpriority.setCellValueFactory(s -> new SimpleStringProperty(s.getValue().getValue().toString()));

        this.percentualPriorities.setCellValueFactory(s -> new SimpleStringProperty(String.format("%.2f%%", (s.getValue().getValue().doubleValue() / actual.getInformation().getStudentCount()) * 100)));

        this.unallocatedPrename.setCellValueFactory(s -> new SimpleStringProperty(s.getValue().getPrename()));

        this.unallocatedName.setCellValueFactory(s -> new SimpleStringProperty(s.getValue().getName()));

        // Theme

        this.checks.put("LIGHT", mb0);
        this.checks.put("DARK", mb1);

        // Update Config

        this.updateInputView();

        EventManager.getInstance().registerEventListener(ConfigEntryChangeEvent.class, event -> {
            if("coosemaximum.text".equals(event.getEntryName()))
                this.updateInputView();
            else if("theme.text".equals(event.getEntryName())) {
                this.checks.forEach((themes, checkbox) -> checkbox.setSelected(false));
                this.checks.get(event.getEntryValue()).setSelected(true);
            } else if("loglevel.text".equals(event.getEntryName()))
                cb1.setValue(Level.parse(event.getEntryValue()));
            else if("printformat.text".equals(event.getEntryName()))
                cb0.setValue(PrintFormat.valueOf(event.getEntryValue()));
        });

        EventManager.getInstance().registerEventListener(ProgressUpdateEvent.class, event -> {
            Platform.runLater(() -> this.progressPercent.setText(String.format("%d / %d (%d%%)", event.getValue(), event.getMax(), event.getProgressPercent())));
        });

        // ConfigManager.getInstance().onConfigChangedGeneral();

        if (Config.shouldImportAutomatic) {
            t1.setText(Config.inputFile);

            new Thread(() -> {
                File file = new File(Config.inputFile);
                if (file.exists()) {
                    new Distributor(Config.inputFile);
                    Platform.runLater(() -> {
                        this.inputView.fill();
                        this.cView.fill();
                    });
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

        this.counter.setText(References.language.getString("distribution.text") + ": " + 0);

        if (Distributor.getInstance() != null && (!Distributor.getInstance().isEmpty())) {
            this.inputView.fill();
            this.cView.fill();
            t1.setText(Config.inputFile);

            if (Distributor.calculated != null && !Distributor.calculated.isEmpty()) {
                Platform.runLater(() -> {
                    GUIManager.getInstance().counter.setText(References.language.getString("distribution.text") + ": "
                            + (Distributor.calculated.indexOf(GUIManager.actual) + 1));

                    LOGGER.config(GUIManager.getInstance().textActual + "");

                    GUIManager.getInstance().textActual.setText(References.language.getString("calculationgoodness.text") + ": "
                            + Util.round(GUIManager.actual.getInformation().getGuete(), 3));

                    GUIManager.getInstance().b1.setDisable(true);
                    if (Distributor.calculated.size() > 1) {
                        GUIManager.getInstance().b4.setDisable(false);
                        GUIManager.getInstance().textNext.setText(References.language.getString("nextgoodness.text")
                                + Util.round(Distributor.calculated.get(2).getInformation().getGuete(), 3));
                    } else
                        GUIManager.getInstance().b4.setDisable(true);
                });

                Platform.runLater(GUIManager.getInstance().outputSView);
                Platform.runLater(GUIManager.getInstance().outputCView);
                Platform.runLater(GUIManager.getInstance().outputIView);

                Platform.runLater(() -> {
                    if (!GUIManager.getInstance().tabOC)
                        GUIManager.getInstance().tabOS = true;

                    GUIManager.getInstance().onTabOutput(
                            new Event(GUIManager.getInstance().tabOutput, GUIManager.getInstance().tabOutput, null));
                });
            }
        }
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
            LOGGER.warning("Can not save the save, please try another location!");
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
            objIn = new ObjectInputStream(new FileInputStream(location));

            objIn.setObjectInputFilter(ObjectInputFilter.Config.createFilter("de.juhu.distributor.Course;de.juhu.distributor.Save;de.juhu.distributor.InformationSave;de.juhu.distributor.Student;java.base/*;!*"));

            new Distributor((GUIManager.actual = (Save) objIn.readObject()), (Save) objIn.readObject(),
                    (Save) objIn.readObject(), (Save) objIn.readObject(), (Save) objIn.readObject());

            Platform.runLater(() -> GUIManager.getInstance().counter
                    .setText(Integer.toString(Distributor.calculated.indexOf(GUIManager.actual))));

            Platform.runLater(GUIManager.getInstance().outputSView);
            Platform.runLater(GUIManager.getInstance().outputCView);
            Platform.runLater(GUIManager.getInstance().outputIView);

            objIn.close();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void onSetEnglish(ActionEvent event) {
        Config.language = "ENGLISH";
        ConfigManager.getInstance().onConfigChanged("language.text", Config.language);
    }

    public void onSetGerman(ActionEvent event) {
        Config.language = "GERMAN";
        ConfigManager.getInstance().onConfigChanged("language.text", Config.language);
    }

}