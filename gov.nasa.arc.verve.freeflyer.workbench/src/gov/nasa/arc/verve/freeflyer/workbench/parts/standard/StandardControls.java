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
import gov.nasa.arc.irg.freeflyer.rapid.state.AggregateAstrobeeState;
import gov.nasa.arc.irg.freeflyer.rapid.state.AstrobeeStateListener;
import gov.nasa.arc.irg.freeflyer.rapid.state.AstrobeeStateManager;
import gov.nasa.arc.verve.freeflyer.workbench.helpers.SelectedAgentConnectedListener;
import gov.nasa.arc.verve.freeflyer.workbench.helpers.SelectedAgentConnectedRegistry;
import gov.nasa.arc.verve.freeflyer.workbench.utils.GuiUtils;
import gov.nasa.arc.verve.freeflyer.workbench.widget.helpers.CommandButton;
import gov.nasa.rapid.idl.ext.astrobee.message.MessageTypeExtAstro;
import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.e4.message.MessageType;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import rapid.ACCESSCONTROL;
import rapid.ACCESSCONTROL_METHOD_REQUESTCONTROL;
import rapid.MOBILITY_METHOD_STOPALLMOTION;
import rapid.ext.astrobee.MOBILITY;
import rapid.ext.astrobee.MOBILITY_METHOD_IDLE_PROPULSION;

public class StandardControls implements AstrobeeStateListener, SelectedAgentConnectedListener {
	protected Agent agent = null;
	protected boolean agentValid = false;
	protected CommandButton idlePropulsionButton;
	protected CommandButton grabControlButton, sendZonesButton;
	protected CommandPublisher commandPublisher;
	private Label agentNameLabel;
	private String satelliteControlString = "Standard Controls for:";
	private String idlePropulsionString = "Idle Propulsion ";
	private String grabControlString = "Grab Control";
	protected boolean astrobeeSelected = false;
	
	protected String stationKeepString = "Station Keep";
	protected CommandButton stationKeepButton;

	protected String myId;
	protected AstrobeeStateManager astrobeeStateManager;
	protected int wholeWidth = 2;
	
	public StandardControls(Composite parent, AstrobeeStateManager astrobeeStateManager) {
		this.astrobeeStateManager = astrobeeStateManager;
		GridLayout gridLayout = new GridLayout(wholeWidth, false);
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		parent.setLayout(gridLayout);

		myId = Agent.getEgoAgent().name();

		GridData wholeWidthWide = new GridData(SWT.FILL, SWT.TOP, true, false);
		wholeWidthWide.horizontalSpan = wholeWidth;
		parent.setLayoutData(wholeWidthWide);

		GuiUtils.makeHorizontalSeparator(parent, wholeWidth);
		createAgentNameLabel(parent);

		makeTopCommandButtons(parent);
		GuiUtils.makeHorizontalSeparator(parent, wholeWidth);
		SelectedAgentConnectedRegistry.addListener(this);
	}

	private void createAgentNameLabel(Composite parent) {
		agentNameLabel = new Label(parent, SWT.None);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = wholeWidth;
		agentNameLabel.setLayoutData(data);
		agentNameLabel.setText(satelliteControlString);
//		Font bigFont = GuiUtils.makeBigFont(parent, agentNameLabel);
//		agentNameLabel.setFont(bigFont);
	}

	private void makeTopCommandButtons(Composite parent) {
		makeGrabControlButton(parent);
		makeSendZonesButton(parent);
		makeStationKeepButton(parent);
		makeIdlePropulsionButton(parent);
	}
	
	protected void makeStationKeepButton(Composite parent) {
		stationKeepButton = new CommandButton(parent, SWT.NONE);
		stationKeepButton.setText(stationKeepString);
		stationKeepButton.setButtonLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		stationKeepButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		stationKeepButton.addSelectionListener(new SelectionListener() {
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
	
	private void makeSendZonesButton(Composite parent) {
		sendZonesButton = new CommandButton(parent, SWT.NONE);
		sendZonesButton.setText("Send Zones");
		sendZonesButton.setButtonLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		sendZonesButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		sendZonesButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				astrobeeStateManager.startSendingZones();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// no-op
			}
		});
	}
	
