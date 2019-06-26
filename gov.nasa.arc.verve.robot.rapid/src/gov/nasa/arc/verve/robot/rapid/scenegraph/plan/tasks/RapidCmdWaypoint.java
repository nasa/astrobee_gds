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

import gov.nasa.arc.verve.common.VerveBaseMap;
import gov.nasa.arc.verve.robot.parts.concepts.plan.PlanState;
import gov.nasa.arc.verve.robot.scenegraph.plan.AbstractPlanTaskWaypoint;
import gov.nasa.rapid.v2.e4.message.helpers.CommandHelper;
import gov.nasa.rapid.v2.e4.message.helpers.ParameterList;

import org.apache.log4j.Logger;

import rapid.Command;
import rapid.MOBILITY_METHOD_MOVE;
import rapid.MOBILITY_METHOD_MOVE_PARAM_END_LOCATION;
import rapid.MOBILITY_METHOD_MOVE_PARAM_END_LOCATION_TOLERANCE;
import rapid.ext.arc.MOTION_METHOD_DRIVEARC;
import rapid.ext.arc.MOTION_METHOD_DRIVEARC_PARAM_CRABDIRECTION;
import rapid.ext.arc.MOTION_METHOD_DRIVEARC_PARAM_DISTANCE;
import rapid.ext.arc.MOTION_METHOD_DRIVEARC_PARAM_HEADINGCHANGE;

import com.ardor3d.math.Transform;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyTransform;

/**
 * 
 */
public class RapidCmdWaypoint extends AbstractPlanTaskWaypoint {
    private static final Logger logger = Logger.getLogger(RapidCmdWaypoint.class);

    static final String[] understoodCommands = { MOBILITY_METHOD_MOVE.VALUE,
                                                 MOTION_METHOD_DRIVEARC.VALUE
    };
    Vector3 t_world = new Vector3();

    public RapidCmdWaypoint(String taskName, Object data, PlanState planState, Object parentKey) {
        super(taskName, data, planState, parentKey);
        updateTask(data, planState);
    }

    @Override
    public void updateTask(Object data, PlanState planState) {
        m_begXfm.set(planState.getXyzTransform());
        final CommandHelper cmdHelp = (CommandHelper)data;
        final Command cmd = cmdHelp.getCommand();
        if(cmd.cmdName.equals(MOBILITY_METHOD_MOVE.VALUE)) {
            updateMobilityMove(cmdHelp, planState);
        }
        if(cmd.cmdName.equals(MOTION_METHOD_DRIVEARC.VALUE)) {
            updateMotionDriveArc(cmdHelp, planState);
        }
        m_endXfm.set(planState.getXyzTransform());
        m_node.setTranslation(planState.getXyzTransform().getTranslation());
    }

    public static boolean isUnderstood(CommandHelper cmdHelp) {
        final Command cmd = cmdHelp.getCommand();
        for(String understoodCommand : understoodCommands) {
            if(cmd.cmdName.equals(understoodCommand))
                return true;
        }
        return false;
    }

    @Override
    public boolean canUpdate(Object data) {
        if(data instanceof CommandHelper) {
            final CommandHelper cmdHelp = (CommandHelper)data;
            return isUnderstood(cmdHelp);
        }
        return false;
    }

    /**
     * FIXME we will assume site frame for now
     */
    protected void updateMobilityMove(CommandHelper cmdHelp, PlanState planState) {
        ParameterList params = cmdHelp.getParameters();
        double[] xyt          = params.getVec3d(MOBILITY_METHOD_MOVE_PARAM_END_LOCATION.VALUE).userData;
        double[] xytTolerance = params.getVec3d(MOBILITY_METHOD_MOVE_PARAM_END_LOCATION_TOLERANCE.VALUE).userData;
        //String refFrame    = params.getString(MOBILITY_METHOD_MOVE_PARAM_FRAME_NAME.VALUE);
        // FIXME we will assume site frame for now
        //Matrix3 wpRot = new Matrix3();
        Vector3 wpXyz = new Vector3(xyt[0], xyt[1], 0);
        double theta = xyt[2];
        float tolerance = 0.5f*(float)(xytTolerance[0]+xytTolerance[1]);
        boolean isDirectional = Math.abs(xytTolerance[2]) <= Math.PI;

        //-- get z from terrain
        ReadOnlyTransform siteToWorld = planState.getSiteToWorldTransform();
        siteToWorld.applyForward(wpXyz, t_world);
        VerveBaseMap.setZFromMap(t_world, 0);
        siteToWorld.applyInverse(t_world, wpXyz);

        setIsDirectional(isDirectional);
        setTolerance(tolerance);
        setDirection(theta);

        planState.setXyz(wpXyz);
        planState.setYaw(theta);
    }


    /**
     * FIXME only handles straight drives
     */
    protected void updateMotionDriveArc(CommandHelper cmdHelp, PlanState planState) {
        ParameterList params = cmdHelp.getParameters();
        float distance      = params.getFloat(MOTION_METHOD_DRIVEARC_PARAM_DISTANCE.VALUE);
        float headingChange = params.getFloat(MOTION_METHOD_DRIVEARC_PARAM_HEADINGCHANGE.VALUE);
        float crabDirection = params.getFloat(MOTION_METHOD_DRIVEARC_PARAM_CRABDIRECTION.VALUE);
        if(headingChange == 0 && crabDirection == 0) {
            Vector3 wpXyz = new Vector3();
            Vector3 drive = new Vector3(distance, 0, 0);
            Transform xfm = planState.getTransform(new Transform());
            xfm.applyForward(drive, wpXyz);

            //-- get z from terrain
            ReadOnlyTransform siteToWorld = planState.getSiteToWorldTransform();
            siteToWorld.applyForward(wpXyz, t_world);
            VerveBaseMap.setZFromMap(t_world, 0);
            siteToWorld.applyInverse(t_world, wpXyz);

            setIsDirectional(false);
            setTolerance(0.2f);

            planState.setXyz(wpXyz);
        }
        else {
            logger.warn("FIXME: updateMotionDriveArc only supports straight line drives");
        }
    }
}
