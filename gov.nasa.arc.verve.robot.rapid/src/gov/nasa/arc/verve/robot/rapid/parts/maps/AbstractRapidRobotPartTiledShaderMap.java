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

import gov.nasa.arc.verve.common.ardor3d.text.BMFont;
import gov.nasa.arc.verve.common.ardor3d.text.BMFontManager;
import gov.nasa.arc.verve.common.ardor3d.text.BMText;
import gov.nasa.arc.verve.robot.exception.TelemetryException;
import gov.nasa.arc.verve.robot.rapid.RapidRobot;
import gov.nasa.arc.verve.robot.rapid.parts.RapidRobotPart;
import gov.nasa.arc.verve.robot.rapid.scenegraph.maps.NavMapNode;
import gov.nasa.arc.verve.robot.rapid.scenegraph.maps.RapidNavMapNode;
import gov.nasa.arc.verve.robot.rapid.scenegraph.maps.shader.INavMapShaderLogic;
import gov.nasa.arc.verve.utils.rapid.RapidVerve;
import gov.nasa.ensemble.ui.databinding.widgets.customization.annotations.Complex;
import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.e4.exception.NotSubscribedException;
import gov.nasa.rapid.v2.e4.message.IDdsInstanceListener;
import gov.nasa.rapid.v2.e4.message.MessageType;
import gov.nasa.rapid.v2.e4.message.collector.RapidMessageCollector;
import gov.nasa.rapid.v2.e4.message.holders.NavMapHolder;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import rapid.RotationEncoding;
import rapid.ext.NavMapConfig;
import rapid.ext.NavMapSample;

import com.ardor3d.image.Texture.MagnificationFilter;
import com.ardor3d.math.Matrix3;
import com.ardor3d.math.Vector3;
import com.ardor3d.renderer.queue.RenderBucketType;
import com.ardor3d.renderer.state.BlendState;
import com.ardor3d.renderer.state.CullState;
import com.ardor3d.renderer.state.CullState.Face;
import com.ardor3d.renderer.state.RenderState.StateType;
import com.ardor3d.renderer.state.ZBufferState;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.hint.PickingHint;

/**
 * 
 * @author mallan
 *
 */
public abstract class AbstractRapidRobotPartTiledShaderMap extends RapidRobotPart implements IDdsInstanceListener {
    private static final Logger logger = Logger.getLogger(AbstractRapidRobotPartTiledShaderMap.class);

    protected final ArrayDeque<NavMapHolder>      m_mapSamples = new ArrayDeque<NavMapHolder>();
    protected NavMapConfig                        m_mapConfig = null;

    protected final Vector3       m_xyz           = new Vector3(0,0,0);
    protected final Matrix3       m_rot           = new Matrix3();
    protected float               m_zSign         = -1;
    protected float               m_zOff          = 0;
    protected Node                m_tileNode      = null;
    protected Node                m_labelNode     = null;
    protected boolean             m_isPickable    = false;
    protected boolean             m_isAlphaTest   = true;
    protected boolean             m_useLocalFrame = false;
    private   boolean             m_clampToTerrain = false;
    protected final double[]      m_mapSize       = new double[2];
    protected final double[]      m_mapOff        = new double[2];

    protected boolean             m_showDeadInstances = true;

    protected int                                 m_maxTiles = 81;
    protected LinkedHashMap<TileKey,NavMapNode>   m_mapTiles;
    protected HashMap<TileKey,TileIdCounter>      m_tileLabels = new HashMap<TileKey,TileIdCounter>();
    protected ArrayList<NavMapNode>               m_tilePool   = new ArrayList<NavMapNode>();
    protected boolean                             m_tileLabelsEnabled = false;

    protected Object                              m_mapLock = new Object();

    protected ArrayList<NavMapNode> m_tempTileList = new ArrayList<NavMapNode>();

    public final MessageType      SAMPLE_TYPE;
    public final MessageType      CONFIG_TYPE;

    MagnificationFilter m_magFilter = MagnificationFilter.Bilinear;

    protected final HashMap<String,INavMapShaderLogic> m_shaderLogic = new HashMap<String,INavMapShaderLogic>();

    /**
     * 
     * @param partId
     * @param parent
     */
    public AbstractRapidRobotPartTiledShaderMap(String partName, RapidRobot parent, 
                                                String participantId, 
                                                float zOffset, 
                                                MessageType navMapMessageType, 
                                                float zSign) {
        super(partName, parent, participantId);
        m_zOff   = zOffset;
        SAMPLE_TYPE = navMapMessageType;
        CONFIG_TYPE = MessageType.valueOf(navMapMessageType.getConfigName());
        setMaxTiles(m_maxTiles);
        m_zSign = (zSign < 0) ? -1 : 1;
    }

