/*******************************************************************************
 * Copyright (c) 2013 United States Government as represented by the 
 * Administrator of the National Aeronautics and Space Administration. 
 * All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package gov.nasa.arc.verve.robot.rapid.parts.concepts;

import gov.nasa.arc.verve.ardor3d.scenegraph.util.SpatialPath;
import gov.nasa.arc.verve.common.scenario.ScenarioPreferences;
import gov.nasa.arc.verve.robot.exception.TelemetryException;
import gov.nasa.arc.verve.robot.rapid.RapidRobot;
import gov.nasa.arc.verve.robot.rapid.parts.RapidRobotPart;
import gov.nasa.arc.verve.robot.scenegraph.shape.concepts.DirectionalPath;
import gov.nasa.arc.verve.utils.rapid.RapidVerve;
import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.e4.message.MessageType;
import gov.nasa.rapid.v2.e4.message.MessageTypeExt;
import gov.nasa.rapid.v2.message.agent.MessageTypeK10;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import rapid.PositionConfig;
import rapid.PositionSample;

import com.ardor3d.image.Texture.MagnificationFilter;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Transform;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyColorRGBA;
import com.ardor3d.math.type.ReadOnlyVector3;
import com.ardor3d.renderer.queue.RenderBucketType;
import com.ardor3d.renderer.state.BlendState;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.scenegraph.hint.LightCombineMode;

/**
 * 
 *
 */
public class RapidRobotPartPositionPath extends RapidRobotPart {    
    private static final Logger logger = Logger.getLogger(RapidRobotPartPositionPath.class);
    private   final Vector3      m_thisPos         = new Vector3();
    private   Vector3            m_lastPos	       = null;
    private   float		         m_poseDistThresh  = 1.0f; // 1m 
    protected final Vector3      m_offset          = new Vector3(0,0,-0.25);

    private final ColorRGBA      m_color           = new ColorRGBA();
    protected Pose               m_poseType;

    protected MessageType        m_sampleType;

    private ArrayList<Vector3>   m_pathHistory     = new ArrayList<Vector3>();
    private int                  m_pathHistoryMax  = 20000;
    private DirectionalPath      m_pathHistoryPath = null;

    private FrameConversion      m_frameConvert    =  FrameConversion.None;
    private final Transform      m_frameXfm        = new Transform();
    private final Transform      m_poseIn          = new Transform();
    private final Transform      m_pose            = new Transform();

    /**
     * 
     * @param partId
     * @param parent
     */
    //=========================================================================
    public RapidRobotPartPositionPath(String partName, RapidRobot parent, 
                                 String participantId, 
                                 MessageType positionSampleType,
                                 ReadOnlyColorRGBA color,
                                 ReadOnlyVector3 offset) {
        super(partName, parent, participantId);
        m_sampleType = positionSampleType;
        m_color.set(color);
        m_offset.set(offset);
        for(Pose pose : Pose.values()) {
            if(pose.msgType.equals(positionSampleType)) {
                m_poseType = pose;
                break;
            }
        }
        if(m_poseType == null) {
            logger.warn("MessageType "+positionSampleType.name()+" is not a valid option");
            m_poseType = Pose.GlobalPose;
        }
    }
    
    /**
     * Handle poses given in UTM
     */
    public enum FrameConversion {
        None,
        UtmENU_to_Site,
    }
    
    public void setFrameConversion(FrameConversion conversion) {
        m_frameConvert = conversion;
        switch(conversion) {
        case None:
            m_frameXfm.setIdentity();
            break;
        case UtmENU_to_Site:
            Vector3 enu           = ScenarioPreferences.getSiteFrameEastingNorthingAltitude();
            Node site             = getRobot().getRobotNode();
            SpatialPath path      = new SpatialPath(site, null, true);
            Spatial utm           = new Node();
            // z value is included in site frame transform. This should be rethought...
            utm.setTranslation(new Vector3(-enu.getX(), -enu.getY(), 0));
            path.prepend(utm);
            path.getTransform(m_frameXfm);
            break;
        }
    }
    

    public FrameConversion getFrameConversion() {
        return m_frameConvert;
    }
    
    /**
     * XXX For UI purposes, use fixed set of pose types
     */
    public enum Pose {
        GlobalPose(MessageType.POSITION_SAMPLE_TYPE),
        RelativePose(MessageType.RELATIVE_POSITION_SAMPLE_TYPE),
        MapAlignedPose(MessageTypeExt.LOCALMAP_ALIGNED_POSITION_SAMPLE_TYPE),
        Ins(MessageTypeExt.INS_POSITION_SAMPLE_TYPE),
        AdvNav(MessageTypeK10.POSE_advNav_POSITION_SAMPLE_TYPE),
        Eigen(MessageTypeK10.POSE_eigen_POSITION_SAMPLE_TYPE),
        EigenImproved(MessageTypeK10.POSE_eigenImproved_POSITION_SAMPLE_TYPE),
        EigenAdvNav(MessageTypeK10.POSE_eigenAdvNav_POSITION_SAMPLE_TYPE),
        RelativeEigen(MessageTypeK10.POSE_relative_eigen_POSITION_SAMPLE_TYPE),
        RelativeEigenImproved(MessageTypeK10.POSE_relative_eigenImproved_POSITION_SAMPLE_TYPE),
        ;
        public final MessageType msgType;
        Pose(MessageType msgType) {
            this.msgType = msgType;
        }
    }

