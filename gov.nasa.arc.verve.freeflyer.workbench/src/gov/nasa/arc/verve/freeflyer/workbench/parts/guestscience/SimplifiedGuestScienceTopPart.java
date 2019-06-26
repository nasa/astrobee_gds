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

import gov.nasa.arc.irg.plan.ui.io.WorkbenchConstants;
import gov.nasa.arc.verve.freeflyer.workbench.dialog.StandardMultiHealthAndStatusDialog;
import gov.nasa.rapid.v2.e4.agent.Agent;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class SimplifiedGuestScienceTopPart extends AdvancedGuestScienceTopPart {
	
	@Inject
	public SimplifiedGuestScienceTopPart(Composite parent,
			MApplication application) {
		super(parent, application);
	}
	
	@Override
	protected AdvancedAstrobeeRow makeAstrobeeRow(Composite c, Agent agent, String contextString) {
		return new SimplifiedAstrobeeRow(c, agent, context, contextString);
	}
	
	@Override
	protected void makeDetailsDialog() {
		StandardMultiHealthAndStatusDialog dialog = ContextInjectionFactory.make(
				StandardMultiHealthAndStatusDialog.class, context);
		dialog.create();
		dialog.setBlockOnOpen(false);
		dialog.open();
	}
	
	@Override
	protected int getCellsAcross() {
		return GSSpaceHelper.simpleCellsAcross;
	}
	
	@Override
	protected void setupRowOfTitles(Composite parent) {
		Composite c = new Composite(parent, SWT.None);
		GridLayout glLeft = new GridLayout(GSSpaceHelper.simpleCellsAcross, true);
		glLeft.marginWidth = 0;
		c.setLayout(glLeft);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).grab(true, false).span(GSSpaceHelper.simpleCellsAcross,1).applyTo(c);
		
		Label spacer = new Label(c, SWT.NONE);
		GridData selectorGd = new GridData(SWT.FILL, SWT.TOP, true, false);
		selectorGd.horizontalSpan = GSSpaceHelper.selection_width;
		spacer.setLayoutData(selectorGd);
		spacer.setText("Name");
		spacer.setToolTipText(NAME_TOOLTIP);

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
		controlGd.horizontalSpan = GSSpaceHelper.control_width;
		controlTitle.setLayoutData(controlGd);

		Label apkSummaryTitle = new Label(c, SWT.NONE);
		apkSummaryTitle.setText("Summary");
		apkSummaryTitle.setToolTipText(WorkbenchConstants.SUMMARY_LABEL_TOOLTIP);
		GridData summaryGd = new GridData(SWT.FILL, SWT.TOP, true, false);
		summaryGd.horizontalSpan = GSSpaceHelper.summary_width;
		apkSummaryTitle.setLayoutData(summaryGd);

		Label planTitle = new Label(c, SWT.NONE);
		planTitle.setText("Plan");
		planTitle.setToolTipText(WorkbenchConstants.PLAN_LABEL_TOOLTIP);
		GridData planGd = new GridData(SWT.FILL, SWT.TOP, true, false);
		planGd.horizontalSpan = GSSpaceHelper.plan_width;
		planTitle.setLayoutData(planGd);

		Label planStatusTitle = new Label(c, SWT.NONE);
		planStatusTitle.setText("Plan Status");
		
		planStatusTitle.setToolTipText(WorkbenchConstants.PLAN_STATUS_TOOLTIP);
		GridData planStatusGd = new GridData(SWT.FILL, SWT.TOP, true, false);
		planStatusGd.horizontalSpan = GSSpaceHelper.plan_status_width;
		planStatusTitle.setLayoutData(planStatusGd);
		
	}
}
