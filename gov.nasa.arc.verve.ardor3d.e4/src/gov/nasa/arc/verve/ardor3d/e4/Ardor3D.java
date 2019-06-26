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
package gov.nasa.arc.verve.ardor3d.e4;

import gov.nasa.arc.verve.ardor3d.e4.framework.CameraControlUpdater;
import gov.nasa.arc.verve.ardor3d.e4.framework.IFrameHandler;
import gov.nasa.arc.verve.ardor3d.e4.framework.LogicalLayerUpdater;
import gov.nasa.util.Colors;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Shell;

import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.type.ReadOnlyColorRGBA;
import com.ardor3d.math.type.ReadOnlyMatrix3;
import com.ardor3d.math.type.ReadOnlyTransform;
import com.ardor3d.math.type.ReadOnlyVector3;
import com.ardor3d.util.Timer;

public abstract class Ardor3D {
    protected static final Logger logger = Logger.getLogger(Ardor3D.class);

    protected Timer                timer;
    protected IFrameHandler        frameHandler;
    protected LogicalLayerUpdater  logicalLayerUpdater;
    protected CameraControlUpdater cameraControlUpdater;

    private static Ardor3D instance;
    
    public static final String P_LAST_TARGET = "LastCameraTarget";

    protected Ardor3D() {
    }
    
    /** set the instance and start render thread */
    public synchronized static void initialize(Ardor3D instance) {
        //logger.debug("Ardor3D.initialize("+instance.getClass().getSimpleName()+")");
        if(Ardor3D.instance == null) {
            Ardor3D.instance = instance;
            Ardor3D.instance.startRenderThreadImpl();
        }
        else {
            logger.warn("Ardor3D is already initialized");
        }
    }

    public static boolean isInitialized() {
        return instance != null;
    }
    
    public static Timer getTimer() {
        return instance.timer;
    }
    
    public static IFrameHandler getFrameHandler() {
        return instance.frameHandler;
    }
    public static LogicalLayerUpdater getLogicalLayerUpdater() {
        return instance.logicalLayerUpdater;
    }
    public static CameraControlUpdater getCameraControlUpdater() {
        return instance.cameraControlUpdater;
    }

    // XXX why is this function commented out? 11/14/14
//    public static void startRenderThread() {
//        instance.startRenderThreadImpl();
//    }
    public static void stopRenderThread() throws InterruptedException {
        instance.stopRenderThreadImpl();
    }
    
    public static void setPreference(String key, String value) {
        instance.setPreferenceImpl(key, value);
    }
    public static String getPreference(String key) {
        return instance.getPreferenceImpl(key);
    }
    
    abstract protected void startRenderThreadImpl();
    abstract protected void stopRenderThreadImpl() throws InterruptedException;
    abstract protected void   setPreferenceImpl(String key, String value);
    abstract protected String getPreferenceImpl(String key);

    public static String format(ReadOnlyColorRGBA clr) {
        return String.format("  r=%.1f g=%.1f b=%.1f a=%.1f", clr.getRed(), clr.getGreen(), clr.getBlue(), clr.getAlpha());
    }

    public static String format(ReadOnlyVector3 t) {
        return String.format("  x=%.3f y=%.3f z=%.3f", t.getX(), t.getY(), t.getZ());
    }

    public static String format(ReadOnlyTransform xfm) {
        ReadOnlyVector3 t = xfm.getTranslation();
        return format(t)+"\n"+format(xfm.getMatrix());
    }

    public static String format(ReadOnlyMatrix3 mat) {
        return String.format("  rot=%.2f  %.2f  %.2f\n"+
                "      %.2f  %.2f  %.2f\n"+
                "      %.2f  %.2f  %.2f",
                mat.getValue(0,0), mat.getValue(0,1), mat.getValue(0,2),
                mat.getValue(1,0), mat.getValue(1,1), mat.getValue(1,2),
                mat.getValue(2,0), mat.getValue(2,1), mat.getValue(2,2));
    }

    public static ColorRGBA color(Colors colorName) {
        return color(colorName, new ColorRGBA());
    }

    public static ColorRGBA color(Colors colorName, ColorRGBA retVal) {
        final int[] rgb = colorName.rgb();
        retVal.set(rgb[0]/255f, rgb[1]/255f, rgb[2]/255f, 1);
        return retVal;
    }

    /**
     * @param colorName
     * @param value scale value of color (0 is black, 1 is color)
     */
    public static ColorRGBA color(Colors colorName, float value) {
        return color(colorName, value, new ColorRGBA());
    }

    /**
     * @param colorName
     * @param value scale value of color (0 is black, 1 is color)
     */
    public static ColorRGBA color(Colors colorName, float value, ColorRGBA retVal) {
        final int[] rgb = colorName.rgb();
        retVal.set(value*rgb[0]/255f, value*rgb[1]/255f, value*rgb[2]/255f, 1);
        return retVal;
    }

    // NOTE: BasicVerveScene.currentTimeMillis() should be used instead of System.currentTimeMillis()
    // wherever possible.
}
