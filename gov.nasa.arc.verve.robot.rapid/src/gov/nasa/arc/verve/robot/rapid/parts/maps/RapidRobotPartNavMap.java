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
package gov.nasa.arc.verve.robot.rapid.parts.maps;

import gov.nasa.arc.verve.common.util.ColorTable;
import gov.nasa.arc.verve.robot.rapid.RapidRobot;
import gov.nasa.arc.verve.robot.rapid.parts.RapidRobotPart;
import gov.nasa.arc.verve.robot.scenegraph.shape.sensors.NavMapHeightField;
import gov.nasa.arc.verve.robot.scenegraph.shape.sensors.NavMapHeightField.HeightMapData;
import gov.nasa.arc.verve.utils.rapid.RapidVerve;
import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.e4.message.MessageType;
import gov.nasa.rapid.v2.e4.message.holders.NavMapHolder;

import java.util.HashMap;

import org.apache.log4j.Logger;

import rapid.RotationEncoding;
import rapid.ext.NAVMAP_CERTAINTY;
import rapid.ext.NAVMAP_GOODNESS;
import rapid.ext.NAVMAP_HEIGHT;
import rapid.ext.NAVMAP_NORMALS;
import rapid.ext.NAVMAP_NUM_OCTET_LAYERS;
import rapid.ext.NAVMAP_NUM_SHORT_LAYERS;
import rapid.ext.NAVMAP_ROUGHNESS;
import rapid.ext.NavMapConfig;
import rapid.ext.NavMapSample;
import rapid.ext.OctetMapLayer;
import rapid.ext.ShortMapLayer;

import com.ardor3d.image.Texture.MagnificationFilter;
import com.ardor3d.math.FastMath;
import com.ardor3d.math.Matrix3;
import com.ardor3d.math.Vector3;
import com.ardor3d.renderer.state.BlendState;
import com.ardor3d.renderer.state.RenderState.StateType;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.hint.PickingHint;

/**
 * non-tiled NavMap visualization. Uses simple texture and geometry. 
 * @author mallan
 *
 */
public class RapidRobotPartNavMap extends RapidRobotPart {
    private static final Logger logger = Logger.getLogger(RapidRobotPartNavMap.class);
    protected NavMapHolder      m_data          = null;
    protected Vector3           m_xyz           = new Vector3(0,0,0);
    protected Matrix3           m_rot           = new Matrix3();
    protected double            m_zOffset       = 0;
    protected Node              m_mapNode       = null;
    //protected float             m_flip          = 1;

    protected NavMapHeightField m_navMap        = null;
    int                         m_goodThreshold = 100;
    int                         m_badThreshold  =  20;
    protected ColorTable        m_colorTable    = new ColorTable(m_badThreshold, m_goodThreshold);
    float                       m_minCertainty  =   0.1f;

    final String HEIGHT    = NAVMAP_HEIGHT.VALUE;
    final String GOODNESS  = NAVMAP_GOODNESS.VALUE;
    final String ROUGHNESS = NAVMAP_ROUGHNESS.VALUE;
    final String CERTAINTY = NAVMAP_CERTAINTY.VALUE;
    final String NORMALS   = NAVMAP_NORMALS.VALUE;
    protected HashMap<String,Integer> m_shortNameMap = new HashMap<String,Integer>(3*NAVMAP_NUM_SHORT_LAYERS.VALUE/2);
    protected HashMap<String,Integer> m_octetNameMap = new HashMap<String,Integer>(3*NAVMAP_NUM_OCTET_LAYERS.VALUE/2);
    protected int                     m_lastSerial  = -1;

    protected static final float zSign = -1; // liam always does z down normals
    public final MessageType    SAMPLE_TYPE;
    public final MessageType    CONFIG_TYPE;

    /**
     * 
     * @param partId
     * @param parent
     */
    public RapidRobotPartNavMap(String partName, RapidRobot parent, String participantId, double zOffset, MessageType navMapMessageType) {
        super(partName, parent, participantId);
        m_zOffset = zOffset;
        SAMPLE_TYPE = navMapMessageType;
        CONFIG_TYPE = MessageType.valueOf(SAMPLE_TYPE.getConfigName());
    }

