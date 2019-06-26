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
import gov.nasa.rapid.v2.e4.agent.Agent;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class Overview3dView extends LiveTelemetryView {

	@Inject
	public Overview3dView(EPartService eps, Shell shell,
			MApplication application) {
		super(eps, shell, application);
		MY_TAB_NAME = TabName.OVERVIEW;
	}
	
	@Override
	protected void toggleArrowsDialog(final boolean show){
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				if (show){
					if (m_arrowsDialog == null){
						m_arrowsDialog = new ArrowsDialogMinusZoomToBeeButton(savedShell);
					} 
					m_arrowsDialog.open();
				} else {
					if (m_arrowsDialog != null){
						m_arrowsDialog.close();
					}
				}
			}
		});
	}
	
	@Override @Inject @Optional
	public void acceptAgent(@Named(FreeFlyerStrings.PRIMARY_BEE) Agent agent) {
		// don't zoom to it because we're the overview
	}
	
	@Override
	protected boolean showMe() {
		if(super.showMe()) {
			freeFlyerScenario.showRobotsGuestSciencePlanTraceAndKeepoutsNoPreview();
			return true;
		}
		return false;
	}
	
	protected class ArrowsDialogMinusZoomToBeeButton extends ArrowsDialog {

		public ArrowsDialogMinusZoomToBeeButton(Shell parent) {
			super(parent);
		}
		
		@Override
		protected Control createLowerButtons(Composite c) {
			Composite parent = new Composite(c, 0);
			GridLayout layout = new GridLayout(2, true);
			parent.setLayout(layout);

			createZoomInButton(parent);
			createZoomOutButton(parent);
			createResetViewButton(parent);
			return parent;
		}
		
	}

}
