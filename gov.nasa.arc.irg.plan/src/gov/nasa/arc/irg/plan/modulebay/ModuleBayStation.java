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

import gov.nasa.arc.irg.plan.model.Plan;
import gov.nasa.arc.irg.plan.model.Position;
import gov.nasa.arc.irg.plan.model.Station;

import java.beans.PropertyChangeEvent;

import org.codehaus.jackson.annotate.JsonTypeInfo;

@JsonTypeInfo(  
		use = JsonTypeInfo.Id.NAME,  
		include = JsonTypeInfo.As.PROPERTY,  
		property = "type")
public class ModuleBayStation extends Station {

	public ModuleBayStation() {
		super();
	}

	@Override
	protected void init() {
		coordinate = new ModuleBayPoint();
		coordinate.addPropertyChangeListener(this);
	}

	public ModuleBayPoint getCoordinate() {
		if(coordinate instanceof ModuleBayPoint)
			return (ModuleBayPoint)coordinate;
		return null;
	}

	private void ensureCoordinateIsModuleBayPoint() {
		if(!(coordinate instanceof ModuleBayPoint)) {
			coordinate = new ModuleBayPoint();
			coordinate.addPropertyChangeListener(this);
		}
	}

	public void setCoordinate(ModuleBayPoint original) {
		if(original instanceof ModuleBayPoint) {
			ensureCoordinateIsModuleBayPoint();

			ModuleBayPoint mbpOriginal = (ModuleBayPoint)original;
			if(mbpOriginal.isBookmarkValid()) {
				((ModuleBayPoint) coordinate).setBookmark(mbpOriginal.getBookmark());
			}
			else if(mbpOriginal.isModuleBayValid()) {
				((ModuleBayPoint) coordinate).setModule(mbpOriginal.getModule());
				((ModuleBayPoint) coordinate).setBayNumber(mbpOriginal.getBayNumber());
				((ModuleBayPoint) coordinate).setCenterOne(mbpOriginal.isCenterOne());
				((ModuleBayPoint) coordinate).setWallOne(mbpOriginal.getWallOne());
				((ModuleBayPoint) coordinate).setWallOneOffset(mbpOriginal.getWallOneOffset());
				((ModuleBayPoint) coordinate).setCenterTwo(mbpOriginal.isCenterTwo());
				((ModuleBayPoint) coordinate).setWallTwo(mbpOriginal.getWallTwo());
				((ModuleBayPoint) coordinate).setWallTwoOffset(mbpOriginal.getWallTwoOffset());
				((ModuleBayPoint) coordinate).setIgnoreOrientation(mbpOriginal.isIgnoreOrientation());
				if(!mbpOriginal.isIgnoreOrientation()) {
					((ModuleBayPoint) coordinate).setOrientationWall(mbpOriginal.getOrientationWall());
				}
			}
			else {
				boolean savedIgnore = mbpOriginal.isIgnoreOrientation();
				coordinate.setX(mbpOriginal.getX());
				coordinate.setY(mbpOriginal.getY());
				coordinate.setZ(mbpOriginal.getZ());
				coordinate.setRoll(mbpOriginal.getRoll());
				coordinate.setPitch(mbpOriginal.getPitch());
				coordinate.setYaw(mbpOriginal.getYaw());
				((ModuleBayPoint) coordinate).setIgnoreOrientation(savedIgnore);
			}
		}
		refresh(this); //necessary??
	}

	//	public void setModelBayPointCoordinate(ModuleBayPoint location) {
	//		coordinate = location;
	//		coordinate.addPropertyChangeListener(this);
	//		refresh(this); //necessary??
	//	}

	@Override
	public ModuleBayStation clone() throws CloneNotSupportedException {
		ModuleBayStation newStation = (ModuleBayStation) super.clone();
		if(coordinate != null && newStation.getCoordinate() == null) {
			newStation.setCoordinate((ModuleBayPoint)coordinate.clone());
		}
		return newStation;
	}

	@Override
	public void propertyChange(PropertyChangeEvent arg0) {
		if(parent instanceof Plan) {
			((Plan)parent).setValid(false);
			((Plan)parent).refresh();
		}
		if(arg0.getSource().equals(coordinate) || arg0.getSource().equals(coordinate)) {
			refresh(this);
		}
	}

	@Override
	public Position getStartPosition() {
		return new Position(coordinate);
	}

	@Override
	public Position getEndPosition() {
		return new Position(coordinate);
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
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
		result = prime * result + Float.floatToIntBits(tolerance);
		result = prime * result + (stopOnArrival ? 1 : 0);
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
		if(!(o instanceof ModuleBayStation)) {
			return false;
		}
		ModuleBayStation other = (ModuleBayStation)o;

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
}
