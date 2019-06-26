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
package gov.nasa.arc.verve.freeflyer.workbench.parts.standard;

import gov.nasa.arc.irg.plan.ui.io.WorkbenchConstants;
import gov.nasa.arc.verve.freeflyer.workbench.utils.GuiUtils;
import gov.nasa.arc.verve.freeflyer.workbench.widget.helpers.CommandButton;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import rapid.ACCESSCONTROL;
import rapid.ACCESSCONTROL_METHOD_REQUESTCONTROL;
import rapid.MOBILITY_METHOD_STOPALLMOTION;
import rapid.ext.astrobee.MOBILITY;
import rapid.ext.astrobee.MOBILITY_METHOD_AUTO_RETURN;
import rapid.ext.astrobee.MOBILITY_METHOD_UNDOCK;
import rapid.ext.astrobee.SETTINGS;
import rapid.ext.astrobee.SETTINGS_METHOD_SET_ENABLE_AUTO_RETURN;
import rapid.ext.astrobee.SETTINGS_METHOD_SET_ENABLE_AUTO_RETURN_PARAM_ENABLE_AUTO_RETURN;

public class DockingPartCreator {
	protected DockingPart dockingPart;
	protected final String BERTH_1_NAME = "Berth 1";
	protected final String BERTH_2_NAME = "Berth 2";

	private final String ENABLE_AUTO_RETURN_TOOLTIP = "Allow Astrobee to Return to Docking Station Automatically When Battery Runs Low";
	private final String DOCK_AUTOMATICALLY_TOOLTIP = "Send Astrobee to Docking Station from Anywhere in ISS";
	private final String APPLY_OPTIONS_TOOLTIP = "Send Selected Options to Astrobee";

	protected void createDockCommandsTab(DockingPart dockingPart, Composite parent) {
		this.dockingPart = dockingPart;

		Composite dockTab = new Composite(parent, SWT.NONE);
		GridLayout gl = new GridLayout(7, false);
		gl.marginHeight = 0;
		dockTab.setLayout(gl);

		createInitializationCompositeOnDockTab(dockTab);

		Label verticalSeparator2 = new Label(dockTab, SWT.SEPARATOR | SWT.VERTICAL);
		GridDataFactory.fillDefaults().grab(false, true).applyTo(verticalSeparator2);

		createDockOptionsComposite(dockTab);

		Label verticalSeparator3 = new Label(dockTab, SWT.SEPARATOR | SWT.VERTICAL);
		GridDataFactory.fillDefaults().grab(false, true).applyTo(verticalSeparator3);

		createDockCommandsComposite(dockTab);
	}

	protected Composite createInitializationCompositeOnDockTab(Composite parent) {
		Composite innerComposite = GuiUtils.setupInnerComposite(parent, 1, GridData.HORIZONTAL_ALIGN_BEGINNING);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(innerComposite);

		Composite innerInnerComposite = GuiUtils.setupInnerComposite(innerComposite, 1, GridData.FILL_HORIZONTAL);

		Label l = new Label(innerInnerComposite, SWT.None);
		GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.BEGINNING).grab(true, false).applyTo(l);
		l.setText("Initialization");

		createGrabControlButtonOnDockTab(innerComposite);

