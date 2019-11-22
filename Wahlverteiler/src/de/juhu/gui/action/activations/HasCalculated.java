package de.juhu.gui.action.activations;

import de.juhu.distributor.Distributor;

public class HasCalculated extends ActivationAction {

	@Override
	public void update() {
		if (!Distributor.getInstance().calculated.isEmpty() && !this.isActive) {
			ContentHider.getInstance().activateAction(this.name());
			this.isActive = true;
		} else if (Distributor.getInstance().calculated.isEmpty() && this.isActive) {
			ContentHider.getInstance().deactivateAction(this.name());
			this.isActive = false;
		}
	}

	@Override
	public String name() {
		return "has_calculated";
	}

}
