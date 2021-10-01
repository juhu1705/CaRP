package de.juhu.guiFX.lists;

import de.juhu.distributor.Student;
import de.juhu.guiFX.GUIManager;
import de.juhu.util.Config;
import de.juhu.util.References;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.TableColumn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static de.noisruker.logger.Logger.LOGGER;

/**
 * Behandelt alle unter Statistiken angezeigten Tabellen.
 *
 * @author Juhu1705
 * @category GUI
 */
public class OutputInformationView implements Runnable {

    public ArrayList<TableColumn<Student, String>> scourses = new ArrayList<>();

    public void fill() {
        GUIManager.getInstance().bStudents
                .setItems(FXCollections.observableArrayList(GUIManager.actual.getInformation().getBStudents()));

        GUIManager.getInstance().bStudents.sort();

        Map<String, String> ratess = new LinkedHashMap<>(5);

        ratess.put("0 - " + References.language.getString("calculationgoodness.text"),
                Double.toString(GUIManager.actual.getInformation().getGuete()));

        ratess.put("1 - " + References.language.getString("highestpriority.text"),
                GUIManager.actual.getHighestPriorityWhithoutIntegerMax() == -1 ? "Inf."
                        : Integer.toString(GUIManager.actual.getHighestPriorityWhithoutIntegerMax()));
//		ratess.put(References.language.getString("expectation.text"),
//				Double.valueOf(GUIManager.actual.getInformation().getExpectation()));
//		ratess.put(References.language.getString("standartDeviation.text"),
//				Double.valueOf(GUIManager.actual.getInformation().getStandartDeviation()));

        ratess.put("2 - " + References.language.getString("studentcount.text"),
                Integer.toString(GUIManager.actual.getAllStudents().size()));
        ratess.put("3 - " + References.language.getString("calculatedstudentcount.text"),
                Integer.toString(GUIManager.actual.getInformation().getStudentCount()));
        ratess.put("4 - " + References.language.getString("coursecount.text"),
                Integer.toString(GUIManager.actual.getAllCourses().size()));

        GUIManager.getInstance().rates.getItems().clear();

        GUIManager.getInstance().rates.setItems(FXCollections.observableArrayList(ratess.entrySet()));

        int[] priorities = GUIManager.actual.getInformation().getStudentPriorities();
        HashMap<String, Integer> p = new HashMap<>();

        for (int i = 0; i < priorities.length - 1; i++)
            p.put(Integer.toString(i + 1), priorities[i]);

        p.put("Inf.", priorities[priorities.length - 1]);

        GUIManager.getInstance().priorities.setItems(FXCollections.observableArrayList(p.entrySet()));

        GUIManager.getInstance().priorities.sort();

        if (!GUIManager.actual.getInformation().getUStudents().isEmpty()) {

            if (!this.scourses.isEmpty()) {
                this.scourses.forEach(c -> GUIManager.getInstance().unallocatedStudents.getColumns().remove(c));
                this.scourses.clear();
            }

            for (int i = 1; i <= Config.maxChooses; i++) {
                TableColumn<Student, String> k = new TableColumn<>(
                        References.language.getString("course.text") + " " + i),
                        s = new TableColumn<>(References.language.getString("subject.text")),
                        t = new TableColumn<>(References.language.getString("teacher.text"));
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

            GUIManager.getInstance().unallocatedStudents
                    .setItems(FXCollections.observableArrayList(GUIManager.actual.getInformation().getUStudents()));

            GUIManager.getInstance().unallocatedStudents.sort();
        } else
            GUIManager.getInstance().unallocatedStudents.getItems().clear();
    }

    @Override
    public void run() {
        LOGGER.info("Update Statistics!");

        fill();
        GUIManager.getInstance().statistics.setDisable(false);

        GUIManager.getInstance().p0.setVisible(false);

        GUIManager.getInstance().r1.setDisable(false);
        GUIManager.getInstance().r2.setDisable(false);
        GUIManager.getInstance().r3.setDisable(false);

        GUIManager.getInstance().b3.setDisable(false);
        GUIManager.getInstance().b6.setDisable(false);
        GUIManager.getInstance().b7.setDisable(false);
    }

}
