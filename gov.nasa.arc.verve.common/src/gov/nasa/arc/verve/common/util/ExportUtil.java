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
package gov.nasa.arc.verve.common.util;

import gov.nasa.util.URIUtil;

import java.io.File;
import java.net.URL;

import org.apache.log4j.Logger;

import com.ardor3d.scenegraph.Node;
import com.ardor3d.util.UrlUtils;
import com.ardor3d.util.export.binary.BinaryExporter;

public class ExportUtil {
    private static final Logger logger = Logger.getLogger(ExportUtil.class);
    private static final String EXT = ".ardor3d";
    /**
     * Given the file URL of an imported file, save an Ardor3d binary
     * @param node
     * @param urlOfImportedModel
     */
    public static void saveAsBinary(Node node, URL urlOfImportedModel) {
        if(node != null) {
            try {
                String filename = urlOfImportedModel.toString().substring(6);
                //logger.debug("urlOfImportedModel="+urlOfImportedModel);
                logger.debug("filename="+filename);
                //File inFile = new File(URIUtil.toURI(urlOfImportedModel));
                File inFile = new File(filename);
                String fileName = inFile.getName();
                if(!fileName.endsWith(EXT)) {
                    fileName = fileName+EXT;
                }
                URL outUrl = UrlUtils.resolveRelativeURL(urlOfImportedModel, fileName);
                File outFile = new File(URIUtil.toURI(outUrl));
                logger.debug("about to save "+outFile.getPath());
                new BinaryExporter().save(node, outFile);
            }
            catch(Throwable t) {
                logger.warn("", t);
            }
        }
    }
}
