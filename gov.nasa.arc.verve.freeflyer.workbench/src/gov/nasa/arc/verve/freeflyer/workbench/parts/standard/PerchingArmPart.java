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
import gov.nasa.arc.irg.freeflyer.rapid.state.AstrobeeStateGds;
import gov.nasa.arc.irg.freeflyer.rapid.state.AstrobeeStateListener;
import gov.nasa.arc.irg.freeflyer.rapid.state.AstrobeeStateManager;
import gov.nasa.arc.irg.plan.ui.io.WorkbenchConstants;
import gov.nasa.arc.verve.freeflyer.workbench.scenario.FreeFlyerScenario;
import gov.nasa.arc.verve.freeflyer.workbench.utils.Berth;
import gov.nasa.arc.verve.freeflyer.workbench.widget.helpers.CommandButton;
import gov.nasa.arc.verve.freeflyer.workbench.widget.helpers.IncrementableTextHorizontalInt;
import gov.nasa.rapid.idl.ext.astrobee.message.MessageTypeExtAstro;
import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.e4.message.MessageType;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Vector;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.databinding.Binding;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import com.ardor3d.math.Vector3;

public class PerchingArmPart implements AstrobeeStateListener {
	protected CommandButton grabControlButtonOnArmTab;
	protected String accessControlName = "";

	protected CommandPublisher commandPublisher;
	protected Agent agent = null;
	protected boolean agentValid = false;
	protected String myId = Agent.getEgoAgent().name();
	protected AstrobeeStateManager astrobeeStateManager;
	protected Berth selectedBerth = null;
	
	protected final String PERCH_STRING = " Perch ";
	protected final String UNPERCH_STRING = "Unperch";
	final String PERCH_TOOLTIP = "Deploy Arm and Grasp Aligned Handrail";
	private final String UNPERCH_TOOLTIP = "Release Handrail and Stow Arm";

	protected final String LONG_BLANK_STRING = "\t\t";
	protected final String OPEN_GRIPPER_STRING = "Open Gripper";
	protected final String CLOSE_GRIPPER_STRING = "Close Gripper";
	protected final String BLANK_STRING = " ";
	protected final double RAD_TO_DEG = 180.0 / Math.PI;

	protected DecimalFormat transFormatter = new DecimalFormat("#.#");
	protected DecimalFormat rotFormatter = new DecimalFormat("#");
	protected Vector3 dragTranslationVec = new Vector3(0,0,0);
	protected Vector3 dragRotationVec = new Vector3(0,0,0);

	protected boolean onTop = false; // if Teleoperation tab is selected
	private boolean beeIsSafeStopped = false;
	private boolean justSwitchedRobots = false; // if we just switch selected robots

	private boolean beeMobilityStateCanMove = false;
	
	@Inject
	FreeFlyerScenario freeFlyerScenario;
	
	protected CommandButton stopButtonOnArmTab;
	
	protected CommandButton perchButton, panAndTiltButton, reacquirePositionButton;

	protected IncrementableTextHorizontalInt panInput, tiltInput;

	IEclipseContext savedContext;

	protected boolean mainTabIsOnTop = true;
	private AstrobeeStateGds.MobilityState currentMobilityState = AstrobeeStateGds.MobilityState.PERCHING;
	private int currentSubMobilityState = Integer.MAX_VALUE-1;
	PerchingArmPartCreator perchingArmSubtabCreator;
	protected boolean accessControlJustChanged = true;
	protected boolean iHaveControl = false;
	
	protected Vector<Binding> bindings = new Vector<Binding>();
	protected boolean alwaysEnablePanAndTilt = false;
	
	protected String spacerString = "    ";
	
	@Inject 
	public PerchingArmPart(MApplication application, Composite parent) {
		alwaysEnablePanAndTilt = WorkbenchConstants.isFlagPresent(WorkbenchConstants.SHOW_ENGINEERING_CONFIGURATION_STRING);
		transFormatter.setRoundingMode(RoundingMode.HALF_UP);
		rotFormatter.setRoundingMode(RoundingMode.HALF_UP);

		savedContext = application.getContext();

		createControls(parent);
	}
	
