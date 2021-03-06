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
import javafx.scene.input.MouseEvent;

/**
 * Behandelt alle Aktionen des About Fensters.
 * 
 * @author Juhu1705
 * @category GUI
 */
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

	public void openLSPage(MouseEvent event) {
		try {
			if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
				Desktop.getDesktop().browse(new URI("https://www.luisenschule-mh.de/"));
			}
		} catch (IOException | URISyntaxException e) {
			References.LOGGER.info("https://www.luisenschule-mh.de/");
		}
	}

	public void onHelpSearch(ActionEvent event) {
		try {
			if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
				Desktop.getDesktop().browse(new URI("https://github.com/juhu1705/CaRP/issues?utf8=%E2%9C%93&q="
						+ URLEncoder.encode(weg.getText(), StandardCharsets.UTF_8.toString())));
			}
		} catch (IOException | URISyntaxException e) {
			References.LOGGER.info("https://github.com/juhu1705/CaRP/issues?utf8=%E2%9C%93&q=" + weg.getText());
		}
	}
}
