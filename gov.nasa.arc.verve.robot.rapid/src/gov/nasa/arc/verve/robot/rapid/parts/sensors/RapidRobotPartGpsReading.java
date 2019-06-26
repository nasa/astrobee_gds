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
package gov.nasa.arc.verve.robot.rapid.parts.sensors;

import gov.nasa.arc.verve.ardor3d.scenegraph.shape.TexRing;
import gov.nasa.arc.verve.common.scenario.ScenarioActivator;
import gov.nasa.arc.verve.common.scenario.ScenarioPreferences;
import gov.nasa.arc.verve.common.scenario.preferences.ScenarioPreferenceKeys;
import gov.nasa.arc.verve.common.ardor3d.text.BMFontManager;
import gov.nasa.arc.verve.common.ardor3d.text.BMText;
import gov.nasa.arc.verve.common.ardor3d.text.BMText.AutoScale;
import gov.nasa.arc.verve.robot.rapid.RapidRobot;
import gov.nasa.arc.verve.robot.rapid.parts.RapidRobotPart;
import gov.nasa.arc.verve.robot.scenegraph.shape.concepts.DirectionalPath;
import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.e4.message.MessageType;
import gov.nasa.rapid.v2.message.MessageTypeExtArc;

import java.util.ArrayList;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

import rapid.ext.arc.GpsSample;

import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyVector3;
import com.ardor3d.renderer.state.BlendState;
import com.ardor3d.renderer.state.BlendState.DestinationFunction;
import com.ardor3d.renderer.state.BlendState.SourceFunction;
import com.ardor3d.renderer.state.ZBufferState;
import com.ardor3d.renderer.state.ZBufferState.TestFunction;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.hint.CullHint;
import com.ardor3d.scenegraph.hint.LightCombineMode;

/**
 * @author mallan
 *
 */
public class RapidRobotPartGpsReading extends RapidRobotPart {
    //private static Logger logger = Logger.getLogger(K10PartGpsReading.class);
    boolean			        m_attached = false;

    protected TexRing       m_ring = null;
    protected BMText        m_text = null;
    protected byte			m_lastMode = 0;
    protected byte			m_lastNumSats = 0;
    protected boolean       m_doUpdateText = true;

    protected ColorRGBA     m_clr = new ColorRGBA(0.3f, 0.8f, 0.8f, 1);

    protected Vector3       m_siteFrameOffset = new Vector3();
    protected Vector3       m_gps = new Vector3(0,0,0);

    protected Node          m_posNode = null;

    protected float         m_fontScale =   0.7f;
    protected double        m_zOffset   =  -0.5;

    protected ArrayList<Vector3>      m_pathHistory     = new ArrayList<Vector3>();
    protected int                     m_pathHistoryMax  = 2000;
    protected boolean                 m_showPathHistory = false;
    protected DirectionalPath         m_pathHistoryPath = null;
    protected boolean                 m_doUpdatePathHistory = false;

    protected GpsSample     m_data = null;
    protected long          m_dataSamples = 0;

    /**
     * 
     * @param partId
     * @param parent
     */
    //=========================================================================
    public RapidRobotPartGpsReading(String partId, RapidRobot parent, String participantId, ReadOnlyVector3 siteOffset) {
        super(partId, parent, participantId);
        m_siteFrameOffset.set(siteOffset);
        m_pathHistory.ensureCapacity(m_pathHistoryMax);

        setSiteFrameFromScenarioSiteFrame();
        initializePreferenceListeners();
    }

    public float getFontScale() {
        return m_fontScale;
    }
    public void setFontScale(float val) {
        m_fontScale = val;
        if(m_text != null) {
            m_text.setFontScale(m_fontScale);
        }
    }

    public double getZOffset() {
        return m_zOffset;
    }
    public void setZOffset(double offset) {
        m_zOffset = offset;
    }

