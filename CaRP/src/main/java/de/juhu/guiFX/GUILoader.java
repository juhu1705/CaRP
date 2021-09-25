package de.juhu.guiFX;

import de.juhu.util.Config;
import de.juhu.util.References;
import de.juhu.util.events.WindowUpdateEvent;
import de.noisruker.event.EventManager;
import de.noisruker.logger.Logger;
import de.noisruker.logger.Settings;
import de.noisruker.logger.events.LogReceivedErrorEvent;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.controlsfx.control.Notifications;

import java.io.File;
import java.util.Objects;

import static de.juhu.util.References.PROJECT_NAME;
import static de.juhu.util.References.VERSION;

/**
 * Diese Klasse stellt die Hauptklasse des CaRP-Assigners da. Von hier werden
 * die Startprozesse eingeleitet und die Sprache geladen.
 *
 * @author Juhu1705
 * @version 1.0
 * @category GUI
 */
public class GUILoader extends Application {

    public static Stage secondaryStage;
    public static Scene scene;
    private static Stage primaryStage;
    private static File toLoad;

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        Settings.PROGRAMM_FOLDER = "/.CaRP/";
        Settings.LOGGER_NAME = PROJECT_NAME;

        Logger.LOGGER.info("Starte: " + PROJECT_NAME + " | Version: " + VERSION);

        Config.register();

        for (String arg : args) {
            if (new File(arg).exists())
                GUILoader.toLoad = new File(arg);
        }

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        GUILoader.primaryStage = primaryStage;

        Image i;

        if (new File("./resources/assets/textures/logo/KuFA.png").exists())
            i = new Image(new File("./resources/assets/textures/logo/KuFA.png").toURI().toString());
        else
            i = new Image("/assets/textures/logo/KuFA.png");

        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/assets/layouts/GUI.fxml")), References.language);

        Scene s = new Scene(root);

        EventManager.getInstance().triggerEvent(new WindowUpdateEvent(primaryStage, s));

        GUIManager.getInstance().checks.forEach((themes, checkbox) -> {
            checkbox.setSelected(false);
        });

        if (GUIManager.getInstance().checks.get(Config.theme) != null)
            GUIManager.getInstance().checks.get(Config.theme).setSelected(true);

        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(580);
        primaryStage.setTitle(PROJECT_NAME + " | " + VERSION);
        primaryStage.setScene(s);
        primaryStage.setOnCloseRequest(c -> {
            GUIManager.getInstance().close(null);
        });

        primaryStage.centerOnScreen();

        primaryStage.initStyle(StageStyle.DECORATED);

        primaryStage.getIcons().add(i);

        primaryStage.show();

        scene = s;

        if (GUILoader.toLoad != null) {
            GUIManager.getInstance().load(GUILoader.toLoad.getPath());
            GUIManager.getInstance().inputView.fill();
            GUIManager.getInstance().cView.fill();
        }

        EventManager.getInstance().registerEventListener(LogReceivedErrorEvent.class, errorEvent -> {
            String message = errorEvent.getRawMessage();
            String cause = errorEvent.getThrown() != null ? errorEvent.getThrown().getLocalizedMessage() : "";

            if (Objects.equals(cause, ""))
                Platform.runLater(() -> Notifications.create().darkStyle().title(errorEvent.getRecord().getLevel().toString())
                        .text(message).owner(GUILoader.getPrimaryStage())
                        .showError());
            else
                Platform.runLater(() -> Notifications.create().darkStyle().title(message)
                        .text(cause).owner(GUILoader.getPrimaryStage())
                        .showError());
        });
    }

}
