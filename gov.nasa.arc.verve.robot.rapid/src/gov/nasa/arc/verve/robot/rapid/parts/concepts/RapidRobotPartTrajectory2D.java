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

import gov.nasa.arc.verve.ardor3d.scenegraph.shape.TexQuad;
import gov.nasa.arc.verve.common.ardor3d.util.RotUtil;
import gov.nasa.arc.verve.common.DataBundleHelper;
import gov.nasa.arc.verve.common.VerveBaseMap;
import gov.nasa.arc.verve.robot.rapid.RapidRobot;
import gov.nasa.arc.verve.robot.rapid.parts.RapidRobotPart;
import gov.nasa.arc.verve.robot.scenegraph.shape.concepts.DirectionalPath;
import gov.nasa.arc.verve.utils.rapid.RapidVerve;
import gov.nasa.rapid.util.math.RapidMath;
import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.e4.message.MessageType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import rapid.RotationEncoding;
import rapid.ext.RTrans2DInterpretation;
import rapid.ext.RTrans2DMeta;
import rapid.ext.RTransMetaSequence;
import rapid.ext.Trajectory2DConfig;
import rapid.ext.Trajectory2DSample;

import com.ardor3d.image.Texture.MagnificationFilter;
import com.ardor3d.image.Texture2D;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Matrix3;
import com.ardor3d.math.Transform;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyColorRGBA;
import com.ardor3d.math.type.ReadOnlyVector3;
import com.ardor3d.renderer.state.BlendState;
import com.ardor3d.renderer.state.CullState;
import com.ardor3d.renderer.state.CullState.Face;
import com.ardor3d.renderer.state.TextureState;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.hint.CullHint;
import com.ardor3d.scenegraph.hint.LightCombineMode;

/**
 * 
 * @author mallan
 *
 */
public class RapidRobotPartTrajectory2D extends RapidRobotPart {
    private static final Logger logger = Logger.getLogger(RapidRobotPartTrajectory2D.class);

    protected Trajectory2DSample  m_sample = null;
    protected Trajectory2DConfig  m_config = null;

    protected final List<TexQuad> m_quads     = new ArrayList<TexQuad>();
    protected Node                m_quadNode  = null; 
    protected float               m_quadSize  = 0.75f;
    protected float               m_quadSize2 = m_quadSize/2;
    protected boolean             m_showQuads = true;
    
    protected ColorRGBA           m_midColor  = new ColorRGBA(ColorRGBA.CYAN);
    protected ColorRGBA           m_endColor  = new ColorRGBA(0.8f, 1, 1, 1);

    protected DirectionalPath     m_line      = null;
    protected final List<Vector3> m_lineVerts = new ArrayList<Vector3>();
    protected final List<ReadOnlyColorRGBA> m_lineColors = new ArrayList<ReadOnlyColorRGBA>();

    protected Texture2D           m_quadTex;
    protected Texture2D           m_lineTex;
    protected TextureState        m_tsNorm;
    protected TextureState        m_tsFlash;

    
    protected float               m_zDir = 0;
    protected float               m_zOff = 0;

    protected final List<TrajPose> m_processedTrajectory = new ArrayList<TrajPose>();

    protected boolean             m_useRobotXfm = false;
    protected boolean             m_useRobotZ   = true;

    protected final MessageType   SAMPLE_TYPE;

    protected class TrajPose {
        public final Transform    pose = new Transform();
        public RTransMetaSequence meta = null;
        public boolean            quad = false;
    }

//    public RapidRobotPartTrajectory2D(String partName, RapidRobot parent, String participantId,  MessageType sampleType) {
//        this(partName, parent, participantId, sampleType, false, 0);
//    }

    public RapidRobotPartTrajectory2D(String partName, RapidRobot parent, String participantId, float zOffset, MessageType sampleType, boolean useRobotZByDefault) {
        super(partName, parent, participantId);
        SAMPLE_TYPE = sampleType;
        m_useRobotZ = useRobotZByDefault;
        m_zOff = zOffset;
    }

    @Override
    public MessageType[] rapidMessageTypes() { 
        return new MessageType[] { SAMPLE_TYPE }; 
    }

    public float getZOffset() {
        return m_zOff;
    }

    public void setZOffset(float zOffset) {
        m_zOff = zOffset;
    }

    public float getSize() {
        return m_quadSize;
    }

    public void setSize(float size) {
        m_quadSize  = size;
        m_quadSize2 = size/2;
        for(TexQuad quad : m_quads) {
            quad.setSize(m_quadSize);
        }
    }

    public boolean isShowArrows() {
        return m_showQuads;
    }

    public void setShowArrows(boolean state) {
        m_showQuads = state;
        if(m_quadNode != null) {
            if(m_showQuads) {
                m_quadNode.getSceneHints().setCullHint(CullHint.Inherit);
            }
            else {
                m_quadNode.getSceneHints().setCullHint(CullHint.Always);
            }
        }
    }

    public boolean isUseRobotTransform() {
        return m_useRobotXfm;
    }

