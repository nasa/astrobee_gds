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

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogReaderService;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public class TrackerCustomizer implements ServiceTrackerCustomizer<LogReaderService, LogReaderService> {
	private final Log4JForwarder forwarder = new Log4JForwarder();

	@Override
	public LogReaderService addingService(
			ServiceReference<LogReaderService> reference) {
		Bundle bundle = reference.getBundle();
		BundleContext bundleContext = bundle.getBundleContext();
		LogReaderService logReaderService = bundleContext.getService(reference);
		logReaderService.addLogListener(forwarder);
		return logReaderService;
	}

	@Override
	public void modifiedService(ServiceReference<LogReaderService> reference,
			LogReaderService arg1) {
	}

	@Override
	public void removedService(ServiceReference<LogReaderService> reference,
			LogReaderService service) {
		service.removeLogListener(forwarder);
	}

}
