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
package gov.nasa.dds.rti;

import gov.nasa.dds.system.Dds;
import gov.nasa.util.IProgressUpdater;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class RtiDdsActivator implements BundleActivator {
    private static final Logger logger = Logger.getLogger(RtiDdsActivator.class);

    private static BundleContext context;

    static BundleContext getContext() {
        return context;
    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    public void start(BundleContext bundleContext) throws Exception {
        RtiDdsActivator.context = bundleContext;
    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    public void stop(BundleContext bundleContext) throws Exception {
        RtiDdsActivator.context = null;
        
        IProgressUpdater progress = new IProgressUpdater() {
            @Override
            public void updateProgress(String status) {
                //logger.debug(status);
            }
        };
        logger.debug("Stopping DDS...");
        Dds.stop(progress);
        
        //logger.debug("Destroy remaining DDS participants on exit...");
        //DdsEntityFactory.destroyAllParticipants(null);
        logger.debug("done.");
    }

}
