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

import gov.nasa.arc.verve.common.ardor3d.util.RotUtil;
import gov.nasa.arc.verve.robot.exception.TelemetryException;
import gov.nasa.arc.verve.robot.rapid.PositionSampleSourceRot;
import gov.nasa.arc.verve.robot.rapid.RapidRobot;
import gov.nasa.arc.verve.robot.rapid.parts.RapidRobotPart;
import gov.nasa.arc.verve.robot.scenegraph.shape.concepts.DirectionalPath;
import gov.nasa.ensemble.ui.databinding.widgets.customization.annotations.Trigger;
import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.e4.message.MessageType;
import gov.nasa.rapid.v2.e4.message.MessageTypeExt;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedList;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import rapid.PositionConfig;
import rapid.PositionSample;

import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Matrix3;
import com.ardor3d.math.Transform;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyColorRGBA;
import com.ardor3d.math.type.ReadOnlyVector3;
import com.ardor3d.renderer.queue.RenderBucketType;
import com.ardor3d.scenegraph.Node;

/**
 * 
 *
 */
public class RapidRobotPartMultiPoseHistory extends RapidRobotPart {
    private static final Logger logger = Logger.getLogger(RapidRobotPartMultiPoseHistory.class);
    protected final ArrayList<PositionSource> m_posSrcs = new ArrayList<PositionSource>();

    protected long   m_lastTime      =     0;
    protected long   m_timeInterval  =   300;
    protected double m_spaceInterval =  0.25;
    protected double m_resetInterval =    10;
    protected int    m_histSize      =   200;
    protected int    m_masterIdx     =     0;
    protected boolean m_constantSync = false;
    protected float  m_zSeparation   =     0;
    protected float  m_zOff          =     0;

    protected static boolean debug = false;
    
    /**
     * XXX For UI purposes, use fixed set of pose types
     */
    enum Pose {
        GlobalPose(MessageType.POSITION_SAMPLE_TYPE),
        RelativePose(MessageType.RELATIVE_POSITION_SAMPLE_TYPE),
        LocalMapAlignedPose(MessageTypeExt.LOCALMAP_ALIGNED_POSITION_SAMPLE_TYPE),
        //Ins(MessageTypeExt.INS_POSITION_SAMPLE_TYPE),
        //Eigen(MessageTypeK10.POSE_eigen_POSITION_SAMPLE_TYPE),
        //EigenImproved(MessageTypeK10.POSE_eigenImproved_POSITION_SAMPLE_TYPE),
        //EigenAdvNav(MessageTypeK10.POSE_eigenAdvNav_POSITION_SAMPLE_TYPE),
        //RelativeEigen(MessageTypeK10.POSE_relative_eigen_POSITION_SAMPLE_TYPE),
        //RelativeEigenImproved(MessageTypeK10.POSE_relative_eigenImproved_POSITION_SAMPLE_TYPE),
        ;
        public final MessageType msgType;
        Pose(MessageType msgType) {
            this.msgType = msgType;
        }
    }
    protected Pose m_masterPose = Pose.GlobalPose;


    public class PositionSource {
        public final PositionSampleSourceRot source;
        public final Transform               thisXfm   = new Transform();
        public final Transform               lastXfm   = new Transform();
        public final Transform               invertXfm = new Transform();
        public       Transform               masterXfm = new Transform();
        public final DirectionalPath         path;
        public final LinkedList<Transform>   history = new LinkedList<Transform>();
       
        public PositionSource(MessageType msgType, Agent agent) {
            source = new PositionSampleSourceRot(msgType, agent)   {
                @Override
                public void onRapidMessageReceived(Agent agent, MessageType msgType, Object sampleObj, Object configObj) {
                    super.onRapidMessageReceived(agent, msgType, sampleObj, configObj);
                    PositionSample sample = (PositionSample)sampleObj;
                    PositionConfig config = (PositionConfig)configObj;
                    
                    if(debug && sample != null && config != null) {
                        logger.debug(String.format("%30s sample.hdr.srcName=%s config.hdr.srcName=%s frameName=%s", 
                                                   msgType.name(), 
                                                   sample.hdr.srcName, 
                                                   config.hdr.srcName,
                                                   config.frameName));
                    }
                }
            };
            path = new DirectionalPath(msgType.name());
        }
    }

