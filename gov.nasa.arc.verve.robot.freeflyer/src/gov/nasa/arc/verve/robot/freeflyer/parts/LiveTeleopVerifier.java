package gov.nasa.arc.verve.robot.freeflyer.parts;

import gov.nasa.arc.irg.plan.ui.plancompiler.PlanCompiler;
import gov.nasa.arc.irg.plan.ui.plancompiler.TrajectoryBoundsCheck;
import gov.nasa.arc.verve.robot.freeflyer.utils.ContextNames;
import gov.nasa.rapid.v2.e4.Rapid;
import gov.nasa.rapid.v2.e4.message.MessageType;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.MApplication;

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
	boolean translationAllowed = false, rotationAllowed = false;
	
	boolean checkKeepouts = true;
	
	@Inject
	public LiveTeleopVerifier(IEclipseContext context) {
		MApplication application = context.get(MApplication.class);
		IEclipseContext topContext = application.getContext();
		topContext.set(LiveTeleopVerifier.class, this);
	}

	@Inject @Optional 
	public void updateTeleopTranslation(@Named(ContextNames.TELEOP_TRANSLATION) Vector3 teleop) {
		if(teleop == null) {
			return;
		}
		displayX = teleop.getX();
		displayY = teleop.getY();
		displayZ = teleop.getZ();
		checkPositionAndNotifyListeners();
	}
	
	@Inject @Optional
	public void updateCheckKeepouts(@Named(ContextNames.CHECK_KEEPOUTS_ENABLED) boolean check) {
		checkKeepouts = check;
		checkPositionAndNotifyListeners();
	}

	// TODO this currently only checks that the center point is legal, not the corners (ie, it's pointless)
	@Inject @Optional
	public void updateTeleopRotation(@Named(ContextNames.TELEOP_ROTATION_RADIANS) Vector3 teleop) {
		if(teleop == null) {
			return;
		}
		displayRoll = teleop.getX();
		displayPitch = teleop.getY();
		displayYaw = teleop.getZ();
		checkPositionAndNotifyListeners();
	}

	public synchronized void checkPositionAndNotifyListeners() {
		boolean astrobeeIsSafe;
		astrobeeIsSafe = absoluteCoordinatesAreSafe();
		
		if(!checkKeepouts) {
			astrobeeIsSafe = true;
		}

		if(astrobeeIsSafe) {
			allowMovement();
		} else {
			disallowMovement();
		}
	}

	private boolean absoluteCoordinatesAreSafe() {
		TrajectoryBoundsCheck tbc = PlanCompiler.getTrajectoryBoundsCheck();
		return tbc.isAstrobeeSafeDegrees(displayX, displayY, displayZ,
				displayRoll, displayPitch, displayYaw);
	}

	private void allowMovement() {
		if(!translationAllowed) {
			translationAllowed = true;
			notifyListenersAllowedMovementChanged();
		}
		if(!rotationAllowed) {
			rotationAllowed = true;
			notifyListenersAllowedMovementChanged();
		}
	}

	private void disallowMovement() {
		if(translationAllowed) {
			translationAllowed = false;
			notifyListenersAllowedMovementChanged();
		}
		if(rotationAllowed) {
			rotationAllowed = false;
			notifyListenersAllowedMovementChanged();
		}
	}

	private void notifyListenersAllowedMovementChanged() {
		for(LiveTeleopVerifierListener listener : listeners) {
			if(translationAllowed) {
				listener.allowMovement();
			} else {
				listener.disallowMovement();
			}
		}
	}

	public void addListener(LiveTeleopVerifierListener l) {
		if(!listeners.contains(l)) {
			listeners.add(l);
			notifyListenersAllowedMovementChanged();
		}
	}

	public void removeListener(LiveTeleopVerifierListener l) {
		listeners.remove(l);
	}

}
