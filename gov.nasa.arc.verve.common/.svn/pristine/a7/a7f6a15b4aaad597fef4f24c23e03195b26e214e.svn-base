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

import com.ardor3d.math.Vector3;

/**
 * static interface to query basemap information
 * @author mallan
 *
 */
public class VerveBaseMap {
    protected static IBaseMap s_baseMap = null;
    
    public static void setBaseMap(IBaseMap map) {
        s_baseMap = map;
    }
    
    public static IBaseMap getBaseMap() {
        return s_baseMap;
    }
    
    public static boolean hasBaseMap() {
        return s_baseMap != null;
    }
    
    /**
     * Get height value (altitude) from base map at x,y location
     * @param x east
     * @param y north
     * @return NaN if no IBaseMap has been set
     */
    public static float getHeightAt(float x, float y) {
        if(s_baseMap != null) {
            return s_baseMap.getHeightAt(x, y);
        }
        return Float.NaN;
    }
    
    public static float getHeightAt(float x, float y, int level) {
        if(s_baseMap != null) {
            return s_baseMap.getHeightAt(x, y, level);
        }
        return Float.NaN;
    }
    
    /**
     * Set Z in store from X and Y values (in world coords)
     * @param store inX=east inY=north outZ=height
     * @param zAdd value to add to outZ value
     * @return store with updated Z, or null if no IBaseMap has been set
     */
    public static Vector3 setZFromMap(Vector3 store, float zAdd) {
        if(s_baseMap != null) {
            final float z = s_baseMap.getHeightAt(store.getXf(), store.getYf());
            store.setZ(z+zAdd);
            return store;
        }
        return null;
    }
    
    
}
