package de.juhu.gui.frames;

import static de.juhu.util.References.LOGGER;
import static de.juhu.util.References.PROJECT_NAME;
import static de.juhu.util.References.VERSION;
import static de.juhu.util.Util.setPositionWithStartPosition;
import static java.lang.Double.parseDouble;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.border.EmptyBorder;

import de.juhu.dateimanager.ExcelImporter;
import de.juhu.dateimanager.WriteableContent;
import de.juhu.gui.action.ActionListenerController;
import de.juhu.gui.action.activations.Content;
import de.juhu.gui.action.activations.ContentHider;
import de.juhu.util.Util;
import de.juhu.util.Interfaces.Renderable;

public abstract class AbstractFrame extends JFrame implements Renderable {

	protected HashMap<String, JButton> buttons = new HashMap<>();
	protected HashMap<String, JProgressBar> progressBars = new HashMap<>();
	protected HashMap<String, JPanel> panels = new HashMap<>();
	protected HashMap<String, JLabel> labels = new HashMap<>();
	protected HashMap<String, JTextPane> textPanes = new HashMap<>();
	protected HashMap<String, JSpinner> spinners = new HashMap<>();
	protected HashMap<String, JToolBar> bars = new HashMap<>();
	protected HashMap<String, JTextField> textFields = new HashMap<>();
	protected HashMap<String, JTable> tables = new HashMap<>();
	protected HashMap<String, Rectangle> bounds = new HashMap<>();
	protected HashMap<String, JTabbedPane> tabs = new HashMap<>();

	protected static AbstractFrame frame = null;

	public static AbstractFrame getFrame() {
		return AbstractFrame.frame;
	}

	protected Rectangle startBound;

	protected String configFile;

	public AbstractFrame(String configFile) {
		super(PROJECT_NAME + " | " + VERSION);
		this.configFile = configFile;
	}

	protected void loadXLSX(String file) {
		List<WriteableContent> wcl;
		try {
			wcl = ExcelImporter.readXLSXImproved(file);
		} catch (IOException | URISyntaxException e) {
			LOGGER.log(Level.SEVERE, "Unable to load GUI", e);
			return;
		}
		for (WriteableContent wc : wcl) {
			if (wc.getName().equals("Panels")) {
				int iterator = 0;
				String[][] grid = wc.getGrid();
				for (String[] line : grid) {
					if (line.length < 4 || line[0].contains("#"))
						continue;
					JPanel p = new JPanel();
					p.setBackground(Color.BLACK);
					this.tabs.get(line[3]).insertTab(line[1], null, p, line[2], iterator++);
					p.setLayout(null);
					this.addPanel(line[0], p);
				}
			}
		}

		for (WriteableContent wc : wcl) {
			switch (wc.getName()) {
			case "Buttons":
				String[][] gridB = wc.getGrid();
				for (String[] line : gridB) {
					if (line.length < 8 || line[0].contains("#"))
						continue;
					JButton b = new JButton(line[1]);
					b.addActionListener(ActionListenerController.getInstance().getActionListener(line[7]));
					b.setBounds(this.getBounds(new String[] { line[3], line[4], line[5], line[6] }));
					if (line.length < 9 || line[8] == null || Util.isBlank(line[8])) {
						this.panels.get(line[2]).add(b);
						this.addButton(line[0], b);
					} else {
						ContentHider.getInstance()
								.register(new Content(b, line[0], line[2], false, this, line[8].split("&")));
					}
				}
				break;
			case "Labels":
				String[][] gridL = wc.getGrid();
				for (String[] line : gridL) {
					if (line.length < 8 || line[0].contains("#"))
						continue;
					JLabel l = new JLabel(line[1]);
					l.setForeground(Color.getColor(line[7]));
					l.setBounds(this.getBounds(new String[] { line[3], line[4], line[5], line[6] }));
					this.panels.get(line[2]).add(l);
					this.addLabel(line[0], l);
				}
				break;
			case "Text Panes":
				String[][] gridTP = wc.getGrid();
				for (String[] line : gridTP) {
					if (line.length < 8 || line[0].contains("#"))
						continue;
					// TODO Text Panes einfügen
				}
				break;
			case "Spinner":
				String[][] gridS = wc.getGrid();
				for (String[] line : gridS) {
					if (line.length < 8 || line[0].contains("#"))
						continue;
					JSpinner b = new JSpinner();
					// TODO Spinner einfügen
					b.setBounds(this.getBounds(new String[] { line[3], line[4], line[5], line[6] }));
					this.panels.get(line[2]).add(b);
					this.addSpinner(line[0], b);
				}
				break;
			case "Text Fields":
				String[][] gridTF = wc.getGrid();
				for (String[] line : gridTF) {
					if (line.length < 8 || line[0].contains("#"))
						continue;
					JTextField b = new JTextField(line[1]);
					b.setEditable(Boolean.getBoolean(line[3]));
					b.setBounds(this.getBounds(new String[] { line[4], line[5], line[6], line[7] }));
					b.setColumns(10);
					this.panels.get(line[2]).add(b);
					this.addTextField(line[0], b);
				}
				break;
			}
		}

	}

	Rectangle getBounds(String... strings) {
		if (strings.length < 4)
			return null;
		return new Rectangle((int) parseDouble(strings[0]), (int) parseDouble(strings[1]),
				(int) parseDouble(strings[2]), (int) parseDouble(strings[3]));
	}

