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
package gov.nasa.arc.verve.robot.rapid.scenegraph.plan;

import gov.nasa.arc.verve.robot.parts.concepts.plan.AbstractPlanOverview.Verbosity;
import gov.nasa.arc.verve.robot.parts.concepts.plan.IPlanTaskFactory;
import gov.nasa.arc.verve.robot.parts.concepts.plan.PlanState;
import gov.nasa.arc.verve.robot.parts.concepts.plan.Task;
import gov.nasa.arc.verve.robot.rapid.scenegraph.plan.tasks.RapidCmdDummy;
import gov.nasa.arc.verve.robot.rapid.scenegraph.plan.tasks.RapidCmdPanorama;
import gov.nasa.arc.verve.robot.rapid.scenegraph.plan.tasks.RapidCmdWaypoint;
import gov.nasa.rapid.v2.e4.message.helpers.CommandHelper;
import rapid.Command;

public class RapidCmdTaskFactory implements IPlanTaskFactory{

    @Override
    public Task createTask(Object data, PlanState state, Verbosity verbosity, Object parentKey) {
        final CommandHelper cmdHelper = (CommandHelper)data;
        final Command cmd = cmdHelper.getCommand();
        String name = cmd.subsysName+"::"+cmd.cmdName;

        if(RapidCmdWaypoint.isUnderstood(cmdHelper)) {
            return new RapidCmdWaypoint(name, cmdHelper, state, parentKey);
        }
        if(RapidCmdPanorama.isUnderstood(cmdHelper)) {
            return new RapidCmdPanorama(name, cmdHelper, state, parentKey);
        }
        //return new RapidCmdWaypoint(name, cmdHelper, state, parentKey);
        return new RapidCmdDummy(name, cmdHelper, state, parentKey);
    }

}
