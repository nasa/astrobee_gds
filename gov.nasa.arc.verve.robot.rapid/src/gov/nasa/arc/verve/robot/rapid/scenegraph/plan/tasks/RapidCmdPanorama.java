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
import gov.nasa.rapid.v2.e4.message.helpers.CommandHelper;
import rapid.Command;

import com.ardor3d.math.Matrix3;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyVector3;

public class RapidCmdPanorama extends Task {
    //private static final Logger logger = Logger.getLogger(RapidCmdPanorama.class);
    protected final Matrix3 m_rot = new Matrix3();
    protected PieWedgeTerrain m_pie = new PieWedgeTerrain("wedge");

    public RapidCmdPanorama(String taskName, Object data, PlanState planState, Object parentKey) {
        super(taskName, data, planState, parentKey);
        m_node.attachChild(m_pie);
        m_pie.setFlipped(false);
        updateTask(data, planState);
    }

    @Override
    public void updateTask(Object data, PlanState planState) {
        m_begXfm.set(planState.getXyzTransform());
        m_endXfm.set(planState.getXyzTransform());
        m_rot.fromAngleNormalAxis(planState.getYaw(), Vector3.UNIT_Z);
        ReadOnlyVector3 xyz = planState.getXyz();
        m_pie.setTranslation(xyz.getX(), xyz.getY(), xyz.getZ()-0.1);
        m_pie.setRotation(m_rot);
        m_pie.setToWorldTransform(planState.getSiteToWorldTransform());
        m_pie.updateVertices();
    }
    
    public static boolean isUnderstood(CommandHelper cmdHelp) {
        final Command cmd = cmdHelp.getCommand();
        if(cmd.subsysName.equals("Panorama")) {
            if(cmd.cmdName.equals("acquire")) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean canUpdate(Object data) {
        if(data instanceof CommandHelper) {
            CommandHelper cmdHelper = (CommandHelper)data;
            return isUnderstood(cmdHelper);
        }
        return false;
    }

}
