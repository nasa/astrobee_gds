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
package gov.nasa.arc.irg.util.ui;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class UtilUIActivator extends AbstractUIPlugin {
    //private static final Logger logger = Logger.getLogger(UtilUIActivator.class);
    
	// The plug-in ID
	public static final String PLUGIN_ID = "gov.nasa.arc.irg.util.ui";

	// The shared instance
	private static UtilUIActivator plugin;
	
	/**
	 * The constructor
	 */
	public UtilUIActivator() {
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
		
		// initialize IrgLogMonitor early so we catch startup messages
		IrgLogMonitor.addRootContext();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
    public void stop(BundleContext context) throws Exception {
        //logger.debug("ColorProvider.INSTANCE.dispose()");
		//ColorProvider.INSTANCE.dispose();
		plugin = null;
        //logger.debug("stop");
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static UtilUIActivator getDefault() {
		return plugin;
	}
	
	@Override
	protected void initializeImageRegistry(ImageRegistry reg) {
		super.initializeImageRegistry(reg);
		reg.put("browse_back", getImageDescriptorFromPath("icons/browse_back.png"));
		reg.put("browse_forward", getImageDescriptorFromPath("icons/browse_forward.png"));
		reg.put("browse_go", getImageDescriptorFromPath("icons/browse_go.png"));
		reg.put("browse_home", getImageDescriptorFromPath("icons/browse_home.png"));
		reg.put("browse_icon", getImageDescriptorFromPath("icons/browse_icon.png"));
		reg.put("browse_icon1", getImageDescriptorFromPath("icons/browse_icon1.png"));
		reg.put("browse_icon2", getImageDescriptorFromPath("icons/browse_icon2.png"));
		reg.put("browse_refresh", getImageDescriptorFromPath("icons/browse_refresh.png"));
		reg.put("browse_stop", getImageDescriptorFromPath("icons/browse_stop.png"));
		reg.put("camera_mount", getImageDescriptorFromPath("icons/camera_mount.png"));
        reg.put("camera_unmount", getImageDescriptorFromPath("icons/camera_unmount.png"));
        reg.put("warning", getImageDescriptorFromPath("icons/warning_16.png"));
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
