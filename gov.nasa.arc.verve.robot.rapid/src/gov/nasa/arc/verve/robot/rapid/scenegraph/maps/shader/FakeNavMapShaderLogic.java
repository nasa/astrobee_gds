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
import rapid.ext.NAVMAP_GOODNESS;

import com.ardor3d.image.Texture2D;
import com.ardor3d.renderer.state.GLSLShaderObjectsState;
import com.ardor3d.renderer.state.RenderState.StateType;
import com.ardor3d.renderer.state.TextureState;
import com.ardor3d.scenegraph.Mesh;

public class FakeNavMapShaderLogic implements INavMapShaderLogic {
    final String GOODNESS = NAVMAP_GOODNESS.VALUE;

    final String[] layerNames = new String[] { GOODNESS };


    public String[] getLayerNames() {
        return layerNames;
    }

    public void setup(Mesh mesh, NavMapDataTextures textures) {
        TextureState ts = (TextureState)mesh.getLocalRenderState(StateType.Texture);
        if(ts == null) {
            ts = new TextureState();
            mesh.setRenderState(ts);
        }
        Texture2D goodness = textures.getLayerData(GOODNESS).texture;
        ts.setTexture(goodness, 1);
        update(mesh, textures);
    }

    public void update(Mesh mesh, NavMapDataTextures textures) {
        update();
    }

    public void update() {
        //
    }

    @Override
    public GLSLShaderObjectsState aliveShaderState() {
        return null;
    }
    
    @Override
    public GLSLShaderObjectsState deadShaderState() {
        return null;
    }

    @Override
    public void updateTextureState(Mesh mesh, NavMapDataTextures textures) {  
        //
    }

    @Override
    public boolean needsTextureStateUpdate() {
        return false;
    }
}