		return innerComposite;
	}

	protected void createDockOptionsComposite(Composite parent) {
		Composite innerComposite = GuiUtils.setupInnerComposite(parent, 2, GridData.HORIZONTAL_ALIGN_BEGINNING);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(innerComposite);
		
		Composite innerInnerComposite = GuiUtils.setupInnerComposite(innerComposite, 1, GridData.FILL_HORIZONTAL);
		GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.BEGINNING).grab(true, false).span(2,1).applyTo(innerInnerComposite);
		
		Label l = new Label(innerInnerComposite, SWT.None);
		GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.BEGINNING).grab(true, false).applyTo(l);
		l.setText("Options");

		createCheckboxesColumn(innerComposite);
		createCheckmarksColumn(innerComposite);
		
		createApplyOptionsButton(innerComposite);
	}

	protected void createDockCommandsComposite(Composite parent) {
		Composite innerComposite = GuiUtils.setupInnerComposite(parent, 1, GridData.HORIZONTAL_ALIGN_BEGINNING);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(innerComposite);

		Composite innerInnerComposite = GuiUtils.setupInnerComposite(innerComposite, 1, GridData.FILL_HORIZONTAL);

		Label l = new Label(innerInnerComposite, SWT.None);
		GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.BEGINNING).grab(true, false).applyTo(l);
		l.setText("Commands");

		createStopButtonOnDockTab(innerComposite);
		createDockAutomaticallyButton(innerComposite);
	}

	protected void createGrabControlButtonOnDockTab(Composite parent) {
		Composite innerComposite = GuiUtils.setupInnerComposite(parent, 1, GridData.FILL_HORIZONTAL);

		dockingPart.grabControlButtonOnDockTab = new CommandButton(innerComposite, SWT.NONE);
		dockingPart.grabControlButtonOnDockTab.setText("Grab Control");
		dockingPart.grabControlButtonOnDockTab.setToolTipText(WorkbenchConstants.GRAB_CONTROL_TOOLTIP);
		dockingPart.grabControlButtonOnDockTab.setButtonLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		dockingPart.grabControlButtonOnDockTab.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		dockingPart.grabControlButtonOnDockTab.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dockingPart.astrobeeStateManager.startRequestingControl();

				dockingPart.commandPublisher.sendGenericNoParamsCommand(
						ACCESSCONTROL_METHOD_REQUESTCONTROL.VALUE,
						ACCESSCONTROL.VALUE);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// no-op
			}
		});
	}

	protected void createCheckboxesColumn(Composite parent) {
		Composite innerComposite = GuiUtils.setupInnerComposite(parent, 1, GridData.FILL_HORIZONTAL);
		createEnableAutoReturnButton(innerComposite);
	}

	protected void createEnableAutoReturnButton(Composite parent) {
		Composite innerComposite = GuiUtils.setupInnerComposite(parent, 1, GridData.FILL_HORIZONTAL);

		dockingPart.enableAutoReturnButton = new Button(innerComposite, SWT.CHECK);
		dockingPart.enableAutoReturnButton.setSelection(true);
		dockingPart.enableAutoReturnButton.setText("Automatically Return to Docking Station when Battery Low");
		dockingPart.enableAutoReturnButton.setToolTipText(ENABLE_AUTO_RETURN_TOOLTIP);
		dockingPart.enableAutoReturnButton.setLayoutData(new GridData(SWT.END, SWT.FILL, true, true));
	}

	protected void createCheckmarksColumn(Composite parent) {
		Composite innerComposite = GuiUtils.setupInnerComposite(parent, 1, GridData.FILL_HORIZONTAL);
		
		Composite innerInnerComposite = GuiUtils.setupInnerComposite(innerComposite, 1, GridData.FILL_BOTH);

		dockingPart.enableAutoReturnCheckmark = new Label(innerInnerComposite, SWT.None);
		dockingPart.enableAutoReturnCheckmark.setImage(dockingPart.unknownCheckedImage);
		GridDataFactory.fillDefaults().grab(true, false).align(SWT.BEGINNING, SWT.BEGINNING).applyTo(dockingPart.enableAutoReturnCheckmark);
	}

	protected void createDockAutomaticallyButton(Composite parent) {
		Composite innerComposite = GuiUtils.setupInnerComposite(parent, 1, GridData.FILL_HORIZONTAL);

		dockingPart.dockAutomaticallyButton = new CommandButton(innerComposite, SWT.NONE);
		dockingPart.dockAutomaticallyButton.setText(dockingPart.DOCK_AUTO);
		dockingPart.dockAutomaticallyButton.setToolTipText(DOCK_AUTOMATICALLY_TOOLTIP);
		dockingPart.dockAutomaticallyButton.setButtonLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		dockingPart.dockAutomaticallyButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		dockingPart.dockAutomaticallyButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(dockingPart.dockAutomaticallyButton.getText().equals(dockingPart.DOCK_AUTO))
					dockingPart.commandPublisher.sendGenericNoParamsCommand(
							MOBILITY_METHOD_AUTO_RETURN.VALUE,
							MOBILITY.VALUE);
				else
					dockingPart.commandPublisher.sendGenericNoParamsCommand(
							MOBILITY_METHOD_UNDOCK.VALUE,
							MOBILITY.VALUE);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// no-op
			}
		});
	}

	protected void createApplyOptionsButton(Composite parent) {
		Composite innerComposite = GuiUtils.setupInnerComposite(parent, 2, GridData.FILL_HORIZONTAL);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).grab(true, false).span(2,1).applyTo(innerComposite);

		dockingPart.applyOptionsOnDockTab = new CommandButton(innerComposite, SWT.NONE);
		dockingPart.applyOptionsOnDockTab.setText("Apply Option");
		dockingPart.applyOptionsOnDockTab.setToolTipText(APPLY_OPTIONS_TOOLTIP);
		dockingPart.applyOptionsOnDockTab.setButtonLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
		dockingPart.applyOptionsOnDockTab.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
		dockingPart.applyOptionsOnDockTab.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(dockingPart.enableAutoReturnButton.getSelection()) {
					dockingPart.commandPublisher.sendGenericBooleanCommand(
							SETTINGS_METHOD_SET_ENABLE_AUTO_RETURN.VALUE, 
							SETTINGS.VALUE, 
							SETTINGS_METHOD_SET_ENABLE_AUTO_RETURN_PARAM_ENABLE_AUTO_RETURN.VALUE,
							true);
				} else {
					dockingPart.commandPublisher.sendGenericBooleanCommand(
							SETTINGS_METHOD_SET_ENABLE_AUTO_RETURN.VALUE, 
							SETTINGS.VALUE, 
							SETTINGS_METHOD_SET_ENABLE_AUTO_RETURN_PARAM_ENABLE_AUTO_RETURN.VALUE,
							false);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// no-op
			}
		});
		dockingPart.applyOptionsOnDockTab.setCompositeEnabled(false);
	}

	protected void createStopButtonOnDockTab(Composite parent) {
		Composite innerComposite = GuiUtils.setupInnerComposite(parent, 1, GridData.FILL_HORIZONTAL);

		dockingPart.stopButtonOnDockTab = new CommandButton(innerComposite, SWT.NONE);
		dockingPart.stopButtonOnDockTab.setText(WorkbenchConstants.STOP_BUTTON_TEXT);
		dockingPart.stopButtonOnDockTab.setToolTipText(WorkbenchConstants.STOP_TOOLTIP);
		dockingPart.stopButtonOnDockTab.setButtonLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		dockingPart.stopButtonOnDockTab.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		dockingPart.stopButtonOnDockTab.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dockingPart.commandPublisher.sendGenericNoParamsCommand(
						MOBILITY_METHOD_STOPALLMOTION.VALUE,
						MOBILITY.VALUE);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// no-op
			}
		});
	}
}
