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
package gov.nasa.arc.verve.robot.rapid.scenegraph.plan.tasks;

import gov.nasa.arc.verve.robot.parts.concepts.plan.PlanState;
import gov.nasa.arc.verve.robot.parts.concepts.plan.Task;

public class RapidCmdDummy extends Task {
    //private static final Logger logger = Logger.getLogger(RapidCmdDummy.class);

    public RapidCmdDummy(String taskName, Object data, PlanState planState, Object parentKey) {
        super(taskName, data, planState, parentKey);
        updateTask(data, planState);
    }

    @Override
    public void updateTask(Object data, PlanState planState) {
        m_begXfm.set(planState.getXyzTransform());
        m_endXfm.set(planState.getXyzTransform());
        
        m_node.setTranslation(planState.getXyzTransform().getTranslation());

        //CommandHelper cmdHelper = (CommandHelper)data;
        //Command cmd = cmdHelper.getCommand();
        //ParameterList pl = cmdHelper.getParameters();
    }

    @Override
    public boolean canUpdate(Object data) {
        return true;
    }

}
