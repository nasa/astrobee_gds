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
import gov.nasa.arc.irg.plan.bookmarks.StationBookmarkList;
import gov.nasa.arc.irg.plan.ui.io.EnlargeableButton;
import gov.nasa.arc.irg.util.ui.ColorProvider;
import gov.nasa.arc.verve.freeflyer.workbench.scenario.FreeFlyerScenario;
import gov.nasa.arc.verve.freeflyer.workbench.utils.Berth;
import gov.nasa.arc.verve.freeflyer.workbench.utils.TrackVisibleBeeCommandingSubtab;
import gov.nasa.arc.verve.freeflyer.workbench.widget.helpers.CommandButton;
import gov.nasa.arc.verve.freeflyer.workbench.widget.helpers.CreateValueStrategy;
import gov.nasa.arc.verve.freeflyer.workbench.widget.helpers.IncrementableText;
import gov.nasa.arc.verve.freeflyer.workbench.widget.helpers.IncrementableTextHorizontalInt;
import gov.nasa.arc.verve.robot.freeflyer.RapidFreeFlyerRobot;
import gov.nasa.arc.verve.robot.freeflyer.parts.LiveTeleopVerifier;
import gov.nasa.arc.verve.robot.freeflyer.parts.LiveTeleopVerifierListener;
import gov.nasa.arc.verve.robot.freeflyer.parts.RobotPartDraggablePreview;
import gov.nasa.arc.verve.robot.freeflyer.utils.ContextNames;
import gov.nasa.arc.verve.robot.freeflyer.utils.IPreviewMovedListener;
import gov.nasa.arc.verve.robot.freeflyer.utils.MathHelp;
import gov.nasa.rapid.idl.ext.astrobee.message.MessageTypeExtAstro;
import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.e4.message.MessageType;

import java.util.Vector;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.IPartListener;
import org.eclipse.jface.databinding.swt.ISWTObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import com.ardor3d.math.Quaternion;
import com.ardor3d.math.Vector3;

public class BeeCommandingPartOnTeleoperateTab implements LiveTeleopVerifierListener, AstrobeeStateListener, IPreviewMovedListener, IPartListener {
	private static final Logger logger = Logger.getLogger(BeeCommandingPartOnTeleoperateTab.class);
	protected String accessControlName = "";

	protected CommandPublisher commandPublisher;
	protected Agent agent = null;
	protected boolean agentValid = false;
	protected String myId = Agent.getEgoAgent().name();
	protected AstrobeeStateManager astrobeeStateManager;
	protected DataBindingContext m_dataBindingContext;

	protected final String TELEOP_DISALLOWED_STRING = "Potential collision";
	protected final String LONG_BLANK_STRING = "\t\t";
	protected final String NO_BOOKMARK_SELECTED_STRING = "Select Bookmarked Location";
	protected final String BLANK_STRING = " ";
	protected final double RAD_TO_DEG = 180.0 / Math.PI;
	protected final double DEG_TO_RAD = Math.PI / 180.0;
	protected final int DELAY = 3000; // delay before validating inputs typed into x, y, z boxes

	protected Berth selectedBerth = null;

	LiveTeleopVerifier teleopVerifier;

	protected boolean onTop = false; // if Teleoperation tab is selected
	protected boolean previewedTeleopIsLegal = true;
	private boolean beeIsSafeStopped = false;
	private boolean justSwitchedRobots = false; // if we just switch selected robots

	FreeFlyerScenario freeFlyerScenario;

	protected final Color pinkColor = ColorProvider.get(213, 94, 163);
	protected final Color greenColor = ColorProvider.get(59,181,74);
	protected final Color blueColor = ColorProvider.get(59,82,164);

	protected EnlargeableButton createBookmarkButton, setToCurrentPositionButton;
	protected Combo locationBookmarksCombo;
	
	protected EnlargeableButton showPreviewButton, snapToBeeButton;
	
	protected Button faceForwardButton, checkObstaclesButton, checkKeepoutsButton;
	protected Label faceForwardCheckmark, checkObstaclesCheckmark, checkKeepoutsCheckmark;
	protected CommandButton applyOptionsOnMainTab;

	protected CommandButton moveButton, stopButtonOnMainTab;
	
	protected final String CREATE_BOOKMARK_BUTTON_STRING = "Create Location Bookmark";
	protected final String CREATE_BOOKMARK_BUTTON_TOOLTIP = "Bookmark the Location of the Preview";

