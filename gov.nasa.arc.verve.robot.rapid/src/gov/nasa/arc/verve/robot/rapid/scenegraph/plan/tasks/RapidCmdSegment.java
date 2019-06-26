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

import java.util.ArrayList;

import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyTransform;

import gov.nasa.arc.verve.common.ardor3d.Ardor3D;
import gov.nasa.arc.verve.common.VerveBaseMap;
import gov.nasa.arc.verve.robot.parts.concepts.plan.PlanState;
import gov.nasa.arc.verve.robot.parts.concepts.plan.TaskCollection;
import gov.nasa.arc.verve.robot.scenegraph.shape.concepts.DirectionalPath;
import gov.nasa.util.Colors;

/**
 * 
 */
public class RapidCmdSegment extends TaskCollection  {
    //private static final Logger logger = Logger.getLogger(RapidCmdSegment.class);

    protected DirectionalPath m_path;

    public RapidCmdSegment(String taskName) {
        super(taskName, null, null, null);
        m_path = new DirectionalPath("segPath");
        m_path.setDefaultColor(Ardor3D.color(Colors.X11.LightGray));
        m_node.attachChild(m_path);
    }

    @Override
    public void updateTask(Object data, PlanState planState) {
        // nothing
    }

    @Override
    public boolean canUpdate(Object data) {
        return false;
    }

    @Override
    public void compile(PlanState planState) {
        //logger.debug(this.getClass().getSimpleName()+":"+getName()+" compile");
        float zOff = 0.2f;
        double vertSpacing = 2;
        int numTasks = m_tasks.size();
        if(numTasks > 0) {
            ReadOnlyTransform  begXfm = m_tasks.get(0).getBeginningTransform();
            ReadOnlyTransform  endXfm = m_tasks.get(numTasks-1).getEndTransform();
            Vector3 siteBeg = new Vector3();
            Vector3 siteEnd = new Vector3();
            Vector3 siteXyz = new Vector3();
            Vector3 worldBeg = new Vector3();
            Vector3 worldEnd = new Vector3();
            Vector3 worldXyz = new Vector3();
            ReadOnlyTransform siteToWorld = planState.getSiteToWorldTransform();

            siteBeg.set(begXfm.getTranslation());
            siteEnd.set(endXfm.getTranslation());

            if(!begXfm.isIdentity()) {
                siteToWorld.applyForward(siteBeg, worldBeg);
                siteToWorld.applyForward(siteEnd, worldEnd);

                double dist = siteBeg.distance(siteEnd);
                int numVerts = (int)(dist/vertSpacing);
                if(numVerts < 2)
                    numVerts = 2;
                ArrayList<Vector3> verts = new ArrayList<Vector3>(numVerts);
                double inc = 1.0/(numVerts-1);
                for(int i = 0; i < numVerts; i++) {
                    worldBeg.lerp(worldEnd, i*inc, worldXyz);
                    VerveBaseMap.setZFromMap(worldXyz, zOff);
                    siteToWorld.applyInverse(worldXyz, siteXyz);
                    verts.add(new Vector3(siteXyz));
                }
                m_path.queueUpdateData(verts, false);
                m_path.setTransform(m_invXfm);
            }
            else {
                clearPath();
            }
        }
        else {
            clearPath();
        }
    }

    protected void clearPath() {
        ArrayList<Vector3> verts = new ArrayList<Vector3>(1);
        m_path.queueUpdateData(verts, false);
    }
}
