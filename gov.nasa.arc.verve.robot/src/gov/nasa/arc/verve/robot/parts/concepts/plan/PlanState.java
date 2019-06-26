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
package gov.nasa.arc.verve.robot.parts.concepts.plan;

import com.ardor3d.math.Matrix3;
import com.ardor3d.math.Transform;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyTransform;
import com.ardor3d.math.type.ReadOnlyVector3;

public class PlanState {
    protected double          m_yaw    = 0;
    protected final Vector3   m_xyz    = new Vector3();
    protected final Transform m_xyzXfm = new Transform();
    protected final Transform m_xfm    = new Transform();
    protected final Transform m_tmp    = new Transform();
    
    protected final Matrix3   rot    = new Matrix3();
    
    protected final Transform m_siteToWorld = new Transform();
    
    public void setXyz(ReadOnlyVector3 xyz) {
        m_xyz.set(xyz);
    }
    
    public void addXyz(ReadOnlyVector3 xyz) {
        m_xyz.addLocal(xyz);
    }
    
    public ReadOnlyVector3 getXyz() {
        return m_xyz;
    }
    
    public void setYaw(double yaw) {
        m_yaw = yaw;
    }
    
    public void addYaw(double yaw) {
        m_yaw += yaw;
    }
    
    public double getYaw() {
        return m_yaw;
    }
    
    public Transform getXyzTransform() {
        m_xyzXfm.setTranslation(m_xyz);
        return m_xyzXfm;
    }

    public Transform getTransform() {
        m_xfm.setTranslation(m_xyz);
        rot.fromAngleNormalAxis(m_yaw, Vector3.UNIT_Z);
        m_xfm.setRotation(rot);
        return m_xfm;
    }

    public Transform getTransform(Transform retVal) {
        retVal.setTranslation(m_xyz);
        rot.fromAngleNormalAxis(m_yaw, Vector3.UNIT_Z);
        retVal.setRotation(rot);
        return retVal;
    }

    public void setSiteToWorldTransform(ReadOnlyTransform xfm) {
        m_siteToWorld.set(xfm);
    }
    public ReadOnlyTransform getSiteToWorldTransform() {
        return m_siteToWorld;
    }
}
