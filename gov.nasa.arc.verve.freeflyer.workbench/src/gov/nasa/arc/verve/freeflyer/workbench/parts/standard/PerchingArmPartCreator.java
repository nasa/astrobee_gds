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
import gov.nasa.arc.verve.freeflyer.workbench.widget.helpers.IncrementableTextHorizontalInt;
import gov.nasa.arc.verve.freeflyer.workbench.widget.helpers.IncrementableTextHorizontalIntBackward;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import rapid.ACCESSCONTROL;
import rapid.ACCESSCONTROL_METHOD_REQUESTCONTROL;
import rapid.MOBILITY_METHOD_STOPALLMOTION;
import rapid.ext.astrobee.ADMIN;
import rapid.ext.astrobee.ADMIN_METHOD_REACQUIRE_POSITION;
import rapid.ext.astrobee.MOBILITY;
import rapid.ext.astrobee.MOBILITY_METHOD_PERCH;
import rapid.ext.astrobee.MOBILITY_METHOD_UNPERCH;

public class PerchingArmPartCreator {
	protected PerchingArmPart perchingArmPart;
	protected final double MIN_PAN = -90, MAX_PAN = 90, MIN_TILT = -20, MAX_TILT = 90;
	protected final double PAN_AND_TILT_INCREMENT = 10;
	protected final double INITIAL_TILT = 0;
	
	private final String REACQUIRE_POSITION_TOOLTIP = "Restart Astrobee Localization";
	private final String PAN_AND_TILT_TOOLTIP = "Move Arm to Configuration Specified in Manual Inputs";
	private final String PAN_INPUT_TOOLTIP = "Absolute Pan Angle";
	private final String TILT_INPUT_TOOLTIP = "Absolute Tilt Angle";
	
	protected void createArmCommandsTab(PerchingArmPart perchingArmPart, Composite parent) {
		this.perchingArmPart = perchingArmPart;

		Composite armTab = new Composite(parent, SWT.NONE);
		GridLayout gl = new GridLayout(7, false);
		gl.marginHeight = 0;
		armTab.setLayout(gl);

		createInitializationCompositeOnArmTab(armTab);

		Label verticalSeparator = new Label(armTab, SWT.SEPARATOR | SWT.VERTICAL);
		GridDataFactory.fillDefaults().grab(false, true).applyTo(verticalSeparator);

		createArmManualInputsComposite(armTab);
		
		Label verticalSeparator2 = new Label(armTab, SWT.SEPARATOR | SWT.VERTICAL);
		GridDataFactory.fillDefaults().grab(false, true).applyTo(verticalSeparator2);

		createArmOptionsComposite(armTab);

		Label verticalSeparator3 = new Label(armTab, SWT.SEPARATOR | SWT.VERTICAL);
		GridDataFactory.fillDefaults().grab(false, true).applyTo(verticalSeparator3);

		createArmCommandsComposite(armTab);
	}

	protected Composite createInitializationCompositeOnArmTab(Composite parent) {
		Composite innerComposite = GuiUtils.setupInnerComposite(parent, 1, GridData.FILL_HORIZONTAL);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(innerComposite);

		Composite innerInnerComposite = GuiUtils.setupInnerComposite(innerComposite, 1, GridData.FILL_HORIZONTAL);

		Label l = new Label(innerInnerComposite, SWT.None);
		GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.BEGINNING).grab(true, false).applyTo(l);
		l.setText("Initialization");

		createGrabControlButtonOnArmTab(innerComposite);

