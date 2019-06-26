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
package gov.nasa.arc.simulator.freeflyer.publishers;

import gov.nasa.arc.simulator.freeflyer.FreeFlyer;
import gov.nasa.dds.exception.DdsEntityCreationException;
import gov.nasa.rapid.idl.ext.astrobee.message.MessageTypeExtAstro;
import gov.nasa.rapid.v2.e4.system.RapidEntityFactory;
import gov.nasa.rapid.v2.e4.util.RapidUtil;

import org.apache.log4j.Logger;

import rapid.Command;
import rapid.ParameterUnion;
import rapid.ext.astrobee.GuestScienceApk;
import rapid.ext.astrobee.GuestScienceConfig;
import rapid.ext.astrobee.GuestScienceConfigDataWriter;
import rapid.ext.astrobee.GuestScienceState;
import rapid.ext.astrobee.GuestScienceStateDataWriter;

import com.rti.dds.infrastructure.InstanceHandle_t;

public class GuestScienceApkStatePublisher {
	private static final Logger logger = Logger.getLogger(GuestScienceApkStatePublisher.class);

	protected int numLoops            = 100;
	protected int sleepTime           = 500;

	protected final String srcName    = GuestScienceApkStatePublisher.class.getSimpleName();

	protected GuestScienceConfig           config;
	protected GuestScienceConfigDataWriter configWriter;
	protected InstanceHandle_t     configInstance;

	protected GuestScienceState           sample;
	protected GuestScienceStateDataWriter sampleWriter;
	protected InstanceHandle_t     sampleInstance;

	private static GuestScienceApkStatePublisher INSTANCE;

	protected SimulatorGuestScienceManager manager;

	public static GuestScienceApkStatePublisher getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new GuestScienceApkStatePublisher();
			try {
				INSTANCE.createWriters();
				INSTANCE.initializeDataTypes();
				INSTANCE.publishConfig();
			} catch (DdsEntityCreationException e) {
				System.err.println(e);
			}
		}
		return INSTANCE;
	}

	private GuestScienceApkStatePublisher() {
	}

	/**
	 * create the endpoints (i.e. readers and writers)
	 */
	public void createWriters() throws DdsEntityCreationException {   	

		configWriter = (GuestScienceConfigDataWriter)
				RapidEntityFactory.createDataWriter(FreeFlyer.PARTICIPANT_ID, 
						MessageTypeExtAstro.GUEST_SCIENCE_CONFIG_TYPE, 
						FreeFlyer.getPartition());

		sampleWriter = (GuestScienceStateDataWriter)
				RapidEntityFactory.createDataWriter(FreeFlyer.PARTICIPANT_ID, 
						MessageTypeExtAstro.GUEST_SCIENCE_STATE_TYPE, 
						FreeFlyer.getPartition());
	}

	/**
	 * initialize the data types that we will be publishing
	 */
	public void initializeDataTypes() {
		final int serialId = 0;

		manager = SimulatorGuestScienceManager.getInstance();

		config = new rapid.ext.astrobee.GuestScienceConfig();
		RapidUtil.setHeader(config.hdr, FreeFlyer.getPartition(), srcName, serialId);
		setConfigFieldsFromManager();

		//-- Initialize an AgentSample
		sample = new rapid.ext.astrobee.GuestScienceState();
		RapidUtil.setHeader(sample.hdr, FreeFlyer.getPartition(), srcName, serialId);
		setSampleFieldsFromManager();

		//-- register the data instances *after* we have set
		//   assetName and participantName in headers (i.e. the keyed fields)
		if(configWriter != null)
			configInstance = configWriter.register_instance(config);
		if(sampleWriter != null)
			sampleInstance = sampleWriter.register_instance(sample);
	}

	protected void setConfigFieldsFromManager() {
		config.apkStates.userData.clear();
		for(GuestScienceApk info : manager.getApkStates()) {
			config.apkStates.userData.add(info);
		}
	}

	protected void setSampleFieldsFromManager() {
		sample.runningApks.userData.clear();
		for(Boolean isRunning : manager.getRunningInfo()) {
			sample.runningApks.userData.add(isRunning);
		}
	}

	public void startApk(String apkName){
		manager.startApk(apkName);
		publishSimulatorGuestScienceApkState();
		try {
			GuestScienceDataPublisher.getInstance().publishData(apkName);
		} catch (InterruptedException e) {
		}
	}
	
	public void startApk(Command cmd) {
		ParameterUnion pu = (ParameterUnion) cmd.arguments.userData.get(0);
		String apkName = pu.s();		
		manager.startApk(apkName);
		
		publishSimulatorGuestScienceApkState();
		try {
			GuestScienceDataPublisher.getInstance().publishData(apkName);
		} catch (InterruptedException e) {
		}
	}

	public void stopApk(String apkName) {	
		manager.stopApk(apkName);
		publishSimulatorGuestScienceApkState();
		GuestScienceDataPublisher.getInstance().stopSummaryPublishing();
	}
	
	public void stopApk(Command cmd) {
		ParameterUnion pu = (ParameterUnion) cmd.arguments.userData.get(0);
		String apkName = pu.s();	
		manager.stopApk(apkName);
		publishSimulatorGuestScienceApkState();
		GuestScienceDataPublisher.getInstance().stopSummaryPublishing();
	}
	
	

	public void publishSimulatorGuestScienceApkState() {
		try {
			setSampleFieldsFromManager();
			publishSample();
		} catch (InterruptedException e) {
			System.err.println(e);
		}
	}

	public void publishConfig() {
		//-- publish the Config
		configWriter.write(config, configInstance);
	}

	int count = 0;
	public void publishSample(GuestScienceState agentState) throws InterruptedException {

		sample = agentState;
		RapidUtil.setHeader(sample.hdr, FreeFlyer.getPartition(), srcName, count++);

		publishSample();
	}

	public void publishSample() throws InterruptedException {
		sample.hdr.timeStamp = System.currentTimeMillis();

		if(sampleWriter != null) {
			sampleWriter.write(sample, sampleInstance);
		}
		else {
			logger.info("sampleWriter is null");
		}
	}
}
