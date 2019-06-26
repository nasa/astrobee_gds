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
package gov.nasa.arc.verve.robot.scenegraph;

import gov.nasa.arc.verve.robot.AbstractRobot;

import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.scenegraph.event.DirtyEventListener;
import com.ardor3d.scenegraph.event.DirtyType;

public class RobotNode extends Node {
//	private static final Logger logger = Logger.getLogger(RobotNode.class);
	
    final AbstractRobot m_robot;
    
    RobotModelNode m_model;
    Node           m_concepts;
    Node           m_sensors;
    Node           m_base;
    
    public RobotNode(AbstractRobot robot, Node model) {
        super(robot.getName()+AbstractRobot.SEP+AbstractRobot.ROOT_NAME);
        m_robot = robot;
        setRobotModel(model);
    }
    
    public AbstractRobot getRobot() {
        return m_robot;
    }

    /**
     * Set the robot model and create the concept and sensor nodes
     * Note that a DirtyEventListener is attached to concepts in order
     * to block attach and detach events from propagating up
     * @param model
     */
    public void setRobotModel(Node model) {
        this.detachAllChildren();
        m_model = new RobotModelNode(m_robot, model);
        m_concepts = new Node(m_robot.getName()+AbstractRobot.SEP+AbstractRobot.CONCEPTS_ROOT_NAME);
        m_sensors  = new Node(m_robot.getName()+AbstractRobot.SEP+AbstractRobot.SENSORS_ROOT_NAME);
        
        DirtyEventListener del = new DirtyEventListener() {
			public boolean spatialDirty(Spatial spatial, DirtyType dirtyType) {
				switch(dirtyType) {
				case Attached:
				case Detached:
					return true; // prevent attach/detach events from propagating up from concepts
				default:
					return false;
				}
			}

            @SuppressWarnings("unused")
            public boolean spatialClean(Spatial spatial, DirtyType dirtyType) {
                // TODO Auto-generated method stub
                return false;
            }
        };
        m_concepts.setListener(del);
        
        this.attachChild(m_model);
        this.attachChild(m_sensors);
        this.attachChild(m_concepts);
        
//        logger.debug("ModelNode = "+m_model.getName());
//        logger.debug("SensorsNode = "+m_sensors.getName());
//        logger.debug("ConceptsNode = "+m_concepts.getName());
    }
    
    /** change code to use getModelNode() 
     * @deprecated 
     */
    @Deprecated
    public void setBaseNode(Node base) {
        m_base = base;
    }
    
    public Node getConceptsNode() {
        return m_concepts;
    }
    
    public Node getSensorsNode() {
        return m_sensors;
    }
    
    public Node getModelNode() {
        return m_model;
    }
    
    /** change code to use getModelNode() 
     * @deprecated 
     */
    @Deprecated
    public Node getBaseNode() {
        return m_base;
    }
    
}
