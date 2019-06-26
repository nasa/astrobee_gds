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
package gov.nasa.arc.viz.io.extensionPoint;

import gov.nasa.arc.viz.io.ImportExportManager;
import gov.nasa.arc.viz.io.importer.ModelImporterFactory;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

/**
 * This class encapsulates the reliance on Eclipse extension points for binding together loaders.
 * 
 * @author tecohen
 *
 */
public class ExtensionPointInitializer {
	public static final String EXTENSION_POINT = "gov.nasa.arc.viz.io.ModelImporterFactory";

	protected HashMap<String, IConfigurationElement> m_configElementMap = new HashMap<String, IConfigurationElement>(); // map of class names to configuration elements 
	protected HashMap<String, String> m_extensionClassNameMap = new HashMap<String, String>(); // map of file extensions to class names.

	protected boolean m_initialized = false;
	
	public static ExtensionPointInitializer INSTANCE = new ExtensionPointInitializer();
	
	private ExtensionPointInitializer() {
		initialize();
	}
	
	/**
	 * Get the set of keys (file extensions) registered with loaders
	 * @return
	 */
	public Set<String> getKeys() {
		return INSTANCE.m_extensionClassNameMap.keySet();
	}
	
	/**
	 * Initialize the registry based on extension points
	 */
	protected synchronized void initialize() {
	        
		if (!m_initialized){
			IExtensionRegistry reg = Platform.getExtensionRegistry();
	
			IConfigurationElement[] genericExtensions = reg.getConfigurationElementsFor(EXTENSION_POINT);
			for (IConfigurationElement element : genericExtensions){
				String importerClassName = element.getAttribute("modelImporterFactory");
				m_configElementMap.put(importerClassName, element);
				for (IConfigurationElement extensionElement : element.getChildren("fileExtension")) {
					String extension = extensionElement.getAttribute("extension");
					extension = ImportExportManager.dotExt(extension);
					m_extensionClassNameMap.put(extension, importerClassName);
				}
			}
		}
	}
	
	/**
     * Load a ModelImportFactory from an extension point.
     * Add it to the registry with all associated extensions.
     * @param ext
     * @return
     */
    public ModelImporterFactory loadModelImportFactory(String ext){

    	String className = m_extensionClassNameMap.get(ext);
    	if (className != null){
    		IConfigurationElement element = m_configElementMap.get(className);
    		if (element != null){
    			Bundle bun = Platform.getBundle(element.getContributor().getName());
    			try {
    				Class customizationClass = bun.loadClass(className);
    				Constructor constructor = customizationClass.getConstructor((Class[])null);
    				ModelImporterFactory result = (ModelImporterFactory)constructor.newInstance((Object[])null);
    				
    				if (result != null){
	    				for (IConfigurationElement extensionElement : element.getChildren("fileExtension")) {
	    					String extension = extensionElement.getAttribute("extension");
	    					if (extension != null){
		    					extension = ImportExportManager.dotExt(extension);
		    					ImportExportManager.registerImporterFactory(ext, result);
	    					}
	    				}
	    				ImportExportManager.registerImporterFactory(ext, result);
    				}
    				
    				return result;
    				
    			} catch (Exception e) { 
    			    // ignored
    			} 
    		}
    	}
    	return null;
		
    }
		

}