	protected void createBasicPanel() {
		JPanel panel = new JPanel();
		this.addPanel("basic", panel);
		panel.setForeground(Color.WHITE);
		panel.setBackground(Color.BLACK);
		panel.setBorder(new EmptyBorder(5, 5, 5, 5));
		panel.setLayout(new BorderLayout(0, 0));
		setContentPane(panel);

		JTabbedPane tabsController = new JTabbedPane(JTabbedPane.TOP);

		this.addTabController("basic", tabsController);

		panel.add(tabsController, BorderLayout.CENTER);
	}

	public void addTabController(String name, JTabbedPane tab) {
		this.tabs.put(name, tab);
	}

	public void addTabel(String name, JTable table) {
		this.tables.put(name, table);
	}

	public void addButton(String name, JButton button) {
		this.buttons.put(name, button);
		this.bounds.put(name, button.getBounds());
	}

	public void addLabel(String name, JLabel label) {
		this.labels.put(name, label);
		this.bounds.put(name, label.getBounds());
	}

	public void addBar(String name, JToolBar bar) {
		this.bars.put(name, bar);
		this.bounds.put(name, bar.getBounds());
	}

	public void addPanel(String name, JPanel panel) {
		this.panels.put(name, panel);
	}

	public void addTextPane(String name, JTextPane textPane) {
		this.textPanes.put(name, textPane);
		this.bounds.put(name, textPane.getBounds());
	}

	public void addSpinner(String name, JSpinner spinner) {
		this.spinners.put(name, spinner);
		this.bounds.put(name, spinner.getBounds());
	}

	public void addTextField(String name, JTextField textField) {
		this.textFields.put(name, textField);
		this.bounds.put(name, textField.getBounds());
	}

	public JTextField getTextField(String name) {
		return this.textFields.get(name);
	}

	public JProgressBar getProgressField(String name) {
		return this.progressBars.get(name);
	}

	@Override
	public void render() {

		for (Entry<String, JButton> e : this.buttons.entrySet()) {
			setPositionWithStartPosition(e.getValue(), this.bounds.get(e.getKey()), super.getBounds(), this.startBound);
		}
		for (Entry<String, JToolBar> e : this.bars.entrySet()) {
			setPositionWithStartPosition(e.getValue(), this.bounds.get(e.getKey()), super.getBounds(), this.startBound);
		}
		for (Entry<String, JLabel> e : this.labels.entrySet()) {
			setPositionWithStartPosition(e.getValue(), this.bounds.get(e.getKey()), super.getBounds(), this.startBound);
		}
		for (Entry<String, JTextField> e : this.textFields.entrySet()) {
			setPositionWithStartPosition(e.getValue(), this.bounds.get(e.getKey()), super.getBounds(), this.startBound);
		}
		for (Entry<String, JTextPane> e : this.textPanes.entrySet()) {
			setPositionWithStartPosition(e.getValue(), this.bounds.get(e.getKey()), super.getBounds(), this.startBound);
		}
		for (Entry<String, JSpinner> e : this.spinners.entrySet()) {
			setPositionWithStartPosition(e.getValue(), this.bounds.get(e.getKey()), super.getBounds(), this.startBound);
		}
		for (Entry<String, JProgressBar> e : this.progressBars.entrySet()) {
			setPositionWithStartPosition(e.getValue(), this.bounds.get(e.getKey()), super.getBounds(), this.startBound);
		}
	}

	public abstract void load();

	public void addAndSortComponent(String id, String panelID, Component component) {
		this.panels.get(panelID).add(component);
		if (component instanceof JButton)
			this.addButton(id, (JButton) component);
		else if (component instanceof JTextField)
			this.addTextField(id, (JTextField) component);
		else if (component instanceof JToolBar)
			this.addBar(id, (JToolBar) component);
		else if (component instanceof JTextPane)
			this.addTextPane(id, (JTextPane) component);
		else if (component instanceof JLabel)
			this.addLabel(id, (JLabel) component);
		else if (component instanceof JSpinner)
			this.addSpinner(id, (JSpinner) component);
		else if (component instanceof JPanel)
			this.addPanel(id, (JPanel) component);
		else if (component instanceof JTable)
			this.addTabel(id, (JTable) component);
		else if (component instanceof JTabbedPane)
			this.addTabController(id, (JTabbedPane) component);
	}

	public void removeComponent(String id, String panelID, Component component) {
		this.panels.get(panelID).remove(component);
		this.bounds.remove(id);
		if (component instanceof JButton)
			this.buttons.remove(id, component);
		else if (component instanceof JTextField)
			this.textFields.remove(id, component);
		else if (component instanceof JToolBar)
			this.bars.remove(id, component);
		else if (component instanceof JTextPane)
			this.textPanes.remove(id, component);
		else if (component instanceof JLabel)
			this.labels.remove(id, component);
		else if (component instanceof JSpinner)
			this.spinners.remove(id, component);
		else if (component instanceof JPanel)
			this.panels.remove(id, component);
		else if (component instanceof JTable)
			this.tables.remove(id, component);
		else if (component instanceof JTabbedPane)
			this.tabs.remove(id, component);

	}

	public boolean hasTextField(String string) {
		return this.textFields.containsKey(string);
	}

}
