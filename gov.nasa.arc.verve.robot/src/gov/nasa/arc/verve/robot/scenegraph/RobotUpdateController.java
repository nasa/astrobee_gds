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

import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.scenegraph.controller.SpatialController;

public class RobotUpdateController implements SpatialController<Spatial> {
    final AbstractRobot robot;
    public RobotUpdateController(AbstractRobot robot) {
        this.robot = robot;
    }
    public void update(double time, Spatial caller) {
    	long t = System.currentTimeMillis();
        robot.handleFrameUpdate(t);
    }

}
