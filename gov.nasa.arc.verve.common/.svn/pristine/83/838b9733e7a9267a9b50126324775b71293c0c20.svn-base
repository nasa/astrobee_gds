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


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import com.ardor3d.image.Texture;
import com.ardor3d.image.Texture2D;
import com.ardor3d.image.TextureStoreFormat;
import com.ardor3d.util.TextureManager;
import com.ardor3d.util.resource.URLResourceSource;


/**
 * Utility class to simplify loading data from bundles. 
 * XXX could use some improvement
 * @author mallan
 *
 */
public class DataBundleHelper {
    private static Logger logger = Logger.getLogger(DataBundleHelper.class);
	
	private HashMap<String,String>	m_dataHash = new HashMap<String,String>();

	private void initHashMap() {
		m_dataHash.put("common", Activator.PLUGIN_ID);

		//----------------------------------------------------------
		// In order to add a new category to the hash, put a line 
		// similar to the following in the Activator.start() call:
		//
		// DataBundleHelper.addDataBundle("newcategory", this.PLUGIN_ID);
		//
	}

	protected DataBundleHelper() {
		initHashMap();
	}
	private static DataBundleHelper s_instance = null;
	public static DataBundleHelper instance() {
		if(s_instance == null) {
			s_instance = new DataBundleHelper();
		}
		return s_instance;
	}

	public static void addDataBundle(String category, String fullPluginName) {
		// XXX should be a check here to see if plugin exists. 
		instance().m_dataHash.put(category, fullPluginName);
	}

	/**
	 * Get an array of bundle names that match regex
	 * Passing "*" returns all bundle names
	 * @param categoryRegEx
	 * @return
	 */
	public String[] getBundleNames(String categoryRegEx) {
		if(categoryRegEx.equals("*")) //support simple wildcard as convenience
			categoryRegEx = ".*";
		ArrayList<String> bundleNames = new ArrayList<String>();
		Set<String> keys = m_dataHash.keySet();
		for(String key : keys) {
			if( Pattern.matches(categoryRegEx, key) ) {
				bundleNames.add(m_dataHash.get(key));
			}
		}
		String[] retVal = new String[bundleNames.size()];
		return bundleNames.toArray(retVal);
	}

	/**
	 * Get an absolute URL to a file in an Eclipse RCP bundle
	 * @param category category name (regex supported) to search for file in 
	 * @param relativePath relative path to file (e.g. "images/foo.jpg")
	 * @returns absolute URL to the file 
	 * @throws IOException
	 */
	public static URL getURL(String category, String relativePath) throws IOException {
		return instance().getURLImpl(category, relativePath);
	}
	public URL getURLImpl(String category, String relativePath) throws IOException
	{
		String[] bundleNames = getBundleNames(category);

		// couldn't match category, try using category string as bundle name
		if(bundleNames.length < 1) {
			Bundle test = Platform.getBundle(category);
			if(test != null) {
			    bundleNames = new String[] { category };
			    //logger.debug("Ok, category is actually a bundle name");
			}
			else {
			    logger.debug("Could not match category identifier \""+category+"\".");
			}
		}

		Bundle bundle = null;
		for(String bundleName : bundleNames ) {
			bundle = Platform.getBundle(bundleName);

			if(bundle == null) {
				logger.warn("Could not resolve bundle "+bundleName+". Verify that it is included in your runtime configuration.");
			}
			else {
				Path path = new Path(relativePath);
				URL bndlURL = FileLocator.find(bundle, path, null);	
				if(bndlURL != null) {
					return FileLocator.toFileURL(bndlURL);
				}
			}
		}
		throw new IOException("Could not resolve \""+relativePath+"\" in category \""+category+"\"");
	}

