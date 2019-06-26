package gov.nasa.arc.verve.robot.freeflyer.camera;

import gov.nasa.arc.irg.plan.ui.io.ConfigFileWrangler;
import gov.nasa.arc.irg.plan.ui.io.GenericJsonConfigLoader;

public class CameraConfigListLoader extends GenericJsonConfigLoader {
	private static CameraConfigList cameraConfigList;

	static {
		classToLoad = CameraConfigList.class;
	}

	public static CameraConfigList getStandardConfig() throws Exception {
		if(cameraConfigList == null) {
			if(cameraConfigList == null) {
				cameraConfigList = (CameraConfigList)getConfig(ConfigFileWrangler.getInstance().getCameraConfigPath());
			}
		}
		return cameraConfigList;
	}

	public static CameraConfigList loadFromFile(String filename) throws Exception {
		return (CameraConfigList) loadFromFileGeneric(filename);
	}
}
