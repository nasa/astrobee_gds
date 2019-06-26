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
package gov.nasa.arc.verve.ardor3d.e4;

import gov.nasa.arc.verve.ardor3d.e4.framework.CameraControlUpdater;
import gov.nasa.arc.verve.ardor3d.e4.framework.FrameHandlerAdapter;
import gov.nasa.arc.verve.ardor3d.e4.framework.LogicalLayerUpdater;
import gov.nasa.arc.verve.ardor3d.e4.framework.RenderUpdateThread;

import javax.inject.Inject;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.ardor3d.framework.FrameHandler;
import com.ardor3d.util.Timer;

public class Ardor3DEclipseNotPlugin extends Ardor3D {
    RenderUpdateThread      renderUpdateThread = null;
    
    FrameHandler frameHandlerImpl;
    IEclipsePreferences prefs;
	protected IEclipseContext applicationContext;
	Display display;
	
    @Inject
    public Ardor3DEclipseNotPlugin(Display display, @Preference IEclipsePreferences preferences, MApplication mapp) {
        super();
        timer                = new Timer();
        frameHandlerImpl     = new FrameHandler(timer);
        frameHandler         = new FrameHandlerAdapter(frameHandlerImpl);
        logicalLayerUpdater  = new LogicalLayerUpdater();
        cameraControlUpdater = new CameraControlUpdater();
        frameHandler.addUpdater(logicalLayerUpdater);
        frameHandler.addUpdater(cameraControlUpdater);

        prefs = preferences;
        this.display = display;
        applicationContext = mapp.getContext();
        applicationContext.set(Ardor3DEclipseNotPlugin.class, this);
        this.startRenderThreadImpl();
    }
    
    @Override
    public void startRenderThreadImpl() {
        if(renderUpdateThread == null) {
            frameHandler.addUpdater(logicalLayerUpdater);
            frameHandler.addUpdater(cameraControlUpdater);

            renderUpdateThread = new RenderUpdateThread(display, frameHandlerImpl, timer);
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
    
    // TODO Fix preferences later
    @Override
    public void setPreferenceImpl(String key, String value) {
    	prefs.put(key, value);
    }
    @Override
    public String getPreferenceImpl(String key) {
    	String def = "";
    	String it = prefs.get(key, def);
    	
    	// WHAT GOOD IS THAT???
//    	return def;
    	return it;
    }

    // NOTE: BasicVerveScene.currentTimeMillis() should be used instead of System.currentTimeMillis()
    // wherever possible.
}
