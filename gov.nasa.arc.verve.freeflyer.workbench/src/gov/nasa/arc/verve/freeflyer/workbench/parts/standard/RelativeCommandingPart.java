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

import gov.nasa.arc.verve.freeflyer.workbench.utils.TrackVisibleBeeCommandingSubtab;
import gov.nasa.arc.verve.robot.freeflyer.RapidFreeFlyerRobot;
import gov.nasa.arc.verve.robot.freeflyer.parts.RobotPartDraggablePreview;
import gov.nasa.arc.verve.robot.freeflyer.parts.RobotPartRelativeDraggablePreview;
import gov.nasa.arc.verve.robot.freeflyer.utils.ContextNames;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.widgets.Composite;

import com.ardor3d.math.Vector3;

public class RelativeCommandingPart extends BeeCommandingPartOnTeleoperateTab {
	
	@Inject 
	public RelativeCommandingPart(EPartService eps, MApplication application, Composite parent) {
		super(eps, application, parent);
	}
	
	// connect DraggablePreviews to TeleopVerifierListeners and PreviewMovedListeners
	protected void connectDraggablePreviewsToListeners() {
		RapidFreeFlyerRobot[] robotsList = freeFlyerScenario.getAllRobots();
		for(int i=0; i<robotsList.length; i++) {
			RapidFreeFlyerRobot robot = robotsList[i];
			RobotPartRelativeDraggablePreview rpdp = (RobotPartRelativeDraggablePreview)robot.getPart(RapidFreeFlyerRobot.RELATIVE_DRAGGABLE_PREVIEW);
			teleopVerifier.addListener(rpdp);
			rpdp.setPreviewMovedListener(this);
		}
	}
	
	public void snapToBee() {
		for(int i=0; i<3; i++) {
			translationInput[i].setTextString(0);
			translationInput[i].updateBinding();
			
			rotationInput[i].setTextStringInt(0);
			rotationInput[i].updateBinding();
		}
	
		checkTeleopVerifier();
	}
	
	@Override
	protected RobotPartDraggablePreview getDraggablePreview() {
		if(freeFlyerScenario == null || freeFlyerScenario.getPrimaryRobot() == null) {
			return null;
		}
		return (RobotPartRelativeDraggablePreview)freeFlyerScenario.getPrimaryRobot()
				.getPart(RapidFreeFlyerRobot.RELATIVE_DRAGGABLE_PREVIEW);
	}
	
	protected void checkTeleopVerifier() {
		if(teleopVerifier != null) {
			teleopVerifier.updateRelativeTeleopTranslation(convertToVector3(translationInput, 1));
			//teleopVerifier.updateRelativeTeleopRotation(convertToVector3(rotationInput, DEG_TO_RAD));
			teleopVerifier.checkRelativePositionAndNotifyListeners();
		}
	}
	
	@Override
	protected void createControls(Composite parent) {
		commandingPartCreator = new RelativeCommandingPartCreator();
		commandingPartCreator.createMainTab(this, parent);
	}
	
	
	@Inject @Optional 
	public void updateRelativeTeleopTranslationFieldsFromContext(@Named(ContextNames.RELATIVE_TELEOP_TRANSLATION) Vector3 teleop) {
		if(teleop == null) {
			return;
		}
		translationInput[0].setTextString(teleop.getX());
		translationInput[1].setTextString(teleop.getY());
		translationInput[2].setTextString(teleop.getZ());
	}

	@Inject @Optional 
	public void updateRelativeTeleopRotationTextFieldsFromContext(@Named(ContextNames.RELATIVE_TELEOP_ROTATION_RADIANS) Vector3 teleop) {
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
	
	@Inject @Optional @Override
	public void updateTeleopTranslationFieldsFromContext(@Named(ContextNames.TELEOP_TRANSLATION) Vector3 teleop) {
	   //
	}

	@Inject @Optional @Override
	public void updateTeleopRotationTextFieldsFromContext(@Named(ContextNames.TELEOP_ROTATION_RADIANS) Vector3 teleop) {
		//
	}

	@Override
	public void allowAbsoluteMovement() {
		// we aren't absolute
	}

	@Override
	public void disallowAbsoluteMovement() {
		// we aren't absolute
	}
	
	@Override
	public void allowRelativeMovement() {
		previewedTeleopIsLegal = true;
		enableMoveButtonIfAllowed();
	}

	@Override
	public void disallowRelativeMovement() {
		previewedTeleopIsLegal = false;
		enableMoveButtonIfAllowed();
	}
	
	@Override
	protected void setContextInDraggablePreviews() {
		RapidFreeFlyerRobot[] robotsList = freeFlyerScenario.getAllRobots();
		for(int i=0; i<robotsList.length; i++) {
			RapidFreeFlyerRobot robot = robotsList[i];
			RobotPartDraggablePreview rpdp = (RobotPartDraggablePreview)robot.getPart(RapidFreeFlyerRobot.RELATIVE_DRAGGABLE_PREVIEW);
			rpdp.setContext(savedContext);
		}	
	}
	
	@Override
	public void partBroughtToTop(MPart part) {
		TrackVisibleBeeCommandingSubtab.INSTANCE.ingestPartBroughtToTop(part.getElementId());
		if(TrackVisibleBeeCommandingSubtab.INSTANCE.isRelativeCommandingOnTop()) {
			freeFlyerScenario.showRelativePreview(TrackVisibleBeeCommandingSubtab.INSTANCE.isRelativePreviewShowing());
		} else if(TrackVisibleBeeCommandingSubtab.INSTANCE.isBeeCommandingOnTop()) {
			// hide because sibling is showing
			freeFlyerScenario.showRelativePreview(false);
		}
	}
}
