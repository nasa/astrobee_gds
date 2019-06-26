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
package gov.nasa.arc.irg.util.ui;

import gov.nasa.util.Colors;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

public class ColorProvider {

    public static final ColorProvider INSTANCE = new ColorProvider();

    /**
     * Get a color from one of the standardized color
     * sets defined in Colors
     */
    public static Color get(Colors color) {
        RGB rgb = new RGB(color.rgb()[0], color.rgb()[1], color.rgb()[2]);
        return get(rgb);
    }

    /**
     * Get Color from RGB components (range 0-255)
     */
    public static Color get(int red, int green, int blue) {
        RGB rgb = new RGB(red, green, blue);
        return get(rgb);
    }

    /**
     * Get Color from RGB.  
     */
    public static Color get(RGB rgb) {
        Color retVal = INSTANCE.m_allocated.get(rgb);
        if(retVal == null) {
            final Display display = Display.getDefault();
            if (display == null || display.isDisposed()){
                return null;
            }
            retVal = new Color(display, rgb);
            INSTANCE.m_allocated.put(rgb, retVal);
        }
        return retVal;
    }

    public Color red;
    public Color green;
    public Color darkGreen;
    public Color black;
    public Color orange;
    public Color superDarkGreen;
    public Color lightGreen2;
    public Color paleYellow;
    public Color white;
    public Color darkGray;
    public Color yellow;
    public Color grayBackground;

    public Color sky;
    public Color ground;

    public Color WIDGET_BACKGROUND;
    public Color gray_226;
    public Color gray_212;

    public Color cyan;

    protected final Map<RGB,Color> m_allocated = new HashMap<RGB,Color>();

    protected ColorProvider() {
        initializeColors();
    }

    /**
     * Initialize the colors.
     */
    protected void initializeColors() {
        final Display display = Display.getDefault();
        if (display == null || display.isDisposed()){
            return;
        }

        display.syncExec(new Runnable() {

            @Override
            public void run() {
                if (red == null){
                    red = display.getSystemColor(SWT.COLOR_RED);
                    green = display.getSystemColor(SWT.COLOR_GREEN);
                    darkGreen = display.getSystemColor(SWT.COLOR_DARK_GREEN);
                    black = display.getSystemColor(SWT.COLOR_BLACK);
                    white = display.getSystemColor(SWT.COLOR_WHITE);
                    darkGray = display.getSystemColor(SWT.COLOR_DARK_GRAY);
                    yellow = display.getSystemColor(SWT.COLOR_YELLOW);
                    WIDGET_BACKGROUND = display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
                    cyan = display.getSystemColor(SWT.COLOR_CYAN);
                }
                if (orange == null){
                    orange = new Color(display, 255,133,36);
                    superDarkGreen = new Color(display, 0, 198, 156);
                    lightGreen2 = new Color(display, 204, 238, 204);
                    paleYellow = new Color(display, 241, 240, 183);

                    sky = new Color(display, 89, 166, 234);
                    ground = new Color(display, 225, 131, 9);
                    grayBackground = new Color(display, 203, 203, 203);
                    gray_226 = new Color(display, 226,226,226);
                    gray_212 = new Color(display, 212,212,212);

                    addDisposable(orange);
                    addDisposable(superDarkGreen);
                    addDisposable(lightGreen2);
                    addDisposable(paleYellow);
                    addDisposable(sky);
                    addDisposable(ground);
                    addDisposable(grayBackground);
                    addDisposable(gray_226);
                    addDisposable(gray_212);
                }
            }
        });
    }

    public void dispose() {
        //        Display display = Display.getDefault();
        //        if (display == null || display.isDisposed()){
        //            return;
        //        }
        //
        //        // dispose within Resource checks if the display is null.
        //        for (Color obj : m_allocated.values()){
        //            obj.dispose();
        //        }
        //        m_allocated.clear();
        //
        //        red = null;
        //        green = null;
        //        darkGreen = null;
        //        black = null;
        //        orange = null;
        //        superDarkGreen = null;
        //        paleYellow = null;
        //        white = null;
        //        darkGray = null;
        //        grayBackground = null;
        //
        //        TS_PENDING = null; 
        //        TS_PAUSED = null;
        //        TS_EXECUTING = null;
        //        TS_SUCCESS = null;
        //        TS_INSTRUMENT_FAILURE = null;
        //        TS_NAVIGATION_FAILURE = null;
        //        TS_TIMEOUT = null;
        //        TS_ABORTED = null;
    }

    protected void addDisposable(Color obj){
        m_allocated.put(obj.getRGB(), obj);
    }

}
