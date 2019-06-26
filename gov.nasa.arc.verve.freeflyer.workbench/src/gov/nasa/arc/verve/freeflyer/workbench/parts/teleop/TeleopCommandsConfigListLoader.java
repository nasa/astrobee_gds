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
package gov.nasa.arc.verve.freeflyer.workbench.parts.teleop;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.codehaus.jackson.map.ObjectMapper;

import gov.nasa.arc.irg.plan.ui.io.ConfigFileWrangler;
import gov.nasa.arc.irg.plan.ui.io.GenericJsonConfigLoader;

public class TeleopCommandsConfigListLoader extends GenericJsonConfigLoader {
	private static TeleopCommandsConfigList teleopCommandsConfigList;

	static {
		classToLoad = TeleopCommandsConfigList.class;
	}

	public static TeleopCommandsConfigList getStandardConfig() throws Exception {
		if(teleopCommandsConfigList == null) {
			if(teleopCommandsConfigList == null) {
				teleopCommandsConfigList = (TeleopCommandsConfigList)getConfig(ConfigFileWrangler.getInstance().getTeleopCommandsConfigPath());
			}
		}
		return teleopCommandsConfigList;
	}

	// this class is overloaded because we need it to call the loadFromFileGeneric in this
	// class, and static inheritance is weird
	public static Object getConfig(String configPath) throws Exception {
		Object loadedConfig = null;
		loadedConfig = loadFromFileGeneric(configPath);
		return loadedConfig;
	}

	// this class is overloaded so we can register subtypes. If other classes need to register
	// subtypes, we could put support in the parent class
	public static Object loadFromFileGeneric(String filename) throws Exception {
		byte[] jsonData = Files.readAllBytes(Paths.get(filename));
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerSubtypes(NoParamsTeleopCommand.class, OppositeCommandsTeleopCommand.class);
		return objectMapper.readValue(jsonData, classToLoad);
	}

	public static TeleopCommandsConfigList loadFromFile(String filename) throws Exception {
		return (TeleopCommandsConfigList) loadFromFileGeneric(filename);
	}
}
