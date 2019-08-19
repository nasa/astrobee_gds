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

import gov.nasa.arc.irg.plan.model.PlanCommand;
import gov.nasa.arc.irg.plan.model.Sequenceable;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonSubTypes.Type;

@JsonSubTypes({  
	@Type(value = Sequenceable.class, name = "Sequenceable"),  
	@Type(value = FreeFlyerCommand.class, name = "FreeFlyerCommand"),
	@Type(value = ArmPanAndTilt.class, name = "armPanAndTilt"),
	@Type(value = ClearData.class, name = "clearData"),
	@Type(value = CustomGuestScience.class, name = "customGuestScience"),
	@Type(value = Dock.class, name = "dock"),
	@Type(value = DownloadData.class, name = "downloadData"),
	@Type(value = SetFlashlightBrightness.class, name = "setFlashlightBrightness"),
	@Type(value = GenericCommand.class, name = "genericCommand"),
	@Type(value = GripperControl.class, name = "gripperControl"),
	@Type(value = InitializeBias.class, name = "initializeBias"),
	@Type(value = IdlePropulsion.class, name = "idlePropulsion"),
	@Type(value = PausePlan.class, name = "pausePlan"),
	@Type(value = Perch.class, name = "perch"),
	@Type(value = PowerOffItem.class, name = "powerOffItem"),
	@Type(value = PowerOnItem.class, name = "powerOnItem"),
	@Type(value = SetCameraRecording.class, name = "setCameraRecording"),
	@Type(value = SetCamera.class, name = "setCamera"),
	@Type(value = SetCameraStreaming.class, name = "setCameraStreaming"),
	@Type(value = SetCheckObstacles.class, name = "setCheckObstacles"),
	@Type(value = SetCheckZones.class, name = "setCheckZones"),
	@Type(value = SetHolonomicMode.class, name = "setHolonomicMode"),
	@Type(value = SetPlanner.class, name = "setPlanner"),
	@Type(value = SetTelemetryRate.class, name = "setTelemetryRate"),
	@Type(value = StartGuestScience.class, name = "startGuestScience"),
	@Type(value = StartRecording.class, name = "startRecording"),
	@Type(value = StopRecording.class, name = "stopRecording"),
	@Type(value = StopGuestScience.class, name = "stopGuestScience"),
	@Type(value = SwitchLocalization.class, name = "switchLocalization"),
	@Type(value = Undock.class, name = "undock"),
	@Type(value = Unperch.class, name = "unperch"),
	@Type(value = Wait.class, name = "wait")

})
public abstract class FreeFlyerCommand extends PlanCommand {

	public static final String freeFlyerSubsystem = "freeFlyer";
	protected static final String commandName = "FreeFlyerCommand";
	protected static List<Class<? extends FreeFlyerCommand>> commandTypes;
	

	public FreeFlyerCommand() {
		super();
	}
	
	public static List<Class<? extends FreeFlyerCommand>> getCommandTypes() {
		if(commandTypes == null) {
			setUpCommandTypes();
		}
		return commandTypes;
	}
	
	private static void setUpCommandTypes() {
		commandTypes = new ArrayList<Class<? extends FreeFlyerCommand>>();
		commandTypes.add(ArmPanAndTilt.class);
		commandTypes.add(ClearData.class);
		commandTypes.add(CustomGuestScience.class);
		commandTypes.add(Dock.class);
		commandTypes.add(DownloadData.class);
		commandTypes.add(SetFlashlightBrightness.class);
		commandTypes.add(GenericCommand.class);
		commandTypes.add(GripperControl.class);
		commandTypes.add(IdlePropulsion.class);
		commandTypes.add(InitializeBias.class);
		commandTypes.add(PausePlan.class);
		commandTypes.add(Perch.class);
		commandTypes.add(PowerOffItem.class);
		commandTypes.add(PowerOnItem.class);
		commandTypes.add(SetCamera.class);
		commandTypes.add(SetCameraRecording.class);
		commandTypes.add(SetCameraStreaming.class);
		commandTypes.add(SetCheckObstacles.class);
		commandTypes.add(SetCheckZones.class);
		commandTypes.add(SetHolonomicMode.class);
		commandTypes.add(SetPlanner.class);
		commandTypes.add(SetTelemetryRate.class);
		commandTypes.add(StartGuestScience.class);
		commandTypes.add(StartRecording.class);
		commandTypes.add(StopRecording.class);
		commandTypes.add(StopGuestScience.class);
		commandTypes.add(SwitchLocalization.class);
		commandTypes.add(Undock.class);
		commandTypes.add(Unperch.class);
		commandTypes.add(Wait.class);
	}

	/**
	 * @return the subsystem, ie the phone or the spheres
	 */
	@JsonIgnore
	public String getSubsystem() {
		return freeFlyerSubsystem;
	}

	@JsonIgnore
	public static String getCustomName() {
		return commandName;
	}
	
	@Override
	public void autoName(int index) {
		String newName = getClass().getSimpleName();
		if (getParent() != null && getParent().getName() != null){
			newName = getParent().getName() + "." + index + " " + getClass().getSimpleName();
		}
		if (getName() == null || !getName().equals(newName)){
			setName(newName);
		}
	}

	/**
	 * call refresh() on previous, next, and parent
	 */
	protected void refreshSequence(Sequenceable seq) {
		// tell everything to recalculate because something changed
		if(getPrevious() != null && getPrevious() != seq) {
			getPrevious().refresh(seq);
		}
		if(getNext() != null && getNext() != seq) {
			getNext().refresh(seq);
		}
		if(getParent() instanceof Sequenceable) {
			((Sequenceable)getParent()).refresh(seq);
		}
	}

	public void refresh(Sequenceable seq) {
		// TODO update name and time
		refreshSequence(seq);
	}
	
	/**
	 *  Want to be able to override so we can show extra info 
	 *  in payload and Guest Science commands
	 */
	@JsonIgnore
	public String getDisplayName() {
		return getName();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((m_name == null) ? 0 : m_name.hashCode());
		result = prime * result + ((m_notes == null) ? 0 : m_notes.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if(this == o) {
			return true;
		}
		// Don't check super - will fail if not same object
		// instanceof returns false for null
		if(!(o instanceof FreeFlyerCommand)) {
			return false;
		}
		FreeFlyerCommand other = (FreeFlyerCommand)o;

		if(!getName().equals(other.getName())) {
			return false;
		}
		if(getNotes() == null) {
			if(other.getNotes() != null) {
				return false;
			}
		} else if(!getNotes().equals(other.getNotes())) {
			return false;
		}
		return true;
	}
}