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

import java.util.HashMap;
import java.util.Set;

import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.type.ReadOnlyColorRGBA;
import com.ardor3d.renderer.queue.RenderBucketType;
import com.ardor3d.renderer.state.BlendState;
import com.ardor3d.renderer.state.MaterialState;
import com.ardor3d.renderer.state.MaterialState.ColorMaterial;
import com.ardor3d.renderer.state.RenderState.StateType;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.scenegraph.hint.TransparencyType;
import com.ardor3d.scenegraph.visitor.Visitor;

/**
 * set the alpha value of all materials 
 */
public class SetAlphaVisitor implements Visitor {
    private final ColorRGBA   m_tempClr = new ColorRGBA();
    private ReadOnlyColorRGBA m_emisClr = null;
    private float             m_alpha   = 0.5f;
    
    private boolean           m_doSaveMaterials = true;
    private final HashMap<Spatial,Save> m_savedMaterials = new HashMap<Spatial,Save>();

    private RenderBucketType  m_alphaBucket = RenderBucketType.Transparent;
    
    public class Save {
        public MaterialState    material = null;
        public BlendState       blend    = null;
        public RenderBucketType bucket   = null;
        public TransparencyType trans    = null;
    }
    
    public SetAlphaVisitor(float alpha) {
        setAlpha(alpha);
    }   
    public SetAlphaVisitor() {
        // foo
    }

    public void setAlpha(float alpha) {
        m_alpha = alpha;
    }
    public float getAlpha() {
        return m_alpha;
    }
    
    /**
     * Set an emissive color override. May be null
     * @param color
     */
    public void setEmissiveColor(ReadOnlyColorRGBA color) {
        m_emisClr = color;
    }
    public ReadOnlyColorRGBA getEmissiveColor() {
        return m_emisClr;
    }
    
    public void setAlphaRenderBucket(RenderBucketType bucket) {
        m_alphaBucket = bucket;
    }
    
    /**
     * if doSave is set to true, the visitor will store the 
     * material state of the visited nodes that it changes so the
     * modifications can be undone by restoreSavedMaterials()
     * @param doSave
     */
    public void setSaveMaterials(boolean doSave) {
        m_doSaveMaterials = doSave;
    }
    public boolean isSaveMaterials() {
        return m_doSaveMaterials;
    }

    public void visit(final Spatial spatial) {
        Save save = null;
        if(m_doSaveMaterials) {
            save = new Save();
        }
        
        MaterialState material = (MaterialState)spatial.getLocalRenderState(StateType.Material);
        if(material != null) {
            MaterialState newMaterial = cloneMaterial(material);
            m_tempClr.set(newMaterial.getDiffuse());
            m_tempClr.setAlpha(m_alpha);
            newMaterial.setDiffuse(m_tempClr);
            if(m_emisClr != null) {
                newMaterial.setEmissive(m_emisClr);
            }
            spatial.setRenderState(newMaterial);
            if(save != null) {
                save.material = material;
                save.bucket   = spatial.getSceneHints().getRenderBucketType();
                save.trans    = spatial.getSceneHints().getTransparencyType();
            }
            if(m_alpha < 0.95) {
                spatial.getSceneHints().setRenderBucketType(m_alphaBucket);
                spatial.getSceneHints().setTransparencyType(TransparencyType.OnePass);
            }
            else {
                spatial.getSceneHints().setRenderBucketType(RenderBucketType.Opaque);

            }
        }
        
        if(save != null) {
            save.blend    = (BlendState)spatial.getLocalRenderState(StateType.Blend);
            
            if( !(save.blend == null && save.material == null) ) {
                m_savedMaterials.put(spatial, save);
            }
        }
        
        spatial.clearRenderState(StateType.Blend);
    }
    
    /**
     * clone a material
     */
    MaterialState cloneMaterial(MaterialState in) {
        MaterialState retVal = new MaterialState();
        retVal.setAmbient  (in.getAmbient()  );
        retVal.setDiffuse  (in.getDiffuse()  );
        retVal.setEmissive (in.getEmissive() );
        retVal.setSpecular (in.getSpecular() );
        retVal.setShininess(in.getShininess());
        retVal.setEnabled  (in.isEnabled()   );
        //retVal.setColorMaterial(in.getColorMaterial());
        retVal.setColorMaterial(ColorMaterial.None);
        return retVal;
    }
    
    /**
     * clear the saved materials without restoring them
     */
    public void clearSavedMaterials() {
        m_savedMaterials.clear();
    }
    
    /**
     * restore saved materials to spatials
     */
    public void restoreSavedMaterials() {
        Set<Spatial> keys = m_savedMaterials.keySet();
        for( Spatial spatial : keys ) {
            if(spatial != null) {
                Save saved = m_savedMaterials.get(spatial);
                if(saved != null) {
                    if(saved.material != null)
                        spatial.setRenderState(saved.material);
                    if(saved.blend != null)
                        spatial.setRenderState(saved.blend);
                    if(saved.bucket != null)
                        spatial.getSceneHints().setRenderBucketType(saved.bucket);
                    if(saved.trans != null)
                        spatial.getSceneHints().setTransparencyType(saved.trans);
                }
            }
        }
        m_savedMaterials.clear();
    }
}

