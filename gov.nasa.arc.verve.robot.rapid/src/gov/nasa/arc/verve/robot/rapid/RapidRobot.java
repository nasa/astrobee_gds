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
package gov.nasa.arc.verve.robot.rapid;

import gov.nasa.arc.verve.robot.AbstractRobot;
import gov.nasa.arc.verve.robot.IPoseProvider;
import gov.nasa.arc.verve.robot.RobotRegistry;
import gov.nasa.arc.verve.robot.exception.TelemetryException;
import gov.nasa.arc.verve.robot.parts.IRobotPart;
import gov.nasa.arc.verve.robot.parts.PartInfo;
import gov.nasa.arc.verve.robot.rapid.parts.IRobotPartFactory;
import gov.nasa.arc.verve.robot.rapid.parts.RapidRobotPartFactory;
import gov.nasa.arc.verve.robot.scenegraph.RobotNode;
import gov.nasa.arc.verve.robot.scenegraph.RobotUpdateController;
import gov.nasa.rapid.v2.e4.Rapid;
import gov.nasa.rapid.v2.e4.agent.Agent;

import java.util.HashMap;
import java.util.Random;

import org.apache.log4j.Logger;

import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyTransform;
import com.ardor3d.math.type.ReadOnlyVector3;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.hint.CullHint;

/**
 *
 */
//==================================================================-
public class RapidRobot extends AbstractRobot {
    private static final Logger 	logger = Logger.getLogger(RapidRobot.class);

    protected static final String   primaryParticipant   = Rapid.PrimaryParticipant; // TODO replace with config option
    protected static final String   secondaryParticipant = Rapid.SecondaryParticipant;  // TODO replace with config option

    protected final Agent           m_agent;

    protected RobotNode             m_robotNode    = null;
    protected RobotUpdateController m_controller   = null;
    protected IPoseProvider         m_poseProvider = null;
    protected IRobotPartFactory     m_partFactory  = null;
    protected RapidRobotFrames      m_frames       = null;

    public static final String NAME_PRE = "rapid";
    public static final String NAME_SEP = "/";
    public static final String NAME_PREFIX = NAME_PRE+NAME_SEP;

    public static final String MODEL_FRAME_XFM_NAME = "ModelFrameXfm";

    /** common part identifiers */
    public static final String BASE                 = "Base";
    public static final String AXES                 = "Axes";
    public static final String SITE_STABLE_AXES     = "SiteStableAxes";
    public static final String POSE_HISTORY         = "PoseHistory";
    public static final String MULTIPOSE_HISTORY    = "MultiPoseHistory";
    public static final String JOINTS               = "Joints";
    public static final String FRAMES               = "Frames";
    public static final String RADIAL_GRID          = "RadialGrid";
    public static final String Z_TESTER             = "ZTester";

    public static final String COMPASS              = "Compass";
    
    public static final String NAVMAP               = "NavMap";
    public static final String TILED_LOCALMAP       = "TiledLocalMap";
    public static final String TILED_CSPACEMAP      = "TiledCSpaceMap";
    public static final String TILED_SHADER_LOCALMAP  = "TiledShaderLocalMap";
    public static final String TILED_SHADER_CSPACEMAP = "TiledShaderCSpaceMap";

    public static final String RANGESCAN            = "RangeScan";

    public static final String TRAJECTORY2D         = "Trajectory2D";
    public static final String TRAJECTORY2D_ALLSTOP = "AllStop"+TRAJECTORY2D;

    public static final String POINTCLOUD           = "PointCloud";
    public static final String LOCALMAP_POINTCLOUD  = "Localmap"+POINTCLOUD;
    public static final String DEBUG_POINTCLOUD     = "Debug"+POINTCLOUD;
    public static final String STEREO_POINTCLOUD    = "Stereo"+POINTCLOUD;

    public static final String GEOMETRY             = "Geometry";
    
    public static final String RAPID_NOTIFIER       = "RapidNotifier";
    
    // initialize telemetry enabled to false
    protected boolean m_telemetryEnabled = false;

    /** */
    protected final HashMap<String,PartInfo> m_partInfo = new HashMap<String,PartInfo>();
    protected final HashMap<String,IRobotPart>  m_parts = new HashMap<String,IRobotPart>();

    /**
     * ctor
     */
    public RapidRobot(Agent agent) {
        m_agent             = agent;
        init(new RapidPoseProvider(primaryParticipant, agent), new RapidRobotPartFactory());
    }

    protected RapidRobot(Agent agent, IPoseProvider poseProvider, IRobotPartFactory partFactory) {
        m_agent             = agent;
        init(poseProvider, partFactory);
    }

