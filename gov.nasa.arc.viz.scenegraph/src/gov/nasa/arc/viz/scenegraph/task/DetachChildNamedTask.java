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

/**
 *
 * Task that detaches a child by name from a node
 * @see com.ardor3d.util.GameTaskQueueManager
 * @author mallan
 * @since 2009-07
 *
 */
public class DetachChildNamedTask implements Callable<Integer> {
    final Node m_parent;
    final String m_childName;

    public DetachChildNamedTask(Node parent, String childName) {
        m_parent = parent;
        m_childName = childName;
    }
    
    public Integer call() throws Exception {
        int retVal = -1;
        if(m_parent != null) {
            retVal = m_parent.detachChildNamed(m_childName);
        }
        return new Integer(retVal);
    }
}
