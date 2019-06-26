///*******************************************************************************
// * Copyright (c) 2013 United States Government as represented by the 
// * Administrator of the National Aeronautics and Space Administration. 
// * All rights reserved.
// * 
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// * 
// *   http://www.apache.org/licenses/LICENSE-2.0
// * 
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// ******************************************************************************/
//package gov.nasa.rapid.v2.ui.e4;
//
//import gov.nasa.rapid.v2.e4.preferences.RapidPreferences;
//import gov.nasa.rapid.v2.ui.e4.preferences.RapidPreferencesEclipseUi;
//
//import java.net.URL;
//
//import org.eclipse.core.runtime.FileLocator;
//import org.eclipse.core.runtime.Path;
//import org.eclipse.jface.resource.ImageDescriptor;
//import org.eclipse.jface.resource.ImageRegistry;
//import org.eclipse.swt.graphics.Image;
//import org.eclipse.ui.plugin.AbstractUIPlugin;
//import org.osgi.framework.BundleContext;
//
///**
// * The activator class controls the plug-in life cycle
// */
//public class RapidV2UiActivator extends AbstractUIPlugin {
//
//	// The plug-in ID
//	public static final String PLUGIN_ID = "gov.nasa.rapid.v2.ui"; //$NON-NLS-1$
//
//	// The shared instance
//	private static RapidV2UiActivator plugin;
//	
//	/**
//	 * The constructor
//	 */
//	public RapidV2UiActivator() {
//	}
//
//	@Override
//    public void start(BundleContext context) throws Exception {
//		super.start(context);
//		plugin = this;
//		RapidPreferences.setImpl(new RapidPreferencesEclipseUi());
//	}
//
//	@Override
//    public void stop(BundleContext context) throws Exception {
//		plugin = null;
//		super.stop(context);
//	}
//
//	/**
//	 * Returns the shared instance
//	 *
//	 * @return the shared instance
//	 */
//	public static RapidV2UiActivator getDefault() {
//		return plugin;
//	}
//
//	/**
//     * Shortcut for getting image descriptor.
//     * @param key
//     * @return
//     */
//    public static ImageDescriptor getImageDescriptor(String key){
//        return getDefault().getImageRegistry().getDescriptor(key);
//    }
//    
//    @Override
//    protected void initializeImageRegistry(ImageRegistry reg) {
//        super.initializeImageRegistry(reg);
//        reg.put("refresh",getImage("icons/arrow_refresh.png"));
//        reg.put("pause",  getImage("icons/pause.png"));
//        reg.put("pin-big",getImage("icons/pin-big.png"));
//        reg.put("pin",    getImage("icons/pin.png"));
//        reg.put("play",   getImage("icons/play.png"));
//        reg.put("rapid",  getImage("icons/rapid-gears-16.png"));
//    }
//
//    public Image getImage(String imageName) {
//        URL url = FileLocator.find(getBundle(), new Path(imageName), null);
//        return ImageDescriptor.createFromURL(url).createImage();
//    }
//}
