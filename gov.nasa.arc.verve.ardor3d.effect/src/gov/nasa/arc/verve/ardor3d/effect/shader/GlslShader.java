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
package gov.nasa.arc.verve.ardor3d.effect.shader;

import gov.nasa.arc.verve.common.DataBundleHelper;

import java.net.URL;

import org.apache.log4j.Logger;

import com.ardor3d.renderer.state.GLSLShaderObjectsState;

public class GlslShader {
    private static Logger logger = Logger.getLogger(GlslShader.class);

    /**
     * retrieve a shader from the ardor3d.effect plugin
     * @param name
     * @return
     */
    public static GLSLShaderObjectsState create(String name) {
        return create("ardor3d.effect", name);
    }
    
    /**
     * retrieve a shader from a specified plugin
     * @param category DataBundleHelper category @see DataBundleHelper.getURL()
     * @param name name of shader
     * @return
     */
    public static GLSLShaderObjectsState create(String category, String name) {
        GLSLShaderObjectsState retVal = null;

        try {
            URL vertUrl = DataBundleHelper.getURL(category, "shaders/"+name+".vert");
            URL fragUrl = DataBundleHelper.getURL(category, "shaders/"+name+".frag");
            retVal = new GLSLShaderObjectsState();
            
            retVal.setVertexShader(vertUrl.openStream());
            retVal.setFragmentShader(fragUrl.openStream());
            retVal._needSendShader = true;
        }
        catch(Throwable t) {
            logger.warn("Error setting up shader: "+name, t);
            retVal = null;
        }

        return retVal;
    }
    
    
}
