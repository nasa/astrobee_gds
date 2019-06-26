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
package gov.nasa.arc.verve.common.ardor3d;

import gov.nasa.arc.verve.common.ardor3d.bounding.LruUsageTreeController;
import gov.nasa.arc.verve.common.ardor3d.framework.BasicVerveScene;
import gov.nasa.arc.verve.common.ardor3d.framework.screenshot.ScreenShotDelegateEclipsePlugin;

import java.net.URL;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.ardor3d.bounding.CollisionTreeManager;
import com.ardor3d.extension.terrain.client.Terrain;

/**
 * The activator class controls the plug-in life cycle
 */
public class VerveArdor3dActivator extends AbstractUIPlugin {
    private static final Logger logger = Logger.getLogger(VerveArdor3dActivator.class);

	// The plug-in ID
	public static final String PLUGIN_ID = "gov.nasa.arc.verve.common.ardor3d";

	// The shared instance
	private static VerveArdor3dActivator plugin;
	
	/**
	 * The constructor
	 */
	public VerveArdor3dActivator() {
	    // 
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		
		// force initialization of viz io plugin to 
		// initialize image loaders
		gov.nasa.arc.viz.io.VizIOActivator.getDefault();
		
		CollisionTreeManager.INSTANCE.setCollisionTreeController(new LruUsageTreeController());
		CollisionTreeManager.INSTANCE.setMaxElements(75);
		
        BasicVerveScene.addPickIgnoreClass(Terrain.class);
        BasicVerveScene.setScreenShotDelegate(new ScreenShotDelegateEclipsePlugin());
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
		
        //-- shutdown the render thread
        logger.debug("halting render thread...");
        try {
            Ardor3D.stopRenderThread();
            logger.debug("...halted.");
        }
        catch(Throwable e) {
            e.printStackTrace();
        }
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static VerveArdor3dActivator getDefault() {
		return plugin;
	}

	@Override
	protected void initializeImageRegistry(ImageRegistry reg) {
		super.initializeImageRegistry(reg);
		reg.put("target", getImageDescriptorFromPath("icons/target.png"));
		reg.put("gear", getImageDescriptorFromPath("icons/gear.png"));
		reg.put("camera__pencil", getImageDescriptorFromPath("icons/camera__pencil.png"));
		reg.put("camera", getImageDescriptorFromPath("icons/camera.png"));
		reg.put("pview", getImageDescriptorFromPath("icons/pview.png"));
		reg.put("ruler", getImageDescriptorFromPath("icons/ruler.png"));
		reg.put("sample", getImageDescriptorFromPath("icons/sample.png"));
		reg.put("target__pencil", getImageDescriptorFromPath("icons/target_pencil.png"));
		
	}

	  /**
	 * Return the image descriptor created from the path found in the bundle 
	 * @param imageName relative to the plugin path
	 * @return the imagedescriptor, null if none found
	 */
	protected ImageDescriptor getImageDescriptorFromPath( String imageName) {
		URL url = FileLocator.find(getBundle(), new Path(imageName), null);
		return ImageDescriptor.createFromURL(url);
	}

	/**
	 * Shortcut for getting image descriptor.
	 * @param key
	 * @return
	 */
	public static ImageDescriptor getImageDescriptorFromRegistry(String key){
		return getDefault().getImageRegistry().getDescriptor(key);
	}

	/**
	 * Gets the image via the image registry
	 * @param key
	 * @return null if not found.
	 */
	public static Image getImageFromRegistry(String key){
		return  getDefault().getImageRegistry().get(key);
	}
	
	/**
     * Returns an image descriptor for the image file at the given
     * plug-in relative path
     *
     * @param path the path
     * @return the image descriptor
     * @deprecated Please use the ImageRegistry as it will correctly dispose of images.
     */
    @Deprecated
    public static ImageDescriptor getImageDescriptor(String path) {
        return imageDescriptorFromPlugin(PLUGIN_ID, path);
    }
    
}
