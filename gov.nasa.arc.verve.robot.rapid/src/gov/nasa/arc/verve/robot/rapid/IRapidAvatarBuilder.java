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

import gov.nasa.arc.verve.robot.IRobotAvatarBuilder;
import gov.nasa.arc.verve.robot.exception.TelemetryException;
import gov.nasa.arc.verve.robot.scenegraph.RobotNode;
import gov.nasa.rapid.v2.e4.agent.Agent;

import java.io.IOException;

public interface IRapidAvatarBuilder extends IRobotAvatarBuilder {
	boolean    canBuild(Agent agent);
	RobotNode  buildAvatar(Agent agent) throws TelemetryException, IllegalStateException, IOException;
}
