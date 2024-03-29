package de.juhu.util;

import de.juhu.distributor.Course;
import de.juhu.util.events.WindowCreatedEvent;
import de.juhu.util.events.WindowUpdateEvent;
import de.noisruker.event.EventManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;

import static de.noisruker.logger.Logger.LOGGER;

/**
 * Beinhaltet nützliche Methoden
 *
 * @author Fabius
 * @category Util
 */
public class Util {

    public static void openLink(String url) {
        String os = System.getProperty("os.name").toLowerCase();
        Runtime rt = Runtime.getRuntime();
        try {
            if (os.contains("win")) {
                rt.exec(new String[]{"rundll32", "url.dll,FileProtocolHandler", url});
            } else if (os.contains("mac")) {
                rt.exec(new String[] {"open", url});
            } else if (os.contains("nix") || os.contains("nux")) {
                String[] browsers = {"epiphany", "firefox", "mozilla", "konqueror",
                        "netscape", "opera", "links", "lynx"};
                StringBuilder cmd = new StringBuilder();
                for (int i = 0; i < browsers.length; i++)
                    cmd.append(i == 0 ? "" : " || ").append(browsers[i]).append(" \"").append(url).append("\" ");

                rt.exec(new String[]{"sh", "-c", cmd.toString()});

            } else {
                LOGGER.log(Level.SEVERE, "Can not browse link!");
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Can not browse link!", ex);
        }
        LOGGER.info("Link opened");
    }

    /**
     * @param input
     * @return
     */
    public static boolean isBlank(String input) {
        return input == null || input.isEmpty() || input.trim().isEmpty();
    }

    public static double round(double toRound, int col) {
        return ((Math.round(Math.pow(10.0d, col) * toRound)) / Math.pow(10.0d, col));
    }

    /**
     * @param toCheck
     * @param strings
     * @return
     */
    public static boolean endsWith(String toCheck, String... strings) {
        for (String s : strings) {
            if (toCheck.endsWith(s))
                return true;
        }
        return false;
    }

    /**
     * @param line
     * @return
     */
    public static String[] removeFirst(String[] line) {
        String[] newline = new String[line.length - 1];

        for (int i = 1; i < line.length; i++) {
            newline[i - 1] = line[i];
        }

        return newline;
    }

    /**
     * @param courses
     * @return
     */
    public static int maxStudentCount(List<Course> courses) {
        int maxCount = 0;

        for (Course c : courses)
            maxCount = maxCount >= c.getStudents().size() ? maxCount : c.getStudents().size();

        return maxCount;
    }

    /**
     * @param name
     * @return
     */
    public static boolean isIgnoreCourse(String... name) {

        for (String s : Config.ignoreStudent.split("\\|"))
            for (String s1 : name)
                if (s.equalsIgnoreCase(s1))
                    return true;

        String connect = "";
        for (String s1 : name) {
            connect += s1;
        }
        return Config.ignoreStudent.replaceAll("|", "").equalsIgnoreCase(connect);
    }

    public static Stage updateWindow(Stage stage, String resourceLocation) {
        Parent root;
        boolean maximized = stage.isMaximized();
        try {
            root = FXMLLoader.load(Objects.requireNonNull(Util.class.getResource(resourceLocation)), References.language);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Start new Window fail", e);
            return stage;
        }

        Scene s = new Scene(root);

        EventManager.getInstance().triggerEvent(new WindowUpdateEvent(stage, s));


        stage.setScene(s);

        stage.centerOnScreen();
        stage.setMaximized(false);
        stage.setMaximized(maximized);

        return stage;
    }

    /**
     * @param resourceLocation
     * @param title
     * @param parent
     * @return
     */
    public static Stage openWindow(String resourceLocation, String title, Stage parent) {
        Stage primaryStage = new Stage();

        Image i = new Image(Util.class.getResource("/assets/textures/logo/CaRP.png").toExternalForm());
        Parent root = null;

        try {
            root = FXMLLoader.load(Util.class.getResource(resourceLocation), References.language);
        } catch (IOException e) {
            return null;
        }
        Scene s = new Scene(root);

        EventManager.getInstance().triggerEvent(new WindowCreatedEvent(primaryStage, s));

        primaryStage.setMinWidth(200);
        primaryStage.setMinHeight(158);
        primaryStage.setTitle(title);
        primaryStage.setScene(s);
        primaryStage.initModality(Modality.WINDOW_MODAL);
        if (parent != null)
            primaryStage.initOwner(parent);
        primaryStage.initStyle(StageStyle.DECORATED);

        primaryStage.getIcons().add(i);
        primaryStage.centerOnScreen();

        primaryStage.show();

        return primaryStage;
    }

}
