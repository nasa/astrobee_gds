package gov.nasa.arc.verve.robot.freeflyer.parts;

import gov.nasa.arc.verve.common.VerveUserData;
import gov.nasa.arc.verve.common.ardor3d.interact.VerveInteractManagers;
import gov.nasa.arc.verve.common.ardor3d.interact.VerveInteractWidgets;
import gov.nasa.arc.verve.common.interact.VerveInteractable;
import gov.nasa.arc.verve.robot.AbstractRobot;
import gov.nasa.arc.verve.robot.freeflyer.utils.ContextNames;
import gov.nasa.arc.verve.robot.freeflyer.utils.IPreviewMovedListener;
import gov.nasa.arc.verve.robot.freeflyer.utils.MathHelp;
import gov.nasa.arc.verve.robot.parts.AbstractRobotPart;
import gov.nasa.rapid.v2.framestore.ConvertUtils;
import gov.nasa.rapid.v2.framestore.EulerAngles;
import gov.nasa.rapid.v2.framestore.ReadOnlyEulerAngles;

import org.eclipse.e4.core.contexts.IEclipseContext;

import com.ardor3d.math.Matrix3;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyMatrix3;
import com.ardor3d.math.type.ReadOnlyTransform;
import com.ardor3d.math.type.ReadOnlyVector3;
import com.ardor3d.scenegraph.Node;

public class RobotPartDraggablePreview extends AbstractRobotPart implements LiveTeleopVerifierListener {
	protected FreeFlyerBasicModel m_preview = null;
	protected double displayX, displayY, displayZ;
	protected int displayRoll, displayPitch, displayYaw;
	boolean draggablePreviewVisible = true;
	protected IEclipseContext context;
	protected IPreviewMovedListener previewMovedListener;

	protected int roundTo = 1;// cm to round to
	protected float halfRoundTo = 0.5f;
	protected boolean hasBeenDraggedRoll = false; // if it was just dragged, ignore text box binding for a bit.
	protected boolean hasBeenDraggedPitch = false;
	protected boolean hasBeenDraggedYaw = false;
	protected boolean hasBeenDraggedX = false;
	protected boolean hasBeenDraggedY = false;
	protected boolean hasBeenDraggedZ = false;
	
	public RobotPartDraggablePreview(String partName, AbstractRobot parent) {
		super(partName, parent);
	}

	public void setContext(IEclipseContext context) {
		this.context = context;
	}

	@Override
	public void connectTelemetry() {
		// unneeded; we listen to telemetry provider
	}

	@Override
	public void disconnectTelemetry() {
		// unneeded; we listen to telemetry provider
	}

	// TeleopControlPanel resets the Bookmark combo whenever the preview moves
	public void setPreviewMovedListener(IPreviewMovedListener previewMovedListener) {
		this.previewMovedListener = previewMovedListener;
	}

	protected String getNodeName() {
		return getRobot().getName() + ":DraggablePreview";
	}
	
	@Override
	public void attachToNodesIn(Node model) throws IllegalStateException {
		m_node = new Node();
		m_preview = new FreeFlyerBasicModel();

		getRobot().getRobotNode().getConceptsNode().attachChild(m_node);

		m_node.attachChild(m_preview);
		m_node.updateWorldBound(false);

		VerveUserData.setInteractable(m_node, createInteractable());
		VerveUserData.setCameraFollowable(m_node, false);
	}

	public void attachToThisNode(Node input) {
		input.attachChild(m_node);
	}

	@Override
	public void handleFrameUpdate(long currentTime) { //
	}

	@Override
	public void reset() {
		// TODO reset draggable positions programmatically
	}
	
	protected VerveInteractable createInteractable() {
		return new VerveInteractable() {
			{ 
				m_preferredWidgets = new String[] { VerveInteractWidgets.MoveXYZRotXYZ }; 
			}

			@Override
			public String getDisplayString(ReadOnlyTransform transform) {
				ReadOnlyVector3 xyz = transform.getTranslation();
				translateFromDrag(xyz);
				rotateFromDrag(transform.getMatrix());
//				return String.format("xyz=[ %.2f, %.2f, %.2f]", xyz.getX(), xyz.getY(), xyz.getZ());
				return String.format("rpy= "+displayRoll + ", " + displayPitch + ", " + displayYaw);
			}
		};
	}
	
