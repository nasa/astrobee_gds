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

import java.util.UUID;

import gov.nasa.arc.irg.util.bean.AbstractModelObject;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeInfo;

@JsonTypeInfo(  
	    use = JsonTypeInfo.Id.NAME,  
	    include = JsonTypeInfo.As.PROPERTY,  
	    property = "type")
public class TypedObject extends AbstractModelObject implements Cloneable{
	protected String m_name;
	protected String m_notes;
	protected String m_id;
	
	public TypedObject() {
		UUID uuid = UUID.randomUUID();
		setId(uuid.toString());
	}
	
	public String getName() {
		if (m_name == null || m_name.isEmpty()){
			return getClass().getSimpleName();
		}
		return m_name;
	}
	
	public void setName(String name) {
		String oldName = m_name;
		if (oldName == null || !oldName.equals(name)){
			m_name = name;
			firePropertyChange("name", oldName, name);
		}
	}
	
	@JsonProperty("notes")
	public String getNotes() {
		return m_notes;
	}
	
	@JsonProperty("notes")
	public void setNotes(String notes) {
		String oldNotes = m_notes;
		if (oldNotes == null || !oldNotes.equals(notes)){
			m_notes = notes;
			firePropertyChange("notes", oldNotes, notes);
		}
	}
	
	public String getId() {
		return m_id;
	}
	
	public void setId(String id) {
		String oldId = m_id;
		if (oldId == null || !oldId.equals(id)){
			m_id = id;
			firePropertyChange("id", oldId, id);
		}
	}
	
	@Override
	public String toString() {
		StringBuffer result =  new StringBuffer();
		if (m_name != null && !m_name.isEmpty()){
			result.append(m_name);
		}
		if (m_notes != null && !m_notes.isEmpty()){
			result.append(": ");
			result.append(m_notes);
		}
		return result.toString();
	}

	@Override
	public TypedObject clone() throws CloneNotSupportedException {
		TypedObject newTypedObject =  (TypedObject)super.clone();
		newTypedObject.setId(getId());
		newTypedObject.setName(getName());
		newTypedObject.setNotes(getNotes());
		return newTypedObject;
	}
	
}
