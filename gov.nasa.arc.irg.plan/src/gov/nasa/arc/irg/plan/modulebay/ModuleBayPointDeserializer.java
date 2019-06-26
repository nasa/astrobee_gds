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

import gov.nasa.arc.irg.plan.bookmarks.StationBookmark;
import gov.nasa.arc.irg.plan.model.modulebay.BayNumber;
import gov.nasa.arc.irg.plan.model.modulebay.LocationMap;
import gov.nasa.arc.irg.plan.ui.io.BookmarkListBuilder;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;

public class ModuleBayPointDeserializer extends JsonDeserializer<ModuleBayPoint>{

	@Override
	public ModuleBayPoint deserialize(JsonParser jp,
			DeserializationContext ctxt) throws IOException,
			JsonProcessingException {
		JsonNode node = jp.getCodec().readTree(jp);
		ModuleBayPoint result = new ModuleBayPoint();

		// try to build from bookmark
		JsonNode bookmarkValidNode = node.get("bookmarkValid");
		boolean bookmarkValid = false;
		if(bookmarkValidNode != null) {
			bookmarkValid = bookmarkValidNode.getBooleanValue();
		}
		if(bookmarkValid) {
			JsonNode bookmarkNode =  node.get("bookmark");
			if(bookmarkNode != null) {
				String bookmarkName = bookmarkNode.getTextValue();
				if(bookmarkName != null) {
					// find the bookmark
					StationBookmark bookmark = BookmarkListBuilder.getBookmarkFromName(bookmarkName);

					if(bookmark != null) {
						result.setBookmark(bookmark);
						return result;
					}
				}
			}
		}

		// if bookmark doesn't work, go through all fields
		boolean buildFromModuleBay = node.get("moduleBayValid").getBooleanValue();
		if(buildFromModuleBay) {
			String moduleString = node.get("module").getTextValue();
			result.setModule(LocationMap.getInstance().getModule(moduleString));
			if(node.get("bayNumberValid").getBooleanValue()) {
				String bayString = node.get("bayNumber").getTextValue();
				result.setBayNumber(BayNumber.fromString(bayString));

				boolean centerOne = node.get("centerOne").getBooleanValue();
				result.setCenterOne(centerOne);
				if(!centerOne) {
					// TODO null check
					result.setWallOne(LocationMap.getInstance().getWall(node.get("wallOne").getTextValue()));
					result.setWallOneOffset(node.get("wallOneOffset").getDoubleValue());
				}

				boolean centerTwo = node.get("centerTwo").getBooleanValue();
				result.setCenterTwo(centerTwo);
				if(!centerTwo) {
					// TODO null check
					result.setWallTwo(LocationMap.getInstance().getWall(node.get("wallTwo").getTextValue()));
					result.setWallTwoOffset(node.get("wallTwoOffset").getDoubleValue());
				}
			}

			boolean ignoreOrientation = node.get("ignoreOrientation").getBooleanValue();
			result.setIgnoreOrientation(ignoreOrientation);
			if(!ignoreOrientation) {
				result.setOrientationWall(LocationMap.getInstance().getWall(node.get("orientationWall").getTextValue()));
			}
		} else {
			result.setX((float)node.get("x").getDoubleValue());
			result.setY((float)node.get("y").getDoubleValue());
			result.setZ((float)node.get("z").getDoubleValue());
			result.setRoll((float)node.get("roll").getDoubleValue());
			result.setPitch((float)node.get("pitch").getDoubleValue());
			result.setYaw((float)node.get("yaw").getDoubleValue());
			boolean ignoreOrientation = node.get("ignoreOrientation").getBooleanValue();
			result.setIgnoreOrientation(ignoreOrientation);
		}

		return result;
	}

}
