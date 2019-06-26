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
import gov.nasa.arc.irg.plan.model.TypedObject;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonSetter;
import org.codehaus.jackson.annotate.JsonTypeInfo;

@JsonTypeInfo(  
	    use = JsonTypeInfo.Id.NAME,  
	    include = JsonTypeInfo.As.PROPERTY,  
	    property = "type")
public class CommandSpec extends TypedObject {
	Logger logger = Logger.getLogger(CommandSpec.class);
	
	protected String m_parentClassName;
	protected boolean m_abstract;
	protected List<ParamSpec> m_paramSpecs = new ArrayList<ParamSpec>();
	protected Object m_defaults;
	protected CommandSpec m_parent;
	protected Class<? extends PlanCommand> m_commandClass;
	protected String m_color = "#555555";	// color for display in the UI
	protected boolean m_blocking = true;  // does this command block progress of other commands
	protected boolean m_scopeTerminate = true;  // Non-blocking commands only -- executive should terminate this command when it reaches the end of the scope containing the command.

	/**
	 * Climb up the param spec hierarchy and populate param spec list
	 * look up the java class associated with this command spec
	 */
	public void lookupValues() {
		if (m_parent != null){
			List<ParamSpec> parentSpecs = new ArrayList<ParamSpec>();
			m_parent.getParamSpecsFromParent(parentSpecs);
			m_paramSpecs.addAll(0,parentSpecs);
		}
	}
	
	/**
	 * Recurse upward and build a comprehensive list of parameter specs
	 * TODO support base class Command and getting its contents.
	 * @param psList
	 */
	protected void getParamSpecsFromParent(List<ParamSpec> psList){
		if (m_parent != null){
			m_parent.getParamSpecsFromParent(psList);
		}
		if (m_paramSpecs != null){
			psList.addAll(m_paramSpecs);
		}
	}
	
	
	
	@JsonIgnore
	public CommandSpec getParent() {
		return m_parent;
	}

	@JsonIgnore
	public void setParent(CommandSpec parent) {
		m_parent = parent;
	}

	public String getParentClassName() {
		return m_parentClassName;
	}
	
	@JsonSetter("parent")
	public void setParentClassName(String parentClassName) {
		m_parentClassName = parentClassName;
	}
	
	public boolean isAbstract() {
		return m_abstract;
	}
	
	public void setAbstract(boolean abstract1) {
		m_abstract = abstract1;
	}
	
	public List<ParamSpec> getParamSpecs() {
		return m_paramSpecs;
	}
	
	@JsonSetter("params")
	public void setParamSpecs(List<ParamSpec> paramSpecs) {
		m_paramSpecs = paramSpecs;
	}

	@JsonIgnore
	public Class<? extends PlanCommand> getCommandClass() {
		return m_commandClass;
	}

	@JsonIgnore
	public void setCommandClass(Class<? extends PlanCommand> commandClass) {
		m_commandClass = commandClass;
	}

	public Object getDefaults() {
		return m_defaults;
	}

	public void setDefaults(Object defaults) {
		m_defaults = defaults;
	}
	
	/**
	 * @return the color
	 */
	public String getColor() {
		return m_color;
	}

	/**
	 * @param color the color to set
	 */
	public void setColor(String color) {
		String oldColor = m_color;
		m_color = color;
		firePropertyChange("color", oldColor, m_color);
	}

	/**
	 * @return the blocking
	 */
	public boolean isBlocking() {
		return m_blocking;
	}

	/**
	 * @param blocking the blocking to set
	 */
	public void setBlocking(boolean blocking) {
		boolean oldBlocking = m_blocking;
		m_blocking = blocking;
		firePropertyChange("blocking", oldBlocking, m_blocking);
	}

	/**
	 * @return the scopeTerminate
	 */
	public boolean isScopeTerminate() {
		return m_scopeTerminate;
	}

	/**
	 * @param scopeTerminate the scopeTerminate to set
	 */
	public void setScopeTerminate(boolean scopeTerminate) {
		boolean oldScopeTerminate = m_scopeTerminate;
		m_scopeTerminate = scopeTerminate;
		firePropertyChange("scopeTerminate", oldScopeTerminate, m_scopeTerminate);
	}

}
