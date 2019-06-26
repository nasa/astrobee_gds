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
import java.nio.ShortBuffer;

import org.apache.log4j.Logger;

import com.ardor3d.bounding.BoundingSphere;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.type.ReadOnlyColorRGBA;
import com.ardor3d.renderer.IndexMode;
import com.ardor3d.renderer.queue.RenderBucketType;
import com.ardor3d.renderer.state.BlendState;
import com.ardor3d.renderer.state.RenderState;
import com.ardor3d.renderer.state.ZBufferState;
import com.ardor3d.scenegraph.Line;
import com.ardor3d.scenegraph.MeshData;
import com.ardor3d.scenegraph.hint.LightCombineMode;
import com.ardor3d.util.geom.BufferUtils;


public class RadialGrid extends Line {
    private static Logger logger = Logger.getLogger(RadialGrid.class);
    int                 m_numSegments   =  48;
    float               m_spacing       =  1;
    int                 m_lastCircle    =  11;
    int                 m_firstCircle   =  1;

    int                 m_clrScheme = 0;
    final ColorRGBA     m_clrHi   = new ColorRGBA(0.8f, 1.00f, 1.0f, 1);
    final ColorRGBA     m_clrMid  = new ColorRGBA(0.4f, 0.70f, 0.8f, 1);
    final ColorRGBA     m_clrLow  = new ColorRGBA(0.2f, 0.35f, 0.4f, 0.5f);

    final ColorRGBA     m_customClr = new ColorRGBA(0.5f, 0.5f, 0.5f, 1);

    float               m_alphaMult = 1;

    int m_midEvery = 5;
    int m_hiEvery  = 10;

    /**
     * 
     * @param name
     */
    //=========================================================================
    public RadialGrid(String name, double innerRadius, double outerRadius, ReadOnlyColorRGBA customColor) {
        this(name, innerRadius, outerRadius, customColor, 1);
    }

    public RadialGrid(String name, double innerRadius, double outerRadius, ReadOnlyColorRGBA customColor, float alphaMultiplier) {
        super(name);
        setLineWidth(1.5f);

        ZBufferState zs = new ZBufferState();
        zs.setWritable(true);
        zs.setFunction(ZBufferState.TestFunction.Always);  
        setRenderState(zs);

        setAntialiased(true);
        BlendState bs = new BlendState();
        bs.setBlendEnabled(true);
        setRenderState(bs);

        getSceneHints().setAllPickingHints(false);
        getSceneHints().setLightCombineMode(LightCombineMode.Off);
        getSceneHints().setRenderBucketType(RenderBucketType.Transparent);

        m_customClr.set(customColor);
        m_alphaMult = alphaMultiplier;
        setColorScheme(m_clrScheme);
        setRadius(innerRadius, outerRadius);

    }

    public void setAlphaMultiplier(float val) {
        m_alphaMult = val;
        setColorScheme(m_clrScheme);
    }

    public float getAlphaMultiplier() {
        return m_alphaMult;
    }

    public boolean isDepthTest() {
        ZBufferState zs = (ZBufferState)getLocalRenderState(RenderState.StateType.ZBuffer);
        if(zs.getFunction().equals(ZBufferState.TestFunction.Always)) {
            return false;
        }
        return true;
    }

    public void setDepthTest(boolean status) {
        ZBufferState zs = (ZBufferState)getLocalRenderState(RenderState.StateType.ZBuffer);
        if(status) {
            zs.setFunction(ZBufferState.TestFunction.LessThanOrEqualTo);  
        }
        else {
            zs.setFunction(ZBufferState.TestFunction.Always);  
        }
    }

    public boolean isBlend() {
        BlendState bs = (BlendState)getLocalRenderState(RenderState.StateType.Blend);
        return bs.isBlendEnabled();
    }

    public void setBlend(boolean blend) {
        BlendState bs = (BlendState)getLocalRenderState(RenderState.StateType.Blend);
        bs.setBlendEnabled(blend);
    }

    public void setCustomColor(ReadOnlyColorRGBA clr) {
        m_customClr.set(clr);
    }

    public ReadOnlyColorRGBA getCustomColor() {
        return m_customClr;
    }

