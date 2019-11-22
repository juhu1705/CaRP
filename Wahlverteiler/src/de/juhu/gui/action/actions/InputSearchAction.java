package de.juhu.gui.action.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;

import de.juhu.distributor.Distributor;
import de.juhu.gui.filter.TableFileFilter;
import de.juhu.gui.frames.GUIFrame;
import de.juhu.util.References;
import de.juhu.util.Interfaces.HasName;

public class InputSearchAction implements ActionListener, HasName, Runnable {

	@Override
	public String name() {
		return "input_search";
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Eingabedatei");

		fileChooser.setFileFilter(TableFileFilter.getInstance());

		File inputFile = fileChooser.getSelectedFile();
		GUIFrame frame = (GUIFrame) GUIFrame.getFrame();
		if (frame == null)
			return;
//		fileChooser.showOpenDialog(frame);
//		fileChooser.setCurrentDirectory(inputFile);
//		inputFile = fileChooser.getSelectedFile();
		frame.getTextField("inputFile").setText(inputFile.getAbsolutePath().toString());

		new Thread(this, "Importer").start();

	}

	@Override
	public void run() {
		References.LOGGER.info("Import");
		new Distributor(((GUIFrame) GUIFrame.getFrame()).getTextField("inputFile").getText());
	}

}
