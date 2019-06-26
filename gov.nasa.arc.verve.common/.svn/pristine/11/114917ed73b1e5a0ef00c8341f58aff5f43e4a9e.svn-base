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
package gov.nasa.arc.verve.common;

import gov.nasa.arc.verve.common.preferences.IVervePreferences;

import java.io.File;

import org.apache.log4j.Logger;

import com.ardor3d.math.ColorRGBA;

public class VervePreferences 
{	
    private static final Logger logger = Logger.getLogger(VervePreferences.class);
    
    public static final String P_DEBUG_AXIS_USE_BOUND_CENTER = "DebugAxisUseBoundCenter";
    public static final String P_DEBUG_SHOW_BOUNDS_ON_SELECT = "DebugShowBoundsOnSelect";
    public static final String P_VERVE_DATA_DIR              = "VerveDataDir";
    public static final String P_USE_FALLBACK_SHADERS        = "UseFallbackShaders";
    public static final String P_DEFAULT_BACKGROUND_COLOR    = "DefaultBackgroundColor";
    public static final String P_TARGET_FRAMERATE            = "TargetFramerate";
    public static final String P_SHADOWMAP_SIZE              = "ShadowMapSize";

    private static IVervePreferences s_impl;

    public static void setImpl(IVervePreferences impl) {
        s_impl = impl;
    }
    
    /** 
     * determines whether the debug axis is drawn at  the center of 
     * the spatial's bounding volume (true) or the origin of the spatial (false)
     * @param state
     */
    public static boolean isDebugAxisUseBoundCenter()   {
        return validate(s_impl).isDebugAxisUseBoundCenter();
    }

    /** 
     * immediately show bounds of spatial when selected in Scene Graph view
     * @param state
     */
    public static boolean isShowBoundsOnSelect()   {
        return validate(s_impl).isShowBoundsOnSelect();
    }

    /**
     * get data dir (create if necessary)
     */
    public static File getDataDir() {
        return validate(s_impl).getDataDir();
    }
    
    public static boolean useFallbackShaders() {
        return validate(s_impl).useFallbackShaders();
    }

    public static ColorRGBA getDefaultBackgroundColor() {
        return validate(s_impl).getDefaultBackgroundColor();
    }

    public static int getTargetFramerate() {
        return validate(s_impl).getTargetFramerate();
    }
    
    public static int getShadowMapSize() {
        return validate(s_impl).getShadowMapSize();
    }

    private static IVervePreferences validate(IVervePreferences impl) {
        if(impl == null) {
            logger.error("No VervePreferences implementation has been set");
            throw new IllegalStateException("No VervePreferences implementation has been set");
        }
        return impl;
    }
}
