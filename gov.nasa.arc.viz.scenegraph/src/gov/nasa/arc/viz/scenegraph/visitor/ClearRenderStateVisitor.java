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

import java.util.Collection;
import java.util.HashSet;

import com.ardor3d.renderer.state.RenderState;
import com.ardor3d.renderer.state.RenderState.StateType;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.scenegraph.visitor.Visitor;

/**
 * Visit a branch of the scenegraph and clear specified render states
 */
public class ClearRenderStateVisitor implements Visitor {
    public final HashSet<StateType> m_stateTypes = new HashSet<StateType>();

    /**
     * 
     */
    public ClearRenderStateVisitor() {
    }

    /**
     * 
     */
    public ClearRenderStateVisitor(StateType... stateTypes) {
        setStateTypes(stateTypes);
    }

    public ClearRenderStateVisitor(Collection<StateType> stateTypes) {
        m_stateTypes.addAll(stateTypes);
    }

    public void setStateTypes(StateType... stateTypes) {
        m_stateTypes.clear();
        for(StateType stateType : stateTypes) {
            m_stateTypes.add(stateType);
        }
    }
    
    public void visit(final Spatial spatial) {
        for(RenderState.StateType stateType : m_stateTypes) {
            RenderState rs = spatial.getLocalRenderState(stateType);
            if(rs != null) {
                spatial.clearRenderState(stateType);
            }
        }
    }
}
