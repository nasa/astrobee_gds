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
package gov.nasa.arc.verve.common.ardor3d.shape.grid;

import com.ardor3d.image.Texture2D;
import com.ardor3d.image.Texture.MagnificationFilter;
import com.ardor3d.image.Texture.MinificationFilter;
import com.ardor3d.image.Texture.WrapMode;
import com.ardor3d.util.TextureManager;
import com.ardor3d.util.resource.URLResourceSource;

public class GridTexture {

    public enum Style {
        None,
        ThickAlphaOutline,
        ThickBlackOutline,
        ThickGreyOutline,
        ThickWhiteOutline,
        ThinAlphaOutline,
        ThinBlackOutline,
        ThinGreyOutline,
        ThinWhiteOutline
    }

    public static Texture2D load(Style style) {
        return load(style, MinificationFilter.Trilinear, MagnificationFilter.NearestNeighbor, WrapMode.Repeat, 0.5f);
    }
    
    public static Texture2D load(Style style, MinificationFilter minFilter, MagnificationFilter magFilter, WrapMode wrapMode, float anisoPercent) {
        Texture2D texture = null;
        URLResourceSource rs = null;
        rs = new URLResourceSource(GridTexture.class.getResource(style.toString()+".png"));
        texture = (Texture2D)TextureManager.load(rs, minFilter, false);
        texture.setMagnificationFilter(magFilter);
        texture.setWrap(wrapMode);
        texture.setAnisotropicFilterPercent(anisoPercent);
        return texture;
    }
}
