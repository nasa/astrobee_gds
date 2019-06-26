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
package gov.nasa.arc.irg.plan.json;

import gov.nasa.arc.irg.plan.model.modulebay.BayNumber;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

public class BayNumberSerializer extends JsonSerializer<BayNumber> {

	@Override
	public void serialize(BayNumber bayNumber, JsonGenerator generator,
			SerializerProvider provider) throws IOException,
			JsonProcessingException {
		generator.writeStartObject();
		generator.writeFieldName("descriptor");
		generator.writeString(bayNumber.getDescriptor());
		generator.writeFieldName("bayIntegerOne");
		generator.writeNumber(bayNumber.getBayIntegerOne());
		generator.writeFieldName("bayIntegerTwo");
		generator.writeNumber(bayNumber.getBayIntegerTwo());
		generator.writeFieldName("split");
		generator.writeBoolean(bayNumber.isSplit());
		generator.writeEndObject();
	}
}
