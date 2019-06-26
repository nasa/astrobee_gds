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

import gov.nasa.arc.viz.scenegraph.visitor.StoredRenderState.RenderStateMap;

import java.util.HashSet;
import java.util.Set;

import com.ardor3d.renderer.state.RenderState;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.scenegraph.visitor.Visitor;

/**
 * Visit a branch of the scenegraph and put specified 
 * render states into a StoredRenderState. Specified
 * RenderStates will be cleared on visited spatials.
 * The state types to restore can be specified. If left
 * unspecified, all state types from the StoredRenderState
 * will be restored. 
 */
public class RestoreRenderStateVisitor implements Visitor {
    
    public final StoredRenderState    m_store;
    public Set<RenderState.StateType> m_stateTypes = null;
    
    /**
     * 
     */
    public RestoreRenderStateVisitor(StoredRenderState store) {
        m_store = store;
    }
    
    public void setRestoreTypes(RenderState.StateType... states) {
        m_stateTypes = new HashSet<RenderState.StateType>();
        for(RenderState.StateType state : states) {
            m_stateTypes.add(state);
        }
    }
    
    public void setRestoreTypes(Set<RenderState.StateType> states) {
        m_stateTypes = states;
    }
    
    public Set<RenderState.StateType> getRestoreTypes() {
        return m_stateTypes;
    }
    
    public StoredRenderState getStoredRenderState() {
        return m_store;
    }
    
    public void visit(final Spatial spatial) {
        Set<RenderState.StateType> stateTypes = m_stateTypes;
        if(stateTypes == null) {
            stateTypes = m_store.getStateTypes();
        }
        for(RenderState.StateType stateType : m_store.getStateTypes()) {
            RenderStateMap map = m_store.getStateMap(stateType);
            RenderState rs = map.get(spatial);
            if(rs != null) {
                spatial.setRenderState(rs);
            }
        }
    }
}
