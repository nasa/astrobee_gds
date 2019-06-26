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
package gov.nasa.arc.verve.robot.parts;

import com.ardor3d.scenegraph.Node;

import gov.nasa.arc.verve.robot.AbstractRobot;
import gov.nasa.arc.verve.robot.exception.TelemetryException;

/**
 * basic interface for scenegraph -> telemetry glue for 
 * robot visualization parts
 */
public interface IRobotPart {
    public String        getPartName();
    public AbstractRobot getRobot();
    public Node          getNode();
    
    public void connectTelemetry() throws TelemetryException;
    public void disconnectTelemetry() throws TelemetryException;
    
    public void attachToNodesIn(Node model) throws TelemetryException, IllegalStateException;
    
    public void handleFrameUpdate(long currentTime);
    public void reset();
    
    public boolean isVisible();
    public void setVisible(boolean state);
}
