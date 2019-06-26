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
package gov.nasa.arc.verve.common;

import com.ardor3d.bounding.BoundingBox;
import com.ardor3d.bounding.BoundingSphere;
import com.ardor3d.bounding.BoundingVolume;
import com.ardor3d.bounding.OrientedBoundingBox;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Vector3;
import com.ardor3d.renderer.Camera;
import com.ardor3d.renderer.Renderer;
import com.ardor3d.renderer.queue.RenderBucketType;
import com.ardor3d.renderer.state.BlendState;
import com.ardor3d.renderer.state.CullState;
import com.ardor3d.renderer.state.OffsetState;
import com.ardor3d.renderer.state.WireframeState;
import com.ardor3d.renderer.state.ZBufferState;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.Renderable;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.scenegraph.hint.CullHint;
import com.ardor3d.scenegraph.hint.LightCombineMode;
import com.ardor3d.scenegraph.shape.Box;
import com.ardor3d.scenegraph.shape.OrientedBox;
import com.ardor3d.scenegraph.shape.Sphere;
import com.ardor3d.util.scenegraph.RenderDelegate;

/**
 * Draws the bounds of spatials in VerveDebug
 * @author mallan
 *
 */
public class VerveDebugBoundsRenderSpatial extends Spatial {
    public static final ColorRGBA  foreColor     = new ColorRGBA(0.6f, 1.0f, 0.8f, 0.15f);
    public static final ColorRGBA  lineColor     = new ColorRGBA(0.6f, 1.0f, 0.8f, 0.15f);
    public static final ColorRGBA  selectColor   = new ColorRGBA(1.0f, 0.4f, 0.2f, 0.15f);
    public static final ColorRGBA  debugColor    = new ColorRGBA(0.0f, 0.7f, 1.0f, 0.1f);
    
    private final int           Z_SAMPLES = 5;
    private final int           R_SAMPLES = 6;
    private final Sphere        m_fillSphere   = new Sphere("debug_bsphere", Z_SAMPLES, R_SAMPLES, 1);
    private final Sphere        m_wireSphere   = new Sphere("debug_bsphere", Z_SAMPLES, R_SAMPLES, 1);
    private final Sphere        m_zbufSphere   = new Sphere("debug_bsphere", Z_SAMPLES, R_SAMPLES, 1);
    private final Box           m_fillBox      = new Box("debug_bbox", new Vector3(), 1, 1, 1);
    private final Box           m_wireBox      = new Box("debug_bbox", new Vector3(), 1, 1, 1);
    private final Box           m_zbufBox      = new Box("debug_bbox", new Vector3(), 1, 1, 1);
    private final OrientedBox   m_fillObb      = new OrientedBox("debug_obbox");
    private final OrientedBox   m_wireObb      = new OrientedBox("debug_obbox");
    private final OrientedBox   m_zbufObb      = new OrientedBox("debug_obbox");
    private ColorRGBA           m_backClr      = selectColor;

    /**
     * 
     */
    public VerveDebugBoundsRenderSpatial() {
        super("SelectedSpatials");
        setRenderDelegate(new VerveSelectedRenderDelegate(), null);
        getSceneHints().setRenderBucketType(RenderBucketType.PostBucket);
        updateRenderStates();
    }

    public void updateRenderStates() {
        setupBoundRenderStates(m_wireSphere,  DrawStyle.Wire);
        setupBoundRenderStates(m_wireBox,     DrawStyle.Wire);
        setupBoundRenderStates(m_wireObb,     DrawStyle.Wire);

        setupBoundRenderStates(m_fillSphere,  DrawStyle.Fill);
        setupBoundRenderStates(m_fillBox,     DrawStyle.Fill);
        setupBoundRenderStates(m_fillObb,     DrawStyle.Fill);

        setupBoundRenderStates(m_zbufSphere,  DrawStyle.ZBuf);
        setupBoundRenderStates(m_zbufBox,     DrawStyle.ZBuf);
        setupBoundRenderStates(m_zbufObb,     DrawStyle.ZBuf);
    }

    @Override
    public void draw(Renderer renderer) {
        if (!renderer.isProcessingQueue()) {
            if (renderer.checkAndAdd(this)) {
                return;
            }
        }
        final RenderDelegate delegate = getCurrentRenderDelegate();
        if (delegate == null) {
            renderer.draw((Renderable) this);
        } else {
            delegate.render(this, renderer);
        }
    }


    @Override
    public void updateWorldBound(boolean recurse) {
        // nada
    }


    public class VerveSelectedRenderDelegate implements RenderDelegate {
        @Override
        public void render(Spatial spatial, Renderer renderer) {
            if (VerveDebug.getDebugBoundList().size() > 0) {
                m_backClr = debugColor;
                for(Spatial s : VerveDebug.getDebugBoundList()) {
                    VerveDebug.drawAxis(s, renderer, false, true);
                    drawBounds(s, renderer, false);
                }
            }
            if (VerveDebug.getSelectBoundList().size() > 0) {
                m_backClr = selectColor;
                for(Spatial s : VerveDebug.getSelectBoundList()) {
                    //VerveDebug.drawAxis(s, renderer, false, true);
                    drawBounds(s, renderer, false);
                }
            }
        }
    }

