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
import gov.nasa.arc.simulator.freeflyer.publishers.AgentStatePublisher;
import gov.nasa.dds.exception.DdsEntityCreationException;
import gov.nasa.rapid.idl.ext.astrobee.message.MessageTypeExtAstro;
import gov.nasa.rapid.v2.e4.message.publisher.RapidMessagePublisher;
import gov.nasa.rapid.v2.e4.system.RapidEntityFactory;
import gov.nasa.rapid.v2.e4.util.RapidUtil;

import org.apache.log4j.Logger;

import rapid.ext.astrobee.CompressedFileAck;
import rapid.ext.astrobee.CompressedFileAckDataWriter;

import com.rti.dds.infrastructure.InstanceHandle_t;

public class CompressedFileAckPublisher {
	
	private static final Logger logger = Logger.getLogger(AgentStatePublisher.class);
	private static CompressedFileAckPublisher INSTANCE;
	private InstanceHandle_t   sampleInstance;
	private CompressedFileAckDataWriter sampleWriter;
	protected RapidMessagePublisher rapidMessagePublisher;
	protected CompressedFileAck sample;
	
	protected final String srcName = CompressedFileAckPublisher.class.getSimpleName();


	
	public static CompressedFileAckPublisher getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new CompressedFileAckPublisher();
			try {
				INSTANCE.initializeDataTypes();
				INSTANCE.createWriters();
			} catch (DdsEntityCreationException e) {
				System.err.println(e);
				System.err.print(e.getStackTrace());
			}
		}
		return INSTANCE;
	}

	
	 public void createWriters() throws DdsEntityCreationException {

		 //-- Create the data writers. The Publisher is created automatically
		 sampleWriter = (CompressedFileAckDataWriter)
				 RapidEntityFactory.createDataWriter(FreeFlyer.PARTICIPANT_ID, 
						 MessageTypeExtAstro.COMPRESSED_FILE_ACK_TYPE, 
						 FreeFlyer.getPartition());
	 }

	 /**
	  * initialize the data types that we will be publishing
	  */
	 public void initializeDataTypes() {
		 final int serialId = 0;
		 sample = new CompressedFileAck();
		 RapidUtil.setHeader(sample.hdr, FreeFlyer.getPartition(), srcName, serialId);
		 sample.id = (int) System.currentTimeMillis();
		 
		 //-- register the data instances *after* we have set
		 //   assetName and participantName in headers (i.e. the keyed fields)
		 if(sampleWriter != null)
			 sampleInstance = sampleWriter.register_instance(sample);
	 }


	 public void publishFileAckSample(int id) {
		 try{
			 sample.hdr.timeStamp = System.currentTimeMillis();

				if(sampleWriter != null) {
					sample.id = id;
					sampleWriter.write(sample, sampleInstance);
				}
				else {
					logger.info("sampleWriter is null");
				} 
		 }catch(Exception e){
			logger.error(e); 
		 }
			
	}

}
