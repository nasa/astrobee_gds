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

import gov.nasa.arc.ff.ocu.handlers.ShowTeleoperationTabHandler;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;

public class ShowTeleoperationTabReferredHandler {
	
	@Inject @Optional
	public void execute(MApplication application,
			@Named(ShowTeleoperationTabHandler.TELEOPERATION_EPARTSERVICE) EPartService partService) {
		
		if(partService == null) {
			return;
		}
		
		IEclipseContext context = application.getContext();
		context.set(ShowTeleoperationTabHandler.TELEOPERATION_EPARTSERVICE, null);
		
		partService.showPart("gov.nasa.arc.ff.ocu.compositepart.teleopTab",
				PartState.ACTIVATE); // Show part
	}
}
