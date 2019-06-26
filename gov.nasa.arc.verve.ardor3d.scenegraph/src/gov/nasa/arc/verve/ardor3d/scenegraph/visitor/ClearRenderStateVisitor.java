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
package gov.nasa.arc.verve.ardor3d.scenegraph.visitor;

import com.ardor3d.renderer.state.RenderState.StateType;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.scenegraph.visitor.Visitor;

/**
 * clear render state on subgraph
 */
public class ClearRenderStateVisitor implements Visitor {
    StateType[] m_stateTypes;

    public ClearRenderStateVisitor() {
        m_stateTypes = StateType.values();
    }

    public ClearRenderStateVisitor(StateType stateType) {
        m_stateTypes = new StateType[] { stateType };
    }

    public ClearRenderStateVisitor(StateType[] stateTypes) {
        if(stateTypes == null) {
            m_stateTypes = StateType.values();
        }
        else {
            m_stateTypes = stateTypes;
        }
    }

    @Override
    public void visit(final Spatial spatial) {
        for(StateType stateType : m_stateTypes) {
            spatial.clearRenderState(stateType);
        }
    }
}
