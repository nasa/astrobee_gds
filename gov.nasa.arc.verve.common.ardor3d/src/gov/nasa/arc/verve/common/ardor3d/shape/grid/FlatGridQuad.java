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
package gov.nasa.arc.verve.common.ardor3d.shape.grid;

import java.nio.FloatBuffer;

import com.ardor3d.bounding.BoundingBox;
import com.ardor3d.bounding.BoundingVolume;
import com.ardor3d.image.Texture2D;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.type.ReadOnlyColorRGBA;
import com.ardor3d.renderer.IndexMode;
import com.ardor3d.renderer.state.MaterialState;
import com.ardor3d.renderer.state.RenderState.StateType;
import com.ardor3d.renderer.state.TextureState;
import com.ardor3d.scenegraph.FloatBufferData;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.hint.LightCombineMode;
import com.ardor3d.util.geom.BufferUtils;

/**
 * Utility geometry for a ground plane grid. TexCoord set
 * 0 is 0,0 to w,h and TexCoord set 1 is 0,0 to 1,1
 * i.e. texture 0 should be the grid texture, and the (optional)
 * texture 1 should be the satellite image
 * @author mallan
 *
 */
public class FlatGridQuad extends Mesh {
    protected float[] 		m_size = new float[] { 0,0 };
    protected Texture2D[]	m_tex  = new Texture2D[] { null, null };
    protected MaterialState m_mat  = new MaterialState();

    /**
     * 
     */
    public FlatGridQuad(String name, float size, ColorRGBA color, Texture2D gridTex, Texture2D baseTex) {
        super(name);
        initialize(size, size, color, gridTex, baseTex);
    }

    public void setColor(ReadOnlyColorRGBA color) {
        m_mat.setDiffuse(color);
        m_mat.setSpecular(ColorRGBA.BLACK);
        m_mat.setEmissive(ColorRGBA.BLACK);
        m_mat.setAmbient(ColorRGBA.BLACK);
        if(getSceneHints().getLightCombineMode() == LightCombineMode.Off) {
            setDefaultColor(color);
        }
        else {
            setRenderState(m_mat);
        }
    }

    /**
     * 
     */
    public void resize(float width, float height) {
        m_size[0] = width;
        m_size[1] = height;
        FloatBuffer vertBuf = this.getMeshData().getVertexBuffer();
        FloatBuffer normBuf = this.getMeshData().getNormalBuffer();
        FloatBuffer texBuf0 = this.getMeshData().getTextureCoords(0).getBuffer();
        FloatBuffer texBuf1 = this.getMeshData().getTextureCoords(1).getBuffer();

        vertBuf.clear();
        if(width > 0) {
            vertBuf.put( 0.0f).put(height).put(0);
            vertBuf.put( 0.0f).put(  0.0f).put(0);
            vertBuf.put(width).put(height).put(0);
            vertBuf.put(width).put(  0.0f).put(0);
        }
        else {
            vertBuf.put( 0.0f).put(  0.0f).put(0);
            vertBuf.put( 0.0f).put(height).put(0);
            vertBuf.put(width).put(  0.0f).put(0);
            vertBuf.put(width).put(height).put(0);
        }

        float up = width > 0 ? 1 : -1;
        normBuf.clear();
        normBuf.put(0).put(0).put(up);
        normBuf.put(0).put(0).put(up);
        normBuf.put(0).put(0).put(up);
        normBuf.put(0).put(0).put(up);

        texBuf0.clear();
        texBuf0.put( 0.0f).put(height);
        texBuf0.put( 0.0f).put(  0.0f);
        texBuf0.put(width).put(height);
        texBuf0.put(width).put(  0.0f);

        texBuf1.clear();
        texBuf1.put(0).put(0);
        texBuf1.put(0).put(1);
        texBuf1.put(1).put(0);
        texBuf1.put(1).put(1);

        updateModelBound();
    }

    /**
     * 
     */
    public void setGridTexture(Texture2D gridTex) {
        TextureState ts = (TextureState)this.getLocalRenderState(StateType.Texture);
        ts.setTexture(gridTex, 0);
    }

    /**
     * 
     */
    public void setBaseTexture(Texture2D baseTex) {
        TextureState ts = (TextureState)this.getLocalRenderState(StateType.Texture);
        ts.setTexture(baseTex, 1);
    }

    /**
     * 
     */
    public void initialize(float width, float height, ColorRGBA color, Texture2D gridTex, Texture2D baseTex) {
        //-- texture state ----------------------
        TextureState ts = new TextureState();
        ts.setTexture(null);
        setRenderState(ts);
        getSceneHints().setLightCombineMode(LightCombineMode.Off);

        setGridTexture(gridTex);
        setBaseTexture(baseTex);

        m_size[0] = width;
        m_size[1] = height;

        setColor(color);

        getMeshData().setIndexMode(IndexMode.TriangleStrip);
        int vertexCount = 4;
        FloatBuffer vertBuf = BufferUtils.createVector3Buffer(vertexCount);
        FloatBuffer normBuf = BufferUtils.createVector3Buffer(vertexCount);
        FloatBuffer texBuf0 = BufferUtils.createVector2Buffer(vertexCount);
        FloatBuffer texBuf1 = BufferUtils.createVector2Buffer(vertexCount);

        getMeshData().setVertexBuffer(vertBuf);
        getMeshData().setNormalBuffer(normBuf);
        getMeshData().setTextureCoords(new FloatBufferData(texBuf0,2), 0);
        getMeshData().setTextureCoords(new FloatBufferData(texBuf1,2), 1);

        BoundingVolume bound = new BoundingBox();
        setModelBound(bound);
        resize(width, height);
    }

    public float getWidth() {
        return m_size[0];
    }

    public float getHeight() {
        return m_size[1];
    }


}
