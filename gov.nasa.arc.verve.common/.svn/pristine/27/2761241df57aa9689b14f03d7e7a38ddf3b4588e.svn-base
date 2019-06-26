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
package gov.nasa.arc.verve.common;

import com.ardor3d.intersection.IntersectionRecord;
import com.ardor3d.intersection.PickData;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyVector3;
import com.ardor3d.scenegraph.Spatial;

/**
 * PickInfo 
 */
public class PickInfo {
    protected final Vector3 m_pickPoint = new Vector3(); // world coords
    protected Spatial       m_spatial = null;
    protected final Vector3 m_offset = new Vector3(); // offset from spatial origin

    public PickInfo(ReadOnlyVector3 pickPoint, Spatial spatial) {
        set(pickPoint, spatial);
    }

    public PickInfo(PickData pickData) {
        set(pickData);
    }

    public void set(ReadOnlyVector3 worldPoint, Spatial spatial) {
        m_pickPoint.set(worldPoint);
        m_spatial = spatial;
        if(m_spatial != null) {
            m_pickPoint.subtract(m_spatial.getWorldTranslation(), m_offset);
        }
        else {
            m_offset.set(0,0,0);
        }
    }

    public void set(PickData pickData) {
        try {
            m_spatial = (Spatial)pickData.getTarget();
            final IntersectionRecord ir = pickData.getIntersectionRecord();
            int closest = ir.getClosestIntersection();
            if( closest >= 0) {
                m_pickPoint.set(ir.getIntersectionPoint(closest));
                m_pickPoint.subtract(m_spatial.getWorldTranslation(), m_offset);
            }
            else if(m_spatial != null) {
                m_pickPoint.set(m_spatial.getWorldTranslation());
            }
            else {
                m_pickPoint.set(0,0,0);
            }
        }
        catch(Throwable t) {
            t.printStackTrace(); // unlikely to have Pickable that is not a Spatial
        }
    }

    public ReadOnlyVector3 getPickPoint() {
        return m_pickPoint;
    }

    public Spatial getSpatial() {
        return m_spatial;
    }

    public ReadOnlyVector3 getSpatialOffset() {
        return m_offset;
    }
}
