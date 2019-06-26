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

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class VerveDataDir {
    public static final String DEFAULT_HOME_ROOT = "/verve/data";
    
    public static final String ORTHO_MAPS   = "maps/ortho";
    public static final String DEM_MAPS     = "maps/dem";
    public static final String MODELS       = "models";
    

    /**
     * Return data subdir (create if necessary)
     */
    public static File get(String relativePath) throws IllegalStateException {
        File dir = new File(VervePreferences.getDataDir().getAbsolutePath()+"/"+relativePath);
        if(dir.exists() && !dir.isDirectory()) {
            throw new IllegalStateException("VERVE Data Dir path is a file, not a directory! "+dir.toString());
        }
        if(!dir.exists()) {
            try {
                FileUtils.forceMkdir(dir);
            } 
            catch (IOException e) {
                throw new IllegalStateException("Cannot create VERVE data dir: "+dir.toString(), e);
            }
        }
        return dir;
    }

    public static File getModelsDir() {
        return get(MODELS);
    }
    public static File getOrthoMapsDir() {
        return get(ORTHO_MAPS);
    }
    public static File getDemMapsDir() {
        return get(DEM_MAPS);
    }
}
