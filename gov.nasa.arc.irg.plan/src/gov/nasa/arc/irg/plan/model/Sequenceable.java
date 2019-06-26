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

import gov.nasa.arc.irg.util.bean.IHasPropertyChangeListeners;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonSubTypes.Type;

/**
 * This is an interface for elements that can be ordered in a sequence, ie stations, segments, and commands.
 * @author tecohen
 *
 */
@JsonTypeInfo(  
	    use = JsonTypeInfo.Id.NAME,  
	    include = JsonTypeInfo.As.PROPERTY,  
	    property = "type")
@JsonSubTypes({  
	@Type(value = Sequenceable.class, name = "Sequenceable"),  
    @Type(value = Station.class, name = "Station"), 
    @Type(value = Segment.class, name = "Segment"),  
    @Type(value = PlanCommand.class, name = "Command"),  
})
public interface  Sequenceable extends IHasPropertyChangeListeners, Cloneable {

	/**
	 * Set the start time (from plan start) in seconds
	 * This will depend on previous start times and durations and will not be serialized to a file
	 * @param startTime
	 */
	@JsonIgnore
	public void setStartTime(int startTime);

	/**
	 * @return the estimated start time (from plan start) in seconds
	 */
	@JsonIgnore
	public int getStartTime();
	
	/**
	 * @return the estimated end time (from plan start) in seconds
	 */
	@JsonIgnore
	public int getEndTime();
	
	/**
	 * Return a value if your sequenceable blocks for any amount of  time, in seconds.
	 * Note this is an estimate and will not be serialized to a file.
	 * @return
	 */
	@JsonIgnore
	public int getCalculatedDuration();
	
	/**
	 * identifier for this particular sequenceable element
	 * @return
	 */
	public String getId();
	
	/**
	 * @param id
	 */
	public void setId(String id);
	
	/**
	 * Name is not required
	 * @return the name
	 */
	public String getName();
	
	/**
	 * @param name
	 */
	public void setName(String name);
	
	/**
	 * Notes is not required
	 * @return notes
	 */
	public String getNotes();
	
	/**
	 * @param noets
	 */
	public void setNotes(String notes);
	
	/**
	 * Tell this sequenceable who came before it so it can update its start position if need be
	 * Times are not updated by this method
	 * @param s
	 */
	@JsonIgnore
	public void setPrevious(Sequenceable s);
	
	/**
	 * Tell this sequenceable who came after it so it can update its end position if need be.
	 * Times are not updated by this method
	 * @param s
	 */
	@JsonIgnore
	public Sequenceable getNext();

	/**
	 * Tell this sequenceable who came before it so it can update its start position if need be
	 * Times are not updated by this method
	 * @param s
	 */
	@JsonIgnore
	public Sequenceable getPrevious();
	
	/**
	 * Tell this sequenceable who came after it so it can update its end position if need be.
	 * Times are not updated by this method
	 * @param s
	 */
	@JsonIgnore
	public void setNext(Sequenceable s);
	
	/**
	 * The start position is calculated (estimated) based on prior sequenceable elements and is not serialized to a file
	 * @return
	 */
	@JsonIgnore
	public Position getStartPosition();
	
	/**
	 * @param p
	 */
	@JsonIgnore
	public void setStartPosition(Position p);
	
	/**
	 * The end position is calculated (estimated) based on prior sequenceable elements and is not serialized to a file
	 * @return
	 */
	@JsonIgnore
	public Position getEndPosition();
	
	/**
	 * @param p
	 */
	@JsonIgnore
	public void setEndPosition(Position p);
	
	/**
	 * @return
	 * @throws CloneNotSupportedException
	 */
	@JsonIgnore
	public Sequenceable clone() throws CloneNotSupportedException;
	
	/**
	 * The Sequence Holder which contains this sequenceable
	 * @return
	 */
	@JsonIgnore
	public SequenceHolder getParent();
	
	/**
	 * @param parent
	 */
	@JsonIgnore
	public void setParent(SequenceHolder parent);
	
	/*
	 * return a shorter string suitable for table display
	 */
	@JsonIgnore
	public String toShortString();
	
	/*
	 * Automatically create a new id for self
	 */
	@JsonIgnore
	public void autoName(int index);
	
	/**
	 * This is a convenience method to initialize default values of a child sequenceable when it is added to a sequence holder.
	 * For example, if you are adding a Drive to a container (ie plan) it should inherit the plan's default speed.
	 */
	@JsonIgnore
	public void inheritDefaults();

	/**
	 * This is a convenience method to return the number of the sequenceable if a plan were numbered
	 * Station 0, Segment 1, Station 2, Segment 3, Station 4, etc.
	 * For PlanCommands, returns the number of the parent station
	 */
	@JsonIgnore
	public int getRapidNumber();
	
	/*
	 * Return a label to be used for this element such as in Run Plan View 
	 */
	@JsonIgnore
	public String getLabel();
	
	/**
	 * call this when a neighboring Sequenceable has changed
	 * updates calculated fields
	 */
	public void refresh(Sequenceable seq);
}
