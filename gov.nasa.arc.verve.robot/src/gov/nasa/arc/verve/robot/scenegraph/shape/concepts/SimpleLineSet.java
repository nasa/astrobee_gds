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
package gov.nasa.arc.verve.robot.scenegraph.shape.concepts;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.apache.log4j.Logger;

import com.ardor3d.bounding.BoundingBox;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Vector3;
import com.ardor3d.renderer.IndexMode;
import com.ardor3d.renderer.Renderer;
import com.ardor3d.scenegraph.Line;
import com.ardor3d.scenegraph.MeshData;
import com.ardor3d.scenegraph.hint.LightCombineMode;
import com.ardor3d.util.geom.BufferUtils;

/**
 * @author mallan
 */
public class SimpleLineSet extends Line {
    static Logger logger = Logger.getLogger(SimpleLineSet.class);
    private static int	s_instance = 0;
    private final int 	m_instance;

    IntBuffer           m_idxBuffer = null;
    FloatBuffer         m_vtxBuffer = null;
    FloatBuffer         m_clrBuffer = null;

    int					m_totalVerts= 0;

    /**
     * 
     * @param name
     */
    public SimpleLineSet(String name) {
        m_instance = s_instance++;
        setName((name != null) ? name : this.getClass().getSimpleName()+m_instance);
        init();
    }

    @Override
    public void draw(Renderer r) {
        if(m_totalVerts > 1) { // XXX HACK XXX
            super.draw(r);
        }
    }

    public void init() {
        m_totalVerts = 0;

        setLineWidth(1);
        setAntialiased(false);
        getMeshData().setIndexMode(IndexMode.LineStrip);
        setDefaultColor(ColorRGBA.WHITE);

        setModelBound(new BoundingBox());
        getSceneHints().setAllPickingHints(false);
        getSceneHints().setLightCombineMode(LightCombineMode.Off);
    }


    /** 
     * get an array of LineInfos in which to store new line data. 
     * Array is initialized with new'ed LineInfos
     * @param numLines
     * @return
     */
    public static LineInfo[] newLineInfoArray(int numLines) {
        LineInfo[] lineInfos = new LineInfo[numLines];
        for(int i = 0; i < numLines; i++) {
            lineInfos[i] = new LineInfo();
        }
        return lineInfos;
    }

    static final float[] zeros = new float[] { 0,0,0 };
    
    /**
     * after line infos are set, update the geometry
     */
    @SuppressWarnings("null")
    public void updateGeometry(LineInfo[] lineInfos, float[] off) {
        //getSceneHints().setCullHint(CullHint.Inherit);

        if(off == null) {
            off = zeros;
        }
        
        m_totalVerts = 0;
        int numLines = (lineInfos==null) ? 0 : lineInfos.length;
        if(numLines <= 0) {
            //getSceneHints().setCullHint(CullHint.Always);
        }
        else {
            //getSceneHints().setCullHint(CullHint.Inherit);
            int[] lengths = new int[numLines];
            IndexMode[] indexModes = new IndexMode[numLines];
            for(int i = 0; i < numLines; i++) {
                indexModes[i] = IndexMode.LineStrip;
                lengths[i] = lineInfos[i].verts.size();
                m_totalVerts += lengths[i];
                if(lengths[i] < 0) {
                    logger.error("lengths["+i+"] = "+lengths[i]+" in updateGeometry().");
                    lengths[i] = 0;
                }
            }
            int i = 0;
            checkBuffers(m_totalVerts);
            for(LineInfo li : lineInfos) {
                for(Vector3 v : li.verts) {
                    m_idxBuffer.put(i++);

                    m_vtxBuffer.put(v.getXf() + off[0]);
                    m_vtxBuffer.put(v.getYf() + off[1]);
                    m_vtxBuffer.put(v.getZf() + off[2]);

                    m_clrBuffer.put(li.color.getRed());
                    m_clrBuffer.put(li.color.getGreen());
                    m_clrBuffer.put(li.color.getBlue());
                    m_clrBuffer.put(li.color.getAlpha());
                }
            }
            final MeshData md = getMeshData();
            md.updateVertexCount();
            md.setIndexLengths(lengths);
            md.setIndexModes(indexModes);
            updateModelBound();
        }
    }

    /**
     * ensure the native buffers have enough room
     * @param nVerts
     */
    protected void checkBuffers(int nVerts) {
        final int chunkSize = 20;
        final int chunks = 1 + (nVerts / chunkSize);
        final int required = chunks * chunkSize;
        if (m_idxBuffer == null || m_idxBuffer.capacity() < required) {
            m_clrBuffer = BufferUtils.createVector4Buffer(required);
            m_vtxBuffer = BufferUtils.createVector3Buffer(required);
            m_idxBuffer = BufferUtils.createIntBuffer(required);
            getMeshData().setVertexBuffer(m_vtxBuffer);
            getMeshData().setIndexBuffer (m_idxBuffer);
            getMeshData().setColorBuffer(m_clrBuffer);
        }
        m_clrBuffer.limit(nVerts * 4).rewind();
        m_vtxBuffer.limit(nVerts * 3).rewind();
        m_idxBuffer.limit(nVerts * 1).rewind();
    }

}