    public void setInnerRadius(double innerRadius) {
        m_firstCircle = (int)((innerRadius/m_spacing)+0.001);
        setColorScheme(m_clrScheme);
        init();
    }

    public void setOuterRadius(double outerRadius) {
        m_lastCircle = 1 + (int)((outerRadius/m_spacing)+0.001);
        setColorScheme(m_clrScheme);
        init();
    }

    public void setRadius(double innerRadius, double outerRadius) {
        m_firstCircle = (int)((innerRadius/m_spacing)+0.001);
        m_lastCircle = 1 + (int)((outerRadius/m_spacing)+0.001);
        setColorScheme(m_clrScheme);
        init();
    }

    /**
     * NOTE: does not automatically init()
     * @param index
     */
    public void setColorScheme(int index) {
        m_clrScheme = index;
        if(index == 0) {
            ReadOnlyColorRGBA c = m_customClr;
            m_clrLow.set(c.getRed()*1, c.getGreen()*1, c.getBlue()*1, m_alphaMult*0.5f);
            m_clrMid.set(c.getRed()*2, c.getGreen()*2, c.getBlue()*2, m_alphaMult*1);
            m_clrHi.set (c.getRed()*4, c.getGreen()*4, c.getBlue()*4, m_alphaMult*1);
        }
        else {
            int ix = Math.abs((index-1)%3);
            float[] v = new float[] { 0.2f, 0.35f, 0.4f };
            float[] vix = new float[3];
            for(int i = 0; i < 3; i++) {
                vix[i] = v[(i+ix)%3];
            }
            m_clrLow.set(vix[0]*1, vix[1]*1, vix[2]*1, m_alphaMult*0.5f);
            m_clrMid.set(vix[0]*2, vix[1]*2, vix[2]*2, m_alphaMult*1);
            m_clrHi.set (vix[0]*4, vix[1]*4, vix[2]*4, m_alphaMult*1);
        }
        m_clrLow.clamp(m_clrLow);
        m_clrMid.clamp(m_clrMid);
        m_clrHi.clamp(m_clrHi);
    }
    public int getColorScheme() {
        return m_clrScheme;
    } 

