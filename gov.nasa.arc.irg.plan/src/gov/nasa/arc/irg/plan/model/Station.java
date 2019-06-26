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

import gov.nasa.arc.irg.plan.modulebay.ModuleBayPoint;
import gov.nasa.arc.irg.plan.modulebay.ModuleBayStation;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonSubTypes.Type;
import org.codehaus.jackson.annotate.JsonTypeInfo;

@JsonTypeInfo(  
	    use = JsonTypeInfo.Id.NAME,  
	    include = JsonTypeInfo.As.PROPERTY,  
	    property = "type")
@JsonSubTypes({  
	@Type(value = ModuleBayStation.class, name = "ModuleBayStation"),  
})
public class Station extends SequenceHolder implements Sequenceable {
	protected Point6Dof coordinate;
	protected float tolerance = 0.1f;
	protected boolean stopOnArrival;
	protected Sequenceable next;
	protected Sequenceable previous;
	protected SequenceHolder parent;
	
	public Station() {
		super();
		init();
	}
	
	protected void init() {
		coordinate = new Point6Dof();
		coordinate.addPropertyChangeListener(this);
	}
	
	public Sequenceable moveThisCommandUp(Sequenceable move) {
		// get the command before it so we know where to put it back
		Sequenceable prevCommand = getPreviousSequenceable(move);
		
		Sequenceable saved = removeSequenceable(move);
		
		saved.setPrevious(null);
		saved.setNext(null);
		
		// then add it back in one position higher
		insertSequenceable(saved, prevCommand);
		
		return saved;
	}
	
	public Sequenceable moveThisCommandDown(Sequenceable move) {
		// get the previous command so we know where to put it back
		Sequenceable nextCommand = getNextSequenceable(move);
		
		Sequenceable saved = removeSequenceable(move);
		
		saved.setPrevious(null);
		saved.setNext(null);
		
		// then add it back in one position lower
		if(indexOf(nextCommand) == size()-1) {
			// we can just append the command
			addSequenceable(saved);
		} else {
			int insertionIndex = indexOf(nextCommand) + 1;
			addSequenceable(insertionIndex, saved);
		}
		
		return saved;
	}
	
	/**
	 * Remove the sequenceable at the given index
	 * @param index
	 * @return
	 */
	@Override
	public Sequenceable removeSequenceable(int index) {
		List<Sequenceable> oldSequence = new ArrayList<Sequenceable>();
		oldSequence.addAll(m_sequence);

		Sequenceable next = null;
		Sequenceable previous = null;
		Sequenceable cut = m_sequence.get(index);
		int changedIndex = index;
		
		// update the other sequenceables per this removal
		if( (index+1) < m_sequence.size() ) {
			next = m_sequence.get(index+1);
		}
		if( index > 0 ) {
			previous = m_sequence.get(index-1);
		}
		
		boolean worked =  m_sequence.remove(cut);
		if (worked){
			cut.removePropertyChangeListener(this);
			cut.setParent(null);

			if (previous != null) {
				previous.setNext(next);
			}
			if (next != null){
				next.setPrevious(previous);
			}
			
			updateTimes(changedIndex);
			updateNames(changedIndex);

			refresh(this);
		}
		
		return cut;
	}
	
	/**
	 * 
	 * Add the sequenceable at the given index
	 * @param index
	 * @param sequenceable
	 * @return
	 */
	@Override
	public boolean addSequenceable(int index, Sequenceable sequenceable) {
		if (index > m_sequence.size() + 1 || index < 0){
			return false;
		}
		List<Sequenceable> oldSequence = new ArrayList<Sequenceable>();
		oldSequence.addAll(m_sequence);
		m_sequence.add(index, sequenceable);

		handleSequenceAddition(sequenceable);
		return true;
	}
	
	@Override
	public void addSequenceable(Sequenceable seq) {
		int size = m_sequence.size();
		addSequenceable(size, seq);

		// this one goes to the TreeViewer in CreatePlanView
		firePropertyChange("command", null, seq);
	}

	@Override
	public void propertyChange(PropertyChangeEvent arg0) {
		if(parent instanceof Plan) {
			((Plan)parent).setValid(false);
		}
		if(arg0.getSource().equals(coordinate)) {
			refresh(this);
		}
	}

	@Override
	public int getEndTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setPrevious(Sequenceable s) {
		previous = s;
		
		if(s != null) {
			refresh(previous);
		}
	}

	@Override
	public Sequenceable getNext() {
		return next;
	}

	@Override
	public Sequenceable getPrevious() {
		return previous;
	}

	@Override
	public void setNext(Sequenceable s) {
		next = s;
	}

	@Override
	public Position getStartPosition() {
		return new Position(coordinate);
	}
	
	@Override
	public void setStartPosition(Position p) {
		setCoordinate(new ModuleBayPoint(p));
	}

	@Override
	/** for backward compatibility.  Use getCoordinate. */
	public Position getEndPosition() {
		return new Position(coordinate);
	}

	@Override
	/** for backward compatibility.  Use setCoordinate. */
	public void setEndPosition(Position p) {
		setCoordinate(new ModuleBayPoint(p));
	}
	
