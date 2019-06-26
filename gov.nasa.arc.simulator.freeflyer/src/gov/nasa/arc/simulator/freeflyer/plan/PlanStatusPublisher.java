package gov.nasa.arc.simulator.freeflyer.plan;

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

import java.util.Vector;

import gov.nasa.arc.simulator.freeflyer.FreeFlyer;
import gov.nasa.dds.exception.DdsEntityCreationException;
import gov.nasa.rapid.idl.ext.astrobee.message.MessageTypeExtAstro;
import gov.nasa.rapid.v2.e4.system.RapidEntityFactory;
import gov.nasa.rapid.v2.e4.util.RapidUtil;

import org.apache.log4j.Logger;

import rapid.AckCompletedStatus;
import rapid.AckStatus;
import rapid.ext.astrobee.PlanStatus;
import rapid.ext.astrobee.PlanStatusDataWriter;
import rapid.ext.astrobee.Status;

import com.rti.dds.infrastructure.InstanceHandle_t;


public class PlanStatusPublisher {
    private static final Logger logger = Logger.getLogger(PlanStatusPublisher.class);

    protected int numLoops            = 100;
    protected int sleepTime           = 500;
    
    protected final String srcName    = PlanStatusPublisher.class.getSimpleName();

    protected PlanStatus           sample;
    protected PlanStatusDataWriter sampleWriter;
    protected InstanceHandle_t     sampleInstance;

    public PlanStatusPublisher() {
		initializeDataTypes();
		try {
			createWriters();
		} catch (DdsEntityCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    
    /**
     * create the endpoints (i.e. readers and writers)
     */
    public void createWriters() throws DdsEntityCreationException {
       
        sampleWriter = (PlanStatusDataWriter)
                RapidEntityFactory.createDataWriter(FreeFlyer.PARTICIPANT_ID, 
                                                    MessageTypeExtAstro.PLAN_STATUS_TYPE, 
                                                    FreeFlyer.getPartition());
    }

    /**
     * initialize the data types that we will be publishing
     */
    public void initializeDataTypes() {
        final int serialId = 0;
       
        //-- Initialize a PlanStatus
        sample = new PlanStatus();
        RapidUtil.setHeader(sample.hdr, FreeFlyer.getPartition(), srcName, serialId);
        sample = new PlanStatus();
        // makeup some fake data
        sample.planName = "";
        sample.currentPoint = 0;
        sample.currentCommand = -1;
		sample.currentStatus = AckStatus.ACK_COMPLETED;
		
		Status cmdStatus = new Status();
		cmdStatus.point = 0;
		cmdStatus.command = -1;
		cmdStatus.duration = 0;
		cmdStatus.status = AckCompletedStatus.ACK_COMPLETED_NOT;
		sample.statusHistory.userData.add( cmdStatus );
		
        //-- register the data instances *after* we have set
        //   assetName and participantName in headers (i.e. the keyed fields)
        if(sampleWriter != null) {
            sampleInstance = sampleWriter.register_instance(sample);
        }
        
        publishSample();
    }

    int count = 0;
    public void publishSample(PlanStatus status) throws InterruptedException{
    	sample = status;
    	RapidUtil.setHeader(sample.hdr, FreeFlyer.getPartition(), srcName, count++);
    	publishSample();
    }
    
    public void printSample() {
    	System.out.println("*************** - PlanStatusPublisher");
    	System.out.println("PointCommand "+sample.currentPoint + ", " +sample.currentCommand + " is " + sample.currentStatus);
    	
    	for(int i=0; i<sample.statusHistory.userData.size(); i++) {
    		Status s = (Status)sample.statusHistory.userData.get(i);
    		System.out.println("--" + s.point + ", " + s.command + " - " + s.status);
    	}
    	System.out.println("***************");
    }
    
    public void publishSample() {
    	sample.hdr.timeStamp = System.currentTimeMillis();
        if(sampleWriter != null) {
        	printSample();
        	
            sampleWriter.write(sample, sampleInstance);
        }
        else {
            logger.info("sampleWriter is null");
        }
    }
  
    /**
     * publish the data 
     */
    public void publishSamples() throws InterruptedException {
        //-- Start the Sample send loop
        for(int i = 0; i < numLoops; i++) {
            if(sampleWriter != null) {
                sampleWriter.write(sample, sampleInstance);
                logger.info("Published PlanStatus "+(i+1)+" of "+numLoops+"...");
            }
            else {
                logger.info("sampleWriter is null");
            }
            Thread.sleep(sleepTime);
        }
        logger.info("Done, exiting.");
        System.out.println("Done, exiting.");
    }
}
