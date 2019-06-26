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

import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonAnyGetter;
import org.codehaus.jackson.annotate.JsonAnySetter;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonTypeInfo;

/**
 * A particular, single command in a plan.
 * It can include params, which are the children or settings for this particular command.
 * 
 * @author tecohen
 *
 */

@JsonTypeInfo(  
	    use = JsonTypeInfo.Id.NAME,  
	    include = JsonTypeInfo.As.PROPERTY,  
	    property = "type")

public abstract class PlanCommand extends TypedObject implements Sequenceable  {
	
	// These are for extra things in the json file that are not called out in the Java command classes
	protected Map<String, Object> m_params = new HashMap<String, Object>();
	
	protected Position m_startPosition = null;
	
	protected Position m_endPosition = null;
	
	protected int m_startTime = 0;		// calculated start time in seconds from beginning of plan
	
	protected SequenceHolder m_parent = null;  // container
	
	protected String m_typeCode; // type code identifier used for generating ids
	
	protected String m_color = "#555555";	// color for display in the UI
	
	protected boolean m_blocking = true;  // does this command block progress of other commands
	
	protected boolean m_scopeTerminate = true;  // Non-blocking commands only -- executive should terminate this command when it reaches the end of the scope containing the command.
	
	protected String m_presetName = null; // sometimes known as profile name, this is a descriptive tip to roverSW

	protected String m_presetCode = null; // related to presetname, this maps to the library where the preset came from.

	protected Sequenceable m_next;
	protected Sequenceable m_previous;

	public PlanCommand() {
		m_params = new HashMap<String, Object>();
		setEndPosition(Position.makeNeutralPosition());
		setStartPosition(Position.makeNeutralPosition());
	}
	
	@JsonIgnore
	public int getRapidNumber() {
		Sequenceable parent = (Sequenceable)getParent();
		return parent.getRapidNumber();
	}
		
	@JsonIgnore
	public int getCommandNumber() {
		String[] parts = getName().split(". ");
		return Integer.valueOf(parts[1]);
	}
	
	/**
	 * @return the typeCode
	 */
	public String getTypeCode() {
		return m_typeCode;
	}

	/**
	 * @param typeCode the typeCode to set
	 */
	public void setTypeCode(String typeCode) {
		String oldTypeCode = m_typeCode;
		m_typeCode = typeCode;
		if (oldTypeCode == null || !oldTypeCode.equals(typeCode)){
			firePropertyChange("typeCode", oldTypeCode, typeCode);
		}
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
		if (oldColor == null || !color.equals(oldColor)){
			firePropertyChange("color", oldColor, m_color);
		}
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
		if (oldBlocking != blocking){
			firePropertyChange("blocking", oldBlocking, m_blocking);
		}
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
		if (oldScopeTerminate != scopeTerminate){
			firePropertyChange("scopeTerminate", oldScopeTerminate, m_scopeTerminate);
		}
	}

	@JsonIgnore
	public Position getStartPosition() {
		return m_startPosition;
	}


	@JsonIgnore
	public void setStartPosition(Position startPosition) {
		Position oldPosition = m_startPosition;
		m_startPosition = startPosition;
		if (oldPosition == null || !oldPosition.equals(startPosition)){
			firePropertyChange("startPosition", oldPosition, m_startPosition);
		}
	}


	@JsonIgnore
	public Position getEndPosition() {
		return m_endPosition;
	}


	@JsonIgnore
	public void setEndPosition(Position endPosition) {
		Position oldPosition = m_endPosition;
		m_endPosition = endPosition;
		if (oldPosition == null || !oldPosition.equals(endPosition)){
			firePropertyChange("endPosition", oldPosition, m_endPosition);
		}
	}
	
	/*
	 * Orientation is stored in position.  If your PlanCommand will affect orientation, update the end position within this method.
	 * By default, start and end position are the same.
	 */
	@JsonIgnore
	public void calculateEndPosition(){
		try {
			setEndPosition(getStartPosition().clone());
		} catch (CloneNotSupportedException e) {
			// but it is
		}
	}


	/**
	 * @return the startTime
	 */
	@JsonIgnore
	public int getStartTime() {
		return m_startTime;
	}

	/**
	 * @param startTime the startTime to set
	 */
	@JsonIgnore
	public void setStartTime(int startTime) {
		int oldStartTime = m_startTime;
		m_startTime = startTime;
		if (oldStartTime != startTime){
			firePropertyChange("startTime", oldStartTime, m_startTime);
		}
	}

	public String getPresetName() {
		return m_presetName;
	}

	public void setPresetName(String presetName) {
		m_presetName = presetName;
	}

	public String getPresetCode() {
		return m_presetCode;
	}

