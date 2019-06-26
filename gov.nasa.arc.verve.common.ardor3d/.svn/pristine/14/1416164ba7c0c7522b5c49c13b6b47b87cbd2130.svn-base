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
package gov.nasa.arc.verve.common.ardor3d.shape;

import gov.nasa.arc.verve.ardor3d.scenegraph.shape.AxisLines;
import gov.nasa.arc.verve.ardor3d.scenegraph.shape.TexQuad;

import com.ardor3d.image.Texture.MagnificationFilter;
import com.ardor3d.image.Texture.MinificationFilter;
import com.ardor3d.image.Texture.WrapMode;
import com.ardor3d.image.Texture2D;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Matrix3;
import com.ardor3d.math.type.ReadOnlyMatrix4;
import com.ardor3d.renderer.Camera;
import com.ardor3d.renderer.Renderer;
import com.ardor3d.renderer.queue.RenderBucketType;
import com.ardor3d.renderer.state.BlendState;
import com.ardor3d.renderer.state.BlendState.DestinationFunction;
import com.ardor3d.renderer.state.BlendState.SourceFunction;
import com.ardor3d.renderer.state.CullState;
import com.ardor3d.renderer.state.CullState.Face;
import com.ardor3d.renderer.state.TextureState;
import com.ardor3d.renderer.state.ZBufferState;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.hint.CullHint;
import com.ardor3d.scenegraph.hint.LightCombineMode;
import com.ardor3d.util.TextureManager;
import com.ardor3d.util.resource.URLResourceSource;

public class CompassAxisIcon extends Node {
    //private static final Logger logger = Logger.getLogger(CompassAxisIcon.class);

    protected Node      m_node;
    protected TexQuad   m_quad;
    protected AxisLines m_axisLines;
    protected Matrix3   m_rot = new Matrix3();
    protected double    m_offset;
    protected double    m_pad   = 75;
    protected int       m_yMode = 2;
    Compass.VAlign      m_vAlign = Compass.VAlign.Top;
    Compass.HAlign      m_hAlign = Compass.HAlign.Right;

    public CompassAxisIcon(String name, float radius) {
        this(name, radius, true, Compass.VAlign.Top, Compass.HAlign.Right);
    }

    public CompassAxisIcon(String name, float radius, boolean drawCompass) {
        this(name, radius, drawCompass, Compass.VAlign.Top, Compass.HAlign.Right);
    }

    public CompassAxisIcon(String name, float radius, boolean drawCompass, Compass.VAlign vAlign, Compass.HAlign hAlign) {
        super(name);
        m_offset = radius + 2;
        m_vAlign = vAlign;
        m_hAlign = hAlign;
        
        m_node = new Node(name+"Node");
        attachChild(m_node);

        m_axisLines = new AxisLines(name+"AxisLines", radius, 0.9f);
        m_axisLines.setAntialiased(true);
        m_node.attachChild(m_axisLines);

        if(drawCompass) {
            m_axisLines.setAlpha(0.3f);
            m_quad = new TexQuad(name+"TexQuad", radius*1.7f, false);
            m_quad.setDefaultColor(new ColorRGBA(1f,1f,1f,0.4f));
            URLResourceSource rs = new URLResourceSource(this.getClass().getResource("compass.png"));
            Texture2D tex = (Texture2D)TextureManager.load(rs, 
                                                           MinificationFilter.Trilinear, 
                                                           false);
            tex.setAnisotropicFilterPercent(1.0f);
            tex.setMagnificationFilter(MagnificationFilter.Bilinear);
            tex.setWrap(WrapMode.EdgeClamp);

            TextureState ts = new TextureState();
            ts.setTexture(tex);
            m_quad.setRenderState(ts);
            m_node.attachChild(m_quad);
        }

        getSceneHints().setRenderBucketType(RenderBucketType.Ortho);
        getSceneHints().setLightCombineMode(LightCombineMode.Off);
        getSceneHints().setCullHint(CullHint.Never);
        getSceneHints().setAllPickingHints(false);

        ZBufferState zs = new ZBufferState();
        zs.setFunction(ZBufferState.TestFunction.Always);
        zs.setWritable(false);
        setRenderState(zs);

        BlendState bs = new BlendState();
        bs.setBlendEnabled(true);
        bs.setSourceFunction(SourceFunction.SourceAlpha);
        if(drawCompass) 
            bs.setDestinationFunction(DestinationFunction.One); 
        else
            bs.setDestinationFunction(DestinationFunction.OneMinusSourceAlpha);
        setRenderState(bs);

        CullState cs = new CullState();
        cs.setCullFace(Face.None);
        setRenderState(cs);

        // squish so we don't get clipped
        setScale(1, 1, 0.001);
    }

    public AxisLines getAxisLines() {
        return m_axisLines;
    }

    public TexQuad getTexQuad() {
        return m_quad;
    }

    @Override
    public void draw(final Renderer r) {
        final Camera cam = Camera.getCurrentCamera();
        final double z = 0;
        double x = 0, y = 0;
        
        switch(m_hAlign) {
        case Left:   x = m_offset; break;
        case Right:  x = cam.getWidth()-m_offset; break; 
        }
        switch(m_vAlign) {
        case Top:    y = cam.getHeight()-m_offset; break;
        case Bottom: y = m_offset; break;
        }

        ReadOnlyMatrix4 m44 = cam.getModelViewMatrix();
        // transpose of modelview rotation
        m_rot.set(m44.getValue(0,0), m44.getValue(1,0), m44.getValue(2,0), 
                  m44.getValue(0,1), m44.getValue(1,1), m44.getValue(2,1), 
                  m44.getValue(0,2), m44.getValue(1,2), m44.getValue(2,2));

        m_node.setTranslation(x,y,z);
        m_node.setRotation(m_rot);
        m_node.updateGeometricState(0, true);
        m_node.updateWorldTransform(true);
        super.draw(r);
    }
}
