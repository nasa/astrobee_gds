package gov.nasa.arc.simulator.freeflyer.publishers;

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

import gov.nasa.arc.simulator.freeflyer.FreeFlyer;
import gov.nasa.arc.simulator.freeflyer.telemetry.SimulatorAggregateAstrobeeState;
import gov.nasa.arc.simulator.freeflyer.telemetry.SimulatorAstrobeeStateGds;
import gov.nasa.dds.exception.DdsEntityCreationException;
import gov.nasa.rapid.idl.ext.astrobee.message.MessageTypeExtAstro;
//import gov.nasa.rapid.v2.examples.Examples;
//import gov.nasa.rapid.v2.examples.ExamplesPreferences;
import gov.nasa.rapid.v2.e4.message.MessageType;
import gov.nasa.rapid.v2.e4.message.helpers.ParameterList;
import gov.nasa.rapid.v2.e4.system.RapidEntityFactory;
import gov.nasa.rapid.v2.e4.util.RapidUtil;

import java.util.Random;

import org.apache.log4j.Logger;

import rapid.AgentConfig;
import rapid.Mat33f;
import rapid.Vec3d;
import rapid.ext.astrobee.AgentState;
import rapid.ext.astrobee.AgentStateDataWriter;
import rapid.ext.astrobee.ExecutionState;
import rapid.ext.astrobee.ExecutionStateSeq;
import rapid.ext.astrobee.MobilityState;
import rapid.ext.astrobee.OperatingState;

import com.rti.dds.infrastructure.InstanceHandle_t;

/**
 * Publishes AgentConfig and AgentSamples
 * @author mallan
 *
 */
public class AgentStatePublisher {
	private static final Logger logger = Logger.getLogger(AgentStatePublisher.class);

	protected int numLoops            = 100;
	protected int sleepTime           = 500;

	protected final String srcName    = AgentStatePublisher.class.getSimpleName();

	protected AgentState           sample;
	protected AgentStateDataWriter sampleWriter;
	protected InstanceHandle_t     sampleInstance;
	
	private static AgentStatePublisher INSTANCE;
	
	public static AgentStatePublisher getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new AgentStatePublisher();
			try {
				INSTANCE.createWriters();
				INSTANCE.initializeDataTypes();
			} catch (DdsEntityCreationException e) {
				System.err.println(e);
			}
		}
		return INSTANCE;
	}
	
	private AgentStatePublisher() {
	}

	/**
	 * create the endpoints (i.e. readers and writers)
	 */
	public void createWriters() throws DdsEntityCreationException {   	

		sampleWriter = (AgentStateDataWriter)
				RapidEntityFactory.createDataWriter(FreeFlyer.PARTICIPANT_ID, 
						MessageTypeExtAstro.AGENT_STATE_TYPE, 
						FreeFlyer.getPartition());
	}

	/**
	 * initialize the data types that we will be publishing
	 */
	public void initializeDataTypes() {
		final int serialId = 0;

		//-- Initialize an AgentSample
		sample = new rapid.ext.astrobee.AgentState();
		RapidUtil.setHeader(sample.hdr, FreeFlyer.getPartition(), srcName, serialId);
		//pl.assign(sample.values.userData);

		//-- register the data instances *after* we have set
		//   assetName and participantName in headers (i.e. the keyed fields)
		if(sampleWriter != null)
			sampleInstance = sampleWriter.register_instance(sample);
	}

	public void publishSimulatorAstrobeeState() {
		try {
			SimulatorAstrobeeStateGds sasg = (SimulatorAstrobeeStateGds)SimulatorAggregateAstrobeeState.getInstance().getAstrobeeState();
			publishSample(sasg.toAgentState());
		} catch (InterruptedException e) {
			System.err.println(e);
		}
	}
	
	public AgentState getUpdatedAgentState() {
		return sample;
	}
	
	int count = 0;
	public void publishSample(AgentState agentState) throws InterruptedException {

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
