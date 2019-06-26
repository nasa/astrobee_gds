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
package gov.nasa.arc.verve.freeflyer.workbench.undo;

import gov.nasa.arc.ff.ocu.commands.ReversibleCommand;
import gov.nasa.arc.irg.plan.freeflyer.command.FreeFlyerCommand;
import gov.nasa.arc.irg.plan.freeflyer.plan.FreeFlyerPlan;
import gov.nasa.arc.irg.plan.model.SequenceHolder;
import gov.nasa.arc.irg.plan.model.Sequenceable;
import gov.nasa.arc.irg.plan.model.Station;
import gov.nasa.arc.irg.plan.modulebay.ModuleBayPlan;
import gov.nasa.arc.verve.robot.freeflyer.plan.PlanEditsRegistry;

public class AppendCommandCommand extends ReversibleCommand {
	private ModuleBayPlan moduleBayPlan;
	private Station station;
	private Station clone;
	private String commandText;
	private FreeFlyerCommand command;
	private FreeFlyerCommand commandClone;
	
	public AppendCommandCommand(ModuleBayPlan mbp, Station s, String sc) {
		moduleBayPlan = mbp;
		station = s;
		commandText = sc;
	}
	
	@Override
	public Sequenceable runCommand() {
		command = moduleBayPlan.appendNewCommand(station, commandText);
		PlanEditsRegistry.onAppend(command);
		
		try {
			clone = station.clone();
			commandClone = (FreeFlyerCommand) command.clone();
		} catch (CloneNotSupportedException e) {

		}
		return command;
	}

	@Override
	public Sequenceable undoCommand() {
		//This is necessary in case the legitimate parent of command is deleted by other commands
		//before an undo is commenced, leaving command with a null parent reference.
		int stationIndex = moduleBayPlan.getSequence().indexOf(clone);
		station = (Station) moduleBayPlan.getSequenceable(stationIndex);
		int subIndex = station.getSequence().indexOf(commandClone);
		command = (FreeFlyerCommand) station.getSequence().get(subIndex);
		
		Sequenceable seq = findHighlightAfterCommandCut((FreeFlyerCommand) command);
		
		moduleBayPlan.deleteSequenceable(command);
		System.out.println("DeleteCommand runCommand() deleted "+command.toString());
		PlanEditsRegistry.onDelete(command);
		
		return seq;
	}
	
	private Sequenceable findHighlightAfterCommandCut(FreeFlyerCommand cut) {
		Sequenceable newSeqSelection = null;
		SequenceHolder parentStation = cut.getParent();

		int cutIndex = parentStation.indexOf(cut);
		if(cutIndex > 0) {
			newSeqSelection = parentStation.getSequenceable(cutIndex-1);
		} else if(cutIndex+1 < parentStation.getSequence().size()) {
			newSeqSelection = parentStation.getSequenceable(cutIndex+1);
		} else {
			newSeqSelection = (Sequenceable) parentStation;
		}
		return newSeqSelection;
	}

}
