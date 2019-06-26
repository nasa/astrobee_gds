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
package gov.nasa.arc.verve.ardor3d.e4.util.export;

import gov.nasa.arc.verve.ardor3d.e4.Ardor3D;

import com.ardor3d.math.Matrix3;
import com.ardor3d.math.Transform;
import com.ardor3d.math.Vector3;

public class TestTransformInverse {

    public static void main(String[] args) {
        Transform aTb = new Transform();
        
        Matrix3 rot = new Matrix3(1, 0, 0, 0, 0, -1, 0, 1, 0);
        aTb.setRotation(rot);
        
        Vector3 trans = new Vector3(1, -2, 0);
        aTb.setTranslation(trans);
        
        Ardor3D.format(aTb);
        System.out.println("aTb = "+ Ardor3D.format(aTb));
        
        Transform bTa = aTb.invert(null);
        
        System.out.println("bTa = " + Ardor3D.format(bTa));
        
        Transform identity = aTb.multiply(bTa, null);
        
        System.out.println("aTb . bTa = " + Ardor3D.format(identity));
    }


}
