package de.juhu.gui.frames;

import java.awt.Color;
import java.awt.Rectangle;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import de.juhu.util.Interfaces.Renderable;

public class ErrorFrame extends JFrame implements Renderable {

	private Rectangle startBound;
	private Throwable t;

	public ErrorFrame(Throwable t) {
		this.t = t;
		this.init();
	}

	@Override
	public void init() {

		this.startBound = new Rectangle(1000, 1000, 1000, 1000);
		super.setForeground(Color.BLACK);
		super.setIconImage(this.loadImage("logo/KuFA.png"));
		super.setResizable(true);
		super.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		super.setBounds(this.startBound);

		JPanel p = new JPanel();
		p.setBackground(Color.BLACK);

		JLabel l = new JLabel(t.getMessage());
		l.setForeground(Color.RED);
		p.add(l);

		JScrollPane scrollPane = new JScrollPane();

		for (char c : t.getCause().getMessage().toCharArray()) {
			JLabel label = new JLabel(c + "", JLabel.LEFT);
			label.setForeground(Color.WHITE);
			p.add(label);
		}

		this.add(p);
		super.setVisible(true);
		// LOGGER.info("Window sucsessfully created!");
		this.setBounds(new Rectangle(100, 100, 450, 300));
	}

	@Override
	public void render() {
		// TODO Auto-generated method stub

	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

}
