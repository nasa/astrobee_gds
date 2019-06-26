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

import org.codehaus.jackson.annotate.JsonTypeInfo;

@JsonTypeInfo(  
	    use = JsonTypeInfo.Id.NAME,  
	    include = JsonTypeInfo.As.PROPERTY,  
	    property = "type")
public class Platform extends TypedObject {

	// class for serialization of plan libraries' platform definitions
	
	@Override
	public Platform clone() throws CloneNotSupportedException {
		return (Platform)super.clone();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
	
		result = prime * result + ((m_name == null) ? 0 : m_name.hashCode());
		result = prime * result + ((m_notes == null) ? 0 : m_notes.hashCode());
		result = prime * result + ((m_id == null) ? 0 : m_id.hashCode());
		
		return result;
	}
	
	@Override
	public boolean equals(Object o) {
		if(this == o) {
			return true;
		}
		// instanceof returns false for null
		if(!(o instanceof Platform)) {
			return false;
		}
		Platform other = (Platform)o;
		
		if(!getName().equals(other.getName())) {
			return false;
		}
		if(getNotes() == null) {
			if(other.getNotes() != null) {
				return false;
			}
		} else if(!getNotes().equals(other.getNotes())) {
			return false;
			
		}
		if(getId() == null) {
			if(other.getId() != null) {
				return false;
			}
		} else if(!getId().equals(other.getId())) {
			return false;
		}
		return true;
	}
	
}
