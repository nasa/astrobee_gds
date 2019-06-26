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

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;

public class GenericJsonConfigLoader {
	protected static Class<?> classToLoad;

	public static Object getConfig(String configPath) throws Exception {
		Object loadedConfig = null;
		loadedConfig = loadFromFileGeneric(configPath);

		return loadedConfig;
	}

	// not called?
	public static Object loadFromUrl(String urlName) {
		try {
			URL url = new URL( urlName );
			InputStream is = url.openConnection().getInputStream();
			byte[] jsonData = IOUtils.toByteArray(is);
			ObjectMapper objectMapper = new ObjectMapper();
			return objectMapper.readValue(jsonData, classToLoad);

		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	public static Object loadFromFileGeneric(String filename, Class<?> classToLoadNow) throws Exception {
		byte[] jsonData = Files.readAllBytes(Paths.get(filename));
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(jsonData, classToLoadNow);
	}

	public static Object loadFromFileGeneric(String filename) throws Exception {
		byte[] jsonData = Files.readAllBytes(Paths.get(filename));
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(jsonData, classToLoad);
	}
}