    public boolean isShowPathHistory() {
        return m_showPathHistory;
    }
    public void setShowPathHistory(boolean state) {
        m_showPathHistory = state;
        if(m_node != null) {
            if(m_pathHistoryPath != null) {
                if(m_pathHistoryPath != null) {
                    if(state) 
                        m_pathHistoryPath.getSceneHints().setCullHint(CullHint.Inherit);
                    else 
                        m_pathHistoryPath.getSceneHints().setCullHint(CullHint.Always);
                }
            }
            addPathHistorySample(null);
        }
    }

    public int getPathHistorySize() {
        return m_pathHistoryMax;
    }
    public void setPathHistorySize(int historySize) {
        m_pathHistoryMax = historySize;
    }

    /**
     */
    @Override
    public void attachToNodesIn(Node model) throws IllegalStateException {
        m_node = new Node("gps");
        m_posNode = new Node("gpsNode");
        m_node.attachChild(m_posNode);

        m_ring = new TexRing("gpsRing", 4, 5);
        m_ring.setRadius(0.05f, 0.17f);
        m_ring.setConcave(5);
        m_ring.setDefaultColor(m_clr);
        m_ring.setAlpha(1.0f);
        m_ring.setTexMultiplier(4);
        m_ring.createDefaultRenderStates();
        m_ring.getSceneHints().setLightCombineMode(LightCombineMode.CombineFirst);
        m_ring.initialize();
        m_ring.getSceneHints().setCullHint(CullHint.Always);
        m_posNode.attachChild(m_ring);

        m_text = new BMText("gpsText",
                            "GPS",
                            BMFontManager.sansSmall(),
                            //BMFontManager.monoSmall(),
                            BMText.Align.SouthWest,
                            BMText.Justify.Left);
        m_text.setFontScale(m_fontScale);
        m_text.setAutoScale(AutoScale.CapScreenSize);
        m_text.setAutoRotate(true);
        m_text.setTextColor(new ColorRGBA(0.8f, 1f, 1f, 0.2f));
        m_text.setTranslation(0,0,-0.1);
        m_posNode.attachChild(m_text);

        BlendState bs = new BlendState();
        bs.setBlendEnabled(true);
        bs.setSourceFunction(SourceFunction.SourceAlpha);
        bs.setDestinationFunction(DestinationFunction.One);
        m_text.setRenderState(bs);

        ZBufferState zs = new ZBufferState();
        zs.setFunction(TestFunction.Always);
        m_text.setRenderState(zs);

        m_pathHistoryPath = new DirectionalPath("gpsPathHistory");
        m_pathHistoryPath.setSpeed(0.25);
        m_pathHistoryPath.setDefaultColor(m_clr);
        m_pathHistoryPath.setTexture(DirectionalPath.Texture.ChaseDashHalf);
        m_pathHistoryPath.setLineWidth(1);
        m_node.attachChild(m_pathHistoryPath);
        setShowPathHistory(true);

        getRobot().getRobotNode().getSensorsNode().attachChild(m_node);
    }

    @Override
    public void handleFrameUpdate(long currentTime) {
        synchronized(m_gps) {
            if(m_doUpdateText) {
                m_doUpdateText = false;
                if(m_data == null) {
                    m_text.setText("");
                }
                else {
                    String str =              " gps: "+getModeText(m_lastMode)+", #sat="+m_lastNumSats;
                    str += "\n"+String.format(" gps: %2.3f %2.3f %2.3f", m_gps.getX(), m_gps.getY(), m_gps.getZ()-m_zOffset);
                    ReadOnlyVector3 rov = getRobot().getPoseProvider().getXyz();
                    str += "\n"+String.format("pose: %2.3f %2.3f %2.3f", rov.getX(), rov.getY(), rov.getZ());
                    m_text.setText(str);
                    //logger.info(str);
                }
            }
            if(m_doUpdatePathHistory) {
                m_pathHistoryPath.handleUpdate(currentTime);
            }
            m_posNode.setTranslation(m_gps);
        }
    }

    @Override
    public MessageType[] rapidMessageTypes() {
        return new MessageType[] { MessageTypeExtArc.GPS_SAMPLE_TYPE };
    }