    /**
     * FIXME: Due to auto-generated gui constraints, it's difficult to provide decent UI for this visualization
     * For now, hard-code the 3 common position sample message types
     * @param partName
     * @param parent
     * @param participantId
     */
    public RapidRobotPartMultiPoseHistory(String partName, RapidRobot parent, String participantId, float zOffset) {
        this(partName, parent, participantId, zOffset,
             Pose.GlobalPose.msgType, 
             Pose.RelativePose.msgType);
    }

    protected RapidRobotPartMultiPoseHistory(String partName, RapidRobot parent, String participantId, float zOffset, MessageType... positionSampleMessageTypes) {
        super(partName, parent, participantId);
        m_zOff = zOffset;
        for(MessageType msgType : positionSampleMessageTypes) {
            PositionSource ps = new PositionSource(msgType, getRapidRobot().getAgent());
            m_posSrcs.add(ps);
        }
    }

    ReadOnlyColorRGBA getColor(int index) {
        switch(index%6) {
        case 0: return ColorRGBA.RED;
        case 1: return ColorRGBA.GREEN;
        case 2: return ColorRGBA.BLUE;
        case 3: return ColorRGBA.YELLOW;
        case 4: return ColorRGBA.CYAN;
        case 5: return ColorRGBA.MAGENTA;
        case 6: return ColorRGBA.WHITE;
        case 7: return ColorRGBA.GRAY;
        }
        return ColorRGBA.BLACK;
    }

    String getColorString(int index) {
        switch(index%6) {
        case 0: return "RED";
        case 1: return "GREEN";
        case 2: return "BLUE";
        case 3: return "YELLOW";
        case 4: return "CYAN";
        case 5: return "MAGENTA";
        case 6: return "WHITE";
        case 7: return "GRAY";
        }
        return "BLACK";
    }

    public void printHelp() {
        for(Pose pose : Pose.values()) {
            logger.info(String.format("MultiPose: %6s = %s", getColorString(pose.ordinal()), pose.name()) );
        }
    }
    
    public void setConstantSync(boolean state) {
        m_constantSync = state;
    }

    public boolean getConstantSync() {
        return m_constantSync;
    }

    public Pose getSyncTo() {
        return m_masterPose;
    }

    public void setSyncTo(Pose poseEnum) {
        m_masterPose = poseEnum;
        m_masterIdx = poseEnum.ordinal();
    }

    public float getZOffset() {
        return m_zOff;
    }

    public void setZOffset(float val) {
        m_zOff = val;
    }

    public float getZSeparation() {
        return m_zSeparation;
    }

    public void setZSeparation(float val) {
        m_zSeparation = val;
    }

    @Override
    public void attachToNodesIn(Node model) throws IllegalStateException {
        m_node = new Node(getPartName());
        for(int i = 0; i < m_posSrcs.size(); i++) {
            PositionSource ps = m_posSrcs.get(i);
            ps.path.getSceneHints().setRenderBucketType(RenderBucketType.PostBucket);
            ps.path.setUseColors(false);
            ps.path.setDefaultColor(getColor(i));
            ps.path.setTexture(DirectionalPath.Texture.ChaseDotDotPale);
            ps.path.setLineWidth(2f-i*0.1f);
            m_node.attachChild(ps.path);
        }
        getRobot().getRobotNode().getConceptsNode().attachChild(m_node);
    }

    @Override
    public void connectTelemetry() throws TelemetryException {
        if(isTelemetryEnabled()) {
            for(PositionSource ps : m_posSrcs) {
                ps.source.connectTelemetry(getParticipantId());
            }
        }
    }

    @Override
    public void disconnectTelemetry() throws TelemetryException {
        for(PositionSource ps : m_posSrcs) {
            ps.source.disconnectTelemetry(getParticipantId());
        }
    }

    @Override
    public void onRapidMessageReceived(Agent agent, MessageType msgType, Object msgObj, Object cfgObj) {
        // nothing to do here; messages received in PositionSource
    }

    @Override
    public MessageType[] rapidMessageTypes() {
        return new MessageType[0];
    }