		return innerComposite;
	}

	protected void createArmManualInputsComposite(Composite parent) {
		Composite innerComposite = GuiUtils.setupInnerComposite(parent, 3, GridData.FILL_HORIZONTAL);
		GridDataFactory.fillDefaults().grab(false, false).applyTo(innerComposite);
		
		Composite innerInnerComposite = GuiUtils.setupInnerComposite(innerComposite, 1, GridData.FILL_HORIZONTAL);
		GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.BEGINNING).grab(true, false).span(3,1).applyTo(innerInnerComposite);

		Label title = new Label(innerInnerComposite, SWT.None);
		title.setText("Pan and Tilt (Degrees)");
		GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.BEGINNING).grab(true, false).span(3,1).applyTo(title);

		createPanRow(innerComposite);
		createTiltRow(innerComposite);
		createPanAndTiltButton(innerComposite);
	}

	protected void createArmOptionsComposite(Composite parent) {
		Composite innerComposite = GuiUtils.setupInnerComposite(parent, 1, GridData.FILL_HORIZONTAL);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(innerComposite);
		
		Composite innerInnerComposite = GuiUtils.setupInnerComposite(innerComposite, 1, GridData.FILL_HORIZONTAL);

		Label l = new Label(innerInnerComposite, SWT.None);
		GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.BEGINNING).grab(true, false).applyTo(l);
		l.setText("Options");

		createReacquirePositionButton(innerComposite);
	}

	protected void createArmCommandsComposite(Composite parent) {
		Composite innerComposite = GuiUtils.setupInnerComposite(parent, 1, GridData.FILL_HORIZONTAL);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(innerComposite);
		
		Composite innerInnerComposite = GuiUtils.setupInnerComposite(innerComposite, 1, GridData.FILL_HORIZONTAL);

		Label l = new Label(innerInnerComposite, SWT.None);
		GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.BEGINNING).grab(true, false).applyTo(l);
		l.setText("Commands");

		createStopButtonOnArmTab(innerComposite);
		createPerchButton(innerComposite);
	}
	
	protected void createReacquirePositionButton(Composite parent) {
		Composite innerComposite = GuiUtils.setupInnerComposite(parent, 1, GridData.FILL_HORIZONTAL);

		perchingArmPart.reacquirePositionButton = new CommandButton(innerComposite, SWT.NONE);
		perchingArmPart.reacquirePositionButton.setText("Reacquire Position");
		perchingArmPart.reacquirePositionButton.setToolTipText(REACQUIRE_POSITION_TOOLTIP);
		perchingArmPart.reacquirePositionButton.setButtonLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		perchingArmPart.reacquirePositionButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		perchingArmPart.reacquirePositionButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				perchingArmPart.commandPublisher.sendGenericNoParamsCommand(
						ADMIN_METHOD_REACQUIRE_POSITION.VALUE,
						ADMIN.VALUE);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// no-op
			}
		});
		perchingArmPart.reacquirePositionButton.setEnabled(false);
	}
	
	protected void createGrabControlButtonOnArmTab(Composite parent) {
		Composite innerComposite = GuiUtils.setupInnerComposite(parent, 1, GridData.FILL_HORIZONTAL);

		perchingArmPart.grabControlButtonOnArmTab = new CommandButton(innerComposite, SWT.NONE);
		perchingArmPart.grabControlButtonOnArmTab.setText("Grab Control");
		perchingArmPart.grabControlButtonOnArmTab.setToolTipText(WorkbenchConstants.GRAB_CONTROL_TOOLTIP);
		perchingArmPart.grabControlButtonOnArmTab.setButtonLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		perchingArmPart.grabControlButtonOnArmTab.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		perchingArmPart.grabControlButtonOnArmTab.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				perchingArmPart.astrobeeStateManager.startRequestingControl();

				perchingArmPart.commandPublisher.sendGenericNoParamsCommand(
						ACCESSCONTROL_METHOD_REQUESTCONTROL.VALUE,
						ACCESSCONTROL.VALUE);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// no-op
			}
		});
	}

	protected void createPerchButton(Composite parent) {
		Composite innerComposite = GuiUtils.setupInnerComposite(parent, 1, GridData.FILL_HORIZONTAL);

		perchingArmPart.perchButton = new CommandButton(innerComposite, SWT.None);
		perchingArmPart.perchButton.setButtonLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		perchingArmPart.perchButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		perchingArmPart.perchButton.setText(perchingArmPart.PERCH_STRING);
		perchingArmPart.perchButton.setToolTipText(perchingArmPart.PERCH_TOOLTIP);
		perchingArmPart.perchButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(perchingArmPart.perchButton.toString().equals(perchingArmPart.PERCH_STRING)) {
					perchingArmPart.commandPublisher.sendGenericNoParamsCommand(
							MOBILITY_METHOD_PERCH.VALUE,
							MOBILITY.VALUE);
				} else {
					perchingArmPart.commandPublisher.sendGenericNoParamsCommand(
							MOBILITY_METHOD_UNPERCH.VALUE,
							MOBILITY.VALUE);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)  {/**/}
		});
	}

	protected void createStopButtonOnArmTab(Composite parent) {
		Composite innerComposite = GuiUtils.setupInnerComposite(parent, 1, GridData.FILL_HORIZONTAL);

		perchingArmPart.stopButtonOnArmTab = new CommandButton(innerComposite, SWT.NONE);
		perchingArmPart.stopButtonOnArmTab.setText(WorkbenchConstants.STOP_BUTTON_TEXT);
		perchingArmPart.stopButtonOnArmTab.setToolTipText(WorkbenchConstants.STOP_TOOLTIP);
		perchingArmPart.stopButtonOnArmTab.setButtonLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		perchingArmPart.stopButtonOnArmTab.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		perchingArmPart.stopButtonOnArmTab.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				perchingArmPart.commandPublisher.sendGenericNoParamsCommand(
						MOBILITY_METHOD_STOPALLMOTION.VALUE,
						MOBILITY.VALUE);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// no-op
			}
		});
	}

	protected void createPanRow(Composite parent) {
		Composite outerInnerComposite = GuiUtils.setupInnerComposite(parent, 1, GridData.FILL_HORIZONTAL);
		GridDataFactory.fillDefaults().grab(true, false).span(3,1).applyTo(outerInnerComposite);
		
		Composite innerComposite = GuiUtils.setupInnerCompositeEvenSpacing(outerInnerComposite, 3, GridData.FILL_HORIZONTAL);
		
		Label panLabel = new Label(innerComposite, SWT.None);
		GridDataFactory.fillDefaults().align(GridData.END, GridData.CENTER).grab(true, false).span(1,1).applyTo(panLabel);
		panLabel.setText("Left");
		perchingArmPart.panInput = new IncrementableTextHorizontalIntBackward(innerComposite, 0, PAN_AND_TILT_INCREMENT);
		perchingArmPart.panInput.setAllowableRange(MIN_PAN, MAX_PAN);
		perchingArmPart.panInput.setToolTipText(PAN_INPUT_TOOLTIP);
		perchingArmPart.panInput.setArrowToolTipText("deg");
		GridData gd = new GridData(SWT.CENTER, SWT.CENTER, true, true);
		perchingArmPart.panInput.setLayoutData(gd);
		
		Label panUnits = new Label(innerComposite, SWT.NONE);
		GridDataFactory.fillDefaults().align(GridData.BEGINNING, GridData.CENTER).grab(true, false).span(1,1).applyTo(panUnits);
		panUnits.setText("Right");
	}

	protected void createTiltRow(Composite parent) {
		Composite innerComposite = GuiUtils.setupInnerCompositeEvenSpacing(parent, 3, GridData.FILL_HORIZONTAL);
		GridDataFactory.fillDefaults().grab(true, false).span(3,1).applyTo(innerComposite);

		Label tiltLabel = new Label(innerComposite, SWT.None);
		GridDataFactory.fillDefaults().align(GridData.END, GridData.CENTER).grab(true, false).span(1,1).applyTo(tiltLabel);
		tiltLabel.setText("Down");
		perchingArmPart.tiltInput = new IncrementableTextHorizontalInt(innerComposite, INITIAL_TILT, PAN_AND_TILT_INCREMENT);
		perchingArmPart.tiltInput.setAllowableRange(MIN_TILT, MAX_TILT);
		perchingArmPart.tiltInput.setToolTipText(TILT_INPUT_TOOLTIP);
		perchingArmPart.tiltInput.setArrowToolTipText("deg");
		GridData gd = new GridData(SWT.CENTER, SWT.CENTER, true, true);
		perchingArmPart.tiltInput.setLayoutData(gd);

		Label tiltUnits = new Label(innerComposite, SWT.NONE);
		GridDataFactory.fillDefaults().align(GridData.BEGINNING, GridData.CENTER).grab(true, false).span(1,1).applyTo(tiltUnits);
		tiltUnits.setText("Up");
	}
	
	protected void createPanAndTiltButton(Composite parent) {
		Composite innerComposite = GuiUtils.setupInnerCompositeEvenSpacing(parent, 1, GridData.FILL_HORIZONTAL);
		GridDataFactory.fillDefaults().grab(true, false).span(1,1).applyTo(innerComposite);
		
		perchingArmPart.panAndTiltButton = new CommandButton(innerComposite, SWT.None);
		perchingArmPart.panAndTiltButton.setButtonLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		perchingArmPart.panAndTiltButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		perchingArmPart.panAndTiltButton.setText("Pan and Tilt");
		perchingArmPart.panAndTiltButton.setToolTipText(PAN_AND_TILT_TOOLTIP);
		perchingArmPart.panAndTiltButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				double pan = perchingArmPart.panInput.getNumber();
				double tilt = perchingArmPart.tiltInput.getNumber();

				perchingArmPart.commandPublisher.sendPanAndTiltCommand(pan, tilt);
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e)  {/**/}
		});
	}
}
