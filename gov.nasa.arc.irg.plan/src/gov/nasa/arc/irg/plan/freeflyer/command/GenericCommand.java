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

import gov.nasa.arc.irg.plan.util.PlanConstants;

import org.codehaus.jackson.annotate.JsonIgnore;

public class GenericCommand extends FreeFlyerCommand {

	protected String commandName;
	protected String param;
	
	@Override
	@JsonIgnore
	public String getDisplayName() {
		StringBuilder sb = new StringBuilder();
		sb.append(getName());
		if(commandName != null) {
			sb.append(" " + commandName);
		} else {
			sb.append(" " + PlanConstants.UNKNOWN_CHARACTER);
		}
		if( param != null) {
			sb.append(" " + param);
		} else {
			sb.append(" " + PlanConstants.UNKNOWN_CHARACTER);
		}
		
		return sb.toString();
	}
	
	public String getCommandName() {
		return commandName;
	}

	public void setCommandName(String commandName) {
		this.commandName = commandName;
	}

	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}

	@JsonIgnore
	public static String getClassNameForWidgetDropdown() {
		return "Generic Command";
	}
	
	@Override
	public int getCalculatedDuration() {
		return 0;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((commandName == null) ? 0 : commandName.hashCode());
		result = prime * result + ((param == null) ? 0 : param.hashCode());
		result = prime * result + ((m_name == null) ? 0 : m_name.hashCode());
		result = prime * result + ((m_notes == null) ? 0 : m_notes.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if(this == o) {
			return true;
		}
		// instanceof returns false for null
		if(!(o instanceof GenericCommand)) {
			return false;
		}
		if(!super.equals(o)) {
			return false;
		}

		GenericCommand other = (GenericCommand)o;

		if(commandName == null) {
			if(other.commandName != null) {
				return false;
			}
		} else {
			if(!commandName.equals(other.commandName)) {
				return false;
			}
		}
		
		if(param == null) {
			if(other.param != null) {
				return false;
			}
		} else {
			if(!param.equals(other.param)) {
				return false;
			}
		}
		
		return true;
	}
}
