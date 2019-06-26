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
package gov.nasa.arc.verve.robot.rapid.parts;

import gov.nasa.arc.verve.robot.parts.IRobotPart;
import gov.nasa.arc.verve.robot.parts.RobotPartBase;
import gov.nasa.arc.verve.robot.parts.concepts.RobotPartPoseHistory;
import gov.nasa.arc.verve.robot.parts.concepts.RobotPartZTester;
import gov.nasa.arc.verve.robot.parts.tools.RobotPartAxes;
import gov.nasa.arc.verve.robot.parts.tools.RobotPartRadialGrid;
import gov.nasa.arc.verve.robot.rapid.RapidRobot;
import gov.nasa.arc.verve.robot.rapid.parts.concepts.RapidRobotPartFrames;

public class RapidRobotPartFactory implements IRobotPartFactory {

    public IRobotPart createPart(String id, RapidRobot robot) {
        String pp = robot.getGlobalParticipant();

        if(id == null || id.length() < 1) { return null; }
        else if(id.equals(RapidRobot.BASE))         return new RobotPartBase(id, robot);
        else if(id.equals(RapidRobot.AXES))         return new RobotPartAxes(id, robot);
        else if(id.equals(RapidRobot.POSE_HISTORY)) return new RobotPartPoseHistory(id, robot);
        else if(id.equals(RapidRobot.FRAMES))       return new RapidRobotPartFrames(id, robot, pp);
        else if(id.equals(RapidRobot.RADIAL_GRID))  return new RobotPartRadialGrid (id, robot, 1, 5);
        
        else if(id.equals(RapidRobot.Z_TESTER))     return new RobotPartZTester(id, robot, 0);
        
        return null;
    }
}
