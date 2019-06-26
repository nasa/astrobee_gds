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

import com.ardor3d.framework.Canvas;
import com.ardor3d.framework.Updater;
import com.ardor3d.util.Timer;

public interface IFrameHandler {

    Timer getTimer();
    void updateFrame();
    void addUpdater(final Updater updater);
    boolean removeUpdater(final Updater updater);
    void addCanvas(final Canvas canvas);
    boolean removeCanvas(final Canvas canvas);

}
