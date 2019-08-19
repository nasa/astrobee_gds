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

package gov.nasa.arc.verve.robot.freeflyer.parts;

import gov.nasa.arc.verve.common.VerveUserData;
import gov.nasa.arc.verve.common.ardor3d.interact.VerveInteractManagers;
import gov.nasa.arc.verve.robot.AbstractRobot;
import gov.nasa.arc.verve.robot.freeflyer.utils.ContextNames;
import gov.nasa.arc.verve.robot.freeflyer.utils.MathHelp;
import gov.nasa.rapid.v2.framestore.ConvertUtils;
import gov.nasa.rapid.v2.framestore.EulerAngles;
import gov.nasa.rapid.v2.framestore.ReadOnlyEulerAngles;

import com.ardor3d.math.Matrix3;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyMatrix3;
import com.ardor3d.math.type.ReadOnlyVector3;
import com.ardor3d.scenegraph.Node;

public class RobotPartRelativeDraggablePreview extends RobotPartDraggablePreview {

	public RobotPartRelativeDraggablePreview(String partName, AbstractRobot parent) {
		super(partName, parent);
	}
	
	@Override
	public void attachToNodesIn(Node model) throws IllegalStateException {
		m_node = new Node();
		m_preview = new FreeFlyerBasicModel("rose");

		getRobot().getRobotNode().getConceptsNode().attachChild(m_node);

		m_node.attachChild(m_preview);
		m_node.updateWorldBound(false);

		VerveUserData.setInteractable(m_node, createInteractable());
		VerveUserData.setCameraFollowable(m_node, false);
	}
	
	@Override
	protected String getNodeName() {
		return getRobot().getName() + ":RelativeDraggablePreview";
	}

	@Override
	protected void translateFromDrag(ReadOnlyVector3 totalMove) {
		double deltaX, deltaY, deltaZ;

		ReadOnlyVector3 currentMove = m_parent.getPoseProvider().getXyz();
		deltaX = round(totalMove.getX() - currentMove.getX());
		deltaY = round(totalMove.getY() - currentMove.getY());
		deltaZ = round(totalMove.getZ() - currentMove.getZ());

		totalMove = new Vector3(deltaX, deltaY, deltaZ);

		if(displayX !=  deltaX) {
			hasBeenDraggedX = true;		
			displayX = deltaX;
		}
		if(displayY != deltaY) {
			hasBeenDraggedY = true;		
			displayY = deltaY;
		}
		if(displayZ != deltaZ) {
			hasBeenDraggedZ = true;
			displayZ = deltaZ;
		}

		if(hasBeenDraggedX || hasBeenDraggedY || hasBeenDraggedZ) {
			if(context != null) {
				context.set(ContextNames.RELATIVE_TELEOP_TRANSLATION, null);
				context.set(ContextNames.RELATIVE_TELEOP_TRANSLATION, totalMove); // tell LiveTeleopVerifier to check again because we just moved
			} 
		}
	}

	@Override
	protected void translateFromDisplayText() {
		Vector3 move = new Vector3(displayX, displayY, displayZ);

		if(context != null) {
			// tell LiveTeleopVerifier to check again because we just moved
			// this also updates TeleopControlPanel
			context.set(ContextNames.RELATIVE_TELEOP_TRANSLATION, move); 
		}
		ReadOnlyVector3 parentXyz = m_parent.getPoseProvider().getXyz();
		Vector3 moveWorld = parentXyz.add(move, null);
		m_node.setTranslation(moveWorld);

		if(draggablePreviewVisible) {
			VerveInteractManagers.INSTANCE.setSpatialTarget(m_node);
		}
	}
	
	@Override
	protected void rotateFromDrag(ReadOnlyMatrix3 totalRotWorld) {
		ReadOnlyMatrix3 current = m_parent.getPoseProvider().calculateTransform().getMatrix();				
		ReadOnlyMatrix3 currentInv = current.invert(null);
		ReadOnlyMatrix3 deltaRot = currentInv.multiply(totalRotWorld, null);

		Vector3 v = MathHelp.findZYXEulerAngles(deltaRot);
		double A = v.getX();
		double B = v.getY();
		double C = v.getZ();

		if(MathHelp.anglesAreDifferent(displayRoll, A)) {
			hasBeenDraggedRoll = true;
			displayRoll = (int)(A * 180.0 / Math.PI);
		}
		if(MathHelp.anglesAreDifferent(displayPitch, B)) {
			hasBeenDraggedPitch = true;
			displayPitch = (int)(B * 180.0 / Math.PI);
		}
		if(MathHelp.anglesAreDifferent(displayYaw, C)) {
			hasBeenDraggedYaw = true;
			displayYaw = (int)(C * 180.0 / Math.PI);
		}

		if(context != null) {
			context.set(ContextNames.RELATIVE_TELEOP_ROTATION_RADIANS, null);
			Vector3 rotate = new Vector3(
					A, B, C);
			context.set(ContextNames.RELATIVE_TELEOP_ROTATION_RADIANS, rotate); // tell LiveTeleopVerifier to check again because we just moved
		}
	}

	@Override
	protected void rotateFromDisplayText() {
		// display is delta, need total
		double rollRadians = displayRoll * Math.PI / 180.0;
		double pitchRadians = displayPitch * Math.PI / 180.0;
		double yawRadians = displayYaw * Math.PI / 180.0;

		EulerAngles deltaEa = new EulerAngles(ReadOnlyEulerAngles.Type.ZYXr, yawRadians, pitchRadians, rollRadians);
		Matrix3 deltaMat = ConvertUtils.toRotationMatrix(deltaEa, null);
		
		ReadOnlyMatrix3 current = m_parent.getPoseProvider().calculateTransform().getMatrix();
		
		ReadOnlyMatrix3 total = current.multiply(deltaMat, null);
		m_node.setRotation(total);
		
		if(context != null) {
			Vector3 deltaRot = new Vector3(rollRadians, pitchRadians, yawRadians);
			context.set(ContextNames.RELATIVE_TELEOP_ROTATION_RADIANS, null);
			context.set(ContextNames.RELATIVE_TELEOP_ROTATION_RADIANS, deltaRot); // tell LiveTeleopVerifier to check again because we just moved
		}

		if(draggablePreviewVisible) {
			VerveInteractManagers.INSTANCE.setSpatialTarget(m_node);
		}
	}
	
	@Override
	public void allowRelativeMovement() {
		hideAlarmColor();
	}
	
	@Override
	public void disallowRelativeMovement() {
		showAlarmColor();
	}
	
	@Override
	public void allowAbsoluteMovement() {
		// ignore, we are not absolute
	}

	@Override
	public void disallowAbsoluteMovement() {
		// ignore, we are not absolute
	}

}
