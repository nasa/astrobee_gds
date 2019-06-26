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
import com.ardor3d.renderer.IndexMode;
import com.ardor3d.renderer.Renderer;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.util.geom.BufferUtils;

public class PieWedge extends Mesh {
    protected float   m_innerRadius;
    protected float   m_outerRadius;
    protected float   m_startAngle;
    protected float   m_endAngle;
    protected int     m_uSteps;
    protected int     m_vSteps;
    protected int     m_numVerts = 0;
    protected boolean m_flip     = false;

    public PieWedge(String name) {
        this(name, (float)-Math.PI/2, (float)Math.PI/2, 0.5f, 2.0f, 16, 1);
    }

    public PieWedge(String name, float startAngle, float endAngle, float innerRadius, float outerRadius, int uSteps, int vSteps) {
        super(name);
        m_startAngle  = startAngle;
        m_endAngle    = endAngle;
        m_innerRadius = innerRadius;
        m_outerRadius = outerRadius;
        m_uSteps      = uSteps;
        m_vSteps      = vSteps;
        getMeshData().setIndexMode(IndexMode.TriangleStrip);
        setModelBound(new BoundingSphere());
        updateVertices();
    }
    
    public void setFlipped(boolean flip) {
        m_flip = flip;
        updateVertices();
    }
    
    public void setRadius(float inner, float outer) {
        m_innerRadius = inner;
        m_outerRadius = outer;
        updateVertices();
    }
    
    public void setSweep(float startAngle, float endAngle) {
        m_startAngle = startAngle;
        m_endAngle = endAngle;
        updateVertices();
    }
    
    public void setSteps(int uSteps, int vSteps) {
        m_uSteps = uSteps;
        m_vSteps = vSteps;
        updateVertices();
    }

    @Override
    public void draw(Renderer r) {
        if(m_numVerts > 0) {
            super.draw(r);
        }
    }

    public void updateVertices() {
        int numVerts = 2*m_vSteps + m_vSteps * (2 + m_uSteps*2);
        ensureSize(numVerts);
        float width  = m_endAngle - m_startAngle;
        float length = m_outerRadius - m_innerRadius;
        float uStep  = width/m_uSteps;
        float vStep  = 1f/m_vSteps;
        float rStep  = vStep*length;
        FloatBuffer verts = getMeshData().getVertexBuffer();
        FloatBuffer txcrd = getMeshData().getTextureBuffer(0);
        verts.rewind();
        txcrd.rewind();

        float angle = 0;
        float x, y;
        float ix = 0;
        float iy = 0;
        float iz = 0;
        float ox = 0;
        float oy = 0;
        float oz = 0;
        float uu = 0;
        float v0 = 0;
        float v1 = 1;
        float r0 = m_innerRadius;
        float r1 = r0 + rStep;
        for(int v = 0; v < m_vSteps; v++) {
            for(int u = 0; u <= m_uSteps; u++) {
                angle = m_startAngle + u*uStep;
                x = (float)Math.cos(angle);
                y = (float)Math.sin(angle);
                ix = x*r0;
                iy = y*r0;
                iz = getZ(ix, iy);
                ox = x*r1;
                oy = y*r1;
                oz = getZ(ox, oy);
                uu = 1f-(float)u/(float)m_uSteps;
                v0 = 1f-(v+0)*vStep;
                v1 = 1f-(v+1)*vStep;
                if(u == 0) {
                    if(m_flip) {
                        verts.put(ix).put(iy).put(iz);
                        txcrd.put(uu).put(v0);
                    }
                    else {
                        verts.put(ox).put(oy).put(oz);
                        txcrd.put(uu).put(v1);
                    }
                }
                if(!m_flip) {
                    verts.put(ox).put(oy).put(oz);
                    verts.put(ix).put(iy).put(iz);
                    txcrd.put(uu).put(v1);
                    txcrd.put(uu).put(v0);
                }
                else {
                    verts.put(ix).put(iy).put(iz);
                    verts.put(ox).put(oy).put(oz);
                    txcrd.put(uu).put(v0);
                    txcrd.put(uu).put(v1);
                }
                if(u == m_uSteps) {
                    if(m_flip) {
                        verts.put(ox).put(oy).put(oz);
                        txcrd.put(uu).put(v1);
                    }
                    else {
                        verts.put(ix).put(iy).put(iz);
                        txcrd.put(uu).put(v0);
                    }
                }
            }
            r0 = r1;
            r1 = r1 + rStep;
        }
        updateModelBound();
    }

    protected float getZ(float x, float y) {
        return 0;
    }

    public void ensureSize(int numVerts) {
        m_numVerts = 0;
        if(numVerts > 0) {
            FloatBuffer verts = getMeshData().getVertexBuffer();
            FloatBuffer txcrd = null;
            final int sz2 = numVerts*2;
            final int sz3 = numVerts*3;

            if( verts == null || verts.capacity() < sz3 ) {
                if(verts != null) 
                    verts.clear();
                verts = BufferUtils.createFloatBuffer(sz3);
                getMeshData().setVertexBuffer(verts);
            }
            txcrd = getMeshData().getTextureBuffer(0);
            if(txcrd == null || txcrd.capacity() < sz2 ) {
                if(txcrd != null)
                    txcrd.clear();
                txcrd = BufferUtils.createFloatBuffer(sz2);
                getMeshData().setTextureBuffer(txcrd, 0);
            }
            if(verts.limit() != sz3) {
                verts.limit(sz3);
                txcrd.limit(sz2);
                getMeshData().updateVertexCount();
            }
        }
        m_numVerts = numVerts;
    }

}
