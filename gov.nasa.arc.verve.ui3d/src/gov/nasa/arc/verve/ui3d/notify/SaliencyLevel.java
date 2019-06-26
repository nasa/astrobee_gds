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
package gov.nasa.arc.verve.ui3d.notify;

import gov.nasa.util.Colors;

public enum SaliencyLevel {    
    Alarm    (Colors.X11.LightCoral,     Colors.X11.White),
    Alert    (Colors.X11.Khaki,          Colors.X11.White),
    Urgent   (Colors.X11.White,          Colors.X11.White),
    Important(Colors.X11.LightSteelBlue, Colors.X11.White),
    Notice   (Colors.X11.LightGray,      Colors.X11.White),
    LogNotice(Colors.X11.Silver,         Colors.X11.Gray);
    
    public Colors backColor;
    public Colors foreColor;
    
    SaliencyLevel(Colors backColor, Colors foreColor) {
        this.backColor = backColor;
        this.foreColor = foreColor;
    }
}
