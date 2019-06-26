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

import java.util.HashSet;
import java.util.Set;

import com.ardor3d.renderer.state.RenderState;
import com.ardor3d.renderer.state.RenderState.StateType;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.scenegraph.visitor.Visitor;

/**
 * Visit a branch of the scenegraph and put specified 
 * render states into a StoredRenderState. Specified
 * RenderStates will be cleared on visited spatials.
 */
public class StoreRenderStateVisitor implements Visitor {
    //private final Logger logger = Logger.getLogger(StoreRenderStateVisitor.class);

    protected final Set<String>  m_ignoreNames = new HashSet<String>();
    protected final Set<Spatial> m_ignoreSpatials = new HashSet<Spatial>();

    public final StoredRenderState m_store;
    public boolean m_clear;

    /**
     * 
     */
    public StoreRenderStateVisitor(StoredRenderState store, boolean doClear) {
        m_store = store;
        m_clear = doClear;
    }

    /**
     * will clear states
     * @param stateTypes
     */
    public StoreRenderStateVisitor(StateType... stateTypes) {
        this(new StoredRenderState(stateTypes), true);
    }

    /**
     * Set names for spatials to ignore. If named spatial is a node, it's children will 
     * be ignored <i>if the visitor is preexecuted</i>
     * @param ignores
     */
    public void setIgnoreSpatials(String... ignores) {
        m_ignoreSpatials.clear();
        m_ignoreNames.clear();
        for(String ignore : ignores) {
            m_ignoreNames.add(ignore);
        }
    }

    public StoredRenderState getStoredRenderState() {
        return m_store;
    }

    public boolean isDoClear() {
        return m_clear;
    }

    public void setDoClear(boolean doClear) {
        m_clear = doClear;
    }

    public void visit(final Spatial spatial) {
        if(m_ignoreNames.contains(spatial.getName())) {
            m_ignoreSpatials.add(spatial);
        }
        if(m_ignoreSpatials.contains(spatial)) {
            if(spatial instanceof Node) {
                // add children of ignored spatial
                Node node = (Node)spatial;
                for(Spatial child : node.getChildren()) {
                    m_ignoreSpatials.add(child);
                }
            }
        }
        else {
            for(RenderState.StateType stateType : m_store.getStateTypes()) {
                RenderState rs = spatial.getLocalRenderState(stateType);
                if(rs != null) {
                    m_store.getStateMap(stateType).put(spatial, rs);
                    if(m_clear) {
                        spatial.clearRenderState(stateType);
                    }
                }
            }
        }
    }
}