    public void setUseRobotTransform(boolean state) {
        m_useRobotXfm = state;
    }

    public boolean isUseRobotZ() {
        return m_useRobotZ;
    }

    public void setUseRobotZ(boolean state) {
        m_useRobotZ = state;
    }

    @Override
    public void attachToNodesIn(Node model) throws IllegalStateException {
        m_node = new Node("Trajectory2D");
        m_quadNode = new Node("quads");
        m_quadNode.getSceneHints().setLightCombineMode(LightCombineMode.Off);

        BlendState bs = new BlendState();
        bs.setTestEnabled(true);
        bs.setReference(0.5f);
        m_quadNode.setRenderState(bs);
        CullState cs = new CullState();
        cs.setCullFace(Face.None);
        m_quadNode.setRenderState(cs);

        m_line = new DirectionalPath("Trajectory2DPath");
        m_line.setDefaultColor(m_midColor);
        m_line.setUseColors(true);
        m_line.setTexture(DirectionalPath.Texture.ChaseDotDotGrey, MagnificationFilter.NearestNeighbor);
        m_line.setLineWidth(4);
        m_line.getSceneHints().setCullHint(CullHint.Never);
        m_line.setSpeed(-m_line.getSpeed());
        m_lineTex = m_line.getTexture();

        m_node.attachChild(m_quadNode);
        m_node.attachChild(m_line);

        try {
            m_quadTex = DataBundleHelper.loadTexture("robot.rapid", "images/trajectory_arrow.png");
            m_tsNorm = new TextureState();
            m_tsNorm.setTexture(m_quadTex, 0);
            m_tsNorm.setEnabled(true);
            m_tsFlash = new TextureState();
            m_tsFlash.setTexture(m_quadTex, 0);
            m_tsFlash.setTexture(m_lineTex, 1);
            m_tsFlash.setEnabled(true);
            m_quadNode.setRenderState(m_tsNorm);
        } 
        catch (IOException e) {
            logger.error("Could not load texture", e);
        }

        getRobot().getRobotNode().getConceptsNode().attachChild(m_node);
        // pre-allocate some geometry
        allocateGeometry(100);
        //testInit();
    }

    @Override
    public void onRapidMessageReceived(Agent agent, MessageType msgType, Object msgObj, Object cfgObj) {
        if(msgType == SAMPLE_TYPE) {
            m_sample = (Trajectory2DSample)msgObj;
            m_config = (Trajectory2DConfig)cfgObj;
        }
        setDirty(true);
    }

