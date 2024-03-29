package de.juhu.guiFX.lists;

import de.juhu.distributor.Student;
import de.juhu.guiFX.GUIManager;
import javafx.collections.FXCollections;
import javafx.scene.control.TableView;

import static de.noisruker.logger.Logger.LOGGER;

/**
 * Behandelt die Tabelle zur Ansicht der verteilten Schüler.
 *
 * @author Juhu1705
 * @category GUI
 */
public class OutputStudentsView implements Runnable {

    TableView<Student> tv;

    public OutputStudentsView(TableView<Student> inputTable) {
        this.tv = inputTable;
    }

    public void fill() {
        LOGGER.config("Loading Output Data to the Preview!");

        this.tv.setItems(FXCollections.observableArrayList(GUIManager.actual.getAllStudents()));
        this.tv.sort();
        this.tv.refresh();
    }

    @Override
    public void run() {
        fill();
        GUIManager.getInstance().students.setDisable(false);
    }

}
