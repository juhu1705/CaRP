package de.juhu.gui.action.activations;

import java.util.ArrayList;

import de.juhu.util.Interfaces.Updateable;

public class ContentHider implements Updateable {

	private ArrayList<Content> hideable;
	private ArrayList<ActivationAction> actionWaiter;

	private static ContentHider instance;

	public static ContentHider getInstance() {
		return instance == null ? instance = new ContentHider() : instance;
	}

	protected ContentHider() {
		this.hideable = new ArrayList<>();
		this.actionWaiter = new ArrayList<>();
	}

	public ContentHider register(ActivationAction a) {
		this.actionWaiter.add(a);
		return this;
	}

	public ContentHider register(Content c) {
		this.hideable.add(c);
		return this;
	}

	public void activateAction(String actionID) {
		for (Content c : this.hideable) {
			c.activate(actionID);
		}
	}

	public void deactivateAction(String actionID) {
		for (Content c : this.hideable) {
			c.deactivate(actionID);
		}
	}

	@Override
	public void update() {
		this.actionWaiter.forEach(a -> a.update());
	}

}