    @Override
    public MessageType[] rapidMessageTypes() {
        return new MessageType[] { CONFIG_TYPE, SAMPLE_TYPE };
    }

    /**
     * 
     */
    @Override
    public void attachToNodesIn(Node model) throws IllegalStateException {
        shaderLogic(); // create shader logic

        m_labelNode = new Node("LabelNode");
        ZBufferState zs = new ZBufferState();
        zs.setFunction(ZBufferState.TestFunction.Always);
        m_labelNode.setRenderState(zs);

        m_tileNode = new Node("TileNode");

        CullState cs = new CullState();
        cs.setCullFace(Face.None);
        m_tileNode.setRenderState(cs);

        BlendState bs = new BlendState();
        bs.setTestEnabled(true);
        bs.setReference(0.3f);
        bs.setBlendEnabled(true);
        m_tileNode.setRenderState(bs);

        m_node = new Node(getPartName());
        m_node.attachChild(m_tileNode);
        if(m_tileLabelsEnabled) {
            m_node.attachChild(m_labelNode);
        }

        getRobot().getRobotNode().getConceptsNode().attachChild(m_node);
    }


    @Complex
    public INavMapShaderLogic getShaderLogic() {
        return shaderLogic();
    }

    /**
     * THIS IS A NOOP. It is only here to allow the UI to create an 
     * editor for the INavMapShaderLogic instance. To change the 
     * shader logic object, use setShaderType()
     */
    public void setShaderLogic(INavMapShaderLogic logic) {
        // NOOP
    }

    public void setCellInterpolation(MagnificationFilter magFilter) {
        m_magFilter = magFilter;
        for(NavMapNode node : m_tilePool) {
            node.setMagnificationFilter(m_magFilter);
        }
        synchronized(m_mapLock) {
            for(NavMapNode node : m_mapTiles.values()) {
                node.setMagnificationFilter(m_magFilter);
            }
        }
    }

    public MagnificationFilter getCellInterpolation() {
        return m_magFilter;
    }

    public boolean isUseLocalFrame() {
        return m_useLocalFrame;
    }

    public void setUseLocalFrame(boolean state) {
        m_useLocalFrame = state;
    }

    /** @return whether tile debug labels are shown or not */
    public boolean isShowTileDebugLabels() {
        return m_tileLabelsEnabled;
    }

    /** show the debug tile labels */
    public void setShowTileDebugLabels(boolean status) {
        m_tileLabelsEnabled = status; 
        if(status) {
            for(TileIdCounter c : m_tileLabels.values()) {
                c.updateText(true);
            }
            m_node.attachChild(m_labelNode);
            m_labelNode.updateWorldBound(true);
        }
        else {
            m_node.detachChild(m_labelNode);
        }
    }

    public boolean isClampToTerrain() {
        return m_clampToTerrain;
    }
    public void setClampToTerrain(boolean status) {
        m_clampToTerrain = status;
    }

    /** @return true if tile meshes are pickable */
    public boolean isPickable() {
        return m_isPickable;
    }

    /** make tile meshes pickable */
    public void setPickable(boolean state) {
        m_isPickable = state;
        NavMapNode[] maps = m_mapTiles.values().toArray(new NavMapNode[m_mapTiles.values().size()]);
        for(NavMapNode map : maps) {
            map.getSceneHints().setPickingHint(PickingHint.Pickable, m_isPickable);
        }
    }

    public float getZOffset() {
        return m_zOff;
    }

    public void setZOffset(float zOffset) {
        m_zOff = zOffset;
    }

    /**
     * @return maximum number of tiles to keep
     */
    public int getMaxTiles() {
        return m_maxTiles;
    }

