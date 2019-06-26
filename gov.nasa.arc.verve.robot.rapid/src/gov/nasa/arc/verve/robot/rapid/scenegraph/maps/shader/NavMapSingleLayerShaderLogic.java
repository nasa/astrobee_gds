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
package gov.nasa.arc.verve.robot.rapid.scenegraph.maps.shader;

import gov.nasa.arc.verve.common.DataBundleHelper;
import gov.nasa.arc.verve.robot.rapid.scenegraph.maps.NavMapDataTextures;
import gov.nasa.arc.verve.robot.rapid.scenegraph.maps.SharedNavMapTextures;
import gov.nasa.arc.verve.robot.rapid.scenegraph.maps.SharedNavMapTextures.Gradient;

import java.net.URL;

import org.apache.log4j.Logger;

import com.ardor3d.image.Texture2D;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.renderer.state.GLSLShaderObjectsState;
import com.ardor3d.renderer.state.RenderState.StateType;
import com.ardor3d.renderer.state.TextureState;
import com.ardor3d.scenegraph.Mesh;

/**
 * 
 * @author mallan
 *
 */
public class NavMapSingleLayerShaderLogic implements INavMapShaderLogic  {
    private static final Logger logger = Logger.getLogger(NavMapSingleLayerShaderLogic.class);
    protected GLSLShaderObjectsState[] m_shaderStates = new GLSLShaderObjectsState[] { new GLSLShaderObjectsState(),
                                                                                       new GLSLShaderObjectsState() };

    final String[]     m_layerNames;

    protected Gradient m_gradient = SharedNavMapTextures.Gradient.PurpleToCyan;

    protected float    m_layerMin = 0f;
    protected float    m_layerMax = 100f;
    protected float    m_layerAlpha = 1;

    protected ColorRGBA m_grayClr = new ColorRGBA(0.4f, 0.5f, 0.4f, 1f);
    protected final float ALIVE_MIX = 0.0f;
    protected final float DEAD_MIX  = 0.5f;

    /**
     * 
     */
    public NavMapSingleLayerShaderLogic(String layerName, Gradient gradient) {
        m_layerNames = new String[] { layerName };
        m_gradient = gradient;
        final String name = "NavMapSingleLayer";
        final String category = "robot.rapid";
        // simple shader, no need for fallback
        try {
            URL vertUrl = DataBundleHelper.getURL(category, "shaders/"+name+".vert");
            URL fragUrl = DataBundleHelper.getURL(category, "shaders/"+name+".frag");            
            for(GLSLShaderObjectsState shaderState : m_shaderStates) {
                shaderState.setVertexShader(vertUrl.openStream());
                shaderState.setFragmentShader(fragUrl.openStream());
                shaderState._needSendShader = true;
            }
        }
        catch(Throwable t) {
            logger.warn("Error setting up shader: "+name, t);
        }
    }

    public String[] getLayerNames() {
        return m_layerNames;
    }

    public void setup(Mesh mesh, NavMapDataTextures textures) {
        updateTextureState(mesh, textures);
        update(mesh, textures);
    }

    public void updateTextureState(Mesh mesh, NavMapDataTextures textures) {
        TextureState ts = (TextureState)mesh.getLocalRenderState(StateType.Texture);
        if(ts == null) {
            ts = new TextureState();
            mesh.setRenderState(ts);
        }
        Texture2D cellTex        = SharedNavMapTextures.getCellTexture();
        Texture2D gradientTex    = SharedNavMapTextures.getGradient(m_gradient);
        Texture2D layerTex       = textures.getLayerData(m_layerNames[0]).texture;
        ts.setTexture(cellTex,        0);
        ts.setTexture(gradientTex,    1);
        ts.setTexture(layerTex,       2);
        for(GLSLShaderObjectsState shaderState : m_shaderStates) {
            shaderState.setUniform("cellTex",     0);
            shaderState.setUniform("gradientTex", 1);
            shaderState.setUniform("layerTex",    2);
            shaderState.setUniform("grayClr",      m_grayClr);
        }
        m_shaderStates[0].setUniform("grayMix", ALIVE_MIX);
        m_shaderStates[1].setUniform("grayMix",  DEAD_MIX);
    }

    public void update(Mesh mesh, NavMapDataTextures textures) {
        update();
    }

    public void update() {
        float gMin = m_layerMin/127f;
        float gMax = m_layerMax/127f;
        for(GLSLShaderObjectsState shaderState : m_shaderStates) {
            shaderState.setUniform("layerMin",    gMin);
            shaderState.setUniform("layerRange",  gMax-gMin);
            shaderState.setUniform("layerAlpha",  m_layerAlpha);
        }
    }

    @Override
    public GLSLShaderObjectsState aliveShaderState() {
        return m_shaderStates[0];
    }

    @Override
    public GLSLShaderObjectsState deadShaderState() {
        return m_shaderStates[1];
    }

    public float getLayerAlpha() {
        return m_layerAlpha;
    }

    public void setLayerAlpha(float mix) {
        m_layerAlpha = mix;
    }

    public Gradient getGradient() {
        return m_gradient;
    }

    public void setGradient(Gradient gradient) {
        m_gradient = gradient;
        m_texDirty = true;
    }

    boolean m_texDirty = false;
    @Override
    public boolean needsTextureStateUpdate() {
        boolean retVal = m_texDirty;
        m_texDirty = false;
        return retVal;
    }
}
