/*******************************************************************************
 * Copyright (c) 2013 United States Government as represented by the 
 * Administrator of the National Aeronautics and Space Administration. 
 * All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package gov.nasa.arc.irg.georef.coordinates;


import java.util.ArrayList;
import java.util.List;

/**
 * Associate a string label with a lat long for preferences usage.
 * @author tecohen
 *
 */
public class LabeledLatLong  {
	String m_name;
	LatLong m_latLong;
	
	public LabeledLatLong(String name, LatLong ll){
		m_name = name;
		m_latLong = ll;
	}
	
	public String getName() {
		return m_name;
	}
	
	public void setName(String name) {
		m_name = name;
	}
	
	public LatLong getLatLong() {
		return m_latLong;
	}
	
	public void setLatLong(LatLong latLong) {
		m_latLong = latLong;
	}
	
	public static List<LabeledLatLong> toLabeledLatLong(String values){
		List<LabeledLatLong> result = new ArrayList<LabeledLatLong>();
		if (values == null || values.length() == 0){
			return result;
		}
		String[] rows = values.split("\n");
		for (String row : rows){
			try {
				String[] contents = row.split(" ");
				if (contents.length == 3){
					double lat = Double.parseDouble(contents[1]);
					double lon = Double.parseDouble(contents[2]);
					LatLong ll = new LatLong(lat,lon);
					result.add(new LabeledLatLong(contents[0], ll));
				} else if (contents.length == 2){
					double lat = Double.parseDouble(contents[0]);
					double lon = Double.parseDouble(contents[1]);
					LatLong ll = new LatLong(lat,lon);
					result.add(new LabeledLatLong("", ll));
				}
			} catch (NumberFormatException nef){
				// ignore
			}
		}
		return result;
	}
	
	public static String toString(List<LabeledLatLong> list){
		StringBuffer buffer = new StringBuffer();
		for (LabeledLatLong lll : list) {
			buffer.append(lll.getName());
			buffer.append(" ");
			buffer.append(lll.getLatLong().getLatitude());
			buffer.append(" ");
			buffer.append(lll.getLatLong().getLongitude());
			buffer.append("\n");
		}
		return buffer.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof LabeledLatLong)) {
			return super.equals(obj);
		}
		LabeledLatLong other = (LabeledLatLong)obj;
		if (other == null || other.getLatLong() == null){
			return false;
		}
		if (this.getName() != null && other.getName() != null && this.getName().equals(other.getName())){
			if (getLatLong() != null 
				&& other.getLatLong() != null 
				&& getLatLong().getLatitude() == other.getLatLong().getLatitude()
				&& getLatLong().getLongitude() == other.getLatLong().getLongitude()){
				return true;
			}
		}
		return false;
	}
	
}