	/** Use this one. */
	public void setCoordinate(ModuleBayPoint start) {
		coordinate = start;
		coordinate.addPropertyChangeListener(this);
		refresh(this); //necessary??
	}
	
	/** Use this one. */
	public Point6Dof getCoordinate() {
		return coordinate;
	}

	@Override
	public Station clone() throws CloneNotSupportedException {
		Station newStation = (Station) super.clone();
		// but ... coordinate is already there ... not sure how ...
//		if(coordinate != null) {
//			newStation.setStartPosition(getStartPosition());
//		}
		newStation.setStopOnArrival(isStopOnArrival());
		newStation.setTolerance(getTolerance());
		return newStation;
	}

	@Override
	public SequenceHolder getParent() {
		return parent;
	}

	@Override
	public void setParent(SequenceHolder parent) {
		this.parent = parent;
	}

	@Override
	public String toShortString() {
		StringBuffer result = new StringBuffer(getClass().getSimpleName());

		if (getName() != null && !(getName().isEmpty())){
			result.append(" ");
			result.append(getName());
		} else if (getClass().getSimpleName() != null && !(getClass().getSimpleName().isEmpty())){
			result.append(" ");
			result.append(getClass().getSimpleName());
		}
		if(coordinate != null) {
			result.append(" ");
			result.append(coordinate.toShortString());
		}
		return result.toString();
	}

	@Override
	public void autoName(int index) {
		setName(String.valueOf(index/2));
		autoNameChildren();
	}

	@Override
	public void inheritDefaults() {
		SequenceHolder parent = getParent();
		Plan plan = parent.getPlan();
		if (plan != null){
			setTolerance(plan.getDefaultTolerance());
		}
	}

	@Override
	public String getLabel() {
		String stationId = getClass().getSimpleName();
		String suffix = stationId;
		try {
			suffix = stationId.substring(stationId.indexOf("STN")+3); //get the station numbers
		} catch (Exception ex) {
			// pass
		}
		return this.getClass().getSimpleName() + " " +suffix;
	}

	@Override
	public void refresh(Sequenceable seq) {
		if(seq == null) {
			return;
		}
		
		// seq is either a child or prev or self
		
		if(seq.equals(getPrevious())) {
			// TODO update time and name
			// if using default orientation, make sure we're up to date
			
				if( getNext()!= null) {
					getNext().refresh(this);
				}
			
		} else if(seq.equals(this)) {
			// our start position changed
			
			if(getNext() != null) {
				getNext().refresh(this);
			}
			if(getPrevious() != null) {
				getPrevious().refresh(this);
				
			}
			// if it's me that changed, fire propertyChange
			firePropertyChange("sequence", null, m_sequence);
			
		} else if (seq.equals(getNext())) {
			// we are in the process of inserting a station
			// the segment will be added shortly
			return;
		} else {
			 // seq is a child. nothing changes
		}
	}
	
	@JsonIgnore
	public Sequenceable getNextSequenceable(Sequenceable ref) {
		int refInd = m_sequence.indexOf(ref);
		if(refInd < 0 || refInd == m_sequence.size()-1) {
			return null;
		}
		return m_sequence.get(refInd+1);
	}
	
	@JsonIgnore
	public Sequenceable getPreviousSequenceable(Sequenceable ref) {
		int refInd = m_sequence.indexOf(ref);
		if(refInd < 1 ) {
			return null;
		}
		return m_sequence.get(refInd-1);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((m_name == null) ? 0 : m_name.hashCode());
		result = prime * result + ((m_notes == null) ? 0 : m_notes.hashCode());
		result = prime * result + Float.floatToIntBits(tolerance);
		result = prime * result + (stopOnArrival ? 1:0);
		result = prime * result + ((coordinate == null) ? 0 : coordinate.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if(this == o) {
			return true;
		}
		if(!super.equals(o)) {
			return false;
		}
		if(!(o instanceof Station)) {
			return false;
		}
		Station other = (Station)o;
		
		if(!getName().equals(other.getName())) {
			return false;
		}
		if(tolerance != other.getTolerance()) {
			return false;
		}
		if(stopOnArrival != other.isStopOnArrival()) {
			return false;
		}
		if(!coordinate.equals(other.getCoordinate())) {
			return false;
		}
		// we will have to start checking start time
		return true;
	}

	@Override
	public int getRapidNumber() {
		return Integer.parseInt(getName())*2;
	}

	public float getTolerance() {
		return tolerance;
	}

	public void setTolerance(float tolerance) {
		float oldTolerance = this.tolerance;
		if (oldTolerance != tolerance){
			this.tolerance = tolerance;
			firePropertyChange("tolerance", oldTolerance, tolerance);
		}
	}
	
	public boolean isStopOnArrival() {
		return stopOnArrival;
	}

	public void setStopOnArrival(boolean stopOnArrival) {
		boolean oldStopOnArrival = this.stopOnArrival;
		if (oldStopOnArrival != stopOnArrival){
			this.stopOnArrival = stopOnArrival;
			firePropertyChange("stopOnArrival", oldStopOnArrival, stopOnArrival);
		}
	}
}
