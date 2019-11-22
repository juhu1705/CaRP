package de.juhu.gui.action.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;

import de.juhu.gui.filter.DirectoryFilter;
import de.juhu.gui.filter.TableFileFilter;
import de.juhu.gui.frames.GUIFrame;
import de.juhu.util.Config;
import de.juhu.util.Interfaces.HasName;

public class OutputSearchAction implements ActionListener, HasName {

	@Override
	public void actionPerformed(ActionEvent e) {
		JFileChooser outputChooser = new JFileChooser();
		outputChooser.setDialogTitle("Ausgabedatei");

		File outputFile = new File("");
		outputChooser.setFileFilter(DirectoryFilter.getInstance());
		outputChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		outputChooser.setFileFilter(TableFileFilter.getInstance());
		outputChooser.setCurrentDirectory(outputFile);
		GUIFrame frame = (GUIFrame) GUIFrame.getFrame();
		if (frame == null)
			return;
		outputChooser.showSaveDialog(frame);
		outputFile = outputChooser.getSelectedFile();
		String output = outputFile.getAbsolutePath().toString();

		frame.getTextField("outputFile")
				.setText(outputFile.isDirectory() ? output + "\\" + Config.outputFile + ".xlsx" : output);

	}

	@Override
	public String name() {
		return "output_search";
	}

}
