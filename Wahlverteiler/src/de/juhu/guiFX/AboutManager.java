package de.juhu.guiFX;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

import de.juhu.util.References;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;

public class AboutManager {

	@FXML
	public TextField weg;

	public void openLink(ActionEvent event) {
		if (event.getSource() instanceof Hyperlink) {
			Hyperlink link = (Hyperlink) event.getSource();

			try {
				Desktop.getDesktop().browse(new URI(link.getText()));
			} catch (IOException | URISyntaxException e) {
				References.LOGGER.log(Level.SEVERE, "Can not browse link!", e);
			}
		}

	}

	public void startMail(ActionEvent event) {
		if (event.getSource() instanceof Hyperlink) {
			Hyperlink link = (Hyperlink) event.getSource();

			try {
				Desktop.getDesktop().mail(new URI("mailto:" + link.getText() + "?" + "Need_Help:CaRP-Assigner"));
			} catch (IOException | URISyntaxException e) {
				References.LOGGER.log(Level.SEVERE, "Can not open new mail!", e);
			}
		}
	}

	public void onHelpSearch(ActionEvent event) {
		try {
			if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
				Desktop.getDesktop().browse(new URI("https://ecosia.org/search?q="
						+ URLEncoder.encode(weg.getText(), StandardCharsets.UTF_8.toString())));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
}
