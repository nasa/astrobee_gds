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
package gov.nasa.rapid.v2.e4;

import gov.nasa.dds.rti.system.RtiDds;
import gov.nasa.dds.system.Dds;
import gov.nasa.rapid.v2.e4.agent.DiscoveredAgentRepository;
import gov.nasa.rapid.v2.e4.message.collector.RapidMessageCollector;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class RapidV2Activator implements BundleActivator {
    //private static Logger logger = Logger.getLogger(RapidV2Activator.class);
    
    private static BundleContext context;

    static BundleContext getContext() {
        return context;
    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    public void start(BundleContext bundleContext) throws Exception {
        RapidV2Activator.context = bundleContext;

        //== First, set our DDS implementation
        Dds.setDdsImpl(new RtiDds());
        
        //== Then, kick off agent discovery
        DiscoveredAgentRepository.INSTANCE.getClass();
    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    public void stop(BundleContext bundleContext) throws Exception {
        //logger.debug("stop.");
        RapidMessageCollector.instance().writeMeasuredSizes();
        RapidV2Activator.context = null;
    }

}
