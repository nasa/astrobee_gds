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

import gov.nasa.arc.irg.plan.model.Sequenceable;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonTypeInfo;

@JsonTypeInfo(  
	    use = JsonTypeInfo.Id.NAME,  
	    include = JsonTypeInfo.As.PROPERTY,  
	    property = "type")
//@OrderedWidgets(children = { "id", "name", "notes", "state", "duration" })
public class Record extends FreeFlyerCommand {

	public enum State {start, stop, duration}
	
	protected State m_state = State.start;
	protected int m_duration = -1;
	
	public Record() {
		super();
	}
	
	@JsonIgnore
	public static String getClassNameForWidgetDropdown() {
		return "Record";
	}
	
	@Override
	public Record clone() throws CloneNotSupportedException {
		Record newRecord = (Record) super.clone();
		newRecord.setState(getState());
		newRecord.setDuration(getDuration());
		return newRecord;
	}

//	@Limits(max=86400,min=0)
//	@Description("Milliseconds")
//	@UnitsLabel("ms")
	public int getDuration() {
		if(m_duration < 0) {
			return 0;
		}
		return m_duration;
	}

	public void setDuration(int duration) {
		int oldDuration = m_duration;
		m_duration = duration;
		firePropertyChange("duration", oldDuration, m_duration);
	}

	public State getState() {
		return m_state;
	}

	/**
	 * Automatically sets duration to -1 if this is a start or stop record command
	 * @param state
	 */
	public void setState(State state) {
		State oldState = m_state;
		m_state = state;
		firePropertyChange("state", oldState, m_state);
		if (m_state != State.duration){
			int oldDuration = m_duration;
			m_duration = -1;
			firePropertyChange("duration", oldDuration, m_duration);
		}
	}
	
	@Override
	protected String customMembersToShortString() {
		String result =  super.customMembersToShortString();
		StringBuffer sb = new StringBuffer(result);
		if (getState() != null){
			sb.append(getState().toString());
		}
		if (getDuration() > 0){
			sb.append(" ");
			sb.append(getDuration());
			sb.append(" s");
		}
		return sb.toString();
	}
	
	@Override
	protected String customMembersToString() {
		String result =  super.customMembersToString();
		StringBuffer sb = new StringBuffer(result);
		if (getState() != null){
			sb.append(getState().toString());
		}
		if (getDuration() > 0){
			sb.append(" for ");
			sb.append(getDuration());
			sb.append(" s");
		}
		return sb.toString();
	}

	@Override
	public int getCalculatedDuration() {
		if ((getState() != null) && (getState().equals(State.duration))){
			return getDuration();
		}
		return 0;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + m_state.hashCode();
		result = prime * result + m_duration;
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if(this == o) {
			return true;
		}
		// instanceof returns false for null
		if(!(o instanceof Record)) {
			return false;
		}
		if(!super.equals(o)) {
			return false;
		}
		Record other = (Record)o;
	
		if(!m_state.equals(other.getState())) {
			return false;
		}
		if(getDuration() != other.getDuration()) {
			return false;
		}
		return true;
	}

	@Override
	public void refresh(Sequenceable seq) {
		// TODO Auto-generated method stub
		
	}
	
}
