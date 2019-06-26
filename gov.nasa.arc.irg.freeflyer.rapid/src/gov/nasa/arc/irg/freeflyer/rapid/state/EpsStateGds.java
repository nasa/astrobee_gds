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

import gov.nasa.arc.irg.plan.ui.io.WorkbenchConstants;

import java.util.Vector;

import rapid.ext.astrobee.BatteryInfo;
import rapid.ext.astrobee.BatteryInfoConfig;
import rapid.ext.astrobee.EpsConfig;
import rapid.ext.astrobee.EpsState;

public class EpsStateGds {
	protected int estimatedMinutesRemaining;
	protected Vector<BatteryInfoGds> batts;
	protected final String uninitialized = WorkbenchConstants.UNINITIALIZED_STRING;

	public EpsStateGds() {
		batts = new Vector<BatteryInfoGds>();
	}

	public EpsStateGds copyFrom(EpsStateGds original) {
		estimatedMinutesRemaining = original.estimatedMinutesRemaining;
		batts.clear();
		for(BatteryInfoGds big : original.batts) {
			batts.add(new BatteryInfoGds(big));
		}
		return this;
	}

	public void ingestEpsConfig(EpsConfig config) {
		batts.clear();

		for(int i=0; i<config.batteries.userData.size(); i++) {
			batts.add(new BatteryInfoGds((BatteryInfoConfig)config.batteries.userData.get(i)));
		}
	}

	public void ingestEpsState(EpsState state) {
		this.estimatedMinutesRemaining = state.estimatedMinutesRemaining;

		if(batts.size() < 1) {
			return;
		}

		for(int i=0; i<state.batteries.userData.size(); i++) {
			batts.get(i).setTo((BatteryInfo)state.batteries.userData.get(i));
		}
	}

	public Vector<BatteryInfoGds> getBatteryInfo() {
		return batts;
	}
	
	public int getBatteryMinutes() {
		return estimatedMinutesRemaining;
	}

	public String getBatteryMinutesString() {
		if(batts.size() < 1) {
			return uninitialized;
		}
		return Float.toString(estimatedMinutesRemaining);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((batts == null) ? 0 : batts.hashCode());
		result = prime * result + Float.floatToIntBits(estimatedMinutesRemaining);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		float EPSILON = 0.0001f;
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		if (obj instanceof EpsStateGds) {
			EpsStateGds other = (EpsStateGds)obj;
			if (batts == null) {
				if (other.batts != null) {
					return false;
				}
			} else if (!batts.equals(other.batts)) {
				return false;
			}
			if(Math.abs(estimatedMinutesRemaining - other.estimatedMinutesRemaining) > EPSILON) {
				return false;
			}
			return true;
		}
		return false;
	}

	public class BatteryInfoGds {
		protected final BatterySlotGds slot;
		protected final float designedCapacity;
		protected final float currentMaxCapacity;
		
		protected float percentage;
		protected float temperature;
		protected float voltage;
		protected float current;
		protected float remainingCapacity;

		public BatteryInfoGds(BatteryInfoConfig config) {
			slot = translateSlot(config.slot);
			designedCapacity = config.designedCapacity;
			currentMaxCapacity = config.currentMaxCapacity;
		}

		public BatteryInfoGds(BatteryInfoGds original) {
			slot = original.slot;
			designedCapacity = original.designedCapacity;
			currentMaxCapacity = original.currentMaxCapacity;
			percentage = original.percentage;
			temperature = original.temperature;
			voltage = original.voltage;
			current = original.current;
			remainingCapacity = original.remainingCapacity;
		}

		public void setTo(BatteryInfo battInfo) {
			percentage = battInfo.percentage;
			temperature = battInfo.temperature;
			voltage = battInfo.voltage;
			current = battInfo.current;
			remainingCapacity = battInfo.remainingCapacity;
		}

		public float getPercentage() {
			return percentage;
		}

		public float getTemperature() {
			return temperature;
		}

		public float getVoltage() {
			return voltage;
		}

		public float getCurrent() {
			return current;
		}

		public float getRemainingCapacity() {
			return remainingCapacity;
		}

		public BatterySlotGds getSlot() {
			return slot;
		}

		public float getDesignedCapacity() {
			return designedCapacity;
		}

		public float getCurrentMaxCapacity() {
			return currentMaxCapacity;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * slot.hashCode();
			result = prime * result + Float.floatToIntBits(designedCapacity);
			result = prime * result + Float.floatToIntBits(currentMaxCapacity);
			result = prime * result + Float.floatToIntBits(temperature);
			result = prime * result + Float.floatToIntBits(percentage);
			result = prime * result + Float.floatToIntBits(voltage);
			result = prime * result + Float.floatToIntBits(current);
			result = prime * result + Float.floatToIntBits(remainingCapacity);
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			float EPSILON = 0.0001f;
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;

			if (obj instanceof BatteryInfoGds) {
				BatteryInfoGds other = (BatteryInfoGds)obj;
				if(!slot.equals(other.slot)) {
					return false;
				}
				if(Math.abs(designedCapacity - other.designedCapacity) > EPSILON) {
					return false;
				}
				if(Math.abs(currentMaxCapacity - other.currentMaxCapacity) > EPSILON) {
					return false;
				}
				if(Math.abs(percentage - other.percentage) > EPSILON) {
					return false;
				}
				if(Math.abs(temperature - other.temperature) > EPSILON) {
					return false;
				}
				if(Math.abs(voltage - other.voltage) > EPSILON) {
					return false;
				}
				if(Math.abs(current - other.current) > EPSILON) {
					return false;
				}
				if(Math.abs(remainingCapacity - other.remainingCapacity) > EPSILON) {
					return false;
				}
				return true;
			}
			return false;
		}
	}
	
	public enum BatterySlotGds {
		 SLOT_TOP_LEFT,
		 SLOT_TOP_RIGHT,
		 SLOT_BOTTOM_LEFT,
		 SLOT_BOTTOM_RIGHT,
		 SLOT_UNKNOWN
	}
	
	protected BatterySlotGds translateSlot(rapid.ext.astrobee.BatterySlot original) {
		if(original.equals(rapid.ext.astrobee.BatterySlot.SLOT_BOTTOM_LEFT)) {
			return BatterySlotGds.SLOT_BOTTOM_LEFT;
		}
		if(original.equals(rapid.ext.astrobee.BatterySlot.SLOT_BOTTOM_RIGHT)) {
			return BatterySlotGds.SLOT_BOTTOM_RIGHT;
		}
		if(original.equals(rapid.ext.astrobee.BatterySlot.SLOT_TOP_LEFT)) {
			return BatterySlotGds.SLOT_TOP_LEFT;
		}
		if(original.equals(rapid.ext.astrobee.BatterySlot.SLOT_TOP_RIGHT)) {
			return BatterySlotGds.SLOT_TOP_RIGHT;
		}
		return BatterySlotGds.SLOT_UNKNOWN;
	}
}