    /**
     * 
     * @param maxTiles
     */
    public void setMaxTiles(final int maxTiles) {
        final float load     = 0.85f;
        final int   capacity = (int)(1.1*(maxTiles/load));
        // create new LRU cache of tiles
        LinkedHashMap<TileKey,NavMapNode> newMapHash = 
                new LinkedHashMap<TileKey,NavMapNode>(capacity, load, true) {
            @Override 
            protected boolean removeEldestEntry(Map.Entry<TileKey,NavMapNode> eldest) {
                if(size() > maxTiles) {
                    synchronized(m_mapLock) {
                        TileKey key = eldest.getKey();
                        NavMapNode value = eldest.getValue();
                        //logger.debug("removing tile "+key.toString()+", current cache size is "+this.size()+", maxTiles is "+maxTiles);
                        m_tileNode.detachChild(value);
                        m_tileLabels.get(key).tileRemoved();
                        m_tilePool.add(value);
                    }
                    return true;
                }
                return false;
            }            
        };
        synchronized(m_mapLock) {
            // populate cache with old maps
            if(m_mapTiles != null) { 
                // XXX this messes up the access ordering of the tiles in the new cache
                // XXX iterate through to maintain order
                TileKey[] tileKeys = m_mapTiles.keySet().toArray(new TileKey[m_mapTiles.size()]);
                for(int i = 0; i < tileKeys.length && i < maxTiles; i++) {
                    newMapHash.put(tileKeys[i], m_mapTiles.get(tileKeys[i]));
                }
                m_mapTiles.clear();
            }
            // set new map and reattach existing children
            m_mapTiles = newMapHash;
            m_maxTiles = maxTiles;
            if(m_tileNode != null) {
                m_tileNode.detachAllChildren();
                for(NavMapNode value : m_mapTiles.values()) {
                    m_tileNode.attachChild(value);
                }
            }
        }
    }

    //-- telemetry ----------------------------------------------------
    @Override
    public void connectTelemetry() throws TelemetryException {
        if(isTelemetryEnabled()) {
            super.connectTelemetry();
            final Agent agent = getRapidRobot().getAgent();
            RapidMessageCollector.instance().addDdsInstanceListener(m_participantId, agent.name(), SAMPLE_TYPE, this);
        }
    }

    @Override
    public void disconnectTelemetry() throws TelemetryException {
        final Agent agent = getRapidRobot().getAgent();
        RapidMessageCollector.instance().removeDdsInstanceListener(m_participantId, agent.name(), SAMPLE_TYPE, this);
        super.disconnectTelemetry();
    }

    /**
     * 
     */
    @Override
    public void onRapidMessageReceived(Agent agent, MessageType type, Object eventObj, Object configObj) {
        //logger.debug(type.name()+" - "+SAMPLE_TYPE.name());
        if(configObj == null) {
            try {
                configObj = RapidMessageCollector.instance().getLastMessage(getParticipantId(), agent, CONFIG_TYPE);
                if(configObj != null) {
                    logger.debug("Got null configObj, but getLastMessage succeeded");
                }
            }
            catch(NotSubscribedException e) {
                logger.debug("Got null configObj, not subscribed to config! "+e.getMessage()+":\n"+configObj);
            }
        }
        if(type == SAMPLE_TYPE) {
            try {
                if(configObj != null) {
                    NavMapHolder holder = new NavMapHolder((NavMapSample)eventObj, (NavMapConfig)configObj);
                    synchronized(m_mapLock) {
                        // don't allow too many tiles to back up if the visualization is turned off
                        while(m_mapSamples.size() >= m_maxTiles) {
                            m_mapSamples.pollFirst();
                        }
                        m_mapSamples.addLast(holder);
                        //int tidx = navMapData.navMapSample.tileId[0];
                        //int tidy = navMapData.navMapSample.tileId[1];
                        //logger.debug("received tile id "+tidx+"_"+tidy);
                    }
                }
            }
            catch(Throwable t) {
                t.printStackTrace();
            }
            setDirty(true);
        }
        else if(type == CONFIG_TYPE) {
            NavMapConfig mapConfig = (NavMapConfig)eventObj;
            if(m_mapConfig == null || (mapConfig.hdr.serial != m_mapConfig.hdr.serial) ) {
                resetAsync();
            }
            m_mapConfig  = mapConfig;
            m_mapSize[0] = m_mapConfig.cellSize[0]*m_mapConfig.numCells[0];
            m_mapSize[1] = m_mapConfig.cellSize[1]*m_mapConfig.numCells[1];
            m_mapOff[0]  = m_mapConfig.offset[0];
            m_mapOff[1]  = m_mapConfig.offset[1];
        }
    }

    /**
     * get a copy of the tile cache. 
     * @param retVal
     * @return
     */
    public Map<TileKey,NavMapNode> getMapTiles(Map<TileKey,NavMapNode> retVal) {
        synchronized(m_mapLock) {
            retVal.clear();
            retVal.putAll(m_mapTiles);
        }
        return retVal;
    }

