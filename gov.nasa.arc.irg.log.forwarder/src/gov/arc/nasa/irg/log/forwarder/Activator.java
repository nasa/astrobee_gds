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
package gov.arc.nasa.irg.log.forwarder;

import java.io.InputStream;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.LogManager;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogReaderService;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator {
	private static final org.apache.log4j.Logger logger =
			org.apache.log4j.Logger.getLogger(Activator.class);

	private ServiceTracker<LogReaderService, LogReaderService> tracker = null;
	private JULHandler handler = null;

	public void start(BundleContext bundleContext) throws Exception {
		// Forward the Eclipse logs to log4j
		tracker = new ServiceTracker<LogReaderService, LogReaderService>(
				bundleContext, LogReaderService.class, new TrackerCustomizer());
		tracker.open();

		// Setup java.util.logging
		LogManager manager = LogManager.getLogManager();
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		InputStream stream = loader.getResourceAsStream("logging.properties");

		if (stream != null) {
			manager.readConfiguration(stream);
		} else {
			logger.error("Java Logging properties resource returned null");
		}

		Logger root = Logger.getLogger("");
		for (Handler handler : root.getHandlers()) {
			root.removeHandler(handler);
		}

		handler = new JULHandler(java.util.logging.Level.INFO);
	}

	public void stop(BundleContext bundleContext) throws Exception {
		tracker.close();
		handler.close();
	}

}
