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
package gov.nasa.arc.irg.plan.modulebay;

import gov.nasa.arc.irg.plan.freeflyer.plan.FreeFlyerPlan;
import gov.nasa.arc.irg.plan.model.Point6Dof;
import gov.nasa.arc.irg.plan.model.Sequenceable;
import gov.nasa.arc.irg.plan.model.Station;

import java.util.ArrayList;
import java.util.List;

public class ModuleBayPlan extends FreeFlyerPlan {

	protected void recalculateDontCareOrientations() {
		ModuleBayStation lastStation = null;
		List<Sequenceable> oldSequence = new ArrayList<Sequenceable>();
		oldSequence.addAll(m_sequence);

		for(Sequenceable seq : m_sequence) {
			if(seq instanceof ModuleBayStation) {
				ModuleBayStation st = (ModuleBayStation)seq;
				if(st.getCoordinate().isIgnoreOrientation()) {
					ModuleBayPoint coord = (ModuleBayPoint) st.getCoordinate();
					if(lastStation == null) {
						coord.setRpyDontFirePropertyChange(0, 0, 0);
					} else {
						coord.setRpyDontFirePropertyChange(lastStation.getCoordinate().getRoll(),
								lastStation.getCoordinate().getPitch(),
								lastStation.getCoordinate().getYaw());
					}
				}
				lastStation = st;
			}
		}

		firePropertyChange("sequence", oldSequence, m_sequence);
	}

	@Override
	public void initialize() {
		// do NOT recalculate start and end positions
		// they are manually set and are not calculated values
	}

	@Override
	public void refresh() {
		// we are taking care of ignoreOrientation stations when compiling - don't do it here
		//		recalculateDontCareOrientations();
	}

	/**
	 * Create a new Station and insert it after the reference Station
	 * @param reference Station to put the new Station after
	 * @return the Station that was inserted
	 */
	public Station insertNewStationAfter(Station reference) {
		Station nextSt = getNextStation(reference);

		if(nextSt != null) {// there is a next station
			return insertNewStation(nextSt);
		} else {
			return appendNewStation();
		}
	}

	/**
	 * Create a new Station and insert it before the reference Station
	 * @param reference Station to put the new Station in front of
	 * @return the Station that was inserted
	 */
	@Override
	public Station insertNewStation(Station reference) {
		// make a new station, name it in sequence
		ModuleBayStation insert = new ModuleBayStation();
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

			if(prevPos instanceof ModuleBayPoint) {
				if(((ModuleBayPoint) prevPos).isIgnoreOrientation()) {
					insert.setCoordinate(new ModuleBayPoint(avgX,
							avgY,
							avgZ));
				} else {
					insert.setCoordinate(new ModuleBayPoint(avgX,
							avgY,
							avgZ,
							prevPos.getRoll(),
							prevPos.getPitch(),
							prevPos.getYaw()));
				}
			}
		}else {
			insert.setCoordinate(new ModuleBayPoint(nextPos));
		}
		insert.autoName(insertNum);
		addStation(insertNum, insert);
		return insert;
	}

	/**
	 * Make a new Station and put it on the end of the Plan
	 * @return the new Station that was inserted
	 */
	@Override
	public ModuleBayStation appendNewStation() {
		// make a new station, name it in sequence
		ModuleBayStation newStation = new ModuleBayStation();
		int newnum = getNumStations();
		if(newnum > 0) {
			// there is a previous station
			// make new station offset from last station
			Station prevSt = getPreviousStation(getSequence().size());
			Point6Dof prevPos = prevSt.getCoordinate();
			if(prevPos instanceof ModuleBayPoint) {
				newStation.setCoordinate((ModuleBayPoint) prevPos);
			}
			else {
				newStation.setCoordinate(new ModuleBayPoint(prevPos));
			}
		}
		newStation.setName(Integer.toString(getNumStations()));
		addStation(newStation);
		return newStation;
	}
}
