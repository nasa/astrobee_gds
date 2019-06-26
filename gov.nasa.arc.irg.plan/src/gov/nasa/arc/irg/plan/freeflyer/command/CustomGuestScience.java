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
package gov.nasa.arc.irg.plan.freeflyer.command;

import gov.nasa.arc.irg.plan.freeflyer.config.GuestScienceApkGds;
import gov.nasa.arc.irg.plan.freeflyer.config.GuestScienceApkGds.GuestScienceCommandGds;

import org.codehaus.jackson.annotate.JsonIgnore;

public class CustomGuestScience extends AbstractGuestScienceCommand {
	protected String commandIndex; // databinding just gives us the selectedIndex in the Combo
	// can't do the fancy convert-index-to-object that we do with the others, because this Combo
	// changes depending on which command is selected
	protected GuestScienceCommandGds guestScienceCommandGds;

	protected String command; // command body, at top level for FSW

	@JsonIgnore
	public static String getClassNameForWidgetDropdown() {
		return "Custom Guest Science";
	}

	@Override
	public int getCalculatedDuration() {
		if(guestScienceCommandGds != null) {
			return guestScienceCommandGds.getDuration();
		}
		return 0;
	}

	@JsonIgnore
	public double getPower() {
		if(guestScienceCommandGds != null) {
			return guestScienceCommandGds.getPower();
		}
		return 0;
	}

	public String getCommandIndex() {
		return commandIndex;
	}

	public void setCommandIndex(String commandIndex) {
		this.commandIndex = commandIndex;
		int index = Integer.parseInt(commandIndex);

		if(guestScienceApkGds != null && guestScienceApkGds.getGuestScienceCommands() != null) {
			guestScienceCommandGds = guestScienceApkGds.getGuestScienceCommands().get(index);
		}

		if(guestScienceCommandGds != null) {
			command = guestScienceCommandGds.getCommand();
		}
	}

	@Override
	public void setGuestScienceApkGds(GuestScienceApkGds guestScienceApkGds) {
		super.setGuestScienceApkGds(guestScienceApkGds);
		if(commandIndex != null) {
			int index = Integer.parseInt(commandIndex);
			if(guestScienceApkGds != null && guestScienceApkGds.getGuestScienceCommands() != null) {
				guestScienceCommandGds = guestScienceApkGds.getGuestScienceCommands().get(index);
			}
		}
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	@Override
	public String getDisplayName() {
		StringBuilder ret = new StringBuilder(getName());

		if(guestScienceApkGds != null) {
			ret.append(" " + guestScienceApkGds.getShortName());
		}

		if(commandIndex != null && guestScienceCommandGds != null) {
			String cmdName = guestScienceCommandGds.getName();
			ret.append(" " + cmdName);
		}

		return ret.toString();
	}
}