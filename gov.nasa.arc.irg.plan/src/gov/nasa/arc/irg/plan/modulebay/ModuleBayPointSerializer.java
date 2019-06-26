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
package gov.nasa.arc.irg.plan.modulebay;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

public class ModuleBayPointSerializer extends JsonSerializer<ModuleBayPoint> {

	@Override
	public void serialize(ModuleBayPoint mbp, JsonGenerator generator,
			SerializerProvider provider) throws IOException,
			JsonProcessingException {
		generator.writeStartObject();

		generator.writeStringField("type", mbp.getClass().getSimpleName());
		// we always need these because FSW wants them
		generator.writeNumberField("x", mbp.getX());
		generator.writeNumberField("y", mbp.getY());
		generator.writeNumberField("z", mbp.getZ());

		// have to write these for Ted's sequencer
		generator.writeNumberField("roll", mbp.getRoll());
		generator.writeNumberField("pitch", mbp.getPitch());
		generator.writeNumberField("yaw", mbp.getYaw());

		generator.writeBooleanField("ignoreOrientation", mbp.isIgnoreOrientation());

		generator.writeBooleanField("moduleBayValid", mbp.isModuleBayValid());

		// don't bother writing these down if they are all invalid
		if(mbp.isModuleBayValid()) {

			generator.writeFieldName("module");
			generator.writeObject(mbp.getModule().getName());

			if(mbp.isBayNumberValid()) {
				generator.writeBooleanField("bayNumberValid", mbp.isBayNumberValid());
				generator.writeFieldName("bayNumber");
				generator.writeString(mbp.getBayNumber().getDescriptor());

				generator.writeBooleanField("centerOne", mbp.isCenterOne());
				generator.writeFieldName("wallOne");
				generator.writeObject(mbp.getWallOne().getName());
				generator.writeNumberField("wallOneOffset", mbp.getWallOneOffset());

				generator.writeBooleanField("centerTwo", mbp.isCenterTwo());
				generator.writeFieldName("wallTwo");
				generator.writeObject(mbp.getWallTwo().getName());
				generator.writeNumberField("wallTwoOffset", mbp.getWallTwoOffset());
			}
			if(!mbp.isIgnoreOrientation()) {
				generator.writeFieldName("orientationWall");
				generator.writeObject(mbp.getOrientationWall().getName());
			}
		}

		generator.writeFieldName("bookmark");

		if(mbp.getBookmark() != null) {
			generator.writeObject(mbp.getBookmark().getName());
		} else {
			generator.writeObject(null); // writes null
		}

		generator.writeBooleanField("bookmarkValid", mbp.isBookmarkValid());
		
		generator.writeEndObject();
	}

}
