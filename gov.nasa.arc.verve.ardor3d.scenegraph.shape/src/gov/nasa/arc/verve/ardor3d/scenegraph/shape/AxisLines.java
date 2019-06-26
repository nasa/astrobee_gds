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
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.type.ReadOnlyColorRGBA;
import com.ardor3d.scenegraph.Line;
import com.ardor3d.scenegraph.hint.LightCombineMode;
import com.ardor3d.util.geom.BufferUtils;

/**
 * 
 * @author mallan
 *
 */
public class AxisLines extends Line {
    private ColorRGBA m_color  = null;
    private float     m_size   = 1;
    private float     m_alpha  = 1;
    
    public AxisLines(String name, float size) {
        this(name, size, 1);
    }
    
    public AxisLines(String name, float size, float alpha) {
        super(name);
        m_size = size;
        m_alpha = alpha;
        init();
    }
    
    public AxisLines(String name, float size, ReadOnlyColorRGBA centerColor) {
        super(name);
        if(centerColor != null)
            m_color = new ColorRGBA(centerColor);
        else 
            m_color = null;
        m_size = size;
        init();
    }
    
    public void setSize(double size) {
        setSize((float)size);
    }
    public void setSize(float size) {
        m_size = size;
        updateSize(size, getMeshData().getVertexBuffer());
    }
    public float getSize() {
        return m_size;
    }
    
    public float getAlpha() {
        return m_alpha;
    }
    public void setAlpha(float alpha) {
        m_alpha = alpha;
        updateColor(m_color, getMeshData().getColorBuffer(), m_alpha);
    }
    
    public void setCenterColor(ReadOnlyColorRGBA color) {
        if(color != null) {
            m_color = new ColorRGBA(color);
        }
        else {
            m_color = null;
        }
        updateColor(m_color, getMeshData().getColorBuffer(), m_alpha);
    }
    
    public ReadOnlyColorRGBA getCenterColor() {
        return m_color;
    }
    
    private void init() {
        setLineWidth(3);
        setAntialiased(false);
        getSceneHints().setLightCombineMode(LightCombineMode.Off);
        
        final int vertexCount = 6;
        FloatBuffer vtxBuffer = BufferUtils.createFloatBuffer(vertexCount*3);
        FloatBuffer clrBuffer = BufferUtils.createFloatBuffer(vertexCount*4);
        
        updateSize(m_size, vtxBuffer);
        updateColor(m_color, clrBuffer, m_alpha);
        
        getMeshData().setVertexBuffer(vtxBuffer);
        getMeshData().setNormalBuffer(null);
        getMeshData().setColorBuffer(clrBuffer);

        //generateIndices();
        setModelBound(new BoundingSphere());
    }
    
    private static FloatBuffer updateSize(float size, FloatBuffer vtxBuffer) {
        vtxBuffer.rewind();
        final float s = size;
        vtxBuffer.put(0).put(0).put(0);  vtxBuffer.put(s).put(0).put(0);
        vtxBuffer.put(0).put(0).put(0);  vtxBuffer.put(0).put(s).put(0);
        vtxBuffer.put(0).put(0).put(0);  vtxBuffer.put(0).put(0).put(s);
        return vtxBuffer;
    }
    
    private static FloatBuffer updateColor(ReadOnlyColorRGBA color, FloatBuffer clrBuffer, float alpha) {
        clrBuffer.rewind();
        final float k = 0.2f;
        final float ka = alpha;
        
        final float[] gr = {0f, 153.f/255.f, 76.f/255.f};
        
        if(color == null) {
            clrBuffer.put(1).put(k).put(k).put(ka);  clrBuffer.put(1).put(k).put(k).put(ka);
            clrBuffer.put(gr[0]).put(gr[1]).put(gr[2]).put(ka);  clrBuffer.put(gr[0]).put(gr[1]).put(gr[2]).put(ka);
            clrBuffer.put(k).put(k).put(1).put(ka);  clrBuffer.put(k).put(k).put(1).put(ka);
        }
        else {
            final float r = color.getRed();
            final float g = color.getGreen();
            final float b = color.getBlue();
            final float a = color.getAlpha();
            clrBuffer.put(r).put(g).put(b).put(a);  clrBuffer.put(1).put(k).put(k).put(ka);
            clrBuffer.put(r).put(g).put(b).put(a);  clrBuffer.put(k).put(1).put(k).put(ka);
            clrBuffer.put(r).put(g).put(b).put(a);  clrBuffer.put(k).put(k).put(1).put(ka);
        }
        return clrBuffer;
    }
    
}
