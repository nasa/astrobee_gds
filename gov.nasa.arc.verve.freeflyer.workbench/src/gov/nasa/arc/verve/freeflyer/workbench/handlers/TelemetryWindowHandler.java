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
package gov.nasa.arc.verve.freeflyer.workbench.handlers;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MCompositePart;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;

public class TelemetryWindowHandler {
	final String COMPOSITE_ID = "gov.nasa.arc.verve.freeflyer.workbench.compositepart.guestScienceTelemetry";
	final String CONTRIBUTION_URI = "bundleclass://gov.nasa.arc.verve.freeflyer.workbench/gov.nasa.arc.verve.freeflyer.workbench.parts.guestscience.GuestScienceTelemetryPartFragment";
	final String ID_PART_PREFIX = "Telemetry";
	int partCnt = 0;
	
	@Execute
	public void execute(MApplication application, EPartService partService, EModelService modelService) {
		System.out.println(this.getClass().getSimpleName() 
			      + " @Execute method called");
		
		// Get the stack
		MCompositePart composite = (MCompositePart)modelService.find(COMPOSITE_ID, application);

		// Create a new Part
		MPart part = modelService.createModelElement(MPart.class);
		part.setElementId(ID_PART_PREFIX + ++partCnt);
		part.setContributionURI(CONTRIBUTION_URI);
		part.setCloseable(true);
		part.setLabel("Select Telemetry");
		part.getTags().add(EPartService.REMOVE_ON_HIDE_TAG);
		
		composite.getChildren().add(part); // Add part to stack
		partService.showPart(part, PartState.ACTIVATE); // Show part
	}

	@CanExecute
	public boolean canExecute(EPartService eps) {
		return true;
	}
}
