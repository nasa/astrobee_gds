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
package gov.nasa.arc.verve.common.preferences.rcp;

import gov.nasa.arc.verve.common.Activator;
import gov.nasa.arc.verve.common.VervePreferences;
import gov.nasa.arc.verve.common.preferences.IVervePreferences;
import gov.nasa.util.StrUtil;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.ardor3d.math.ColorRGBA;
import com.ardor3d.renderer.ContextCapabilities;
import com.ardor3d.renderer.ContextManager;
import com.ardor3d.renderer.RenderContext;

public class VervePreferencesEclipsePlugin implements IVervePreferences {

    @SuppressWarnings("unused")
    private static String prefString(String str) {
        return Activator.getDefault().getPreferenceStore().getString(str);
    }
    private static boolean prefBool(String str) {
        return Activator.getDefault().getPreferenceStore().getBoolean(str);
    }   
    private static int prefInt(String str) {
        return Activator.getDefault().getPreferenceStore().getInt(str);
    }   

    /** 
     * determines whether the debug axis is drawn at  the center of 
     * the spatial's bounding volume (true) or the origin of the spatial (false)
     * @param state
     */
    public boolean isDebugAxisUseBoundCenter()   {
        if(Activator.getDefault() != null) {
            return prefBool(VervePreferences.P_DEBUG_AXIS_USE_BOUND_CENTER);
        }
        return false;
    }

    /** 
     * immediately show bounds of spatial when selected in Scene Graph view
     * @param state
     */
    public boolean isShowBoundsOnSelect()   {
        if(Activator.getDefault() != null) {
            return prefBool(VervePreferences.P_DEBUG_SHOW_BOUNDS_ON_SELECT);
        }
        return false;
    }

    /**
     * get data dir (create if necessary)
     */
    public File getDataDir() {
        File dir = null;
        if(Activator.getDefault() != null) {
            String pathString = prefString(VervePreferences.P_VERVE_DATA_DIR);
            dir = new File(pathString);
            if(dir.exists() && !dir.isDirectory()) {
                throw new IllegalStateException("VERVE data dir path is set to a file, not a directory!");
            }
            if(!dir.exists()) {
                try {
                    FileUtils.forceMkdir(dir);
                } 
                catch (IOException e) {
                    throw new IllegalStateException("Cannot create VERVE data dir", e);
                }
            }
        }
        return dir;
    }
    
    public boolean useFallbackShaders() {
        return prefBool(VervePreferences.P_USE_FALLBACK_SHADERS);
    }
    
    public ColorRGBA getDefaultBackgroundColor() {
        float[] clr = new float[] { 1,1,1,1 };
        String clrString = prefString(VervePreferences.P_DEFAULT_BACKGROUND_COLOR);
        float[] tmp = StrUtil.toFloatArray(clrString, ",");
        for(int i = 0; i < tmp.length && i < 4; i++) {
            clr[i] = tmp[i]/255.0f;
        }
        return new ColorRGBA(clr[0], clr[1], clr[2], clr[3]);
    }
    
    public int getTargetFramerate() {
        int framerate = prefInt(VervePreferences.P_TARGET_FRAMERATE);
        if(framerate < 1)
            return 1;
        if(framerate > 30)
            return 30;
        return framerate;
    }
    
    public int getShadowMapSize() {
        int twoK = 2048;
        int size = prefInt(VervePreferences.P_SHADOWMAP_SIZE);
        RenderContext context = ContextManager.getCurrentContext();
        if(context != null) {
            ContextCapabilities caps = context.getCapabilities();
            int max = caps.getMaxTextureSize();
            if(size <= max) {
                return size;
            }
            Activator.getDefault().getPreferenceStore().setValue(VervePreferences.P_SHADOWMAP_SIZE, max);
            return max;
        }
        Activator.getDefault().getPreferenceStore().setValue(VervePreferences.P_SHADOWMAP_SIZE, twoK);
        return twoK;
    }
}
