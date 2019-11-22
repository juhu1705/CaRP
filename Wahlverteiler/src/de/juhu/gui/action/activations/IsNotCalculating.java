package de.juhu.gui.action.activations;

import de.juhu.distributor.Distributor;

public class IsNotCalculating extends ActivationAction {

	public IsNotCalculating() {
		this.isActive = false;
	}

	@Override
	public void update() {
		if (!Distributor.calculate && !this.isActive) {
			ContentHider.getInstance().activateAction(this.name());
			this.isActive = true;
		} else if (Distributor.calculate && this.isActive) {
			ContentHider.getInstance().deactivateAction(this.name());
			this.isActive = false;
		}

	}

	@Override
	public String name() {
		return "is_not_calculating";
	}

}
