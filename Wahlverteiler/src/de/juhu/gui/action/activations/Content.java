package de.juhu.gui.action.activations;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map.Entry;

import de.juhu.gui.frames.AbstractFrame;
import de.juhu.util.References;

public class Content {

	private Component component;

	private String id;

	private String panelID;

	private AbstractFrame frame;

	private HashMap<String, Boolean> neededActions;

	private boolean isActive;

	public Content(Component component, String id, String panelID, boolean isActive, AbstractFrame frame,
			String... neededActions) {
		this.component = component;
		this.id = id;
		this.panelID = panelID;
		this.neededActions = new HashMap<>(neededActions.length);
		this.frame = frame;
		this.isActive = isActive;

		for (String s : neededActions) {
			this.neededActions.put(s, false);
			References.LOGGER.info(s);
		}

		this.frame.addAndSortComponent(this.id, this.panelID, this.component);
		this.hide();
	}

	public Component getComponent() {
		return this.component;
	}

	public String getID() {
		return this.id;
	}

	public String getPanelID() {
		return this.panelID;
	}

	public Content activate(String actionID) {
		if (this.neededActions.containsKey(actionID)) {

			this.neededActions.replace(actionID, false, true);

			if (!this.isActive) {
				boolean shouldActivate = true;
				for (Entry<String, Boolean> e : this.neededActions.entrySet()) {
					if (!e.getValue())
						shouldActivate = false;
				}

				if (shouldActivate)
					this.show();
			}
		}
		return this;
	}

	private void show() {
		this.component.setEnabled(true);
		this.isActive = true;

	}

	private void hide() {
		this.component.setEnabled(false);
		this.isActive = false;
		// this.frame.addAndSortComponent(this.id, this.panelID, this.component);
	}

	public Content deactivate(String actionID) {
		if (this.neededActions.containsKey(actionID)) {

			this.neededActions.replace(actionID, true, false);
			if (this.isActive)
				this.hide();
		}
		return this;
	}
}
