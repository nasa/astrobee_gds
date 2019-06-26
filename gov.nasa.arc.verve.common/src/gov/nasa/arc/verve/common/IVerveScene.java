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
package gov.nasa.arc.verve.common;

import gov.nasa.arc.verve.ardor3d.extension.interact.InteractManager;

import java.io.File;

import com.ardor3d.framework.Scene;
import com.ardor3d.intersection.PickResults;
import com.ardor3d.math.Ray3;
import com.ardor3d.scenegraph.Node;

public interface IVerveScene extends Scene {
    /** shared scenario root */
    public Node    getRoot();
    /** local root node for UI elements */
    public Node    getUiRoot();
    /** local root node for markup elements */
    public Node    getMarkupRoot();
    
    public void    setInteractManager(InteractManager mgr);
    public InteractManager getInteractManager();
    
    public void    addScenePickListener(ScenePickListener listener);
    
    // XXX this is too specific; only here temporarily for convenience
    public boolean isMeasureMode();
    public void    setMeasureMode(boolean status);
    public void    toggleMeasureMode();
    
    public void    toggleShowNormals();
    
    public void    setShadowMapping(boolean state);
    public boolean isShadowMapping();
    public void    toggleShadowMapping();
    
    public void    setScreenShot(int numShots, File directory);

    /** current time at the beginning of every frame */
    public long    currentTimeMillis();
    
    public PickResults doPick(Ray3 pickRay, int x, int y);
    public void showCrosshair(int x, int y, int numFrames);
}
