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

import rapid.ext.NAVMAP_CERTAINTY;
import rapid.ext.NAVMAP_NORMALS;

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
public class NavMapSlopeShaderLogic  implements INavMapShaderLogic  {
    private static final Logger logger = Logger.getLogger(NavMapSlopeShaderLogic.class);
    protected GLSLShaderObjectsState[] m_shaderStates = new GLSLShaderObjectsState[] { new GLSLShaderObjectsState(),
                                                                                       new GLSLShaderObjectsState() };

    final String CERTAINTY = NAVMAP_CERTAINTY.VALUE;
    final String NORMALS   = NAVMAP_NORMALS.VALUE;

    final String[] layerNames = new String[] { CERTAINTY, NORMALS };


    protected float m_zSign = -1;
    protected float m_maxAngle = (float)(Math.PI * 45.0/180.0);
    //protected float m_maxAngle = (float)(Math.PI * 80.0/180.0);

    protected Gradient m_gradient = SharedNavMapTextures.Gradient.GreyToPink;
    protected boolean  m_texDirty = false;

    protected ColorRGBA m_grayClr = new ColorRGBA(0.4f, 0.4f, 0.5f, 1.0f);
    protected final float ALIVE_MIX = 0.0f;
    protected final float DEAD_MIX  = 0.5f;

    /**
     * 
     */
    public NavMapSlopeShaderLogic(float zSign) {
        final String name = "NavMapSlope";
        final String category = "robot.rapid";
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
            logger.warn("Error constructing shader: "+name, t);
        }
        m_zSign = zSign;
    }

    public String[] getLayerNames() {
        return layerNames;
    }

    public void setup(Mesh mesh, NavMapDataTextures textures) {
        updateTextureState(mesh, textures);
        float min = 0;
        float max = 100f/127f;
        for(GLSLShaderObjectsState shaderState : m_shaderStates) {
            shaderState.setUniform("certaintyMin",   min);
            shaderState.setUniform("certaintyRange", max-min);
            shaderState.setUniform("certaintyThresh", 0.5f);
            shaderState.setUniform("normScale",      127f/100f);
            shaderState.setUniform("zSign",          m_zSign);
        }
        update(mesh, textures);
    }

    @Override
    public void updateTextureState(Mesh mesh, NavMapDataTextures textures) {
        TextureState ts = (TextureState)mesh.getLocalRenderState(StateType.Texture);
        if(ts == null) {
            ts = new TextureState();
            mesh.setRenderState(ts);
        }
        Texture2D cellTex      = SharedNavMapTextures.getCellTexture();
        Texture2D gradientTex  = SharedNavMapTextures.getGradient(m_gradient);
        Texture2D certaintyTex = textures.getLayerData(CERTAINTY).texture;
        Texture2D normalsTex   = textures.getLayerData(NORMALS).texture;
        ts.setTexture(cellTex,      0);
        ts.setTexture(gradientTex,  1);
        ts.setTexture(certaintyTex, 2);
        ts.setTexture(normalsTex,   3);
        for(GLSLShaderObjectsState shaderState : m_shaderStates) {
            shaderState.setUniform("cellTex",      0);
            shaderState.setUniform("gradientTex",  1);
            shaderState.setUniform("certaintyTex", 2);
            shaderState.setUniform("normalsTex",   3);
            shaderState.setUniform("grayClr",      m_grayClr);
        }
        m_shaderStates[0].setUniform("grayMix", ALIVE_MIX);
        m_shaderStates[1].setUniform("grayMix",  DEAD_MIX);
    }

    public void update(Mesh mesh, NavMapDataTextures textures) {
        update();
    }

    float foo = 0;
    public void update() {
        for(GLSLShaderObjectsState shaderState : m_shaderStates) {
            shaderState.setUniform("cosMaxAngleDiff", (float)Math.abs(Math.cos(m_maxAngle)));
            //setUniform("cosMaxAngleDiff", (float)(0.5*(1+Math.cos(foo+=0.0005))) );
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

    public float getMaxSlopeRadians() {
        return m_maxAngle;
    }
    public void setMaxSlopeRadians(float radians) {
        m_maxAngle = radians;
    }

    public float getMaxSlopeDegrees() {
        return 180f*m_maxAngle/(float)Math.PI;
    }
    public void setMaxSlopeDegrees(float degrees) {
        m_maxAngle = (float)Math.PI*degrees/180f;
    }

    public Gradient getGradient() {
        return m_gradient;
    }

    public void setGradient(Gradient gradient) {
        m_gradient = gradient;
        m_texDirty = true;
    }

    @Override
    public boolean needsTextureStateUpdate() {
        boolean retVal = m_texDirty;
        m_texDirty = false;
        return retVal;
    }
}
