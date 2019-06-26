package gov.nasa.arc.verve.robot.rapid.parts.concepts;

import gov.nasa.arc.verve.robot.rapid.RapidRobot;
import gov.nasa.rapid.v2.e4.message.MessageType;

import com.ardor3d.math.type.ReadOnlyColorRGBA;
import com.ardor3d.math.type.ReadOnlyVector3;

/**
 * Necessary to subclass to allow multiple instances in RobotProperties view
 * This needs to be fixed
 */
public class RapidRobotPartPositionPath3 extends RapidRobotPartPositionPath {

    public RapidRobotPartPositionPath3(String partName, RapidRobot parent,
                                       String participantId,
                                       MessageType positionSampleType,
                                       ReadOnlyColorRGBA color, 
                                       ReadOnlyVector3 offset) {
        super(partName, parent, participantId, positionSampleType, color, offset);
    }

}
