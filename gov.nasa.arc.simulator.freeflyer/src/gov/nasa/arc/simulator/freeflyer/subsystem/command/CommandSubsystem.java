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
package gov.nasa.arc.simulator.freeflyer.subsystem.command;

import gov.nasa.arc.irg.freeflyer.rapid.FreeFlyerCommands;
import gov.nasa.arc.simulator.freeflyer.publishers.PublisherSubsystem;

import java.util.Random;




/**
 * This will keep track of what telemetry will be published for this subsystem and the timing
 * @author rjayt3
 *
 */
public class CommandSubsystem extends PublisherSubsystem {
	
	private CommandSubsystemPublisher commandSubsystemPublisher = new CommandSubsystemPublisher();
	
	private static final int SLEEPTIME_MILLIS = 2000+new Random(System.currentTimeMillis()).nextInt(500);;
	
	
	public void publishTelemetry() {
		publishTelemetry(-1);  // publish all telemetry for this subsystem forever
	}
	
	/**
	 * Thread that will start publishing telemetry
	 */
	@Override
	public void publishTelemetry(int total) {
		final boolean forever = (total < 0) ? true : false;

		Runnable t = new Runnable() {
			@Override
			public void run() {

				
				commandSubsystemPublisher.publishConfig();
				int ctr=0;
				int loops = 1;
				if (!forever) loops = total;
				for (int i=0; i<loops; i++) {
					// QoS dictates lifecycle of message to 100 samples
			    	if (ctr % MESSAGE_LIFECYCLE_LIMIT != 0) {
			    		//commandSubsystemPublisher = new CommandSubsystemPublisher();
			    	}
					
					try {
						//real freeflyer wont send commands to self
						//commandSubsystemPublisher.publishSample(FreeFlyerCommands.COMMANDS.GO_CMD,"Random Number : "+new Random(System.currentTimeMillis()).nextInt(10000));
					    ctr = (ctr > 1000) ? 1 : ctr+1;
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					if (forever) {
						i--;
					}
					
					try {
						Thread.sleep(SLEEPTIME_MILLIS);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					//System.out.println("publish command: "+ ctr);
					
				}
			}
		};
		(new Thread(t)).start();
	}

}
