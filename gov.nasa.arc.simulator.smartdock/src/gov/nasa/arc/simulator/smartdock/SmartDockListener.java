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
package gov.nasa.arc.simulator.smartdock;

import gov.nasa.rapid.idl.ext.astrobee.message.MessageTypeExtAstro;
import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.e4.message.IRapidMessageListener;
import gov.nasa.rapid.v2.e4.message.MessageType;
import gov.nasa.rapid.v2.e4.message.collector.RapidMessageCollector;
import rapid.Command;
import rapid.ParameterUnion;
import rapid.ext.astrobee.ADMIN_METHOD_WAKE;


public class SmartDockListener implements IRapidMessageListener {
	
	public void createReader(){
		RapidMessageCollector.instance().addRapidMessageListener(SmartDock.PARTICIPANT_ID, SmartDock.getAgent(), MessageTypeExtAstro.COMMAND_TYPE, this);
	}

	@Override
	public void onRapidMessageReceived(Agent agent, MessageType msgType,
			Object msgObj, Object cfgObj) {
		if(msgObj instanceof Command){
			final Command cmd = (Command)msgObj;
			final ParameterUnion pu = (ParameterUnion)cmd.arguments.userData.get(0);
			if(cmd.cmdName.equals(ADMIN_METHOD_WAKE.VALUE)){
				SmartDockAstrobeeController.getInstance().wakeBerth(pu.i);
			}
			//if(cmd.cmdName.equals(ADMIN_METHOD_SHUTDOWN){}
		}
		
	}
}
