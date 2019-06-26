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
package gov.nasa.arc.verve.robot.scenegraph.shape.sensors;

import com.ardor3d.image.Texture.MagnificationFilter;
import com.ardor3d.image.Texture.MinificationFilter;
import com.ardor3d.image.Texture.WrapMode;
import com.ardor3d.image.Texture2D;
import com.ardor3d.util.TextureManager;
import com.ardor3d.util.resource.URLResourceSource;

public class RobotSensors {
    public static Texture2D getTex(String imageName) {
        Texture2D texture = null;
        URLResourceSource rs = null;
        rs = new URLResourceSource(RobotSensors.class.getResource(imageName));
        texture = (Texture2D)TextureManager.load(rs, MinificationFilter.Trilinear, false);
        texture.setMagnificationFilter(MagnificationFilter.Bilinear);
        texture.setWrap(WrapMode.EdgeClamp);
        return texture;
    }

}
