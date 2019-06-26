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

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

/**
 * Build a RapidRobot and return RobotNode. 
 * @author mallan
 *
 */
public class RapidAvatarFactory {
    private static final Logger logger = Logger.getLogger(RapidAvatarFactory.class);

    public static final String RAPID_AVATAR_CONTEXT = "rapid";
    public static final String RAPID_AVATAR_BUILDER_ID = "gov.nasa.arc.verve.robot.rapid.RapidAvatarBuilder";

    public static RobotNode buildAvatar(Agent agent) throws IllegalStateException, TelemetryException, IOException {
        RobotNode retVal = null;
        //logger.debug("Attempting to locate avatar builder for "+agent);
        
        IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor(RAPID_AVATAR_BUILDER_ID);
        for (IConfigurationElement e : config) {
            Object obj;
            try {
                obj = e.createExecutableExtension("class");
                if (obj instanceof IRapidAvatarBuilder) {
                    final IRapidAvatarBuilder builder = (IRapidAvatarBuilder)obj;
                    if(builder.canBuild(agent)) {
                        retVal = builder.buildAvatar(agent);
                        if(retVal != null) 
                            break;
                    }
                }
            }
            catch (CoreException e1) {
            	IOException ioe = new IOException("error querying extension point");
            	ioe.initCause(e1);
                throw ioe;
            }
        }

        if(retVal == null) {
            IRapidAvatarBuilder builder = new GenericRapidAvatarBuilder();
            retVal = builder.buildAvatar(agent);
            logger.warn("Could not find an extension to build "+agent+". Creating a generic RAPID agent avatar");
        }

        return retVal;
    }
}
