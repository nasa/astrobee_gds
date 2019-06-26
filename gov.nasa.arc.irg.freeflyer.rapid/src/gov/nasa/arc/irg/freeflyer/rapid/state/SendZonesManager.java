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
package gov.nasa.arc.irg.freeflyer.rapid.state;

import gov.nasa.arc.irg.freeflyer.rapid.CommandPublisher;
import gov.nasa.arc.irg.freeflyer.rapid.CompressedFilePublisher;
import gov.nasa.rapid.idl.ext.astrobee.message.MessageTypeExtAstro;
import gov.nasa.rapid.v2.e4.Rapid;
import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.e4.message.IRapidMessageListener;
import gov.nasa.rapid.v2.e4.message.MessageType;
import gov.nasa.rapid.v2.e4.message.collector.RapidMessageCollector;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import rapid.ext.astrobee.SETTINGS;
import rapid.ext.astrobee.SETTINGS_METHOD_SET_ZONES;

public class SendZonesManager implements IRapidMessageListener {
	private static final Logger logger = Logger.getLogger(SendZonesManager.class);
	protected boolean waitingToSendSetZonesCommand = false;
	protected Agent agent;
	protected MessageType[] sampleType;
	protected String participantId = Rapid.PrimaryParticipant;
	protected CommandPublisher commandPublisher;
	
	protected static Map<Agent,SendZonesManager> managers = new HashMap<Agent,SendZonesManager>();
	
	public static SendZonesManager get(Agent agent) {
		if(managers.get(agent) == null) {
			managers.put(agent, new SendZonesManager(agent));
		}
		return managers.get(agent);
	}
	
	private SendZonesManager(Agent agent) {
		this.agent = agent;
		commandPublisher = CommandPublisher.getInstance(agent);
		init();
		subscribe();
	}
	
	protected void init() {
		sampleType = new MessageType[] {
				MessageTypeExtAstro.COMPRESSED_FILE_ACK_TYPE
		};
	}
	
	@Override
	public void onRapidMessageReceived(Agent agent, MessageType msgType,
			Object msgObj, Object cfgObj) {
		if(msgType.equals(MessageTypeExtAstro.COMPRESSED_FILE_ACK_TYPE)){
			// assume it is the right one - can put in checks later
			if(waitingToSendSetZonesCommand) {
				sendSetZonesCommand();
				waitingToSendSetZonesCommand = false;
			}
		}
	}
	
	public void sendCompressedZones() {
		// send keepouts
		try {
			CompressedFilePublisher.getInstance(agent).sendKeepoutZones();

		} catch (Exception e) {
			e.printStackTrace();
		}

		waitingToSendSetZonesCommand = true;
	}

	protected void sendSetZonesCommand() {
		commandPublisher.sendGenericNoParamsCommand(SETTINGS_METHOD_SET_ZONES.VALUE, SETTINGS.VALUE);
	}
	
	public void subscribe() {
		if (getParticipantId() == null || getParticipantId().isEmpty() || getAgent() == null){
			return;
		}

		for(MessageType mt : getSampleType()) {
			if (mt != null){
				RapidMessageCollector.instance().addRapidMessageListener(getParticipantId(), 
						getAgent(), 
						mt, 
						this);
			}
		}
	}
	
	public String getParticipantId() {
		return participantId;
	}
	public Agent getAgent() {
		return agent;
	}
	protected MessageType[] getSampleType() {
		return sampleType;
	}
}
