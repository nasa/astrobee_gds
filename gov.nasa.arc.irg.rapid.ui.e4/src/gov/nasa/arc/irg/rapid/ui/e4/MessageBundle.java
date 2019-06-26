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
package gov.nasa.arc.irg.rapid.ui.e4;

import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.e4.message.MessageType;

import java.util.Date;

public class MessageBundle {
	public Agent agent;
	public MessageType messageType;
	public Object eventObj;
	public Object configObj;
	public Date receivedTime;
	public int counter = 0;

	public MessageBundle(Agent agent, MessageType msgType, Object eventObj, Object configObj){
		this.receivedTime = new Date();
		this.agent = agent;
		this.messageType = msgType;
		this.eventObj = eventObj;
		this.configObj = configObj;
	}
}
