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

import javax.inject.Inject;

import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.widgets.Shell;

public class Debug3dView extends LiveTelemetryView {

	@Inject
	public Debug3dView(EPartService eps, Shell shell, MApplication application) {
		super(eps, shell, application);
		MY_TAB_NAME = TabName.DEBUGGING;
	}

	@Override
	protected boolean showMe() {
		if(super.showMe()) {
			freeFlyerScenario.showRobotsRunPlanTraceAndKeepoutsNoPreview();
			updateCameraCenterOfInterest();
			return true;
		}
		return false;
	}
	
}
