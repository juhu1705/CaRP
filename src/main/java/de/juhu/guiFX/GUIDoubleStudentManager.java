package de.juhu.guiFX;

import de.juhu.util.Config;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.controlsfx.control.ToggleSwitch;

import java.net.URL;
import java.util.ResourceBundle;

import static de.noisruker.logger.Logger.LOGGER;

/**
 * Behandelt alle Aktionen des Fensters, dass erscheint, wenn ein doppelter
 * Schüler beim Importieren einer Datei auftritt.
 *
 * @author Juhu1705
 * @category GUI
 */
public class GUIDoubleStudentManager implements Initializable {

    public static String sName = "", sPrename = "";
    public static boolean finished = false;

    @FXML
    public TextField name, prename;

    @FXML
    public ToggleSwitch shouldMemorice;

    public void skip(ActionEvent event) {
        Config.rememberDecision = shouldMemorice.isSelected();

        Config.allowDoubleStudents = false;

        GUIDoubleStudentManager.finished = true;

        ((Stage) ((Button) event.getSource()).getScene().getWindow()).close();
    }

    public void add(ActionEvent event) {
        Config.rememberDecision = shouldMemorice.isSelected();

        LOGGER.info(Config.rememberDecision + "");

        Config.allowDoubleStudents = true;

        GUIDoubleStudentManager.finished = true;

        ((Stage) ((Button) event.getSource()).getScene().getWindow()).close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        if (!sName.isEmpty())
            name.setText(sName);

        if (!sPrename.isEmpty())
            prename.setText(sPrename);

    }

}
