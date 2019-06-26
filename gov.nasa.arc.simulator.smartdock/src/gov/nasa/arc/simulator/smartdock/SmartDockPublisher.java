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
package gov.nasa.arc.simulator.smartdock;

import gov.nasa.dds.exception.DdsEntityCreationException;
import gov.nasa.rapid.idl.ext.astrobee.message.MessageTypeExtAstro;
import gov.nasa.rapid.v2.e4.system.RapidEntityFactory;
import gov.nasa.rapid.v2.e4.util.RapidUtil;

import org.apache.log4j.Logger;

import rapid.ext.astrobee.DockState;
import rapid.ext.astrobee.DockStateDataWriter;

import com.rti.dds.infrastructure.InstanceHandle_t;

public class SmartDockPublisher {
	private static final Logger logger = Logger.getLogger(SmartDockPublisher.class);

	protected final String srcName    = SmartDockPublisher.class.getSimpleName();
	protected DockStateDataWriter sampleWriter;
	protected DockState sample;
	protected InstanceHandle_t handler;

	private static SmartDockPublisher INSTANCE;

	public static SmartDockPublisher getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new SmartDockPublisher();
			try {
				INSTANCE.createWriters();
				INSTANCE.initializeDataTypes();
			} catch (DdsEntityCreationException e) {
				System.err.println(e);
			}

		}
		return INSTANCE;
	}

	private SmartDockPublisher() {
	}


	/**
	 * create the endpoints (i.e. readers and writers)
	 */
	public void createWriters() throws DdsEntityCreationException {   	

		sampleWriter = (DockStateDataWriter)
				RapidEntityFactory.createDataWriter(SmartDock.PARTICIPANT_ID, 
						MessageTypeExtAstro.DOCK_STATE_TYPE, 
						SmartDock.getPartition());
	}

	public void setupBerths(String berthOneBee, String berthTwoBee) {
		SmartDockAstrobeeController.getInstance().setBerthOne(berthOneBee);
		SmartDockAstrobeeController.getInstance().setBerthTwo(berthTwoBee);
	}
	
	/**
	 * initialize the data types that we will be publishing
	 */
	public void initializeDataTypes() {
		final int serialId = 0;

		//-- Initialize an AgentSample
		sample = new DockState();
		RapidUtil.setHeader(sample.hdr, SmartDock.getPartition(), srcName, serialId);
		
		if(sampleWriter != null) {
			handler = sampleWriter.register_instance(sample);
		}
	}

	public void updateDockState(){
		
	}
	
	public void publishData(){
		if(sampleWriter != null) {
			try{
				System.out.println("Publishing SmartDock state from the publisher");
				sampleWriter.write(SmartDockAstrobeeController.getInstance().getBerth(), handler);
			}catch(Exception e){
			}
		}
		else {
			logger.info("sampleWriter is null");
		}
	}
}