    public Pose getPoseType() {
        return m_poseType;
    }

    public void setPoseType(Pose poseType) {
        try {
            disconnectTelemetry();
        }
        catch(Throwable t) {
            logger.warn("Error disconnecting "+m_sampleType.name()+" telemetry", t);
        }
        m_poseType = poseType;
        m_sampleType = poseType.msgType;
        
        try {
            connectTelemetry();
        }
        catch(Throwable t) {
            logger.warn("Error connecting "+m_sampleType.name()+" telemetry", t);
        }
    }

    public int getPathHistorySize() {
        return m_pathHistoryMax;
    }
    public void setPathHistorySize(int historySize) {
        m_pathHistoryMax = historySize;
    }

    public ReadOnlyColorRGBA getColor() {
        return m_color;
    }

    public void setColor(ReadOnlyColorRGBA color) {
        m_color.set(color);
        if(m_pathHistoryPath != null) {
            m_pathHistoryPath.setDefaultColor(color);
        }
    }

    public void setXOffset(float offset) {
        m_offset.setX(offset);
    }
    public float getXOffset() {
        return m_offset.getXf();
    }
    
    public void setYOffset(float offset) {
        m_offset.setY(offset);
    }
    public float getYOffset() {
        return m_offset.getYf();
    }
    
    public void setZOffset(float offset) {
        m_offset.setZ(offset);
    }
    public float getZOffset() {
        return m_offset.getZf();
    }
    
    @Override
    public void attachToNodesIn(Node model) throws IllegalStateException {
        m_node = new Node(getPartName()+"PosePath");

        m_pathHistoryPath = new DirectionalPath("path");
        m_node.attachChild(m_pathHistoryPath);
        //m_pathHistoryPath.setSpeed(0.5);
        m_pathHistoryPath.setDefaultColor(m_color);
        m_pathHistoryPath.getSceneHints().setLightCombineMode(LightCombineMode.Off);
        m_pathHistoryPath.getSceneHints().setRenderBucketType(RenderBucketType.PostBucket);
        m_pathHistoryPath.setTexture(DirectionalPath.Texture.ChaseDotDotAlpha, MagnificationFilter.NearestNeighbor);
        //m_pathHistoryPath.initTexture("images/ChaseDashHalf.png", MagnificationFilter.NearestNeighbor);
        m_pathHistoryPath.setLineWidth(2);
        m_pathHistoryPath.setAntialiased(true);
        m_pathHistoryPath.setTranslation(Vector3.ZERO);
        BlendState bs = new BlendState();
        bs.setBlendEnabled(true);
        bs.setTestEnabled(true);
        m_pathHistoryPath.setRenderState(bs);

        getRobot().getRobotNode().getConceptsNode().attachChild(m_node);
    }

    /**
     * 
     * @param thisPos
     * @return
     */
    protected boolean checkEuclideanDistance(ReadOnlyVector3 thisPos) {
        if(m_lastPos == null) {
            m_lastPos = new Vector3(thisPos);
            return false;
        }
        else {
            final double dist = m_lastPos.distance(thisPos);
            if(dist > m_poseDistThresh) {
                m_lastPos.set(thisPos);
                return true;
            }
        }
        return false;
    }

    /**
     * @param pos will be copied and added to history
     */
    protected void addPathHistorySample(ReadOnlyVector3 pos) {
        if(pos != null) {
            if(m_pathHistory.size() >= m_pathHistoryMax) {
                ArrayList<Vector3> old = new ArrayList<Vector3>();
                old.ensureCapacity(m_pathHistoryMax);
                old.addAll(m_pathHistory.subList(m_pathHistoryMax/2, m_pathHistory.size()-1));
                m_pathHistory = old;
            }
            m_pathHistory.add(new Vector3(pos.getX(), pos.getY(), pos.getZ()+0.02));
        }
        m_pathHistoryPath.queueUpdateData(m_pathHistory);
    }

    @Override
    public void handleFrameUpdate(long currentTime) {
        if( isDirty() && checkEuclideanDistance(m_thisPos) ) {
            addPathHistorySample(m_thisPos);
        }
        m_pathHistoryPath.handleUpdate(currentTime);
    }

    @Override
    public void reset() {
        m_lastPos = null;
        m_pathHistory.clear();
        m_pathHistoryPath.setTranslation(Vector3.ZERO);
        if(m_pathHistoryPath != null) {
            m_pathHistoryPath.queueUpdateData(m_pathHistory);
        }
    }

    @Override
    public synchronized void connectTelemetry() throws TelemetryException {
        super.connectTelemetry();
    }

    @Override
    public synchronized void disconnectTelemetry() throws TelemetryException {
        super.disconnectTelemetry();
        m_lastPos = null;
    }

    @Override
    public MessageType[] rapidMessageTypes() { 
        return new MessageType[] { m_sampleType }; 
    }

    public void onRapidMessageReceived(Agent agent, MessageType msgType, Object msgObj, Object cfgObj) {
        if(msgType.equals(m_sampleType) && cfgObj != null) {
            PositionSample sample = (PositionSample)msgObj;
            PositionConfig config = (PositionConfig)cfgObj;
            
            RapidVerve.toArdor(sample.pose, config.poseEncoding, m_poseIn);
            m_frameXfm.multiply(m_poseIn, m_pose);
            
            //logger.debug("pose = "+Ardor3D.format(m_poseIn));
            
            m_thisPos.set(m_pose.getTranslation());
            setDirty(true);
        }
    }
}
