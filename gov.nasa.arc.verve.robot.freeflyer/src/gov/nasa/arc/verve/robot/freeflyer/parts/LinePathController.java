package gov.nasa.arc.verve.robot.freeflyer.parts;

import com.ardor3d.scenegraph.controller.SpatialController;


/**
 * SpatialController will call update on the path every frame. 
 * We make all changes to geometry in this callback to avoid threading problems. 
 */
public class LinePathController implements SpatialController<LinePath> {
	public void update(double time, LinePath caller) {
		// the time passed in the Controller interface is the time between
		// frames, which is not what we want. So grab system time. 
		long currentTime = System.currentTimeMillis();
		caller.handleUpdate(currentTime);
	}
}