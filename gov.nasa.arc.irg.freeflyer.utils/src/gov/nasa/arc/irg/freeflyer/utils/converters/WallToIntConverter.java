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
package gov.nasa.arc.irg.freeflyer.utils.converters;

import gov.nasa.arc.irg.plan.model.modulebay.LocationMap;

import org.eclipse.core.databinding.conversion.Converter;

public class WallToIntConverter extends Converter {
	String[] items;
	
	public WallToIntConverter() {
		super(LocationMap.Wall.class, Integer.TYPE);
		setItems(LocationMap.getInstance().allWallsAsStringArray());
	}

	public Object convert(Object fromObject) {
		if(fromObject instanceof LocationMap.Wall) {
			LocationMap.Wall wall = (LocationMap.Wall) fromObject;
			for(int i=0; i<items.length; i++) {
				if(wall.getName().equals(items[i])) {
					return i;
				}
			}
		}
		return null;
	}
	
	public void setItems(String[] it) {
		items = it;
	}
}
