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
import gov.nasa.arc.verve.robot.rapid.scenegraph.maps.SharedNavMapTextures.Gradient;
import gov.nasa.arc.verve.robot.rapid.scenegraph.maps.shader.INavMapShaderLogic;
import gov.nasa.arc.verve.robot.rapid.scenegraph.maps.shader.NavMapGoodnessShaderLogic;
import gov.nasa.arc.verve.robot.rapid.scenegraph.maps.shader.NavMapRoughnessShaderLogic;
import gov.nasa.arc.verve.robot.rapid.scenegraph.maps.shader.NavMapSlopeShaderLogic;
import gov.nasa.rapid.v2.e4.message.MessageType;

import org.apache.log4j.Logger;

/**
 * 
 * @author mallan
 *
 */
public class RapidRobotPartTiledShaderNavMap extends AbstractRapidRobotPartTiledShaderMap {
    private static final Logger logger = Logger.getLogger(RapidRobotPartTiledShaderNavMap.class);

    // XXX change to string based lookup: enum is short term kludge for ui purposes
    public enum ShaderType {
        HazardGoodness, 
        Goodness, 
        Roughness, 
        Slope,
    }
    protected ShaderType m_shaderType = ShaderType.Goodness;

    /**
     * 
     * @param partId
     * @param parent
     */
    public RapidRobotPartTiledShaderNavMap(String partName, RapidRobot parent, 
                                           String participantId, 
                                           float zOffset, 
                                           MessageType navMapMessageType, 
                                           float zSign) {
        super(partName, parent, participantId, zOffset, navMapMessageType, zSign);
    }

    public ShaderType getShaderType() {
        return m_shaderType;
    }

    public void setShaderType(ShaderType type) {
        //logger.debug("setShaderType: "+type.name());
        m_shaderType = type;
        if(m_tileNode != null) {
            INavMapShaderLogic logic = shaderLogic();
            //m_tileNode.setRenderState(shaderLogic().asShaderState());
            synchronized(m_mapLock) {
                for(NavMapNode node : m_tilePool) {
                    node.setShaderLogic(logic);
                }
                for(NavMapNode node : m_mapTiles.values()) {
                    node.setShaderLogic(logic);
                }
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
            case HazardGoodness: return new NavMapGoodnessShaderLogic(m_zSign, Gradient.Red0YellowToGreenAlpha);
            case Goodness:       return new NavMapGoodnessShaderLogic(m_zSign);
            case Roughness:      return new NavMapRoughnessShaderLogic(m_zSign);
            case Slope:          return new NavMapSlopeShaderLogic(m_zSign);
            }
        }
        catch(Throwable t) {
            logger.warn(t);
        }
        return null;
    }

}
