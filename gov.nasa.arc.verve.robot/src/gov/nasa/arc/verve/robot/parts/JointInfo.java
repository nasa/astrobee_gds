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
package gov.nasa.arc.verve.robot.parts;

import com.ardor3d.math.type.ReadOnlyVector3;
import com.ardor3d.scenegraph.Spatial;

public class JointInfo {
    /** name of node in 3D model */
    public String          nodeName = "";
    /** name of joint in JointConfig (currently unused) */
    public String          jointName = null;
    /** index in JointSample array (currently unused) */
    public int             jointIndex = 0;
    public Spatial         spatial  = null;
    public ReadOnlyVector3 rotAxis = null;
    public float           offset = 0;
    public float           scale  = 1;
    public boolean         required = false;
    
    public JointInfo(String nodeName, ReadOnlyVector3 rotAxis) {
        this.nodeName = nodeName;
        this.rotAxis  = rotAxis;
    }
    
    public JointInfo(String nodeName, ReadOnlyVector3 rotAxis, boolean required) {
        this.nodeName = nodeName;
        this.rotAxis  = rotAxis;
        this.required = required;
    }
    
    public JointInfo(String nodeName, ReadOnlyVector3 rotAxis, boolean required, float offset) {
        this.nodeName = nodeName;
        this.rotAxis  = rotAxis;
        this.required = required;
        this.offset   = offset;
    }
    
    
}
