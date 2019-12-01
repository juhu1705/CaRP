package de.juhu.guiFX;

import static de.juhu.util.References.LOGGER;
import static de.juhu.util.References.PROJECT_NAME;
import static de.juhu.util.References.VERSION;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.util.Locale;
import java.util.PropertyResourceBundle;

import de.juhu.util.References;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * 
 * @author Fabius
 *
 */
public class GUILoader extends Application {

	private static Stage primaryStage;
	public static Stage secondaryStage;
	public static Scene scene;

	@Override
	public void start(Stage primaryStage) throws Exception {
		// LOGGER.info(getClass().getResource("/de/juhu/guiFX/GUI.fxml") + "");
//		LOGGER.info(new File("./de/juhu/guiFX/GUI.fxml").toURI() + "");
//		LOGGER.info(new File("./de/juhu/guiFX/GUI.fxml").toURI().toURL() + "");
		
		if (!Files.exists(FileSystems.getDefault().getPath(System.getenv("localappdata") + "/CaRP/language.properties"),
				LinkOption.NOFOLLOW_LINKS))
			Files.copy(getClass().getResourceAsStream("/assets/language/de.properties"), FileSystems.getDefault().getPath(System.getenv("localappdata") + "/CaRP/language.properties"));
		this.loadLanguage();
		
		Image i;

		if (new File("./resources/assets/textures/logo/KuFA.png").exists())
			i = new Image(new File("./resources/assets/textures/logo/KuFA.png").toURI().toString());
		else
			i = new Image("/assets/textures/logo/KuFA.png");
		
		Parent root = FXMLLoader.load(getClass().getResource("/assets/layouts/GUI.fxml"), References.language);
		Scene s = new Scene(root);

		s.getStylesheets().add("/assets/styles/dark_theme.css");

		primaryStage.setMinWidth(400);
		primaryStage.setMinHeight(310);
		primaryStage.setTitle(PROJECT_NAME + " | " + VERSION);
		primaryStage.setScene(s);
		primaryStage.setOnCloseRequest(c -> {
			GUIManager.getInstance().close(null);
		});

		primaryStage.centerOnScreen();
		primaryStage.initStyle(StageStyle.DECORATED);

		primaryStage.getIcons().add(i);

		primaryStage.show();
		GUILoader.primaryStage = primaryStage;
		scene = s;
	}

	public void starting2() throws IOException {

		secondaryStage.show();
		primaryStage.close();
		GUILoader.primaryStage = secondaryStage;
	}

	public static Stage getPrimaryStage() {
		return primaryStage;
	}
	
	private void loadLanguage() {
		
		try {
			References.language = new PropertyResourceBundle(new FileReader(System.getenv("localappdata") + "/CaRP/language.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		LOGGER.info("Starte: " + PROJECT_NAME + " | Version: " + VERSION);
		
		launch(args);
	}

}
