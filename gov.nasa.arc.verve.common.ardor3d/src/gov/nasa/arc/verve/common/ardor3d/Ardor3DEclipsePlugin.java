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
import gov.nasa.arc.verve.common.ardor3d.framework.FrameHandlerAdapter;
import gov.nasa.arc.verve.common.ardor3d.framework.LogicalLayerUpdater;
import gov.nasa.arc.verve.common.ardor3d.framework.RenderUpdateThread;

import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.ardor3d.framework.FrameHandler;
import com.ardor3d.util.Timer;

public class Ardor3DEclipsePlugin extends Ardor3D {
    RenderUpdateThread      renderUpdateThread = null;
    final AbstractUIPlugin  appPlugin;
    
    FrameHandler frameHandlerImpl;

    public Ardor3DEclipsePlugin(AbstractUIPlugin applicationPlugin) {
        super();
        appPlugin            = applicationPlugin;
        timer                = new Timer();
        frameHandlerImpl     = new FrameHandler(timer);
        frameHandler         = new FrameHandlerAdapter(frameHandlerImpl);
        logicalLayerUpdater  = new LogicalLayerUpdater();
        cameraControlUpdater = new CameraControlUpdater();
        frameHandler.addUpdater(logicalLayerUpdater);
        frameHandler.addUpdater(cameraControlUpdater);

        //renderUpdateThread = new RenderUpdateThread(applicationPlugin, frameHandlerImpl, timer);
        //renderUpdateThread.start();
    }
    
    @Override
    public void startRenderThreadImpl() {
        if(renderUpdateThread == null) {
            frameHandler.addUpdater(logicalLayerUpdater);
            frameHandler.addUpdater(cameraControlUpdater);

            renderUpdateThread = new RenderUpdateThread(appPlugin, frameHandlerImpl, timer);
            renderUpdateThread.start();
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void stopRenderThreadImpl() throws InterruptedException {
        if(renderUpdateThread != null) {
            frameHandler.removeUpdater(logicalLayerUpdater);
            frameHandler.removeUpdater(cameraControlUpdater);

            renderUpdateThread.keepGoing.set(false);
            renderUpdateThread.interrupt();
            renderUpdateThread.join(5000);
            if(renderUpdateThread.isAlive()) {
                logger.debug("renderUpdateThread won't die.");
                renderUpdateThread.stop(); // yes, we realize this is discouraged behavior
            }
        }
    }

    @Override
    public void setPreferenceImpl(String key, String value) {
        VerveArdor3dActivator.getDefault().getPreferenceStore().setValue(key, value);
    }
    @Override
    public String getPreferenceImpl(String key) {
        return VerveArdor3dActivator.getDefault().getPreferenceStore().getString(key);
    }

    // NOTE: BasicVerveScene.currentTimeMillis() should be used instead of System.currentTimeMillis()
    // wherever possible.
}
