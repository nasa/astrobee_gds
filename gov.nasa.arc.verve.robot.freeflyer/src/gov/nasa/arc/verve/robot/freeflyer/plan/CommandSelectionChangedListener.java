package gov.nasa.arc.verve.robot.freeflyer.plan;

import gov.nasa.arc.irg.plan.model.Sequenceable;

public interface CommandSelectionChangedListener {
	public void commandSelectionChanged(Sequenceable command);
}
