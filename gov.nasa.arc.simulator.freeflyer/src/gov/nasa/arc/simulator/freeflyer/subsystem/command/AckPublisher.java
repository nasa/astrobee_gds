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

import gov.nasa.arc.simulator.freeflyer.FreeFlyer;
import gov.nasa.arc.simulator.freeflyer.publishers.AgentStatePublisher;
import gov.nasa.dds.exception.DdsEntityCreationException;
import gov.nasa.rapid.v2.e4.message.MessageType;
import gov.nasa.rapid.v2.e4.system.RapidEntityFactory;
import gov.nasa.rapid.v2.e4.util.RapidUtil;

import org.apache.log4j.Logger;

import rapid.Ack;
import rapid.AckCompletedStatus;
import rapid.AckDataWriter;
import rapid.AckStatus;

import com.rti.dds.infrastructure.InstanceHandle_t;

public class AckPublisher {
	private static final Logger logger = Logger.getLogger(AckPublisher.class);

	protected final String srcName    = AckPublisher.class.getSimpleName();

	protected Ack           executingSample, 
							completedGoodSample, 
							completedBadSample, 
							completedFailedSample;
	protected AckDataWriter sampleWriter;
	protected InstanceHandle_t executingSampleInstance, 
							   completedGoodSampleInstance, 
							   completedBadSampleInstance,
							   completedFailedSampleInstance;

	private static volatile AckPublisher INSTANCE = null;
	private int count = 0;

	public static AckPublisher getInstance() {
		if(INSTANCE == null) {
			synchronized (AckPublisher.class) {
				if (INSTANCE == null) {
					final AckPublisher temp;
					temp = new AckPublisher();
					try {
						temp.createWriters();
						temp.initializeDataTypes();
					} catch (DdsEntityCreationException e) {
						System.err.println(e);
					}
					
					INSTANCE = temp;
				}
			}
		}
		return INSTANCE;
	}

	private AckPublisher() {
	}


	/**
	 * create the endpoints (i.e. readers and writers)
	 */
	public void createWriters() throws DdsEntityCreationException {   	

		sampleWriter = (AckDataWriter)
				RapidEntityFactory.createDataWriter(FreeFlyer.PARTICIPANT_ID, 
						MessageType.ACK_TYPE, 
						FreeFlyer.getPartition());
	}

	/**
	 * initialize the data types that we will be publishing
	 */
	public void initializeDataTypes() {
		final int serialId = 0;

		//-- Initialize an AgentSample
		executingSample = new Ack();
		RapidUtil.setHeader(executingSample.hdr, FreeFlyer.getPartition(), srcName, serialId);
		executingSample.status = AckStatus.ACK_EXECUTING;
		executingSample.completedStatus = AckCompletedStatus.ACK_COMPLETED_NOT;

		completedGoodSample = new Ack();
		RapidUtil.setHeader(completedGoodSample.hdr, FreeFlyer.getPartition(), srcName, serialId);
		completedGoodSample.status = AckStatus.ACK_COMPLETED;
		completedGoodSample.completedStatus = AckCompletedStatus.ACK_COMPLETED_OK;

		completedBadSample = new Ack();
		RapidUtil.setHeader(completedBadSample.hdr, FreeFlyer.getPartition(), srcName, serialId);
		completedBadSample.status = AckStatus.ACK_COMPLETED;
		completedBadSample.completedStatus = AckCompletedStatus.ACK_COMPLETED_BAD_SYNTAX;
		
		completedFailedSample = new Ack();
		RapidUtil.setHeader(completedFailedSample.hdr, FreeFlyer.getPartition(), srcName, serialId);
		completedFailedSample.status = AckStatus.ACK_COMPLETED;
		completedFailedSample.completedStatus = AckCompletedStatus.ACK_COMPLETED_EXEC_FAILED;
		completedFailedSample.message = "Propulsion subsystem inoperable";

		//pl.assign(sample.values.userData);

		//-- register the data instances *after* we have set
		//   assetName and participantName in headers (i.e. the keyed fields)
		if(sampleWriter != null) {
			executingSampleInstance = sampleWriter.register_instance(executingSample);
			//completedGoodSampleInstance = sampleWriter.register_instance(completedGoodSample);
			completedBadSampleInstance = sampleWriter.register_instance(completedBadSample);
			completedFailedSampleInstance = sampleWriter.register_instance(completedFailedSample);
		}
	}

	public void publishExecutingAck(String commandId){
		executingSample.cmdId = commandId;
		RapidUtil.setHeader(executingSample.hdr, FreeFlyer.getPartition(), srcName, count++);
		executingSample.hdr.timeStamp = System.currentTimeMillis();

		publishThisSample(executingSample, executingSampleInstance);
	}
	
	public synchronized void publishCompletedGoodAck(String commandId){
		completedGoodSample.cmdId = commandId;
		RapidUtil.setHeader(completedGoodSample.hdr, FreeFlyer.getPartition(), srcName, count++);
		completedGoodSample.hdr.timeStamp = System.currentTimeMillis();
		
		publishThisSample(completedGoodSample, completedGoodSampleInstance);
	}
	
	public void publishCompletedBadAck(String commandId){
		completedBadSample.cmdId = commandId;
		completedBadSample.message = "";
		
		RapidUtil.setHeader(completedBadSample.hdr, FreeFlyer.getPartition(), srcName, count++);
		completedBadSample.hdr.timeStamp = System.currentTimeMillis();
		
		publishThisSample(completedBadSample, completedBadSampleInstance);
	}
	
	public void publishCompletedFailedAck(String commandId){
		completedFailedSample.cmdId = commandId;
		RapidUtil.setHeader(completedFailedSample.hdr, FreeFlyer.getPartition(), srcName, count++);

		completedFailedSample.hdr.timeStamp = System.currentTimeMillis();

		publishThisSample(completedFailedSample, completedFailedSampleInstance);
	}
	
	public void publishCompletedFailedAck(String commandId, String message){
		completedFailedSample.cmdId = commandId;
		completedFailedSample.message = message;
		RapidUtil.setHeader(completedFailedSample.hdr, FreeFlyer.getPartition(), srcName, count++);

		completedFailedSample.hdr.timeStamp = System.currentTimeMillis();

		publishThisSample(completedFailedSample, completedFailedSampleInstance);
	}
	
	private void publishThisSample(Ack sample, InstanceHandle_t sampleInstance) {
		if(sampleWriter != null) {
			sampleWriter.write(sample, sampleInstance);
		}
		else {
			logger.info("sampleWriter is null");
		}
	}
}





