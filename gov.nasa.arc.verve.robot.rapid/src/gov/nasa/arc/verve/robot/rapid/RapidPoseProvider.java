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

import gov.nasa.arc.verve.common.ardor3d.Ardor3D;
import gov.nasa.arc.verve.ardor3d.scenegraph.util.SpatialPath;
import gov.nasa.arc.verve.common.IVerveScene;
import gov.nasa.arc.verve.common.SceneHack;
import gov.nasa.arc.verve.common.VerveBaseMap;
import gov.nasa.arc.verve.common.scenario.ScenarioActivator;
import gov.nasa.arc.verve.common.scenario.ScenarioPreferences;
import gov.nasa.arc.verve.robot.IPoseProvider;
import gov.nasa.arc.verve.robot.exception.TelemetryException;
import gov.nasa.arc.verve.robot.scenegraph.RobotNode;
import gov.nasa.rapid.v2.e4.message.MessageType;
import gov.nasa.rapid.v2.e4.agent.Agent;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

import com.ardor3d.math.Matrix3;
import com.ardor3d.math.Transform;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyTransform;
import com.ardor3d.math.type.ReadOnlyVector3;
import com.ardor3d.scenegraph.Node;

/**
 *
 */
public class RapidPoseProvider implements IPoseProvider {
    private static final Logger logger = Logger.getLogger(RapidPoseProvider.class);

    protected final Agent  m_agent;
    protected String       m_participant;
    protected Vector3      m_xyz        = new Vector3();
    protected Matrix3      m_rot        = new Matrix3();
    protected Transform    m_xfm        = new Transform();
    protected IRotSource   m_rotSource  = null;
    protected IXyzSource   m_xyzSource  = null;

    protected RobotNode    m_robotNode  = null;
    protected Transform    m_siteToRoot = null;
    protected Transform    m_rootToSite = null;

    protected double       m_fixedZ     = 0;
    protected boolean      m_isFixedZ   = false;
    protected double       m_siteAltitude = ScenarioPreferences.getSiteFrameAltitude();

    protected final List<IRotSource> m_rotSources = new ArrayList<IRotSource>();
    protected final List<IXyzSource> m_xyzSources = new ArrayList<IXyzSource>();

    protected final HashMap<MessageType,Integer> m_subscriptions = new HashMap<MessageType,Integer>();

    protected boolean m_connected = false;

    /**
     * ctor
     */
    //============================================================
    public RapidPoseProvider(String participantId, Agent agent) {
        m_participant = participantId;
        m_agent = agent;
        m_xyz.set(0,0,0);
        m_xfm.setTranslation(m_xyz);
        initializePreferenceListeners();
        // add default position sample source
        MessageType msgType = MessageType.POSITION_SAMPLE_TYPE;
        m_rotSources.add(m_rotSource = new PositionSampleSourceRot(msgType, agent));
        m_xyzSources.add(m_xyzSource = new PositionSampleSourceXyz(msgType, agent));
        msgType = MessageType.RELATIVE_POSITION_SAMPLE_TYPE;
        m_rotSources.add(new PositionSampleSourceRot(msgType, agent));
        m_xyzSources.add(new PositionSampleSourceXyz(msgType, agent));
    }

    public synchronized void setParticipantId(String participantId) {
        //logger.debug(m_agent.name()+" PoseProvider.setParticipantId "+participantId);
        if(!m_connected) {
            m_participant = participantId;
        }
        else {
            try {
                disconnectTelemetry();
                m_participant = participantId;
                connectTelemetry();
            }
            catch(TelemetryException te) {
                logger.error("Could not set ParticipantId", te);
            }
        }
    }

    public String getParticipantId() {
        return m_participant;
    }

    /**
     * ugh, this is ugly
     */
    public void setRobotNode(RobotNode robotNode) {
        m_robotNode = robotNode;
    }


    /**
     * @throws TelemetryException 
     * 
     */
    public synchronized void connectTelemetry() throws TelemetryException {
        m_xyzSource.connectTelemetry(m_participant);
        m_rotSource.connectTelemetry(m_participant);
        m_connected = true;
    }

    /**
     * @throws TelemetryException 
     * 
     */
    public synchronized void disconnectTelemetry() throws TelemetryException {
        m_xyzSource.disconnectTelemetry(m_participant);
        m_rotSource.disconnectTelemetry(m_participant);
        m_connected = false;
    }

    /**
     * get names of all rotation sources
     */
    public String[] getRotSourceValues() {
        String[] retVal = new String[m_rotSources.size()];
        for(int i = 0; i < m_rotSources.size(); i++) {
            retVal[i] = m_rotSources.get(i).name();
        }
        return retVal;
    }

    /**
     * get names of all xyz sources
     */
    public String[] getXyzSourceValues() {
        String[] retVal = new String[m_xyzSources.size()];
        for(int i = 0; i < m_xyzSources.size(); i++) {
            retVal[i] = m_xyzSources.get(i).name();
        }
        return retVal;
    }

    public synchronized void setRotSource(String name) {
        IRotSource newSource = null;
        for(IRotSource src : m_rotSources) {
            if(src.name().equals(name)) {
                newSource = src;
                break;
            }
        }
        if(newSource != null) {
            if(!m_connected) {
                m_rotSource = newSource;
            }
            else {
                try {
                    m_rotSource.disconnectTelemetry(m_participant);
                    m_rotSource = newSource;
                    m_rotSource.setData(null, null);
                    m_rotSource.connectTelemetry(m_participant);
                }
                catch(Throwable t) {
                    logger.error("Could not set rotation source", t);
                }
            }
        }
        else {
            logger.warn("bad rotation source name: "+name);
        }
    }

