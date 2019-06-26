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
package gov.nasa.arc.verve.freeflyer.workbench.parts.liveTelemetryView;

import gov.nasa.arc.irg.freeflyer.rapid.FreeFlyerStrings;
import gov.nasa.arc.verve.robot.AbstractRobot;
import gov.nasa.rapid.v2.e4.agent.Agent;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.widgets.Shell;

public class AdvancedGuestScience3dView extends LiveTelemetryView {
	int NUM_BEES = 3;
	protected boolean[] selectedAstrobees = new boolean[NUM_BEES];

	@Inject
	public AdvancedGuestScience3dView(EPartService eps, Shell shell, MApplication application) {
		super(eps, shell, application);

		for(int i=0; i<NUM_BEES; i++) {
			selectedAstrobees[i] = false;
		}

		MY_TAB_NAME = TabName.ADVANCED_GUEST_SCIENCE;
	}

	@Override
	protected boolean showMe() {
		if(super.showMe()) {
			freeFlyerScenario.showRobotsGuestSciencePlanTraceAndKeepoutsNoPreview();
			return true;
		}
		return false;
	}

	@Override
	public void zoomToBee() {
		AbstractRobot selectedRobot;

		for(int i=0; i<NUM_BEES; i++) {
			if(selectedAstrobees[i]) {
				selectedRobot = freeFlyerScenario.getAllRobots()[i];
				getCameraControl().setCenterOfInterest(selectedRobot);
				getCameraControl().setDistance(AUTO_ZOOM_DISTANCE);
			}
		}
	}

	protected boolean haveBeeToZoomTo() {
		for(int i=0; i<NUM_BEES; i++) {
			if(selectedAstrobees[i]) {
				return true;
			}
		}
		return false;
	}

	@Inject @Optional
	public void acceptAgent(@Named(FreeFlyerStrings.PRIMARY_BEE) Agent agent) {
		// we aren't concerned with this because we watch the Guest Science selection
	}

	@Inject @Optional
	public void acceptGuestScienceAgent1(@Named(FreeFlyerStrings.SELECTED_GUEST_SCIENCE_1) Agent a) {
		if(a == null) {
			selectedAstrobees[0] = false;
		} else {
			selectedAstrobees[0] = true;
		}
		updateButtonsThatZoomToBee();
	}

	@Inject @Optional
	public void acceptGuestScienceAgent2(@Named(FreeFlyerStrings.SELECTED_GUEST_SCIENCE_2) Agent a) {
		if(a == null) {
			selectedAstrobees[1] = false;
		} else {
			selectedAstrobees[1] = true;
		}
		updateButtonsThatZoomToBee();
	}

	@Inject @Optional
	public void acceptGuestScienceAgent3(@Named(FreeFlyerStrings.SELECTED_GUEST_SCIENCE_3) Agent a) {
		if(a == null) {
			selectedAstrobees[2] = false;
		} else {
			selectedAstrobees[2] = true;
		}
		updateButtonsThatZoomToBee();
	}
}
