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

import gov.nasa.arc.irg.plan.freeflyer.command.Record;
import gov.nasa.arc.irg.plan.freeflyer.command.Wait;
import gov.nasa.arc.irg.plan.model.PlanCommand;
import gov.nasa.arc.irg.plan.model.Segment;
import gov.nasa.arc.irg.plan.model.Sequenceable;
import gov.nasa.arc.irg.plan.model.Station;

import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonSubTypes.Type;
import org.codehaus.jackson.annotate.JsonTypeInfo;

@JsonTypeInfo( 
		use = JsonTypeInfo.Id.NAME, 
		include = JsonTypeInfo.As.PROPERTY, 
		property = "type")
@JsonSubTypes({
	@Type(value=Sequenceable.class, name="Sequenceable"),
	@Type(value = Station.class, name = "Station"), 
	@Type(value = Segment.class, name = "Segment"),  
	@Type(value = PlanCommand.class, name = "Command"),  
	@Type(value=Wait.class, name="Wait"),
	@Type(value=Record.class, name="Record"),
})
public abstract class SequenceableMixins {

}
