package de.juhu.guiFX.lists;

import de.juhu.distributor.Course;
import de.juhu.distributor.Distributor;
import javafx.collections.FXCollections;
import javafx.scene.control.TableView;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;

import static de.noisruker.logger.Logger.LOGGER;

/**
 * Behandelt die Tabelle zur Ansicht der importierten Kursliste.
 *
 * @author Juhu1705
 * @category GUI
 */
public class CourseView {
    TableView inputTable;

    public CourseView(TableView<Course> inputTable) {
        this.inputTable = inputTable;
    }

    public void fill() {
        ArrayList<Course> allCourses = new ArrayList<>(Distributor.getInstance().getCourses());

        Collections.sort(allCourses);
        LOGGER.config("Loading Input Data to the Preview!");
        this.inputTable.setItems(FXCollections.observableArrayList(allCourses));
        this.inputTable.sort();
        this.inputTable.refresh();
    }
}
