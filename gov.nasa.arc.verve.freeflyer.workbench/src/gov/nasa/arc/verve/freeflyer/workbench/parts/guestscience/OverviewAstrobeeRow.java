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
import gov.nasa.rapid.v2.e4.agent.Agent;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class OverviewAstrobeeRow extends SimplifiedAstrobeeRow {

	protected Label titleLabel;
	
	public OverviewAstrobeeRow(Composite parent, Agent agent,
			IEclipseContext context, String contextString) {
		super(parent, agent, context, contextString);
	}
	
	@Override
	protected int getCellsAcross() {
		return GSSpaceHelper.overviewCellsAcross;
	}

	@Override
	protected void createComposite(Composite parent) {
		createTitleLabel(parent);
		
		createCommLed(parent);
		createHealthLed(parent);
		
		createBattLabel(parent);
		createControlLabel(parent);
	}
	
	protected void createTitleLabel(Composite parent) {
		titleLabel = new Label(parent, SWT.NONE);
		titleLabel.setText(agent.name());
		GridData selectorGd = new GridData(SWT.FILL, SWT.TOP, true, false);
		selectorGd.horizontalSpan = GSSpaceHelper.title_width;
		titleLabel.setLayoutData(selectorGd);
	}
	
	@Override
	protected void createControlLabel(Composite parent) {
		controlLabel = new Label(parent, SWT.NONE);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).grab(true, false).span(GSSpaceHelper.control_width_for_summary,1).applyTo(controlLabel);
		controlLabel.setBackground(colorCyan);
		controlLabel.setToolTipText(WorkbenchConstants.CONTROL_TOOLTIP);
	}
	
	public void onGuestScienceStateChange(GuestScienceStateManager manager) {
		this.manager = manager;
	}
	
}