    @Override
    public void onRapidMessageReceived(Agent agent, MessageType msgType, Object msgObj, Object cfgObj) {
        m_data = (GpsSample)msgObj;
        if(m_dataSamples++ == 0) {
            m_ring.getSceneHints().setCullHint(CullHint.Inherit);
        }
        synchronized(m_gps) {
            m_gps.set((m_data.xyz.userData[0] - m_siteFrameOffset.getX()), 
                      (m_data.xyz.userData[1] - m_siteFrameOffset.getY()), 
                      (m_data.xyz.userData[2] - m_siteFrameOffset.getZ()) + m_zOffset);
            if( (m_data.mode != m_lastMode) || (m_data.numSats != m_lastNumSats) ) {
                m_doUpdateText  = true;
                m_lastMode      = m_data.mode;
                m_lastNumSats   = m_data.numSats;
            }
            if(m_pathHistoryPath != null) {
                addPathHistorySample(m_gps);
            }
        }
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
            m_pathHistory.add(new Vector3(pos));
        }
        if(m_showPathHistory) {
            m_pathHistoryPath.queueUpdateData(m_pathHistory);
        }
        m_doUpdatePathHistory = true;
    }

    public static String getModeText(int mode) { 
        return "mode:FIXME"; 
    }

    //    public static String getModeText(int mode) {
    //        switch(mode) {
    //        case GPS_MODE_NONE.value:				return "none";
    //        case GPS_MODE_FIXDPOS.value:			return "FixedPos";
    //        case GPS_MODE_FIXEDHEIGHT.value:		return "FixedHeight";
    //        case GPS_MODE_FIXEDVEL.value:			return "FixedVelocity";
    //        case GPS_MODE_DOPPLER_VELOCITY .value:	return "DopplerVelocity";
    //        case GPS_MODE_SINGLE.value:				return "Single Point";
    //        case GPS_MODE_PSRDIFF.value:			return "Pseudorange Differential";
    //        case GPS_MODE_WAAS.value:				return "WAAS";
    //        case GPS_MODE_OMNISTAR.value:			return "OmniSTAR";
    //        case GPS_MODE_L1_FLOAT.value:			return "L1 Float";
    //        case GPS_MODE_IONOFREE_FLOAT.value:		return "Iono-Free Float";
    //        case GPS_MODE_NARROW_FLOAT.value:		return "Narrow Float";
    //        case GPS_MODE_L1_INT.value:				return "L1 Int";
    //        case GPS_MODE_WIDE_INT.value:			return "Wide Int";
    //        case GPS_MODE_NARROW_INT.value:			return "Narrow Int";
    //        case GPS_MODE_RTK_DIRECT_INS.value:		return "RTK Direct INS";
    //        case GPS_MODE_INS.value:				return "INS";
    //        case GPS_MODE_OMNISTAR_HP.value:		return "OmniSTAR HP";
    //        }
    //        return null;
    //    }

    @Override
    public void reset() {
        // TODO Auto-generated method stub
        m_pathHistory.clear();
        if(m_pathHistoryPath != null) {
            m_pathHistoryPath.queueUpdateData(m_pathHistory);
            m_doUpdatePathHistory = true;
        }
        m_gps.set(0,0,0);
        //        m_data = null;
        m_doUpdateText = true;
    }

    private void setSiteFrameFromScenarioSiteFrame() {
        Vector3 site = ScenarioPreferences.getSiteFrameEastingNorthingAltitude();
        m_siteFrameOffset.setX(site.getY());
        m_siteFrameOffset.setY(site.getX());
        m_siteFrameOffset.setZ(site.getZ());
    }

    protected void initializePreferenceListeners() {
        IPropertyChangeListener listener; 
        listener = new IPropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent event) {
                if(event.getProperty().equals(ScenarioPreferenceKeys.P_SITE_FRAME_LOCATION)) {
                    setSiteFrameFromScenarioSiteFrame();
                }
            }
        };
        ScenarioActivator.getDefault().getPreferenceStore().addPropertyChangeListener(listener);
    }


}