	public static URL getPluginURL(String pluginId, String relativePath) throws IOException  {
		Bundle bundle = null;
		bundle = Platform.getBundle(pluginId);
		if(bundle == null) {
			throw new IOException("Could not resolve bundle "+pluginId+". Verify that it is included in your runtime configuration.");
		}
		else {
			Path path = new Path(relativePath);
			URL bndlURL = FileLocator.find(bundle, path, null);	
			if(bndlURL != null) {
				return FileLocator.toFileURL(bndlURL);
			}
		}
		return null;
	}

//	/**
//	 * TODO throw a better exception
//	 * @param objName
//	 * @param objMan
//	 * @return
//	 */
//	public static Node loadModel(String category, String modelPath) throws Exception {
//		return instance().loadModelImpl(category, modelPath);
//	}
//	public Node loadModelImpl(String category, String modelPath) throws Exception
//	{
//		Node retVal = null;
//		
//		String plugin = m_dataHash.get(category);
//		Bundle bundle = Platform.getBundle(plugin);
//		
//		if(bundle != null ) {
//			Path path = new Path(modelPath);
//			URL bndlURL = FileLocator.find(bundle, path, null);	
//			if(bndlURL != null)
//			{
//				try {
//					URL fileURL = FileLocator.toFileURL(bndlURL);
//					String fileString = fileURL.getPath();
//					
//					File file = new File(fileString);
//					retVal = LoaderManager.loadURI(null, file.toURI(), VizDataTypes.DATA_TYPE_3D);
//				} 
//				catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		}
//		else {
//			throw new Exception("Could not resolve category \""+category+"\" to a data plugin.");
//		}
//		
//		if(retVal == null) {
//			// TODO create some default object and log warning
//		}
//		
//		return retVal;
//	}

/**
 * @param objName
 * @param objMan
 * @return
 * @throws IOException 
 */
public static BufferedImage loadImage(String category, String imagePath) throws IOException {
	return instance().loadImageImpl(category, imagePath);
}
public BufferedImage loadImageImpl(String category, String imagePath) throws IOException
{
	URL fileURL = getURLImpl(category, imagePath);
	File file = new File(fileURL.getPath());
	return ImageIO.read(file);
}

/**
 * load a texture with defaults (wrap=Repeat, minFilter = Trilinar, magFilter = Bilinear, no anisotropy)
 */
public static Texture2D loadTexture(String category, String path) 
throws IOException {
	return instance().loadTextureImpl(category, path, 
			Texture.WrapMode.Repeat, 
			Texture.MinificationFilter.Trilinear, 
			Texture.MagnificationFilter.Bilinear, 
			0, false);
}

/**
 * load a texture with defaults (minFilter = Trilinar, magFilter = Bilinear, no anisotropy)
 */
public static Texture2D loadTexture(String category, String path, 
		Texture.WrapMode wrapMode) 
throws IOException {
	return instance().loadTextureImpl(category, path, 
			wrapMode, 
			Texture.MinificationFilter.Trilinear, 
			Texture.MagnificationFilter.Bilinear, 
			0, false);
}

/**
 * load a texture with no anisotropy
 */
public static Texture2D loadTexture(String category, String path, 
		Texture.WrapMode wrapMode, 
		Texture.MinificationFilter minFilter, 
		Texture.MagnificationFilter magFilter) 
throws IOException {
	return instance().loadTextureImpl(category, path, 
			wrapMode, minFilter, magFilter, 0, false);
}

/**
 * load a texture from the workspace
 */
public static Texture2D loadTexture(String category, String path, 
		Texture.WrapMode wrapMode, 
		Texture.MinificationFilter minFilter, 
		Texture.MagnificationFilter magFilter, 
		float anisotropy) 
throws IOException {
	return instance().loadTextureImpl(category, path, 
			wrapMode, minFilter, magFilter, 
			anisotropy, false);
}

/**
 * load a texture from the workspace
 */
public static Texture2D loadTexture(String category, String path, 
		Texture.WrapMode wrapMode, 
		Texture.MinificationFilter minFilter, 
		Texture.MagnificationFilter magFilter, 
		float anisotropy, boolean flipped) 
throws IOException {
	return instance().loadTextureImpl(category, path, 
			wrapMode, minFilter, magFilter, 
			anisotropy, flipped);
}

/**
 * load a texture from the workspace
 */
public static Texture2D loadTexture(URL url) 
throws IOException {
	return instance().loadTextureImpl(url, 
			Texture.WrapMode.Repeat, 
			Texture.MinificationFilter.Trilinear, 
			Texture.MagnificationFilter.Bilinear, 
			0, false);
}

/**
 * implementation of texture loading routine
 */
public static Texture2D loadTexture(URL url, 
		Texture.WrapMode wrapMode, 
		Texture.MinificationFilter minFilter, 
		Texture.MagnificationFilter magFilter, 
		float anisoPercent) 
throws IOException {
	return instance().loadTextureImpl(url, wrapMode, minFilter, magFilter, anisoPercent, false);
}

/**
 * implementation of texture loading routine
 */
public static Texture2D loadTexture(URL url, 
		Texture.WrapMode wrapMode, 
		Texture.MinificationFilter minFilter, 
		Texture.MagnificationFilter magFilter, 
		float anisoPercent, boolean flipped) 
throws IOException {
	return instance().loadTextureImpl(url, wrapMode, minFilter, magFilter, anisoPercent, flipped);
}

/**
 * implementation of texture loading routine
 */
protected Texture2D loadTextureImpl(String category, String path, 
		Texture.WrapMode wrapMode, 
		Texture.MinificationFilter minFilter, 
		Texture.MagnificationFilter magFilter, 
		float anisoPercent,
		boolean flipped) 
throws IOException {      
	URL url = DataBundleHelper.getURL(category, path);
	return loadTextureImpl(url, wrapMode, minFilter, magFilter, anisoPercent, flipped);
}

/**
 * implementation of texture loading routine
 */
protected Texture2D loadTextureImpl(URL url, 
		Texture.WrapMode wrapMode, 
		Texture.MinificationFilter minFilter, 
		Texture.MagnificationFilter magFilter, 
		float anisoPercent,
		boolean flipped) 
throws IOException {
	Texture2D texture = null;
	texture = (Texture2D) TextureManager.load(new URLResourceSource(url), // XXX FIXME URL to string
			minFilter, TextureStoreFormat.GuessNoCompressedFormat, flipped);
	if(texture == null) {
		throw new IOException("Failed to load texture from URL: "+url.toString());
	}
	texture.setWrap(wrapMode);
	texture.setMagnificationFilter(magFilter);
	texture.setAnisotropicFilterPercent(anisoPercent);
	return texture;
}

}
