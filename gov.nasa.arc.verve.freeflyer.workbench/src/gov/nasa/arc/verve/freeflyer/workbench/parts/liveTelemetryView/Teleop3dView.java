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

import gov.nasa.arc.irg.plan.ui.io.EnlargeableButton;
import gov.nasa.arc.verve.freeflyer.workbench.helpers.LiveTelemetryViewMovementRegistry;
import gov.nasa.arc.verve.freeflyer.workbench.parts.standard.BeeCommandingPartOnTeleoperateTab;
import gov.nasa.arc.verve.freeflyer.workbench.utils.TrackVisibleBeeCommandingSubtab;

import javax.inject.Inject;

import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class Teleop3dView extends LiveTelemetryView {

	BeeCommandingPartOnTeleoperateTab manualCommandPart;
	@Inject
	public Teleop3dView(EPartService eps, Shell shell, MApplication application) {
		super(eps, shell, application);
		MY_TAB_NAME = TabName.TELEOP;
		
		manualCommandPart = application.getContext().get(BeeCommandingPartOnTeleoperateTab.class);
	}

	@Override
	protected boolean showMe() {
		if(super.showMe()) {
			freeFlyerScenario.teleopAbsoluteTranslateTabOnTop(false);
			return true;
		}
		return false;
	}
	
	@Override
	protected void toggleArrowsDialog(final boolean show){
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				if (show){
					if (m_arrowsDialog == null){
						m_arrowsDialog = new ArrowsDialogWithPreviewButton(savedShell);
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
	
	protected class ArrowsDialogWithPreviewButton extends ArrowsDialog {
		private final String CENTER_ON_PREVIEW_TOOLTIP = "Put Preview at the Center of the Map";
		private EnlargeableButton zoomToPreviewButton;

		public ArrowsDialogWithPreviewButton(Shell parent) {
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
			createZoomToBeeButton(parent);
			createZoomToPreviewButton(parent);
			return parent;
		}
		
		protected void createZoomToBeeButton(Composite parent) {
			zoomToBeeButton = new EnlargeableButton(parent, SWT.None);
			zoomToBeeButton.setText("Zoom to Bee");
			zoomToBeeButton.setToolTipText(ZOOM_TO_BEE_TOOLTIP);
			GridData cbGD = new GridData(SWT.FILL, SWT.TOP, true, false);
			cbGD.horizontalSpan = 2;
			zoomToBeeButton.setLayoutData(cbGD);
			zoomToBeeButton.setButtonLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
			zoomToBeeButton.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					LiveTelemetryViewMovementRegistry.zoomToBee();
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e)  {/**/}
			});
			if(!haveBeeToZoomTo()) {
				zoomToBeeButton.setEnabled(false);
			}
		}
		
		protected void createZoomToPreviewButton(Composite parent) {
			zoomToPreviewButton = new EnlargeableButton(parent, SWT.None);
			zoomToPreviewButton.setText("Zoom to Preview");
			zoomToPreviewButton.setToolTipText(CENTER_ON_PREVIEW_TOOLTIP);
			GridData cbGD = new GridData(SWT.FILL, SWT.TOP, true, false);
			cbGD.horizontalSpan = 2;
			zoomToPreviewButton.setLayoutData(cbGD);
			zoomToPreviewButton.setButtonLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
			zoomToPreviewButton.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					if(TrackVisibleBeeCommandingSubtab.INSTANCE.isAbsolutePreviewShowing()) {
						LiveTelemetryViewMovementRegistry.zoomToAbsolutePreview();
						return;
					}
					if(TrackVisibleBeeCommandingSubtab.INSTANCE.isRelativePreviewShowing()) {
						LiveTelemetryViewMovementRegistry.zoomToRelativePreview();
						return;
					}
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e)  {/**/}
			});
			if(!haveBeeToZoomTo()) {
				zoomToPreviewButton.setEnabled(false);
			}
		}
		
		public void snapToBee() {
			manualCommandPart.snapToBee();
		}

		public void disableButtonsThatZoomToBee() {
			if(zoomToPreviewButton != null && !zoomToPreviewButton.isDisposed()) {
				zoomToPreviewButton.setEnabled(false);
				zoomToPreviewButton.setEnabled(false);
				zoomToBeeButton.setEnabled(false);
			}
		}
		
		public void enableButtonsThatZoomToBee() {
			if(zoomToPreviewButton != null && !zoomToPreviewButton.isDisposed()) {
				zoomToPreviewButton.setEnabled(true);
				zoomToPreviewButton.setEnabled(true);
				zoomToBeeButton.setEnabled(true);
			}
		}
	}
}
