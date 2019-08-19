package gov.nasa.arc.verve.robot.freeflyer.utils;

/** central repository of all the strings we use as keys for objects in the context */
public class ContextNames {

	/** Name of PlanBuilder associated with PlanEditor */
	final public static String PLAN_BUILDER_FOR_PLAN_EDITOR = "CreatePlanBuilder";

	/** Boolean that controls whether Append Station is legal */
	final public static String APPEND_STATION_ENABLED = "appendStationEnabled";
	
	/** Boolean that controls whether Insert Station is legal */
	final public static String INSERT_STATION_ENABLED = "insertStationEnabled";
	
	/** Boolean that controls whether Move Station Up is legal */
	final public static String MOVE_STATION_UP_ENABLED = "moveStationUpEnabled";
	
	/** Boolean that controls whether Move Station Down is legal */
	final public static String MOVE_STATION_DOWN_ENABLED = "moveStationDownEnabled";
	
	/** Boolean that controls whether Delete Station is legal */
	final public static String DELETE_STATION_ENABLED = "deleteStationEnabled";
	
	/** Boolean that controls whether Add via 3D View mode is active */
	final public static String ADD_VIA_MAP_ENABLED = "addViaMapEnabled";
	
	/** Boolean that controls whether new plan is legal */
	final public static String NEW_PLAN_ENABLED = "newPlanEnabled";
	
	/** Boolean that controls whether open plan is legal */
	final public static String OPEN_PLAN_ENABLED = "openPlanEnabled";
	
	/** Boolean that controls whether close plan is legal */
	final public static String CLOSE_PLAN_ENABLED = "closePlanEnabled";
	
	/** Boolean that controls whether save plan is legal */
	final public static String SAVE_PLAN_ENABLED = "savePlanEnabled";
	
	/** Boolean that controls whether save as is legal */
	final public static String SAVE_PLAN_AS_ENABLED = "savePlanAsEnabled";
	
	/** Boolean that controls whether undo is legal */
	final public static String UNDO_ENABLED = "undoEnabled";
	
	/** KeepoutBuilder that is currently active */
	final public static String KEEPOUT_BUILDER_FOR_KEEPOUT_EDITOR = "createKeepoutBuilder";
	
	/** Boolean that controls whether new keepout is legal */
	final public static String NEW_KEEPOUT_ENABLED = "newKeepoutEnabled";
	
	/** Boolean that controls whether undo is legal */
	final public static String OPEN_KEEPOUT_ENABLED = "openKeepoutEnabled";
	
	/** Boolean that controls whether close keepout is legal */
	final public static String CLOSE_KEEPOUT_ENABLED = "closeKeepoutEnabled";
	
	/** Boolean that controls whether save keepout is legal */
	final public static String SAVE_KEEPOUT_ENABLED = "saveKeepoutEnabled";
	
	/** KeepoutBox that is currently selected */
	final public static String SELECTED_KEEPOUT = "selectedKeepout";
		
	/** Vector3 to preview teleop translation */
	final public static String TELEOP_TRANSLATION = "teleopTranslation";
	
	/** Vector3 to preview teleop translation */
	final public static String RELATIVE_TELEOP_TRANSLATION = "relativeTeleopTranslation";
	
	/** Vector3 to preview teleop rotation */
	final public static String TELEOP_ROTATION_RADIANS = "teleopRotationRadians";
	
	/** Vector3 to preview teleop rotation */
	final public static String RELATIVE_TELEOP_ROTATION_RADIANS = "relativeTeleopRotationRadians";

	/** false if selected robot has checkKeepouts as false, true otherwise */
	final public static String CHECK_KEEPOUTS_ENABLED = "checkKeepoutsEnabled";
	
	/** Vector3 to add a station in Add via 3D View mode */
	final public static String ADD_VIA_MAP_LOCATION = "addViaMapLocation";

	/** Point6Dof to make UpdateValueCommand from drag. Angles in degrees. */
	final public static String NEW_STATION_LOCATION = "newStationLocation";
	
	/** Vector3 to preview station translation */
	final public static String STATION_TRANSLATION = "stationTranslation";
	
	/** StationBox to get the currently selected station */
	final public static String SELECTED_STATION = "selectedStation";

	/** HandrailBuilder that is current active */
	final public static String HANDRAIL_BUILDER = "handrailBuilder";
	
	/** HandrailModel to get the currently selected station */
	final public static String SELECTED_HANDRAIL = "selectedHandrail";
	
	/** Boolean that controls whether save handrails is legal */
	final public static String SAVE_HANDRAILS_ENABLED = "saveHandrailsEnabled";
}