    int cnt = 0;
    @Override
    public void handleFrameUpdate(long currentTime) {
        cnt++;
        if(isDirty()) {
            modifyReset();
            
            Matrix3 rot = new Matrix3();
            Vector3 xyz = new Vector3();
            Vector3 vec = new Vector3();

            if(m_zDir == 0) { // we cannot determine z up until robot is attached to scenegraph
                vec = getRapidRobot().roverToWorld(new Vector3(0,0,1), vec);
                if(vec.getZf() < 0) 
                    m_zDir = -1;
                else 
                    m_zDir = 1;
            }
            m_quadNode.detachAllChildren();

            Trajectory2DSample sample = m_sample;
            Trajectory2DConfig config = m_config;
            if(sample == null) {
                logger.debug("sample is null");
                return;
            }
            int quadSkip = 10;
            RTrans2DInterpretation interpretation = RTrans2DInterpretation.RTRANS2D_RELATIVE_TO_ORIGIN;
            if(config != null) {
                interpretation = m_config.trajectoryInterp;
                if(m_config.samplingInterval > 0) {
                    quadSkip = (int)(1000000l/m_config.samplingInterval);
                }
            }
            Transform rXfm = new Transform();
            Transform tXfm = new Transform();
            Transform currXfm = new Transform();
            Transform origXfm = new Transform();
            float     lastZ   = 0;
            float     origZ   = 0;
            float     diffZ   = 0;
            float     currZ   = 0;
            float     zAdd    = 0;
            final float zAddInit = m_zDir * 0.001f;

            rot = RapidVerve.toArdor(sample.origin.rot.userData, RotationEncoding.RAPID_ROT_M33, rot);
            // get rotation w/o roll and pitch
            vec = RotUtil.toEulerXYZ(rot, vec);
            vec.setX(0);
            vec.setY(0);
            rot = RotUtil.toMatrixXYZ(vec, rot);
            // get xyz
            xyz = RapidVerve.toArdor(sample.origin.xyz.userData, xyz);
            if(m_useRobotZ) {
                xyz.setZ(getRobot().getPoseProvider().getXyz().getZf());
            }
            xyz.addLocal(0,0,m_zOff);

            // get z from basemap
            vec = getRapidRobot().roverToWorld(xyz, vec);
            origZ = lastZ = VerveBaseMap.getHeightAt(vec.getXf(), vec.getYf());
            currXfm = new Transform();

            if(m_useRobotXfm) { // unnecessarily expensive, but it's for debug purposes only so no big deal
                xyz.set(getRobot().getPoseProvider().getTransform().getTranslation());
                xyz.addLocal(0,0,m_zOff);
                vec = RotUtil.toEulerXYZ(getRobot().getPoseProvider().getTransform().getMatrix(), vec);
                vec.setX(0);
                vec.setY(0);
                rot = RotUtil.toMatrixXYZ(vec, rot);
            }

            currXfm.setTranslation(xyz);
            currXfm.setRotation(rot);
            origXfm.set(currXfm);

            TrajPose trjPose;
            int numPts = sample.trajectory.userData.size();
            //TexQuad quad;
            RTrans2DMeta rx;
            allocateGeometry(numPts+1);

            for(int i = 0; i < numPts; i++) {
                rx = (RTrans2DMeta)sample.trajectory.userData.get(i);
                rot.fromAngleNormalAxis(rx.theta, Vector3.UNIT_Z);
                rXfm.setRotation(rot);
                rXfm.setTranslation(rx.x, rx.y, 0);

                if(interpretation == RTrans2DInterpretation.RTRANS2D_RELATIVE_TO_PREVIOUS) {
                    tXfm = currXfm.multiply(rXfm, tXfm);
                    currXfm.set(tXfm);
                    zAdd = zAddInit;
                    xyz.set(currXfm.getTranslation());
                    diffZ = lastZ;
                }
                else if(interpretation == RTrans2DInterpretation.RTRANS2D_RELATIVE_TO_ORIGIN) {
                    tXfm = origXfm.multiply(rXfm, tXfm);
                    currXfm.set(tXfm);
                    zAdd = zAddInit*i;
                    xyz.set(currXfm.getTranslation());
                    diffZ = origZ;
                }

                if(origZ == origZ) {
                    vec   = getRapidRobot().roverToWorld(xyz, vec);
                    currZ = VerveBaseMap.getHeightAt(vec.getXf(), vec.getYf());
                    zAdd += m_zDir*(currZ - diffZ);
                    lastZ = currZ;
                }
                trjPose = m_processedTrajectory.get(i);
                trjPose.pose.setTranslation(xyz.getX(), xyz.getY(), zAdd+xyz.getZ());
                trjPose.pose.setRotation(currXfm.getMatrix());
                trjPose.meta = rx.meta;
            }

            TexQuad quad;
            List<ReadOnlyVector3> lineVerts = m_line.getQueuedData();
            List<ReadOnlyColorRGBA> lineColors = m_line.getQueuedColors();
            lineVerts.clear();
            lineColors.clear();
            int r = 0;
            for(int i = numPts-1; i >= 0; i--, r++) {
                trjPose = m_processedTrajectory.get(i);
                final ReadOnlyColorRGBA clr = modifyLineVertex(trjPose.pose, trjPose.meta);
                lineVerts.add(trjPose.pose.getTranslation());
                lineColors.add(clr);
                if(r%quadSkip == 0) {
                    quad = m_quads.get(i);
                    modifyQuad(trjPose.pose, quad, trjPose.meta);
                    m_quadNode.attachChild(quad);
                }
            }
            m_line.setDirty(true);
            this.setDirty(false);
        }
        m_line.handleUpdate(currentTime);
    }

    protected void modifyReset() {
        // empty
    }

    protected void modifyQuad(Transform xfm, TexQuad quad, RTransMetaSequence meta) {
        quad.setTransform(xfm);
    }

    protected ReadOnlyColorRGBA modifyLineVertex(Transform xfm, RTransMetaSequence meta) {
        return m_midColor;
    }

    @Override
    public void reset() {
        m_sample = null;
        m_config = null;
        m_line.updateData(0, null, null);
        m_quadNode.detachAllChildren();
    }

    /**
     * Create new geometry for segments, if necessary
     * @param nsegs
     */
    protected void allocateGeometry(int nsegs) {
        int numEndsNeeded = nsegs - m_quads.size();
        if( numEndsNeeded > 0) {
            for(int i = 0; i < numEndsNeeded; i++) {
                TexQuad tq = new TexQuad("quad", m_quadSize, m_quadSize, false, true);
                tq.setDefaultColor(m_midColor);
                m_quads.add(tq);
                m_lineVerts.add(new Vector3());
                m_lineColors.add(new ColorRGBA());
                m_processedTrajectory.add(new TrajPose());
            }
        }
    }

    protected void testInit() {
        Trajectory2DSample sample = new Trajectory2DSample();
        RapidMath.identity3x3(sample.origin.rot.userData);
        sample.origin.xyz.userData[2] = 7.3;
        RTrans2DMeta last = null;
        for(int i = 0; i < m_quads.size()-1; i++) {
            RTrans2DMeta t = new RTrans2DMeta();
            if(last == null) {
                t.x = 0.5f;
                t.y = 0;
                t.theta = 0.025f;
            }
            else {
                t.x = last.x+0.5f;
                t.y = last.y+0.1f;
                t.theta = last.theta+0.025f;
            }
            sample.trajectory.userData.add(t);
            last = t;
        }
        onRapidMessageReceived(getRapidRobot().getAgent(), SAMPLE_TYPE, sample, null);
    }
}
