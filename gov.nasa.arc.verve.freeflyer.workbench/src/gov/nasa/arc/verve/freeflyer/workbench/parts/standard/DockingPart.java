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
import gov.nasa.arc.irg.iss.ui.IssUiActivator;
import gov.nasa.arc.verve.freeflyer.workbench.scenario.FreeFlyerScenario;
import gov.nasa.arc.verve.freeflyer.workbench.utils.Berth;
import gov.nasa.arc.verve.freeflyer.workbench.widget.helpers.CommandButton;
import gov.nasa.arc.verve.robot.freeflyer.parts.LiveTeleopVerifier;
import gov.nasa.rapid.idl.ext.astrobee.message.MessageTypeExtAstro;
import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.e4.message.MessageType;

import java.text.DecimalFormat;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import com.ardor3d.math.Vector3;

public class DockingPart implements AstrobeeStateListener {
	protected CommandButton grabControlButtonOnDockTab;
	protected String accessControlName = "";

	protected CommandPublisher commandPublisher;
	protected Agent agent = null;
	protected boolean agentValid = false;
	protected String myId = Agent.getEgoAgent().name();
	protected AstrobeeStateManager astrobeeStateManager;

	protected final String LONG_BLANK_STRING = "\t\t";
	protected final String BLANK_STRING = " ";

	protected Berth selectedBerth = null;
	
	protected DecimalFormat transFormatter = new DecimalFormat("#.#");
	protected DecimalFormat rotFormatter = new DecimalFormat("#");
	protected Vector3 dragTranslationVec = new Vector3(0,0,0);
	protected Vector3 dragRotationVec = new Vector3(0,0,0);

	LiveTeleopVerifier teleopVerifier;

	protected boolean onTop = false; // if Teleoperation tab is selected
	private boolean beeIsSafeStopped = false;
	private boolean justSwitchedRobots = false; // if we just switch selected robots

	@Inject
	FreeFlyerScenario freeFlyerScenario;

	protected CommandButton stopButtonOnDockTab;
	protected CommandButton applyOptionsOnDockTab;
	
	protected final String ENABLE_AUTORETURN_CHECKED_TOOLTIP = "Astrobee Will Automatically Return to Docking Station When Battery Low";
	protected final String ENABLE_AUTORETURN_UNCHECKED_TOOLTIP = "Astrobee Will Not Automatically Return to Docking Station When Battery Low";
	
	protected Button enableAutoReturnButton;
	protected Label enableAutoReturnCheckmark;
	protected CommandButton dockAutomaticallyButton;

	IEclipseContext savedContext;

	protected boolean mainTabIsOnTop = true;
	private boolean robotSelected = false;
	private boolean beeMobilityStateCanMove = false;
	private AstrobeeStateGds.MobilityState currentMobilityState = AstrobeeStateGds.MobilityState.PERCHING;
	private int currentSubMobilityState = Integer.MAX_VALUE-1;
	DockingPartCreator dockingSubtabCreator;
	protected boolean accessControlJustChanged = true;
	protected boolean iHaveControl = false;
	
	protected Image	checkedImage, uncheckedImage, unknownCheckedImage;
	protected String spacerString = "    ";
	
	protected final String DOCK_AUTO = "Send Bee to Docking Station";
	protected final String UNDOCK = "Undock";
	
	@Inject 
	public DockingPart(MApplication application, Composite parent) {

		savedContext = application.getContext();
		//savedContext.set(TeleopControlPanel.class, this);

		createControls(parent);
	}
	
	protected void createControls(Composite parent) {
		loadCheckedImages();
		
		dockingSubtabCreator = new DockingPartCreator();
		dockingSubtabCreator.createDockCommandsTab(this, parent);
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
			stopButtonOnDockTab.setCompositeEnabled(false);
			return;
		}
		agent = a; // have to do this because we might be the other control panel

		commandPublisher = CommandPublisher.getInstance(agent);
		agentValid = true;

		robotSelected = true;
		stopButtonOnDockTab.setCompositeEnabled(true);

		if(astrobeeStateManager != null) {
			astrobeeStateManager.addListener(this);
		} else {
			System.err.println("DockingPart does not have an AstrobeeStateManager");
		}
	}
	
	protected void enableButtonsThatAreAlwaysEnabledIfWeHaveControl(boolean enabled) {
		dockAutomaticallyButton.setCompositeEnabled(enabled);
		applyOptionsOnDockTab.setCompositeEnabled(enabled);
	}

	@Override
	public void onAstrobeeStateChange(AggregateAstrobeeState aggregateState) {
		if(grabControlButtonOnDockTab == null) {
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

				updateCheckmarksForAstrobeeState(aggregateState.getAstrobeeState());
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
					break;
				case DOCKING:
					if(aggregateState.getAstrobeeState().getSubMobilityState() == 0)
						dockAutomaticallyButton.setText(UNDOCK);
					else
						dockAutomaticallyButton.setText(DOCK_AUTO);
					break;
				case PERCHING:
					break;
				case DRIFTING:
					if(!beeIsSafeStopped || justSwitchedRobots) {
						beeMobilityStateCanMove = true;
//						if(onTop) {
//							updateOpenPreviewTab();
//						}
						beeIsSafeStopped = true;
						justSwitchedRobots = false;
					}
					break;
				case STOPPING:
					beeMobilityStateCanMove = true;
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
			grabControlButtonOnDockTab.setCompositeEnabled(true);
			enableButtonsThatAreAlwaysEnabledIfWeHaveControl(false);
			return false;
		} else {
			if(!iHaveControl) {
				accessControlJustChanged = true;
				iHaveControl = true;
			}
			grabControlButtonOnDockTab.setCompositeEnabled(false);
			enableButtonsThatAreAlwaysEnabledIfWeHaveControl(true);
			return true;
		}
	}
	
	protected void updateCheckmarksForAstrobeeState(AstrobeeStateGds astrobeeStateGds) {
		if(astrobeeStateGds == null || enableAutoReturnCheckmark.isDisposed()) {
			return ;
		}
		
		if(astrobeeStateGds.getEnableAutoReturn()) {
			enableAutoReturnCheckmark.setImage(checkedImage);
			enableAutoReturnCheckmark.setToolTipText(this.ENABLE_AUTORETURN_CHECKED_TOOLTIP);
		} else {
			enableAutoReturnCheckmark.setImage(uncheckedImage);
			enableAutoReturnCheckmark.setToolTipText(this.ENABLE_AUTORETURN_UNCHECKED_TOOLTIP);
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
			break;
		case DOCKING:
			break;
		case PERCHING:
			break;
		case DRIFTING:
			if(!beeIsSafeStopped || justSwitchedRobots) {
				beeMobilityStateCanMove = true;
				// XXX ??????????
//				if(onTop) {
//					updateOpenPreviewTab();
//				}
				beeIsSafeStopped = true;
				justSwitchedRobots = false;
			}
			break;
		case STOPPING:
			beeMobilityStateCanMove = true;
		}
	}

	public void setOnTop(boolean onTop) {
		this.onTop = onTop;
	}
	
	protected void loadCheckedImages() {
			checkedImage = IssUiActivator.getImageFromRegistry("success_16");
			uncheckedImage = IssUiActivator.getImageFromRegistry("transparent");
			unknownCheckedImage = IssUiActivator.getImageFromRegistry("transparent");
	}
}
