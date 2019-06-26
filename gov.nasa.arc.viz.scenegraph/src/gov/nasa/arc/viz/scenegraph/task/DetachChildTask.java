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

import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.Spatial;

/**
 *
 * Task that detaches a child from a node
 * @see com.ardor3d.util.GameTaskQueueManager
 * @author mallan
 * @since 2009-07
 *
 */
public class DetachChildTask implements Callable<Integer> {
    final Node m_parent;
    final Spatial m_child;
	final int m_index;

    public DetachChildTask(Node parent, Spatial child) {
        m_parent = parent;
        m_child = child;
        m_index = -1;
    }
    
    public DetachChildTask(Node parent, int index) {
        m_parent = parent;
        m_index = index;
        m_child = null;
    }
    
    
    public Integer call() throws Exception {
        int retVal = -1;
        if(m_parent != null) {
        	if (m_index >= 0){
        		Spatial found = m_parent.detachChildAt(m_index);
        		if (found != null){
        			retVal = m_index;
        		}
        	} else {
        		retVal = m_parent.detachChild(m_child);
        	}
        }
        return new Integer(retVal);
    }
    
    public Spatial getChild() {
		return m_child;
	}
}
