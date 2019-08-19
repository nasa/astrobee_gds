package gov.nasa.arc.verve.robot.freeflyer.parts;

public interface LiveTeleopVerifierListener {

	public void allowAbsoluteMovement();
	public void disallowAbsoluteMovement();
	
	public void allowRelativeMovement();
	public void disallowRelativeMovement();

}
