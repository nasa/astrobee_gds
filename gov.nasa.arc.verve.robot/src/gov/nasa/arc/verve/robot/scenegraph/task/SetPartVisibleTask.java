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
package gov.nasa.arc.verve.robot.scenegraph.task;

import gov.nasa.arc.verve.robot.parts.IRobotPart;

import java.util.concurrent.Callable;

/**
 *
 * Set visibility of a robot part
 */
public class SetPartVisibleTask implements Callable<Void> {
    final IRobotPart m_part;
    final boolean    m_visible;

    public SetPartVisibleTask(IRobotPart part, boolean visible) {
        m_part = part;
        m_visible = visible;
    }
    
    public Void call() throws Exception {
        m_part.setVisible(m_visible);
        return null;
    }
}
