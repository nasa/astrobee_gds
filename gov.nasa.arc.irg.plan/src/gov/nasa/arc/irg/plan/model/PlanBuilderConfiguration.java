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

import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

public abstract class PlanBuilderConfiguration {
	private static Logger logger = Logger.getLogger(PlanBuilderConfiguration.class);
	protected ProfileManager m_profileManager;
	protected ObjectMapper m_mapper; // for converting to/from JSON

	protected String m_profilesURL;
	protected InputStream m_libraryInputStream;
	protected Class<? extends PlanCommand> m_commandClass;
	protected Class<? extends Plan> m_planClass;

	public PlanBuilderConfiguration(InputStream libraryInputStream,
									   Class<? extends PlanCommand> commandClass,
									   Class<? extends Plan> planClass,
									   String profilesURL) {
		m_libraryInputStream = libraryInputStream;
		m_commandClass = commandClass;
		m_planClass = planClass;
		m_profilesURL = profilesURL;
		
		initJsonConverter();
	}

	public ProfileManager getProfileManager() {
		if (m_profileManager == null){
			m_profileManager = ProfileManager.getProfileManager(m_planClass);
			setupProfiles();
			setupCommandClasses();
		}
		return m_profileManager;
	}
	
	public void setProfileManager(ProfileManager profileManager) {
		m_profileManager = profileManager;
	}
	
	public ObjectMapper getMapper() {
		return m_mapper;
	}
	
	public void setMapper(ObjectMapper mapper) {
		m_mapper = mapper;
	}
	
	public InputStream getLibraryInputStream() {
		return m_libraryInputStream;
	}

	public void setLibraryInputStream(InputStream libraryInputStream) {
		m_libraryInputStream = libraryInputStream;
	}
	
	
	/**
	 * Particular class type for your plan.
	 * Must extend PlanCommand.class
	 * @return
	 */
	protected Class<? extends PlanCommand> getCommandType(){
		return m_commandClass;
	}
	
	public void setCommandType(Class<? extends PlanCommand> commandType) {
		m_commandClass = commandType;
	}
	
	/**
	 * Read and load the profiles (prepopulated commands) from a json file
	 */
	protected void setupProfiles() {
		try {
			PlanLibrary library = m_mapper.readValue(getLibraryInputStream(), PlanLibrary.class);
			m_profileManager.setLibrary(m_planClass, library);
		} catch (JsonParseException e) {
			logger.error(e.getMessage(), e);
		} catch (JsonMappingException e) {
			logger.error(e.getMessage(), e);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
       
	}
	
	public String getProfilesURL() {
		return m_profilesURL;
	}

	public void setProfilesURL(String profilesURL) {
		m_profilesURL = profilesURL;
	}

	protected void initJsonConverter() {
		m_mapper = new ObjectMapper();
		m_mapper.configure(SerializationConfig.Feature.INDENT_OUTPUT, true);

		SerializationConfig serializationConfig = m_mapper.getSerializationConfig();
		DeserializationConfig deserializationConfig = m_mapper.getDeserializationConfig();
		
		// doesn't this do ... nothing?
		serializationConfig.addMixInAnnotations(Sequenceable.class, Sequenceable.class);
		deserializationConfig.addMixInAnnotations(Sequenceable.class, Sequenceable.class);
		
		deserializationConfig.addMixInAnnotations(PlanCommand.class, getCommandType());  
		serializationConfig.addMixInAnnotations(PlanCommand.class, getCommandType());
		
		serializationConfig.setSerializationInclusion(Inclusion.NON_NULL);
//		serializationConfig.setSerializationInclusion(Inclusion.NON_DEFAULT);
		
	}
	
	
	
	/**
	 * Extend this to set up the classes to use.
	 */
	protected  abstract void setupCommandClasses();
	
	public Class<? extends PlanCommand> lookupCommandClass(String name){
		return getProfileManager().getCommandClass(name);
	}
	
	/**
	 * Get the strings describing the command classes in the given package.
	 * @return
	 */
//	protected Collection<String> getCommandStrings() {
//		String path = getCommandPackage().replace('.', '/');
//		BundleWiring wiring = getBundle().adapt(BundleWiring.class);
//		Collection<String> strings = wiring.listResources(path, "*.class", 0);
//		return strings;
//	}
	
}
