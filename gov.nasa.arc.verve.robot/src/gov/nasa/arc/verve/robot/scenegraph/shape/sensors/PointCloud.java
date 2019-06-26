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
package gov.nasa.arc.verve.robot.scenegraph.shape.sensors;

import java.nio.FloatBuffer;

import com.ardor3d.bounding.BoundingBox;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.type.ReadOnlyColorRGBA;
import com.ardor3d.renderer.Renderer;
import com.ardor3d.scenegraph.Point;
import com.ardor3d.scenegraph.hint.LightCombineMode;
import com.ardor3d.util.geom.BufferUtils;

public class PointCloud extends Point {
    protected int               m_size = 0;
    protected final BoundingBox m_bb;
    protected boolean           m_useTexCoords = false;
    protected boolean           m_useVertColor = false;
    protected float[]           m_intensities  = null;

    public PointCloud(String name) {
        super();
        setName(name);

        getSceneHints().setAllPickingHints(false);
        getSceneHints().setLightCombineMode(LightCombineMode.Off);
        setDefaultColor(ColorRGBA.WHITE);

        m_bb = new BoundingBox();
        m_bb.setCenter(0, 0, 0);
        m_bb.setXExtent(1);
        m_bb.setYExtent(1);
        m_bb.setZExtent(1);

        setModelBound(m_bb);
    }

    public BoundingBox getBoundingBox() {
        return (BoundingBox)_modelBound;
    }

    public boolean isUseTexCoords() {
        return m_useTexCoords;
    }
    public void setUseTexCoords(boolean state) {
        if(state) {
            ensureSize(m_size);
        }
        else {
            getMeshData().setTextureBuffer(null, 0);
        }
    }

    public boolean isUseVertexColors() {
        return m_useVertColor;
    }
    public void setUseVertexColors(boolean state) {
        m_useVertColor = state;
        if(state) {
            ensureSize(m_size);
        }
        else {
            m_intensities = null;
            getMeshData().setColorBuffer(null);
        }
    }

    @Override
    public void draw(Renderer r) {
        if(m_size > 0) {
            super.draw(r);
        }
    }

    public void setPointColor(ReadOnlyColorRGBA clr) {
        if(m_useVertColor) {
            FloatBuffer color = getMeshData().getColorBuffer();
            color.rewind();
            final float r = clr.getRed();
            final float g = clr.getGreen();
            final float b = clr.getBlue();
            final float a = clr.getAlpha();
            for(int i = 0; i < m_size; i++) {
                final float t = m_intensities[i];
                color.put(r*t).put(g*t).put(b*t).put(a);
            }
        }
        else {
            setDefaultColor(clr);
        }
    }

    public void ensureSize(int nVerts) {
        m_size = 0; // disable drawing
        if(nVerts > 0) {
            FloatBuffer verts = getMeshData().getVertexBuffer();
            FloatBuffer txcrd = null;
            FloatBuffer color = null;
            int sz1 = nVerts*1;
            int sz2 = nVerts*2;
            int sz3 = nVerts*3;
            int sz4 = nVerts*4;
            int buff_sz1 = sz1*125/100;
            int buff_sz2 = sz2*125/100;
            int buff_sz3 = sz3*125/100;
            int buff_sz4 = sz4*125/100;

            if( true ) {
                if( verts == null || verts.capacity() < sz3 ) {
                    if(verts != null) 
                        verts.clear();
                    verts = BufferUtils.createFloatBuffer(buff_sz3);
                    getMeshData().setVertexBuffer(verts);
                }
            }
            if(m_useTexCoords) {
                txcrd = getMeshData().getTextureBuffer(0);
                if(txcrd == null || txcrd.capacity() < sz2 ) {
                    if(txcrd != null)
                        txcrd.clear();
                    txcrd = BufferUtils.createFloatBuffer(buff_sz2);
                    getMeshData().setTextureBuffer(txcrd, 0);
                }
            }
            
            if(m_useVertColor) {
                m_intensities = new float[buff_sz1];
                color = getMeshData().getColorBuffer();
                if(color == null || color.capacity() < sz4) {
                    color = BufferUtils.createFloatBuffer(buff_sz4);
                    for(int i = 0; i < buff_sz4; i++) {
                        color.put(1);
                    }
                    color.rewind();
                    getMeshData().setColorBuffer(color);
                }
            }
            else {
                getMeshData().setColorBuffer(null);
            }

            if(verts.limit() != sz3) {
                verts.limit(sz3);
                if(txcrd != null) txcrd.limit(sz2);
                if(color != null) color.limit(sz4);
                getMeshData().updateVertexCount();
            }
        }
        m_size = nVerts;
    }
}
