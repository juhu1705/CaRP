package de.juhu.guiFX.lists;

import java.util.ArrayList;

import de.juhu.distributor.Distributor;
import de.juhu.distributor.Student;
import de.juhu.util.References;
import javafx.collections.FXCollections;
import javafx.scene.control.TableView;

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

		ArrayList calcStud = Distributor.getInstance().getCalcStudents();
		ArrayList ignStud = Distributor.getInstance().getIgnoreStudents();
		ArrayList allStudents = new ArrayList<>(calcStud);
		allStudents.addAll(ignStud);

		References.LOGGER.config("Loading Input Data to the Preview!");

		this.inputTable.getItems().clear();
		this.inputTable.setItems(FXCollections.observableArrayList(allStudents));
		this.inputTable.sort();
	}
}
