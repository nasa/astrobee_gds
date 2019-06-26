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
package gov.nasa.arc.verve.robot.rapid.scenegraph.maps;

import gov.nasa.arc.verve.robot.rapid.parts.maps.TileKey;
import gov.nasa.arc.verve.robot.rapid.scenegraph.maps.shader.INavMapShaderLogic;

import com.ardor3d.image.Texture.MagnificationFilter;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.hint.PickingHint;

public class NavMapNode extends Node {
    protected NavMapDataTextures m_mapTextures = null;
    protected INavMapGeometry    m_mapGeometry = null;
    protected INavMapShaderLogic m_shaderLogic = null;
    protected INavMapShaderLogic m_shaderDead  = null;
    protected boolean            m_texturesDirty = true;
    protected boolean            m_alive       = true;

    static final String GEOMETRY = "Geometry";

    protected Object m_lastTelemetry = null;

    public TileKey tileKey = null;

    /**
     * 
     * @param name
     */
    public NavMapNode(String name, NavMapDataTextures mapTextures, INavMapGeometry mapGeometry) {
        super(name);
        m_mapTextures = mapTextures;
        m_mapGeometry = mapGeometry;
        m_mapGeometry.setName(name+GEOMETRY);
        attachChild(m_mapGeometry.asMesh());
        getSceneHints().setPickingHint(PickingHint.Collidable, false);
        getSceneHints().setPickingHint(PickingHint.Pickable, false);
    }

    @Override
    public void setName(String name) {
        super.setName(name);
        m_mapGeometry.setName(name+GEOMETRY);
    }

    public INavMapGeometry getGeometry() {
        return m_mapGeometry;
    }

    public void setShaderLogic(INavMapShaderLogic logic) {
        m_shaderLogic = logic;
        setAlive(m_alive);

        m_texturesDirty = true;
        if(m_lastTelemetry != null) {
            updateTextures(m_lastTelemetry);
        }
    }

    public MagnificationFilter getMagnificationFilter() {
        return m_mapTextures.getMagnificationFilter();
    }

    public void setMagnificationFilter(final MagnificationFilter magFilter) {
        m_mapTextures.setMagnificationFilter(magFilter);
    }

    public void updateTextures(Object telemetry) {
        m_lastTelemetry = telemetry;
        if(m_texturesDirty) {
            m_mapTextures.setLayerTextureNames(m_shaderLogic.getLayerNames());
            m_mapTextures.updateTextures(telemetry);
            m_shaderLogic.setup(m_mapGeometry.asMesh(), m_mapTextures);
            m_texturesDirty = false;
        }
        else {
            m_mapTextures.updateTextures(telemetry);
        }
    }

    public void updateTextureStates() {
        m_shaderLogic.updateTextureState(m_mapGeometry.asMesh(), m_mapTextures);
    }

    public void handleFrameUpdate(long currentTime) {
        m_shaderLogic.update(m_mapGeometry.asMesh(), m_mapTextures);
    }

    public boolean isAlive() {
        return m_alive;
    }
    public void setAlive(boolean state) {
        m_alive = state;
        if(m_alive && m_shaderLogic != null)
            setRenderState(m_shaderLogic.aliveShaderState());
        else 
            setRenderState(m_shaderLogic.deadShaderState());
    }

    public Object getLastTelemetry() {
        return m_lastTelemetry;
    }
}
