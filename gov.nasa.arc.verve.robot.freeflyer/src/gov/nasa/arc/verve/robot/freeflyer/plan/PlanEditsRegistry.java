package gov.nasa.arc.verve.robot.freeflyer.plan;

import gov.nasa.arc.irg.plan.model.Sequenceable;
import gov.nasa.arc.irg.plan.model.Station;

import java.util.ArrayList;
import java.util.List;

public class PlanEditsRegistry {
	private static List<PlanEditsListener> s_list = new ArrayList<PlanEditsListener>();
	
	public static void addListener(PlanEditsListener pel) {
		s_list.add(pel);
	}
	
	public static void removeListener(PlanEditsListener pel) {
		s_list.remove(pel);
	}
	
	public static void onAppend(Sequenceable appended) {
		for(PlanEditsListener p: s_list) {
			p.onAppend(appended);
		}
	}
	
	public static void onInsert(Sequenceable inserted) {
		for(PlanEditsListener p: s_list) {
			p.onInsert(inserted);
		}
	}
	
	public static void onDelete(Sequenceable deleted) {
		for(PlanEditsListener p: s_list) {
			p.onDelete(deleted);
		}
	}
	
	public static void onMoveUp(Sequenceable moved) {
		for(PlanEditsListener p: s_list) {
			p.onMoveUp(moved);
		}
	}
	
	public static void onMoveDown(Sequenceable moved) {
		for(PlanEditsListener p: s_list) {
			p.onMoveDown(moved);
		}
	}
	
	public static void onStationMoved(Station moved) {
		for(PlanEditsListener p: s_list) {
			p.onStationMoved(moved);
		}
	}
}