	public void setPresetCode(String presetCode) {
		m_presetCode = presetCode;
	}

	/**
	 * @return the parent
	 */
	@JsonIgnore
	public SequenceHolder getParent() {
		return m_parent;
	}

	/**
	 * @param parent the parent to set
	 */
	@JsonIgnore
	public void setParent(SequenceHolder parent) {
		SequenceHolder oldParent = m_parent;
		m_parent = parent;
		if (oldParent == null || !oldParent.equals(parent)){
			firePropertyChange("parent", oldParent, m_parent);
			if(m_parent instanceof Station) {
				setStartPosition(((Station)m_parent).getEndPosition());
				setEndPosition(((Station)m_parent).getEndPosition());
			}
		}
	}

	@Override
	public PlanCommand clone() throws CloneNotSupportedException {
		//TODO verify we are cloning everything we need
		PlanCommand newCommand =  (PlanCommand)super.clone();
		for (String key : getParams().keySet()){
			newCommand.setParams(key, getParams().get(key));
		}
		newCommand.setStartPosition(getStartPosition().clone());
		newCommand.setEndPosition(getEndPosition().clone());
		newCommand.setStartTime(getStartTime());
		newCommand.setPresetCode(getPresetCode());
		newCommand.setPresetName(getPresetName());
		newCommand.setParent(getParent());
		return newCommand;
	}

	// "any getter" needed for serialization    
    @JsonAnyGetter
    public Map<String,Object> getParams() {
        return m_params;
    }

    @JsonAnySetter
    public void setParams(String name, Object value) {
    	Object old = m_params.get(name);
        m_params.put(name, value);
        if (old == null || !old.equals(value)){
        	firePropertyChange(name, old, value);
        }
    }
    
    @JsonIgnore
    public Object get(String name){
    	return m_params.get(name);
    }
    
    @JsonIgnore
    public void put(String key, Object value){
    	setParams(key, value);
    }
    
	public Object remove(String key) {
		Object old = m_params.get(key);
		Object result = m_params.remove(key);
		firePropertyChange(key, old, null);
		return result;
	}

	public void clearParams() {
		if (!m_params.isEmpty()){
			m_params.clear();
			firePropertyChange("params", m_params, m_params);
		}
	}

	@Override
	@JsonIgnore
	public void setPrevious(Sequenceable s) {
		m_previous = s;
		if(m_previous != null) {
			// this is probably a hint that you need to change your name
			String oldName = m_name;
			// Name format is "<STN>.<CMD> <TYPE>"
			String delims = "[. ]";
			String[] tokens = m_previous.getName().split(delims);

			int newNumber = Integer.valueOf(tokens[1]) + 1;
			String newName = tokens[0] + "." + newNumber + " " +getClass().getSimpleName();

			if(!newName.equals(oldName)) {
				m_name = newName;
				// tell the next guy
				if(m_next != null) {
					m_next.setPrevious(this);
				}
			}
		}
	}

	@Override
	@JsonIgnore
	public void setNext(Sequenceable s) {
		m_next = s;
	}

	@Override
	@JsonIgnore
	public int getEndTime() {
		return getStartTime() + getCalculatedDuration();
	}
	
	@Override
	public void inheritDefaults() {
		// noop by default
	}

	public String toShortString(){
		StringBuilder result = new StringBuilder();
		if (getName() != null && !getName().isEmpty()){
			result.append(getName());
		} else {
			result.append(getClass().getSimpleName());
		}
		String members = customMembersToShortString();
		if (members.length() > 0){
			result.append(" ");
			result.append(members);
		}
		return result.toString();
	}
	
	protected String customMembersToShortString(){
		return("");
	}

	protected String customMembersToString(){
		return ("");
	}
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();

		result.append(getClass().getSimpleName());
		result.append(" ");
		
		if (getId() != null && !(getId().isEmpty())){
			result.append(getId());
			result.append(":");
		}
		
		if (getName() != null && !(getName().isEmpty())){
			result.append(getName());
		}
		result.append(" (");
		
		result.append(customMembersToString());
		
		result.append(")");
		

		if (getNotes() != null && !getNotes().isEmpty()){
			result.append(" //");
			if (getNotes().length() > 30){
				result.append(getNotes().substring(0, 30));
			} else {
				result.append(getNotes());
			}
		}
		return result.toString();
	}
	
	/*
	 * Return a label to be used for this element such as in Run Plan View 
	 */
	@JsonIgnore
	public String getLabel() {
		return this.getClass().getSimpleName();
	}
	
	public Sequenceable getNext() {
		return m_next;
	}

	public Sequenceable getPrevious() {
		return m_previous;
	}
	
	
}
