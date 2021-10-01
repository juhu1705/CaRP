package de.juhu.guiFX;

import de.juhu.util.References;
import de.juhu.util.Util;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;
import java.util.logging.Level;

import static de.noisruker.logger.Logger.LOGGER;

/**
 * Behandelt alle Aktionen des About Fensters.
 *
 * @author Juhu1705
 * @category GUI
 */
public class AboutManager implements Initializable {

    @FXML
    public Label version;

    @FXML
    public TextField weg;

    public void openLink(ActionEvent event) {
        if (event.getSource() instanceof Hyperlink) {
            Hyperlink link = (Hyperlink) event.getSource();

            Util.openLink(link.getText());
        }

    }

    public void startMail(ActionEvent event) {
        if (event.getSource() instanceof Hyperlink) {
            Hyperlink link = (Hyperlink) event.getSource();

            Util.openLink("mailto:" + link.getText() + "?" + "Need_Help:CaRP-Assigner");
        }
    }

    public void openLSPage(MouseEvent event) {
        Util.openLink("https://www.luisenschule-mh.de/");
    }

    public void onHelpSearch(ActionEvent event) {
        try {
            Util.openLink("https://github.com/juhu1705/CaRP/issues?utf8=%E2%9C%93&q="
                    + URLEncoder.encode(weg.getText(), StandardCharsets.UTF_8.toString()));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "https://github.com/juhu1705/CaRP/issues?utf8=%E2%9C%93&q=" + weg.getText(), e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        version.setText("Version: " + References.VERSION);
    }
}
