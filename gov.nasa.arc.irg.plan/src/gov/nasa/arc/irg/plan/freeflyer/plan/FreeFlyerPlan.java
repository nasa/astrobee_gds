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
package gov.nasa.arc.irg.plan.freeflyer.plan;

import gov.nasa.arc.irg.plan.freeflyer.command.FreeFlyerCommand;
import gov.nasa.arc.irg.plan.freeflyer.config.InertiaConfigList.InertiaConfig;
import gov.nasa.arc.irg.plan.freeflyer.config.OperatingLimitsConfigList.OperatingLimitsConfig;
import gov.nasa.arc.irg.plan.model.Plan;
import gov.nasa.arc.irg.plan.model.PlanBuilderConfiguration;
import gov.nasa.arc.irg.plan.model.Point6Dof;
import gov.nasa.arc.irg.plan.model.Segment;
import gov.nasa.arc.irg.plan.model.SequenceHolder;
import gov.nasa.arc.irg.plan.model.Sequenceable;
import gov.nasa.arc.irg.plan.model.Station;
import gov.nasa.arc.irg.plan.modulebay.ModuleBayPoint;
import gov.nasa.arc.irg.plan.modulebay.ModuleBayStation;
import gov.nasa.arc.irg.plan.schema.PlanSchema;
import gov.nasa.arc.irg.plan.schema.PlanSchemaBuilder;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * A Freeflyer plan MUST have a segment between stations in order for the FreeFlyer to traverse.
 * As a convention we will not have any commands outside of stations or segments.
 * As a convention we will not have any commands within segments
 * 
 * @author tecohen, ddwheele
 *
 */
public class FreeFlyerPlan extends Plan {

	public static final String EXTENSION = "fplan";
	public static final String LIBRARY_URL = "http://www.example.com/freeFlyerPlanLibrary.json";
	public static final String SCHEMA_URL = "http://www.example.com/freeFlyerPlanSchema.json";
	public static final String CATEGORY = "FreeFlyer";

	protected static PlanBuilderConfiguration s_planBuilderConfiguration = null;
	protected static PlanSchema s_planSchema = null;

	protected List<Class<? extends FreeFlyerCommand>> commandTypes = new ArrayList<Class<? extends FreeFlyerCommand>>();
	protected final String getNameName = "getClassNameForWidgetDropdown";
	
	protected InertiaConfig inertiaConfiguration;
	protected OperatingLimitsConfig operatingLimits;
	protected String notes;

	public FreeFlyerPlan() {
		super();
		setDefaultSpeed(1.0f/10.0f); // 10 cm/s in m/s
		setSchemaUrl(SCHEMA_URL);
		
		commandTypes = FreeFlyerCommand.getCommandTypes();
	}
	
	@JsonIgnore
	/** get last Segment or PlanCommand, as a PointCommand */
	public PointCommand getFinalPointCommand() {
		Station lastStation = (Station)getSequence().get(getSequence().size()-1);
		
		if(lastStation.getSequence().isEmpty()) {
			return new PointCommand(lastStation.getRapidNumber()-1, -1);
		} else {
			return new PointCommand(lastStation.getRapidNumber(), lastStation.getSequence().size()-1);
		}
	}
	
	@JsonIgnore
	/** convenience function to let you look up Stations and Segments by their Point number */
	public Sequenceable getSequenceableByRapidNumber(int point) {
		if(point >= getSequence().size()) {
			return null;
		}
		return getSequence().get(point);
	}
	
