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
import gov.nasa.arc.irg.plan.model.SequenceHolder;
import gov.nasa.arc.irg.plan.model.Sequenceable;
import gov.nasa.arc.irg.plan.model.Station;
import gov.nasa.arc.irg.plan.modulebay.ModuleBayPlan;
import gov.nasa.arc.verve.robot.freeflyer.plan.PlanEditsRegistry;

public class DeleteCommand extends ReversibleCommand {
	private Sequenceable deleted;
	private Sequenceable reference;
	private boolean deletedWasLast = false;
	private ModuleBayPlan plan;
	private Station parent;

	public DeleteCommand(ModuleBayPlan mbp, Sequenceable seq) {
		plan = mbp;
		try {
			deleted = seq.clone();
		} catch (CloneNotSupportedException e) {
			System.out.println(e.toString());
		}
	}

	@Override
	public Sequenceable runCommand() {
		Sequenceable seq = null;

		if(deleted instanceof Station) {
			seq = findHighlightAfterStationCut((Station)deleted);
			reference = plan.getNextStation(deleted);
		}
		else if(deleted instanceof FreeFlyerCommand) {
			seq = findHighlightAfterCommandCut((FreeFlyerCommand) deleted);
			parent = (Station) ((FreeFlyerCommand) deleted).getParent();
			reference = plan.getNextCommand(parent, (FreeFlyerCommand) deleted);
		}
		if(reference == null) {
			deletedWasLast = true;
		}
		
		plan.deleteSequenceable(deleted);
		System.out.println("DeleteCommand runCommand() deleted "+deleted.toString());
		PlanEditsRegistry.onDelete(deleted);
		
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

	private Sequenceable findHighlightAfterStationCut(Station deleted) {
		Sequenceable newSeqSelection = null;
		int cutIndex = plan.indexOf(deleted);
		if(cutIndex > 0) {
			newSeqSelection = plan.getSequenceable(cutIndex-2);
		} else if(cutIndex+1 < plan.getSequence().size()) {
			newSeqSelection = plan.getSequenceable(cutIndex+2);
		}
		return newSeqSelection;
	}

	@Override
	// TODO implement undo for FreeFlyerCommand deletion
	public Sequenceable undoCommand() {
		if(deleted instanceof Station) {
			if(!deletedWasLast) {
				// reference is the thing after it
				plan.insertThisStation((Station)deleted, (Station)reference);
				PlanEditsRegistry.onInsert(deleted);
			} else {
				// put deleted on the end
				plan.addStation((Station)deleted);
				PlanEditsRegistry.onAppend(deleted);
			}
		} else if (deleted instanceof FreeFlyerCommand) {
			if(!deletedWasLast) {
				plan.insertThisCommand(parent, (FreeFlyerCommand)deleted, (FreeFlyerCommand)reference);
				PlanEditsRegistry.onInsert(deleted);
			} else {
				plan.appendThisCommand(parent, (FreeFlyerCommand)deleted);
				PlanEditsRegistry.onAppend(deleted);
			}
		}
		return deleted;
	}
}