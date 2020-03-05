package de.juhu.guiFX.lists;

import de.juhu.distributor.Student;
import de.juhu.guiFX.GUIManager;
import de.juhu.util.References;
import javafx.collections.FXCollections;
import javafx.scene.control.TableView;

/**
 * Behandelt die Tabelle zur Ansicht der verteilten Schüler.
 * 
 * @author Juhu1705
 * @category GUI
 */
public class OutputStudentsView implements Runnable {

	TableView tv;

	public OutputStudentsView(TableView<Student> inputTable) {
		this.tv = inputTable;
	}

	public void fill() {

		References.LOGGER.config("Loading Output Data to the Preview!");

		this.tv.setItems(FXCollections.observableArrayList(GUIManager.actual.getAllStudents()));
		this.tv.sort();
	}

	@Override
	public void run() {
		fill();
		GUIManager.getInstance().students.setDisable(false);
	}

}
