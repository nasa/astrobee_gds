package gov.nasa.arc.verve.robot.freeflyer.parts;

import java.util.Collections;
import java.util.LinkedList;

import gov.nasa.arc.verve.robot.parts.concepts.RobotPartPoseHistory.Transforms;
import gov.nasa.arc.verve.robot.rapid.PositionSampleSourceRot;
import gov.nasa.arc.verve.robot.rapid.PositionSampleSourceXyz;
import gov.nasa.arc.verve.robot.rapid.RapidRobot;
import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.e4.message.MessageType;

import org.apache.log4j.Logger;

import com.ardor3d.image.Texture.MagnificationFilter;
import com.ardor3d.math.Matrix3;
import com.ardor3d.math.Transform;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyColorRGBA;
import com.ardor3d.renderer.queue.RenderBucketType;
import com.ardor3d.renderer.state.BlendState;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.scenegraph.hint.LightCombineMode;

public class RobotPartBreadCrumbSubtopic extends RobotPartBreadCrumb {
	private final Logger logger = Logger.getLogger(RobotPartBreadCrumbSubtopic.class);
	private PositionSampleSourceXyz m_xyzSource;
	private PositionSampleSourceRot m_rotSource;
	private String m_participantId;
	private MessageType m_msgType;

	public RobotPartBreadCrumbSubtopic(String partId, RapidRobot parent, String participantId, Agent a, MessageType mt) {
		super(partId, parent);
		m_xyzSource = new PositionSampleSourceXyz(mt, a);
		m_rotSource = new PositionSampleSourceRot(mt, a);
		m_participantId = participantId;
		m_msgType = mt;
	}
	
    @Override
    public void attachToNodesIn(Node model) throws IllegalStateException {
        m_poseNode = new Node("Pose", Collections.synchronizedList(new LinkedList<Spatial>()));

        //m_headNode = new Node("Heading", Collections.synchronizedList(new LinkedList<Spatial>()));
        //m_headClr = new ColorRGBA(1,1,0,1);

        m_node = new Node(getPartName()+"PoseHistory");
        initRenderStates(m_node);

        m_node.attachChild(m_poseNode);
        //m_node.attachChild(m_headNode);

        m_pathHistoryPath = new LinePath("posePathHistory");
        m_node.attachChild(m_pathHistoryPath);
        //m_pathHistoryPath.setSpeed(0.5);
        m_pathHistoryPath.setDefaultColor(getAColor());
        m_pathHistoryPath.getSceneHints().setLightCombineMode(LightCombineMode.Off);
        m_pathHistoryPath.getSceneHints().setRenderBucketType(RenderBucketType.PostBucket);
        m_pathHistoryPath.setTexture(LinePath.Texture.Blank, MagnificationFilter.NearestNeighbor);
        //m_pathHistoryPath.initTexture("images/ChaseDashHalf.png", MagnificationFilter.NearestNeighbor);
        m_pathHistoryPath.setLineWidth(2);
        m_pathHistoryPath.setAntialiased(true);
        m_pathHistoryPath.setTranslation(0, 0, 0);
        BlendState bs = new BlendState();
        bs.setBlendEnabled(true);
        bs.setTestEnabled(true);
        m_pathHistoryPath.setRenderState(bs);
        // show or hide by default?
        setShowPathHistory(true);

        // I want it on the sensors node so it's easy to turn off with the Alvar and Viz bases/
        // Is that bad?
        getRobot().getRobotNode().getSensorsNode().attachChild(m_node);
//        getRobot().getRobotNode().getConceptsNode().attachChild(m_node);
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
//        m_pendingPoses.add(tq);

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
//            m_pathHistoryPath.handleUpdate(currentTime);
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
