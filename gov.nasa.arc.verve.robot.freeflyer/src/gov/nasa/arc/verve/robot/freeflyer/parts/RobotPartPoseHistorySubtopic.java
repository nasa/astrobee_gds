package gov.nasa.arc.verve.robot.freeflyer.parts;

import gov.nasa.arc.verve.robot.parts.concepts.RobotPartPoseHistory;
import gov.nasa.arc.verve.robot.rapid.PositionSampleSourceRot;
import gov.nasa.arc.verve.robot.rapid.PositionSampleSourceXyz;
import gov.nasa.arc.verve.robot.rapid.RapidRobot;
import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.e4.message.MessageType;

import org.apache.log4j.Logger;

import com.ardor3d.math.Matrix3;
import com.ardor3d.math.Transform;
import com.ardor3d.math.Vector3;

public class RobotPartPoseHistorySubtopic extends RobotPartPoseHistory {
	private final Logger logger = Logger.getLogger(RobotPartPoseHistorySubtopic.class);
	private PositionSampleSourceXyz m_xyzSource;
	private PositionSampleSourceRot m_rotSource;
	private String m_participantId;
	private MessageType m_msgType;

	public RobotPartPoseHistorySubtopic(String partId, RapidRobot parent, String participantId, Agent a, MessageType mt) {
		super(partId, parent);
		m_xyzSource = new PositionSampleSourceXyz(mt, a);
		m_rotSource = new PositionSampleSourceRot(mt, a);
		m_participantId = participantId;
		m_msgType = mt;
	}
	
    @Override
	protected void addPoseMarker(boolean forward) {
        //-- add to path history

        Transforms tq;
        Transform tmpXfmA = Transform.fetchTempInstance();
        Transform tmpXfmB = Transform.fetchTempInstance();
        Transform robotPose = new Transform();
        robotPose.setTranslation(m_xyzSource.getXyz(new Vector3()));
        robotPose.setRotation(m_rotSource.getRot(new Matrix3()));
        tq = new Transforms();
        robotPose.multiply(m_poseOffset, tq.pose);

        tmpXfmA = m_nodePath.getTransform(tmpXfmA);
        tmpXfmB = robotPose.multiply(tmpXfmA, tmpXfmB);
        tq.heading = tmpXfmB.multiply(m_headAddXfm, tq.heading);
        if(!forward) {
            tmpXfmA.set(tq.heading);
            tq.heading = tmpXfmA.multiply(m_flipY, tq.heading);
        }        
        m_pendingPoses.add(tq);

        addPathHistorySample(tq.pose.getTranslation());

        Transform.releaseTempInstance(tmpXfmA);
        Transform.releaseTempInstance(tmpXfmB);
    }
    
    @Override
    public void handleFrameUpdate(long currentTime) {
        if(m_doReset.get()) {
            m_lastPos = null;
            m_pendingPoses.clear();
            m_poseNode.detachAllChildren();
            //m_headNode.detachAllChildren();
            m_doReset.set(false);
        }
        else {
            Transforms tq;

            if( checkEuclideanDistance(m_xyzSource.getXyz(new Vector3())) ) {
                addPoseMarker(true);
            }

            //-- update the pose history 
            if(m_poseNode != null) {
                // insert any pending pose markers
                while(m_pendingPoses.size() > 0) {
                    m_markerCount++;
                    tq = m_pendingPoses.remove(0);
                    m_poseNode.attachChildAt(newPoseMarker(tq.pose), 0);
                    //m_headNode.attachChildAt(newHeadingMarker(tq.heading), 0);
                    //                    if(false) {
                    //                        AxisRods ar = new AxisRods("heading", true, 0.5, 0.05);
                    //                        ar.setTransform(tq.heading);
                    //                        m_node.attachChild(ar);
                    //                    }
                }
                // remove extraneous markers from end of list
                int total;
                while( (total = m_poseNode.getNumberOfChildren()) > m_histSize) {
                    m_poseNode.detachChildAt(total-1);
                    //m_headNode.detachChildAt(total-1);
                }
            }
        }
        if(m_doUpdatePathHistory) {
            m_pathHistoryPath.handleUpdate(currentTime);
            m_doUpdatePathHistory = false;
        }
    }
	
	@Override 
	public void connectTelemetry() {
		if (getParticipantId() == null || getParticipantId().isEmpty()){
			return;
		}
		try {
			m_xyzSource.connectTelemetry(getParticipantId());
			m_rotSource.connectTelemetry(getParticipantId());
		}
		catch(Exception e) {
			logger.error(e);
		}
	}

	@Override 
	public void disconnectTelemetry() {
		if (getParticipantId() == null || getParticipantId().isEmpty()){
			return;
		}
		try {
			m_xyzSource.disconnectTelemetry(getParticipantId());
			m_rotSource.disconnectTelemetry(getParticipantId());
		}
		catch(Exception e) {
			logger.error(e);
		}
	}

	private String getParticipantId() {
		return m_participantId;
	}
		
}
