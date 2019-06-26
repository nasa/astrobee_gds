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
package gov.nasa.arc.simulator.freeflyer.subsystem.image;

import gov.nasa.arc.simulator.freeflyer.FreeFlyer;
import gov.nasa.dds.exception.DdsEntityCreationException;
import gov.nasa.rapid.v2.e4.message.MessageType;
import gov.nasa.rapid.v2.e4.system.RapidEntityFactory;
import gov.nasa.rapid.v2.e4.util.RapidUtil;

import java.io.ByteArrayOutputStream;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;

import rapid.Command;
import rapid.ImageSensorSample;
import rapid.ImageSensorSampleDataWriter;
import rapid.MIME_IMAGE_JPEG;
import rapid.ParameterUnion;
import rapid.ext.astrobee.CameraInfo;
import rapid.ext.astrobee.CameraMode;
import rapid.ext.astrobee.CameraResolution;
import rapid.ext.astrobee.SETTINGS_METHOD_SET_CAMERA;
import rapid.ext.astrobee.SETTINGS_METHOD_SET_CAMERA_STREAMING;

import com.rti.dds.infrastructure.InstanceHandle_t;

public class CameraImagePublisher {

	private static final Logger logger = Logger.getLogger(CameraImagePublisher.class);

	public final String name;
	public final CameraMode mode;
	protected int sleepTime           = 500;
	protected final String srcName    = FreeFlyer.getPartition(); 

	protected ImageSensorSample        sample;
	protected InstanceHandle_t         sampleInstance;
	protected ImageSensorSampleDataWriter sampleWriter;

	protected ImageSetByResolution images;
//	protected CameraResolutionPlus currentResolution;
//	protected boolean currentlyStreaming = false;

	protected Thread streamingThread;
	
	protected CameraInfo camInfo;

	public CameraImagePublisher(String name, CameraMode mode, String topicName, String imgDir){
		this.name = name;
		this.mode = mode;
		
		try{
			images = new ImageSetByResolution(imgDir);
			createWriters(topicName);
			initializeDataTypes();
			initializeCameraInfo();
			
		} catch (final DdsEntityCreationException e) {
			e.printStackTrace();
		}
	}
	
	private void initializeCameraInfo() {
		camInfo = new CameraInfo();
		camInfo.streaming = false;
		camInfo.resolution = (CameraResolution) images.getResolutions().toArray()[0];
		camInfo.frameRate = 0;
		camInfo.bandwidth = 0;
	}

	/**
	 * We initialize our data types, but we do not need to manage instance handles
	 */
	public void initializeDataTypes() {
		final int serialId = 0;
		try {
			//-- Initialize an ImageSensorSample 
			sample = new ImageSensorSample();
			RapidUtil.setHeader(sample.hdr, FreeFlyer.getPartition(), srcName, serialId);
			if(sampleWriter != null)
				sampleInstance = sampleWriter.register_instance(sample);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	public void createWriters(final String topicName) throws DdsEntityCreationException {
		MessageType.IMAGESENSOR_SAMPLE_TYPE.setTopicName(topicName);
		sampleWriter = (ImageSensorSampleDataWriter)
				RapidEntityFactory.createDataWriter(FreeFlyer.PARTICIPANT_ID, 
						MessageType.IMAGESENSOR_SAMPLE_TYPE, 
						FreeFlyer.getPartition());
	}
	
	public void executeSetCameraStreamingCommand(Command cmd) {
		if(!cmd.cmdName.equals(SETTINGS_METHOD_SET_CAMERA_STREAMING.VALUE))  {
			return;
		}
		
		String camName = ((ParameterUnion)cmd.arguments.userData.get(0)).s();
		if(!name.equals(camName)) {
			return;
		}
		
		// Stream
		boolean stream = ((ParameterUnion)cmd.arguments.userData.get(1)).b();
		
		if(stream) {
			startPublishing();
		} else {
			stopPublishing();
		}
	}
	
	public void executeSetCameraParamsCommand(Command cmd) {
		if(!cmd.cmdName.equals(SETTINGS_METHOD_SET_CAMERA.VALUE))  {
			return;
		}
		String camName = ((ParameterUnion)cmd.arguments.userData.get(0)).s();
		if(!name.equals(camName)) {
			return;
		}
		boolean wasStreaming = camInfo.streaming;
		if(wasStreaming) {
			stopPublishing();
		}
		
		// Resolution
		String resString = ((ParameterUnion)cmd.arguments.userData.get(1)).s();
		CameraResolutionPlus res = CameraResolutionPlus.fromString(resString);
		if(!camInfo.resolution.equals(res.getCameraResolution())) {
			camInfo.resolution = res.getCameraResolution();
		}
		
		// Framerate
		camInfo.frameRate = ((ParameterUnion)cmd.arguments.userData.get(2)).f();
		
		// Bandwidth
		camInfo.bandwidth = ((ParameterUnion)cmd.arguments.userData.get(3)).f();
	
		if(wasStreaming) {
			startPublishing();
		}
	}

	private void startPublishing() {
		if(streamingThread != null && streamingThread.isAlive()) {
			streamingThread.interrupt();
		}
		Runnable r = new Runnable() {
			public void run() {
				try {
					publishSamples();
				}
				catch(Exception e) {
					//throw new RuntimeException(e);
					// just need to stop the thread, don't need to do anything else
				}
			}
		};
		streamingThread = new Thread(r);
		streamingThread.start();
		camInfo.streaming = true;
	}

	private void stopPublishing() {
		streamingThread.interrupt();
		camInfo.streaming = false;
	}
	/**
	 * publish the data 
	 */
	public void publishSamples() throws Exception {
		// get vector
		Vector<ImageData> pics = images.getImages(camInfo.resolution);
		while(true) {
			//-- Start the Sample send loop
			for(ImageData imgData : pics) {
				ImageLoader imageLoader = new ImageLoader();
				imageLoader.data = new ImageData[] {imgData};
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				imageLoader.save(stream, SWT.IMAGE_JPEG);
				byte[] databytes = stream.toByteArray();

				sample.mimeType = MIME_IMAGE_JPEG.VALUE;
				sample.meta.width = imgData.width;
				sample.meta.height = imgData.height;
				sample.hdr.timeStamp = System.currentTimeMillis();

				sample.data.userData.clear();
				sample.data.userData.addAllByte(databytes);
				//rapidPub.writeMessage(FreeFlyer.PARTICIPANT_ID,MessageType.IMAGESENSOR_SAMPLE_TYPE, sample);
				sampleWriter.write(sample, sampleInstance);

				Thread.sleep(sleepTime);
			}
			logger.debug("Done, exiting.");
		}
	}
	
	public CameraInfo getCameraInfo() {
		return camInfo;
	}
	
	public Vector<CameraResolution> getAvailableResolutions() {
		Vector<CameraResolution> ret = new Vector<CameraResolution>();
		for(CameraResolution camRes : images.getResolutions()) {
			ret.add(camRes);
		}
		return ret;
	}
}