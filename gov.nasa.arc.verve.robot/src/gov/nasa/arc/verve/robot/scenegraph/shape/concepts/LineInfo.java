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
package gov.nasa.arc.verve.robot.scenegraph.shape.concepts;

import java.util.ArrayList;

import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Vector3;

public class LineInfo {
    public final ArrayList<Vector3> verts = new ArrayList<Vector3>();
    public final ColorRGBA color = new ColorRGBA();

    public void setLength(int len) {
        if(len > verts.size()) {
            verts.ensureCapacity(len);
            while(len > verts.size()) {
                verts.add(new Vector3());
            }
        }
        else {
            while(len < verts.size()) {
                verts.remove(verts.size()-1);
            }
        }
    }
    public int getLength() {
        return verts.size();
    }
}