	@JsonIgnore
	/** returns the next thing that could run, or null */
	public PointCommand getNextExecutableElement(PointCommand current) {
		if(!getFinalPointCommand().isGreaterThan(current)) {
			return null; // at the end already
		}
		
		if(current.point % 2 == 0) { // station

			List<?> kids = ((Station)getSequenceableByRapidNumber(current.point)).getSequence();
			boolean skipAgain = false;
			if(current.equals(new PointCommand(0,-1))) {
				skipAgain = true;
			}
			
			// there's another kid, go to it
			if( current.command + 1 < kids.size() ) {
				PointCommand candidate = new PointCommand(current.point, current.command + 1);
				if(skipAgain) {
					// the first station was queued, go forward again.
					return getNextExecutableElement(candidate);
				} else {
					return candidate;
				}
			} else {
				// if no kids, or that was the last one, go to next segment
				PointCommand candidate = new PointCommand(current.point+1, -1);
				if(skipAgain) {
					// the first station was queued, go forward again.
					return getNextExecutableElement(candidate);
				} else {
					return candidate;
				}
			}
		}
		else { // segment
			Station nextStation = (Station) getSequenceableByRapidNumber(current.point + 1);
			
			// no kids, go to next segment
			if(nextStation.getSequence().isEmpty()) {
				return new PointCommand(current.point + 2, -1);
			}
			else { // kids, go to first kid
				return new PointCommand(current.point + 1, 0);
			}
		}
	}
	
	/**
	 * Move the selected Station one position earlier in the plan
	 * @param move Station to move
	 * @return the Station that was moved, null if it was already the first Station
	 */
	public Station moveThisStationUp(Station move) {
		Station previousStation;

		int moveIndex = m_sequence.indexOf(move);
		if(moveIndex > 1) {
			previousStation = (Station) getSequenceable(moveIndex-2);
		} else {
			// if it's the first thing, can't move it up
			return null;
		}
		Station saved = (Station) removeSequenceable(move);

		// avoid infinite loops when we auto-update
		saved.setNext(null);
		saved.setPrevious(null);

		// then add it back one position higher
		insertThisStation(saved, previousStation);
		setValid(false);
		return saved;
	}

	/**
	 * Move the selected Station one position later in the plan
	 * @param move Station to move
	 * @return the Station that was moved, null if it was already the last Station
	 */
	public Station moveThisStationDown(Station move) {
		Station nextStation;

		int moveIndex = indexOf(move);
		if(moveIndex > size()-2) {
			// if it's the last thing, can't move it down
			return null;
		} else {
			nextStation = (Station) getSequenceable(moveIndex+2);
		}

		Station saved = (Station) removeSequenceable(move);

		// avoid infinite loops when we auto-update
		saved.setNext(null);
		saved.setPrevious(null);

		// then add it back one position lower
		insertThisStationAfter(saved, nextStation);
		setValid(false);
		return saved;
	}

	public Station insertNewStation(Sequenceable reference) {
		if(reference instanceof Station) {
			return insertNewStation((Station)reference);
		}
		return insertNewStation(getNextStation(reference));
	}

	/**
	 * Create a new Station and insert it before the reference Station
	 * @param reference Station to put the new Station in front of
	 * @return the Station that was inserted
	 */
	public Station insertNewStation(Station reference) {
		// make a new station, name it in sequence
		Station insert = new Station();
		int insertNum = indexOf(reference);
		Point6Dof nextPos = reference.getCoordinate();
		if(insertNum > 0) {
			// there is a previous station
			// average the coordinates of prev and reference
			Station prevSt = getPreviousStation(insertNum);
			Point6Dof prevPos = prevSt.getCoordinate();

			float avgX = (prevPos.getX() + nextPos.getX()) / 2.0f;
			float avgY = (prevPos.getY() + nextPos.getY()) / 2.0f;
			float avgZ = (prevPos.getZ() + nextPos.getZ()) / 2.0f;

			insert.setCoordinate(new ModuleBayPoint(avgX,
					avgY,
					avgZ,
					prevPos.getRoll(),
					prevPos.getPitch(),
					prevPos.getYaw()));
		} else {
			insert.setCoordinate(new ModuleBayPoint(nextPos.getX(),
					nextPos.getY(),
					nextPos.getZ(),
					0,
					0,
					0));
		}
		insert.autoName(insertNum);
		addStation(insertNum, insert);
		return insert;
	}

