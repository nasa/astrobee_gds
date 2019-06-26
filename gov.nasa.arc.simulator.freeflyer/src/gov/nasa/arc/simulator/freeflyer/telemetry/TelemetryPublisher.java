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
package gov.nasa.arc.simulator.freeflyer.telemetry;

import gov.nasa.arc.simulator.freeflyer.FreeFlyer;
import gov.nasa.arc.simulator.freeflyer.subsystem.image.CameraImagePublisher;
import gov.nasa.dds.exception.DdsEntityCreationException;
import gov.nasa.rapid.idl.ext.astrobee.message.MessageTypeExtAstro;
import gov.nasa.rapid.v2.e4.system.RapidEntityFactory;
import gov.nasa.rapid.v2.e4.util.RapidUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import rapid.Command;
import rapid.IMAGESENSOR_SAMPLE_TOPIC;
import rapid.ParameterUnion;
import rapid.ext.astrobee.CameraInfoConfig;
import rapid.ext.astrobee.CameraMode;
import rapid.ext.astrobee.SETTINGS_CAMERA_NAME_DOCK;
import rapid.ext.astrobee.SETTINGS_CAMERA_NAME_NAV;
import rapid.ext.astrobee.SETTINGS_METHOD_SET_CAMERA;
import rapid.ext.astrobee.SETTINGS_METHOD_SET_CAMERA_STREAMING;
import rapid.ext.astrobee.SETTINGS_METHOD_SET_TELEMETRY_RATE;
import rapid.ext.astrobee.SETTINGS_TELEMETRY_TYPE_COMM_STATUS;
import rapid.ext.astrobee.SETTINGS_TELEMETRY_TYPE_DISK_STATE;
import rapid.ext.astrobee.SETTINGS_TELEMETRY_TYPE_EKF_STATE;
import rapid.ext.astrobee.SETTINGS_TELEMETRY_TYPE_POSITION;
import rapid.ext.astrobee.TelemetryConfig;
import rapid.ext.astrobee.TelemetryConfigDataWriter;
import rapid.ext.astrobee.TelemetryState;
import rapid.ext.astrobee.TelemetryStateDataWriter;

import com.rti.dds.infrastructure.InstanceHandle_t;

/**
 * Publishes TelemetryConfig and TelemetryState
 * 
 * @author ddwheele
 */
public class TelemetryPublisher {
	private static final Logger logger = Logger.getLogger(TelemetryPublisher.class);
	private static TelemetryPublisher INSTANCE;

	private TelemetryConfig           config;
	private TelemetryConfigDataWriter configWriter;
	private InstanceHandle_t     	  configInstance;

	private TelemetryState           sample;
	private TelemetryStateDataWriter sampleWriter;
	private InstanceHandle_t    	 sampleInstance;

	protected final String srcName    = TelemetryPublisher.class.getSimpleName();

	private final String IMAGES_DIRECTORY = "files"+File.separator+"images" +File.separator;

	private String[] cameraNames = {SETTINGS_CAMERA_NAME_NAV.VALUE, SETTINGS_CAMERA_NAME_DOCK.VALUE};
	private CameraMode[] cameraModes = {CameraMode.MODE_VIDEO, CameraMode.MODE_FRAMES};

	private Map<String,CameraImagePublisher> cameraPublishers = new HashMap<String,CameraImagePublisher>();

