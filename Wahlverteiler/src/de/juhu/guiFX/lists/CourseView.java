package de.juhu.guiFX.lists;

import de.juhu.distributor.Course;
import de.juhu.distributor.Distributor;
import de.juhu.util.References;
import javafx.collections.FXCollections;
import javafx.scene.control.TableView;

public class CourseView {
	TableView inputTable;

	public CourseView(TableView<Course> inputTable) {
		this.inputTable = inputTable;
	}

	public void fill() {

		References.LOGGER.config("Loading Input Data to the Preview!");
		this.inputTable.getItems().clear();
		this.inputTable.setItems(FXCollections.observableArrayList(Distributor.getInstance().getCourses()));
		this.inputTable.sort();
	}
}