    @Override
    public MessageType[] rapidMessageTypes() {
        MessageType[] retVal = new MessageType[] { SAMPLE_TYPE, CONFIG_TYPE };
        return retVal;
    }

    //    public boolean isFlipNormals() {
    //        return m_flip > 0;
    //    }
    //    
    //    public void setFlipNormals(boolean doFlip) {
    //        if(doFlip) m_flip = -1;
    //        else       m_flip =  1;
    //    }
    int foo = 1;

    @Override
    public void onRapidMessageReceived(Agent agent, MessageType type, Object eventObj, Object configObj) {
        if(type == CONFIG_TYPE) {
            //
        }
        else if(type == SAMPLE_TYPE) {
            //logger.debug(SAMPLE_TYPE.name()+" received");
            try {
                NavMapHolder navMapData = new NavMapHolder(eventObj, configObj);
                m_data = navMapData;
                getRobot().getPoseProvider().getXyz(m_xyz);

                if(foo == 0) {
                    if(m_data.config != null && m_data.sample != null) {
                        foo++;
                        System.err.println("Config="+m_data.config.toString());
                        System.err.println("Sample="+m_data.sample.toString());
                    }
                }

                m_xyz.set(navMapData.sample.location.xyz.userData[0],
                          navMapData.sample.location.xyz.userData[1],
                          navMapData.sample.location.xyz.userData[2]+m_zOffset);
                m_rot = RapidVerve.toArdor(navMapData.sample.location.rot.userData, RotationEncoding.RAPID_ROT_M33, m_rot);
                if(false) { //configObj != null) {
                    NavMapConfig config = (NavMapConfig)configObj;
                    NavMapSample sample = (NavMapSample)eventObj;
                    int numNames;
                    numNames = config.octetLayerNames.userData.size();
                    boolean printNames = false;
                    if(printNames) {
                        for(int i = 0; i < numNames; i++) {
                            String name = (String)config.octetLayerNames.userData.get(i);
                            OctetMapLayer layer = (OctetMapLayer)sample.octetLayers.userData.get(i);
                            logger.debug("   octet name = "+name);
                            logger.debug("      density = "+layer.density);
                        }
                        numNames = config.shortLayerNames.userData.size();
                        for(int i = 0; i < numNames; i++) {
                            String name = (String)config.shortLayerNames.userData.get(i);
                            ShortMapLayer layer = (ShortMapLayer)sample.shortLayers.userData.get(i);
                            logger.debug("short name = "+name);
                            logger.debug("      density = "+layer.density);

                        }
                    }
                }
            }
            catch(Throwable t) {
                t.printStackTrace();
            }
        }
        setDirty(true);
    }


    @Override
    public void attachToNodesIn(Node model) throws IllegalStateException {
        m_navMap  = new NavMapHeightField();
        m_mapNode = new Node("MapNode");
        m_mapNode.attachChild(m_navMap);
        m_node    = new Node(getPartName());
        m_node.attachChild(m_mapNode);
        getRobot().getRobotNode().getConceptsNode().attachChild(m_node);
    }

    public boolean isPickable() {
        return m_navMap.getSceneHints().isPickingHintEnabled(PickingHint.Pickable);
    }

    public void setPickable(boolean state) {
        //logger.debug("setPickable("+state+")");
        m_navMap.getSceneHints().setPickingHint(PickingHint.Pickable, state);
    }

    public boolean isAlphaTest() {
        BlendState bs = (BlendState)m_navMap.getLocalRenderState(StateType.Blend);
        return bs.isTestEnabled();
    }

    public void setAlphaTest(boolean state) {
        //logger.debug("setAlphaTest("+state+")");
        BlendState bs = (BlendState)m_navMap.getLocalRenderState(StateType.Blend);
        bs.setTestEnabled(state);
        bs.setBlendEnabled(false);
    }

    public void setGoodnessCellInterpolation(MagnificationFilter magFilter) {
        m_navMap.getNavMapTexture().setMagnificationFilter(magFilter);
    }

    public MagnificationFilter getGoodnessCellInterpolation() {
        return m_navMap.getNavMapTexture().getMagnificationFilter();
    }

    public double getZOffset() {
        return m_zOffset;
    }

    public void setZOffset(double zOffset) {
        m_zOffset = zOffset;
    }

    public int getGoodThreshold() {
        return m_goodThreshold;
    }

