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
package gov.nasa.dds.rti.ui;

import gov.nasa.dds.rti.ui.preferences.DdsRestartPropertyChangeListener;

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
public class RtiDdsUiActivator extends AbstractUIPlugin {
    //private static Logger logger = Logger.getLogger(RtiDdsUiActivator.class);

	// The plug-in ID
	public static final String PLUGIN_ID = "gov.nasa.dds.rti.ui"; //$NON-NLS-1$

	// The shared instance
	private static RtiDdsUiActivator plugin;
	
	/**
	 * The constructor
	 */
	public RtiDdsUiActivator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
    public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		getPreferenceStore().addPropertyChangeListener(new DdsRestartPropertyChangeListener());
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
    public void stop(BundleContext context) throws Exception {
        //logger.debug("stop.");
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static RtiDdsUiActivator getDefault() {
		return plugin;
	}

	   /**
     * Shortcut for getting image descriptor.
     * @param key
     * @return
     */
    public static ImageDescriptor getImageDescriptor(String key){
        return getDefault().getImageRegistry().getDescriptor(key);
    }
    
    @Override
    protected void initializeImageRegistry(ImageRegistry reg) {
        super.initializeImageRegistry(reg);
        reg.put("dds", getImage("icons/dds-16.png"));
        reg.put("dds-participant", getImage("icons/dds-participant.png"));
    }

    public Image getImage(String imageName) {
        URL url = FileLocator.find(getBundle(), new Path(imageName), null);
        return ImageDescriptor.createFromURL(url).createImage();
    }

}
