package de.juhu.util.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import de.juhu.math.Vec2i;

public class Keyboard implements KeyListener, MouseMotionListener, MouseListener {

	private static Vec2i mousePosition;
	private static ArrayList<Integer> keysPressed = new ArrayList<>(), keysTyped = new ArrayList<>(),
			mouseButtonsPressed = new ArrayList<>();
	private static int scrolloffset;

	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {

	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	@Override
	public void mouseDragged(MouseEvent e) {

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		Keyboard.mousePosition.set(e.getX(), e.getY());
	}

	@Override
	public void keyTyped(KeyEvent e) {
		Keyboard.keysTyped.add(Integer.valueOf(e.getKeyCode()));
	}

	@Override
	public void keyPressed(KeyEvent e) {
		Keyboard.keysPressed.add(Integer.valueOf(e.getKeyCode()));
	}

	@Override
	public void keyReleased(KeyEvent e) {
		Keyboard.keysPressed.remove(Integer.valueOf(e.getKeyCode()));
	}

}