	protected void translateFromDrag(ReadOnlyVector3 vec) {
		if(displayX !=  round(vec.getX())) {
			hasBeenDraggedX = true;
			displayX = round(vec.getX());
		}
		if(displayY != round(vec.getY())) {
			hasBeenDraggedY = true;
			displayY = round(vec.getY());
		}
		if(displayZ != round(vec.getZ())) {
			hasBeenDraggedZ = true;
			displayZ = round(vec.getZ());
		}

		if(context != null) {
			context.set(ContextNames.TELEOP_TRANSLATION, null);
			context.set(ContextNames.TELEOP_TRANSLATION, vec); // tell LiveTeleopVerifier to check again because we just moved
		} 
	}
	protected void rotateFromDrag(ReadOnlyMatrix3 m33) {
		// receive matrix from VERVE
		Vector3 v = MathHelp.findZYXEulerAngles(m33);
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
			context.set(ContextNames.TELEOP_ROTATION_RADIANS, null);
			Vector3 rotate = new Vector3(
					A, B, C);
			context.set(ContextNames.TELEOP_ROTATION_RADIANS, rotate); // tell LiveTeleopVerifier to check again because we just moved
		}
	}

	protected void translateFromDisplayText() {
		Vector3 move = new Vector3(displayX, displayY, displayZ);

		if(context != null) {
			// tell LiveTeleopVerifier to check again because we just moved
			// this also updates TeleopControlPanel
			context.set(ContextNames.TELEOP_TRANSLATION, move); 
		}

		m_node.setTranslation(displayX, displayY, displayZ);
		
		if(draggablePreviewVisible) {
			VerveInteractManagers.INSTANCE.setSpatialTarget(m_node);
		}
	}

	protected void rotateFromDisplayText() {
		double rollRadians = displayRoll * Math.PI / 180.0;
		double pitchRadians = displayPitch * Math.PI / 180.0;
		double yawRadians = displayYaw * Math.PI / 180.0;

		EulerAngles ea = new EulerAngles(ReadOnlyEulerAngles.Type.ZYXr, yawRadians, pitchRadians, rollRadians);
		Matrix3 m33 = ConvertUtils.toRotationMatrix(ea, null);
		m_node.setRotation(m33);

		Vector3 move = new Vector3(rollRadians, pitchRadians, yawRadians);
		
		if(context != null) {
			context.set(ContextNames.TELEOP_ROTATION_RADIANS, move);  // tell LiveTeleopVerifier to check again because we just moved
		}

		if(draggablePreviewVisible) {
			VerveInteractManagers.INSTANCE.setSpatialTarget(m_node);
		}
	}

	// need getBlah, setBlah for databinding!
	public void setX(double x) {
		if(hasBeenDraggedX) {
			hasBeenDraggedX = false;
			return;
		}
		this.displayX = x;
		translateFromDisplayText();
		if(previewMovedListener != null) {
			previewMovedListener.onPreviewMoved();
		}
	}

	public void setY(double y) {
		if(hasBeenDraggedY) {
			hasBeenDraggedY = false;
			return;
		}
		this.displayY = y;
		translateFromDisplayText();
		if(previewMovedListener != null) {
			previewMovedListener.onPreviewMoved();
		}
	}

	public void setZ(double z) {
		if(hasBeenDraggedZ) {
			hasBeenDraggedZ = false;
			return;
		}
		this.displayZ = z;
		translateFromDisplayText();
		if(previewMovedListener != null) {
			previewMovedListener.onPreviewMoved();
		}
	}

	public double getX() {
		return displayX;
	}

	public double getY() {
		return displayY;
	}

	public double getZ() {
		return displayZ;
	}

	public void setRoll(int roll) {
		if(hasBeenDraggedRoll) {
			hasBeenDraggedRoll = false;
			return;
		}
		this.displayRoll = roll;
		rotateFromDisplayText();
		if(previewMovedListener != null) {
			previewMovedListener.onPreviewMoved();
		}
	}

	public void setPitch(int pitch) {
		if(hasBeenDraggedPitch) {
			hasBeenDraggedPitch = false;
			return;
		}
		this.displayPitch = pitch;
		rotateFromDisplayText();
		if(previewMovedListener != null) {
			previewMovedListener.onPreviewMoved();
		}
	}

	public void setYaw(int yaw) {
		if(hasBeenDraggedYaw) {
			hasBeenDraggedYaw = false;
			return;
		}
		this.displayYaw = yaw;
		rotateFromDisplayText();
		if(previewMovedListener != null) {
			previewMovedListener.onPreviewMoved();
		}	
	}

	public int getRoll() {
		return displayRoll;
	}

	public int getPitch() {
		return displayPitch;
	}

	public int getYaw() {
		return displayYaw;
	}

	protected void showAlarmColor() {
		m_preview.showAlarmColor();
	}

	protected void hideAlarmColor() {
		m_preview.hideAlarmColor();
	}

	public boolean isDraggablePreviewVisible() {
		return draggablePreviewVisible;
	}

	public void showDraggablePreview() {
		draggablePreviewVisible = true;
		setVisible(true);
		VerveInteractManagers.INSTANCE.setSpatialTarget(m_node);
	}

	public void hideDraggablePreview() {
		draggablePreviewVisible = false;
		setVisible(false);
		VerveInteractManagers.INSTANCE.setSpatialTarget(null);
	}

	public void allowAbsoluteMovement() {
		hideAlarmColor();
	}

	public void disallowAbsoluteMovement() {
		showAlarmColor();
	}

	// for databinding
	public void setDraggablePreviewVisible(boolean draggablePreviewVisible) {
		this.draggablePreviewVisible = draggablePreviewVisible;
		if(draggablePreviewVisible) {
			setVisible(true);
			VerveInteractManagers.INSTANCE.setSpatialTarget(m_node);
		} else {
			setVisible(false);
			VerveInteractManagers.INSTANCE.setSpatialTarget(null);
		}
	}

	public void allowRotation() { /**/	}
	public void disallowRotation() { /**/ }

	@Override
	public String toString() {
		return "node = " + m_node;
	}
	
	protected double round(double d) {
		int bigD = (int) (d * 100);
		int remainder = bigD % roundTo;
		if(0 < remainder) {
			if(remainder < halfRoundTo) {
				bigD -= remainder;
			} else {
				bigD += (roundTo - remainder);
			}
		} else {
			if(remainder < -halfRoundTo) {
				bigD -= (roundTo + remainder);
			} else {
				bigD -= remainder;
			}
		}
		double ret = bigD/100.0;

		return ret;
	}

	@Override
	public void allowRelativeMovement() {
		// ignore, we are not relative
	}

	@Override
	public void disallowRelativeMovement() {
		// ignore, we are not relative
	}
}
