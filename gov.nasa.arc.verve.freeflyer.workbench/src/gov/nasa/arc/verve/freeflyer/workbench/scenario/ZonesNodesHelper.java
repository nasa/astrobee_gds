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
package gov.nasa.arc.verve.freeflyer.workbench.scenario;

import gov.nasa.arc.irg.plan.freeflyer.config.KeepoutConfig;
import gov.nasa.arc.irg.plan.freeflyer.config.ZonesConfig;
import gov.nasa.arc.irg.plan.ui.io.ConfigFileWrangler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectReader;

import rapid.ext.astrobee.CompressedFile;

import com.rti.dds.infrastructure.ByteSeq;

public class ZonesNodesHelper {
	private final String agentName;
	private BoxesNode keepins;
	private BoxesNode keepouts;
	private int[] keepinsColor = { 66, 244, 176 };

	private static Map<String, ZonesNodesHelper> s_instances = new HashMap<String, ZonesNodesHelper>();
	
	public static ZonesNodesHelper get(String agentName) {
		ZonesNodesHelper ret = s_instances.get(agentName);
		if(ret == null) {
			// sometimes caller asks for "rapid/Robot" instead of "Robot"
			String[] tokens = agentName.split("/");
			String shortName = tokens[tokens.length-1];
			ret = s_instances.get(shortName);
		}
		if(ret == null) {
			// it's really not there, make it
			ret = new ZonesNodesHelper(agentName);
			s_instances.put(agentName, ret);
		}
		return ret;
	}
	
	private ZonesNodesHelper(String agentName) {
		this.agentName = agentName;
		// initialize with the zones in config files
		keepins = new BoxesNode(agentName + "Keepins", ConfigFileWrangler.getInstance().getKeepinFiles(), keepinsColor);
		keepouts = new BoxesNode(agentName + "Keepouts", ConfigFileWrangler.getInstance().getKeepoutFiles());
	}

	public BoxesNode getKeepins() {
		return keepins;
	}

	public BoxesNode getKeepouts() {
		return keepouts;
	}

	public void ingestZonesFile(CompressedFile newZones) {
		// turn compressedfile into String
		final ByteSeq seq = newZones.compressedFile.userData;
		byte[] bytes = seq.toArrayByte(new byte[seq.size()]);

		// Reading...
		// Create an immutable reader
		ObjectMapper jsonObjectMapper = new ObjectMapper();
		final ObjectReader reader = jsonObjectMapper.reader();

		
		try {
			// Use the reader for thread safe access
			final JsonNode newNode = reader.readTree(new ByteArrayInputStream(bytes));

			// parse the String as a Keepout/in thing
			
			ZonesConfig newJsonNode = jsonObjectMapper.treeToValue(newNode, ZonesConfig.class);
			
			// put that thing into the appropriate array
			KeepoutConfig kc = newJsonNode.getZones().get(0);
			
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}










