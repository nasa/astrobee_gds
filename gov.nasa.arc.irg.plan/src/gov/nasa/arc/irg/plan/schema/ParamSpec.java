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

import gov.nasa.arc.irg.plan.model.Point;
import gov.nasa.arc.irg.plan.model.TypedObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonSetter;
import org.codehaus.jackson.annotate.JsonTypeInfo;

@JsonTypeInfo(  
	    use = JsonTypeInfo.Id.NAME,  
	    include = JsonTypeInfo.As.PROPERTY,  
	    property = "type")
@JsonIgnoreProperties(ignoreUnknown = true)
@SuppressWarnings("rawtypes")
public class ParamSpec extends TypedObject {
	Logger logger = Logger.getLogger(ParamSpec.class);

	protected String m_parentClassName;		// the name of the parent param spec
	protected ParamSpec m_parent = null; 	// the parent param spec
	
	protected String m_valueClassName;	 	// the name of the class for the value this param is describing.
	protected Class m_valueClass = null; 	// the Java class for the value
	
	protected Number m_minimum;				// minimum number for value
	protected Number m_maximum;				// maximum number for value
	protected List<List<String>> m_choices;		// list of strings for a combo
	protected List<String> m_simpleChoices;	// simplify the choices to strings
	protected boolean m_visible = true;		// false if we want to hide this parameter from the UI
	protected boolean m_editable = true;	// false if we want to disable editing of this parameter
	protected boolean m_required = false;	// true if this parameter is required
	
	protected Object m_default = null;		// default value
	
	protected String m_unit;				// the unit
	
	/**
	 * Climb up the param spec hierarchy and populate values.
	 * child values clobber parent values
	 * 
	 */
	public void lookupValues() {
		if (m_parent != null){
			ParamSpec valuesFromParent = new ParamSpec();
			m_parent.populateValuesFromParent(valuesFromParent);
			valuesFromParent.copyValues(this);
		}
		
		// flesh out the class for this value type
		if (getValueClass() == null){
			lookupValueClass();
		}
	}
	
	protected void lookupValueClass() {
		if (m_valueClassName != null && !m_valueClassName.isEmpty()){
			if (m_valueClassName.equals("string")) {
				m_valueClass = String.class;
			} else if (m_valueClassName.equals("integer")){
				m_valueClass = Integer.class;
			} else if (m_valueClassName.equals("number")){
				m_valueClass = Double.class;
			} else if (m_valueClassName.equals("boolean")){
				m_valueClass = Boolean.class;
			} else if (m_valueClassName.equals("coords")){
				m_valueClass = Coords.class;
			}  else if (m_valueClassName.equals("date-time")){
				m_valueClass = Date.class;
			} else if (m_valueClassName.equals("Point")){
				m_valueClass = Point.class;
			} else {
				try {
					Class foundClass = Class.forName(m_valueClassName);
					m_valueClass = foundClass;
				} catch (ClassNotFoundException e) {
					logger.error("No class found for " + m_valueClassName, e);
				}
			}
		}
	}
	
	/**
	 * Climb up the parent tree and populate all non-null values.
	 * @param aggregate
	 */
	protected void populateValuesFromParent(ParamSpec aggregate) {
		if (m_parent != null){
			m_parent.populateValuesFromParent(aggregate);
		}
		
		copyValues(aggregate);
	}
	
	
	/**
	 * Populate the values in the aggregate param spec with the values from this param spec.
	 * 
	 * @param destination
	 */
	protected void copyValues(ParamSpec destination){
		if (getName() != null && !getName().isEmpty()){
			destination.setName(getName());
		}
		if (getNotes() != null && !getNotes().isEmpty()){
			destination.setNotes(getNotes());
		}
		if (getValueClassName() != null && !getValueClassName().isEmpty()){
			destination.setValueClassName(getValueClassName());
		}
		if (getMinimum() != null){
			destination.setMinimum(getMinimum());
		}
		if (getMaximum() != null){
			destination.setMaximum(getMaximum());
		}
		if (getChoices() != null && !getChoices().isEmpty()){
			destination.setChoices(getChoices());
		}
		destination.setVisible(isVisible());
		destination.setEditable(isEditable());
	}
	
	
	@JsonIgnore
	public ParamSpec getParent() {
		return m_parent;
	}

	@JsonIgnore
	public void setParent(ParamSpec parent) {
		m_parent = parent;
	}

	public Class getValueClass() {
		return m_valueClass;
	}

	public void setValueClass(Class valueClass) {
		m_valueClass = valueClass;
	}

	public String getParentClassName() {
		return m_parentClassName;
	}
	
	@JsonSetter("parent")
	public void setParentClassName(String parentClassName) {
		m_parentClassName = parentClassName;
	}
	
	public String getValueClassName() {
		return m_valueClassName;
	}
	
	@JsonSetter("valueType")
	public void setValueClassName(String valueClassName) {
		m_valueClassName = valueClassName;
	}
	
	public Number getMinimum() {
		return m_minimum;
	}
	
	public void setMinimum(Number minimum) {
		m_minimum = minimum;
	}
	
	public Number getMaximum() {
		return m_maximum;
	}
	
	public void setMaximum(Number maximum) {
		m_maximum = maximum;
	}
	
	public List<String> getSimpleChoices() {
		if (m_simpleChoices == null){
			m_simpleChoices = new ArrayList<String>();
			for (List<String> complex : getChoices()){
				if (!complex.isEmpty()){
					m_simpleChoices.add(complex.get(0));
				}
			}
		}
		return m_simpleChoices;
		
	}
	
	public List<List<String>> getChoices() {
		return m_choices;
	}
	
	public void setChoices(List<List<String>> choices) {
		m_choices = choices;
	}
	
	public boolean isVisible() {
		return m_visible;
	}
	
	public void setVisible(boolean visible) {
		m_visible = visible;
	}
	
	public boolean isEditable() {
		return m_editable;
	}
	
	public void setEditable(boolean editable) {
		m_editable = editable;
	}

	/**
	 * @return the required
	 */
	public boolean isRequired() {
		return m_required;
	}

	/**
	 * @param required the required to set
	 */
	public void setRequired(boolean required) {
		m_required = required;
	}

	/**
	 * @return the default
	 */
	public Object getDefault() {
		return m_default;
	}

	/**
	 * @param default1 the default to set
	 */
	public void setDefault(Object default1) {
		m_default = default1;
	}

	public String getUnit() {
		return m_unit;
	}

	public void setUnit(String unit) {
		m_unit = unit;
	}
}
