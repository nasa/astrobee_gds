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

import java.util.HashMap;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties({"type"})
public class Crs implements Cloneable{
	protected HashMap<String, Object> m_properties = new HashMap<String, Object>();

	public HashMap<String, Object> getProperties() {
		return m_properties;
	}

	public void setProperties(HashMap<String, Object> properties) {
		m_properties = properties;
	}
	
	@Override
	public Crs clone() throws CloneNotSupportedException {
		Crs newCrs =  (Crs)super.clone();
		HashMap<String, Object> newProperties = new HashMap<String, Object>();
		
		for (String key : m_properties.keySet()){
			newProperties.put(key, m_properties.get(key));
		}
		newCrs.setProperties(newProperties);
		return newCrs;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		for (String key : m_properties.keySet()){
			result = prime * result + ((key == null) ? 0 : key.hashCode());
			Object prop =  m_properties.get(key);
			result = prime * result + ((prop == null) ? 0 : prop.hashCode());
		}
		return result;
	}
	
	@Override
	public boolean equals(Object o) {
		if(this == o) {
			return true;
		}
		// Don't check super - will fail if not same object
		// instanceof returns false for null
		if(!(o instanceof Crs)) {
			return false;
		}
		Crs other = (Crs)o;
		
		HashMap<String, Object> other_prop = other.getProperties();
		for (String key : m_properties.keySet()){
			Object thing = m_properties.get(key);
			Object otherthing = other_prop.get(key);
			if (thing == null) {
				if (otherthing != null) {
					return false;
				}
			} else if (!thing.equals(otherthing)) {
				return false;
			}
		}
		// if we don't check both ways, it's not symmetric
		for (String key : other_prop.keySet()){
			Object thing = m_properties.get(key);
			Object otherthing = other_prop.get(key);
			if (thing == null) {
				if (otherthing != null) {
					return false;
				}
			} else if (!thing.equals(otherthing)) {
				return false;
			}
		}
		
		
		return true;
	}
	
}
