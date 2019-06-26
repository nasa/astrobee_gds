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
package gov.nasa.arc.irg.freeflyer.rapid.state;

import rapid.ext.astrobee.CommState;

public class CommStateGds {
	protected CommState commState;
	protected final String uninitialized = "--";

	public CommStateGds() {
		/*
		commState = new CommState();
		commState.wirelessConnected = false;
		commState.apName = "Not Applicable";
		commState.bssid = "Not Applicable";
		commState.rssi = -1;
		commState.frequency = -1;
		commState.channel = -1;
		commState.lanConnected = false;
		*/
	}
	
	public CommStateGds copyFrom(CommStateGds other) {
		if(other.getCommState() != null) {
			commState = new CommState( other.getCommState() );
		}
		return this;
	}
	
	private CommState getCommState() {
		return commState;
	}
	
	public void ingestCommState(CommState cs) {
		commState = cs;
	}
	
	public boolean isWirelessConnected() {
		if(commState != null) 
			return commState.wirelessConnected;
		return false;
	}

	public String getApName() {
		if(commState != null) 
			return commState.apName;
		return uninitialized;
	}

	public String getBssid() {
		if(commState != null) 
			return commState.bssid;
		return uninitialized;
	}

	public float getRssi() {
		if(commState != null) 
			return commState.rssi;
		return -1;
	}

	public float getFrequency() {
		if(commState != null) 
			return commState.frequency;
		return -1;
	}

	public int getChannel() {
		if(commState != null) 
			return commState.channel;
		return -1;
	}

	public boolean isLanConnected() {
		if(commState != null) 
			return commState.lanConnected;
		return false;
	}
	
	public boolean isNull() {
		return commState == null;
	}
	
	@Override
	public int hashCode() {
		return ((commState == null) ? 0 : commState.hashCode());
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		if (obj instanceof CommStateGds) {
			CommStateGds other = (CommStateGds)obj;
			if (commState == null) {
				if (other.getCommState() != null) {
					return false;
				}
			} else if (!commState.equals(other.getCommState())) {
				return false;
			}
			return true;
		}
		return false;
	}
}
