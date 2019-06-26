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

import gov.nasa.arc.irg.plan.freeflyer.command.AbstractGuestScienceCommand;
import gov.nasa.arc.simulator.freeflyer.FreeFlyer;
import gov.nasa.dds.exception.DdsEntityCreationException;
import gov.nasa.rapid.idl.ext.astrobee.message.MessageTypeExtAstro;
import gov.nasa.rapid.v2.e4.system.RapidEntityFactory;
import gov.nasa.rapid.v2.e4.util.RapidUtil;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import rapid.ext.astrobee.GuestScienceData;
import rapid.ext.astrobee.GuestScienceDataDataWriter;
import rapid.ext.astrobee.GuestScienceDataType;

import com.rti.dds.infrastructure.InstanceHandle_t;

public class GuestScienceDataPublisher {
	private static final Logger logger = Logger.getLogger(GuestScienceDataPublisher.class);
			
	protected int sleepTime           = 5000;

	protected final String srcName    = GuestScienceDataPublisher.class.getSimpleName();

	protected GuestScienceData 			 sample;
	protected GuestScienceDataDataWriter sampleWriter;
	
	protected InstanceHandle_t     sampleInstance;
	
	private static GuestScienceDataPublisher INSTANCE;
	
	protected static SimulatorGuestScienceManager manager;
	
	private boolean publishSummary = false;
	private String sampleTopic = "Msg";
	
	public static GuestScienceDataPublisher getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new GuestScienceDataPublisher();
			try {
				INSTANCE.createWriters();
				INSTANCE.initializeDataTypes();
			} catch (DdsEntityCreationException e) {
				System.err.println(e);
			}
		}
		return INSTANCE;
	}
	
	private GuestScienceDataPublisher() {}
	
	/**
	 * create the endpoints (i.e. readers and writers)
	 */
	public void createWriters() throws DdsEntityCreationException {   	
		sampleWriter = (GuestScienceDataDataWriter)
				RapidEntityFactory.createDataWriter(FreeFlyer.PARTICIPANT_ID, 
						MessageTypeExtAstro.GUEST_SCIENCE_DATA_TYPE, 
						FreeFlyer.getPartition());
	}

	/**
	 * initialize the data types that we will be publishing
	 */
	public void initializeDataTypes() {
		final int serialId = 0;

		//-- Initialize an AgentSample
		sample = new rapid.ext.astrobee.GuestScienceData();
		RapidUtil.setHeader(sample.hdr, FreeFlyer.getPartition(), srcName, serialId);

		manager = SimulatorGuestScienceManager.getInstance();

		if(sampleWriter != null)
			sampleInstance = sampleWriter.register_instance(sample);
	}
	
	public void publishData(String apkName) throws InterruptedException{
		
		sample.hdr.timeStamp = System.currentTimeMillis();
		sample.apkName = apkName;
        if(sampleWriter != null) {
            sampleWriter.write(sample, sampleInstance);
            publishSummary = true;
            startPublisher();
        }
        else {
            logger.info("sampleWriter is null");
        }
	}
	
	public void publishData(AbstractGuestScienceCommand command) throws InterruptedException{
		
		sample.hdr.timeStamp = System.currentTimeMillis();
		sample.apkName = command.getApkName();
        if(sampleWriter != null) {
            sampleWriter.write(sample, sampleInstance);
            publishSummary = true;
            startPublisher();
        }
        else {
            logger.info("sampleWriter is null");
        }
	}
	
	public void stopSummaryPublishing(){
		publishSummary = false;
	}
	
	
	public void sendManuallyUploadSuccess(final String apk, final String cmd){
		new Thread(new Runnable() {
			public void run() {
				final JSONObject jsonData = new JSONObject();
				sample.hdr.timeStamp = System.currentTimeMillis();
				sample.topic = sampleTopic;
				sample.type = GuestScienceDataType.GUEST_SCIENCE_JSON;
				jsonData.clear();
				jsonData.put("Summary", new String(cmd + " Executed"));
				sample.data.userData.clear();
				sample.data.userData.addAllByte(jsonData.toJSONString().getBytes());
				sample.apkName = apk;
				if(sampleWriter != null) {
					sampleWriter.write(sample, sampleInstance);
				}
				else {
					logger.info("sampleWriter is null");
				}
				try {
					Thread.sleep(sleepTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	public void startPublisher() throws InterruptedException {

		new Thread(new Runnable() {
			public void run() {
				final JSONObject jsonData = new JSONObject();
				int i = 0;
				while(publishSummary){
				    System.out.println(sample.apkName + " publishing data " + i);
					//i = i+1;
					sample.hdr.timeStamp = System.currentTimeMillis();
					sample.topic = sampleTopic;
					sample.type = GuestScienceDataType.GUEST_SCIENCE_JSON;
					jsonData.clear();
					jsonData.put("Summary", new String("Power Up "+i));
					jsonData.put("Status", new String("Off"));
					jsonData.put("Data", new Integer(i));
					logger.info(sample.apkName+", "+sampleTopic+": "+i);
					
					sample.data.userData.clear();
					sample.data.userData.addAllByte(jsonData.toJSONString().getBytes());
					i++;
					if(sampleWriter != null) {
						sampleWriter.write(sample, sampleInstance);
					}
					else {
						logger.info("sampleWriter is null");
					}
					try {
						Thread.sleep(sleepTime);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
}
