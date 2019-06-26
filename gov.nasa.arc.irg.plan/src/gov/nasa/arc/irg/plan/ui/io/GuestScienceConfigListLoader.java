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

import gov.nasa.arc.irg.plan.freeflyer.config.GuestScienceApkGds;
import gov.nasa.arc.irg.plan.freeflyer.config.GuestScienceApkGds.GuestScienceCommandGds;
import gov.nasa.arc.irg.plan.freeflyer.config.GuestScienceConfigList;

public class GuestScienceConfigListLoader extends GenericJsonConfigLoader {

	private static GuestScienceConfigList guestScienceConfigList;

	static {
		classToLoad = GuestScienceConfigList.class;
	}

	private static void ensureListIsLoaded() throws Exception {
		if(guestScienceConfigList == null) {
			guestScienceConfigList = (GuestScienceConfigList)getConfig(ConfigFileWrangler.getInstance().getGuestScienceConfigPath());
		}
	}

	public static GuestScienceConfigList getStandardConfig() throws Exception {
		ensureListIsLoaded();
		return guestScienceConfigList;
	}

	public static GuestScienceApkGds getGuestScienceConfigFromIndex(int index) throws Exception {
		ensureListIsLoaded();
		return guestScienceConfigList.getGuestScienceConfigs().get(index);
	}

	public static GuestScienceApkGds getGuestScienceConfigFromName(String configName) throws Exception {
		if(configName.isEmpty()) {
			return null;
		}
		
		ensureListIsLoaded();

		for(GuestScienceApkGds config : guestScienceConfigList.getGuestScienceConfigs()) {
			if(config.getShortName().equals(configName)) {
				return config;
			}
		}
		System.out.println("Invalid config name requested");
		return null;
	}

	public static int getIndexOfGuestScienceConfig(GuestScienceApkGds config) throws Exception {
		ensureListIsLoaded();
		return guestScienceConfigList.getGuestScienceConfigs().indexOf(config);
	}

	public static GuestScienceConfigList loadFromFile(String filename) throws Exception {
		return (GuestScienceConfigList) loadFromFileGeneric(filename);
	}

	public static int getNumberOfGuestScienceConfigs() throws Exception {
		ensureListIsLoaded();
		return guestScienceConfigList.getGuestScienceConfigs().size();
	}

	public static String[] getArrayOfConfigNames() throws Exception {
		ensureListIsLoaded();
		String[] ret = new String[getNumberOfGuestScienceConfigs()];
		int i=0;
		for(GuestScienceApkGds config: guestScienceConfigList.getGuestScienceConfigs()) {
			ret[i++] = config.getShortName();
		}
		return ret;
	}

	public static String[] getArrayOfCommandNames(GuestScienceApkGds config) throws Exception {
		ensureListIsLoaded();
		if(config != null && config.getGuestScienceCommands() != null) {
			String[] ret = new String[config.getGuestScienceCommands().size()];
			int i=0;
			for(GuestScienceCommandGds command: config.getGuestScienceCommands()) {
				ret[i++] = command.getName();
			}
			return ret;
		} else {
			return new String[0];
		}
	}
}
