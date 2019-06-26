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
package gov.nasa.arc.verve.freeflyer.workbench.undo;

import gov.nasa.arc.ff.ocu.commands.ReversibleCommand;
import gov.nasa.arc.irg.plan.model.Point6Dof;
import gov.nasa.arc.irg.plan.model.Sequenceable;
import gov.nasa.arc.irg.plan.model.Station;
import gov.nasa.arc.irg.plan.modulebay.ModuleBayPoint;
import gov.nasa.arc.irg.plan.modulebay.ModuleBayStation;
import gov.nasa.arc.verve.robot.freeflyer.plan.PlanEditsRegistry;

import org.apache.log4j.Logger;

import com.ardor3d.math.type.ReadOnlyVector3;

public class UpdateValueCommand extends ReversibleCommand {
	private Logger logger = Logger.getLogger(UpdateValueCommand.class);
	private ModuleBayPoint oldLocation;
	private ModuleBayPoint newLocation;
	private Station station;

	public UpdateValueCommand(ModuleBayStation station, ReadOnlyVector3 newLoc) {	
		init(station);
		newLocation = new ModuleBayPoint(newLoc.getXf(), newLoc.getYf(), newLoc.getZf(),
				oldLocation.getRoll(), oldLocation.getPitch(), oldLocation.getYaw());
	}

	/** Angles in degrees */
	public UpdateValueCommand(ModuleBayStation station,	Point6Dof newLoc) {
		init(station);
		// do this to preserve ignoreOrientation while dragging
		if(newLoc instanceof ModuleBayPoint) {
			try {
				newLocation = (ModuleBayPoint) newLoc.clone();
			} catch (CloneNotSupportedException e) {
				logger.error("Error cloning new location");
			}
		} else {
			newLocation = new ModuleBayPoint(newLoc.getX(), newLoc.getY(), newLoc.getZ(),
					newLoc.getRoll(), newLoc.getPitch(), newLoc.getYaw());
		}
	}

	private void init(ModuleBayStation station) {
		this.station = station;
		try {
			oldLocation = station.getCoordinate().clone();
		} catch (CloneNotSupportedException e) {
			logger.error("Error cloning station coordinate?");
		}
	}

	@Override
	public Sequenceable runCommand() {
		station.setCoordinate(newLocation);
		PlanEditsRegistry.onStationMoved(station);
		return station;
	}

	@Override
	public Sequenceable undoCommand() {
		station.setCoordinate(oldLocation);
		PlanEditsRegistry.onStationMoved(station);
		return station;
	}
}
