package de.juhu.gui.action;

import java.awt.event.ActionListener;
import java.util.HashMap;

public class ActionListenerController {

	private static ActionListenerController instance;

	public static ActionListenerController getInstance() {
		return ActionListenerController.instance != null ? ActionListenerController.instance
				: (ActionListenerController.instance = new ActionListenerController());
	}

	protected ActionListenerController() {

	}

	private HashMap<String, ActionListener> actions = new HashMap<>();

	public ActionListenerController addActionListener(String name, ActionListener a) {
		this.actions.put(name, a);
		return this;
	}

	public ActionListener getActionListener(String name) {
		return this.actions.get(name);
	}

}
