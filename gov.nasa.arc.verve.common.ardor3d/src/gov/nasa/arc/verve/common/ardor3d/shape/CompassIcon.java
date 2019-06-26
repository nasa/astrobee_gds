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

import gov.nasa.arc.verve.ardor3d.scenegraph.shape.TexQuad;

import org.apache.log4j.Logger;

import com.ardor3d.image.Texture.MagnificationFilter;
import com.ardor3d.image.Texture.MinificationFilter;
import com.ardor3d.image.Texture.WrapMode;
import com.ardor3d.image.Texture2D;
import com.ardor3d.math.Matrix3;
import com.ardor3d.math.Vector3;
import com.ardor3d.renderer.Camera;
import com.ardor3d.renderer.Renderer;
import com.ardor3d.renderer.queue.RenderBucketType;
import com.ardor3d.renderer.state.BlendState;
import com.ardor3d.renderer.state.BlendState.DestinationFunction;
import com.ardor3d.renderer.state.BlendState.SourceFunction;
import com.ardor3d.renderer.state.TextureState;
import com.ardor3d.renderer.state.ZBufferState;
import com.ardor3d.scenegraph.hint.CullHint;
import com.ardor3d.scenegraph.hint.LightCombineMode;
import com.ardor3d.util.Ardor3dException;
import com.ardor3d.util.TextureManager;
import com.ardor3d.util.resource.URLResourceSource;

public class CompassIcon extends TexQuad {
    private static Logger logger = Logger.getLogger(CompassIcon.class);

    Matrix3 m_rot = new Matrix3();
    Vector3 m_dir = new Vector3();
    Compass.VAlign  m_vAlign = Compass.VAlign.Top;
    Compass.HAlign  m_hAlign = Compass.HAlign.Right;
    double  m_offset;

    public CompassIcon(String name, float radius) {
        this(name, radius, Compass.VAlign.Top, Compass.HAlign.Right);
    }

    public CompassIcon(String name, float radius, Compass.VAlign vAlign, Compass.HAlign hAlign) {
        super(name, radius*2, false);
        m_vAlign = vAlign;
        m_hAlign = hAlign;
        m_offset = radius + 2;
        getSceneHints().setRenderBucketType(RenderBucketType.Ortho);
        getSceneHints().setCullHint(CullHint.Never);
        getSceneHints().setLightCombineMode(LightCombineMode.Off);

        ZBufferState zs = new ZBufferState();
        zs.setFunction(ZBufferState.TestFunction.Always);
        zs.setWritable(false);
        setRenderState(zs);

        BlendState bs = new BlendState();
        bs.setBlendEnabled(true);
        bs.setSourceFunction(SourceFunction.SourceAlpha);
        bs.setDestinationFunction(DestinationFunction.One); //MinusSourceAlpha);
        setRenderState(bs);

        URLResourceSource rs = new URLResourceSource(this.getClass().getResource("compass.png"));
        Texture2D tex = (Texture2D)TextureManager.load(rs, 
                                                       MinificationFilter.Trilinear, 
                                                       false);
        tex.setMagnificationFilter(MagnificationFilter.Bilinear);
        tex.setWrap(WrapMode.EdgeClamp);
        
        TextureState ts = new TextureState();
        ts.setTexture(tex);
        setRenderState(ts);

        getSceneHints().setAllPickingHints(false);
        setModelBound(null);
    }

    @Override
    public void draw(final Renderer r) {
        final Camera cam = Camera.getCurrentCamera();
        double x=0,y=0;

        switch(m_hAlign) {
        case Left:   x = m_offset; break;
        case Right:  x = cam.getWidth()-m_offset; break; 
        }

        switch(m_vAlign) {
        case Top:    y = cam.getHeight() - m_offset; break;
        case Bottom: y = m_offset; break;
        }

        m_dir.set(cam.getDirection());
        if(Math.abs(m_dir.getZ()) > 0.7) {
            m_dir.set(cam.getUp());
        }
        m_dir.setZ(0);
        m_dir.normalizeLocal();
        double a = Math.acos(m_dir.getY());
        if(m_dir.getX() < 0) {
            a = -a;
        }
        try {
            m_rot.fromAngleNormalAxis(a, Vector3.UNIT_Z);
            _worldTransform.setRotation(m_rot);
            _worldTransform.setTranslation(x,y,0);
        }
        catch(Ardor3dException e) {
            logger.error("CompassIcon transform is invalid, a = "+a);
            _worldTransform.setIdentity();
        }
        super.draw(r);
    }
}
