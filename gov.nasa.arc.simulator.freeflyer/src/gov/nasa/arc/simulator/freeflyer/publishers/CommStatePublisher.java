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

import rapid.ext.astrobee.CommState;
import rapid.ext.astrobee.CommStateDataWriter;

import com.rti.dds.infrastructure.InstanceHandle_t;

public class CommStatePublisher {
	 private static final Logger logger = Logger.getLogger(CommStatePublisher.class);
	    
	private static CommStatePublisher INSTANCE;
	
    private CommState           sample;
    private CommStateDataWriter sampleWriter;
    private InstanceHandle_t    sampleInstance;
    
    protected final String srcName    = DiskStatePublisher.class.getSimpleName();
    
	public static CommStatePublisher getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new CommStatePublisher();
			try {
				INSTANCE.createWriters();
				INSTANCE.initializeDataTypes();
			} catch (DdsEntityCreationException e) {
				System.err.println(e);
			}
		}
		return INSTANCE;
	}
	
	protected CommStatePublisher() {
	}

	/**
	 * create the endpoints (i.e. readers and writers)
	 */
	public void createWriters() throws DdsEntityCreationException {   	

		sampleWriter = (CommStateDataWriter)
				RapidEntityFactory.createDataWriter(FreeFlyer.PARTICIPANT_ID, 
						MessageTypeExtAstro.COMM_STATE_TYPE, 
						FreeFlyer.getPartition());
	}
    
    /**
     * initialize the data types that we will be publishing
     */
    public void initializeDataTypes() {
        final int serialId = 0;

		// -- Initialize a PositionSample
		sample = new CommState();
		RapidUtil.setHeader(sample.hdr, FreeFlyer.getPartition(), srcName, serialId);
		sample.wirelessConnected = true;
		sample.apName = "AVeryExcellentNetwork";
		sample.bssid = "Bessie";
		sample.rssi = 94.5f;
		sample.frequency = 2.4f;
		sample.channel = 5;
		sample.lanConnected = true;
		
        //-- register the data instances *after* we have set
        //   assetName and participantName in headers (i.e. the keyed fields)

        if(sampleWriter != null)
            sampleInstance = sampleWriter.register_instance(sample);
    }

    
    public void publishSample() {
        if(sampleWriter != null) {
            sampleWriter.write(sample, sampleInstance);
        }
        else {
            logger.info("sampleWriter is null");
        }
    }
   
}
