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
package gov.nasa.arc.verve.common.scenario;

import gov.nasa.arc.irg.georef.coordinates.UTM;
import gov.nasa.arc.irg.georef.coordinates.util.UtmLatLongConverter;
import gov.nasa.arc.verve.common.DataBundleHelper;
import gov.nasa.arc.verve.common.scenario.preferences.ScenarioPreferenceKeys;
import gov.nasa.arc.verve.common.ardor3d.shape.grid.GridTexture;
import gov.nasa.util.StrUtil;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.ardor3d.image.Texture;
import com.ardor3d.image.Texture.MagnificationFilter;
import com.ardor3d.image.Texture.MinificationFilter;
import com.ardor3d.image.Texture2D;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Vector2;
import com.ardor3d.math.Vector3;


public class ScenarioPreferences {
    private static final Logger logger = Logger.getLogger("ScenarioPreferences");
    
    private static int prefInt(String str) {
        return ScenarioActivator.getDefault().getPreferenceStore().getInt(str);
    }
    private static double prefDbl(String key) {
        String val = ScenarioActivator.getDefault().getPreferenceStore().getString(key);
        if(val.length() == 0)
            return 0;
        return Double.valueOf(val);
    }
    private static boolean prefBool(String str) {
        return ScenarioActivator.getDefault().getPreferenceStore().getBoolean(str);
    }
    private static String prefString(String str) {
        return ScenarioActivator.getDefault().getPreferenceStore().getString(str);
    }


    public static int getNumFlatGrids() {
        return prefInt(ScenarioPreferenceKeys.P_MAX_FLAT_GRIDS);
    }
    
    public static void setFlatGridEnabled(int index, boolean state) {
        ScenarioActivator.getDefault().getPreferenceStore().setValue(ScenarioPreferenceKeys.gridEnabled(index), state);
    }
    public static boolean getFlatGridEnabled(int index) {
        return prefBool(ScenarioPreferenceKeys.gridEnabled(index));
    }
    public static float getFlatGridSize(int index) {
        return (float)prefDbl(ScenarioPreferenceKeys.gridSize(index));
    }
    public static double[] getFlatGridOffset(int index) {
        return StrUtil.toDoubleArray( prefString(ScenarioPreferenceKeys.gridOffset(index)) );
    }
    public static ColorRGBA getFlatGridColor(int index)	{
        float[] clr = new float[] { 1,1,1,1 };
        String clrString = prefString(ScenarioPreferenceKeys.gridColor(index));
        float[] tmp = StrUtil.toFloatArray(clrString, ",");
        for(int i = 0; i < tmp.length && i < 4; i++) {
            clr[i] = tmp[i]/255.0f;
        }
        return new ColorRGBA(clr[0], clr[1], clr[2], clr[3]);
    }
    
    /** @returns texture for grid lines */
    public static Texture2D getFlatGridGridTexture(int index) {
        Texture2D retVal = null;
        try {
            final String str = prefString(ScenarioPreferenceKeys.gridGridTextureFile(index));
            GridTexture.Style style = GridTexture.Style.valueOf(str);
            retVal = GridTexture.load(style);
        }
        catch(Throwable t) {
            // ignore
        }
        return retVal;
    }
    
    public static Texture2D getFlatGridBaseTexture(int index) {
        final String texFileName =  prefString(ScenarioPreferenceKeys.gridBaseTextureFile(index));
        Texture2D texture = null;
        if(texFileName.length() > 0) {
            try {
                texture = DataBundleHelper.loadTexture("*", "images/"+texFileName,
                                                       Texture.WrapMode.Repeat,
                                                       MinificationFilter.Trilinear, 
                                                       MagnificationFilter.Bilinear,
                                                       0.25f);                      
            } 
            catch (IOException e) {
                logger.warn("Error loading texture \""+texFileName+"\": "+e.getMessage());
            }
        }
        return texture;
    }
    
    public static boolean getFlatGridDepthWriteEnable() {
        return prefBool(ScenarioPreferenceKeys.P_GRID_DEPTH_WRITE);
    }

    /**
     * @return
     */
    public static Vector2 getSiteFrameLatLonOffset() {
        Vector2 retVal = new Vector2();
        double[] ll = StrUtil.toDoubleArray( prefString(ScenarioPreferenceKeys.P_SITE_FRAME_LOCATION) );
        retVal.set(ll[0], ll[1]);
        return retVal;
    }

    /**
     * @return
     */
    public static double getSiteFrameAltitude() {
        return prefDbl(ScenarioPreferenceKeys.P_SITE_FRAME_ALTITUDE);
    }

    /**
     * get 
     * @return
     */
    public static Vector3 getSiteFrameEastingNorthingAltitude() {
        Vector2 latlon = getSiteFrameLatLonOffset();
        UTM utm = UtmLatLongConverter.toUTM(latlon.getX(), latlon.getY());
        Vector3 retVal = new Vector3();
        retVal.set(utm.getEasting(), utm.getNorthing(), getSiteFrameAltitude());
        return retVal;
    }
}
