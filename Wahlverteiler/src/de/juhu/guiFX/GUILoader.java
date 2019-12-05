package de.juhu.guiFX;

import static de.juhu.util.References.LOGGER;
import static de.juhu.util.References.PROJECT_NAME;
import static de.juhu.util.References.VERSION;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
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
		try {
			if (!Files.exists(
					FileSystems.getDefault().getPath(System.getenv("localappdata") + "/CaRP/language.properties"),
					LinkOption.NOFOLLOW_LINKS)) {
				if (!Files.exists(FileSystems.getDefault().getPath(System.getenv("localappdata") + "/CaRP/"),
						LinkOption.NOFOLLOW_LINKS))
					new File(System.getenv("localappdata") + "/CaRP/").mkdir();

				Files.copy(getClass().getResourceAsStream("/assets/language/de.properties"),
						FileSystems.getDefault().getPath(System.getenv("localappdata") + "/CaRP/language.properties"));
			}
		} catch (Exception e) {
			FileReader reader;
			References.language = new PropertyResourceBundle(reader = new FileReader("/assets/language/de.properties"));
			reader.close();
		}
		this.loadLanguage();

		if (References.language.containsKey("version")) {
			FileReader reader = null;
			InputStreamReader reader2 = null;
			if (References.language.getString("version").equalsIgnoreCase(
					new PropertyResourceBundle((new File("./resources/assets/language/de.properties").exists()
							? reader = new FileReader("./resources/assets/language/de.properties")
							: (reader2 = new InputStreamReader(
									References.class.getResourceAsStream("/assets/language/de.properties")))))
											.getString("version"))) {
				if (reader != null)
					reader.close();
				if (reader2 != null)
					reader2.close();

				if (!Files.exists(FileSystems.getDefault().getPath(System.getenv("localappdata") + "/CaRP/"),
						LinkOption.NOFOLLOW_LINKS))
					new File(System.getenv("localappdata") + "/CaRP/").mkdir();

				if (Files.exists(
						FileSystems.getDefault().getPath(System.getenv("localappdata") + "/CaRP/language.properties"),
						LinkOption.NOFOLLOW_LINKS))
					Files.delete(FileSystems.getDefault()
							.getPath(System.getenv("localappdata") + "/CaRP/language.properties"));

				Files.copy(getClass().getResourceAsStream("/assets/language/de.properties"),
						FileSystems.getDefault().getPath(System.getenv("localappdata") + "/CaRP/language.properties"));

				this.loadLanguage();
			}
		} else {
			if (!Files.exists(FileSystems.getDefault().getPath(System.getenv("localappdata") + "/CaRP/"),
					LinkOption.NOFOLLOW_LINKS))
				new File(System.getenv("localappdata") + "/CaRP/").mkdir();

			if (Files.exists(
					FileSystems.getDefault().getPath(System.getenv("localappdata") + "/CaRP/language.properties"),
					LinkOption.NOFOLLOW_LINKS))
				Files.delete(
						FileSystems.getDefault().getPath(System.getenv("localappdata") + "/CaRP/language.properties"));

			Files.copy(getClass().getResourceAsStream("/assets/language/de.properties"),
					FileSystems.getDefault().getPath(System.getenv("localappdata") + "/CaRP/language.properties"));

			this.loadLanguage();
		}

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

		if (References.language != null)
			return;
		try {
			FileReader reader;
			References.language = new PropertyResourceBundle(
					reader = new FileReader(System.getenv("localappdata") + "/CaRP/language.properties"));
			reader.close();
		} catch (IOException e) {
			try {
				References.language = new PropertyResourceBundle(new FileReader("/assets/language/de.properties"));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		LOGGER.info("Starte: " + PROJECT_NAME + " | Version: " + VERSION);

		launch(args);
	}

}