	protected final String SHOW_PREVIEW_STRING = "Show Preview";
	protected final String HIDE_PREVIEW_STRING = "Hide Preview";

	protected final String SHOW_PREVIEW_TOOLTIP = "Preview Pose Specified by Manual Inputs";
	protected final String HIDE_PREVIEW_TOOLTIP = "Hide Preview";
	
	protected final String SNAP_TO_BEE_STRING = "Snap Preview to Bee";
	protected final String SNAP_TO_BEE_TOOLTIP = "Enter Current Astrobee Coordinates into Manual Inputs";

	protected final String FACE_FORWARD_BUTTON_STRING = "Face Forward";
	protected final String FACE_FORWARD_BUTTON_TOOLTIP = "Require Astrobee to Face Direction of Motion";
	protected final String FACE_FORWARD_UNCHECKED_TOOLTIP = "Astrobee Will Fly Sideways";
	protected final String FACE_FORWARD_CHECKED_TOOLTIP = "Astrobee Will Face Direction of Motion";

	protected final String CHECK_OBSTACLES_BUTTON_STRING = "Check Obstacles";
	protected final String CHECK_OBSTACLES_BUTTON_TOOLTIP = "Require Astrobee to Stop If Obstacle Detected";
	protected final String CHECK_OBSTACLES_CHECKED_TOOLTIP = "Astrobee Will Stop If Obstacle Detected";
	protected final String CHECK_OBSTACLES_UNCHECKED_TOOLTIP = "Astrobee Will Not Stop If Obstacle Detected";

	protected final String CHECK_KEEPOUTS_BUTTON_STRING = "Check Keepouts";
	protected final String CHECK_KEEPOUTS_BUTTON_TOOLTIP = "Prevent Astrobee From Entering Keepouts";
	protected final String CHECK_KEEPOUTS_CHECKED_TOOLTIP = "Astrobee Will Stop Before Entering a Keepout";
	protected final String CHECK_KEEPOUTS_UNCHECKED_TOOLTIP = "Astrobee Will Enter Keepouts";

	BeeCommandingPartOnTeleoperateTabCreator commandingPartCreator;

	protected IncrementableText[] translationInput = new IncrementableText[3];
	protected String[][] translationInputLabel = {{"Aft","Fwd"},{"Port","Stbd"},{"Ovhd","Deck"}};
	protected IncrementableTextHorizontalInt[] rotationInput = new IncrementableTextHorizontalInt[3];
	protected String[] rotationInputLabel = {"Roll","Pitch","Yaw"};
	protected Color[] rotationColor = {pinkColor, greenColor, blueColor};

	protected Label moveDisabledExplanationLabel;

	protected StationBookmarkList locationBookmarks;
	protected String[] bookmarkNames;

	protected boolean justSetLocationBookmark = false;
	IEclipseContext savedContext;

	private boolean robotSelected = false;
	private boolean beeMobilityStateCanMove = false;
	private AstrobeeStateGds.MobilityState currentMobilityState = AstrobeeStateGds.MobilityState.PERCHING;
	private int currentSubMobilityState = Integer.MAX_VALUE-1;
	protected boolean accessControlJustChanged = true;
	protected boolean iHaveControl = false;

	protected Vector<Binding> bindings = new Vector<Binding>();

	protected Image	checkedImage, uncheckedImage, unknownCheckedImage;
	protected String spacerString = "    ";
	protected boolean checkingKeepouts = true;

	@Inject 
	public BeeCommandingPartOnTeleoperateTab(EPartService eps, MApplication application, Composite parent) {
		m_dataBindingContext = new DataBindingContext();

		savedContext = application.getContext();
		savedContext.set(BeeCommandingPartOnTeleoperateTab.class, this);

		eps.addPartListener(this);
		
		setup();
		loadCheckedImages();
		createControls(parent);
	}
	
	@Inject @Optional
	public void acceptFreeFlyerScenario(FreeFlyerScenario ffs) {
		freeFlyerScenario = ffs;
		connectDraggablePreviewsToListeners();
	}

	protected void createControls(Composite parent) {
		commandingPartCreator = new BeeCommandingPartOnTeleoperateTabCreator();
		commandingPartCreator.createMainTab(this, parent);
	}

	@PostConstruct
	public void postConstruct() {
		bindUI();
		setContextInDraggablePreviews();
		snapToBee();
	}

	public IncrementableText[] getCurrentPosition(){
		return translationInput;
	}

	public IncrementableText[] getCurrentRotation(){
		return rotationInput;
	}

