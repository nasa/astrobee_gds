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
package gov.nasa.arc.verve.robot.rapid.scenegraph.maps.shader;

import gov.nasa.arc.verve.robot.rapid.scenegraph.maps.NavMapDataTextures;

import com.ardor3d.renderer.state.GLSLShaderObjectsState;
import com.ardor3d.scenegraph.Mesh;

public interface INavMapShaderLogic {
    GLSLShaderObjectsState aliveShaderState();
    GLSLShaderObjectsState deadShaderState();
    
    String[] getLayerNames();
    
    /** setup shader */
    void setup(Mesh mesh, NavMapDataTextures textures);
    /** update texture state and related shader values */
    void updateTextureState(Mesh mesh, NavMapDataTextures textures);
    /** update telemetry defined values */
    void update(Mesh mesh, NavMapDataTextures textures);
    /** update user defined values */
    void update();
    
    /** return true if texture state needs to be updated */
    boolean needsTextureStateUpdate();
}
