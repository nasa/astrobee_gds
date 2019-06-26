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

import gov.nasa.arc.verve.common.interest.InterestPointListener;
import gov.nasa.arc.verve.common.interest.InterestPointProvider;
import gov.nasa.arc.verve.robot.AbstractRobot;

import com.ardor3d.scenegraph.Node;

/**
 * Node under which the 'physical' robot model resides
 * Delegates InterestPointProvider calls to AbstractRobot
 *
 */
public class RobotModelNode extends Node implements InterestPointProvider {
    final AbstractRobot m_robot;
    
    RobotModelNode(AbstractRobot robot, Node model) {
        super(robot.getName()+AbstractRobot.SEP+AbstractRobot.MODEL_ROOT_NAME);
        m_robot = robot;
        model.removeFromParent();
        this.attachChild(model);
    }

    @Override
    public void addInterestPointListener(InterestPointListener listener,
            String mode) {
        m_robot.addInterestPointListener(listener, mode);
    }

    @Override
    public String getInterestPointName() {
        return m_robot.getInterestPointName();
    }

    @Override
    public void removeInterestPointListener(InterestPointListener listener) {
        m_robot.removeInterestPointListener(listener);
    }

    @Override
    public String[] getInterestPointModes() {
        return m_robot.getInterestPointModes();
    }

    @Override
    public void updateInterestPointListeners() {
        m_robot.updateInterestPointListeners();
    }

    @Override
    public boolean isInterestPointEnabled() {
        return m_robot.isInterestPointEnabled();
    }
    
	public int compareTo(InterestPointProvider arg0) {
		return getInterestPointName().compareTo(arg0.getInterestPointName());
	}

}