	protected void setContextInDraggablePreviews() {
		RapidFreeFlyerRobot[] robotsList = freeFlyerScenario.getAllRobots();
		for(int i=0; i<robotsList.length; i++) {
			RapidFreeFlyerRobot robot = robotsList[i];
			RobotPartDraggablePreview rpdp = (RobotPartDraggablePreview)robot.getPart(RapidFreeFlyerRobot.ABSOLUTE_DRAGGABLE_PREVIEW);
			rpdp.setContext(savedContext);
		}	
	}

	// connect DraggablePreviews to TeleopVerifierListeners and PreviewMovedListeners
	protected void connectDraggablePreviewsToListeners() {
		RapidFreeFlyerRobot[] robotsList = freeFlyerScenario.getAllRobots();
		for(int i=0; i<robotsList.length; i++) {
			RapidFreeFlyerRobot robot = robotsList[i];
			RobotPartDraggablePreview rpdp = (RobotPartDraggablePreview)robot.getPart(RapidFreeFlyerRobot.ABSOLUTE_DRAGGABLE_PREVIEW);
			teleopVerifier.addListener(rpdp);
			rpdp.setPreviewMovedListener(this);
		}
	}

	protected void setup() {
		teleopVerifier = savedContext.get(LiveTeleopVerifier.class);
		if(teleopVerifier == null) {
			ContextInjectionFactory.make(LiveTeleopVerifier.class, savedContext); 
			teleopVerifier = savedContext.get(LiveTeleopVerifier.class);
		}
		teleopVerifier.addListener(this);
	}

	@Inject @Optional 
	public void updateTeleopTranslationFieldsFromContext(@Named(ContextNames.TELEOP_TRANSLATION) Vector3 teleop) {
		if(teleop == null) {
			return;
		}
		translationInput[0].setTextString(teleop.getX());
		translationInput[1].setTextString(teleop.getY());
		translationInput[2].setTextString(teleop.getZ());
	}

	@Inject @Optional 
	public void updateTeleopRotationTextFieldsFromContext(@Named(ContextNames.TELEOP_ROTATION_RADIANS) Vector3 teleop) {
		if(teleop == null) {
			return;
		}
		double rdeg = teleop.getX() * RAD_TO_DEG;
		double pdeg = teleop.getY() * RAD_TO_DEG;
		double ydeg = teleop.getZ() * RAD_TO_DEG;
		
		rotationInput[0].setTextStringInt((int)rdeg);
		rotationInput[1].setTextStringInt((int)pdeg);
		rotationInput[2].setTextStringInt((int)ydeg);
	}

	@PreDestroy
	public void preDestroy() {
		teleopVerifier.removeListener(this);
		teleopVerifier.removeListener(getDraggablePreview());
	}

	protected boolean bindUI() {
		if(getDraggablePreview() == null) {
			return false;
		}

		boolean result = true;
		result &= bind("x", translationInput[0], getDraggablePreview());
		result &= bind("y", translationInput[1], getDraggablePreview());
		result &= bind("z", translationInput[2], getDraggablePreview());
		
		result &= bind("roll", rotationInput[0], getDraggablePreview());
		result &= bind("pitch", rotationInput[1], getDraggablePreview());
		result &= bind("yaw", rotationInput[2], getDraggablePreview());
		
		checkTeleopVerifier();
		return result;
	}
	
	protected void checkTeleopVerifier() {
		if(teleopVerifier != null) {
			teleopVerifier.updateAbsoluteTeleopTranslation(convertToVector3(translationInput, 1));
			teleopVerifier.updateAbsoluteTeleopRotation(convertToVector3(rotationInput, DEG_TO_RAD));
			teleopVerifier.checkAbsolutePositionAndNotifyListeners();
		}
	}

	protected Vector3 convertToVector3(IncrementableText[] input, double scaleFactor) {
		Vector3 output = new Vector3();
		output.setX(input[0].getNumber() * scaleFactor);
		output.setY(input[1].getNumber() * scaleFactor);
		output.setZ(input[2].getNumber() * scaleFactor);
		return output;
	}

	protected void unbindUI() {
		for(Binding b: bindings) {
			b.dispose();
		}
		bindings.clear();
	}

