package de.juhu.guiFX.lists;

import de.juhu.distributor.Course;
import de.juhu.distributor.Distributor;
import javafx.collections.FXCollections;
import javafx.scene.control.TableView;

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

        LOGGER.config("Loading Input Data to the Preview!");
        this.inputTable.getItems().clear();
        this.inputTable.setItems(FXCollections.observableArrayList(Distributor.getInstance().getCourses()));
        this.inputTable.sort();
    }
}
