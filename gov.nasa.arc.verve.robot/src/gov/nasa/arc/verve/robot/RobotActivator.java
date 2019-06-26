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
package gov.nasa.arc.verve.robot;

import gov.nasa.arc.verve.common.DataBundleHelper;

import java.net.URI;

import org.apache.log4j.Logger;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class RobotActivator extends AbstractUIPlugin {
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(RobotActivator.class);

    // The plug-in ID
    public static final String PLUGIN_ID = "gov.nasa.arc.verve.robot";

    // The shared instance
    private static RobotActivator plugin;
    private RobotPersistenceHandler persistenceHandler = null;
    
    /**
     * The constructor
     */
    public RobotActivator() {
    }

    public RobotPersistenceHandler getPersistenceHandler() {
        return persistenceHandler;
    }
    
    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    @Override
    @SuppressWarnings("static-access")
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;

        URI savePath = getStateLocation().toFile().toURI();
        persistenceHandler = new RobotPersistenceHandler(savePath);
        RobotRegistry.addListener(persistenceHandler);
        
        //-- add the images dir to the DataBundleHelper
        DataBundleHelper.addDataBundle("robot", PLUGIN_ID);
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop(BundleContext context) throws Exception {	    
        // cleanup robot registry
        RobotRegistry.clear();
        RobotRegistry.removeListener(persistenceHandler);

        plugin = null;
        super.stop(context);
    }

    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static RobotActivator getDefault() {
        return plugin;
    }

}
