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
package gov.nasa.arc.verve.robot;

import gov.nasa.arc.verve.common.DataBundleHelper;
import gov.nasa.arc.verve.common.util.ExportUtil;
import gov.nasa.arc.viz.io.ImportExportManager;

import java.io.IOException;
import java.net.URL;

import org.apache.log4j.Logger;

import com.ardor3d.scenegraph.Node;
import com.ardor3d.util.export.binary.BinaryImporter;

public class ModelLoader {
    private static Logger logger = Logger.getLogger(ModelLoader.class);

    /**
     * Given a category and model name, load a model. This method will attempt
     * to load an Ardor3D binary version of the file if the last modified time of the
     * binary file is greater than the last modified time of the source file. 
     * @param category
     * @param modelName
     * @return
     */
    public static Node loadBinaryIfPossible(String category, String modelName) {
        URL sourceUrl;
        URL binaryUrl;
        Node model = null;

        try {
            try {
                binaryUrl = DataBundleHelper.getURL(category, "models/"+modelName+".ardor3d");
                // disable timestamp check because it fails with 
                // exported products
                //File sourceFile = new File(sourceUrl.toURI());
                //File binaryFile = new File(binaryUrl.toURI());
                //long sourceMod = sourceFile.lastModified();
                //long binaryMod = binaryFile.lastModified();
                if(true) { //sourceMod-binaryMod < 0) {
                    model = (Node)(new BinaryImporter().load(binaryUrl));
                }
            }
            catch(IOException e) {
                logger.info("Could not load ardor3d binary model: "+e.getMessage());
            }
            if(model == null) {
                sourceUrl = DataBundleHelper.getURL(category, "models/"+modelName);
                model = ImportExportManager.importModel(sourceUrl);
                ExportUtil.saveAsBinary(model, sourceUrl);
                logger.info("Saved model as ardor3d binary: "+sourceUrl.toString());
            }
        }
        catch(Exception e) {
            logger.error("Error loading model "+modelName, e);
        }
        return model;
    }

}
