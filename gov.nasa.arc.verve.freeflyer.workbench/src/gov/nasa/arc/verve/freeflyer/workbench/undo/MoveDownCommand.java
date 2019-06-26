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
import gov.nasa.arc.irg.plan.model.Sequenceable;
import gov.nasa.arc.irg.plan.model.Station;
import gov.nasa.arc.irg.plan.modulebay.ModuleBayPlan;
import gov.nasa.arc.verve.ardor3d.e4.util.DeselectListenerRegistry;
import gov.nasa.arc.verve.robot.freeflyer.plan.PlanEditsRegistry;

public class MoveDownCommand extends ReversibleCommand {
	private Sequenceable original;
	private Sequenceable moved;
	private Sequenceable clone;
	private Station parent;
	private ModuleBayPlan plan;
	
	public MoveDownCommand(ModuleBayPlan mpb, Sequenceable seq) {
		original = seq;
		plan = mpb;
	}

	@Override
	public Sequenceable runCommand() {
		if(original instanceof Station) {
			DeselectListenerRegistry.onDeselect(); // so the AbstractPlanTrace doesn't get messed up
			moved = plan.moveThisStationDown((Station)original);
		}
		else if(original instanceof FreeFlyerCommand) {
			Station parentStation = (Station)original.getParent();
			moved = parentStation.moveThisCommandDown(original);
			try {
				parent = (Station) moved.getParent().clone();
			} catch (CloneNotSupportedException e) {
			
			}
		}
		PlanEditsRegistry.onMoveDown(moved);
		try {
			clone = moved.clone();
		} catch (CloneNotSupportedException e) {
			
		}
		return moved;
	}

	@Override
	public Sequenceable undoCommand() {
		if(moved instanceof Station) {
			int index = plan.getSequence().indexOf(clone);
			moved = plan.getSequenceable(index);
		} else {
			int index = plan.getSequence().indexOf(parent);
			parent = (Station) plan.getSequenceable(index);
			int subIndex = parent.getSequence().indexOf(clone);
			moved = parent.getSequenceable(subIndex);
		}
		
		if(moved instanceof Station) {
			DeselectListenerRegistry.onDeselect(); // so the AbstractPlanTrace doesn't get messed up
			plan.moveThisStationUp((Station)moved);
		}
		else if(moved instanceof FreeFlyerCommand) {
			Station parentStation = (Station)moved.getParent();
			original = parentStation.moveThisCommandUp(moved);
		}
		PlanEditsRegistry.onMoveUp(original);
		return moved;
	}
}
