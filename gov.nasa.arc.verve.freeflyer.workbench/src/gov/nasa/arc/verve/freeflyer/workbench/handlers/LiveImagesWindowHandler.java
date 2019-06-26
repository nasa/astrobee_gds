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
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.model.application.ui.basic.MTrimmedWindow;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.IPresentationEngine;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.eclipse.swt.SWT;

public class LiveImagesWindowHandler {
	final String ID_STACK = "aPartStack";
	final String ID_WINDOW = "secondWindow";
	final String ID_PART_PREFIX = "PartNumber";
	int partCnt = 0;
	
	@Execute
	public void execute(MApplication application, EModelService modelService) {
		System.out.println(this.getClass().getSimpleName() 
			      + " @Execute method called");
		
		EPartService partService = null;
		MWindow window;
		// Check if the Part already exists
		MPartStack stack = (MPartStack)modelService.find(ID_STACK, application);
		if (stack == null) { // and create it if not
			stack = modelService.createModelElement(MPartStack.class);
			stack.setElementId(ID_STACK);
			
			// Find the Default-Window to place the Part
			 window = (MWindow)modelService.find(ID_WINDOW, application);
			if (window == null) { // and create it if it doesn't exist  (expected)
				window = modelService.createModelElement(MTrimmedWindow.class);
				window.setElementId(ID_WINDOW);
				window.setLabel("Live Images Window");
				window.getPersistedState().put(IPresentationEngine.STYLE_OVERRIDE_KEY, "" + SWT.SHELL_TRIM);
				
				application.getChildren().add(window); // Add window to application
			}
			
			window.getChildren().add(0, stack); // Add stack to the window
		} else {
			 window = (MWindow)modelService.find(ID_WINDOW, application);
		}
		partService = window.getContext().get(EPartService.class);

		// Create a new Part
		MPart part = modelService.createModelElement(MPart.class);
		part.setElementId(ID_PART_PREFIX + ++partCnt);
		part.setContributionURI("bundleclass://gov.nasa.arc.verve.freeflyer.workbench/gov.nasa.arc.verve.freeflyer.workbench.parts.standard.LiveImagesSelectAstrobeePart");
		part.setCloseable(true);
		part.setLabel("Part " + partCnt);
		part.getTags().add(EPartService.REMOVE_ON_HIDE_TAG);
		
		stack.getChildren().add(part); // Add part to stack
		partService.showPart(part, PartState.ACTIVATE); // Show part
	}

	@CanExecute
	public boolean canExecute(EPartService eps) {
		return true;
	}
}
