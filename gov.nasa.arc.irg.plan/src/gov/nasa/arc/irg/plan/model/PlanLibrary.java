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
package gov.nasa.arc.irg.plan.model;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonSetter;
import org.codehaus.jackson.annotate.JsonTypeInfo;


@JsonTypeInfo(  
	    use = JsonTypeInfo.Id.NAME,  
	    include = JsonTypeInfo.As.PROPERTY,  
	    property = "type")
public class PlanLibrary extends TypedObject{
	protected String m_xpjson;				// spec version
	
	protected URI m_schema;					// uri to the schema
	
	protected List<Site> m_sites = new ArrayList<Site>();					// information describing the site or location of the plan
	
	protected List<Platform> m_platforms = new ArrayList<Platform>();		// what "platform" we are running on, ie K10Red
	
	protected List<Station> m_stations = new ArrayList<Station>();
	
	protected List<PlanCommand> m_commands = new ArrayList<PlanCommand>();

	/**
	 * @return the xpjson
	 */
	public String getXpjson() {
		return m_xpjson;
	}

	/**
	 * @param xpjson the xpjson to set
	 */
	public void setXpjson(String xpjson) {
		m_xpjson = xpjson;
	}

	/**
	 * @return the schema
	 */
	public URI getSchema() {
		return m_schema;
	}

	/**
	 * @param schema the schema to set
	 */
	@JsonSetter("schemaUrl")
	public void setSchema(URI schema) {
		m_schema = schema;
	}

	/**
	 * @return the sites
	 */
	public List<Site> getSites() {
		return m_sites;
	}

	/**
	 * @param sites the sites to set
	 */
	public void setSites(List<Site> sites) {
		m_sites = sites;
	}

	/**
	 * @return the platforms
	 */
	public List<Platform> getPlatforms() {
		return m_platforms;
	}

	/**
	 * @param platforms the platforms to set
	 */
	public void setPlatforms(List<Platform> platforms) {
		m_platforms = platforms;
	}

	/**
	 * @return the stations
	 */
	public List<Station> getStations() {
		return m_stations;
	}

	/**
	 * @param stations the stations to set
	 */
	public void setStations(List<Station> stations) {
		m_stations = stations;
	}

	/**
	 * @return the commands
	 */
	public List<PlanCommand> getCommands() {
		return m_commands;
	}

	/**
	 * @param commands the commands to set
	 */
	public void setCommands(List<PlanCommand> commands) {
		m_commands = commands;
	}
	
	
}