    //-------------------------------------------------------------------------
    /**
     * draws the bounding volume for a given Spatial and optionally its children.
     */
    public void drawBounds(final Spatial se, final Renderer r, boolean doChildren) {
        if (se == null) {
            return;
        }
        if (se.getWorldBound() != null && se.getSceneHints().getCullHint() != CullHint.Always) {
            final Camera cam = Camera.getCurrentCamera();
            final int state = cam.getPlaneState();
            if (cam.contains(se.getWorldBound()) == Camera.FrustumIntersect.Outside) {
                doChildren = false;
            } 
            else {
                final BoundingVolume bound = se.getWorldBound();
                drawBounds(bound, r);
            }
            cam.setPlaneState(state);
        }
        if (doChildren && se instanceof Node) {
            final Node n = (Node) se;
            if (n.getNumberOfChildren() != 0) {
                for (int i = n.getNumberOfChildren(); --i >= 0;) {
                    drawBounds(n.getChild(i), r, true);
                }
            }
        }
    }

    protected void drawBounds(final BoundingVolume bv, final Renderer r) {
        switch (bv.getType()) {
        case AABB:   drawBoundingBox((BoundingBox) bv, r);       break;
        case Sphere: drawBoundingSphere((BoundingSphere) bv, r); break;
        case OBB:    drawObb((OrientedBoundingBox) bv, r);       break;
        }
    }

    enum DrawStyle {
        Fill,
        Wire,
        ZBuf
    }
    
    protected void setupBoundRenderStates(Mesh mesh, DrawStyle drawStyle) {
        //mesh.getSceneHints().setRenderBucketType(RenderBucketType.PostBucket);
        mesh.getSceneHints().setRenderBucketType(RenderBucketType.Skip);
        mesh.getSceneHints().setLightCombineMode(LightCombineMode.Off);

        BlendState blendState = new BlendState();
        blendState.setEnabled(true);
        blendState.setBlendEnabled(true);
        blendState.setSourceFunction(BlendState.SourceFunction.SourceAlpha);

        WireframeState wireframeState = new WireframeState();
        wireframeState.setAntialiased(true);

        CullState cullState = new CullState();
        cullState.setCullFace(CullState.Face.Back);

        OffsetState offsetState = new OffsetState();
        offsetState.setTypeEnabled(OffsetState.OffsetType.Fill, true);
        offsetState.setFactor(-1);
        offsetState.setUnits(4);

        ZBufferState zState = new ZBufferState();
        zState.setWritable(false);
        
        mesh.setRenderState(zState);
        mesh.setRenderState(cullState);
        mesh.setRenderState(blendState);

        switch(drawStyle) {
        case Fill:
            zState.setFunction(ZBufferState.TestFunction.LessThanOrEqualTo);
            mesh.setSolidColor(foreColor); 
            blendState.setDestinationFunction(BlendState.DestinationFunction.One);
            mesh.setRenderState(offsetState);
            break;
        case Wire:
            zState.setFunction(ZBufferState.TestFunction.LessThanOrEqualTo);
            mesh.setSolidColor(lineColor); 
            blendState.setDestinationFunction(BlendState.DestinationFunction.One);
            wireframeState.setLineWidth(1);
            mesh.setRenderState(wireframeState);
            break;
        case ZBuf:
            zState.setFunction(ZBufferState.TestFunction.Always);
            mesh.setSolidColor(m_backClr);
            blendState.setDestinationFunction(BlendState.DestinationFunction.OneMinusSourceAlpha);
            wireframeState.setLineWidth(5);
            mesh.setRenderState(wireframeState);
            break;
        }
       
        mesh.updateWorldRenderStates(false);
    }

    protected void drawBoundingSphere(final Sphere sphere, final BoundingSphere bound, final Renderer r) {
        sphere.setData(bound.getCenter(), Z_SAMPLES, R_SAMPLES, bound.getRadius());
        sphere.updateWorldRenderStates(false);
        sphere.updateGeometricState(0, false);
        sphere.draw(r);
    }
    public void drawBoundingSphere(final BoundingSphere bound, final Renderer r) {
        m_zbufSphere.setSolidColor(m_backClr);
        drawBoundingSphere(m_zbufSphere, bound, r);
        drawBoundingSphere(m_fillSphere, bound, r);
        drawBoundingSphere(m_wireSphere, bound, r);
    }

    public void drawBoundingBox(final Box box, final BoundingBox bound, final Renderer r) {
        box.setData(bound.getCenter(), bound.getXExtent(), bound.getYExtent(), bound.getZExtent());
        box.updateWorldRenderStates(false);
        box.updateGeometricState(0, false);
        box.draw(r);
    }
    public void drawBoundingBox(final BoundingBox bound, final Renderer r) {
        m_zbufBox.setSolidColor(m_backClr);
        drawBoundingBox(m_zbufBox, bound, r);
        drawBoundingBox(m_fillBox, bound, r);
        drawBoundingBox(m_wireBox, bound, r);
    }
    public void drawObb(OrientedBox obb, final OrientedBoundingBox bound, final Renderer r) {
        obb.getCenter().set(bound.getCenter());
        obb.getxAxis().set(bound.getXAxis());
        obb.getYAxis().set(bound.getYAxis());
        obb.getZAxis().set(bound.getZAxis());
        obb.getExtent().set(bound.getExtent());
        obb.computeInformation();
        obb.updateWorldRenderStates(false);
        obb.updateGeometricState(0, false);
        obb.draw(r);
    }
    public void drawObb(final OrientedBoundingBox bound, final Renderer r) {
        m_zbufObb.setSolidColor(m_backClr);
        drawObb(m_zbufObb, bound, r);
        drawObb(m_fillObb, bound, r);
        drawObb(m_wireObb, bound, r);
    }

}
