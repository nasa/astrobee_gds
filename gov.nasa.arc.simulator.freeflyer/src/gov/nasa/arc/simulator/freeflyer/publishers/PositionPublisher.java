package gov.nasa.arc.simulator.freeflyer.publishers;

/*******************************************************************************
 * Copyright (c) 2014 United States Government as represented by the 
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
import gov.nasa.arc.simulator.freeflyer.subsystem.command.TeleopSubsystem;
import gov.nasa.dds.exception.DdsEntityCreationException;
import gov.nasa.rapid.v2.e4.message.MessageType;
import gov.nasa.rapid.v2.e4.system.RapidEntityFactory;
import gov.nasa.rapid.v2.e4.util.RapidUtil;

import java.util.Random;

import org.apache.log4j.Logger;

import rapid.POSITION_CONFIG_TOPIC;
import rapid.POSITION_SAMPLE_TOPIC;
import rapid.PositionConfig;
import rapid.PositionConfigDataWriter;
import rapid.PositionSample;
import rapid.PositionSampleDataWriter;
import rapid.RotationEncoding;

import com.rti.dds.infrastructure.InstanceHandle_t;

/**
 * Publishes PositionConfig and PositionSamples
 */
public class PositionPublisher {
	private static final Logger logger = Logger.getLogger(PositionPublisher.class);
	private static PositionPublisher INSTANCE;   
	private final Random randomGenerator = new Random();

	protected int sleepTime           = 100;
	public static final int MESSAGE_LIFECYCLE_LIMIT = 100;

	protected final String srcName    = PositionPublisher.class.getSimpleName();

	protected PositionConfig           config;
	protected PositionConfigDataWriter configWriter;
	protected InstanceHandle_t         configInstance;

	protected PositionSample           sample;
	protected PositionSampleDataWriter sampleWriter;
	protected InstanceHandle_t         sampleInstance;

	public static PositionPublisher getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new PositionPublisher();
			try {
				INSTANCE.createWriters(POSITION_CONFIG_TOPIC.VALUE, POSITION_SAMPLE_TOPIC.VALUE);
				INSTANCE.initializeDataTypes();
				INSTANCE.publishConfig();
			} catch (DdsEntityCreationException e) {
				System.err.println(e);
			}
		}
		return INSTANCE;
	}

	protected PositionPublisher() {
	}

	/**
	 * create the endpoints (i.e. readers and writers)
	 */
	public void createWriters(final String positionConfigTopicName,String positionSampleTopicName) throws DdsEntityCreationException {

		//override default topic name
		MessageType.POSITION_CONFIG_TYPE.setTopicName(positionConfigTopicName);
		MessageType.POSITION_SAMPLE_TYPE.setTopicName(positionSampleTopicName);

		//-- Create the data writers. The Publisher is created automatically
		configWriter = (PositionConfigDataWriter)
				RapidEntityFactory.createDataWriter(FreeFlyer.PARTICIPANT_ID,
						MessageType.POSITION_CONFIG_TYPE,
						FreeFlyer.getPartition());
		sampleWriter = (PositionSampleDataWriter)
				RapidEntityFactory.createDataWriter(FreeFlyer.PARTICIPANT_ID, 
						MessageType.POSITION_SAMPLE_TYPE, 
						FreeFlyer.getPartition());
	}

	public void initializeDataTypes() {
		final int serialId = 0;
		//-- Initialize a PositionConfig
		config = new PositionConfig();
		RapidUtil.setHeader(config.hdr, FreeFlyer.getPartition(), srcName, serialId);
		config.frameName = FreeFlyer.getPartition();
		config.poseEncoding = RotationEncoding.RAPID_ROT_QUAT;

		// -- Initialize a PositionSample
		sample = new PositionSample();
		RapidUtil.setHeader(sample.hdr, FreeFlyer.getPartition(), srcName, serialId);

		// start it at the origin
		sample.pose.xyz.userData[0]= 0;
		sample.pose.xyz.userData[1]= 0;
		sample.pose.xyz.userData[2]= 0;

		// set rotation matrix to identity
		sample.pose.rot.userData[0] = 0;
		sample.pose.rot.userData[1] = 0;
		sample.pose.rot.userData[2] = 0;
		sample.pose.rot.userData[3] = 1;

		// set rotation matrix to identity
		sample.velocity.rot.userData[0] = 1;
		sample.velocity.rot.userData[4] = 1;
		sample.velocity.rot.userData[8] = 1;


		//-- register the data instances *after* we have set
		//   assetName and participantName in headers (i.e. the keyed fields)
		if(configWriter != null)
			configInstance = configWriter.register_instance(config);
		if(sampleWriter != null)
			sampleInstance = sampleWriter.register_instance(sample);
	}

	public void publishConfig() {
		//-- publish the Config
		logger.info("Publishing PositionConfig...");
		configWriter.write(config, configInstance);
	}

	synchronized public void publishSample(final float x, final float y, final float z){
		sample.pose.xyz.userData[0]= x;
		sample.pose.xyz.userData[1]= y;
		sample.pose.xyz.userData[2]= z;

		republishSample();
	}

	synchronized public void publishSample(final float x, final float y, final float z, 
			final float qx, final float qy, final float qz, final float qw){
		sample.pose.xyz.userData[0]= x;
		sample.pose.xyz.userData[1]= y;
		sample.pose.xyz.userData[2]= z;

		sample.pose.rot.userData[0] = qx;
		sample.pose.rot.userData[1] = qy;
		sample.pose.rot.userData[2] = qz;
		sample.pose.rot.userData[3] = qw;

		republishSample();
	}

	synchronized public void publishSample(final double[] offsets, int seed) throws InterruptedException {
		sample.pose.xyz.userData[0]= Math.sin(0.1*seed)+offsets[0];
		sample.pose.xyz.userData[1]= Math.cos(0.1*seed)+offsets[1];
		sample.pose.xyz.userData[2]= Math.sin(0.01*seed)+offsets[2];

		republishSample();
	}

	public void publishTelemetry(final float x, final float y, final float z) {
		sample.pose.xyz.userData[0]= x;
		sample.pose.xyz.userData[1]= y;
		sample.pose.xyz.userData[2]= z;
		
		Runnable t = new Runnable() {
			@Override
			public void run() {
				while(true) {
					republishSample();

					try {
						Thread.sleep(4500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		(new Thread(t)).start();
	}

	synchronized private void republishSample() {
		sample.hdr.timeStamp = System.currentTimeMillis();

		if(sampleWriter != null) {
			sampleWriter.write(sample, sampleInstance);
		}
		else {
			logger.info("sampleWriter is null");
		}
	}

}
