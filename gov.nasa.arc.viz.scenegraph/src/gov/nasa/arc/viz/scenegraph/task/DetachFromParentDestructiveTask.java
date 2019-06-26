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
package gov.nasa.arc.viz.scenegraph.task;

import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import com.ardor3d.renderer.state.RenderState;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.Spatial;

/**
 * Remove a spatial from its parent. This task will recurse through all children and
 * detach them from their parents. 
 * Upon completion of the task, System.gc() is called.
 */
public class DetachFromParentDestructiveTask implements Callable<Integer> {
	private static final Logger logger = Logger.getLogger(DetachFromParentDestructiveTask.class);
	
    final Spatial m_branch;
    int m_releaseCount = 0;
    
    public DetachFromParentDestructiveTask(Spatial branch) {
        m_branch = branch;
    }
    
    public Integer call() throws Exception {
        if(m_branch != null) {
            recurse(m_branch);
        }
        m_branch.removeFromParent();

        // request garbage collection
        System.gc();
        logger.debug("removed "+m_releaseCount+" spatials from scenegraph");
        
        return new Integer(m_releaseCount);
    }
    
    protected void recurse(Spatial spatial) {
        if(spatial instanceof Node) {
            Node node = (Node)spatial;
            Spatial[] children = new Spatial[node.getNumberOfChildren()];
            children = node.getChildren().toArray(children);
            for(Spatial child : children) {
                recurse(child);
            }
        }
        spatial.removeFromParent();
        // remove all controllers
        while(spatial.getControllerCount() > 0) {
            spatial.removeController(0);
        }
        // clear all render states
        for(RenderState.StateType stateType : RenderState.StateType.values()) {
            spatial.clearRenderState(stateType);
        }
        m_releaseCount++;
    }
}