	public static TelemetryPublisher getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new TelemetryPublisher();
			try {
				INSTANCE.setupCameraPublishers();
				INSTANCE.createWriters();
				INSTANCE.initializeDataTypes();
				INSTANCE.publishConfig();
				INSTANCE.publishSample();

			} catch (DdsEntityCreationException e) {
				System.err.println(e);
			}
		}
		return INSTANCE;
	}

	protected TelemetryPublisher() {

	}

	private void setupCameraPublishers() {
		for(int i=0; i<cameraNames.length; i++) {
			cameraPublishers.put(cameraNames[i],
					new CameraImagePublisher(cameraNames[i],cameraModes[i],
							IMAGESENSOR_SAMPLE_TOPIC.VALUE+"-"+cameraNames[i],
							IMAGES_DIRECTORY + cameraNames[i]));
		}
	}

	/**
	 * create the endpoints (i.e. readers and writers)
	 */
	public void createWriters() throws DdsEntityCreationException {

		//-- Create the data writers. The Publisher is created automatically
		configWriter = (TelemetryConfigDataWriter)
				RapidEntityFactory.createDataWriter(FreeFlyer.PARTICIPANT_ID,
						MessageTypeExtAstro.TELEMETRY_CONFIG_TYPE,
						FreeFlyer.getPartition());
		sampleWriter = (TelemetryStateDataWriter)
				RapidEntityFactory.createDataWriter(FreeFlyer.PARTICIPANT_ID, 
						MessageTypeExtAstro.TELEMETRY_STATE_TYPE, 
						FreeFlyer.getPartition());
	}

	/**
	 * initialize the data types that we will be publishing
	 */
	public void initializeDataTypes() {
		final int serialId = 0;
		//-- Initialize a TelemetryConfig
		config = new TelemetryConfig();
		RapidUtil.setHeader(config.hdr, FreeFlyer.getPartition(), srcName, serialId);

		// -- Initialize a TelemetryState
		sample = new TelemetryState();
		RapidUtil.setHeader(sample.hdr, FreeFlyer.getPartition(), srcName, serialId);
		sample.positionRate = 5;
		sample.ekfStateRate = 7;
		sample.commStatusRate = 8;
		sample.diskStateRate = 17;

		// Can't just iterate through the set because we need a guaranteed order
		for(int i=0; i<cameraNames.length; i++) {
			CameraImagePublisher camPub = cameraPublishers.get(cameraNames[i]);

			CameraInfoConfig cic = new CameraInfoConfig();
			cic.name = camPub.name;
			cic.mode = camPub.mode;
			cic.availResolutions.userData.addAll(camPub.getAvailableResolutions());
			config.cameras.userData.add(cic);

			sample.cameras.userData.add(camPub.getCameraInfo());
		}

		//-- register the data instances *after* we have set
		//   assetName and participantName in headers (i.e. the keyed fields)
		if(configWriter != null)
			configInstance = configWriter.register_instance(config);
		if(sampleWriter != null)
			sampleInstance = sampleWriter.register_instance(sample);
	}

	public void changeTelemetryRateAndPublishSample(Command cmd) {
		if(cmd.cmdName.equals(SETTINGS_METHOD_SET_TELEMETRY_RATE.VALUE)) {
			String name = ((ParameterUnion)cmd.arguments.userData.get(0)).s();
			float rate = ((ParameterUnion)cmd.arguments.userData.get(1)).f();

			if(name.equals(SETTINGS_TELEMETRY_TYPE_POSITION.VALUE)) {
				sample.positionRate = rate;
			} else if(name.equals(SETTINGS_TELEMETRY_TYPE_EKF_STATE.VALUE)) {
				sample.ekfStateRate = rate;
			} else if(name.equals(SETTINGS_TELEMETRY_TYPE_COMM_STATUS.VALUE)) {
				sample.commStatusRate = rate;
			} else if(name.equals(SETTINGS_TELEMETRY_TYPE_DISK_STATE.VALUE)) {
				sample.diskStateRate = rate;
			}
		}
		publishSample();
	}
	
	public void setCameraAndPublishSample(Command cmd) {
		if(cmd.cmdName.equals(SETTINGS_METHOD_SET_CAMERA.VALUE)) {
			String nameFromCommand = ((ParameterUnion)cmd.arguments.userData.get(0)).s();

			// get the right CameraImagePublisher and give it the command
			CameraImagePublisher cip = cameraPublishers.get(nameFromCommand);
			cip.executeSetCameraParamsCommand(cmd);

			// update CameraInfo
			// this is lame but I'm not sure there's a better way
			for(int i=0; i<cameraNames.length; i++) {
				if(cameraNames[i].equals(nameFromCommand)) {
					sample.cameras.userData.set(i, cip.getCameraInfo());
				}
			}
		}
		publishSample();
	}

	public void setCameraStreamingAndPublishSample(Command cmd) {
		if(cmd.cmdName.equals(SETTINGS_METHOD_SET_CAMERA_STREAMING.VALUE)) {
			String nameFromCommand = ((ParameterUnion)cmd.arguments.userData.get(0)).s();

			// get the right CameraImagePublisher and give it the command
			CameraImagePublisher cip = cameraPublishers.get(nameFromCommand);
			if(cip == null) {
				return;
			}
			cip.executeSetCameraStreamingCommand(cmd);

			// update CameraInfo
			// this is lame but I'm not sure there's a better way
			for(int i=0; i<cameraNames.length; i++) {
				if(cameraNames[i].equals(nameFromCommand)) {
					sample.cameras.userData.set(i, cip.getCameraInfo());
				}
			}
		}
		publishSample();
	}

	public void publishSample() {
		if(sampleWriter != null) {
			sampleWriter.write(sample, sampleInstance);
		}
		else {
			logger.info("sampleWriter is null");
		}
	}

	public void publishConfig() {
		//-- publish the Config
		logger.info("Publishing TelemetryConfig...");
		configWriter.write(config, configInstance);
	}
}