	protected boolean bind(String feature, final IncrementableText widget, final Object model) {
		if (widget == null){
			return false;
		}

		IObservableValue modelObservableValue = BeanProperties.value(feature).observe(model);

		if (modelObservableValue == null){
			return false;
		}

		//TODO verify that lose focus still works even with this binding
		ISWTObservableValue targetObservableValue = WidgetProperties.text(SWT.Modify).observeDelayed(DELAY, widget.getTextControl());

		Binding bd = m_dataBindingContext.bindValue(targetObservableValue, modelObservableValue,
				CreateValueStrategy.getTargetToModelStrategy(feature), null);	

		bindings.add(bd);
		widget.setBinding(bd);
		return true;
	}

	protected RobotPartDraggablePreview getDraggablePreview() {
		if(freeFlyerScenario == null || freeFlyerScenario.getPrimaryRobot() == null) {
			return null;
		}
		return (RobotPartDraggablePreview)freeFlyerScenario.getPrimaryRobot()
				.getPart(RapidFreeFlyerRobot.ABSOLUTE_DRAGGABLE_PREVIEW);
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
			stopButtonOnMainTab.setCompositeEnabled(false);
			return;
		}
		unbindUI();
		agent = a; // have to do this because we might be the other control panel

		commandPublisher = CommandPublisher.getInstance(agent);
		agentValid = true;

		robotSelected = true;
		stopButtonOnMainTab.setCompositeEnabled(true);

