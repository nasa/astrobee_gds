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

import rapid.ext.astrobee.SETTINGS_TELEMETRY_TYPE_COMM_STATUS;
import rapid.ext.astrobee.SETTINGS_TELEMETRY_TYPE_CPU_STATE;
import rapid.ext.astrobee.SETTINGS_TELEMETRY_TYPE_DISK_STATE;
import rapid.ext.astrobee.SETTINGS_TELEMETRY_TYPE_EKF_STATE;
import rapid.ext.astrobee.SETTINGS_TELEMETRY_TYPE_GNC_STATE;
import rapid.ext.astrobee.SETTINGS_TELEMETRY_TYPE_PMC_CMD_STATE;
import rapid.ext.astrobee.SETTINGS_TELEMETRY_TYPE_POSITION;

public class TelemetryNameToIntConverter extends Converter {

	public TelemetryNameToIntConverter() {
		super(String.class, Integer.TYPE);
	}

	@Override
	public Object convert(Object fromObject) {
		if(fromObject instanceof String) {
			if(fromObject.equals(SETTINGS_TELEMETRY_TYPE_COMM_STATUS.VALUE)) {
				return 0;
			}
			if(fromObject.equals(SETTINGS_TELEMETRY_TYPE_CPU_STATE.VALUE)) {
				return 1;
			}
			if(fromObject.equals(SETTINGS_TELEMETRY_TYPE_DISK_STATE.VALUE)) {
				return 2;
			}
			if(fromObject.equals(SETTINGS_TELEMETRY_TYPE_EKF_STATE.VALUE)) {
				return 3;
			}
			if(fromObject.equals(SETTINGS_TELEMETRY_TYPE_GNC_STATE.VALUE)) {
				return 4;
			}
			if(fromObject.equals(SETTINGS_TELEMETRY_TYPE_PMC_CMD_STATE.VALUE)) {
				return 5;
			}
			if(fromObject.equals(SETTINGS_TELEMETRY_TYPE_POSITION.VALUE)) {
				return 6;
			}
		}
		return null;
	}

}
