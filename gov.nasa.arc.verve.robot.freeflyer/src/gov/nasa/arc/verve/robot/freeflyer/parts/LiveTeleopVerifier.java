package gov.nasa.arc.verve.robot.freeflyer.parts;

import gov.nasa.arc.irg.freeflyer.rapid.state.AstrobeeStateManager;
import gov.nasa.arc.irg.plan.ui.plancompiler.PlanCompiler;
import gov.nasa.arc.irg.plan.ui.plancompiler.TrajectoryBoundsCheck;
import gov.nasa.arc.verve.robot.freeflyer.utils.ContextNames;
import gov.nasa.rapid.v2.e4.Rapid;
import gov.nasa.rapid.v2.e4.message.MessageType;
import gov.nasa.rapid.v2.framestore.ConvertUtils;
import gov.nasa.rapid.v2.framestore.EulerAngles;
import gov.nasa.rapid.v2.framestore.ReadOnlyEulerAngles;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.MApplication;

import com.ardor3d.math.Matrix3;
import com.ardor3d.math.Quaternion;
import com.ardor3d.math.Vector3;

/** Checks current location and proposed teleop movement of Freeflyer against TrajectoryBoundsCheck 
 *  in PlanCompiler.  Notifies registered LiveTeleopVerifierListener if movement is allowed or not. */
@Creatable
@Singleton
public class LiveTeleopVerifier {
	protected String participantId = Rapid.PrimaryParticipant;
	protected MessageType[] sampleType;
	protected MessageType configType;
	protected List<LiveTeleopVerifierListener> listeners = new ArrayList<LiveTeleopVerifierListener>();
	double displayX = 0, displayY = 0, displayZ = 0;
	double displayRoll = 0, displayPitch = 0, displayYaw = 0;
	double relativeDisplayX = 0, relativeDisplayY = 0, relativeDisplayZ = 0;
	double relativeDisplayRoll = 0, relativeDisplayPitch = 0, relativeDisplayYaw = 0;
	boolean absoluteTranslationAllowed = false, absoluteRotationAllowed = false;
	boolean relativeTranslationAllowed = false, relativeRotationAllowed = false;
	AstrobeeStateManager astrobeeStateManager;

	boolean checkKeepouts = true;
	
	@Inject
	public LiveTeleopVerifier(IEclipseContext context) {
		MApplication application = context.get(MApplication.class);
		IEclipseContext topContext = application.getContext();
		topContext.set(LiveTeleopVerifier.class, this);
	}

	@Inject @Optional 
	public void updateAbsoluteTeleopTranslation(@Named(ContextNames.TELEOP_TRANSLATION) Vector3 teleop) {
		if(teleop == null) {
			return;
		}
		displayX = teleop.getX();
		displayY = teleop.getY();
		displayZ = teleop.getZ();
		checkAbsolutePositionAndNotifyListeners();
	}
	
	@Inject @Optional 
	public void updateRelativeTeleopTranslation(
			@Named(ContextNames.RELATIVE_TELEOP_TRANSLATION) Vector3 teleop) {
		if(teleop == null) {
			return;
		}
		relativeDisplayX = teleop.getX();
		relativeDisplayY = teleop.getY();
		relativeDisplayZ = teleop.getZ();
		checkRelativePositionAndNotifyListeners();
	}
	
	@Inject
	@Optional
	public void acceptAstrobeeStateManager(AstrobeeStateManager asm) {
		astrobeeStateManager = asm;
	}


	@Inject @Optional
	public void updateCheckKeepouts(@Named(ContextNames.CHECK_KEEPOUTS_ENABLED) boolean check) {
		checkKeepouts = check;
		checkAbsolutePositionAndNotifyListeners();
		checkRelativePositionAndNotifyListeners();
	}

	// TODO this currently only checks that the center point is legal, not the corners (ie, it's pointless)
	@Inject @Optional
	public void updateAbsoluteTeleopRotation(@Named(ContextNames.TELEOP_ROTATION_RADIANS) Vector3 teleop) {
		if(teleop == null) {
			return;
		}
		displayRoll = teleop.getX();
		displayPitch = teleop.getY();
		displayYaw = teleop.getZ();
		checkAbsolutePositionAndNotifyListeners();
	}
	
