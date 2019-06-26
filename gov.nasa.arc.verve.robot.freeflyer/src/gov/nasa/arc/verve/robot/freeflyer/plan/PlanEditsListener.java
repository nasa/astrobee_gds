package gov.nasa.arc.verve.robot.freeflyer.plan;

import gov.nasa.arc.irg.plan.model.Sequenceable;
import gov.nasa.arc.irg.plan.model.Station;


public interface PlanEditsListener {

	public void onAppend(Sequenceable appended);
	public void onInsert(Sequenceable inserted);
	public void onDelete(Sequenceable deleted);
	public void onMoveUp(Sequenceable moved);
	public void onMoveDown(Sequenceable moved);
	
	public void onStationMoved(Station moved);
}
