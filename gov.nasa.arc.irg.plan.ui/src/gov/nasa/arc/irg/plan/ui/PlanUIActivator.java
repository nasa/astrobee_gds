/******************************************************************************
 * Copyright © 2019, United States Government, as represented by the 
 * Administrator of the National Aeronautics and Space Administration. All 
 * rights reserved.
 * 
 * The Astrobee Control Station platform is licensed under the Apache License, 
 * Version 2.0 (the "License"); you may not use this file except in compliance 
 * with the License. You may obtain a copy of the License at
 * 
 * 	http://www.apache.org/licenses/LICENSE-2.0. 
 *   
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT 
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the 
 * License for the specific language governing permissions and limitations 
 * under the License.
 *****************************************************************************/
package gov.nasa.arc.irg.plan.ui;

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
public class PlanUIActivator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "gov.nasa.arc.irg.plan.ui"; //$NON-NLS-1$

	// The shared instance
	private static PlanUIActivator plugin;
	
	/**
	 * The constructor
	 */
	public PlanUIActivator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static PlanUIActivator getDefault() {
		return plugin;
	}

	@Override
	protected void initializeImageRegistry(ImageRegistry reg) {
		super.initializeImageRegistry(reg);
		
		reg.put("pause_24", getImageDescriptorFromPath("images/pause_24.png"));
		reg.put("play_24", getImageDescriptorFromPath("images/play_24.png"));
		reg.put("skip_command_24", getImageDescriptorFromPath("images/skip_command_24.png"));
		
		reg.put("fail_16", getImageDescriptorFromPath("images/fail_16.png"));
		reg.put("pause_16", getImageDescriptorFromPath("images/pause_16.png"));
		reg.put("play_16", getImageDescriptorFromPath("images/play_16.png"));
		reg.put("skip_16", getImageDescriptorFromPath("images/skip_16.png"));
		reg.put("success_16", getImageDescriptorFromPath("images/success_16.png"));
		
		reg.put("default", getImageDescriptorFromPath("images/timeline/default.png"));
		reg.put("pancam", getImageDescriptorFromPath("images/timeline/pancam.png"));
		reg.put("station", getImageDescriptorFromPath("images/timeline/station.png"));
		reg.put("traverse", getImageDescriptorFromPath("images/timeline/traverse.png"));
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
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
}
