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
package gov.nasa.arc.verve.common.ardor3d;

import gov.nasa.arc.verve.common.ardor3d.framework.CameraControlUpdater;
import gov.nasa.arc.verve.common.ardor3d.framework.IFrameHandler;
import gov.nasa.arc.verve.common.ardor3d.framework.LogicalLayerUpdater;

public class Ardor3DAndroid extends Ardor3D {

    public Ardor3DAndroid(IFrameHandler fh) {
        timer                = fh.getTimer();
        frameHandler         = fh;
        logicalLayerUpdater  = new LogicalLayerUpdater();
        cameraControlUpdater = new CameraControlUpdater();
    }
    
    @Override
    protected void startRenderThreadImpl() {
        // TODO
    }

    @Override
    @SuppressWarnings("unused")
    protected void stopRenderThreadImpl() throws InterruptedException {
        // TODO 
    }

    @Override
    protected void setPreferenceImpl(String key, String value) {
        // TODO 
    }

    @Override
    protected String getPreferenceImpl(String key) {
        return "";
    }

}