	private void makeGrabControlButton(Composite parent) {
		grabControlButton = new CommandButton(parent, SWT.NONE);
		grabControlButton.setText(grabControlString);
		grabControlButton.setButtonLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		grabControlButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
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
	
	private void makeIdlePropulsionButton(Composite parent) {
		idlePropulsionButton = new CommandButton(parent, SWT.NONE);
		idlePropulsionButton.setText(idlePropulsionString);
		idlePropulsionButton.setButtonLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		idlePropulsionButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		idlePropulsionButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
					commandPublisher.sendGenericNoParamsCommand(
							MOBILITY_METHOD_IDLE_PROPULSION.VALUE,
							MOBILITY.VALUE);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// no-op
			}
		});
	}

	public void onAgentSelected(Agent a) {
		// TODO: somehow set the selection of the other partitionsCombo to the right thing
		//partitionsCombo.set
		agent = a; // have to do this because we might be the other control panel
		astrobeeSelected = true;
		if(a != null) {
			agentValid = true;
		}
		if(agentNameLabel != null) {
			agentNameLabel.setText( satelliteControlString + " " + agent.name() );
		}
		commandPublisher = CommandPublisher.getInstance(agent);
		if(astrobeeStateManager != null) {
			astrobeeStateManager.addListener(this, MessageType.ACCESSCONTROL_STATE_TYPE);
			astrobeeStateManager.addListener(this, MessageTypeExtAstro.AGENT_STATE_TYPE);
		} else {
			System.err.println("StandardControls does not have an AstrobeeStateManager");
		}
		if(idlePropulsionButton != null) {
			idlePropulsionButton.setEnabled(true);
			stationKeepButton.setEnabled(true);
		}
	}
	
	public void acceptAstrobeeStateManager(AstrobeeStateManager manager) {
		astrobeeStateManager = manager;
		astrobeeStateManager.addListener(this, MessageType.ACCESSCONTROL_STATE_TYPE);
		astrobeeStateManager.addListener(this, MessageTypeExtAstro.AGENT_STATE_TYPE);
	}
	
	public void deregister() {
		astrobeeStateManager.removeListener(this);
	}

	public void onAstrobeeStateChange(AggregateAstrobeeState aggregateState) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if(grabControlButton == null || grabControlButton.isDisposed()) {
					return;
				}
				if(aggregateState.getAccessControl() == null || !astrobeeSelected) {
					return;
				}
				if(aggregateState.getAccessControl().equals(myId)) {
					grabControlButton.setCompositeEnabled(false);
					sendZonesButton.setCompositeEnabled(true);
				} else {
					grabControlButton.setCompositeEnabled(true);
					sendZonesButton.setCompositeEnabled(false);
					idlePropulsionButton.setCompositeEnabled(false);
					return;
				}

				if(aggregateState.getAstrobeeState() == null || aggregateState.getAstrobeeState().getOperatingState() == null) {
					return;
				}
				switch(aggregateState.getAstrobeeState().getOperatingState()) {
				case TELEOPERATION:
				case PLAN_EXECUTION:
				case AUTO_RETURN:
				case READY:
				case FAULT:
					idlePropulsionButton.setCompositeEnabled(true);
					break;
				}
				
				switch(aggregateState.getAstrobeeState().getMobilityState()) {
				case FLYING:
				case DRIFTING:
				case STOPPING:
					stationKeepButton.setCompositeEnabled(true);
					break;
				case PERCHING:
				case DOCKING:
					if(aggregateState.getAstrobeeState().getSubMobilityState() == 0) {
						// perched or docked
						stationKeepButton.setCompositeEnabled(false);
					} else {
						// still in the process of perching or docking
						stationKeepButton.setCompositeEnabled(true);
					}
					break;
				default:
					stationKeepButton.setCompositeEnabled(false);
					break;
				}
			}
		});
	}

	public void onSelectedAgentConnected() {
		// TODO Auto-generated method stub
		
	}

	public void onSelectedAgentDisconnected() {
		astrobeeSelected = false;
//		grabControlButton.setCompositeEnabled(false);
	}
}
