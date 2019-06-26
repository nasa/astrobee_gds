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
package gov.nasa.arc.verve.robot.rapid;

import gov.nasa.arc.verve.robot.exception.TelemetryException;
import gov.nasa.arc.verve.robot.scenegraph.RobotNode;
import gov.nasa.rapid.v2.e4.agent.Agent;

import java.io.IOException;

public abstract class AbstractRapidAvatarBuilder implements IRapidAvatarBuilder {

    public abstract boolean canBuild(Agent agent);
    public abstract RobotNode buildAvatar(Agent agent) throws IllegalStateException, TelemetryException, IOException;
    
    public RobotNode buildAvatar(String robotName, String context) throws IllegalStateException, TelemetryException, IOException {
        if(!context.equals(RapidAvatarFactory.RAPID_AVATAR_CONTEXT))
            throw new IllegalStateException("Cannot build a \""+context+"\" robot, can only build \""+RapidAvatarFactory.RAPID_AVATAR_CONTEXT+"\" robots");
        Agent agent = Agent.valueOf(robotName);
        if(agent == null) 
            throw new IllegalStateException("Could not map \""+robotName+"\" to RAPID Agent");
        
        return buildAvatar(agent);
    }

    public boolean canBuild(String robotName, String context) {
        if(context.equals(RapidAvatarFactory.RAPID_AVATAR_CONTEXT)) {
            Agent agent = Agent.valueOf(robotName);
            if(agent != null) {
                return canBuild(agent);
            }
        }
        return false;
    }


}
