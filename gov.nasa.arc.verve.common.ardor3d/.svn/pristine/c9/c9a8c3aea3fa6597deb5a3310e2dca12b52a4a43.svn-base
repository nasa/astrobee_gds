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
package gov.nasa.arc.verve.common.ardor3d.framework.screenshot;


import gov.nasa.arc.verve.common.IScreenShotDelegate;

import java.io.File;

import com.ardor3d.renderer.Renderer;

public class ScreenShotDelegateEclipsePlugin implements IScreenShotDelegate {

    @Override
    public void doScreenShot(Renderer renderer, File screenDir, String basename, String fileExt) {
        ScreenShotImageExporter screenExporter = new ScreenShotImageExporter(screenDir, 
                                                                             basename, 
                                                                             fileExt, 
                                                                             false);
        VerveScreenExporter.exportCurrentScreen(renderer, screenExporter);
    }

}
