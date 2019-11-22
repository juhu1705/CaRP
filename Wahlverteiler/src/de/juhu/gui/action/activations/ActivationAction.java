package de.juhu.gui.action.activations;

import de.juhu.gui.frames.AbstractFrame;
import de.juhu.util.Interfaces.HasName;
import de.juhu.util.Interfaces.Updateable;

public abstract class ActivationAction implements Updateable, HasName {

	protected boolean isActive;

	public static void register() {
		ContentHider.getInstance().register(new HasInput(AbstractFrame.getFrame()));
		ContentHider.getInstance().register(new HasOutput(AbstractFrame.getFrame()));
		ContentHider.getInstance().register(new HasCalculated());
		ContentHider.getInstance().register(new IsNotCalculating());
	}
}
