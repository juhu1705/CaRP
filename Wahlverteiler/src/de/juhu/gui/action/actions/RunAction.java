package de.juhu.gui.action.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;

import de.juhu.distributor.Distributor;
import de.juhu.gui.frames.ErrorFrame;
import de.juhu.gui.frames.GUIFrame;
import de.juhu.util.References;
import de.juhu.util.Util;
import de.juhu.util.Interfaces.HasName;

public class RunAction implements ActionListener, HasName {

	@Override
	public void actionPerformed(ActionEvent e) {
		GUIFrame frame = (GUIFrame) GUIFrame.getFrame();
		if (frame == null)
			return;
		if (frame.getTextField("inputFile").getText() == null
				|| Util.isBlank(frame.getTextField("inputFile").getText())) {
			Throwable t = new Throwable("Missing input file",
					new Throwable("The input file is empty, or does not exist, choose the input file and try again."));

			Thread thread = new Thread(new Runnable() {

				@Override
				public void run() {
					new ErrorFrame(t);
				}
			}, "ERROR");
			thread.start();
			try {
				throw t;
			} catch (Throwable e1) {
				References.LOGGER.log(Level.SEVERE, "", e1);
			}
			return;
		}
		Thread t = new Thread(Distributor.getInstance(), "Calculator");
		t.start();
	}

	@Override
	public String name() {
		return "run";
	}

}
