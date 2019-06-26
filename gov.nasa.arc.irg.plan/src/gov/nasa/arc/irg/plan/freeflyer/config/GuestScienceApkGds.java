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
package gov.nasa.arc.irg.plan.freeflyer.config;

import java.util.ArrayList;
import java.util.List;

import rapid.ext.astrobee.GuestScienceApk;
import rapid.ext.astrobee.GuestScienceCommand;

public class GuestScienceApkGds {
	protected String apkName;
	protected String shortName;
	protected boolean primary;
	protected List<GuestScienceCommandGds> guestScienceCommandGds;
	protected float power = 0;
	protected int duration = 0;

	public GuestScienceApkGds() {

	}

	public GuestScienceApk asGuestScienceApk() {
		GuestScienceApk ret = new GuestScienceApk();
		ret.apkName = apkName;
		ret.shortName = shortName;
		ret.primary = primary;

		if(guestScienceCommandGds != null) {
			for(GuestScienceCommandGds cmdGds : guestScienceCommandGds) {
				ret.commands.userData.add(cmdGds.asGuestScienceCommand());
			}
		}
		return ret;
	}

	public String getApkName() {
		return apkName;
	}

	public void setApkName(String apkName) {
		this.apkName = apkName;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public GuestScienceCommandGds getGuestScienceCommandFromName(String name) {
		for(GuestScienceCommandGds cmd : guestScienceCommandGds) {
			if(cmd.getName().equals(name)) {
				return cmd;
			}
		}
		return null;
	}

	public List<GuestScienceCommandGds> getGuestScienceCommands() {
		if(guestScienceCommandGds == null) {
			guestScienceCommandGds = new ArrayList<GuestScienceCommandGds>();
		}
		return guestScienceCommandGds;
	}

	public void setGuestScienceCommands(
			List<GuestScienceCommandGds> guestScienceCommandGds) {
		this.guestScienceCommandGds = guestScienceCommandGds;
	}

	public float getPower() {
		return power;
	}

	public void setPower(float power) {
		this.power = power;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public boolean isPrimary() {
		return primary;
	}

	public void setPrimary(boolean primary) {
		this.primary = primary;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((apkName == null) ? 0 : apkName.hashCode());
		result = prime * result + ((shortName == null) ? 0 : shortName.hashCode());
		result = prime * result + (primary ? 0 : 1);
		result = prime * result + ((guestScienceCommandGds == null) ? 0 : guestScienceCommandGds.hashCode());
		result = prime * result + Float.floatToIntBits(power);
		result = prime * result + duration;
		return result;
	}

	@Override
	public boolean equals(Object o) {
		float EPSILON = 0.0001f;

		if(this == o) {
			return true;
		}
		if(!(o instanceof GuestScienceApkGds)) {
			return false;
		}
		GuestScienceApkGds other = (GuestScienceApkGds)o;

		if(!getApkName().equals(other.getApkName())) {
			return false;
		}

		if(!getShortName().equals(other.getShortName())) {
			return false;
		}

		if(isPrimary() != other.isPrimary()) {
			return false;
		}

		List<GuestScienceCommandGds> othersCommands = other.getGuestScienceCommands();

		if(!getGuestScienceCommands().isEmpty()) {
			if(othersCommands.isEmpty()) {
				return false;
			} else { // both non-null
				if(guestScienceCommandGds.size() != othersCommands.size()) {
					return false;
				}

				for(int i=0; i<guestScienceCommandGds.size(); i++) {
					if(!guestScienceCommandGds.get(i).equals(othersCommands.get(i))){
						return false;
					}
				}
			}
		} else {
			if(!othersCommands.isEmpty()) {
				return false;
			}
		}

		if(Math.abs(getPower()- other.getPower())>EPSILON) {
			return false;
		}

		if(Math.abs(getDuration()- other.getDuration())>EPSILON) {
			return false;
		}

		return true;
	}
	
	@Override
	public String toString() {
		if(primary) {
			return shortName + ": " + power  + "W, " + duration + "s, primary";
		}
		else {
			return shortName + ": " + power  + "W, " + duration + "s, secondary";
		}
	}

	public static class GuestScienceCommandGds {
		private String name;
		private String command;
		private int duration = 0;
		private float power = 0;

		public GuestScienceCommandGds() {
		}

		public GuestScienceCommandGds(GuestScienceCommand input) {
			name = input.name;
			command = input.command;
		}

		public GuestScienceCommand asGuestScienceCommand() {
			GuestScienceCommand ret = new GuestScienceCommand();
			ret.name = name;
			ret.command = command;
			return ret;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getCommand() {
			return command;
		}

		public void setCommand(String command) {
			this.command = command;
		}

		public int getDuration() {
			return duration;
		}

		public void setDuration(int duration) {
			this.duration = duration;
		}

		public float getPower() {
			return power;
		}

		public void setPower(float power) {
			this.power = power;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			result = prime * result + ((command == null) ? 0 : command.hashCode());
			result = prime * result + Float.floatToIntBits(power);
			result = prime * result + duration;
			return result;
		}

		@Override
		public boolean equals(Object o) {
			float EPSILON = 0.0001f;

			if(this == o) {
				return true;
			}
			if(!(o instanceof GuestScienceCommandGds)) {
				return false;
			}
			GuestScienceCommandGds other = (GuestScienceCommandGds)o;

			if(!getName().equals(other.getName())) {
				return false;
			}

			if(!getCommand().equals(other.getCommand())) {
				return false;
			}

			if(Math.abs(getPower()- other.getPower())>EPSILON) {
				return false;
			}

			if(Math.abs(getDuration()- other.getDuration())>EPSILON) {
				return false;
			}

			return true;
		}
	}
}
