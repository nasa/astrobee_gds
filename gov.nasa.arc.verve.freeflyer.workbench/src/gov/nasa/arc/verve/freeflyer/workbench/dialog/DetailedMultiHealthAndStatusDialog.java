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
import gov.nasa.arc.irg.freeflyer.rapid.state.AstrobeeStateAdapter.StateTableRow;
import gov.nasa.rapid.v2.e4.agent.Agent;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.swt.widgets.Shell;

public class DetailedMultiHealthAndStatusDialog extends
		StandardMultiHealthAndStatusDialog {

	@Inject
	public DetailedMultiHealthAndStatusDialog(@Named(IServiceConstants.ACTIVE_SHELL) Shell parent,
			MApplication mapp, 
			@Optional @Named(FreeFlyerStrings.SELECTED_GUEST_SCIENCE_1) Agent selected1,
			@Optional @Named(FreeFlyerStrings.SELECTED_GUEST_SCIENCE_2) Agent selected2,
			@Optional @Named(FreeFlyerStrings.SELECTED_GUEST_SCIENCE_3) Agent selected3,
			@Optional @Named(FreeFlyerStrings.GUEST_SCIENCE_ASTROBEE_STATE_MANAGER_1) GuestScienceAstrobeeStateManager manager1,
			@Optional @Named(FreeFlyerStrings.GUEST_SCIENCE_ASTROBEE_STATE_MANAGER_2) GuestScienceAstrobeeStateManager manager2,
			@Optional @Named(FreeFlyerStrings.GUEST_SCIENCE_ASTROBEE_STATE_MANAGER_3) GuestScienceAstrobeeStateManager manager3
			) {
		super(parent, mapp, selected1, selected2, selected3, manager1, manager2,
				manager3);
	}

	public List<StateTableRow> getMyInput(GuestScienceAstrobeeStateManager mgr) {
		return mgr.getAdapter().getDetailedHealthAndStatusData();
	}
	
}
