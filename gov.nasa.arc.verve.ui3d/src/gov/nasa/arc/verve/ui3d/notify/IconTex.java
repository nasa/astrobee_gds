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

import org.apache.log4j.Logger;

import com.ardor3d.extension.ui.util.SubTex;
import com.ardor3d.image.Texture.MagnificationFilter;
import com.ardor3d.image.Texture.MinificationFilter;
import com.ardor3d.image.Texture.WrapMode;
import com.ardor3d.image.Texture2D;
import com.ardor3d.util.TextureManager;
import com.ardor3d.util.resource.URLResourceSource;

public class IconTex {
    private final static Logger logger = Logger.getLogger(IconTex.class);

    public static Texture2D tex;
    
    static {
        try {
            URLResourceSource rs = null;
            String imageName = "Icons16.png";
            rs = new URLResourceSource(IconTex.class.getResource(imageName));
            tex = (Texture2D)TextureManager.load(rs, MinificationFilter.BilinearNoMipMaps, false);
            tex.setMagnificationFilter(MagnificationFilter.NearestNeighbor);
            tex.setWrap(WrapMode.EdgeClamp);
        } 
        catch (final Throwable t) {
            logger.error("failed to load texture", t);
        }
    }
    
    public static enum Icon {
        Plus ( 0, 0, 16,16),
        Minus(16, 0, 16,16)
        ;
        public SubTex subtex;
        Icon(int x, int y, int w, int h) {
            subtex = new SubTex(tex, x, y, w, h);
        }
    }
}
