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

import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.scenegraph.event.DirtyType;
import com.ardor3d.scenegraph.visitor.Visitor;

/**
 * Brute force mark dirty on entire subgraph
 */
public class ForceDirtyVisitor implements Visitor {
    final protected DirtyType[] m_dirtyTypes;
    
    /**
     * 
     * @param dirtyTypes array of dirty types to mark. If null, all dirty types are marked. 
     */
    public ForceDirtyVisitor(DirtyType[] dirtyTypes) {
        if(dirtyTypes == null) {
            m_dirtyTypes = DirtyType.values();
        }
        else {
            m_dirtyTypes = dirtyTypes;
        }
    }
    
    public void visit(final Spatial spatial) {
        for(DirtyType dirtyType : m_dirtyTypes) {
            spatial.markDirty(dirtyType);
        }
    }
}
