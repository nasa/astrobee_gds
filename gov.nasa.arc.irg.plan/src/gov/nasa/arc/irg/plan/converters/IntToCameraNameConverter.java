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

import gov.nasa.arc.irg.plan.util.PlanConstants;

import org.eclipse.core.databinding.conversion.Converter;

public class IntToCameraNameConverter extends Converter {

	public IntToCameraNameConverter() {
		super(Integer.TYPE, String.class);
	}

	@Override
	public Object convert(Object fromObject) {
		int asInt = (int) fromObject;

		if(asInt == 0) {
			return PlanConstants.SCI_CAM_NAME;
		}
		if(asInt == 1) {
			return PlanConstants.NAV_CAM_NAME;
		}
		if(asInt == 2) {
			return PlanConstants.HAZ_CAM_NAME;
		}
		if(asInt == 3) {
			return PlanConstants.DOCK_CAM_NAME;
		}
		if(asInt == 4) {
			return PlanConstants.PERCH_CAM_NAME;
		}

		return null;
	}
}
