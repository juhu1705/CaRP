package de.juhu.gui.action.activations;

import de.juhu.gui.frames.AbstractFrame;
import de.juhu.util.Util;

public class HasInput extends ActivationAction {

	private AbstractFrame frame;

	public HasInput(AbstractFrame frame) {
		this.frame = frame;
		this.isActive = false;
	}

	@Override
	public void update() {
		if (this.frame.hasTextField("inputFile")) {
			if (!Util.isBlank(this.frame.getTextField("inputFile").getText()) && !this.isActive) {
				ContentHider.getInstance().activateAction(this.name());
				this.isActive = true;
			} else if (Util.isBlank(this.frame.getTextField("inputFile").getText()) && this.isActive) {
				ContentHider.getInstance().deactivateAction(this.name());
				this.isActive = false;
			}
		}

	}

	@Override
	public String name() {
		return "has_input";
	}

}