    protected NavMapNode getNavMapNode(TileKey key) {
        NavMapNode retVal;
        synchronized(m_mapLock) {
            retVal = m_mapTiles.get(key);
            if(retVal == null) {
                if(m_tilePool.size() > 0) {
                    retVal = m_tilePool.remove(m_tilePool.size()-1);
                    retVal.setName(key.toString());
                    retVal.getGeometry().setStaticDataDirty();
                    //logger.debug("*** recycle tile "+key.toString()+"  clamp="+m_clampToTerrain);
                }
                else {
                    retVal = new RapidNavMapNode(key.toString());
                    retVal.getGeometry().setInvertWinding(m_zSign > 0);
                    retVal.getGeometry().getSceneHints().setRenderBucketType(RenderBucketType.Transparent);
                    //logger.debug("*** create tile "+key.toString()+"  clamp="+m_clampToTerrain);
                }
                retVal.getGeometry().setClampToTerrain(m_clampToTerrain); // FIXME
                retVal.setShaderLogic(shaderLogic());
                retVal.setAlive(true);
                retVal.setMagnificationFilter(m_magFilter);
                retVal.getSceneHints().setPickingHint(PickingHint.Pickable, m_isPickable);
                m_mapTiles.put(key, retVal);
                m_tileNode.attachChild(retVal);
                m_tileLabels.get(key).tileAdded();
            }
        }
        retVal.tileKey = key;
        return retVal;
    }

    protected Vector3 getTranslation(NavMapSample sample, Vector3 retVal) {
        if(m_useLocalFrame) {
            int xn = sample.tileId[0];
            int yn = sample.tileId[1];
            retVal.setX(xn*m_mapSize[0]-m_mapOff[0]);
            retVal.setY(yn*m_mapSize[1]-m_mapOff[1]);
            retVal.setZ(m_zOff);
        }
        else {
            final double[] xyz = sample.location.xyz.userData;
            retVal.set(xyz[0],
                       xyz[1],
                       xyz[2]+m_zOff);
        }
        return retVal;
    }

    protected Matrix3 getRotation(NavMapSample sample, Matrix3 retVal) {
        if(m_useLocalFrame) {
            retVal.setIdentity();
        }
        else {
            RapidVerve.toArdor(sample.location.rot.userData, RotationEncoding.RAPID_ROT_M33, retVal);
        }
        return retVal;
    }

    /**
     * update visualization
     */
    @Override
    public void handleFrameUpdate(long currentTime) {
        if(shaderLogic().needsTextureStateUpdate()) {
            updateTextureStates();
        }

        synchronized(m_mapLock) {
            if(isDirty() && m_mapConfig != null) {
                while(m_mapSamples.size() > 0) {
                    final NavMapHolder holder = m_mapSamples.removeFirst();
                    final NavMapSample sample = holder.sample;
                    final NavMapConfig config = m_mapConfig;
                    if(sample.hdr.serial == config.hdr.serial) {
                        TileKey key = new TileKey(sample.tileId[0],sample.tileId[1]);

                        //-- tile debug labels
                        TileIdCounter counter = m_tileLabels.get(key);
                        if(counter == null) { // add before calling getNavMapHeightField
                            counter = new TileIdCounter(key, m_labelNode);
                            m_tileLabels.put(key, counter);
                        }
                        counter.updatedTile();

                        getTranslation(sample, m_xyz);
                        getRotation(sample, m_rot);

                        try {
                            NavMapNode navMapNode = getNavMapNode(key);
                            counter.text.setTranslation(m_xyz);
                            counter.text.setRotation(m_rot);
                            navMapNode.setTranslation(m_xyz);
                            navMapNode.setRotation(m_rot);

                            navMapNode.getGeometry().setClampToTerrain(m_clampToTerrain); // FIXME
                            navMapNode.getGeometry().updateFromTelemetry(holder, 
                                                                         getRapidRobot().siteToWorldTransform(),
                                                                         navMapNode.getTransform(), 
                                                                         m_zOff,
                                                                         m_zSign);
                            navMapNode.updateTextures(holder);
                            //MapDebug.writeMapAsImages(holder, 0);
                            setDirty(false);
                        }
                        catch (Throwable t) {
                            logger.error("error receiving NavMap data", t);
                        }
                    }
                }
            }
            m_tempTileList.clear();
            m_tempTileList.addAll(m_mapTiles.values());
        }
        for(NavMapNode map : m_tempTileList) {
            map.handleFrameUpdate(currentTime);
        }
    }