    @Override
    public void handleFrameUpdate(long currentTime) {
        if(currentTime-m_lastTime > m_timeInterval) {
            m_lastTime = currentTime;
            Matrix3 rot   = new Matrix3();
            Vector3 xyz   = new Vector3();
            if(m_constantSync) {
                synchronizePoses();
            }
            for(PositionSource ps : m_posSrcs) {
                ps.source.getXyz(xyz);
                final double dist = xyz.distance(ps.lastXfm.getTranslation());
                if(dist > m_spaceInterval) {
                    if(dist > m_resetInterval) {
                        reset();
                    }
                    //logger.debug(ps.source.msgType().name()+" add point ");
                    ps.source.getRot(rot);
                    if(!m_constantSync) {
                        ps.thisXfm.setTranslation(xyz);
                        ps.thisXfm.setRotation(rot);
                    }
                    ps.history.add(new Transform(ps.thisXfm));
                    while(ps.history.size() > m_histSize) {
                        ps.history.removeFirst();
                    }
                    ps.lastXfm.set(ps.thisXfm);
                }
            }
            calculatePaths();
        }
        for(PositionSource ps : m_posSrcs) {
            ps.path.handleUpdate(currentTime);
        }
    }

    void calculatePaths() {
        Transform toOrigin = new Transform();
        Transform toMaster = new Transform();
        for(int i = 0; i < m_posSrcs.size(); i++) {
            final float zAdd = m_zOff + i*m_zSeparation;
            PositionSource ps = m_posSrcs.get(i);
            final LinkedList<ReadOnlyVector3> verts = ps.path.getQueuedData();
            verts.clear();
            for(Transform t : ps.history) {
                ps.invertXfm.multiply(t, toOrigin);
                ps.masterXfm.multiply(toOrigin, toMaster);
                final ReadOnlyVector3 v = toMaster.getTranslation();
                verts.add(new Vector3(v.getX(), v.getY(), v.getZ()+zAdd));
            }
            ps.path.setDirty(true);
        }
    }

    @Override
    public void reset() {
        for(PositionSource ps : m_posSrcs) {
            ps.history.clear();
            ps.path.getQueuedData().clear();
            ps.path.setDirty(true);
        }
    }

    protected void desynchronizePoses() {
        for(PositionSource ps : m_posSrcs) {
            ps.invertXfm.setIdentity();
            ps.masterXfm.setIdentity();
        }
    }

    protected void synchronizePoses() {
        Matrix3 rot   = new Matrix3();
        Vector3 xyz   = new Vector3();
        for(PositionSource ps : m_posSrcs) {
            ps.source.getXyz(xyz);
            ps.source.getRot(rot);
            ps.thisXfm.setTranslation(xyz);
            ps.thisXfm.setRotation(rot);
            ps.thisXfm.invert(ps.invertXfm);
        }
        for(PositionSource ps : m_posSrcs) {
            ps.masterXfm = new Transform(m_posSrcs.get(m_masterIdx).thisXfm);
        }
    }

    @Trigger
    public void triggerSyncPoses() {
        logger.debug("synchronize poses");
        synchronizePoses();
        calculatePaths();
        printHelp();
    }

    @Trigger
    public void triggerDesync() {
        desynchronizePoses();
        calculatePaths();
    }

    @Trigger
    public void triggerWriteDebugFiles() {
        String basename = FileUtils.getUserDirectoryPath()+File.separator+"VerveMultiPose-";
        String filename = "";
        PrintStream output = null;
        Transform toOrigin = new Transform();
        Transform toMaster = new Transform();
        Vector3 rpy = new Vector3();
        ReadOnlyVector3 xyz;
        try {
            for(int i = 0; i < m_posSrcs.size(); i++) {
                PositionSource ps = m_posSrcs.get(i);
                filename = basename+ps.source.msgType().name()+".csv";
                output = new PrintStream(new FileOutputStream(filename));
                output.format("#x,y,z,roll,pitch,yaw : "+ps.source.msgType()+" synced to "+m_posSrcs.get(m_masterIdx).source.msgType().name());
                for(Transform t : ps.history) {
                    ps.invertXfm.multiply(t, toOrigin);
                    ps.masterXfm.multiply(toOrigin, toMaster);
                    xyz = toMaster.getTranslation();
                    rpy = RotUtil.toEulerXYZ(toMaster.getMatrix(), rpy);
                    output.format("%f,%f,%f,%f,%f,%f", 
                                  xyz.getX(), xyz.getY(), xyz.getZ(),
                                  rpy.getX(), rpy.getY(), rpy.getZ());
                                  
                }
                output.close();
            }
        }
        catch(Throwable t) {
            logger.error("Could not write debug file : "+filename);
        }
    }
}
