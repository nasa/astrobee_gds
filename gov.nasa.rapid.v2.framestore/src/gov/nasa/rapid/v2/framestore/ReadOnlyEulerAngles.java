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
package gov.nasa.rapid.v2.framestore;

/**
 * Interface for the EulerAngles. @see EulerAngles for more information.
 * 
 * @author Lorenzo Flueckiger
 *
 */
public interface ReadOnlyEulerAngles {

    double getAngle1();
    
    double getAngle2();
    
    double getAngle3();
    
    Type getType();
    
    double[] toArray(double[] store);
    
    public enum Type {
        XYZs,
        ZYXr,
        XYZr,
        ZYXs
        // and 20 others still to define...
    }

}
