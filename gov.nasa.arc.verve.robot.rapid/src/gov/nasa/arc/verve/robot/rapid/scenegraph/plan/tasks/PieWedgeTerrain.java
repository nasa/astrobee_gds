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

import gov.nasa.arc.verve.ardor3d.scenegraph.shape.PieWedge;
import gov.nasa.arc.verve.common.VerveBaseMap;

import com.ardor3d.math.Transform;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyTransform;

public class PieWedgeTerrain extends PieWedge {
    protected final Transform m_toRoot = new Transform();

    public PieWedgeTerrain(String name) {
        this(name, (float)-Math.PI/4, (float)Math.PI/4, 1.0f, 3.0f, 16, 8);
    }

    public PieWedgeTerrain(String name, float startAngle, float endAngle,
                           float innerRadius, float outerRadius, int uSteps,
                           int vSteps) {
        super(name, startAngle, endAngle, innerRadius, outerRadius, uSteps, vSteps);
    }

    public void setToWorldTransform(ReadOnlyTransform toWorld) {
        m_toRoot.set(toWorld);
    }

    @Override
    protected float getZ(float x, float y) {
        if(false) { //m_toRoot != null) {
            Vector3 site = new Vector3(x,y,0);
            Vector3 world = new Vector3();
            m_toRoot.applyForward(site, world);
            VerveBaseMap.setZFromMap(world, 0.02f);
            m_toRoot.applyInverse(world, site);
            return site.getZf();
        }
        return 0;
    }


}
