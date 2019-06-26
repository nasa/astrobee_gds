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
package gov.nasa.arc.viz.scenegraph.visitor;

import com.ardor3d.renderer.state.MaterialState;
import com.ardor3d.renderer.state.RenderState.StateType;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.scenegraph.visitor.Visitor;

/**
 * Brute force mark dirty on entire subgraph
 */
public class SetMaterialVisitor implements Visitor {
    final protected MaterialState m_material = new MaterialState();
    
    /**
     * 
     */
    public SetMaterialVisitor(MaterialState material) {
        setMaterial(m_material, material);
    }
    
    public void visit(final Spatial spatial) {
        MaterialState ms = (MaterialState)spatial.getLocalRenderState(StateType.Material);
        if(ms != null) {
            setMaterial(ms, m_material);
        }
    }
    
    void setMaterial(MaterialState dst, MaterialState src) {
        dst.setAmbient(src.getAmbient());
        dst.setDiffuse(src.getDiffuse());
        dst.setSpecular(src.getSpecular());
        dst.setEmissive(src.getEmissive());
        dst.setColorMaterial(src.getColorMaterial());
        dst.setEnabled(src.isEnabled());
        dst.setShininess(src.getShininess());
    }
}
