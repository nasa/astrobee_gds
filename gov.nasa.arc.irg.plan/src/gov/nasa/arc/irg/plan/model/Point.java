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

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonTypeInfo;


@JsonTypeInfo(  
		use = JsonTypeInfo.Id.NAME,  
		include = JsonTypeInfo.As.PROPERTY,  
		property = "type")
public class Point extends Geometry {
	private Logger logger = Logger.getLogger(Point.class);
	protected List<Float> m_coordinates;
	protected final int m_defaultSize = 3;

	/**
	 * WARNING
	 * use setCoordinateValue to alter the contents of coordinates correctly, so property changes are fired.
	 * @return
	 */
	public List<Float> getCoordinates() {
		return m_coordinates;
	}

	public Point(){
		m_coordinates = new ArrayList<Float>();
	}

	//	public Point(Position position){
	//		m_coordinates = new ArrayList<Float>();
	//		m_coordinates.addAll(position.getCoordinates());
	//	}

	public Point(float x, float y, float z) {
		m_coordinates = new ArrayList<Float>();
		m_coordinates.add(x);
		m_coordinates.add(y);
		m_coordinates.add(z);
	}

	@JsonIgnore
	public void setX(float x) {
		setCoordinateValue(0,x);
	}
	@JsonIgnore
	public void setY(float y) {
		setCoordinateValue(1,y);
	}
	@JsonIgnore
	public void setZ(float z) {
		setCoordinateValue(2,z);
	}
	@JsonIgnore
	public float getX() {
		return m_coordinates.get(0);
	}
	@JsonIgnore
	public float getY() {
		return m_coordinates.get(1);
	}
	@JsonIgnore
	public float getZ() {
		return m_coordinates.get(2);
	}

	public void setCoordinates(List<Float> coordinates) {
		List<Float> oldValue = getCoordinates();

		if (coordinates == null){
			m_coordinates.clear();
			firePropertyChange("coordinates" , oldValue, new ArrayList<Float>());
			return;
		}

		if(coordinates.size() != m_defaultSize) {
			logger.error("Points hold only "+m_defaultSize+" values");
			return;
		}

		if(m_coordinates.size() == 0) {
			m_coordinates.addAll(coordinates);
		} else if(m_coordinates.size() == m_defaultSize) {
			for(int i=0; i<m_defaultSize; i++) {
				m_coordinates.set(i, coordinates.get(i));
			}
		} else {
			logger.error("Point has " + m_coordinates.size() + " coordinates.");
		}

		List<Float> newValue = getCoordinates();

		firePropertyChange("coordinates" , oldValue, newValue);

		//		boolean differs = false;
		//		if (oldValue.size() != coordinates.size()) {
		//			differs = true;
		//		} else if (!coordinates.isEmpty() &&  !oldValue.isEmpty()){
		//			// compare them
		//			if (oldValue.size() == coordinates.size()){
		//				int index = 0;
		//				for (Float mine : oldValue){
		//					if (!mine.equals(coordinates.get(index))){
		//						differs = true;
		//						break;
		//					}
		//					index++;
		//				}
		//			}
		//		}
		//		
		//		// if they differ and are both not null, differs will be true.
		//		if (differs){
		//			m_coordinates.clear();
		//			for (Float f : coordinates){
		//				m_coordinates.add(f);
		//			}
		//			firePropertyChange("coordinates" , oldValue, m_coordinates);
		//		}
	}

	public void setCoordinateValue(int index, Float value){
		List<Float> oldValue = null;
		if (m_coordinates != null){
			oldValue = new ArrayList<Float>(m_defaultSize);
			oldValue.addAll(m_coordinates);
		}
		if (m_coordinates == null){
			m_coordinates = new ArrayList<Float>(m_defaultSize);
		}
		if (m_coordinates.size() > index){
			Float current = m_coordinates.get(index);
			if (!current.equals(value)){
				m_coordinates.set(index, value);
				firePropertyChange("coordinates" , oldValue, m_coordinates);
			}
			return;
		} else {
			// I guess append zeros until you get to the index
			for (int i = m_coordinates.size(); i < index; i++){
				m_coordinates.add(0.0f);
			}
			m_coordinates.add(value);
		}
		firePropertyChange("coordinates" , oldValue, m_coordinates);
	}

	@Override
	public Point clone() throws CloneNotSupportedException {
		Point newPoint =  (Point)super.clone();
		ArrayList<Float> coordinates = new ArrayList<Float>(m_defaultSize);
		if (m_coordinates != null){
			coordinates.addAll(m_coordinates);
		}
		newPoint.m_coordinates = coordinates;
		return newPoint;
	}

	@Override
	@JsonIgnore
	protected List<Float> getCenter() {
		return getCoordinates();
	}


	@Override
	public String toString() {
		return getX() + ", " + getY() + ", " + getZ();

		//		if (x = null && y != null){
		//			StringBuffer result = new StringBuffer();
		//			
		//			int index = 0;
		//			for (Float c : m_coordinates){
		//				result.append(c.toString());
		//				if (index < m_coordinates.size() - 1){
		//					result.append(", ");
		//				}
		//				index++;
		//			}
		//			
		//			return result.toString();
		//		}
		//		return "";
	}

	public static Point makeNeutralPoint() {
		Point result = new Point(0f, 0f, 0f);

		return result;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		for (Float coord : getCoordinates()){
			result = prime * result + Float.floatToIntBits(coord);
		}
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Point){
			if (obj == this) {
				return true;
			}
			Point other = (Point)obj;
			List<Float> otherCoords = other.getCoordinates();
			if (otherCoords != null && getCoordinates() != null){
				if (otherCoords.size() == this.getCoordinates().size()){
					int index = 0;
					for (Float mine : getCoordinates()){
						if (!mine.equals(otherCoords.get(index))){
							return false;
						}
						index++;
					}
				}
				return true;
			}
			if (otherCoords == null && getCoordinates() == null){
				return true;
			}

		} 
		return false;
	}
}
