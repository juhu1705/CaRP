package de.juhu.gui.action.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import de.juhu.distributor.Distributor;
import de.juhu.gui.action.ActionListenerController;
import de.juhu.util.References;
import de.juhu.util.Interfaces.HasName;

public class RunAndSave implements ActionListener, HasName, Runnable {

	ActionEvent e;

	@Override
	public void actionPerformed(ActionEvent e) {
		this.e = e;
		Thread t = new Thread(this, "Save");
		t.start();
	}

	@Override
	public String name() {
		return "run_and_save";
	}

	@Override
	public void run() {
		ActionListenerController.getInstance().getActionListener("run").actionPerformed(e);

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		while (Distributor.calculate) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}

		// ((GUIFrame) AbstractFrame.getFrame()).createTableField();
		References.LOGGER.info("Start Exporting data");
		ActionListenerController.getInstance().getActionListener("save").actionPerformed(e);
		References.LOGGER.info("Finished Exporting data");
	}

}
