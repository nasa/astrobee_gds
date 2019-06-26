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
package gov.nasa.arc.irg.plan.ui.io;

import gov.nasa.arc.irg.plan.freeflyer.config.OperatingLimitsConfigList;

public class OperatingLimitsConfigListLoader extends GenericJsonConfigLoader {
	private static OperatingLimitsConfigList operatingLimitsConfigList;
	
	static {
		classToLoad = OperatingLimitsConfigList.class;
	}
	
	public static OperatingLimitsConfigList getStandardConfig() throws Exception {
		if(operatingLimitsConfigList == null) {
			operatingLimitsConfigList = (OperatingLimitsConfigList)getConfig(ConfigFileWrangler.getInstance().getOperatingLimitsConfigPath());
		}
		return operatingLimitsConfigList;
	}
	
	public static OperatingLimitsConfigList loadFromFile(String filename) throws Exception {
		return (OperatingLimitsConfigList) loadFromFileGeneric(filename);
	}
}