	public synchronized void checkAbsolutePositionAndNotifyListeners() {
		boolean astrobeeIsSafe;
		astrobeeIsSafe = absoluteCoordinatesAreSafe();
		
		if(!checkKeepouts) {
			astrobeeIsSafe = true;
		}

		if(astrobeeIsSafe) {
			allowAbsoluteMovement();
		} else {
			disallowAbsoluteMovement();
		}
	}

	public synchronized void checkRelativePositionAndNotifyListeners() {
		boolean astrobeeIsSafe;
		astrobeeIsSafe = relativeCoordinatesAreSafe();
		
		if(!checkKeepouts) {
			astrobeeIsSafe = true;
		}

		if(astrobeeIsSafe) {
			allowRelativeMovement();
		} else {
			disallowRelativeMovement();
		}
	}
	
	private boolean relativeCoordinatesAreSafe() {
		TrajectoryBoundsCheck tbc = PlanCompiler.getTrajectoryBoundsCheck();
		Vector3 beePosition = astrobeeStateManager.getAstrobeePosition();
		Quaternion currentQ = astrobeeStateManager.getAstrobeeOrientation();
		
		EulerAngles ea = EulerAngles.fromDegrees(ReadOnlyEulerAngles.Type.ZYXr,
				displayYaw, displayPitch, displayRoll);
		Matrix3 m33 = ConvertUtils.toRotationMatrix(ea, null);
		Quaternion deltaQ = new Quaternion();
		deltaQ.fromRotationMatrix(m33);

		Quaternion totalQuat = currentQ.multiply(deltaQ, null);
		
		boolean safe = tbc.isAstrobeeSafeQuaternion(
				beePosition.getX() + relativeDisplayX, 
				beePosition.getY() + relativeDisplayY, 
				beePosition.getZ() + relativeDisplayZ,
				totalQuat);
		
		return safe;
	}


	private boolean absoluteCoordinatesAreSafe() {
		TrajectoryBoundsCheck tbc = PlanCompiler.getTrajectoryBoundsCheck();
		return tbc.isAstrobeeSafeDegrees(displayX, displayY, displayZ,
				displayRoll, displayPitch, displayYaw);
	}

	private void allowAbsoluteMovement() {
		if(!absoluteTranslationAllowed) {
			absoluteTranslationAllowed = true;
			notifyListenersAllowedAbsoluteMovementChanged();
		}
		if(!absoluteRotationAllowed) {
			absoluteRotationAllowed = true;
			notifyListenersAllowedAbsoluteMovementChanged();
		}
	}

	private void disallowAbsoluteMovement() {
		if(absoluteTranslationAllowed) {
			absoluteTranslationAllowed = false;
			notifyListenersAllowedAbsoluteMovementChanged();
		}
		if(absoluteRotationAllowed) {
			absoluteRotationAllowed = false;
			notifyListenersAllowedAbsoluteMovementChanged();
		}
	}
	
	private void allowRelativeMovement() {
		if(!relativeTranslationAllowed) {
			relativeTranslationAllowed = true;
			notifyListenersAllowedRelativeMovementChanged();
		}
		if(!relativeRotationAllowed) {
			relativeRotationAllowed = true;
			notifyListenersAllowedRelativeMovementChanged();
		}
	}

	private void disallowRelativeMovement() {
		if(relativeTranslationAllowed) {
			relativeTranslationAllowed = false;
			notifyListenersAllowedRelativeMovementChanged();
		}
		if(relativeRotationAllowed) {
			relativeRotationAllowed = false;
			notifyListenersAllowedRelativeMovementChanged();
		}
	}

	private void notifyListenersAllowedAbsoluteMovementChanged() {
		for(LiveTeleopVerifierListener listener : listeners) {
			if(absoluteTranslationAllowed) {
				listener.allowAbsoluteMovement();
			} else {
				listener.disallowAbsoluteMovement();
			}
		}
	}
	
	private void notifyListenersAllowedRelativeMovementChanged() {
		for(LiveTeleopVerifierListener listener : listeners) {
			if(relativeTranslationAllowed) {
				listener.allowRelativeMovement();
			} else {
				listener.disallowRelativeMovement();
			}
		}
	}

	public void addListener(LiveTeleopVerifierListener l) {
		if(!listeners.contains(l)) {
			listeners.add(l);
			notifyListenersAllowedAbsoluteMovementChanged();
			notifyListenersAllowedRelativeMovementChanged();
		}
	}

	public void removeListener(LiveTeleopVerifierListener l) {
		listeners.remove(l);
	}

}