	/**
	 * Insert this Station into the Plan after the reference Station
	 * @param insert Station to insert
	 * @param reference Station to put insert Station after
	 * @return the Station that was inserted
	 */
	public Station insertThisStationAfter(Station insert, Station reference) {
		// make a new station, name it in sequence
		int insertNum = indexOf(reference);

		insert.autoName(insertNum+2); // another segment will be added

		if(insertNum+2 <= size()) {
			addStation(insertNum+2, insert);
		} else {
			addStation(insertNum+1, insert);
		}
		return insert;
	}

	/**
	 * Insert this Station into the Plan before the reference Station
	 * @param saved Station to insert
	 * @param previousStation Station to put insert Station in front of
	 * @return the Station that was inserted
	 */
	public Station insertThisStation(Station saved, Station previousStation) {
		// make a new station, name it in sequence
		int insertNum = indexOf(previousStation);

		saved.autoName(insertNum);
		addStation(insertNum, saved);
		return saved;
	}

	/**
	 * Make a new Station and put it on the end of the Plan
	 * @return the new Station that was inserted
	 */
	// not called
	public ModuleBayStation appendNewStation() {
		// make a new station, name it in sequence
		ModuleBayStation st = new ModuleBayStation();
		int newnum = getNumStations();
		if(newnum > 0) {
			// there is a previous station
			// make new station at position of last station
			Station prevSt = getPreviousStation(getSequence().size());
			Point6Dof prevPos = prevSt.getCoordinate();
			st.setCoordinate(new ModuleBayPoint(prevPos.getX(),
					prevPos.getY(),
					prevPos.getZ(),
					prevPos.getRoll(),
					prevPos.getPitch(),
					prevPos.getYaw()));
		}
		st.setName(Integer.toString(getNumStations()));
		addStation(st);
		return st;
	}
	
	public FreeFlyerCommand insertNewCommand(Station station, String commandText, FreeFlyerCommand reference) {
		FreeFlyerCommand command = null;
		for(Class<? extends FreeFlyerCommand> ffc : commandTypes ) {
			if(commandText.equals(getClassCustomName(ffc))) {
				 Constructor[] ctors = ffc.getConstructors();
				 int numParams = ctors[0].getParameterTypes().length;
				 if(numParams == 0) {
					 try {
						command = (FreeFlyerCommand) ctors[0].newInstance();
						String commandName = station.getName()+"."+station.getSequence().size()+" "+command.getClass().getSimpleName();
						command.setName(commandName);
						command.setParent(station);
						
						if(reference == null) {
							station.addSequenceable(command);
						} else {
							station.addSequenceable(station.indexOf(reference), command);
						}
						break;
					} catch (Exception e1) {
						System.err.println(e1);
						return null;
					}
				 }
			}
		}
		return command;
	}
	
	public FreeFlyerCommand insertThisCommand(Station station, FreeFlyerCommand command, FreeFlyerCommand reference) {
		if(reference == null) {
			station.addSequenceable(command);
		} else {
			station.addSequenceable(station.indexOf(reference), command);
		}
		return command;
	}
	
	public FreeFlyerCommand appendNewCommand(Station station, String commandText) {
		return insertNewCommand(station, commandText, null);
	}
	
	public FreeFlyerCommand appendThisCommand(Station station, FreeFlyerCommand command) {
		return insertThisCommand(station, command, null);
	}
	
	public FreeFlyerCommand getNextCommand(Station station, FreeFlyerCommand command) {
		if(station.indexOf(command) == station.getSequence().size() - 1) {
			return null;
		} else {
			return (FreeFlyerCommand) station.getSequenceable(station.indexOf(command) + 1);
		}
	}

	@JsonIgnore
	public Station getPreviousStation(Sequenceable seq) {
		int index = m_sequence.indexOf(seq);
		if( index > -1) {
			return getPreviousStation(index-1);
		}
		return null;
	}

	@Override
	@JsonIgnore
	public Station getNextStation(Sequenceable seq) {
		int index = m_sequence.indexOf(seq);
		if( index > -1) {
			return getNextStation(index+1);
		}
		return null;
	}

