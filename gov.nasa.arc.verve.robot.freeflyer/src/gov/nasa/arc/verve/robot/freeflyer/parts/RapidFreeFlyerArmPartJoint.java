package gov.nasa.arc.verve.robot.freeflyer.parts;

import gov.nasa.arc.verve.robot.parts.JointInfo;
import gov.nasa.arc.verve.robot.rapid.RapidRobot;
import gov.nasa.arc.verve.robot.rapid.parts.RapidRobotPartJoints;
import gov.nasa.rapid.v2.e4.message.MessageType;

import com.ardor3d.math.Vector3;
import com.ardor3d.scenegraph.Node;


public class RapidFreeFlyerArmPartJoint  extends RapidRobotPartJoints{

	private final static int ARM_JOINT_NUM = 4;
	
	
	RapidFreeFlyerArmPartJoint(final String partName, final RapidRobot parent,
			final String participantId, final MessageType messageType) {
		super(partName, parent, participantId, messageType, ARM_JOINT_NUM);
		m_node = new FreeFlyerArmModel().getModel();
		
		m_joints[0] = new JointInfo("armBase", new Vector3(0, -1, 0), true, (float)Math.PI);
		m_joints[0].spatial = ((Node)m_node.getChild(0)).getChild(1);
		m_joints[1] = new JointInfo("armWrist", new Vector3(0, 0, 1));
		m_joints[1].spatial = ((Node)m_joints[0].spatial).getChild(1);
		m_joints[2] = new JointInfo("armRightHand", new Vector3(0, 0, 1));
		m_joints[2].spatial = ((Node)m_joints[1].spatial).getChild(1);
		m_joints[3] = new JointInfo("armLeftHand", new Vector3(0, 0, 1));
		m_joints[3].spatial = ((Node)m_joints[1].spatial).getChild(2);

	}
	
	@Override
	public void attachToNodesIn(final Node model) throws IllegalStateException {
		model.attachChild(m_node);
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void setupJointInfo() {
		// TODO Auto-generated method stub
		
	}

}
