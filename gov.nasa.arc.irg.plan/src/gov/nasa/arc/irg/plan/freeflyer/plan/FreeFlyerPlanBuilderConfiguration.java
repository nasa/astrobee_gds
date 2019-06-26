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
package gov.nasa.arc.irg.plan.freeflyer.plan;

import gov.nasa.arc.irg.plan.model.Plan;
import gov.nasa.arc.irg.plan.model.PlanBuilderConfiguration;
import gov.nasa.arc.irg.plan.model.PlanCommand;
import gov.nasa.arc.irg.plan.model.Sequenceable;

import java.io.InputStream;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;


public class FreeFlyerPlanBuilderConfiguration extends PlanBuilderConfiguration {

	public FreeFlyerPlanBuilderConfiguration(InputStream libraryInputStream,
			Class<? extends PlanCommand> commandClass,
			Class<? extends Plan> planClass,
			String profilesURL) {
		super(libraryInputStream, commandClass, planClass, profilesURL);
	}
	
	@Override
	protected void setupCommandClasses() {
		// 10.21.15 - commenting to make it compile, even though these are all PlanCommands
//		m_profileManager.addCommandClass(Orient.class); // X
//		m_profileManager.addCommandClass(Wait.class);
//		m_profileManager.addCommandClass(Record.class); // X
//		m_profileManager.addCommandClass(ArmPanAndTilt.class);
//		m_profileManager.addCommandClass(ConfigureOperatingLimits.class);
	}

	@Override
	protected void initJsonConverter() {
		m_mapper = new ObjectMapper();
		m_mapper.configure(SerializationConfig.Feature.INDENT_OUTPUT, true);

		SerializationConfig serializationConfig = m_mapper.getSerializationConfig();
		DeserializationConfig deserializationConfig = m_mapper.getDeserializationConfig();
		
		// let's actually mix something in here
		serializationConfig.addMixInAnnotations(Sequenceable.class, SequenceableMixins.class);
		deserializationConfig.addMixInAnnotations(Sequenceable.class, SequenceableMixins.class);
		
		deserializationConfig.addMixInAnnotations(PlanCommand.class, getCommandType());  
		serializationConfig.addMixInAnnotations(PlanCommand.class, getCommandType());
		
		serializationConfig.setSerializationInclusion(Inclusion.NON_NULL);
//		serializationConfig.setSerializationInclusion(Inclusion.NON_DEFAULT);
		
	}
	
}