    /**
     * initalize geometry
     */
    private void init() {
        try {
            int totalCircles = m_lastCircle - m_firstCircle;
            int vertexCount = 0;
            float r;
            float a;

            final int numRayHi  = 8;
            final int numRayMid = 8;
            final int numRayLow = 8;
            //-- concentric circles 
            vertexCount = (m_numSegments * totalCircles) + numRayHi + numRayMid + numRayLow;
            int[] stripVertexLens = new int[totalCircles + 3];
            IndexMode[] stripIndexModes = new IndexMode[totalCircles + 3];
            int i;
            for(i = 0; i < totalCircles; i++) {
                stripVertexLens[i] = m_numSegments;
                stripIndexModes[i] = IndexMode.LineStrip;
            }
            stripVertexLens[i] = numRayHi; // hi rays
            stripIndexModes[i] = IndexMode.Lines; 
            i++;
            stripVertexLens[i] = numRayMid; // mid rays
            stripIndexModes[i] = IndexMode.Lines;
            i++;
            stripVertexLens[i] = numRayLow; // lo rays
            stripIndexModes[i] = IndexMode.Lines;

            FloatBuffer vtxBuffer = BufferUtils.createFloatBuffer(vertexCount*3);
            FloatBuffer clrBuffer = BufferUtils.createFloatBuffer(vertexCount*4);
            ShortBuffer idxBuffer = BufferUtils.createShortBuffer(vertexCount);

            short idx = 0;
            ColorRGBA clr;
            for(int c = m_firstCircle; c < m_lastCircle; c++) {
                r = c * m_spacing;
                for(int s = 0; s < m_numSegments; s++) {
                    idxBuffer.put(idx++);

                    a   = ((float)Math.PI*2) * s/(m_numSegments-1.0f);

                    vtxBuffer.put(r * (float)Math.sin(a));
                    vtxBuffer.put(r * (float)Math.cos(a));
                    vtxBuffer.put(0);

                    //-- vertex colors
                    if     (c%m_hiEvery == 0)  clr = m_clrHi;
                    else if(c%m_midEvery == 0) clr = m_clrMid;
                    else                       clr = m_clrLow;       
                    clrBuffer.put(clr.getRed());
                    clrBuffer.put(clr.getGreen());
                    clrBuffer.put(clr.getBlue());
                    clrBuffer.put(clr.getAlpha());
                }
            }

            float x,y;
            final float pi2  = (float)(Math.PI/2);
            final float pi4  = (float)(Math.PI/4);
            final float pi12 = (float)(Math.PI/12);
            final float radius = (m_lastCircle-1)*m_spacing;
            final float inner  = m_firstCircle*m_spacing;

            //-- 90 degree marks
            clr = m_clrHi;
            for(i = 0; i < 4; i++) {
                x = (float)Math.cos(i*pi2);
                y = (float)Math.sin(i*pi2);
                idxBuffer.put(idx++);
                vtxBuffer.put(inner * x);
                vtxBuffer.put(inner * y);
                vtxBuffer.put(0);
                clrBuffer.put(clr.getRed()).put(clr.getGreen()).put(clr.getBlue()).put(clr.getAlpha());

                idxBuffer.put(idx++);
                vtxBuffer.put(radius * x);
                vtxBuffer.put(radius * y);
                vtxBuffer.put(0);
                clrBuffer.put(clr.getRed()).put(clr.getGreen()).put(clr.getBlue()).put(clr.getAlpha());     
            }

            //-- 45 degree marks
            clr = m_clrMid;
            for(i = 0; i < 4; i++) {
                x = (float)Math.cos(pi4 + i*pi2);
                y = (float)Math.sin(pi4 + i*pi2);

                idxBuffer.put(idx++);
                vtxBuffer.put(inner * x);
                vtxBuffer.put(inner * y);
                vtxBuffer.put(0);
                clrBuffer.put(clr.getRed()).put(clr.getGreen()).put(clr.getBlue()).put(clr.getAlpha());

                idxBuffer.put(idx++);
                vtxBuffer.put(radius * x);
                vtxBuffer.put(radius * y);
                vtxBuffer.put(0);
                clrBuffer.put(clr.getRed()).put(clr.getGreen()).put(clr.getBlue()).put(clr.getAlpha());     
            }

            //-- 15 degrees, in -45 to +45 range
            clr = m_clrLow;
            for(i = 0; i < 2; i++) {
                x = (float)Math.cos(pi12 + i*pi12);
                y = (float)Math.sin(pi12 + i*pi12);

                idxBuffer.put(idx++);
                vtxBuffer.put(inner * x);
                vtxBuffer.put(inner * y);
                vtxBuffer.put(0);
                clrBuffer.put(clr.getRed()).put(clr.getGreen()).put(clr.getBlue()).put(clr.getAlpha());

                idxBuffer.put(idx++);
                vtxBuffer.put(radius * x);
                vtxBuffer.put(radius * y);
                vtxBuffer.put(0);
                clrBuffer.put(clr.getRed()).put(clr.getGreen()).put(clr.getBlue()).put(clr.getAlpha());

                idxBuffer.put(idx++);
                vtxBuffer.put( inner * x);
                vtxBuffer.put(-inner * y);
                vtxBuffer.put(0);
                clrBuffer.put(clr.getRed()).put(clr.getGreen()).put(clr.getBlue()).put(clr.getAlpha());

                idxBuffer.put(idx++);
                vtxBuffer.put( radius * x);
                vtxBuffer.put(-radius * y);
                vtxBuffer.put(0);
                clrBuffer.put(clr.getRed()).put(clr.getGreen()).put(clr.getBlue()).put(clr.getAlpha());     
            }

            MeshData mdata = getMeshData();
            mdata.setIndexLengths(stripVertexLens);
            mdata.setIndexModes(stripIndexModes);
            mdata.setVertexBuffer(vtxBuffer);
            mdata.setColorBuffer(clrBuffer);
            mdata.setIndexBuffer(idxBuffer);

            BoundingSphere bound = new BoundingSphere();
            bound.computeFromPoints(getMeshData().getVertexBuffer());
            this.setModelBound(bound);
        }
        catch(Throwable t) {
            logger.error("Unexpected exception: ", t);
        }
    }

}
