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

import gov.nasa.arc.irg.plan.util.NumberUtil;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonTypeInfo;

@JsonTypeInfo(  
	    use = JsonTypeInfo.Id.NAME,  
	    include = JsonTypeInfo.As.PROPERTY,  
	    property = "type")
public class Position implements Cloneable{
	
	/** DEGREES */
	protected List<Float> m_coordinates = new ArrayList<Float>(3);
	/** DEGREES */
	protected List<Float> m_orientation = new ArrayList<Float>(3);
	
	public Position() {
		m_coordinates.add(0.0f);
		m_coordinates.add(0.0f);
		m_coordinates.add(0.0f);
		m_orientation.add(0.0f);
		m_orientation.add(0.0f);
		m_orientation.add(0.0f);
	}
	
	/**
	 * TODO not sure this is correct but the position refers to the point's coordinates to support changes
	 * @param point
	 */
	public Position(Point point){
		m_orientation.add(0.0f);
		m_orientation.add(0.0f);
		m_orientation.add(0.0f);
		if (point == null) {
			m_coordinates.add(0.0f);
			m_coordinates.add(0.0f);
			return;
		}
		if (point.getCoordinates() != null){
			m_coordinates.addAll(point.getCoordinates());
		}
	}
	
	public Position(Point6Dof pt) {
		m_coordinates.add(pt.getX());
		m_coordinates.add(pt.getY());
		m_coordinates.add(pt.getZ());
		m_orientation.add(pt.getRoll());
		m_orientation.add(pt.getPitch());
		m_orientation.add(pt.getYaw());
	}
	
	public List<Float> getCoordinates() {
		return m_coordinates;
	}
	
	public void setCoordinates(List<Float> coordinates) {
		m_coordinates = coordinates;
	}
	
	public boolean hasCoordinates() {
		return m_coordinates != null && !m_coordinates.isEmpty();
	}
	
	public List<Float> getOrientation() {
		return m_orientation;
	}
	
	public void setOrientation(List<Float> orientation) {
		m_orientation = orientation;
	}
	
	public boolean hasOrientation() {
		return m_orientation != null && !m_orientation.isEmpty();
	}
	
	public String getPositionString() {
		StringBuffer result = new StringBuffer();
		if (m_coordinates != null){
			for (Float f : m_coordinates){
				result.append(f.toString());
				if (m_coordinates.indexOf(f) < m_coordinates.size() - 1 ){
					result.append(", ");
				}
			}
		}
		return result.toString();
	}
	
	public String getOrientationString() {
		StringBuffer result = new StringBuffer();
		if (m_orientation != null){
			for (Float f : m_orientation){
				result.append(f.toString());
				if (m_orientation.indexOf(f) < m_orientation.size() - 1 ){
					result.append(", ");
				}
			}
		}
		return result.toString();
	}
	
	@Override
	public Position clone() throws CloneNotSupportedException{
		Position newPosition = new Position();// this doesn't do anything: (Position)super.clone();
		if (m_coordinates != null){
			for(int i=0; i<m_coordinates.size(); i++) {
				newPosition.getCoordinates().set(i,m_coordinates.get(i));
			}
		}
		if (m_orientation != null){
			for(int i=0; i<m_orientation.size(); i++) {
				newPosition.getOrientation().set(i, m_orientation.get(i));
			}
		}
		return newPosition;
	}
	
	public static Position makeNeutralPosition() {
		Position result = new Position();
		// the constructor already puts zeros in m_coordinates and m_orientation
		return result;
	}
	
	@Override
	public String toString() {
		return "position = " + getPositionString() + " orientation = " + getOrientationString();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		for (Float coord : getCoordinates()){
			result = prime * result + Float.floatToIntBits(coord);
		}
		for (Float coord : getOrientation()){
			result = prime * result + Float.floatToIntBits(coord);
		}
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if (obj instanceof Position){
			if (obj == this){
				return true;
			}
			Position other = (Position)obj;
			List<Float> otherCoords = other.getCoordinates();
			if (otherCoords == null && getCoordinates() == null && other.getOrientation() == null && this.getOrientation() == null){
				return true;
			}
			
			if (otherCoords != null && getCoordinates() != null){
				if (otherCoords.size() == this.getCoordinates().size()){
					int index = 0;
					for (Float mine : getCoordinates()){
						if (!NumberUtil.equals(mine, otherCoords.get(index))){
							return false;
						}
						index++;
					}
				}
				// NO we haven't checked the orientation yet!!! return true;
			}
			List<Float> otherOrientation = other.getOrientation();
			if (otherOrientation != null && getOrientation() != null){
				if (otherOrientation.size() == this.getOrientation().size()){
					int index = 0;
					for (Float mine : getOrientation()){
						if (!NumberUtil.equals(mine, otherOrientation.get(index))){
							return false;
						}
						index++;
					}
				}
				return true;
			}
			
		}
		return false;
	}
}
