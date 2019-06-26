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

import gov.nasa.arc.irg.plan.freeflyer.config.KeepoutConfig;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

public class KeepoutConfigLoaderAndWriter extends GenericJsonConfigLoader {
	
	static {
		classToLoad = KeepoutConfig.class;
	}
	
	public static KeepoutConfig loadFromFile(String filename) throws Exception {
		return (KeepoutConfig) loadFromFileGeneric(filename, KeepoutConfig.class);
	}
	
	public static void write(String filename, KeepoutConfig keepoutConfig) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationConfig.Feature.INDENT_OUTPUT, true);

		if(keepoutConfig == null) {
			System.err.println("BookmarkListBuilder has no list to write");
			return;
		}
		
		String listAsString = mapper.writeValueAsString(keepoutConfig);
		
		try (Writer writer = new BufferedWriter(new OutputStreamWriter(
	              new FileOutputStream(filename), "utf-8"))) {
			writer.write(listAsString);
		}
	}
}
