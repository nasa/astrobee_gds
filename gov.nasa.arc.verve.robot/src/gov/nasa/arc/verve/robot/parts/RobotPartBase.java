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
package gov.nasa.arc.verve.robot.parts;

import gov.nasa.arc.verve.robot.AbstractRobot;

import com.ardor3d.math.Transform;
import com.ardor3d.scenegraph.Node;

public class RobotPartBase extends AbstractRobotPart {
	
    public RobotPartBase(String partId, AbstractRobot parent) {
        super(partId, parent);
    }

    @Override
    public void attachToNodesIn(Node model) throws IllegalStateException {
    	m_node = getRobot().getRobotNode().getModelNode();
    }

    @Override
    public void handleFrameUpdate(long currentTime) {
        if(m_node != null) {
            m_node.setTransform(getRobot().getPoseProvider().getTransform());
        }
    }

    @Override 
    public void connectTelemetry() {
        // unneeded; we listen to telemetry provider
    }
    
    @Override 
    public void disconnectTelemetry() {
        // unneeded; we listen to telemetry provider
    }
    
    @Override
    public void reset() {
        m_node.setTransform(Transform.IDENTITY);
    }

}
