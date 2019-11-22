package de.juhu.guiFX;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class ErrorGuiController implements Initializable {

	public static String headline, information;

	@FXML
	public TextArea tinformation, theadline;

	public void onOK(ActionEvent event) {
		((Stage) ((Button) event.getSource()).getScene().getWindow()).close();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.theadline.setText(headline);

		this.tinformation.setText(information);
	}

}
