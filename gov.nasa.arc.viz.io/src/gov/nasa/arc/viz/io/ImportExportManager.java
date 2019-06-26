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
package gov.nasa.arc.viz.io;

import gov.nasa.arc.viz.io.extensionPoint.ExtensionPointInitializer;
import gov.nasa.arc.viz.io.image.SGIImageLoader;
import gov.nasa.arc.viz.io.importer.FileDescriptor;
import gov.nasa.arc.viz.io.importer.IModelImporter;
import gov.nasa.arc.viz.io.importer.ModelImporterFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.osgi.framework.Bundle;

import com.ardor3d.image.Image;
import com.ardor3d.image.util.ImageLoaderUtil;
import com.ardor3d.image.util.AWTImageLoader;
import com.ardor3d.scenegraph.Node;

/**
 * The ImportExportManager class provides a registry of importers and
 * methods for loading files and URLs.  Each importer is placed in the
 * registry one time for each file format it supports.  File formats are
 * determined by file extension.  There is always only a single
 * instance of this class.
 *
 */
public class ImportExportManager {
    private static final Logger logger = Logger.getLogger(ImportExportManager.class.getName());

    protected Hashtable<String, ModelImporterFactory> registry = null;	
    public static ImportExportManager INSTANCE = new ImportExportManager();

    /**
     * Constructor
     * Creates and registers an instance of the VRML01 and VRML97 loaders.
     *
     */
    protected ImportExportManager() {
        initialize();
    }


    public synchronized void startup() {
        // forces construction
    }

    /**
     * Initialize the registry based on extension points
     */
    protected synchronized void initialize() {
        if (registry == null){
            registry = new Hashtable<String, ModelImporterFactory>();
            AWTImageLoader.registerLoader();
            final SGIImageLoader sgiLoader = new SGIImageLoader();
            ImageLoaderUtil.registerHandler(sgiLoader, ".sgi", ".rgb", ".rgba");
        }
    }

    /**
     * make sure all extensions begin with '.' 
     * @param ext
     * @return
     */
    public static String dotExt(String ext) {
        if(!ext.startsWith(".")) {
            ext = "."+ext;
        }
        return ext;
    }

    /**
     * Add a loader to the registry.
     * @param ext the loader file extension (do not prefix with a period)
     * @param loader the loader instance
     */
    public static synchronized void registerImporterFactory(String ext, ModelImporterFactory loader) {
        INSTANCE.registry.put(dotExt(ext), loader);
    }

    /**
     * Remove a loader for a specific file extension.
     * @param ext the file extension (do not prefix with a period)
     */
    public static synchronized void unregisterImporterFactory(String ext) {
        INSTANCE.registry.remove(dotExt(ext));
    }

    /**
     * TODO: if you do not want to depend on Eclipse, remove the lazy loading feature below.
     * @param ext
     * @return
     */
    protected ModelImporterFactory getModelImporterFactory(String ext) {
        String dotted = dotExt(ext);
        ModelImporterFactory result =  registry.get(dotted);
        if (result == null){
            // try lazily loading it via extension point
            result = ExtensionPointInitializer.INSTANCE.loadModelImportFactory(dotted);
        }
        return result;
    }

    /**
     * Get the keys (file extensions) associated with registered importers
     * @return
     */
    public static Set<String> getKeys() {
    	return ExtensionPointInitializer.INSTANCE.getKeys();
    }
    
    /**
     * Load the contents of a scene graph file.
     * @param label the label for the root node
     * @param uriString the URI of the file location
     * @param dataType the VizDataType of the file
     * @return the BranchGroup containing the scene graph
     * @throws URISyntaxException
     */
    public static synchronized Node importModel(String location, Map<String,Object> params, String ext) {
        try {
            FileDescriptor desc = new FileDescriptor(location);
            if (ext == null)
                ext = desc.getExtension();
            if (INSTANCE.getModelImporterFactory(ext) == null)
                ext = desc.getSecondaryExtension();
            ModelImporterFactory factory = INSTANCE.getModelImporterFactory(ext);
            if (factory == null) {
                logger.warning("No importer for "+ext);
                return(null);
            }
            IModelImporter modelImporter = factory.createModelImporter();
            if (modelImporter == null) {
                logger.warning("Error creating importer for "+ext);
                return(null);
            }       
            logger.info("Importing "+desc+" . . .");
            if (desc.isURL()) {
                return(modelImporter.importModel(desc.getURL(), params));
            }
            else {
                return(modelImporter.importModel(desc.toString(), params));
            }
        }
        catch (Exception e) {
            logger.warning("Unable to import "+location);
            e.printStackTrace();
            return(null);
        }

    }

