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
package gov.nasa.arc.verve.ardor3d.e4.framework;


import gov.nasa.arc.verve.common.ardor3d.text.BMFontManager;
import gov.nasa.arc.verve.common.ardor3d.text.BMText;

import java.nio.FloatBuffer;

import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Matrix3;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyVector3;
import com.ardor3d.renderer.IndexMode;
import com.ardor3d.renderer.Renderer;
import com.ardor3d.renderer.queue.RenderBucketType;
import com.ardor3d.renderer.state.BlendState;
import com.ardor3d.renderer.state.RenderState;
import com.ardor3d.renderer.state.ZBufferState;
import com.ardor3d.scenegraph.Line;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.util.geom.BufferUtils;

public class DebugLine extends Node {
    final FloatBuffer   m_vtxBuffer =  BufferUtils.createVector3Buffer(2);
    final Vector3       m_ctr = new Vector3();
    final Matrix3       m_frame = new Matrix3();
    final Vector3       m_aInFrame = new Vector3();
    final Vector3       m_bInFrame = new Vector3();
    
    final Line          m_line;
    BMText              m_distText;
    BMText              m_pickText;
    BMText              m_lastText;

    ColorRGBA lineColor     = new ColorRGBA(1.0f, 1.0f, 0.0f, 1); 
    ColorRGBA distTextColor = new ColorRGBA(1.0f, 1.0f, 0.6f, 1); 
    ColorRGBA pickTextColor = new ColorRGBA(0.9f, 0.9f, 0.9f, 1); 
    ColorRGBA lastTextColor = new ColorRGBA(0.8f, 0.8f, 0.8f, 1); 

    /**
     * 
     * @param name
     */
    public DebugLine(String name) {
        super(name);
        m_frame.setIdentity();
        
        m_vtxBuffer.put(0).put(0).put(0);
        m_vtxBuffer.put(0).put(0).put(0);

        m_line = new Line("DebugLine");
        m_line.setLineWidth(2);
        m_line.setAntialiased(true);
        m_line.setDefaultColor(lineColor);
        m_line.getMeshData().setIndexMode(IndexMode.Lines);
        m_line.getMeshData().setVertexBuffer(m_vtxBuffer);
        m_line.getSceneHints().setRenderBucketType(RenderBucketType.Skip);

        m_distText = new BMText("DistanceLineText", "", BMFontManager.monoMedium(),
                BMText.Align.Center, BMText.Justify.Center, true);
        m_distText.setAutoRotate(true);
        m_distText.setAutoScale(BMText.AutoScale.FixedScreenSize);
        m_distText.getSceneHints().setRenderBucketType(RenderBucketType.Skip);
        m_distText.setAutoFade(BMText.AutoFade.Off);
        m_distText.setTextColor(distTextColor);
        m_distText.setUseBlend(true);
        
        m_pickText = new BMText("PickLineText", "", BMFontManager.sansSmall(),
                BMText.Align.West, BMText.Justify.Center, true);
        m_pickText.setAutoRotate(true);
        m_pickText.setAutoScale(BMText.AutoScale.FixedScreenSize);
        m_pickText.getSceneHints().setRenderBucketType(RenderBucketType.Skip);
        m_pickText.setAutoFade(BMText.AutoFade.Off);
        m_pickText.setTextColor(pickTextColor);
        m_pickText.setUseBlend(true);
        
        m_lastText = new BMText("LastLineText", "", BMFontManager.sansSmall(),
                BMText.Align.West, BMText.Justify.Center, true);
        m_lastText.setAutoRotate(true);
        m_lastText.setAutoScale(BMText.AutoScale.FixedScreenSize);
        m_lastText.getSceneHints().setRenderBucketType(RenderBucketType.Skip);
        m_lastText.setAutoFade(BMText.AutoFade.Off);
        m_lastText.setTextColor(lastTextColor);
        m_lastText.setUseBlend(true);
        
        BlendState bs = new BlendState();
        bs.setBlendEnabled(true);
        setRenderState(bs);

        ZBufferState zs;
        zs = new ZBufferState();
        zs.setFunction(ZBufferState.TestFunction.LessThan);
        m_line.setRenderState(zs);
        
        //zs = new ZBufferState();
        setZBufferState(m_distText);
        setZBufferState(m_pickText);
        setZBufferState(m_lastText);
        
        attachChild(m_distText);
        attachChild(m_pickText);
        attachChild(m_lastText);
        attachChild(m_line);

        getSceneHints().setRenderBucketType(RenderBucketType.Skip);
        updateGeometricState(0);
    }

    
    public void setCoordFrame(Matrix3 rot) {
        m_frame.set(rot);
    }
    
    @Override
    public void draw(Renderer r) {
        super.draw(r);
    }

    /**
     * FIXME: the auto-state setting in BMFont is biting me here... 
     * rethink, patch, and send to Josh
     */
    private void setZBufferState(BMText s) {
        ZBufferState zs = (ZBufferState) s.getLocalRenderState(RenderState.StateType.ZBuffer);
        zs.setFunction(ZBufferState.TestFunction.Always);
    }
    
    public void setEndPoints(ReadOnlyVector3 a, ReadOnlyVector3 b) {
        a.add(b, m_ctr);
        m_ctr.multiplyLocal(0.5);

        m_frame.applyPost(a, m_aInFrame);
        m_frame.applyPost(b, m_bInFrame);
        
        m_distText.setText(String.format("%.2fm",a.distance(b)));
        m_distText.setTranslation(m_ctr);

        m_lastText.setText(String.format("[%.1f, %.1f, %.1f]",
                m_aInFrame.getX(), m_aInFrame.getY(), m_aInFrame.getZ()));
        m_lastText.setTranslation(a);

        m_pickText.setText(String.format("[%.1f, %.1f, %.1f]",
                m_bInFrame.getX(), m_bInFrame.getY(), m_bInFrame.getZ()));
        m_pickText.setTranslation(b);

        m_vtxBuffer.rewind();
        m_vtxBuffer.put(a.getXf()).put(a.getYf()).put(a.getZf());
        m_vtxBuffer.put(b.getXf()).put(b.getYf()).put(b.getZf());

        m_line.getMeshData().updateVertexCount();
        
        setZBufferState(m_distText);
        setZBufferState(m_lastText);
        setZBufferState(m_pickText);
        
        updateGeometricState(0);
        updateWorldRenderStates(true);
    }
    
    public void setAlpha(float val) {
        lineColor.setAlpha(val);
        distTextColor.setAlpha(val);
        pickTextColor.setAlpha(val);
        lastTextColor.setAlpha(val);
        m_line.setDefaultColor(lineColor);
        m_distText.setTextColor(distTextColor);
        m_pickText.setTextColor(pickTextColor);
        m_lastText.setTextColor(lastTextColor);
    }
}
