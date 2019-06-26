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
package gov.nasa.arc.irg.plan.freeflyer.command;


import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonTypeInfo;

@JsonTypeInfo(  
	    use = JsonTypeInfo.Id.NAME,  
	    include = JsonTypeInfo.As.PROPERTY,  
	    property = "type")
//@OrderedWidgets(children = { "id", "name", "notes", "duration" })
/** formerly StationKeep */
public class Wait extends FreeFlyerCommand {
	protected int m_duration = 5;

	public Wait() {
		super();
	}
	
	@JsonIgnore
	public static String getClassNameForWidgetDropdown() {
		return "Wait";
	}
	
	@Override
	public Wait clone() throws CloneNotSupportedException {
		Wait newPause = (Wait) super.clone();
		newPause.setDuration(getDuration());
		return newPause;
	}
	
//	@Limits(max=86400000,min=0)
//	@Description("Seconds")
//	@UnitsLabel("s")
	public int getDuration() {
		if(m_duration < 0)
			return 0;
		return m_duration;
	}

	public void setDuration(int duration) {
		int oldDuration = m_duration;
		m_duration = duration;
		firePropertyChange("duration", oldDuration, m_duration);
	}
	
	@Override
	protected String customMembersToShortString() {
		StringBuffer sb = new StringBuffer();
//		if (getDuration() > 0){
//			sb.append(getDuration());
//			sb.append(" s");
//		}
		return sb.toString();
	}
	
	@Override
	protected String customMembersToString() {
		StringBuffer sb = new StringBuffer();
		if (getDuration() > 0){
			sb.append(getDuration());
			sb.append(" s");
		}
		if (getNotes() != null && !getNotes().isEmpty()) {
			sb.append(" //");
			sb.append(getNotes());
		}
		return sb.toString();
	}

	@Override
	public int getCalculatedDuration() {
		return getDuration();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + m_duration;
		return result;
	}
	
	@Override
	public boolean equals(Object o) {
		if(this == o) {
			return true;
		}
		// instanceof returns false for null
		if(!(o instanceof Wait)) {
			return false;
		}
		if(!super.equals(o)) {
			return false;
		}
		
		Wait other = (Wait)o;
		if(m_duration != other.getDuration()) {
			return false;
		}
		return true;
	}

//	@Override
//	public void refresh(Sequenceable seq) {
//		// TODO Auto-generated method stub
//		
//	}
}
