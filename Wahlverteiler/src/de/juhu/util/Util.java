package de.juhu.util;

import static de.juhu.util.References.RAND_GEN;

import java.awt.Component;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import de.juhu.distributor.Course;
import de.juhu.distributor.Student;

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

	public static void setPositionWithStartPosition(Component c, Rectangle startPosition, Rectangle windowPosition,
			Rectangle startWindow) {
		double x = ((double) startPosition.x / (double) startWindow.width) * windowPosition.width,
				y = ((double) startPosition.y / (double) startWindow.height) * windowPosition.height,
				width = ((double) startPosition.width / (double) startWindow.width) * windowPosition.width,
				height = ((double) startPosition.height / (double) startWindow.height) * windowPosition.height;
		c.setBounds((int) x, (int) y, (int) width, (int) height);
	}

	public static ArrayList<Student> randomize(ArrayList<Student> s) {

		int size = s.size();

		ArrayList<Student> randomized = new ArrayList<>(size);

		for (int i = 0; i < size; i++) {
			randomized.add(s.remove(RAND_GEN.nextInt(s.size())));
		}

		return randomized;
	}

	public static int quad(int i, int height) {
		if (height < 0)
			return 1 / quad(i, Math.abs(height));
		if (height == 0)
			return 0;
		if (height == 1)
			return i;
		return i * quad(i, height - 1);
	}

	public static boolean isBlank(String input) {
		return input == null || input.isEmpty() || input.trim().isEmpty();
	}

	public static boolean endsWith(String toCheck, String... strings) {
		for (String s : strings) {
			if (toCheck.endsWith(s))
				return true;
		}
		return false;
	}

	public static String[] removeFirst(String[] line) {
		String[] newline = new String[line.length - 1];

		for (int i = 1; i < line.length; i++) {
			newline[i - 1] = line[i];
		}

		return newline;
	}

	public static int maxStudentCount(List<Course> courses) {
		int maxCount = 0;

		for (Course c : courses)
			maxCount = maxCount >= c.getStudents().size() ? maxCount : c.getStudents().size();

		return maxCount;
	}

	public static boolean isIgnoreCourse(String name) {
		for (String s : Config.ignoreStudent.split("|"))
			if (s.equalsIgnoreCase(name))
				return true;

		return false;
	}

}
