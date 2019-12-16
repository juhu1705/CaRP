package de.juhu.gui.action.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import de.juhu.dateimanager.CSVExporter;
import de.juhu.dateimanager.ExcelExporter;
import de.juhu.distributor.Distributor;
import de.juhu.distributor.Save;
import de.juhu.gui.frames.AbstractFrame;
import de.juhu.util.Util;
import de.juhu.util.Interfaces.HasName;

public class SaveAction implements ActionListener, HasName {

	@Override
	public String name() {
		return "save";
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (Distributor.getInstance().calculated.isEmpty())
			return;

		if (Distributor.calculate)
			return;

		Save save = Distributor.getInstance().calculated.poll();
		Distributor.getInstance().calculated.add(save);
		String s;
		if (Util.isBlank((s = AbstractFrame.getFrame().getTextField("outputFile").getText())))
			return;

		boolean xls = false, xlsx = false, csv = false;

		if (s.endsWith(".xls")) {
			s = s.substring(0, s.length() - 4);
			xls = true;
		} else if (s.endsWith(".xlsx")) {
			s = s.substring(0, s.length() - 5);
			xlsx = true;
		} else if (s.endsWith(".csv")) {
			csv = true;
			s = s.substring(0, s.length() - 4);
		}

		if (xls) {
			try {
				ExcelExporter.writeXLS(s, save.writeInformation());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		if (xlsx) {
			try {
				ExcelExporter.writeXLSX(s, save.writeInformation());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		if (csv) {
			try {
				CSVExporter.writeCSV(s + "course", save.writeCourseInformation());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				CSVExporter.writeCSV(s + "student", save.writeStudentInformation());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

	}

}
