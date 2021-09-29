package de.juhu.guiFX.lists;

import de.juhu.distributor.Distributor;
import de.juhu.distributor.Student;
import javafx.collections.FXCollections;
import javafx.scene.control.TableView;

import java.util.ArrayList;
import java.util.Collections;

import static de.noisruker.logger.Logger.LOGGER;

/**
 * Behandelt die Tabelle zur Ansicht der importierten Schülerliste.
 *
 * @author Juhu1705
 * @category GUI
 */
public class InputView {

    TableView inputTable;

    public InputView(TableView<Student> inputTable) {
        this.inputTable = inputTable;
    }

    public void fill() {

        ArrayList<Student> calcStud = Distributor.getInstance().getCalcStudents();
        ArrayList<Student> ignStud = Distributor.getInstance().getIgnoreStudents();
        ArrayList<Student> allStudents = new ArrayList<>(calcStud);
        allStudents.addAll(ignStud);

        Collections.sort(allStudents);
        LOGGER.config("Loading Input Data to the Preview!");

        this.inputTable.setItems(FXCollections.observableArrayList(allStudents));
        this.inputTable.sort();
        this.inputTable.refresh();
    }
}
