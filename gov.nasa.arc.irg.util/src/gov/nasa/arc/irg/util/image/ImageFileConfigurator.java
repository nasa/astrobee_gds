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
package gov.nasa.arc.irg.util.image;


/**
 * using the image file configurator sets up location in file system and sets up properties
 * 
 * This can be used for anything that requires images to be saved in the file system containing a properties file with meta data information
 */
import gov.nasa.arc.irg.util.PropertiesHelper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.FileUtils;

public class ImageFileConfigurator {
	
	//TODO just take this out
	public static final String DEFAULT_WORKING_DIRECTORY = System.getProperty("user.dir") + System.getProperty("file.separator") + "images";
	protected String m_rootFilePath = DEFAULT_WORKING_DIRECTORY;
	
	/**
	 * Override this method to load properties from a different bundle
	 * @return the loaded properties
	 */
	public Properties getProperties(String imagePropertiesFileName) {
		// do initialization
		Properties imageProperties = new Properties();
		String propertiesFilePath = getPropertiesFilePath() + System.getProperty("file.separator") + imagePropertiesFileName;

		try {
			InputStream stream = getClass().getResourceAsStream(propertiesFilePath);
			if (stream != null && (stream.available() > 0)) {
				imageProperties.load(stream);
			} else {
				imageProperties = PropertiesHelper.loadProperties(propertiesFilePath);
			}
		}
		catch (IOException e) {
			System.err.println("Unable to retrieve properties: " + propertiesFilePath);
			imageProperties = null;
		}
		return imageProperties;
	}

	
	/**
	 * Initialize the properties directory
	 * 
	 * @param imageName
	 * @return
	 * @throws IOException 
	 */
	public void initializeImagePath(String imageName) throws IOException {
		initializeImagePath(m_rootFilePath, imageName);
	}
	
	public void initializeImagePath(String rootPath, String imageName) throws IOException {
		// given the directory path, set it up
		FileUtils.forceMkdir(new File(rootPath + System.getProperty("file.separator") + imageName));
	}
	
	/**
	 * utility method to return the full path concatinated with the filename
	 */
	public String getFullImagePath(String imageName) {
		return getPropertiesFilePath() + System.getProperty("file.separator") + imageName;
	}
	

	/**
	 * Given the full properties path (including properties filename), update
	 * the key with value.
	 * 
	 * If the file does not exist, create it.  If the file exists but there are 
	 * issues accessing it, throw an Exception
	 * 
	 * @param fullPath
	 * @param key
	 * @param value
	 * @throws Exception
	 */
	public void updateProperties(String fullPath, String key, String value) throws Exception {
		// first check to see if the file already exists
		Properties properties = getProperties(fullPath);
		if (properties == null) {
			createPropertiesFile(fullPath);
			
			// properties file now created
			properties = getProperties(fullPath);
			if (properties==null) { 
				// if there's STILL a problem, BAIL! BAIL! BAIL!
				throw new IOException ("File: " + fullPath + " cannot be accessed.");
			}
		}

		if (!properties.containsKey(key)) {
			// if the property isn't there, create it
			properties.put(key, value);
		} else {
			// otherwise just update it
			properties.setProperty(key, value);
		}
	}
	
	public void createPropertiesFile(String fullPath) throws IOException {
		// check to see if the file exists
		File propFile = new File(fullPath);

		if (propFile.isDirectory()) {
			throw new IOException("File specified: " + fullPath + " is a directory and NOT a file.");
		}

		if (propFile.exists()) { 
			return; // OK file already exists
		} 

		// if it got this far, it's OK, just create the file
		if (!propFile.createNewFile()) {
			throw new IOException("File: " + fullPath + " does not exist and cannot be created.  Please check filesystem.");
		}
	}
	
	public String getPropertiesFilePath() {
		return m_rootFilePath;
	}

	public String getRootPath() {
		return m_rootFilePath;
	}
}