    public synchronized void setXyzSource(String name) {
        IXyzSource newSource = null;
        for(IXyzSource src : m_xyzSources) {
            if(src.name().equals(name)) {
                newSource = src;
                break;
            }
        }
        if(newSource != null) {
            if(!m_connected) {
                m_xyzSource = newSource;
            }
            else {
                try {
                    m_xyzSource.disconnectTelemetry(m_participant);
                    m_xyzSource = newSource;
                    m_xyzSource.setData(null, null);
                    m_xyzSource.connectTelemetry(m_participant);
                }
                catch(Throwable t) {
                    logger.error("Could not set xyz source", t);
                }
            }
        }
        else {
            logger.warn("bad xyz source name: "+name);
        }
    }

    public String getRotSource() {
        return m_rotSource.name();
    }

    public String getXyzSource() {
        return m_xyzSource.name();
    }

    /**
     * Get xyz estimate from most recent call to calculateTransform
     * @param retVal Vector3 to store xyz values
     * @returns retVal
     *///-----------------------------------------------------------------
    public Vector3 getXyz(Vector3 retVal) {
        retVal.set(m_xfm.getTranslation());
        return retVal;
    }

    public ReadOnlyVector3 getXyz() {
        return getXyz(m_xyz);
    }

    /**
     * this should be called at the beginning of every frame to calculate latest transform
     */
    public ReadOnlyTransform calculateTransform() {
        m_xyzSource.getXyz(m_xyz);
        if( m_isFixedZ ) {
            m_xyz.setZ(getZFromBaseMap(m_xyz));
        }
        m_rotSource.getRot(m_rot);
        m_xfm.setRotation(m_rot);
        m_xfm.setTranslation(m_xyz);
        if(m_doDebug) {
            writeDebugFile();
        }
        return m_xfm;
    }

    /**
     * parts should retrieve the cached transform
     */
    public ReadOnlyTransform getTransform() {
        return m_xfm;
    }

    public void setFixedZOffset(double fixedZ) {
        m_fixedZ = fixedZ;
    }

    public double getFixedZOffset() {
        return m_fixedZ;
    }

    public void setFixedZ(boolean use) {
        m_isFixedZ = use;
        calculateTransform();
    }
    public boolean isFixedZ() {
        return m_isFixedZ;
    }

    protected Transform getSiteToRootTransform() {
        try {
            if(m_siteToRoot == null) {
                if(m_robotNode.getParent() != null) {
                    final IVerveScene verveScene = SceneHack.getMainScene();
                    final Node root = verveScene.getRoot();
                    SpatialPath path = new SpatialPath(m_robotNode, root, true);
                    m_rootToSite = new Transform();
                    m_siteToRoot = new Transform();
                    path.getTransform(m_rootToSite);
                    m_rootToSite.setTranslation(0,0,0); // we want rotation only
                    m_rootToSite.invert(m_siteToRoot);
                }
            }
            final boolean dbg = false;
            if(dbg) {
                try {
                    System.err.println("m_rootToSite = \n"+Ardor3D.format(m_rootToSite));
                    System.err.println("m_siteToRoot = \n"+Ardor3D.format(m_siteToRoot));
                }
                catch(Throwable t) {
                    t.printStackTrace();
                }
            }
        }
        catch(Throwable t) {
            logger.error("error getting siteToRootTransform", t);
        }
        return m_siteToRoot;
    }

    protected Transform getRootToSiteTransform() {
        if(m_rootToSite == null) {
            getSiteToRootTransform();
        }
        return m_rootToSite;
    }

    public double getZFromBaseMap(Vector3 xyz) { 
        if(m_robotNode != null && m_robotNode.getParent() != null) {
            Vector3 rv = new Vector3(xyz);
            final Transform xfm = getSiteToRootTransform();
            xfm.applyInverse(rv);
            double z = VerveBaseMap.getHeightAt(rv.getXf(),rv.getYf()) - m_siteAltitude;
            if(z == z) {
                rv.setZ(z);
                getRootToSiteTransform().applyInverse(rv);
                z = rv.getZf();
                return z + m_fixedZ;
            }
        }
        return m_fixedZ;
    }

    public void setInitialPosition(double nOfTotal, double radius, double z) {
        double a = nOfTotal * 2 * Math.PI;
        double x = radius * Math.sin(a);
        double y = radius * Math.cos(a);
        m_xyz.set(x,y,z);
        m_xfm.setTranslation(m_xyz);
    }

    protected void initializePreferenceListeners() {
        IPropertyChangeListener propertyChangeListener; 
        propertyChangeListener = new IPropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent event) {
                m_siteAltitude = ScenarioPreferences.getSiteFrameAltitude();
                m_siteToRoot = null;
                m_rootToSite = null;
                getSiteToRootTransform();
            }
        };
        ScenarioActivator.getDefault().getPreferenceStore().addPropertyChangeListener(propertyChangeListener);
    }

    protected boolean           m_doDebug = false;
    private   FileOutputStream  d_fout    = null;
    private   PrintStream       d_ps      = null;

    public boolean isWriteDebugFile() {
        return m_doDebug;
    }

    public void setWriteDebugFile(boolean state) {
        m_doDebug = state;
        if(m_doDebug == false && d_fout != null) {
            try {
                d_ps.close();
                d_fout.close();
            } 
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected void writeDebugFile() {
        try {
            if(d_fout == null) {
                String filename = String.format("%s/RapidPoseProvider-xyzSource-"+m_agent.toString()+".csv", System.getProperty("user.home"));
                d_fout = new FileOutputStream (filename);
                d_ps = new PrintStream(d_fout);
                d_ps.println(m_xyzSource.debugFileHeader());
            }
            d_ps.println(m_xyzSource.debugFileRow());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
