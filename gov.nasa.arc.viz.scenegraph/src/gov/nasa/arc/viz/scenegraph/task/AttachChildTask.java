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
 * Task that attaches a child spatial to a
 * node.
 * @see com.ardor3d.util.GameTaskQueueManager
 * @author mallan
 *
 */
public class AttachChildTask implements Callable<Integer> {
    final Node m_root;
    final Spatial m_child;
    final int m_index;
    
    public AttachChildTask(Node root, Spatial child) { 
        m_root = root;
        m_child = child;
        m_index = -1;
    }
    
    public AttachChildTask(Node root, Spatial child, int index ) { 
        m_root = root;
        m_child = child;
        m_index = index;
    }
    
   /**
    * @return the number of children maintained by parent node
    */
    public Integer call() throws Exception {
        int retVal = -1;
        if(m_root != null && m_child != null) {
            if(m_index < 0){
                retVal = m_root.attachChild(m_child);
            } else {
                retVal = m_root.attachChildAt(m_child, m_index);
            }
        }
        return new Integer(retVal);
    }
}
