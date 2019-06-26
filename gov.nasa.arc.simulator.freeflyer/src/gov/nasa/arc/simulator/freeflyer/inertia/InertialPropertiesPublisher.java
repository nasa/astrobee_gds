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
package gov.nasa.arc.simulator.freeflyer.inertia;

import gov.nasa.arc.simulator.freeflyer.FreeFlyer;
import gov.nasa.dds.exception.DdsEntityCreationException;
import gov.nasa.rapid.idl.ext.astrobee.message.MessageTypeExtAstro;
import gov.nasa.rapid.v2.e4.system.RapidEntityFactory;
import gov.nasa.rapid.v2.e4.util.RapidUtil;

import org.apache.log4j.Logger;

import rapid.Mat33f;
import rapid.ext.astrobee.InertialProperties;
import rapid.ext.astrobee.InertialPropertiesDataWriter;

import com.rti.dds.infrastructure.InstanceHandle_t;

public class InertialPropertiesPublisher {

	private static final Logger logger = Logger.getLogger(InertialPropertiesPublisher.class);
	private static InertialPropertiesPublisher INSTANCE;

	private InertialProperties           sample;
	private InertialPropertiesDataWriter sampleWriter;
	private InstanceHandle_t    	  sampleInstance;

	protected final String srcName    = InertialPropertiesPublisher.class.getSimpleName();
	String[] topicNames;

	public static InertialPropertiesPublisher getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new InertialPropertiesPublisher();
			try {
				INSTANCE.createWriters();
				INSTANCE.initializeDataTypes();
			} catch (DdsEntityCreationException e) {
				System.err.println(e);
				System.err.print(e.getStackTrace());
			}
		}
		return INSTANCE;
	}

	private InertialPropertiesPublisher() {
	}

	/**
	 * create the endpoints (i.e. readers and writers)
	 */
	public void createWriters() throws DdsEntityCreationException {
		
		sampleWriter = (InertialPropertiesDataWriter)
				RapidEntityFactory.createDataWriter(FreeFlyer.PARTICIPANT_ID, 
						MessageTypeExtAstro.INERTIAL_PROPERTIES_TYPE, 
						FreeFlyer.getPartition());
	}

	/**
	 * initialize the data types that we will be publishing
	 */
	public void initializeDataTypes() {
		final int serialId = 0;

		// -- Initialize a DataToDiskSample
		sample = new InertialProperties();
		RapidUtil.setHeader(sample.hdr, FreeFlyer.getPartition(), srcName, serialId);

		//-- register the data instances *after* we have set
		//   assetName and participantName in headers (i.e. the keyed fields)
		if(sampleWriter != null)
			sampleInstance = sampleWriter.register_instance(sample);
	}

	public void publishSample(String name, float mass, Mat33f matrix) {
		sample.name = name;
		sample.mass = mass;
		sample.matrix = matrix;
		publishSample();
	}

	private void publishSample() {
		if(sampleWriter != null) {
			sampleWriter.write(sample, sampleInstance);
		}
		else {
			logger.info("sampleWriter is null");
		}
	}
}