    /**
     * @param url
     * @return
     * @throws IOException
     */
    public static synchronized Node importModel(URL url) throws IOException {
        return importModel(url, null);
    }

    /**
     * @param url
     * @param params
     * @return
     * @throws IOException
     */
    public static synchronized Node importModel(URL url, Map<String,Object> params) throws IOException {
        return importModel(url, params, null);
    }

    /**
     * @param url
     * @param params
     * @param ext
     * @return
     * @throws IOException
     */
    public static synchronized Node importModel(URL url, Map<String,Object> params, String ext) throws IOException
    {
        try {
            FileDescriptor desc = new FileDescriptor(url.toString());
            if (ext == null)
                ext = desc.getExtension();
            if (INSTANCE.getModelImporterFactory(ext) == null)
                ext = desc.getSecondaryExtension();
            ModelImporterFactory factory = INSTANCE.getModelImporterFactory(ext);
            if (factory == null) {
                throw new IOException("No importer for extension \""+ext+"\"");
            }
            IModelImporter modelImporter = factory.createModelImporter();
            if (modelImporter == null) {
                throw new IOException("Error creating importer for \""+ext+"\"");
            }       
            logger.info("Importing "+desc+" . . .");
            return(modelImporter.importModel(url, params));
        }
        catch (Throwable t) {
            IOException e =  new IOException("Error importing "+url.toString());
            e.initCause(t);
            throw e;
        }
    }


    /**
     * @param bundle
     * @param filepath
     * @return
     * @throws IOException
     */
    public static synchronized Node importModel(Bundle bundle, String filepath) throws IOException {
        return importModel(bundle, filepath, null);
    }

    /**
     * @param bundle
     * @param filepath
     * @param params
     * @return
     * @throws IOException
     */
    public static synchronized Node importModel(Bundle bundle, String filepath, Map<String,Object> params) throws IOException {
        return importModel(bundle, filepath, params, null);
    }

    /**
     * @param bundle
     * @param filepath
     * @param params
     * @param ext
     * @return
     * @throws IOException
     */
    public static synchronized Node importModel(Bundle bundle, String filepath, Map<String,Object> params, String ext) throws IOException
    {
        try {
            FileDescriptor desc = new FileDescriptor(filepath);
            if (ext == null)
                ext = desc.getExtension();
            if (INSTANCE.getModelImporterFactory(ext) == null)
                ext = desc.getSecondaryExtension();
            ModelImporterFactory factory = INSTANCE.getModelImporterFactory(ext);
            if (factory == null) {
                throw new IOException("No importer for extension \""+ext+"\"");
            }
            IModelImporter modelImporter = factory.createModelImporter();
            if (modelImporter == null) {
                throw new IOException("Error creating importer for \""+ext+"\"");
            }       
            logger.info("Importing "+desc+" . . .");
            return(modelImporter.importModel(bundle, filepath, params));
        }
        catch (Throwable t) {
            IOException e =  new IOException("Error importing "+filepath.toString() + " from plugin " + bundle);
            e.initCause(t);
            throw e;
        }
    }

    /**
     * Import an image given the file location and format (file extension).
     * If the format is null the method will try to obtain it from the location.
     * @param location the file location
     * @param format the file format
     * @param flipImage true if the image should be flipped
     * @return
     */
    public static synchronized Image importImage(String location, String format, boolean flipImage) {	
        Image image = null;
        try {
            FileDescriptor desc = new FileDescriptor(location);
            if (format == null)
                format = desc.getExtension();
            if (desc.isURL()) {
            	InputStream is = desc.getURL().openStream();
                image = ImageLoaderUtil.loadImage("."+format, is, flipImage);
                is.close();
            }
            else {
            	FileInputStream fis = new FileInputStream(desc.getFile());
                image = ImageLoaderUtil.loadImage("."+format, fis, flipImage);
                fis.close();
            }
        }
        catch (Exception e) {
            logger.warning("Could not load image: "+location);
            e.printStackTrace();
        }
        return(image);
    }
}
