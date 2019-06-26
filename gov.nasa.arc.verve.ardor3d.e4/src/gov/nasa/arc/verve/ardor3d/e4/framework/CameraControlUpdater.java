/*******************************************************************************
 * Copyright (c) 2013 United States Government as represented by the 
 * Administrator of the National Aeronautics and Space Administration. 
 * All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package gov.nasa.arc.verve.ardor3d.e4.framework;

import gov.nasa.arc.verve.ardor3d.e4.input.control.AbstractCamControl;

import java.util.LinkedList;
import java.util.List;

import com.ardor3d.framework.Updater;
import com.ardor3d.util.ReadOnlyTimer;

public class CameraControlUpdater implements Updater {
	private final List<AbstractCamControl> cameraControls = new LinkedList<AbstractCamControl>();
	
	public CameraControlUpdater() {
	}
	
	/**
	 * register a CameraControl to be updated. If cameraControl already exists in the list,
	 * it will be made the first element
	 * @param cameraControl
	 */
	public void registerCameraControl(AbstractCamControl cameraControl) {
	    if(cameraControls.contains(cameraControl))
	        cameraControls.remove(cameraControl);
		cameraControls.add(0, cameraControl);
	}
	
	public boolean removeCameraControl(AbstractCamControl cameraControl) {
		return cameraControls.remove(cameraControl);
	}
	
	public List<AbstractCamControl> getCameraControls() {
	   return cameraControls;
	}
	
    @Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

    @Override
	public void update(ReadOnlyTimer timer) {
		for(AbstractCamControl cameraControl : cameraControls) {
			cameraControl.handleFrameUpdate(timer.getTimePerFrame());
		}
	}

}
