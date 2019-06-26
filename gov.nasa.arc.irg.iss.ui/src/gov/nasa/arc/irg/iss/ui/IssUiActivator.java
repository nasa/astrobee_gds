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
package gov.nasa.arc.irg.iss.ui;

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
public class IssUiActivator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "gov.nasa.arc.irg.iss.ui"; //$NON-NLS-1$

	// The shared instance
	private static IssUiActivator plugin;
	
	/**
	 * The constructor
	 */
	public IssUiActivator() {
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
	public static IssUiActivator getDefault() {
		return plugin;
	}
	
	@Override
	protected void initializeImageRegistry(ImageRegistry reg) {
		super.initializeImageRegistry(reg);
		reg.put("success_16", getImageDescriptorFromPath("icons/success_16.png"));
		reg.put("fail_16", getImageDescriptorFromPath("icons/fail_16.png"));
		reg.put("log_24", getImageDescriptorFromPath("icons/log_24.png"));
		reg.put("help_24", getImageDescriptorFromPath("icons/help_24.png"));
		reg.put("exit_24", getImageDescriptorFromPath("icons/exit_24.png"));
		reg.put("setup_24", getImageDescriptorFromPath("icons/setup_24.png"));
		
		reg.put("transparent", getImageDescriptorFromPath("images/Transparent.png"));
		reg.put("greenCircle", getImageDescriptorFromPath("images/ActiveGreen.png"));
		reg.put("orangeCircle", getImageDescriptorFromPath("images/InactiveOrange.png"));
		reg.put("grayCircle", getImageDescriptorFromPath("images/EmptyGray.png"));
		reg.put("cyanCircle", getImageDescriptorFromPath("images/StaleCyan.png"));
		
		reg.put("command_bg_91", getImageDescriptorFromPath("images/command_bg_91.png"));
		reg.put("command_bg_disabled_91", getImageDescriptorFromPath("images/command_bg_disabled_91.png"));
		reg.put("command_bg_pressed_91", getImageDescriptorFromPath("images/command_bg_pressed_91.png"));

		reg.put("command_bg_121", getImageDescriptorFromPath("images/command_bg_121.png"));
		reg.put("command_bg_disabled_121", getImageDescriptorFromPath("images/command_bg_disabled_121.png"));
		reg.put("command_bg_pressed_121", getImageDescriptorFromPath("images/command_bg_pressed_121.png"));
		
		reg.put("command_bg_160", getImageDescriptorFromPath("images/command_bg_160.png"));
		reg.put("command_bg_disabled_160", getImageDescriptorFromPath("images/command_bg_disabled_160.png"));
		reg.put("command_bg_pressed_160", getImageDescriptorFromPath("images/command_bg_pressed_160.png"));

		reg.put("command_bg_200", getImageDescriptorFromPath("images/command_bg_200.png"));
		reg.put("command_bg_disabled_200", getImageDescriptorFromPath("images/command_bg_disabled_200.png"));
		reg.put("command_bg_pressed_200", getImageDescriptorFromPath("images/command_bg_pressed_200.png"));

		reg.put("button_normal_78", getImageDescriptorFromPath("images/button_normal_78.png"));
		reg.put("button_normal_enter_78", getImageDescriptorFromPath("images/button_normal_enter_78.png"));
		reg.put("button_pressed_78", getImageDescriptorFromPath("images/button_pressed_78.png"));
		
		reg.put("button_nm_90x28", getImageDescriptorFromPath("images/button_nm_90x28.png"));
		reg.put("button_dn_90x28", getImageDescriptorFromPath("images/button_dn_90x28.png"));
		reg.put("button_en_90x28", getImageDescriptorFromPath("images/button_en_90x28.png"));
		
		reg.put("controlpad_small_down", getImageDescriptorFromPath("images/controlpad_small_down.png"));
		reg.put("controlpad_small_left", getImageDescriptorFromPath("images/controlpad_small_left.png"));
		reg.put("controlpad_small_plain", getImageDescriptorFromPath("images/controlpad_small_plain.png"));
		reg.put("controlpad_small_right", getImageDescriptorFromPath("images/controlpad_small_right.png"));
		reg.put("controlpad_small_up", getImageDescriptorFromPath("images/controlpad_small_up.png"));
		
		reg.put("zoomin", getImageDescriptorFromPath("images/zoomin.png"));
		reg.put("zoomout", getImageDescriptorFromPath("images/zoomout.png"));
		reg.put("forwardCamera", getImageDescriptorFromPath("images/forward_camera_icon.png"));
		reg.put("backwardCamera", getImageDescriptorFromPath("images/backward_camera_icon.png"));
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
