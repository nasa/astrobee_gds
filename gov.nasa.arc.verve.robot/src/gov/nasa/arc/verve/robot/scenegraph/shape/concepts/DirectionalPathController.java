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
package gov.nasa.arc.verve.robot.scenegraph.shape.concepts;

import com.ardor3d.scenegraph.controller.SpatialController;

/**
 * SpatialController will call update on the path every frame. 
 * We make all changes to geometry in this callback to avoid threading problems. 
 */
public class DirectionalPathController implements SpatialController<DirectionalPath> {
	public void update(double time, DirectionalPath caller) {
		// the time passed in the Controller interface is the time between
		// frames, which is not what we want. So grab system time. 
		long currentTime = System.currentTimeMillis();
		caller.handleUpdate(currentTime);
	}
}
