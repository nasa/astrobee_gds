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

import com.ardor3d.extension.ui.util.SubTex;
import com.ardor3d.image.Texture.MagnificationFilter;
import com.ardor3d.image.Texture.MinificationFilter;
import com.ardor3d.image.Texture.WrapMode;
import com.ardor3d.image.Texture2D;
import com.ardor3d.util.TextureManager;
import com.ardor3d.util.resource.URLResourceSource;

public class NotifySubTex {
    protected static Texture2D texture;
    
    static {
        URLResourceSource rs = null;
        rs = new URLResourceSource(NotifySubTex.class.getResource("NotifyIcons.png"));
        texture = (Texture2D)TextureManager.load(rs, MinificationFilter.BilinearNoMipMaps, false);
        texture.setMagnificationFilter(MagnificationFilter.Bilinear);
        texture.setWrap(WrapMode.EdgeClamp);
        texture.setAnisotropicFilterPercent(0);
    }
    
    public static final SubTex ALARM     = new SubTex(texture,   0,   0, 64, 64);
    public static final SubTex ALERT     = new SubTex(texture,  64,   0, 64, 64);
    public static final SubTex URGENT    = new SubTex(texture, 128,   0, 64, 64);
    public static final SubTex IMPORTANT = new SubTex(texture,   0,  64, 64, 64);
    public static final SubTex NOTICE    = new SubTex(texture,  64,  64, 64, 64);
    public static final SubTex LOGNOTICE = new SubTex(texture,   0, 128, 64, 64);
    
    public static SubTex get(SaliencyLevel level) {
        switch(level) {
        case Alarm:     return ALARM;
        case Alert:     return ALERT;
        case Urgent:    return URGENT;
        case Important: return IMPORTANT;
        case Notice:    return NOTICE;
        case LogNotice: return LOGNOTICE;
        }
        return null;
    }
}
