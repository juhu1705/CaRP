package de.juhu.guiFX;

import static de.juhu.util.References.PROJECT_NAME;
import static de.juhu.util.References.VERSION;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.util.Objects;
import java.util.PropertyResourceBundle;

import de.juhu.util.Config;
import de.juhu.util.References;
import de.juhu.util.events.WindowUpdateEvent;
import de.noisruker.event.EventManager;
import de.noisruker.logger.Logger;
import de.noisruker.logger.Settings;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;

/**
 * Diese Klasse stellt die Hauptklasse des CaRP-Assigners da. Von hier werden
 * die Startprozesse eingeleitet und die Sprache geladen.
 * 
 * @author Juhu1705
 * @category GUI
 * @version 1.0
 */
public class GUILoader extends Application {

	private static Stage primaryStage;
	public static Stage secondaryStage;
	public static Scene scene;
	private static File toLoad;

	@Override
	public void start(Stage primaryStage) throws Exception {
//		LOGGER.info(getClass().getResource("/de/juhu/guiFX/GUI.fxml") + "");
//		LOGGER.info(new File("./de/juhu/guiFX/GUI.fxml").toURI() + "");
//		LOGGER.info(new File("./de/juhu/guiFX/GUI.fxml").toURI().toURL() + "");
		GUILoader.primaryStage = primaryStage;

		Image i;

		if (new File("./resources/assets/textures/logo/KuFA.png").exists())
			i = new Image(new File("./resources/assets/textures/logo/KuFA.png").toURI().toString());
		else
			i = new Image("/assets/textures/logo/KuFA.png");

		System.out.println(References.language);
		System.out.println(getClass().getResource("/assets/layouts/GUI.fxml"));
		Parent root = FXMLLoader.load(getClass().getResource("/assets/layouts/GUI.fxml"), References.language);

		Scene s = new Scene(root);

		EventManager.getInstance().triggerEvent(new WindowUpdateEvent(primaryStage, s));

		GUIManager.getInstance().checks.forEach((themes, checkbox) -> {
			checkbox.setSelected(false);
		});

		//GUIManager.getInstance().checks.get(GUIManager.getInstance().theme).setSelected(true);

		primaryStage.setMinWidth(1400);
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
	}

	public void starting2() throws IOException {

		secondaryStage.show();
		primaryStage.close();
		GUILoader.primaryStage = secondaryStage;
	}

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

}
