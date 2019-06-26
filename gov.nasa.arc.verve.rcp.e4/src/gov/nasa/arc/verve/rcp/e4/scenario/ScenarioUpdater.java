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
package gov.nasa.arc.verve.rcp.e4.scenario;

import gov.nasa.arc.verve.common.VerveTask;

import com.ardor3d.annotation.MainThread;
import com.ardor3d.framework.Updater;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.util.ReadOnlyTimer;

public class ScenarioUpdater implements Updater {
	Node m_root;
	
    /**
     * Create, register and initialize Updater
     */
    public ScenarioUpdater(Node root) {
    	if(root == null) 
    		throw new NullPointerException("root node cannot be null.");
    	m_root = root;
        VerveTask.getQueue().setExecutionTime(30);
    }
    
    @Override
    @MainThread
    public void init() {
        //
    }

    @Override
    @MainThread
    public void update(ReadOnlyTimer timer) {
        // Execute updateQueue item
        VerveTask.getQueue().execute();
        m_root.updateGeometricState(timer.getTimePerFrame(), true);
    }
}
