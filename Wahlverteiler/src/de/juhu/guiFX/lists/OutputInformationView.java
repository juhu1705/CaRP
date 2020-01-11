package de.juhu.guiFX.lists;

import java.util.ArrayList;
import java.util.HashMap;

import de.juhu.distributor.Student;
import de.juhu.guiFX.GUIManager;
import de.juhu.util.Config;
import de.juhu.util.References;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.TableColumn;

public class OutputInformationView implements Runnable {

	public ArrayList<TableColumn<Student, String>> scourses = new ArrayList<>();

	public void fill() {

		GUIManager.getInstance().bStudents
				.setItems(FXCollections.observableArrayList(GUIManager.actual.getInformation().getBStudents()));

		GUIManager.getInstance().bStudents.sort();

		HashMap<String, Double> ratess = new HashMap<>();

		ratess.put(References.language.getString("highestpriority.text"),
				Double.valueOf(GUIManager.actual.getInformation().getHighestPriority()));
		ratess.put(References.language.getString("calculationrate.text"),
				Double.valueOf(GUIManager.actual.getInformation().getRate()));
		ratess.put(References.language.getString("calculationgoodness.text"),
				Double.valueOf(GUIManager.actual.getInformation().getGuete()));
		ratess.put(References.language.getString("studentcount.text"),
				Double.valueOf(GUIManager.actual.getAllStudents().size()));
		ratess.put(References.language.getString("calculatedstudentcount.text"),
				Double.valueOf(GUIManager.actual.getInformation().getStudentCount()));
		ratess.put(References.language.getString("coursecount.text"),
				Double.valueOf(GUIManager.actual.getAllCourses().size()));

		GUIManager.getInstance().rates.getItems().clear();

		GUIManager.getInstance().rates.setItems(FXCollections.observableArrayList(ratess.entrySet()));

		GUIManager.getInstance().rates.sort();

		int[] priorities = GUIManager.actual.getInformation().getStudentPriorities();
		HashMap<Integer, Integer> p = new HashMap<>();

		for (int i = 0; i < priorities.length - 1; i++)
			p.put(Integer.valueOf(i + 1), Integer.valueOf(priorities[i]));

		p.put(Integer.valueOf(-1), Integer.valueOf(priorities[priorities.length - 1]));

		GUIManager.getInstance().priorities.setItems(FXCollections.observableArrayList(p.entrySet()));

		GUIManager.getInstance().priorities.sort();

		if (!GUIManager.actual.getInformation().getUStudents().isEmpty()) {

			if (!this.scourses.isEmpty()) {
				this.scourses.forEach(c -> GUIManager.getInstance().unallocatedStudents.getColumns().remove(c));
				this.scourses.clear();
			}

			for (int i = 1; i <= Config.maxChooses; i++) {
				TableColumn<Student, String> k = new TableColumn<>("Course " + i), s = new TableColumn<>("Subject"),
						t = new TableColumn<>("Teacher");
				k.getColumns().addAll(s, t);
				this.scourses.add(k);
				this.scourses.add(s);
				this.scourses.add(t);

				final int number = i;

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

				GUIManager.getInstance().unallocatedStudents.getColumns().add(k);
			}

			GUIManager.getInstance().unallocatedStudents.getItems().clear();

			// GUIManager.getInstance().unallocatedStudents.setItems(FXCollections.observableArrayList(new
			// ArrayList<>()));

			References.LOGGER.fine(GUIManager.actual.getInformation().getUStudents().toString());

			GUIManager.getInstance().unallocatedStudents.setItems(
					FXCollections.observableArrayList(GUIManager.actual.getInformation().getUStudents()));

			GUIManager.getInstance().unallocatedStudents.sort();
		} else
			GUIManager.getInstance().unallocatedStudents.getItems().clear();
	}

	@Override
	public void run() {
		References.LOGGER.info("Update Statistics!");

		fill();
		GUIManager.getInstance().statistics.setDisable(false);

		GUIManager.getInstance().p0.setVisible(false);

		GUIManager.getInstance().r1.setDisable(false);
		GUIManager.getInstance().r2.setDisable(false);
		GUIManager.getInstance().r3.setDisable(false);

		GUIManager.getInstance().b1.setDisable(false);
		GUIManager.getInstance().b2.setDisable(false);
		GUIManager.getInstance().b3.setDisable(false);
		GUIManager.getInstance().b4.setDisable(false);
		GUIManager.getInstance().b5.setDisable(false);
		GUIManager.getInstance().b6.setDisable(false);
	}

}
