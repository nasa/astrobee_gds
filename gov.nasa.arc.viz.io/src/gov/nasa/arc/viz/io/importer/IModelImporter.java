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
package gov.nasa.arc.viz.io.importer;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

import org.osgi.framework.Bundle;

import com.ardor3d.scenegraph.Node;

public interface IModelImporter {
	
	 /**
     * @param url
     * @param map
     * @return
     * @throws IOException
     */
    public Node importModel(URL url, Map<String, Object> map)
    throws IOException;

    /**
     * @param filepath
     * @param map
     * @return
     * @throws IOException
     */
    public Node importModel(String filepath, Map<String, Object> map)
    throws IOException;
    
    /**
     * Import a model from within a plugin.
     * @param bundle
     * @param filepath
     * @param map
     * @return
     * @throws IOException
     */
    public Node importModel(Bundle bundle, String filepath, Map<String, Object> map)
    throws IOException;

    /**
     * @param inputStream
     * @param map
     * @return
     * @throws IOException
     */
    public Node importModel(InputStream inputStream, Map<String, Object> map)
    throws IOException;

}
