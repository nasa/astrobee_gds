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
package gov.nasa.arc.verve.robot.scenegraph.visitors;

import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.type.ReadOnlyColorRGBA;
import com.ardor3d.renderer.queue.RenderBucketType;
import com.ardor3d.renderer.state.BlendState;
import com.ardor3d.renderer.state.RenderState.StateType;
import com.ardor3d.scenegraph.Line;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.scenegraph.hint.CullHint;
import com.ardor3d.scenegraph.hint.LightCombineMode;
import com.ardor3d.scenegraph.hint.TransparencyType;
import com.ardor3d.scenegraph.visitor.Visitor;

/**
 */
public class LineColorVisitor implements Visitor {
    private final ColorRGBA m_lineClr = new ColorRGBA(1,1,1,1);
    //private final ColorRGBA m_lineClr = new ColorRGBA(0.4f, 0.2f, 0.01f, 0.75f);
    private boolean m_show   = true;
    private boolean m_neon   = true;
    private boolean m_smooth = false;
    private float   m_lineWidth = 2;

    public LineColorVisitor(boolean showLines, ReadOnlyColorRGBA clr, float lineWidth, boolean smooth, boolean neon) {
        m_show = showLines;
        setColor(clr);
        m_lineWidth = lineWidth;
        m_smooth = smooth;
        m_neon = neon;
    }   
    public LineColorVisitor(boolean showLines) {
        m_show = false;
    }
    public LineColorVisitor() { 
        // empty    
    }

    public void setColor(ReadOnlyColorRGBA clr) {
        m_lineClr.set(clr);
    }
    public ReadOnlyColorRGBA getColor() {
        return m_lineClr;
    }

    public void setShowLines(boolean doShow) {
        m_show = doShow;
    }
    public boolean isShowLines() {
        return m_show;
    }
    
    public void setLineWidth(float lineWidth) {
        m_lineWidth = lineWidth;
    }
    public float getLineWidth() {
        return m_lineWidth;
    }

    public void setSmooth(boolean doSmooth) {
        m_smooth = doSmooth;
    }
    public boolean isSmooth() {
        return m_smooth;
    }

    public void setNeon(boolean doNeon) {
        m_neon = doNeon;
    }
    public boolean isNeon() {
        return m_neon;
    }

    public void visit(final Spatial spatial) {
        if(spatial instanceof Line) {
            if(m_show) spatial.getSceneHints().setCullHint(CullHint.Inherit);
            else       spatial.getSceneHints().setCullHint(CullHint.Always);
            Line line = (Line)spatial;
            line.setAntialiased(m_smooth);
            line.setLineWidth(m_lineWidth);
            line.clearRenderState(StateType.Material);
            line.getSceneHints().setLightCombineMode(LightCombineMode.Off);
            //line.getSceneHints().setRenderBucketType(RenderBucketType.PostBucket);
            line.getSceneHints().setRenderBucketType(RenderBucketType.Transparent);
            line.getSceneHints().setTransparencyType(TransparencyType.TwoPass);
            line.setDefaultColor(m_lineClr);
            if(m_neon) {
                BlendState bs = new BlendState();
                bs.setSourceFunction(BlendState.SourceFunction.SourceAlpha);
                bs.setDestinationFunction(BlendState.DestinationFunction.One);
                bs.setBlendEnabled(true);
                bs.setEnabled(true);
                line.setRenderState(bs);
            }
            else {
                line.clearRenderState(StateType.Blend);
            }
        }
    }
}