    protected void updateTextureStates() {
        synchronized(m_mapLock) {
            if(m_tileNode != null) {
                for(NavMapNode node : m_tilePool) {
                    node.updateTextureStates();
                }
                for(NavMapNode node : m_mapTiles.values()) {
                    node.updateTextureStates();
                }
            }
        }
    }

    /**
     * 
     */
    @Override
    public void reset() {
        synchronized(m_mapLock) {
            TileKey[] keys = m_mapTiles.keySet().toArray(new TileKey[m_mapTiles.keySet().size()]);
            for(TileKey key : keys) {
                NavMapNode value = m_mapTiles.get(key);
                m_tileNode.detachChild(value);
                m_tileLabels.get(key).tileRemoved();
                m_tilePool.add(value);
                m_mapTiles.remove(key);
            }
            m_labelNode.detachAllChildren();
            m_tileLabels.clear();
            m_mapTiles.clear();
        }
    }

    /**
     * 
     */
    //==========================================================
    protected class TileIdCounter {
        public final BMText  text;
        private final TileKey key;
        private int count = 0;
        private int added = 0;
        private int remed = 0;

        public TileIdCounter(TileKey tileKey, Node parent) {
            BMFont font = BMFontManager.sansExtraSmall();
            key = tileKey;
            text = new BMText("Key", key.toString(),
                              font,
                              BMText.Align.South,
                              BMText.Justify.Left);
            updateText(AbstractRapidRobotPartTiledShaderMap.this.m_tileLabelsEnabled);
            parent.attachChild(text);
            text.clearRenderState(StateType.ZBuffer);
        }
        public void updatedTile() {
            count++; updateText(AbstractRapidRobotPartTiledShaderMap.this.m_tileLabelsEnabled);
        }
        public void tileRemoved() {
            remed++; updateText(AbstractRapidRobotPartTiledShaderMap.this.m_tileLabelsEnabled);
        }
        public void tileAdded() {
            added++; updateText(AbstractRapidRobotPartTiledShaderMap.this.m_tileLabelsEnabled);
        }
        public void updateText(boolean doit) {
            text.setText(String.format("%s up(%d)\nadd/rem(%d/%d)",
                                       key.toString(), count, 
                                       added, remed));
            text.updateModelBound();
        }
        public void reset() {
            count = 0;
            added = 0;
            remed = 0;
        }
    }

    /** get current shader logic; create if necessary */
    abstract protected INavMapShaderLogic shaderLogic();
    protected INavMapShaderLogic shaderLogic(String shaderTypeName) {
        INavMapShaderLogic retVal = m_shaderLogic.get(shaderTypeName);
        if(retVal == null) {
            retVal = newShaderLogic(shaderTypeName);
            m_shaderLogic.put(shaderTypeName, retVal);
        }
        return retVal;
    }

    /** shader logic factory */
    abstract protected INavMapShaderLogic newShaderLogic(String shaderTypeName);

    //-- instance liveliness monitoring ------------------------------------
    public boolean isShowDeadInstances() {
        return m_showDeadInstances;
    }
    public void setShowDeadInstances(boolean show) {
        m_showDeadInstances = show;
    }

    //int num = 0;
    @Override
    public void onDdsInstanceAlive(String partition, MessageType msgType, Object sample) {
        if(msgType == SAMPLE_TYPE) {
            NavMapSample map = (NavMapSample)sample;
            TileKey key = new TileKey(map.tileId[0], map.tileId[1]);
            synchronized(m_mapLock) {
                NavMapNode node = m_mapTiles.get(key);
                if(node != null) {
                    node.setAlive(true);
                }
            }
            //logger.debug(String.format("tile instance alive % 3d % 3d", map.tileId[0], map.tileId[1]));
        }
    }

    @Override
    public void onDdsInstanceDead(String partition, MessageType msgType, Object sample) {
        if(msgType == SAMPLE_TYPE) {
            NavMapSample map = (NavMapSample)sample;
            TileKey key = new TileKey(map.tileId[0], map.tileId[1]);
            synchronized(m_mapLock) {
                NavMapNode node = m_mapTiles.get(key);
                if(m_showDeadInstances && node != null) {
                    node.setAlive(false);
                }
            }
            //logger.debug(String.format("tile instance dead % 3d % 3d", map.tileId[0], map.tileId[1]));
        }
    }

}
