/*******************************************************************************
 * Copyright (c) 2013 United States Government as represented by the 
 * Administrator of the National Aeronautics and Space Administration. 
 * All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package gov.nasa.ensemble.ui.databinding.widgets.customization;

/**
 * Used to define entries in a combo
 * @author tecohen
 *
 */
public class ComboEntry {
	protected String m_label;
	protected Object m_data;
	
	public ComboEntry(String label, Object data){
		m_label = label;
		m_data = data;
	}
	
	public String getLabel() {
		return m_label;
	}
	public void setLabel(String label) {
		m_label = label;
	}
	public Object getData() {
		return m_data;
	}
	public void setData(Object data) {
		m_data = data;
	}
	
	@Override
	public String toString() {
		return getLabel();
	}
	
	public String getName(){
		return getLabel();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null){
			return false;
		}
		if (obj instanceof ComboEntry){
			ComboEntry ce = (ComboEntry)obj;
			boolean result = ce.getLabel().equals(getLabel());
			if (getData() != null && ce.getData() != null){
				result |= ce.getData().equals(getData());
			}
			return result;
		}
		if (getData() != null){
			return (obj.equals(getData()));
		}
		
		return super.equals(obj);
	}
}
