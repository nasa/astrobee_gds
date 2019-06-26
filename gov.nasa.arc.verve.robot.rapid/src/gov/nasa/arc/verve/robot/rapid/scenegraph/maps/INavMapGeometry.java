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
package gov.nasa.arc.verve.robot.rapid.scenegraph.maps;

import com.ardor3d.bounding.BoundingVolume;
import com.ardor3d.math.type.ReadOnlyMatrix3;
import com.ardor3d.math.type.ReadOnlyTransform;
import com.ardor3d.math.type.ReadOnlyVector3;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.MeshData;
import com.ardor3d.scenegraph.hint.SceneHints;

/**
 * Interface representing the geometry of a nav map. 
 * Methods from the Ardor3D Mesh class are defined here to simplify downstream useage
 * @author mallan
 *
 */
public interface INavMapGeometry {
    
    boolean isInvertWinding();
    /** invert polygon winding */
    void setInvertWinding(boolean state);
    
    /** invalidate geometry so it will not be drawn */
    void invalidate();
    
    /** set flag to reinitialize static data on next draw */
    void setStaticDataDirty();
    
    /**
     * A client should request a HeightMapData object 
     * using borrowHeightMapData() and fill out all
     * of the public fields. When complete, YOU MUST 
     * return the data using returnHeightMapData() to
     * release the write lock. This should be done in 
     * a try-finally block.
     */
    HeightMapData borrowHeightMapData();
    void returnHeightMapData();
    
    boolean isClampToTerrain();
    void setClampToTerrain(boolean state);
    
    /**
     * Generic method to update mesh data from arbitrary telemetry
     * @param telemetry 
     * @param siteToWorld transform from site frame to world coordinates
     * @param mapFrame transform of map frame
     * @param zOffset offset
     * @param zSign whether map is z-up(1) or z-down(-1)
     */
    void updateFromTelemetry(Object telemetry, ReadOnlyTransform siteToWorld, ReadOnlyTransform mapFrame, float zOffset, float zSign);

    /** @return this object as a Mesh */
    Mesh asMesh();
    //== Mesh methods ====================
    MeshData getMeshData();
    String getName();
    void   setName(String name);
    SceneHints getSceneHints();
    void setTranslation(final ReadOnlyVector3 translation);
    void setRotation(final ReadOnlyMatrix3 rotation);
    BoundingVolume getModelBound();
    BoundingVolume getModelBound(final BoundingVolume store);
    void setModelBound(final BoundingVolume modelBound);
    //void setModelBound(final BoundingVolume modelBound, final boolean autoCompute);
}