    public void setGoodThreshold(int goodThreshold) {
        m_goodThreshold = goodThreshold;
        m_colorTable.createColorTable(m_badThreshold, m_goodThreshold);
    }

    public int getBadThreshold() {
        return m_badThreshold;
    }

    public void setBadThreshold(int badThreshold) {
        m_badThreshold = badThreshold;
        m_colorTable.createColorTable(m_badThreshold, m_goodThreshold);
    }

    public float getMinCertainty() {
        return m_minCertainty;
    }

    public void setMinCertainty(float minCertainty) {
        m_minCertainty = minCertainty;
    }

    protected void updateNameMaps(NavMapConfig config) {
        m_shortNameMap.clear();
        for(int i = 0; i < config.shortLayerNames.userData.size(); i++) {
            m_shortNameMap.put((String)config.shortLayerNames.userData.get(i), i);
        }
        m_octetNameMap.clear();
        for(int i = 0; i < config.octetLayerNames.userData.size(); i++) {
            m_octetNameMap.put((String)config.octetLayerNames.userData.get(i), i);
        }
    }

    /**
     * FIXME: this assumes that the sizes of height, goodness, and certainty are all the same
     */
    @Override
    public void handleFrameUpdate(long currentTime) {
        if(isDirty() && m_data != null) {
            HeightMapData hmd = m_navMap.borrowHeightMapData();
            try {
                final NavMapSample navMapSmp = m_data.sample;
                final NavMapConfig navMapCfg = m_data.config;
                if(navMapCfg == null) {
                    logger.debug("Incomplete data (Config missing)");
                    m_data = null;
                    return;
                }
                if(navMapSmp == null) {
                    logger.debug("Incomplete data (Sample missing)");
                    m_data = null;
                    return;
                }
                if(navMapCfg.hdr.serial != m_lastSerial) {
                    logger.debug("NavMap serialId doesn't match. Reset static data.");
                    m_lastSerial = navMapCfg.hdr.serial;
                    updateNameMaps(navMapCfg);
                    m_navMap.setStaticDataDirty();
                }

                m_mapNode.setTranslation(m_xyz);
                m_mapNode.setRotation(m_rot);
                m_data = null;

                hmd.numXCells = navMapCfg.numCells[0];
                hmd.numYCells = navMapCfg.numCells[1];
                hmd.xCellSize = navMapCfg.cellSize[0];
                hmd.yCellSize = navMapCfg.cellSize[1];

                //-- layers we currently expect
                ShortMapLayer heightLayer    = null;
                OctetMapLayer goodnessLayer  = null;
                OctetMapLayer certaintyLayer = null;
                OctetMapLayer normalLayer    = null;

                Integer index;
                index = m_shortNameMap.get(HEIGHT);
                heightLayer = (ShortMapLayer) ((index == null) ? null : navMapSmp.shortLayers.userData.get(index));

                index = m_octetNameMap.get(GOODNESS);
                goodnessLayer  = (OctetMapLayer) ((index == null) ? null : navMapSmp.octetLayers.userData.get(index));
                index = m_octetNameMap.get(CERTAINTY);
                certaintyLayer = (OctetMapLayer) ((index == null) ? null : navMapSmp.octetLayers.userData.get(index));
                index = m_octetNameMap.get(NORMALS);
                normalLayer    = (OctetMapLayer) ((index == null) ? null : navMapSmp.octetLayers.userData.get(index));

                if(heightLayer == null || goodnessLayer == null || certaintyLayer == null) {
                    logger.debug("ERROR: expected height, goodness and certainty layers");
                    return;
                }

                //-- height and color
                hmd.offset.set(navMapCfg.offset[0], navMapCfg.offset[1], 0);
                if(hmd.zCoords == null || hmd.zCoords.length != heightLayer.data.userData.size()) {
                    hmd.zCoords = new float[heightLayer.data.userData.size()];
                    hmd.zIsFine = new boolean[heightLayer.data.userData.size()];
                }
                if(hmd.colors == null || hmd.colors.length != 4*goodnessLayer.data.userData.size()) {
                    hmd.colors = new float[4*goodnessLayer.data.userData.size()];
                }
                float[] c;
                int ci = 0; 
                boolean lastNotGood = false;
                float lastGoodZ = 0;
                float certainty = 0;
                float currentZ;
                for(int y = 0; y < hmd.numYCells; y++) {
                    lastGoodZ = Float.NaN;
                    for(int x = 0; x < hmd.numXCells; x++) {
                        int i = y*hmd.numXCells + x;
                        certainty = certaintyLayer.data.userData.getByte(i) * certaintyLayer.scale;
                        currentZ = heightLayer.data.userData.getShort(i) * heightLayer.scale;

                        if(certainty < m_minCertainty) {
                            if(lastGoodZ == lastGoodZ) {
                                hmd.zCoords[i] = lastGoodZ;
                                hmd.zIsFine[i] = true;
                            }
                            else {
                                hmd.zCoords[i] = currentZ;
                                hmd.zIsFine[i] = false;
                            }
                            lastNotGood = true;
                        }
                        else {
                            hmd.zCoords[i] = lastGoodZ = currentZ;
                            hmd.zIsFine[i] = true;
                            if(lastNotGood == true) {
                                hmd.zCoords[i-1] = lastGoodZ;
                                hmd.zIsFine[i-1] = true;
                                lastNotGood = false;
                            }
                        }
                        c = m_colorTable.getColor(goodnessLayer.data.userData.getByte(i),
                                                  certaintyLayer.data.userData.getByte(i));
                        for(int j = 0; j < 3; j++) {
                            hmd.colors[ci++] = c[j];
                        }
                        if(certainty < m_minCertainty) {
                            hmd.colors[ci++] = 0;
                        }
                        else {
                            hmd.colors[ci++] = 1;
                        }
                    }
                }

                // FIXME REDUNDANT, done to clean up side edges
                if(true) {
                    for(int x = 0; x < hmd.numXCells; x++) {
                        lastGoodZ   = Float.NaN;
                        lastNotGood = false;
                        for(int y = 0; y < hmd.numYCells; y++) {
                            int i = y*hmd.numXCells + x;
                            certainty = certaintyLayer.data.userData.getByte(i) * certaintyLayer.scale;
                            if(hmd.zIsFine[i]) {
                                lastGoodZ = hmd.zCoords[i];
                                if(lastNotGood == true) {
                                    hmd.zCoords[i-1] = lastGoodZ;
                                    hmd.zIsFine[i-1] = true;
                                }
                                lastNotGood = false;
                            }
                            else {
                                if(lastGoodZ == lastGoodZ) {
                                    hmd.zCoords[i] = lastGoodZ;
                                    hmd.zIsFine[i] = true;
                                }
                                lastNotGood = true;
                            }
                        }
                    }
                }

                //-- normals
                if(normalLayer != null && normalLayer.data.userData.size() > 0) {
                    if(hmd.normals == null || hmd.normals.length != 3*normalLayer.data.userData.size()) {
                        hmd.normals = new float[3*heightLayer.data.userData.size()];
                    }
                }
                else {
                    hmd.normals = null;
                }
                // XXX FIXME XXX assuming full density normals, and two elements (x,y) for now
                float tmp, nx, ny, nz;
                if(hmd.normals != null && normalLayer != null) {
                    float normScale = normalLayer.scale;
                    for(int y = 0; y < hmd.numYCells; y++) {
                        for(int x = 0; x < hmd.numXCells; x++) {
                            int srci = 2*(y*hmd.numXCells + x);
                            int dsti = 3*(y*hmd.numXCells + x);
                            nx  = normalLayer.data.userData.getByte(srci+0)*normScale;
                            ny  = normalLayer.data.userData.getByte(srci+1)*normScale;
                            tmp = 1 - (nx*nx + ny*ny);
                            nz  = zSign * (float)FastMath.sqrt(tmp);
                            hmd.normals[dsti+0] = nx;// * m_flip;
                            hmd.normals[dsti+1] = ny;// * m_flip;
                            hmd.normals[dsti+2] = nz;// * m_flip;
                        }
                    }
                }
                setDirty(false);
            }
            catch (Throwable t) {
                logger.error("error receiving NavMap data", t);
            }
            finally {
                m_navMap.returnHeightMapData();
            }
        }
    }

    @Override
    public void reset() {
        if(m_navMap != null) {
            m_navMap.invalidate();
        }
    }


}
