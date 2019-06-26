/******************************************************************************
 * Copyright © 2019, United States Government, as represented by the 
 * Administrator of the National Aeronautics and Space Administration. All 
 * rights reserved.
 * 
 * The Astrobee Control Station platform is licensed under the Apache License, 
 * Version 2.0 (the "License"); you may not use this file except in compliance 
 * with the License. You may obtain a copy of the License at
 * 
 * 	http://www.apache.org/licenses/LICENSE-2.0. 
 *   
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT 
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the 
 * License for the specific language governing permissions and limitations 
 * under the License.
 *****************************************************************************/
package gov.nasa.arc.verve.ardor3d.e4.util;

import com.ardor3d.math.Vector2;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyVector3;

/**
 * Find the closest point on a line given a test point
 * Adapted from a compact solution found here: http://www.ogre3d.org/tikiwiki/Nearest+point+on+a+line
 * with the exception of clamping to endpoints which can be 
 * efficiently done using tS
 */
public class PointOnLine {
    private final Vector3 a = new Vector3(); // point 1 of line
    private final Vector3 b = new Vector3(); // point 1 of line
    private final Vector3 A = new Vector3(); // vector from a to test point
    private final Vector3 B = new Vector3(); // vector from a to b
    private final Vector3 u = new Vector3(); // normalized vector from a to b 
    private final Vector3 V = new Vector3(); // vector along line to give point
    private double tS;
    private double len;
    private final Vector3 result = new Vector3();
        
    public void setLine(ReadOnlyVector3 begin, ReadOnlyVector3 end) {
        a.set(begin);
        b.set(end);
        len = a.distance(b);
    }

    /**
     * give line which has already been set, get closest point on line
     * @param tp
     * @return
     */
    public ReadOnlyVector3 closestPointTo(ReadOnlyVector3 testPoint) {
        testPoint.subtract(a, A);
        b.subtract(a, B);
        B.normalize(u);
        tS = A.dot(u);
        u.multiply(tS, V);
        if(tS < 0) { // 
            result.set(a);
        }
        else if(tS > len) {
            result.set(b);
        }
        else {
            a.add(V, result);
        }

        return result;
    }
}