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
package gov.nasa.rapid.v2.framestore.dds.config.miroparams;

// miro params struct
// maps to rapid::FrameMParameters
public class rapid_FrameMParameters {
    public String parent = "roverFrame";
    public double x = 0;
    public double y = 0;
    public double z = 0;
    public double r11 = 1.0;
    public double r12 = 0.0;
    public double r13 = 0.0;
    public double r21 = 0.0;
    public double r22 = 1.0;
    public double r23 = 0.0;
    public double r31 = 0.0;
    public double r32 = 0.0;
    public double r33 = 1.0;
    
    @Override
    public String toString() {
        return String.format("xyz[%f,%f,%f] m[%f,%f,%f; %f,%f,%f; %f,%f,%f] parent=%s", 
                x,y,z, 
                r11, r12, r13, r21, r22, r23, r31, r32, r33,
                parent);
    }

}
