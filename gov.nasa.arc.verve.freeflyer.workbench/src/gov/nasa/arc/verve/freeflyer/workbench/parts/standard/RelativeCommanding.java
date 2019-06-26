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

import gov.nasa.arc.irg.freeflyer.rapid.CommandPublisher;
import gov.nasa.arc.irg.freeflyer.rapid.FreeFlyerStrings;
import gov.nasa.arc.irg.freeflyer.rapid.state.AggregateAstrobeeState;
import gov.nasa.arc.irg.freeflyer.rapid.state.AstrobeeStateListener;
import gov.nasa.arc.irg.freeflyer.rapid.state.AstrobeeStateManager;
import gov.nasa.arc.irg.plan.ui.io.WorkbenchConstants;
import gov.nasa.arc.verve.freeflyer.workbench.utils.GuiUtils;
import gov.nasa.arc.verve.freeflyer.workbench.widget.helpers.CommandButton;
import gov.nasa.rapid.idl.ext.astrobee.message.MessageTypeExtAstro;
import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.e4.message.MessageType;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import rapid.ACCESSCONTROL;
import rapid.ACCESSCONTROL_METHOD_REQUESTCONTROL;
import rapid.MOBILITY;
import rapid.MOBILITY_METHOD_STOPALLMOTION;

public class RelativeCommanding implements AstrobeeStateListener {
//	private Logger logger = Logger.getLogger(RelativeCommanding.class);
	protected CommandPublisher commandPublisher;
	private Text poseInputText;
	private CommandButton moveButton, grabControlButton, stopButton;
	private String hintString = "x, y, z, qx, qy, qz, qw\t";
	private String commandButtonLabel = "Move";
	private Agent agent;
	protected AstrobeeStateManager astrobeeStateManager;
	protected String accessControlName = "";
	protected boolean iHaveControl = false;
	protected String myId = Agent.getEgoAgent().name();

	@Inject 
	public RelativeCommanding(MApplication application, Composite parent) {

		Composite inner = new Composite(parent, SWT.NONE);
		GridLayout gl = new GridLayout(5, false);
		gl.marginHeight = 0;
		inner.setLayout(gl);

		createInitializationCompositeOnRelativeTab(inner);

		Label verticalSeparator = new Label(inner, SWT.SEPARATOR | SWT.VERTICAL);
		GridDataFactory.fillDefaults().grab(false, true).applyTo(verticalSeparator);

		createInputCompositeOnRelativeTab(inner);
		
		Label verticalSeparator2 = new Label(inner, SWT.SEPARATOR | SWT.VERTICAL);
		GridDataFactory.fillDefaults().grab(false, true).applyTo(verticalSeparator2);
		
		createCommandsComposite(inner);
	}
	
	protected void createCommandsComposite(Composite parent) {
		Composite innerComposite = GuiUtils.setupInnerComposite(parent, 1, GridData.FILL_HORIZONTAL);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(innerComposite);

		Composite innerInnerComposite = GuiUtils.setupInnerComposite(innerComposite, 1, GridData.FILL_HORIZONTAL);
		Label l = new Label(innerInnerComposite, SWT.None);
		GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.BEGINNING).grab(true,false).applyTo(l);
		l.setText("Commands");

