package gov.nasa.arc.verve.robot.freeflyer.parts;

import gov.nasa.arc.irg.freeflyer.rapid.FreeFlyerMessageType;
import gov.nasa.arc.verve.robot.freeflyer.RapidFreeFlyerRobot;
import gov.nasa.arc.verve.robot.parts.IRobotPart;
import gov.nasa.arc.verve.robot.parts.RobotPartBase;
import gov.nasa.arc.verve.robot.parts.tools.RobotPartAxes;
import gov.nasa.arc.verve.robot.parts.tools.RobotPartRadialGrid;
import gov.nasa.arc.verve.robot.rapid.RapidRobot;
import gov.nasa.arc.verve.robot.rapid.parts.IRobotPartFactory;

public class RapidFreeFlyerPartFactory implements IRobotPartFactory {
	public IRobotPart createPart(String id, RapidRobot robot) {
		String pp = robot.getGlobalParticipant();   

		if(id == null || id.length() < 1) { return null; }
		else if(id.equals(RapidFreeFlyerRobot.BASE))         
			return new RobotPartBase(id, robot);
		else if(id.equals(RapidFreeFlyerRobot.AXES))          return new RobotPartAxes(id, robot);
		else if(id.equals(RapidFreeFlyerRobot.RADIAL_GRID))   return new RobotPartRadialGrid(id, robot);
		else if(id.equals(RapidFreeFlyerRobot.POSE_HISTORY))  return new RobotPartBreadCrumb(id, robot);
		else if(id.equals(RapidFreeFlyerRobot.ABSOLUTE_DRAGGABLE_PREVIEW))
			return new RobotPartDraggablePreview(id, robot);
		else if(id.equals(RapidFreeFlyerRobot.RELATIVE_DRAGGABLE_PREVIEW)) 
			return new RobotPartRelativeDraggablePreview(id, robot);
		else if(id.equals(RapidFreeFlyerRobot.JOINTS )){
			return new RapidFreeFlyerArmPartJoint(id, robot, pp, FreeFlyerMessageType.JOINT_SAMPLE_TYPE);
		}
		return null;
	}
}
