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

import gov.nasa.arc.verve.robot.rapid.RapidRobot;
import gov.nasa.arc.verve.robot.rapid.scenegraph.maps.NavMapNode;
import gov.nasa.arc.verve.robot.rapid.scenegraph.maps.shader.CSpaceEvalShaderLogic;
import gov.nasa.arc.verve.robot.rapid.scenegraph.maps.shader.CSpaceOptimisticShaderLogic;
import gov.nasa.arc.verve.robot.rapid.scenegraph.maps.shader.CSpacePessimisticShaderLogic;
import gov.nasa.arc.verve.robot.rapid.scenegraph.maps.shader.INavMapShaderLogic;
import gov.nasa.rapid.v2.e4.message.MessageType;
import gov.nasa.rapid.v2.e4.message.holders.NavMapHolder;

import java.util.HashMap;
import java.util.LinkedHashMap;

import org.apache.log4j.Logger;

import rapid.ext.NavMapConfig;
import rapid.ext.NavMapSample;

/**
 * 
 * @author mallan
 *
 */
public class RapidRobotPartTiledShaderCSpaceMap extends AbstractRapidRobotPartTiledShaderMap {
    private static final Logger logger = Logger.getLogger(RapidRobotPartTiledShaderNavMap.class);

    // XXX change to string based lookup: enum is short term hack for ui purposes
    public enum ShaderType {
        Optimistic,
        Pessimistic,
        CSpaceEval, 
    }
    protected ShaderType m_shaderType = ShaderType.Pessimistic;

    protected boolean                         m_useNavMapHeightField = true;
    protected RapidRobotPartTiledShaderNavMap m_thatMapPart = null;
    protected HashMap<TileKey,NavMapNode>     m_thatMapTiles = new HashMap<TileKey,NavMapNode>();
    protected LinkedHashMap<TileKey,Object>   m_thatTelemetryCache = 
            new LinkedHashMap<TileKey,Object>(128, 0.75f, true); // FIXME: fixed size cache must be made dynamic

    /**
     * 
     * @param partId
     * @param parent
     */
    public RapidRobotPartTiledShaderCSpaceMap(String partName, RapidRobot parent, 
                                              String participantId, 
                                              float zOffset, 
                                              MessageType navMapMessageType, 
                                              float zSign) {
        super(partName, parent, participantId, zOffset, navMapMessageType, zSign);
    }

    /**
     * 
     * @param partId
     * @param parent
     */
    public RapidRobotPartTiledShaderCSpaceMap(String partName, RapidRobot parent, 
                                              String participantId, 
                                              float zOffset, 
                                              MessageType navMapMessageType) {
        this(partName, parent, participantId, zOffset, navMapMessageType, -1);
    }

    public ShaderType getShaderType() {
        return m_shaderType;
    }

    public void setShaderType(ShaderType type) {
        //logger.debug("setShaderType: "+type.name());
        m_shaderType = type;
        if(m_tileNode != null) {
            INavMapShaderLogic logic = shaderLogic();
            for(NavMapNode node : m_tilePool) {
                node.setShaderLogic(logic);
            }
            for(NavMapNode node : m_mapTiles.values()) {
                node.setShaderLogic(logic);
            }
        }
    }


    /** get current shader logic; create if necessary */
    @Override
    protected INavMapShaderLogic shaderLogic() {
        return shaderLogic(m_shaderType.name());
    }
    /** shader logic factory */
    @Override
    protected INavMapShaderLogic newShaderLogic(String shaderTypeName) {
        try {
            ShaderType type = ShaderType.valueOf(shaderTypeName);
            switch(type) {
            case CSpaceEval:  return new CSpaceEvalShaderLogic(m_zSign);
            case Optimistic:  return new CSpaceOptimisticShaderLogic();
            case Pessimistic: return new CSpacePessimisticShaderLogic();
            }
        }
        catch(Throwable t) {
            logger.warn(t);
        }
        return null;
    }

    public void setUseLocalMapHeightField(boolean state) {
        m_useNavMapHeightField = state;
    }

    public boolean isUseLocalMapHeightField() {
        return m_useNavMapHeightField;
    }

    @Override
    public void handleFrameUpdate(long currentTime) {
        if(shaderLogic().needsTextureStateUpdate()) {
            updateTextureStates();
        }
        if(m_useNavMapHeightField) {
            if(m_thatMapPart == null) {
                m_thatMapPart = (RapidRobotPartTiledShaderNavMap)getRapidRobot().getPart(RapidRobot.TILED_SHADER_LOCALMAP);
            }
            if(m_thatMapPart != null) {
                m_thatMapPart.getMapTiles(m_thatMapTiles);
            }
        }

        if(isDirty() && m_mapConfig != null) {
            NavMapHolder holder = null;
            synchronized(m_mapSamples) {
                while(m_mapSamples.size() > 0) {
                    holder = m_mapSamples.removeFirst();
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
                        
                        NavMapNode   thatNode = null;
                        NavMapHolder thatHolder = null;
                        if(m_useNavMapHeightField) {
                            thatNode = m_thatMapTiles.get(key);
                            if(thatNode != null) {
                                thatHolder = (NavMapHolder)thatNode.getLastTelemetry();
                                getTranslation(thatHolder.sample, m_xyz);
                                getRotation(thatHolder.sample, m_rot);
                            }
                        }

                        try {
                            NavMapNode thisNode = getNavMapNode(key);

                            counter.text.setTranslation(m_xyz);
                            counter.text.setRotation(m_rot);
                            thisNode.setTranslation(m_xyz);
                            thisNode.setRotation(m_rot);

                            if(thatNode != null) {
                                m_thatTelemetryCache.put(key, thatHolder);
                                thisNode.getGeometry().updateFromTelemetry(thatHolder, 
                                                                           getRapidRobot().siteToWorldTransform(),
                                                                           thatNode.getTransform(), 
                                                                           m_zOff, m_zSign);
                            }
                            else {
                                thisNode.getGeometry().updateFromTelemetry(holder, 
                                                                           getRapidRobot().siteToWorldTransform(),
                                                                           thisNode.getTransform(), 
                                                                           m_zOff, m_zSign);
                            }
                            thisNode.updateTextures(holder);
                            setDirty(false);
                        }
                        catch (Throwable t) {
                            logger.error("error receiving NavMap data", t);
                        }
                    }
                }
            }
        }

        m_tempTileList.clear();
        synchronized(m_mapSamples) {
            m_tempTileList.addAll(m_mapTiles.values());
        }
        for(NavMapNode thisNode : m_tempTileList) {
            if(m_useNavMapHeightField) { 
                // check if other nodes have been updated
                // try not to mess up access ordering
                if(m_thatMapPart != null) {
                    final TileKey key = thisNode.tileKey;
                    NavMapNode thatNode = m_thatMapTiles.get(key);
                    if(thatNode != null) {
                        Object telemetry = thatNode.getLastTelemetry();
                        if(!telemetry.equals(m_thatTelemetryCache.get(key))) {
                            m_thatTelemetryCache.put(key, telemetry);
                            thisNode.getGeometry().updateFromTelemetry(telemetry, 
                                                                       getRapidRobot().siteToWorldTransform(),
                                                                       thatNode.getTransform(), 
                                                                       m_zOff,
                                                                       m_zSign);
                        }
                    }
                }
            }
            thisNode.handleFrameUpdate(currentTime);
        }
    }


}