    protected void init(IPoseProvider poseProvider, IRobotPartFactory partFactory) {
        m_frames       = new RapidRobotFrames(primaryParticipant, m_agent);
        m_poseProvider = poseProvider;
        m_partFactory  = partFactory;
        Random r = new Random();
        m_iconColor.set(0.5f+r.nextFloat()*0.5f,
                        0.5f+r.nextFloat()*0.5f,
                        0.5f+r.nextFloat()*0.5f,1);
    }

    public String getGlobalParticipant() {
        return primaryParticipant;
    }

    public String getLocalParticipant() {
        return secondaryParticipant;
    }

    public Agent getAgent() {
        return m_agent;
    }

    @Override
    public String getName() {
        return NAME_PREFIX+m_agent.name();
    }

    @Override
    public String[] getPartNames() {
        String[] retVal = new String[m_parts.size()];
        retVal = m_parts.keySet().toArray(retVal);
        return retVal;
    }    

    @Override
    public IRobotPart getPart(String partName) {
        return m_parts.get(partName);
    }

    public void resetPart(String partName) {
        getPart(partName).reset();
    }

    @Override
    public IPoseProvider getPoseProvider() {
        return m_poseProvider;
    }

    public void setupPartInfo() {
        m_partInfo.put("Base",          new PartInfo(true));
        m_partInfo.put("Axes",          new PartInfo(true));
        m_partInfo.put("PoseHistory",   new PartInfo(true));
    }

    @Override
    public void attachToNodesIn(Node model)  throws IllegalStateException, TelemetryException {
        String illegal = "";

        if(model == null) {
            throw new IllegalStateException("3D Model Node cannot be null");
        }
        m_robotNode = new RobotNode(this, model);
        // XXX FIXME this is disgusting
        ((RapidPoseProvider)m_poseProvider).setRobotNode(m_robotNode);
        // -- create & attach parts
        setupPartInfo();
        IRobotPart part;
        for(String partName : m_partInfo.keySet()) {
            part = m_partFactory.createPart(partName, this);
            if(part == null) {
                illegal += getName()+"."+partName+": part is null - check "+m_partFactory.getClass().getSimpleName()+"\n";
            }
            else {
                try {
                    m_parts.put(partName, part);
                    part.attachToNodesIn(m_robotNode.getModelNode());
                }
                catch (IllegalStateException e) {
                    illegal += getName()+"."+partName+": " + e.getMessage()
                            + "\n";
                }
            }
        }

        if( illegal.length() > 0 ) {
            logger.warn(illegal);
            throw new IllegalStateException(illegal);
        }

        for(String partName : m_partInfo.keySet()) {
            getPart(partName).setVisible(m_partInfo.get(partName).visibleByDefault);
        }

        // Set up handleSceneUpdate to be called every frame
        m_controller = new RobotUpdateController(this);
        m_robotNode.addController(m_controller);

        // add robot to the registry
        // also, load persisted robot properties via the 
        // persistenceHandler that was created in 
        // the RobotActivator start() method
        RobotRegistry.register(getName(), this);
    }

    @Override
    public boolean isTelemetryEnabled() {
        return m_telemetryEnabled;
    }

    @Override
    public void setTelemetryEnabled(boolean state) throws TelemetryException {
        if(state) {
            m_telemetryEnabled = true;
            m_frames.connectTelemetry();
            m_poseProvider.connectTelemetry();
            for(IRobotPart part : m_parts.values()) {
                part.connectTelemetry();
            }
        }
        else {
            m_frames.disconnectTelemetry();
            m_poseProvider.disconnectTelemetry();
            for(IRobotPart part : m_parts.values()) {
                part.disconnectTelemetry();
            }
            m_telemetryEnabled = false;
        }
    }

    @Override
    public RobotNode getRobotNode() {
        return m_robotNode;
    }

    /**
     * Convert a point in rover frame to world coords
     */
    public Vector3 roverToWorld(ReadOnlyVector3 point, Vector3 store) {
        ReadOnlyTransform xfm = siteToWorldTransform();
        if(xfm != null) {
            xfm.applyInverse(point, store);
        }
        return store;
    }

    /**
     * Should be called every frame
     */
    @Override
    public void handleFrameUpdate(long currentTimeMillis) {
        if(!m_robotNode.getSceneHints().getCullHint().equals(CullHint.Always)) {
            m_poseProvider.calculateTransform();
            
            try {
                for(IRobotPart part : m_parts.values()) {
                    if(part.isVisible()) {
                        part.handleFrameUpdate(currentTimeMillis);
                    }
                }
            }
            catch (Throwable t) {
                logger.error("Exception in "+this.getClass().getSimpleName()+".handleUpdate()", t);
            }
            updateInterestPointListeners();
        }
    }

    @Override
    protected ReadOnlyVector3 getNextGoalPosition() {
        return null;
    }

    @Override
    public boolean isInterestPointEnabled() {
        return true;
    }

}
