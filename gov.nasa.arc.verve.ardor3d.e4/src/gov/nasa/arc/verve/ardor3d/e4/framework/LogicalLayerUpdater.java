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
package gov.nasa.arc.verve.ardor3d.e4.framework;

import java.util.ArrayList;
import java.util.List;

import com.ardor3d.framework.Updater;
import com.ardor3d.input.logical.InputTrigger;
import com.ardor3d.input.logical.LogicalLayer;
import com.ardor3d.util.ReadOnlyTimer;

public class LogicalLayerUpdater implements Updater {
	private final List<LogicalLayer> m_logicalLayers = new ArrayList<LogicalLayer>();
	private final List<InputTrigger> m_globalTriggers = new ArrayList<InputTrigger>();
	
	public LogicalLayerUpdater() {
	    // empty
	}
	
	public synchronized void registerGlobalTrigger(InputTrigger inputTrigger) {
        for(LogicalLayer logicalLayer : m_logicalLayers) {
            logicalLayer.registerTrigger(inputTrigger);
        }
        m_globalTriggers.add(inputTrigger);
	}
	
	public synchronized void deregisterGlobalTrigger(InputTrigger inputTrigger) {
        for(LogicalLayer logicalLayer : m_logicalLayers) {
            logicalLayer.deregisterTrigger(inputTrigger);
        }
        m_globalTriggers.remove(inputTrigger);
	}
	
	public synchronized boolean registerLogicalLayer(LogicalLayer logicalLayer) {
	    for(InputTrigger globalTrigger : m_globalTriggers) {
	        logicalLayer.registerTrigger(globalTrigger);
	    }
		return m_logicalLayers.add(logicalLayer);
	}
	
	public synchronized boolean removeLogicalLayer(LogicalLayer logicalLayer) {
       for(InputTrigger globalTrigger : m_globalTriggers) {
            logicalLayer.deregisterTrigger(globalTrigger);
        }
		return m_logicalLayers.remove(logicalLayer);
	}
	
    @Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

    @Override
	public synchronized void update(ReadOnlyTimer timer) {
        final double tpf = timer.getTimePerFrame();
		for(LogicalLayer logicalLayer : m_logicalLayers) {
			logicalLayer.checkTriggers(tpf);
		}
	}

}
