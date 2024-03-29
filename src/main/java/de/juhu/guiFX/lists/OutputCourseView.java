package de.juhu.guiFX.lists;

import de.juhu.distributor.Course;
import de.juhu.guiFX.GUIManager;
import de.juhu.util.References;
import de.juhu.util.Util;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.ArrayList;

import static de.noisruker.logger.Logger.LOGGER;

/**
 * Behandelt die Tabelle zur Ansicht der verteilten Kurse.
 *
 * @author Juhu1705
 * @category GUI
 */
public class OutputCourseView implements Runnable {

    TableView<Course> tv;

    ArrayList<TableColumn<Course, String>> students = new ArrayList<>();

    public OutputCourseView(TableView<Course> inputTable) {
        this.tv = inputTable;
    }

    public void fill() {
        LOGGER.config("Loading Output Data to the Preview!");

        this.tv.setItems(FXCollections.observableArrayList(GUIManager.actual.getAllCourses()));
        this.tv.sort();
        this.tv.refresh();
    }

    private void loadMaxStudents() {
        for (TableColumn<Course, String> t : students) {
            tv.getColumns().remove(t);
        }
        students.clear();

        int maxCount = Util.maxStudentCount(GUIManager.actual.getAllCourses());

        for (int i = 0; i < maxCount; i++) {
            TableColumn<Course, String> prename = new TableColumn<>(References.language.getString("prename.text"));
            TableColumn<Course, String> name = new TableColumn<>(References.language.getString("name.text"));
            TableColumn<Course, String> student = new TableColumn<>(
                    References.language.getString("student.text") + " " + (i + 1));

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

        GUIManager.getInstance().teachers.setDisable(false);
    }

}