		if(astrobeeStateManager != null) {
			astrobeeStateManager.addListener(this);
		} else {
			System.err.println("BeeCommandingPartOnTeleoperateTab does not have an AstrobeeStateManager");
		}
		bindUI();
		snapToBee();
	}

	protected void setEnableButtonsThatAreAlwaysEnabledIfWeHaveControl(boolean enabled) {
		if(applyOptionsOnMainTab != null || !applyOptionsOnMainTab.isDisposed() ) {
			applyOptionsOnMainTab.setCompositeEnabled(enabled);
		}
		if(locationBookmarksCombo != null || !locationBookmarksCombo.isDisposed()) {
			locationBookmarksCombo.setEnabled(enabled);
		}
	}

	@Override
	public void onAstrobeeStateChange(AggregateAstrobeeState aggregateState) {
		if(moveButton == null) {
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
					break;
				case PERCHING:
					break;
				case DRIFTING:
					if(!beeIsSafeStopped || justSwitchedRobots) {
						beeMobilityStateCanMove = true;
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
			moveButton.setCompositeEnabled(false);
			setEnableButtonsThatAreAlwaysEnabledIfWeHaveControl(false);
			return false;
		} else {
			if(!iHaveControl) {
				accessControlJustChanged = true;
				iHaveControl = true;
			}
			setEnableButtonsThatAreAlwaysEnabledIfWeHaveControl(true);
			return true;
		}
	}

	protected void updateCheckmarksForAstrobeeState(AstrobeeStateGds astrobeeStateGds) {
		if(astrobeeStateGds == null || faceForwardCheckmark.isDisposed()) {
			return ;
		}

		if(astrobeeStateGds.getEnableHolonomic()) {
			faceForwardCheckmark.setImage(uncheckedImage);
			faceForwardCheckmark.setText(spacerString);
			faceForwardCheckmark.setToolTipText(FACE_FORWARD_UNCHECKED_TOOLTIP);
		} else {
			faceForwardCheckmark.setImage(checkedImage);
			faceForwardCheckmark.setToolTipText(FACE_FORWARD_CHECKED_TOOLTIP);
		}

		if(astrobeeStateGds.getCheckObstacles()) {
			checkObstaclesCheckmark.setImage(checkedImage);
			checkObstaclesCheckmark.setToolTipText(this.CHECK_OBSTACLES_CHECKED_TOOLTIP);
		} else {
			checkObstaclesCheckmark.setImage(uncheckedImage);
			checkObstaclesCheckmark.setText(spacerString);
			checkObstaclesCheckmark.setToolTipText(this.CHECK_OBSTACLES_UNCHECKED_TOOLTIP);
		}

		if(astrobeeStateGds.getCheckKeepouts()) {
			checkKeepoutsCheckmark.setImage(checkedImage);
			checkKeepoutsCheckmark.setToolTipText(this.CHECK_KEEPOUTS_CHECKED_TOOLTIP);
			checkingKeepouts = true;
		} else {
			checkKeepoutsCheckmark.setImage(uncheckedImage);
			checkKeepoutsCheckmark.setText(spacerString);
			checkKeepoutsCheckmark.setToolTipText(this.CHECK_KEEPOUTS_UNCHECKED_TOOLTIP);
			checkingKeepouts = false;
		}
		this.savedContext.set(ContextNames.CHECK_KEEPOUTS_ENABLED, checkingKeepouts);
		enableMoveButtonIfAllowed();
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
				enableMoveButtonIfAllowed();
				accessControlJustChanged = false;
			}
			return true;
		case PLAN_EXECUTION:
		case FAULT:
		case AUTO_RETURN:
			if(accessControlJustChanged || beeMobilityStateCanMove) {
				beeMobilityStateCanMove = false;
				moveButton.setCompositeEnabled(false);
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
				beeIsSafeStopped = true;
				justSwitchedRobots = false;
			}
			break;
		case STOPPING:
			beeMobilityStateCanMove = true;
		}
	}

	public void onPreviewMoved() {
		if(justSetLocationBookmark) {
			return;
		}
		locationBookmarksCombo.setText(NO_BOOKMARK_SELECTED_STRING);
	}

	public void snapToBee() {
		// Can't updateBinding() in a loop later because
		// it will reset the 2nd and 3rd ones while doing the first
		if(astrobeeStateManager == null) {
			return;
		}
		Vector3 astrobeePosition = astrobeeStateManager.getAstrobeePosition();
		translationInput[0].setTextString(astrobeePosition.getX());
		translationInput[0].updateBinding();

		translationInput[1].setTextString(astrobeePosition.getY());
		translationInput[1].updateBinding();

		translationInput[2].setTextString(astrobeePosition.getZ());
		translationInput[2].updateBinding();

		Quaternion q = astrobeeStateManager.getAstrobeeOrientation();
		Vector3 ea = MathHelp.findZYXEulerAngles(q);

		double roll = ea.getX() * RAD_TO_DEG;
		double pitch = ea.getY() * RAD_TO_DEG;
		double yaw = ea.getZ() * RAD_TO_DEG;

		rotationInput[0].setTextStringInt((int)Math.round(roll));
		rotationInput[0].updateBinding();

		rotationInput[1].setTextStringInt((int)Math.round(pitch));
		rotationInput[1].updateBinding();

		rotationInput[2].setTextStringInt((int)Math.round(yaw));
		rotationInput[2].updateBinding();

		teleopVerifier.checkAbsolutePositionAndNotifyListeners();
	}

	@Override
	public void allowAbsoluteMovement() {
		previewedTeleopIsLegal = true;
		enableMoveButtonIfAllowed();
	}

	@Override
	public void disallowAbsoluteMovement() {
		previewedTeleopIsLegal = false;
		enableMoveButtonIfAllowed();
	}

	protected void enableMoveButtonIfAllowed() {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if( moveButton == null ) { //openPreviewTabName == null ) {
					return;
				}
				boolean canMove = robotSelected && beeMobilityStateCanMove && previewedTeleopIsLegal && accessControlName.equals(myId);

				moveButton.setCompositeEnabled(canMove || !checkingKeepouts);
				if(previewedTeleopIsLegal) {
					moveDisabledExplanationLabel.setText(LONG_BLANK_STRING);
				} else {
					moveDisabledExplanationLabel.setText(TELEOP_DISALLOWED_STRING);
				}
			}
		});
	}

	protected void loadCheckedImages() {
		checkedImage = IssUiActivator.getImageFromRegistry("success_16");
		uncheckedImage = IssUiActivator.getImageFromRegistry("transparent");
		unknownCheckedImage = IssUiActivator.getImageFromRegistry("transparent");
	}

	@Override
	public void allowRelativeMovement() {
		// we aren't relative
	}

	@Override
	public void disallowRelativeMovement() {
		// we aren't relative
	}

	@Override
	public void partActivated(MPart part) {}

	@Override
	public void partBroughtToTop(MPart part) {
		TrackVisibleBeeCommandingSubtab.INSTANCE.ingestPartBroughtToTop(part.getElementId());
		if(TrackVisibleBeeCommandingSubtab.INSTANCE.isBeeCommandingOnTop()) {
			freeFlyerScenario.showAbsolutePreview(TrackVisibleBeeCommandingSubtab.INSTANCE.isAbsolutePreviewShowing());
		} else if(TrackVisibleBeeCommandingSubtab.INSTANCE.isRelativeCommandingOnTop()) {
			// hide because sibling is showing
			freeFlyerScenario.showAbsolutePreview(false);
		}
	}

	@Override
	public void partDeactivated(MPart part) {}

	@Override
	public void partHidden(MPart part) {}

	@Override
	public void partVisible(MPart part) {}
}