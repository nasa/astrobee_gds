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
package gov.nasa.arc.verve.common.interest;

import com.ardor3d.math.Vector3;

/**
 * simple holder for interest point info
 */
public class InterestPointData {
    public InterestPointProvider provider     = null;
    public String                mode         = "Free";
    public final Vector3         primary      = new Vector3();
    public final Vector3         secondary    = new Vector3();
    public final Vector3         location     = new Vector3();
    public boolean               hasSecondary = false;
    public boolean               hasFixedLocation = false;
    
    public void set(InterestPointData in) {
        provider = in.provider;
        mode     = in.mode;
        primary.set(in.primary);
        secondary.set(in.secondary);
        location.set(in.location);
        hasSecondary = in.hasSecondary;
        hasFixedLocation = in.hasFixedLocation;
    }
    
    public void clear() {
        provider = null;
        mode     = "Free";
        primary.set(0,0,0);
        secondary.set(0,0,0);
        location.set(0,0,0);
        hasSecondary = false;
        hasFixedLocation = false;
    }
}
