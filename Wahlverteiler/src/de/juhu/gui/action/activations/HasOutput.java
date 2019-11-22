package de.juhu.gui.action.activations;

import de.juhu.gui.frames.AbstractFrame;
import de.juhu.util.Util;

public class HasOutput extends ActivationAction {

	private AbstractFrame frame;

	public HasOutput(AbstractFrame frame) {
		this.frame = frame;
		this.isActive = false;
	}

	@Override
	public void update() {
		if (this.frame.hasTextField("outputFile")) {
			if (!Util.isBlank(this.frame.getTextField("outputFile").getText()) && !this.isActive) {
				ContentHider.getInstance().activateAction(this.name());
				this.isActive = true;
			} else if (Util.isBlank(this.frame.getTextField("outputFile").getText()) && this.isActive) {
				ContentHider.getInstance().deactivateAction(this.name());
				this.isActive = false;
			}
		}
	}

	@Override
	public String name() {
		return "has_output";
	}

}
