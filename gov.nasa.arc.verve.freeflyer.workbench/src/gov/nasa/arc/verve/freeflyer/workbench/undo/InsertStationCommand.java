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
import gov.nasa.arc.irg.plan.model.Sequenceable;
import gov.nasa.arc.irg.plan.model.Station;
import gov.nasa.arc.irg.plan.modulebay.ModuleBayPlan;
import gov.nasa.arc.verve.robot.freeflyer.plan.PlanEditsRegistry;

public class InsertStationCommand extends ReversibleCommand {
	private Station inserted;
	private Sequenceable reference;
	private ModuleBayPlan plan;
	
	public InsertStationCommand(ModuleBayPlan plan, Sequenceable ref) {
		this.plan = plan;
		reference = ref;
	}
	
	@Override
	public Sequenceable runCommand() {
		inserted = plan.insertNewStation(reference);
		PlanEditsRegistry.onInsert(inserted);
		return inserted;
	}

	@Override
	public Sequenceable undoCommand() {
		plan.removeSequenceable(inserted);
		PlanEditsRegistry.onDelete(inserted);
		return reference;
	}

}
