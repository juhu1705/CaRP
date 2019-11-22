package de.juhu.gui.frames;

import static de.juhu.util.References.LOGGER;
import static de.juhu.util.References.PROJECT_NAME;
import static de.juhu.util.References.VERSION;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.juhu.gui.action.ActionListenerController;
import de.juhu.gui.action.actions.InputSearchAction;
import de.juhu.gui.action.actions.OutputSearchAction;
import de.juhu.gui.action.actions.RunAction;
import de.juhu.gui.action.actions.RunAndSave;
import de.juhu.gui.action.actions.SaveAction;
import de.juhu.gui.action.activations.ActivationAction;
import de.juhu.gui.action.activations.ContentHider;
import de.juhu.util.Interfaces.Renderable;

public class GUIFrame extends AbstractFrame implements Renderable {
	private JTable table;

	private boolean moveStarted;
	private String configFile;

	public GUIFrame(String configFile) {
		super(PROJECT_NAME + " | " + VERSION);
		this.configFile = configFile;
		super.frame = this;
	}

	@Override
	public void init() {
		this.load();

		super.startBound = new Rectangle(1000, 1000, 1000, 1000);
		super.setForeground(Color.BLACK);
		super.setIconImage(this.loadImage("logo/KuFA.png"));
		super.setResizable(true);
		super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		super.setBounds(startBound);

		super.createBasicPanel();

		// this.createTableField();
		this.loadXLSX(this.configFile);

		super.progressBars.put("basic", new JProgressBar(0, 100));
		super.progressBars.get("basic").setBounds(20, 500, 881, 20);
		super.bounds.put("basic", new Rectangle(20, 500, 881, 20));
		super.panels.get("inOut").add(super.progressBars.get("basic"));

		new DropTarget(this.textFields.get("inputFile"), new DropTargetListener() {

			@Override
			public void dropActionChanged(DropTargetDragEvent dtde) {

			}

			@Override
			public void drop(DropTargetDropEvent dtde) {

			}

			@Override
			public void dragOver(DropTargetDragEvent dtde) {

			}

			@Override
			public void dragExit(DropTargetEvent dte) {

			}

			@Override
			public void dragEnter(DropTargetDragEvent dtde) {

			}
		});

		super.setVisible(true);
		LOGGER.info("Window sucsessfully created!");
		this.setBounds(new Rectangle(100, 100, 450, 300));

		ActivationAction.register();
	}

	public void createTableField() {
		JPanel panel = new JPanel(new GridLayout(1, 0));
		panel.setBackground(Color.BLACK);
		super.tabs.get("basic").insertTab("Table", null, panel, "Here you can see and edit the Excel file", 0);
		// panel.setLayout(null);
		this.addPanel("table", panel);

		JTabbedPane tables = new JTabbedPane(JTabbedPane.BOTTOM);
		tables.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {

			}
		});
		panel.add(tables, BorderLayout.CENTER);

		JPanel panel1 = new JPanel(new GridLayout(1, 0));
		panel1.setBackground(Color.BLACK);
		tables.insertTab("Students", null, panel1, "Here you can see and edit the Excel file", 0);
		// panel.setLayout(null);
		this.addPanel("Students", panel);

		String[] columNames = new String[] { "Type", "Vorname", "Nachname", "Fach", "Lehrer", "Fach", "Lehrer", "Fach",
				"Lehrer" };
		table = new JTable(/**
							 * Distributor.calculated.get(0).writeCourseInformation().getReverseGrid(),
							 * Distributor.calculated.get(0).writeCourseInformation().getReverseGrid()[0]
							 */
		);
		table.setPreferredScrollableViewportSize(new Dimension(500, 70));
		table.setFillsViewportHeight(true);
		JScrollPane scrollPane = new JScrollPane(table);
		// table.setFillsViewportHeight(true);

		panel1.add(scrollPane);
	}

	@Override
	public void render() {
		super.render();
		ContentHider.getInstance().update();
	}

	@Override
	public void close() {

	}

	@Override
	public void load() {
		InputSearchAction inputSearchAction;
		ActionListenerController.getInstance().addActionListener((inputSearchAction = new InputSearchAction()).name(),
				inputSearchAction);
		OutputSearchAction outputSearchAction;
		ActionListenerController.getInstance().addActionListener((outputSearchAction = new OutputSearchAction()).name(),
				outputSearchAction);
		RunAction runAction;
		ActionListenerController.getInstance().addActionListener((runAction = new RunAction()).name(), runAction);
		SaveAction saveAction;
		ActionListenerController.getInstance().addActionListener((saveAction = new SaveAction()).name(), saveAction);
		RunAndSave ras;
		ActionListenerController.getInstance().addActionListener((ras = new RunAndSave()).name(), ras);
	}

}