	@JsonIgnore
	public synchronized static PlanBuilderConfiguration getPlanBuilderConfiguration() {
		if (s_planBuilderConfiguration == null){
			s_planBuilderConfiguration = new FreeFlyerPlanBuilderConfiguration(FreeFlyerPlan.class.getResourceAsStream("freeFlyerPlanLibrary.json"),
					FreeFlyerCommand.class,
					FreeFlyerPlan.class,
					LIBRARY_URL
					);
		}
		return s_planBuilderConfiguration;
	}

	@JsonIgnore
	public synchronized static PlanSchema getPlanSchema() {
		if (s_planSchema == null){
			s_planSchema = PlanSchemaBuilder.loadPlanSchema(FreeFlyerPlan.class,
					FreeFlyerPlan.class.getResourceAsStream("freeFlyerPlanSchema.json"), 
					s_planBuilderConfiguration);

		}
		return s_planSchema;
	}

	/**
	 * Update start times, ids and locations based on a change
	 * @param index start at this index
	 */
	@Override
	public void updateNames(int index){
		if (index >= 0 && !m_sequence.isEmpty()){
			// make sure everything knows what comes before it
			for(int i=index; i<getSequence().size(); i++){
				Sequenceable s = getSequenceable(i);
				s.autoName(i);
			}
		}
	}

	@Override
	@JsonIgnore
	public String getFileExtension() {
		return EXTENSION;
	}

	@JsonIgnore
	@Override
	public String getCategory() {
		return CATEGORY;
	}

	public InertiaConfig getInertiaConfiguration() {
		return inertiaConfiguration;
	}

	public void setInertiaConfiguration(InertiaConfig inertiaConfig) {
		inertiaConfiguration = inertiaConfig;
		setValid(false);
	}

	public OperatingLimitsConfig getOperatingLimits() {
		return operatingLimits;
	}

	public void setOperatingLimits(OperatingLimitsConfig operatingLimitsConfig) {
		this.operatingLimits = operatingLimitsConfig;
		if(operatingLimitsConfig != null) {
			setDefaultSpeed(operatingLimitsConfig.getTargetLinearVelocity());
		}
		setValid(false);
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
		setValid(false);
	}

	public void deleteSequenceable(Sequenceable toDelete) {
		if(toDelete instanceof Station) {
			removeSequenceable(toDelete);
			setValid(false);
		}
		else if(toDelete instanceof FreeFlyerCommand) {
			SequenceHolder parentStation = toDelete.getParent();
			parentStation.removeSequenceable(toDelete);
		}
		else if(toDelete instanceof Segment) {
			return;
		}
	}
	
	protected String getClassCustomName(Class<? extends FreeFlyerCommand> ffc) {
		try {
			Method getName = ffc.getMethod( getNameName );
			return (String) getName.invoke(null, (Object[])null);
		} catch (Exception e) {
			System.err.println(e);
		}
		return "no name";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + super.hashCode();
		result = prime * result + ((inertiaConfiguration == null) ? 0 : inertiaConfiguration.hashCode());
		result = prime * result + ((operatingLimits == null) ? 0 : operatingLimits.hashCode());
		result = prime * result + ((notes == null) ? 0 : notes.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		if (obj instanceof FreeFlyerPlan){
			FreeFlyerPlan other = (FreeFlyerPlan) obj;
			if(!super.equals(other)) {
				return false;
			}
			if (inertiaConfiguration == null) {
				if (other.getInertiaConfiguration() != null) {
					return false;
				}
			} else if (!inertiaConfiguration.equals(other.getInertiaConfiguration())) {
				return false;
			}
			if (operatingLimits == null) {
				if (other.getOperatingLimits() != null) {
					return false;
				}
			} else if (!operatingLimits.equals(other.getOperatingLimits())) {
				return false;
			}
			if (notes == null) {
				if (other.notes != null) {
					return false;
				}
			} else if (!notes.equals(other.notes)) {
				return false;
			}
			return true;
		}
		return false;
	}
}
