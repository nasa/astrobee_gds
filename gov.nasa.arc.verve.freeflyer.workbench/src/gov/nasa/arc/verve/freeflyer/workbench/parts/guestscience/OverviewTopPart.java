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
package gov.nasa.arc.verve.freeflyer.workbench.parts.guestscience;

import gov.nasa.arc.irg.freeflyer.rapid.FreeFlyerStrings;
import gov.nasa.arc.irg.plan.ui.io.WorkbenchConstants;
import gov.nasa.arc.verve.freeflyer.workbench.dialog.OverviewMultiHealthAndStatusDialog;
import gov.nasa.rapid.v2.e4.agent.ActiveAgentSet;
import gov.nasa.rapid.v2.e4.agent.Agent;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

public class OverviewTopPart extends AdvancedGuestScienceTopPart {

	@Inject
	public OverviewTopPart(Composite parent, MApplication application) {
		super(parent, application);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void createControls(Composite parent) {
		GridLayout gl = new GridLayout(1, false);
		parent.setLayout(gl);

		makeAndPopulateMainComposite(parent);
		makeHealthAndStatusDetailsButton(parent);
		activeAgentSetChanged();
	}
	
	@Override
	protected void makeDetailsDialog() {
		OverviewMultiHealthAndStatusDialog dialog = ContextInjectionFactory.make(
				OverviewMultiHealthAndStatusDialog.class, context);
		dialog.create();
		dialog.setBlockOnOpen(false);
		dialog.open();
	}
	
	@Override
	protected AdvancedAstrobeeRow makeAstrobeeRow(Composite c, Agent agent, String contextString) {
		return new OverviewAstrobeeRow(c, agent, context, contextString);
	}
	
	@Override
	protected int getCellsAcross() {
		return GSSpaceHelper.overviewCellsAcross;
	}
	
	protected int getDetailsButtonWidth() {
		return GSSpaceHelper.overviewDetailsButtonWidth;
	}
	
	protected boolean isDetailsButtonEnabledByDefault() {
		return false;
	}
	
	@Override
	protected void setupRowOfTitles(Composite parent) {
		Composite c = new Composite(parent, SWT.None);
		GridLayout glLeft = new GridLayout(GSSpaceHelper.overviewCellsAcross, true);
		glLeft.marginWidth = 0;
		c.setLayout(glLeft);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).grab(true, false).span(GSSpaceHelper.overviewCellsAcross,1).applyTo(c);
		
		Label spacer = new Label(c, SWT.NONE);
		GridData selectorGd = new GridData(SWT.FILL, SWT.TOP, true, false);
		selectorGd.horizontalSpan = GSSpaceHelper.title_width;
		spacer.setText("Name");
		spacer.setToolTipText(NAME_TOOLTIP);
		spacer.setLayoutData(selectorGd);

		Label commTitle = new Label(c, SWT.NONE);
		commTitle.setText("Comm");
		commTitle.setToolTipText(COMM_TITLE_TOOLTIP);
		GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.BOTTOM).grab(true, true).span(GSSpaceHelper.comm_width,1).applyTo(commTitle);
		
		Label healthTitle = new Label(c, SWT.NONE);
		healthTitle.setText("Health");
		healthTitle.setToolTipText(HEALTH_TOOLTIP);
		GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.BOTTOM).span(GSSpaceHelper.health_width,1).grab(true, false).applyTo(healthTitle);

		Label battTitle = new Label(c, SWT.NONE);
		battTitle.setText("Est Batt");
		battTitle.setToolTipText(WorkbenchConstants.BATT_TOOLTIP);
		GridData battGd = new GridData(SWT.FILL, SWT.TOP, true, false);
		battGd.horizontalSpan = GSSpaceHelper.batt_width;
		battTitle.setLayoutData(battGd);
		
		Label controlTitle = new Label(c, SWT.NONE);
		controlTitle.setText("Control");
		controlTitle.setToolTipText(WorkbenchConstants.CONTROL_TOOLTIP);
		GridData controlGd = new GridData(SWT.FILL, SWT.TOP, true, false);
		controlGd.horizontalSpan = GSSpaceHelper.control_width_for_summary;
		controlTitle.setLayoutData(controlGd);
	}
	
	@Override
	public void activeAgentSetChanged() {
		super.activeAgentSetChanged();
		
		for(int i=0; i<astrobeeRows.length; i++) {
			if(ActiveAgentSet.contains(astrobeeRows[i].getAgent())) {
				// somebody is connected
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						healthDetailsButton.setEnabled(true);
					}
				});
				return;
			}
		}
		// nobody is connected
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				healthDetailsButton.setEnabled(false);
			}
		});
	}
	
	@Inject @Optional
	public void acceptGuestScienceAgent1(@Named(FreeFlyerStrings.SELECTED_GUEST_SCIENCE_1) Agent a) {
		// No-op
	}

	@Inject @Optional
	public void acceptGuestScienceAgent2(@Named(FreeFlyerStrings.SELECTED_GUEST_SCIENCE_2) Agent a) {
		// No-op
	}

	@Inject @Optional
	public void acceptGuestScienceAgent3(@Named(FreeFlyerStrings.SELECTED_GUEST_SCIENCE_3) Agent a) {
		// No-op
	}
	
}
