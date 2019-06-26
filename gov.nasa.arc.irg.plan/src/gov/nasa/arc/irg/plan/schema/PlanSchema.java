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
package gov.nasa.arc.irg.plan.schema;

import gov.nasa.arc.irg.plan.model.PlanCommand;
import gov.nasa.arc.irg.plan.model.Site;
import gov.nasa.arc.irg.plan.model.TypedObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonTypeInfo;

@JsonTypeInfo(  
	    use = JsonTypeInfo.Id.NAME,  
	    include = JsonTypeInfo.As.PROPERTY,  
	    property = "type")
public class PlanSchema extends TypedObject {
	protected String m_xpjson;
	protected List<Site> m_sites;
	protected List<ParamSpec> m_paramSpecs;
	protected List<ParamSpec> m_stationParams;
	protected List<ParamSpec> m_segmentParams;
	protected List<ParamSpec> m_planParams;
	
	protected List<CommandSpec> m_commandSpecs;
	protected URL m_url; // url for this schema
	
	protected String m_planIdFormat;
	protected String m_stationIdFormat;
	protected String m_segmentIdFormat;
	protected String m_commandIdFormat;
	
	protected List<String> m_stationSequenceCommands;
	protected List<String> m_segmentSequenceCommands;
	
	public void createBasicCommandSpec() {
		List<ParamSpec> paramSpecs = new ArrayList<ParamSpec>();
		ParamSpec ps = new ParamSpec();
		ps.setId("id");
		ps.setEditable(false);
		ps.setName("Id");
		ps.setValueClass(String.class);
		ps.setValueClassName("string");
		paramSpecs.add(ps);
		
		ps = new ParamSpec();
		ps.setId("name");
		ps.setName("Name");
		ps.setValueClass(String.class);
		ps.setValueClassName("string");
		paramSpecs.add(ps);
		
		ps = new ParamSpec();
		ps.setId("notes");
		ps.setName("Notes");
		ps.setValueClass(String.class);
		ps.setValueClassName("string");
		paramSpecs.add(ps);
		
		CommandSpec commandCommandSpec = new CommandSpec();
		commandCommandSpec.setParamSpecs(paramSpecs);
		commandCommandSpec.setId("Command");
		commandCommandSpec.setName("Command");
		commandCommandSpec.setCommandClass(PlanCommand.class);
		m_commandSpecs.add(commandCommandSpec);
	}
	
	/**
	 * Iterate through all the param specs and commands within this plan schema and create the parent-relationships between then.
	 * Then iterate through and populate all of the values which may be inherited from the parents.
	 */
	public void constructHierarchy(){
		// populate parents for the globally defined param specs
		for (ListIterator<ParamSpec> iterator = m_paramSpecs.listIterator(m_paramSpecs.size()); iterator.hasPrevious();) {
			ParamSpec ps  = iterator.previous();
			populateParent(ps);
		}
		// populate values for the globally defined param specs
		for (ListIterator<ParamSpec> iterator = m_paramSpecs.listIterator(m_paramSpecs.size()); iterator.hasPrevious();) {
			ParamSpec ps  = iterator.previous();
			ps.lookupValues();
		}
		
		// populate parents for the command specs and their contained param specs
		for (CommandSpec cs : m_commandSpecs){
			populateParent(cs);
			if (cs.getParamSpecs() != null){
				for (ParamSpec ps : cs.getParamSpecs()){
					populateParent(ps);
				}
				for (ParamSpec ps : cs.getParamSpecs()){
					ps.lookupValues();
				}
			}
		}
	}
	
	protected void populateParent(ParamSpec paramSpec){
		if (paramSpec.getParentClassName() != null && !paramSpec.getParentClassName().isEmpty()){
			ParamSpec parent = getParamSpec(paramSpec.getParentClassName());
			if (parent != null){
				paramSpec.setParent(parent);
			} 
		}
	}
	
	protected void populateParent(CommandSpec commandSpec){
		if (commandSpec.getParentClassName() != null && !commandSpec.getParentClassName().isEmpty()){
			CommandSpec parent = getCommandSpec(commandSpec.getParentClassName());
			if (parent != null){
				commandSpec.setParent(parent);
			} else {
				//HERETAMAR
			}
		}
	}
	
	public String getXpjson() {
		return m_xpjson;
	}
	
	public void setXpjson(String xpsjson) {
		m_xpjson = xpsjson;
	}
	
	public String getPlanIdFormat() {
		return m_planIdFormat;
	}

	public void setPlanIdFormat(String planIdFormat) {
		m_planIdFormat = planIdFormat;
	}

	public String getStationIdFormat() {
		return m_stationIdFormat;
	}

	public void setStationIdFormat(String stationIdFormat) {
		m_stationIdFormat = stationIdFormat;
	}

	public String getSegmentIdFormat() {
		return m_segmentIdFormat;
	}

	public void setSegmentIdFormat(String segmentIdFormat) {
		m_segmentIdFormat = segmentIdFormat;
	}

	public String getCommandIdFormat() {
		return m_commandIdFormat;
	}

	public void setCommandIdFormat(String commandIdFormat) {
		m_commandIdFormat = commandIdFormat;
	}

	/**
	 * @return the url
	 */
	@JsonIgnore
	public URL getUrl() {
		return m_url;
	}

	/**
	 * @param url the url to set
	 */
	@JsonIgnore
	public void setUrl(URL url) {
		m_url = url;
	}

	public List<Site> getSites() {
		return m_sites;
	}
	
	public void setSites(List<Site> sites) {
		m_sites = sites;
	}
	
	public List<ParamSpec> getParamSpecs() {
		return m_paramSpecs;
	}
	
	public void setParamSpecs(List<ParamSpec> paramSpecs) {
		m_paramSpecs = paramSpecs;
	}
	
	public List<CommandSpec> getCommandSpecs() {
		return m_commandSpecs;
	}
	
	public void setCommandSpecs(List<CommandSpec> commandSpecs) {
		m_commandSpecs = commandSpecs;
	}
	
	@JsonIgnore
	public ParamSpec getParamSpec(String name){
		for (ParamSpec ps : getParamSpecs()){
			if (ps.getName() != null && ps.getName().equals(name)){
				return ps;
			}
		}
		return null;
	}
	
	@JsonIgnore
	public CommandSpec getCommandSpec(String name){
		List<CommandSpec> commandSpecs = getCommandSpecs();
		for (CommandSpec cs : commandSpecs){
			if (cs.getName() != null && cs.getName().equals(name)){
				return cs;
			}
		}
		return null;
	}

	public List<ParamSpec> getStationParams() {
		return m_stationParams;
	}

	public void setStationParams(List<ParamSpec> stationParams) {
		m_stationParams = stationParams;
	}

	public List<ParamSpec> getSegmentParams() {
		return m_segmentParams;
	}

	public void setSegmentParams(List<ParamSpec> segmentParams) {
		m_segmentParams = segmentParams;
	}

	public List<ParamSpec> getPlanParams() {
		return m_planParams;
	}

	public void setPlanParams(List<ParamSpec> planParams) {
		m_planParams = planParams;
	}

	public List<String> getStationSequenceCommands() {
		return m_stationSequenceCommands;
	}

	public void setStationSequenceCommands(List<String> stationSequenceCommands) {
		m_stationSequenceCommands = stationSequenceCommands;
	}

	public List<String> getSegmentSequenceCommands() {
		return m_segmentSequenceCommands;
	}

	public void setSegmentSequenceCommands(List<String> segmentSequenceCommands) {
		m_segmentSequenceCommands = segmentSequenceCommands;
	}
}
