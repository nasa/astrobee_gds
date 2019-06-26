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
import gov.nasa.arc.verve.ardor3d.e4.input.control.CamControlType;
import gov.nasa.arc.verve.common.IVerveScene;

import com.ardor3d.framework.Canvas;
import com.ardor3d.input.PhysicalLayer;
import com.ardor3d.input.logical.LogicalLayer;

public interface IVerveCanvasView {
	Canvas                getCanvas();
	IVerveScene           getScene();
	void                  selectCameraControl(CamControlType cameraType);
	AbstractCamControl    getCameraControl();
	
    PhysicalLayer         getPhysicalLayer();
    LogicalLayer          getLogicalLayer();

    void                  addListener(IVerveCanvasListener listener);
    void                  removeListener(IVerveCanvasListener listener);

    int                   getCanvasWidth();
    int                   getCanvasHeight();
    
	//void                  interestPointChanged(AbstractCamControl changer, InterestPointProvider ipp);
}
