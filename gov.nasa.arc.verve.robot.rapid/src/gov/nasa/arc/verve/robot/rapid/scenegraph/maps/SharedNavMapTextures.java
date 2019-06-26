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
package gov.nasa.arc.verve.robot.rapid.scenegraph.maps;

import gov.nasa.arc.verve.robot.AbstractRobot;

import com.ardor3d.image.Texture;
import com.ardor3d.image.Texture.MagnificationFilter;
import com.ardor3d.image.Texture.MinificationFilter;
import com.ardor3d.image.Texture.WrapMode;
import com.ardor3d.image.Texture2D;
import com.ardor3d.util.TextureManager;
import com.ardor3d.util.resource.URLResourceSource;

public class SharedNavMapTextures {
    //private static final Logger logger = Logger.getLogger(SharedNavMapTextures.class);

    protected static Texture2D s_cellTex = null;
    protected static Texture2D s_gradientSlope      = null;
    protected static Texture2D s_gradientRedToGreen = null;

    public enum Gradient {
        Autumn,
        BeigeToRed,
        BlueToRedHSVc,
        BlueToRedHSVcc,
        Cool,
        Copper,
        GreenToRedHSVc,
        GreenToRedHSVcc,
        GreyToPink,
        Hot,
        Jet,
        PurpleToCyan,
        Purple0BlueToCyan,
        RedToBeige,
        RedToGreen,
        Red0YellowToGreen,
        Red0YellowToGreenAlpha,
        Spring,
        Striped,
        Summer,
        WhiteDebug,
        Winter,
        ;
        Texture2D texture = null;
    }

    public static Texture2D getGradient(Gradient type) {
        if(type.texture == null) {
            Texture2D texture = null;
            URLResourceSource rs = null;
            String imageName = "NavMapGradient_"+type.name()+".png";
            rs = new URLResourceSource(SharedNavMapTextures.class.getResource(imageName));
            texture = (Texture2D)TextureManager.load(rs, MinificationFilter.BilinearNoMipMaps, false);
            texture.setMagnificationFilter(MagnificationFilter.NearestNeighbor);
            texture.setWrap(WrapMode.EdgeClamp);
            return texture;
        }
        return type.texture;
    }

    public static Texture2D getCellTexture() {
        if(s_cellTex == null) {
            s_cellTex = AbstractRobot.getTex("cell.png",
                                             Texture.WrapMode.Repeat, 
                                             Texture.MinificationFilter.Trilinear, 
                                             Texture.MagnificationFilter.Bilinear, 
                                             0);
        }
        return s_cellTex;
    }



}
