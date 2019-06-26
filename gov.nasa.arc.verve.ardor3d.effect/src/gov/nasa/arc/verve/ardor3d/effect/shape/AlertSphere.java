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
package gov.nasa.arc.verve.ardor3d.effect.shape;

import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.type.ReadOnlyColorRGBA;
import com.ardor3d.renderer.queue.RenderBucketType;
import com.ardor3d.renderer.state.BlendState;
import com.ardor3d.renderer.state.BlendState.DestinationFunction;
import com.ardor3d.renderer.state.MaterialState;
import com.ardor3d.renderer.state.ZBufferState;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.scenegraph.controller.SpatialController;
import com.ardor3d.scenegraph.hint.LightCombineMode;
import com.ardor3d.scenegraph.shape.Sphere;

/**
 * 
 * @author mallan
 */
public class AlertSphere extends Node {
    protected final Spatial         m_anchor;
    protected Sphere[]              m_spheres    = new Sphere[3];
    protected AlertSphereController m_controller;
    protected float                 m_baseAlpha  = 0.7f;
    protected boolean               m_reverse = false;
    /**
     * 
     * @param name name of spatial
     * @param anchor where the alert should be centered
     * @param size maximum size of transparent pulse
     * @param markerRadius average radius of the small marker sphere
     * @param color color of alert
     * @param period period of time for a single pulse
     * @param offset time offset of secondary pulse
     */
    public AlertSphere(String name, Spatial anchor, double size, double markerRadius, 
                       ReadOnlyColorRGBA color, double period, double offset, boolean reverse) {
        super(name);
        m_reverse = reverse;
        m_anchor = anchor;
        BlendState bs = new BlendState();
        bs.setEnabled(true);
        bs.setBlendEnabled(true);
        bs.setDestinationFunction(DestinationFunction.One);
        this.setRenderState(bs);
        
        ZBufferState zs = new ZBufferState();
        zs.setWritable(false);
        this.setRenderState(zs);
        
        for(int i = 0; i < m_spheres.length; i++) {
            m_spheres[i] = new Sphere(name+"-"+i, 10, 20, 0.5);
        }
        m_controller = new AlertSphereController(size, markerRadius, period, offset, color, m_spheres);
        for(int i = 0; i < 2; i++) {
            m_spheres[i].getSceneHints().setRenderBucketType(RenderBucketType.Transparent);
            //m_spheres[i].addController(m_controller);
            this.attachChild(m_spheres[i]);
        }
        
        m_spheres[2].setScale(markerRadius*0.5);
        m_spheres[2].getSceneHints().setLightCombineMode(LightCombineMode.Off);            
        m_spheres[2].getSceneHints().setRenderBucketType(RenderBucketType.Transparent);
        ColorRGBA clr = new ColorRGBA(color);
        clr.setAlpha(0.75f);
        m_spheres[2].setDefaultColor(clr);
        bs = new BlendState();
        bs.setBlendEnabled(true);
        bs.setDestinationFunction(DestinationFunction.OneMinusSourceAlpha);
        m_spheres[2].setRenderState(bs);
        m_spheres[2].setRenderState(new ZBufferState());
        this.attachChild(m_spheres[2]);
        
        this.addController(m_controller);
    }
    
    /**
     * if anchor is a Node, attach to that. Otherwise, attach to parent 
     * of the anchor (this is obviously not optimal)
     */
    public void attachToAnchor() {
        if(m_anchor instanceof Node) {
            Node node = (Node)m_anchor;
            node.attachChild(this);
        }
        else {       
            m_anchor.getParent().attachChild(this);
        }
    }
    
    public class AlertSphereController implements SpatialController {
        double          elapsed = 0;
        final double    size;
        final double    period;
        final double    offset;
        final double    markerRadius;
        final Mesh      one;
        final Mesh      two;
        final Mesh      marker;
        MaterialState   oneMs = new MaterialState();
        MaterialState   twoMs = new MaterialState();
        final ColorRGBA color;

        public AlertSphereController(double size, double markerRadius, 
                                     double period, double offset, 
                                     ReadOnlyColorRGBA color, Mesh[] meshes) {
            this.size   = size;
            this.period = period;
            this.offset = offset;
            this.markerRadius = markerRadius;
            this.one    = meshes[0];
            this.two    = meshes[1];
            this.color  = new ColorRGBA(ColorRGBA.BLACK); //color);
            MaterialState[] mss = new MaterialState[] { oneMs, twoMs };
            for(MaterialState ms : mss) {
                ms.setAmbient (ColorRGBA.BLACK);
                ms.setDiffuse (color);
                ms.setSpecular(color);
                ms.setEmissive(color.multiply(0.5f, new ColorRGBA()));
                ms.setShininess(2);
            }
            one.setRenderState(oneMs);
            two.setRenderState(twoMs);
            
            this.marker = meshes[2];
        }

        @Override
        public void update(double time, Spatial caller) {
            elapsed += time;
            double mod;
            double scale;
            {
                mod = (elapsed-offset) % period;
                if(mod < 0)
                    scale = 0.01;
                else
                    scale = m_reverse ? 1-mod/period : mod/period;
                color.setAlpha(m_baseAlpha * (1f-(float)scale));
                one.setScale(0.01+scale*size);
                oneMs.setDiffuse(color);
            }
            { 
                mod = (elapsed) % period;
                scale = m_reverse ? 1-mod/period : mod/period;
                color.setAlpha(m_baseAlpha * (1f-(float)scale));
                two.setScale(0.01+scale*size);
                two.setDefaultColor(color);                
                twoMs.setDiffuse(color);
            }
            {
                final double pulse = (elapsed*2*Math.PI)/period;
                final double a = 0.5*markerRadius* (m_reverse ? Math.cos(pulse) : Math.sin(pulse));
                marker.setScale(0.5 * (markerRadius + a));
            }
        }
    }
}
