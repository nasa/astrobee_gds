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
package gov.nasa.arc.irg.plan.converters;

import org.eclipse.core.databinding.conversion.Converter;

import rapid.ext.astrobee.ADMIN_LOCALIZATION_MODE_ARTAGS;
import rapid.ext.astrobee.ADMIN_LOCALIZATION_MODE_HANDRAIL;
import rapid.ext.astrobee.ADMIN_LOCALIZATION_MODE_MAPPED_LANDMARKS;
import rapid.ext.astrobee.ADMIN_LOCALIZATION_MODE_NONE;
import rapid.ext.astrobee.ADMIN_LOCALIZATION_MODE_PERCH;
import rapid.ext.astrobee.ADMIN_LOCALIZATION_MODE_TRUTH;

public class IntToLocalizationConverter extends Converter {

	public IntToLocalizationConverter() {
		super(Integer.TYPE, String.class);
	}
	
	@Override
	public Object convert(Object fromObject) {
		int asInt = (int) fromObject;

		if(asInt == 0) {
			return ADMIN_LOCALIZATION_MODE_NONE.VALUE;
		}
		if(asInt == 1) {
			return ADMIN_LOCALIZATION_MODE_MAPPED_LANDMARKS.VALUE;
		}
		if(asInt == 2) {
			return ADMIN_LOCALIZATION_MODE_ARTAGS.VALUE;
		}
		if(asInt == 3) {
			return ADMIN_LOCALIZATION_MODE_HANDRAIL.VALUE;
		}
		if(asInt == 4) {
			return ADMIN_LOCALIZATION_MODE_PERCH.VALUE;
		}
		if(asInt == 5) {
			return ADMIN_LOCALIZATION_MODE_TRUTH.VALUE;
		}

		return null;
	}

}
