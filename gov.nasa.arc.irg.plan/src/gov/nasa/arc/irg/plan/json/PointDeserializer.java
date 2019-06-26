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

import gov.nasa.arc.irg.plan.model.Point;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;

public class PointDeserializer extends JsonDeserializer<Point> {

	@Override
	public Point deserialize(JsonParser jp, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		
		JsonToken token = jp.getCurrentToken();
		JsonToken value = jp.nextValue();
		if (value.equals(JsonToken.START_ARRAY)) {
			Point result = new Point();
			
			List<Float> coordinates = new ArrayList<Float>();
			JsonToken coordToken = jp.nextValue();
			while (coordToken != null){
				Number num = jp.getNumberValue();
				coordinates.add(num.floatValue());
				coordToken = jp.nextValue();
			}
			result.setCoordinates(coordinates);
			return result;
		}
		return null;
	}

}
