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
package gov.nasa.arc.verve.ardor3d.scenegraph.shape;

import java.nio.FloatBuffer;

import com.ardor3d.bounding.BoundingSphere;
import com.ardor3d.image.Texture;
import com.ardor3d.math.Vector2;
import com.ardor3d.renderer.IndexMode;
import com.ardor3d.renderer.state.RenderState;
import com.ardor3d.renderer.state.TextureState;
import com.ardor3d.scenegraph.FloatBufferData;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.MeshData;
import com.ardor3d.util.geom.BufferUtils;

/**
 * Simple XY quad for rendering a texture. 
 *
 */
public class TexQuad extends Mesh {
    Vector2     m_size      = new Vector2(1,1);
    boolean     m_isFlipped = false;
    boolean     m_xUp       = false;
    
    boolean     m_isInitialized = false;
    float[]     m_texMul = new float[] { 1,1 };

    public TexQuad(String name ) {
        super(name);
        m_size.set(1,1);
        m_isFlipped = false;
        initialize();
    }

    /**
     * 
     * @param name spatial name
     * @param size 
     * @param isFlipped flip draw order to invert polygon face
     */
    public TexQuad(String name, float size, boolean isFlipped ) {
        super(name);
        m_size.set(size, size);
        m_isFlipped = isFlipped;
        initialize();
    }

    /**
     * 
     * @param name spatial name
     * @param sizeX
     * @param sizeY
     * @param isFlipped flip draw order to invert polygon face
     */
    public TexQuad(String name, float sizeX, float sizeY, boolean isFlipped ) {
        super(name);
        m_size.set(sizeX, sizeY);
        m_isFlipped = isFlipped;
        initialize();
    }

    /**
     * 
     * @param name spatial name
     * @param sizeX
     * @param sizeY
     * @param isFlipped flip draw order to invert polygon face
     * @param xUp use X as up for tex coords (instead of default Y up)
     */
    public TexQuad(String name, float sizeX, float sizeY, boolean isFlipped, boolean xUp) {
        super(name);
        m_size.set(sizeX, sizeY);
        m_isFlipped = isFlipped;
        m_xUp = xUp;
        initialize();
    }

    /**
     * set texture, size and color to be the same as 'in'
     * @param in
     */
    public TexQuad(TexQuad in) {
        m_size.set(in.m_size);
        m_isFlipped = in.m_isFlipped;
        initialize();
    }
    
    public void setSize(float size) {
        setSize(size, size);
    }
    
    public void setSize(float x, float y) {
        m_size.set(x, y);
        FloatBuffer crdBuf = getMeshData().getVertexBuffer();
        FloatBuffer nrmBuf = getMeshData().getNormalBuffer();
        FloatBuffer txcBuf = getMeshData().getTextureBuffer(0);
        setBufferValues(crdBuf, nrmBuf, txcBuf);
    }

    /**
     * @return average of x and y size
     */
    public float getSize() {
        return (m_size.getXf()+m_size.getYf())*0.5f;
    }

    public float getXSize() {
        return m_size.getXf();
    }
    
    public float getYSize() {
        return m_size.getYf();
    }
    
    private short[] getIndexes(boolean flipped) {
        if(flipped) {
            return new short[] { 1, 0, 2, 3 };
        }
        return new short[] { 0, 1, 3, 2 };
    }

    public void setTexture(Texture tex) {
        TextureState ts = (TextureState)getLocalRenderState(RenderState.StateType.Texture);
        if(ts == null) {
            ts = new TextureState();
            setRenderState(ts);
        }
        ts.setTexture(tex);
        if(tex != null) {
            ts.setEnabled(true);
        }
        else {
            ts.setEnabled(false);
        }
    }

    /**
     * initalize geometry
     */
    protected void initialize() {
        try {
            final int totalVerts = 4;
            short[] indexes = getIndexes(m_isFlipped);

            FloatBuffer crdBuf = BufferUtils.createFloatBuffer(totalVerts*3);
            FloatBuffer nrmBuf = BufferUtils.createFloatBuffer(totalVerts*3);
            FloatBuffer txcBuf = BufferUtils.createFloatBuffer(totalVerts*2);
            
            setBufferValues(crdBuf, nrmBuf, txcBuf);
            
            FloatBufferData tc;
            MeshData data = getMeshData();
            data.setIndexMode(IndexMode.TriangleStrip);
            data.setVertexBuffer(crdBuf);
            data.setNormalBuffer(nrmBuf);
            tc = new FloatBufferData(txcBuf,2);
            data.setTextureCoords(tc, 0);
            data.setIndexBuffer(BufferUtils.createShortBuffer(indexes));

            BoundingSphere bound = new BoundingSphere();
            bound.computeFromPoints(getMeshData().getVertexBuffer());
            this.setModelBound(bound);

            m_isInitialized = true;
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    protected void setBufferValues(FloatBuffer crdBuf, FloatBuffer nrmBuf, FloatBuffer txcBuf) {
        crdBuf.rewind();
        float sx = m_size.getXf()/2;
        float sy = m_size.getYf()/2;
        crdBuf.put(-sx).put( sy).put(0);
        crdBuf.put(-sx).put(-sy).put(0);
        crdBuf.put( sx).put(-sy).put(0);
        crdBuf.put( sx).put( sy).put(0);
        
        nrmBuf.rewind();
        float n = (m_isFlipped) ? -1 : 1;
        nrmBuf.put(0).put(0).put(n);
        nrmBuf.put(0).put(0).put(n);
        nrmBuf.put(0).put(0).put(n);
        nrmBuf.put(0).put(0).put(n);

        txcBuf.rewind();
        float a = m_texMul[0];
        float b = m_texMul[1];
        if(m_xUp) {
            txcBuf.put(0).put(a);
            txcBuf.put(b).put(a);
            txcBuf.put(b).put(0);
            txcBuf.put(0).put(0);
        }
        else {
            txcBuf.put(0).put(0);
            txcBuf.put(0).put(a);
            txcBuf.put(b).put(a);
            txcBuf.put(b).put(0);
        }
    }
}