		createStopButton(innerComposite);
		createMoveButton(innerComposite);
		
	}

	protected Composite createInputCompositeOnRelativeTab(Composite parent) {
		Composite innerComposite = GuiUtils.setupInnerComposite(parent, 1, GridData.FILL_HORIZONTAL);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(innerComposite);

		Composite innerInnerComposite = GuiUtils.setupInnerComposite(innerComposite, 1, GridData.FILL_HORIZONTAL);

		Label l = new Label(innerInnerComposite, SWT.None);
		GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.BEGINNING).grab(true, false).applyTo(l);
		l.setText("Input");
		
		poseInputText = new Text(innerInnerComposite, SWT.BORDER);
		poseInputText.setText(hintString);
		poseInputText.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		
		return innerComposite;
	}
	
	protected Composite createInitializationCompositeOnRelativeTab(Composite parent) {
		Composite innerComposite = GuiUtils.setupInnerComposite(parent, 1, GridData.FILL_HORIZONTAL);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(innerComposite);

		Composite innerInnerComposite = GuiUtils.setupInnerComposite(innerComposite, 1, GridData.FILL_HORIZONTAL);

		Label l = new Label(innerInnerComposite, SWT.None);
		GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.BEGINNING).grab(true, false).applyTo(l);
		l.setText("Initialization");

		createGrabControlButton(innerComposite);

		return innerComposite;
	}


	protected void createGrabControlButton(Composite parent) {
		Composite innerComposite = GuiUtils.setupInnerComposite(parent, 1, GridData.FILL_HORIZONTAL);

		grabControlButton = new CommandButton(innerComposite, SWT.NONE);
		grabControlButton.setText("Grab Control");
		grabControlButton.setToolTipText(WorkbenchConstants.GRAB_CONTROL_TOOLTIP);
		grabControlButton.setButtonLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		grabControlButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		grabControlButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				astrobeeStateManager.startRequestingControl();

				commandPublisher.sendGenericNoParamsCommand(
						ACCESSCONTROL_METHOD_REQUESTCONTROL.VALUE,
						ACCESSCONTROL.VALUE);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// no-op
			}
		});
	}

	@Inject
	@Optional
	public void acceptAstrobeeStateManager(AstrobeeStateManager asm) {
		astrobeeStateManager = asm;
		if(agent != null) {
			astrobeeStateManager.addListener(this, MessageType.ACCESSCONTROL_STATE_TYPE);
			astrobeeStateManager.addListener(this, MessageTypeExtAstro.AGENT_STATE_TYPE);
		}
	}


	@Inject @Optional
	public void onAgentSelected(@Named(FreeFlyerStrings.PRIMARY_BEE) Agent a) {
		if(a == null) {
			stopButton.setCompositeEnabled(false);
			return;
		}
		agent = a; // have to do this because we might be the other control panel

		commandPublisher = CommandPublisher.getInstance(agent);
		if(astrobeeStateManager != null) {
			astrobeeStateManager.addListener(this);
		} else {
			System.err.println("DockingPart does not have an AstrobeeStateManager");
		}
	}

	@Override
	public void onAstrobeeStateChange(AggregateAstrobeeState aggregateState) {
		if(grabControlButton == null) {
			return;
		}
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if(agent.name().equals(Agent.SmartDock.name()))
					return;

				accessControlName = aggregateState.getAccessControl();
				
				enableButtonsForAccessControl();
			}
		});
	}

	/** returns false if no more buttons should be enabled. */
	protected boolean enableButtonsForAccessControl() {
		if(accessControlName == null) {
			return false;
		}

		if(!accessControlName.equals(myId)) {
			if(iHaveControl) {
				iHaveControl = false;
			}
			grabControlButton.setCompositeEnabled(true);
			moveButton.setCompositeEnabled(false);
			stopButton.setCompositeEnabled(false);
			return false;
		} else {
			if(!iHaveControl) {
				iHaveControl = true;
			}
			grabControlButton.setCompositeEnabled(false);
			stopButton.setCompositeEnabled(true);
			moveButton.setCompositeEnabled(true);
			return true;
		}
	}

	protected void createMoveButton(Composite parent) {
		moveButton = new CommandButton(parent, SWT.NONE);
		moveButton.setText(commandButtonLabel);
		moveButton.setButtonLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		moveButton.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
	
		moveButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String raw = poseInputText.getText();
				String tokens[] = raw.split(",");
				double x = Double.valueOf(tokens[0]);
				double y = Double.valueOf(tokens[1]);
				double z = Double.valueOf(tokens[2]);
				float qx = Float.valueOf(tokens[3]);
				float qy = Float.valueOf(tokens[4]);
				float qz = Float.valueOf(tokens[5]);
				float qw = Float.valueOf(tokens[6]);
				commandPublisher.sendRelative(x, y, z, qx, qy, qz, qw);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)  {/**/}
		});
	}

	protected void createStopButton(Composite parent) {
		Composite innerComposite = GuiUtils.setupInnerComposite(parent, 1, GridData.FILL_HORIZONTAL);
		stopButton = new CommandButton(innerComposite, SWT.NONE);
		stopButton.setText(WorkbenchConstants.STOP_BUTTON_TEXT);
		stopButton.setToolTipText(WorkbenchConstants.STOP_TOOLTIP);
		stopButton.setButtonLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		stopButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		stopButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				commandPublisher.sendGenericNoParamsCommand(
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
