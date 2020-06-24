package de.juhu.util;

import java.awt.Component;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.List;

import de.juhu.distributor.Course;
import de.juhu.guiFX.Theme;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Beinhaltet nützliche Methoden
 * 
 * @author Fabius
 * @category Util
 */
public class Util {

	@Deprecated
	private static void setPositionCorrectly(Component c, double xRelation, double yRelation, double widthRelation,
			double heightRelation, double newWindowWidth, double newWindowHeight) {
		double dx = xRelation * newWindowWidth, dy = yRelation * newWindowHeight,
				dwidth = widthRelation * newWindowWidth, dheight = heightRelation * newWindowHeight;

		int x = (int) dx, y = (int) dy, width = (int) dwidth, height = (int) dheight;

		if (isReliable(x / (int) newWindowWidth, xRelation) && isReliable(y / (int) newWindowHeight, yRelation)
				&& isReliable(width / (int) newWindowWidth, widthRelation)
				&& isReliable(height / (int) newWindowHeight, heightRelation))
			c.setBounds(x, y, width, height);

	}

	@Deprecated
	private static boolean isReliable(double d1, double d2) {
		return d1 == d2 || ((d1 + 2) > d2 && (d1 - 2) < d2);
	}

	@Deprecated
	public static void setPositionCorrectly(Component jC, Rectangle c, int newWindowWidth, int newWindowHeight) {
		Util.setPositionCorrectly(jC, c.x, c.y, c.width, c.height, newWindowWidth, newWindowHeight);
	}

	@Deprecated
	public static void setPositionCorrectly(Component c, Rectangle relationBounds, Rectangle windowBounds) {
		Util.setPositionCorrectly(c, relationBounds.x, relationBounds.y, relationBounds.width, relationBounds.height,
				windowBounds.width, windowBounds.height);
	}

	@Deprecated
	public static void setPositionCorrectlyWithWindowSettings(Component c, Rectangle previousWindowBounds,
			Rectangle windowBounds) {
		Rectangle unrelatedBounds = c.getBounds();
		Util.setPositionCorrectly(c, (double) unrelatedBounds.x / (double) previousWindowBounds.width,
				(double) unrelatedBounds.y / (double) previousWindowBounds.height,
				(double) unrelatedBounds.width / (double) previousWindowBounds.width,
				(double) unrelatedBounds.height / (double) previousWindowBounds.height, windowBounds.width,
				windowBounds.height);
	}

	@Deprecated
	public static void setPositionWithStartPosition(Component c, Rectangle startPosition, Rectangle windowPosition,
			Rectangle startWindow) {
		double x = ((double) startPosition.x / (double) startWindow.width) * windowPosition.width,
				y = ((double) startPosition.y / (double) startWindow.height) * windowPosition.height,
				width = ((double) startPosition.width / (double) startWindow.width) * windowPosition.width,
				height = ((double) startPosition.height / (double) startWindow.height) * windowPosition.height;
		c.setBounds((int) x, (int) y, (int) width, (int) height);
	}

	@Deprecated
	public static int quad(int i, int height) {
		if (height < 0)
			return 1 / quad(i, Math.abs(height));
		if (height == 0)
			return 0;
		if (height == 1)
			return i;
		return i * quad(i, height - 1);
	}

	/**
	 * 
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
	 * 
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
	 * 
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
	 * 
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
	 * 
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

	/**
	 * 
	 * @param resourceLocation
	 * @param title
	 * @param parent
	 * @param darkTheme
	 * @return
	 */
	public static Stage openWindow(String resourceLocation, String title, Stage parent, Theme theme) {
		Stage primaryStage = new Stage();

		Image i;

		if (new File("./resources/assets/textures/logo/KuFA.png").exists())
			i = new Image(new File("./resources/assets/textures/logo/KuFA.png").toURI().toString());
		else
			i = new Image("/assets/textures/logo/KuFA.png");
		Parent root = null;

		try {
			root = FXMLLoader.load(Util.class.getClass().getResource(resourceLocation), References.language);
		} catch (IOException e) {
			return null;
		}
		Scene s = new Scene(root);
		if (!theme.getLocation().equalsIgnoreCase("remove")) {
			s.getStylesheets().add(theme.getLocation());
		}

		primaryStage.setMinWidth(200);
		primaryStage.setMinHeight(158);
		primaryStage.setTitle(title);
		primaryStage.setScene(s);
		primaryStage.initModality(Modality.WINDOW_MODAL);
		if (parent != null)
			primaryStage.initOwner(parent);
		primaryStage.initStyle(StageStyle.DECORATED);

		primaryStage.getIcons().add(i);

		primaryStage.show();

		return primaryStage;
	}

}
