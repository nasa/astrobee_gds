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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.ardor3d.renderer.state.RenderState;
import com.ardor3d.renderer.state.RenderState.StateType;
import com.ardor3d.scenegraph.Spatial;

public class StoredRenderState {
    public class RenderStateMap extends HashMap<Spatial,RenderState> {
        //
    }
    public final HashMap<StateType,RenderStateMap> m_stateMaps = new HashMap<StateType,RenderStateMap>();
    public final HashSet<StateType> m_stateTypes = new HashSet<StateType>();
    
    public StoredRenderState(StateType... stateTypes) {
        setStateTypes(stateTypes);
    }
    
    public StoredRenderState(StoredRenderState toCopy) {
        setStateTypes(toCopy.getStateTypes());
        for(StateType stateType : m_stateTypes) {
            RenderStateMap mapToCopy = toCopy.getStateMap(stateType);
            RenderStateMap mapLocal = getStateMap(stateType);
            mapLocal.putAll(mapToCopy);
        }
    }
    
    /**
     * clears any existing stored state and sets types that can be stored
     * @param stateTypes
     */
    public void setStateTypes(StateType... stateTypes) {
        m_stateMaps.clear();
        m_stateTypes.clear();
        for(StateType stateType : stateTypes) {
            m_stateTypes.add(stateType);
            m_stateMaps.put(stateType, new RenderStateMap());
        }
    }
    
    public void setStateTypes(Collection<StateType> stateTypes) {
        m_stateMaps.clear();
        m_stateTypes.clear();
        for(StateType stateType : stateTypes) {
            m_stateTypes.add(stateType);
            m_stateMaps.put(stateType, new RenderStateMap());
        }
    }
    
    public Set<StateType> getStateTypes() {
        return m_stateTypes;
    }
    
    public RenderStateMap getStateMap(RenderState.StateType stateType) {
        return m_stateMaps.get(stateType);
    }
    
}
