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
import org.eclipse.e4.ui.model.application.ui.basic.MTrimmedWindow;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.IPresentationEngine;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.SWT;

public class LiveVideoWindowHandler {
	final String ID_WINDOW = "Live Video Window";
	final int SHELL_TRIM = SWT.CLOSE | SWT.TITLE | SWT.MIN ; //disable maximize and resizing 
	final int WIDTH = 800;
	final int HEIGHT = 600;
	
	@Execute
	public void execute(final MApplication application, final EModelService modelService) {
		System.out.println(this.getClass().getSimpleName() + " @Execute method called");
		
		// Add vlcj
		final MPart liveVideoPart = modelService.createModelElement(MPart.class);
		liveVideoPart.setContributionURI("bundleclass://gov.nasa.arc.verve.freeflyer.workbench/gov.nasa.arc.verve.freeflyer.workbench.parts.engineering.LiveVideoComboPartSimplified");
		liveVideoPart.setCloseable(true);
		liveVideoPart.setLabel("Live Video Part");
		liveVideoPart.getTags().add(EPartService.REMOVE_ON_HIDE_TAG);
		
		
		final MWindow window;
		window = modelService.createModelElement(MTrimmedWindow.class);
		window.setElementId(ID_WINDOW);
		window.setLabel("Video Streaming Window");
		window.setWidth(WIDTH);
		window.setHeight(HEIGHT);
		window.getPersistedState().put(IPresentationEngine.STYLE_OVERRIDE_KEY,"" + SHELL_TRIM);
		window.getChildren().add(0, liveVideoPart); 
		application.getChildren().add(window);
	}

	@CanExecute
	public boolean canExecute(final EPartService eps) {
		return true;
	}
}
