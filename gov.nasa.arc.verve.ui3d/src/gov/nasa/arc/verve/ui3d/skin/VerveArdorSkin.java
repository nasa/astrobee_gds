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
package gov.nasa.arc.verve.ui3d.skin;

import org.apache.log4j.Logger;

import com.ardor3d.extension.ui.skin.generic.GenericSkin;
import com.ardor3d.image.Texture2D;
import com.ardor3d.image.Texture.MagnificationFilter;
import com.ardor3d.image.Texture.MinificationFilter;
import com.ardor3d.image.Texture.WrapMode;
import com.ardor3d.util.TextureManager;
import com.ardor3d.util.resource.URLResourceSource;

public class VerveArdorSkin extends GenericSkin {
    private static Logger logger = Logger.getLogger(VerveArdorSkin.class);

    public VerveArdorSkin() {
        try {
            _sharedTex = getTex("verveHudArdorSkin.png");
        } 
        catch (final Exception e) {
            logger.warn("", e);
        }
    }
    
    public static Texture2D getTex(String imageName) {
        Texture2D texture = null;
        URLResourceSource rs = null;
        rs = new URLResourceSource(VerveArdorSkin.class.getResource(imageName));
        texture = (Texture2D)TextureManager.load(rs, MinificationFilter.BilinearNoMipMaps, false);
        texture.setMagnificationFilter(MagnificationFilter.Bilinear);
        texture.setWrap(WrapMode.BorderClamp);
        return texture;
    }

}
