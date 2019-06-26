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
 * Task that detaches all children from a node
 * @see com.ardor3d.util.GameTaskQueueManager
 * @author mallan
 *
 */
public class DetachAllChildrenTask implements Callable<Boolean> {
    final Node m_parent;

    public DetachAllChildrenTask(Node parent) {
        m_parent = parent;
    }
    public Boolean call() throws Exception {
        boolean retVal = false;
        if(m_parent != null) {
            m_parent.detachAllChildren();
            retVal = true;
        }
        return new Boolean(retVal);
    }
}
