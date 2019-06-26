/*******************************************************************************
 * Copyright (c) 2011 United States Government as represented by the 
 * Administrator of the National Aeronautics and Space Administration.
 * All rights reserved.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package gov.nasa.arc.irg.plan.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;

/**
 * Keeps a map of profiles.
 * Profiles are predefined commands.
 * @author tecohen
 *
 */
public class ProfileManager {
	private static Logger logger = Logger.getLogger(ProfileManager.class);
	
	protected static Map<Class<? extends Plan>, ProfileManager> s_managerMap = new HashMap<Class<? extends Plan>, ProfileManager>();
	
	protected Map<String, PlanCommand> m_profiles = new LinkedHashMap<String, PlanCommand>();	// prepopulated Commands with default values
	
	protected Map<Class<? extends PlanCommand>, List<PlanCommand>> m_profileMap = new HashMap<Class<? extends PlanCommand>, List<PlanCommand>>();	// map of class type to prepopulated Commands
	
	protected Map<String, Class<? extends PlanCommand>> m_commandClasses = new LinkedHashMap<String, Class<? extends PlanCommand>>();	// generic Command types
	
	protected PlanLibrary m_planLibrary; // right now we have one plan library per class
	
	protected boolean m_initialized = false;

	public static ProfileManager getProfileManager(Class<? extends Plan> planClass){
		ProfileManager found = s_managerMap.get(planClass);
		if (found == null){
			found = new ProfileManager();
			s_managerMap.put(planClass, found);
		}
		return found;
	}
	
	protected ProfileManager() {
	}
	
	public boolean isInitialized() {
		return m_initialized;
	}

	public void setInitialized(boolean initialized) {
		m_initialized = initialized;
	}
	
	public void setLibrary(Class<? extends Plan> planClass, PlanLibrary library){
		m_planLibrary = library;
		for (PlanCommand p : library.getCommands()){
			addProfile(p);
		}
	}
	
	public PlanLibrary getLibrary() {
		return m_planLibrary;
	}
	
	public List<Station> getStations() {
		if (m_planLibrary != null){
			return m_planLibrary.getStations();
		}
		return Collections.EMPTY_LIST;
	}
	
	public List<Site> getSites() {
		if (m_planLibrary != null){
			return m_planLibrary.getSites();
		}
		return Collections.EMPTY_LIST;
	}

	
	public void addProfile(PlanCommand profile) {
		if (profile != null){
			m_profiles.put(profile.getPresetCode(), profile);
			List<PlanCommand> existing = m_profileMap.get(profile.getClass());
			if (existing != null && !existing.contains(profile)){
				existing.add(profile);
			} else {
				List<PlanCommand> Commandlist = new ArrayList<PlanCommand>();
				Commandlist.add(profile);
				m_profileMap.put(profile.getClass(), Commandlist);
			}
		}
	}
	
	public PlanCommand getProfile(String profileName) {
		return m_profiles.get(profileName);
	}

	public Set<String> getProfileNames() {
		return m_profiles.keySet();
	}

	public Collection<PlanCommand> getProfiles() {
		return m_profiles.values();
	}
	
	public List<? extends PlanCommand> getCommandClasses(Class<? extends PlanCommand> baseType){
		return m_profileMap.get(baseType);
	}
	
	public Collection<Class<? extends PlanCommand>> getCommandClasses(){
		return m_commandClasses.values();
	}
	
	public Class<? extends PlanCommand> getCommandClass(String name){
		return m_commandClasses.get(name);
	}
	
	public void addCommandClasses(Collection<Class<? extends PlanCommand>> commandClasses){
		for (Class<? extends PlanCommand> t : commandClasses){
			addCommandClass(t);
		}
	}
	
	public void addCommandClass(Class<? extends PlanCommand> commandClass){
		m_commandClasses.put(commandClass.getSimpleName(), commandClass);
	}
}
