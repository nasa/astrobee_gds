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
package gov.nasa.arc.simulator.freeflyer.subsystem.accesscontrol;

import gov.nasa.arc.simulator.freeflyer.FreeFlyer;
import gov.nasa.dds.exception.DdsEntityCreationException;
import gov.nasa.rapid.v2.e4.message.MessageType;
import gov.nasa.rapid.v2.e4.system.RapidEntityFactory;
import gov.nasa.rapid.v2.e4.util.RapidUtil;

import java.util.Random;

import org.apache.log4j.Logger;

import rapid.AccessControlState;
import rapid.AccessControlStateDataWriter;
import rapid.Command;
import rapid.ParameterUnion;
import rapid.ext.astrobee.ACCESSCONTROL_METHOD_GRAB_CONTROL;

import com.rti.dds.infrastructure.InstanceHandle_t;

public class AccessControlSubsystem {
	private static final Logger logger = Logger.getLogger(AccessControlSubsystem.class);
	private static AccessControlSubsystem INSTANCE;
	protected final String srcName    = AccessControlSubsystem.class.getSimpleName();

	protected AccessControlState           sample;
	protected AccessControlStateDataWriter sampleWriter;
	protected InstanceHandle_t    		   sampleInstance;
	private String alphabet = "abcdefghijklmnopqrstuvwxyz";
	
	private String lastCookie = "";
	private int count = 0;
	
	public static AccessControlSubsystem getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new AccessControlSubsystem();
			try {
				INSTANCE.createWriters();
				INSTANCE.initializeDataTypes();
			} catch (DdsEntityCreationException e) {
				System.err.println(e);
			}
		}
		return INSTANCE;
	}
	
	private AccessControlSubsystem() {
	}

	/**
	 * create the endpoints (i.e. readers and writers)
	 */
	public void createWriters() throws DdsEntityCreationException {   	

		sampleWriter = (AccessControlStateDataWriter)
				RapidEntityFactory.createDataWriter(FreeFlyer.PARTICIPANT_ID, 
						MessageType.ACCESSCONTROL_STATE_TYPE, 
						FreeFlyer.getPartition());
	}
	

	/**
	 * initialize the data types that we will be publishing
	 */
	public void initializeDataTypes() {
		final int serialId = 0;

		//-- Initialize an AgentSample
		sample = new AccessControlState();
		RapidUtil.setHeader(sample.hdr, FreeFlyer.getPartition(), srcName, serialId);
		sample.controller = "No Controller";
		//pl.assign(sample.values.userData);

		//-- register the data instances *after* we have set
		//   assetName and participantName in headers (i.e. the keyed fields)
		if(sampleWriter != null)
			sampleInstance = sampleWriter.register_instance(sample);
	}

	public boolean grabControl(Command cmd) {
		if(cmd.cmdName.equals(ACCESSCONTROL_METHOD_GRAB_CONTROL.VALUE)) {
			ParameterUnion pu = (ParameterUnion)cmd.arguments.userData.get(0);
			String trialCookie = pu.s();
			logger.warn("Last Cookie = " + lastCookie + ", submitted cookie = " + trialCookie);
			if(lastCookie.equals(trialCookie)) {
				sample.controller = cmd.cmdSrc;
				publishSample();
				return true;
			}
		}
		return false;
	}
	
	private String generateRandomString() {
		StringBuffer buf = new StringBuffer();
		for(int i=0; i<10; i++) {
			int random = new Random().nextInt(26);
			String next = alphabet.substring(random, random+1);
			buf.append(next);
		}
		return buf.toString();
	}
	
	public void sendCookie() {
		// send an AccessControlState message with requestors[0] as key string
		lastCookie = generateRandomString();
		System.out.println("lastCookie = " + lastCookie);
		sample.requestors.userData.clear();
		sample.requestors.userData.add(lastCookie);
		publishSample();
	}
	
	public void publishSample() {
		sample.hdr.timeStamp = System.currentTimeMillis();
		RapidUtil.setHeader(sample.hdr, FreeFlyer.getPartition(), srcName, count++);

		if(sampleWriter != null) {
			sampleWriter.write(sample, sampleInstance);
		}
		else {
			logger.info("sampleWriter is null");
		}
	}
}
