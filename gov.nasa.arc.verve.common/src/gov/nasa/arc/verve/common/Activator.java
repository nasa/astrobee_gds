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

import gov.nasa.arc.verve.common.preferences.rcp.VervePreferencesEclipsePlugin;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "gov.nasa.arc.verve.common";

    // The shared instance
    private static Activator plugin;

    /**
     * The constructor
     */
    public Activator() {
        //
    }

    IPropertyChangeListener listener;
    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;

        VervePreferences.setImpl(new VervePreferencesEclipsePlugin());
        // establish data dir
       
        // DW 8/8/16 - This line creates a verve/data directory in the user's home folder
        // The Control Station is not allowed to do that.
        // (Why is this Activator even getting called???)
        // VervePreferences.getDataDir();

        // add a listener to update VerveDebug
        listener = new IPropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent event) {
                if(event.getProperty().equals(VervePreferences.P_DEBUG_AXIS_USE_BOUND_CENTER)) {
                    VerveDebug.setDrawDebugAxisUseBoundCenter(VervePreferences.isDebugAxisUseBoundCenter());
                }
            }
        };
        getPreferenceStore().addPropertyChangeListener(listener);
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static Activator getDefault() {
        return plugin;
    }


    @Override
    protected void initializeImageRegistry(ImageRegistry reg) {
        super.initializeImageRegistry(reg);
        reg.put("folder", getImageDescriptorFromPath("images/icon/folder.png"));
        reg.put("network_link", getImageDescriptorFromPath("images/icon/network_link.png"));
        reg.put("ground_overlay", getImageDescriptorFromPath("images/icon/ground_overlay.png"));
        reg.put("label", getImageDescriptorFromPath("images/icon/label.png"));
        reg.put("layer_shape_polyline", getImageDescriptorFromPath("images/icon/layer_shape_polyline.png"));
        reg.put("layer_shape", getImageDescriptorFromPath("images/icon/layer_shape.png"));
        reg.put("layer_small", getImageDescriptorFromPath("images/icon/layer_small.png"));
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
