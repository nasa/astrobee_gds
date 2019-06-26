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
package gov.nasa.arc.verve.freeflyer.workbench.dialog;

import gov.nasa.arc.irg.freeflyer.rapid.FreeFlyerStrings;
import gov.nasa.arc.irg.freeflyer.rapid.state.GuestScienceAstrobeeStateManager;
import gov.nasa.arc.verve.freeflyer.workbench.utils.AgentsFromCommandLine;
import gov.nasa.rapid.v2.e4.agent.ActiveAgentSet;
import gov.nasa.rapid.v2.e4.agent.Agent;

import java.util.List;
import java.util.Vector;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.swt.widgets.Shell;

public class OverviewMultiHealthAndStatusDialog extends StandardMultiHealthAndStatusDialog {

	@Inject
	public OverviewMultiHealthAndStatusDialog(@Named(IServiceConstants.ACTIVE_SHELL) Shell parent, MApplication mapp,
			@Optional Agent selected1,
			@Optional Agent selected2,
			@Optional Agent selected3,
			@Optional @Named(FreeFlyerStrings.GUEST_SCIENCE_ASTROBEE_STATE_MANAGER_1) GuestScienceAstrobeeStateManager manager1,
			@Optional @Named(FreeFlyerStrings.GUEST_SCIENCE_ASTROBEE_STATE_MANAGER_2) GuestScienceAstrobeeStateManager manager2,
			@Optional @Named(FreeFlyerStrings.GUEST_SCIENCE_ASTROBEE_STATE_MANAGER_3) GuestScienceAstrobeeStateManager manager3) {
		super(parent, mapp, selected1, selected2, selected3, manager1, manager2,
				manager3);
	}

	protected void setUpTheSelectedAgents(Agent selected1, Agent selected2, Agent selected3,
			GuestScienceAstrobeeStateManager manager1, GuestScienceAstrobeeStateManager manager2, GuestScienceAstrobeeStateManager manager3) {
		selectedGuestScience = new Vector<Agent>();
		manager = new Vector<GuestScienceAstrobeeStateManager>();

		List<Agent> commandLineAgents = AgentsFromCommandLine.INSTANCE.getAgentsList();

		Agent candidate1 = commandLineAgents.get(0);
		if( ActiveAgentSet.contains(candidate1)) {
			selectedGuestScience.add(candidate1);
			manager.add(manager1);
			if(manager1 != null) {
				manager1.addListener(this);
			}
		}

		Agent candidate2 = commandLineAgents.get(1);
		if(ActiveAgentSet.contains(candidate2)) {
			selectedGuestScience.add(candidate2);
			manager.add(manager2);
			if(manager2 != null) {
				manager2.addListener(this);
			}
		}
		
		Agent candidate3 = commandLineAgents.get(2);
		if(ActiveAgentSet.contains(candidate3)) {
			selectedGuestScience.add(candidate3);
			manager.add(manager3);
			if(manager3 != null) {
				manager3.addListener(this);
			}
		}
	}
}