	protected void createControls(Composite parent) {
		perchingArmSubtabCreator = new PerchingArmPartCreator();
		perchingArmSubtabCreator.createArmCommandsTab(this, parent);
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
			stopButtonOnArmTab.setCompositeEnabled(false);
			return;
		}
		agent = a; // have to do this because we might be the other control panel

		commandPublisher = CommandPublisher.getInstance(agent);
		agentValid = true;

		stopButtonOnArmTab.setCompositeEnabled(true);

		if(astrobeeStateManager != null) {
			astrobeeStateManager.addListener(this);
		} else {
			System.err.println("PerchingArmPart does not have an AstrobeeStateManager");
		}
	}

	protected void enableButtonsThatAreAlwaysEnabledIfWeHaveControl(boolean enabled) {
		reacquirePositionButton.setCompositeEnabled(enabled);
		if(alwaysEnablePanAndTilt) {
			panAndTiltButton.setCompositeEnabled(enabled);
		}
	}

	@Override
	public void onAstrobeeStateChange(AggregateAstrobeeState aggregateState) {
		if(grabControlButtonOnArmTab == null) {
			return;
		}
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if(agent.name().equals(Agent.SmartDock.name()))
					return;
		
				accessControlName = aggregateState.getAccessControl();
				
				boolean shouldContinue = enableButtonsForAccessControl();

				if(!shouldContinue) {
					return;
				}

				shouldContinue = enableButtonsForOperatingState(aggregateState.getAstrobeeState());

				if(!shouldContinue) {
					return;
				}
				
				boolean mobilityStateChanged = false;
				if(aggregateState.getAstrobeeState().getMobilityState() != null && !currentMobilityState.equals(aggregateState.getAstrobeeState().getMobilityState())) {
					currentMobilityState = aggregateState.getAstrobeeState().getMobilityState();
					mobilityStateChanged = true;
				}

				if(currentSubMobilityState != aggregateState.getAstrobeeState().getSubMobilityState()
						|| justSwitchedRobots) {
					currentSubMobilityState = aggregateState.getAstrobeeState().getSubMobilityState();
					mobilityStateChanged = true;
				}

				if(!mobilityStateChanged) {
					return;
				}

				switch(currentMobilityState){
				case FLYING:
					perchButton.setText(PERCH_STRING);
					perchButton.setToolTipText(PERCH_TOOLTIP);
					perchButton.setCompositeEnabled(true);
					if(!alwaysEnablePanAndTilt) {
						panAndTiltButton.setCompositeEnabled(false);
					}
					break;
				case DOCKING:
					perchButton.setCompositeEnabled(false);
					if(!alwaysEnablePanAndTilt) {
						panAndTiltButton.setCompositeEnabled(false);
					}
					break;
				case PERCHING:
					if(currentSubMobilityState == 0){
						perchButton.setText(UNPERCH_STRING);
						perchButton.setToolTipText(UNPERCH_TOOLTIP);
						perchButton.setCompositeEnabled(true);	
						panAndTiltButton.setCompositeEnabled(true);
						panInput.setTextStringInt(0);
						tiltInput.setTextStringInt(0);
					}else{
						perchButton.setText(PERCH_STRING);
						perchButton.setToolTipText(PERCH_TOOLTIP);
						perchButton.setCompositeEnabled(false);	
						if(!alwaysEnablePanAndTilt) {
							panAndTiltButton.setCompositeEnabled(false);
						}
					}
					break;
				case DRIFTING:
					perchButton.setText(PERCH_STRING);
					perchButton.setToolTipText(PERCH_TOOLTIP);
					perchButton.setCompositeEnabled(true);
					if(!alwaysEnablePanAndTilt) {
						panAndTiltButton.setCompositeEnabled(false);
					}
					if(!beeIsSafeStopped || justSwitchedRobots) {
						beeMobilityStateCanMove = true;
						beeIsSafeStopped = true;
						justSwitchedRobots = false;
					}
					break;
				case STOPPING:
					beeMobilityStateCanMove = true;
					perchButton.setText(PERCH_STRING);
					perchButton.setToolTipText(PERCH_TOOLTIP);
					if(currentSubMobilityState != 0){
						perchButton.setCompositeEnabled(false);
						if(!alwaysEnablePanAndTilt) {
							panAndTiltButton.setCompositeEnabled(false);
						}
					} else {
						perchButton.setCompositeEnabled(true);
						if(!alwaysEnablePanAndTilt) {
							panAndTiltButton.setCompositeEnabled(false);
						}
					}
				}
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
				accessControlJustChanged = true;
				iHaveControl = false;
			}
			grabControlButtonOnArmTab.setCompositeEnabled(true);
			perchButton.setCompositeEnabled(false);
			if(!alwaysEnablePanAndTilt) {
				panAndTiltButton.setCompositeEnabled(false);
			}
			enableButtonsThatAreAlwaysEnabledIfWeHaveControl(false);
			return false;
		} else {
			if(!iHaveControl) {
				accessControlJustChanged = true;
				iHaveControl = true;
			}
			grabControlButtonOnArmTab.setCompositeEnabled(false);
			enableButtonsThatAreAlwaysEnabledIfWeHaveControl(true);
			return true;
		}
	}
	
	
	/** returns false if no more buttons should be enabled. */
	protected boolean enableButtonsForOperatingState(AstrobeeStateGds astrobeeStateGds) {
		if(astrobeeStateGds == null || astrobeeStateGds.getOperatingState() == null) {
			return false;
		}

		switch(astrobeeStateGds.getOperatingState()) {
		case READY:
		case TELEOPERATION:
			if(accessControlJustChanged || !beeMobilityStateCanMove) {
				beeMobilityStateCanMove = true;
				accessControlJustChanged = false;
			}
			return true;
		case PLAN_EXECUTION:
		case FAULT:
		case AUTO_RETURN:
			if(accessControlJustChanged || beeMobilityStateCanMove) {
				beeMobilityStateCanMove = false;
				accessControlJustChanged = false;
			}
			return false;
		}
		return true;
	}

	private void enableButtonsForCurrentMobilityState() {
		switch(currentMobilityState){
		case FLYING:
			perchButton.setText(PERCH_STRING);
			perchButton.setToolTipText(PERCH_TOOLTIP);
			perchButton.setCompositeEnabled(true);
			if(!alwaysEnablePanAndTilt) {
				panAndTiltButton.setCompositeEnabled(false);
			}
			break;
		case DOCKING:
			perchButton.setCompositeEnabled(false);
			if(!alwaysEnablePanAndTilt) {
				panAndTiltButton.setCompositeEnabled(false);
			}
			break;
		case PERCHING:
			//			perchButton.setCompositeEnabled(false);
			//			panAndTiltButton.setCompositeEnabled(false);
			if(currentSubMobilityState == 0){
				perchButton.setText(UNPERCH_STRING);
				perchButton.setToolTipText(UNPERCH_TOOLTIP);
				perchButton.setCompositeEnabled(true);	
				panAndTiltButton.setCompositeEnabled(true);
			}else{
				perchButton.setText(PERCH_STRING);
				perchButton.setToolTipText(UNPERCH_TOOLTIP);
				perchButton.setCompositeEnabled(false);	
				if(!alwaysEnablePanAndTilt) {
					panAndTiltButton.setCompositeEnabled(false);
				}
			}
			break;
		case DRIFTING:
			perchButton.setText(PERCH_STRING);
			perchButton.setCompositeEnabled(true);
			if(!alwaysEnablePanAndTilt) {
				panAndTiltButton.setCompositeEnabled(false);
			}
			if(!beeIsSafeStopped || justSwitchedRobots) {
				beeMobilityStateCanMove = true;
				beeIsSafeStopped = true;
				justSwitchedRobots = false;
			}
			break;
		case STOPPING:
			beeMobilityStateCanMove = true;
			perchButton.setText(PERCH_STRING);
			if(currentSubMobilityState != 0){
				perchButton.setCompositeEnabled(false);
				if(!alwaysEnablePanAndTilt) {
					panAndTiltButton.setCompositeEnabled(false);
				}
			} else {
				perchButton.setCompositeEnabled(true);
				panAndTiltButton.setCompositeEnabled(false);
			}
		}
	}

	public void setOnTop(boolean onTop) {
		this.onTop = onTop;
	}
}