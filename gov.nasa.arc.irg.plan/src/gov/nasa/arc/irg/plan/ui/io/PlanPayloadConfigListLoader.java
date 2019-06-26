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

import gov.nasa.arc.irg.plan.freeflyer.config.PlanPayloadConfig;
import gov.nasa.arc.irg.plan.freeflyer.config.PlanPayloadConfigList;

public class PlanPayloadConfigListLoader extends GenericJsonConfigLoader {
	private static PlanPayloadConfigList planPayloadConfigList;
	
	static {
		classToLoad = PlanPayloadConfigList.class;
	}
	
	private static void ensureListIsLoaded() throws Exception {
		if(planPayloadConfigList == null) {
			classToLoad = PlanPayloadConfigList.class;
			planPayloadConfigList = (PlanPayloadConfigList)getConfig(ConfigFileWrangler.getInstance().getPayloadConfigPath());
		}
	}
	
	public static PlanPayloadConfigList getStandardConfig() throws Exception {
		ensureListIsLoaded();
		return planPayloadConfigList;
	}
	
	public static PlanPayloadConfigList loadFromFile(String filename) throws Exception {
		return (PlanPayloadConfigList) loadFromFileGeneric(filename);
	}
	
	public static PlanPayloadConfig getPlanPayloadConfigFromIndex(int index) throws Exception {
		ensureListIsLoaded();
		return planPayloadConfigList.getPlanPayloadConfigs().get(index);
	}
	
	public static PlanPayloadConfig getPlanPayloadConfigFromName(String name) throws Exception {
		ensureListIsLoaded();
		for(PlanPayloadConfig conf : planPayloadConfigList.getPlanPayloadConfigs()) {
			if(conf.getName().equals(name)) {
				return conf;
			}
		}
		return null;
	}

	public static int getIndexOfPlanPayloadConfig(PlanPayloadConfig config) throws Exception {
		ensureListIsLoaded();
		return planPayloadConfigList.getPlanPayloadConfigs().indexOf(config);
	}
	
	public static int getNumberOfPayloads() throws Exception {
		ensureListIsLoaded();
		return planPayloadConfigList.getPlanPayloadConfigs().size();
	}
	
	public static String[] getArrayOfNames() throws Exception {
		ensureListIsLoaded();
		String[] ret = new String[getNumberOfPayloads()];
		int i=0;
		for(PlanPayloadConfig code: planPayloadConfigList.getPlanPayloadConfigs()) {
			ret[i++] = code.getName();
		}
		return ret;
	}
}
