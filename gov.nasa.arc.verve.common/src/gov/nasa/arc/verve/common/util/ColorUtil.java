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
package gov.nasa.arc.verve.common.util;

import com.ardor3d.math.ColorRGBA;

public class ColorUtil {
    private static ColorUtil s_instance = null;
    public static final int	ColorTableSize = 255;
    float[][]			m_colorTableHue		= new float[ColorTableSize][3];
    float[][]			m_colorTableHueValue= new float[ColorTableSize][3];

    protected static ColorUtil instance() {
        if(s_instance == null) {
            s_instance = new ColorUtil();
        }
        return s_instance;
    }

    /**
     * get a color from a standard hex color string of length 7 for RGB (#AABBCC) or
     * length 9 for RGBA (#AABBCCDD)
     */
    public static ColorRGBA fromHex(String colorStr, ColorRGBA retVal) {
        if(retVal == null) 
            retVal = new ColorRGBA();
        final int len = colorStr.length();
        if(len == 9) {
            retVal.set(Integer.valueOf( colorStr.substring( 1, 3 ), 16)/255f,
                       Integer.valueOf( colorStr.substring( 3, 5 ), 16)/255f,
                       Integer.valueOf( colorStr.substring( 5, 7 ), 16)/255f,
                       Integer.valueOf( colorStr.substring( 7, 9 ), 16)/255f);
        }
        else if(len == 7) {
            retVal.set(Integer.valueOf( colorStr.substring( 1, 3 ), 16)/255f,
                       Integer.valueOf( colorStr.substring( 3, 5 ), 16)/255f,
                       Integer.valueOf( colorStr.substring( 5, 7 ), 16)/255f,
                       1f);
        }
        return retVal;
    }

    private static int s_ValueMapIndex = 0;
    /**
     * color mapping options
     */
    public enum ColorMap {
        HueScale,
        GreyScale,
        HueValueScale;

        public final int index;

        ColorMap() {
            index = s_ValueMapIndex++;
        }

        public static ColorMap fromIndex(int index) {
            for( ColorMap vm : ColorMap.values() ) {
                if(vm.index == index) 
                    return vm;
            }
            return null;
        }
        public static ColorMap fromName(String name) {
            if(name != null) {
                for( ColorMap vm : ColorMap.values() ) {
                    if(vm.toString().equalsIgnoreCase(name)) 
                        return vm;
                }
            }
            return null;
        }
    }

    /**
     * create color tables - HSV interpolation from
     * blue (low) green (mid) red (high)
     */
    ColorUtil() {
        float h;
        // blue (low) green (mid) red (high)
        for(int i = 0; i < ColorTableSize; i++) {
            h = (1.0f - (float)i/(float)ColorTableSize) * 245.0f;
            //h = ((float)i/(float)ColorTableSize) * 360.0f;
            convertHSVtoRGB(h, 1, 1, m_colorTableHue[i]);
        }

        // blue (low) green (mid) red (high)
        for(int i = 0; i < ColorTableSize; i++) {
            h = (1.0f - (float)i/(float)ColorTableSize) * 245.0f;
            convertHSVtoRGB(h, 0.5f, 1.0f, m_colorTableHueValue[i]);
        }
    }

    public static float[][] colorTableHue() {
        return instance().m_colorTableHue;
    }
    public static float[][] colorTableHueValue() {
        return instance().m_colorTableHueValue;
    }

    public static int getRgba(float red, float green, float blue, float alpha) {
        int val, r, g, b, a;
        b = ((int)(255*blue));
        g = ((int)(255*green)) << 8;
        r = ((int)(255*red))   << 16;
        a = ((int)(255*alpha)) << 24;
        val = r | g | b | a;
        return val;
    }

    /**
     * given a ColorMap type, convert the data value to rgb
     * @param map
     * @param data
     * @param min
     * @param max
     * @param range
     * @return
     */
    public static int colorMap(ColorMap map, float data, float min, float max, float range) {
        switch(map) {
        case GreyScale: return greyScale(data, min, max, range);
        case HueScale:	return hueScale(data, min, max, range);
        case HueValueScale: return hueValueScale(data, min, max, range);
        }
        return 0;
    }

    /**
     * simple greyscale - dataMin is black, dataMax is white
     * @param data
     * @return
     */
    public static int greyScale(float data, float min, float max, float range) {
        if( Float.isNaN(data) ) { // NaN values are error condition
            return 255 << 16;
        }

        float i;
        int v,r,g,b,a;
        if(data < min) { // dark red if outside lower bound
            r = 128 << 16;
            g = b = 0;
        }
        else if(data > max) { // dark green if outside upper bound
            g = 128 << 8;
            r = b = 0;
        }
        else {
            i = (data-min)/range;
            v = (int)(255.0f * i);
            b = v;
            g = v << 8;
            r = v << 16;
        }
        a = 255 << 24;
        return b|g|r|a;
    }

    /**
     * @param data
     * @return
     */
    public static int hueScale(float data, float min, float max, float range) {
        if(Float.isNaN(data)) { // NaN values are error condition
            return 0;
        }

        if(data > max)
            max = data;
        int r,g,b,a;
        float	val;
        float	p;
        int		pi;
        float[] rgb;
        if(data < min) { // black if outside lower bound
            r = g = b = 0;
        }
        else if(data > max) { // white if outside upper bound
            b = 255;
            g = 255 << 8;
            r = 255 << 16;
        }
        else {
            p   = (data-min)/range;
            pi  = ((int)(p * (ColorUtil.ColorTableSize-1)));
            rgb = colorTableHue()[pi];
            val = 255;
            b = (int)(val*rgb[2]);
            g = (int)(val*rgb[1]) << 8;
            r = (int)(val*rgb[0]) << 16;
        }
        a = 255 << 24;
        return b|g|r|a;
    }

    /**
     * @param data
     * @return
     */
    public static int hueValueScale(float data, float min, float max, float range) {
        final float valueScale = 0.7f;
        if(Float.isNaN(data)) { // NaN values are error condition
            return 0;
        }


        //if(data > max)
        //	max = data;
        int r,g,b,a;
        float	val;
        float	p;
        int		pi;
        float[] rgb;
        if(data < min) { // black if outside lower bound
            r = g = b = 0;
        }
        else if(data > max) { // white if outside upper bound
            b = 255;
            g = 255 << 8;
            r = 255 << 16;
        }
        else {
            p   = (data-min)/range;
            pi  = ((int)(p * (ColorUtil.ColorTableSize-1)));
            rgb = colorTableHueValue()[pi];
            val = 255 - 255*(valueScale*(1-p));
            b = (int)(val*rgb[2]);
            g = (int)(val*rgb[1]) << 8;
            r = (int)(val*rgb[0]) << 16;
        }
        a = 255 << 24;
        return b|g|r|a;
    }

    /** convert rgb value (range 0,1) to hsv. Expects 3 element float arrays */
    public static void convertRGBtoHSV(float[] rgb, float[] hsv) {
        convertRGBtoHSV(rgb[0], rgb[1], rgb[2], hsv);
    }
    /** convert rgb value (range 0,1) to hsv. Expects 3 element float arrays */
    public static void convertRGBtoHSV(float r, float g, float b, float[] hsv) {
        float h = 0;
        float s = 0;
        //        float v = 0;

        float max = (r > g) ? r : g;
        max = (max > b) ? max : b;

        float min = (r < g) ? r : g;
        min = (min < b) ? min : b;

        s = max;    // this is the value v

        // Calculate the saturation s
        if(max != 0) {
            s = (max - min) / max;
        }
        else {
            s = 0;
        }

        if(s == 0) {
            h = 0;  // h => UNDEFINED
        }
        else {
            // Chromatic case: Saturation is not 0, determine hue
            float delta = max - min;

            if(r == max)
            {
                // resulting color is between yellow and magenta
                h = (g - b) / delta ;
            }
            else if(g == max)
            {
                // resulting color is between cyan and yellow
                h = 2 + (b - r) / delta;
            }
            else if(b == max)
            {
                // resulting color is between magenta and cyan
                h = 4 + (r - g) / delta;
            }

            // convert hue to degrees and make sure it is non-negative
            h = h * 60;
            while(h < 0) {
                h += 360;
            }
        }

        // now assign everything....
        hsv[0] = h;
        hsv[1] = s;
        hsv[2] = max;
    }


    /**
     * Convert Hue, Saturation, Value to Red, Green Blue. Expects 3 element float arrays
     */
    public static void convertHSVtoRGB(float[] hsv, float[] rgb)
    {
        convertHSVtoRGB(hsv[0], hsv[1], hsv[2], rgb);
    }

    /**
     * Convert Hue, Saturation, Value to Red, Green Blue. Expects 3 element float arrays
     */
    public static void convertHSVtoRGB(float h, float s, float v, float[] rgb)
    {
        float r = 0;
        float g = 0;
        float b = 0;

        if(s == 0) {
            r = v;
            g = v;
            b = v;
        }
        else {
            h = h%360;
            h = h / 60;

            int i = (int)Math.floor(h);
            float f = h - i;             
            float p = v * (1 - s);
            float q = v * (1 - (s * f));
            float t = v * (1 - (s * (1 - f)));

            switch(i){
            case 0:
                r = v;
                g = t;
                b = p;
                break;

            case 1:
                r = q;
                g = v;
                b = p;
                break;

            case 2:
                r = p;
                g = v;
                b = t;
                break;

            case 3:
                r = p;
                g = q;
                b = v;
                break;

            case 4:
                r = t;
                g = p;
                b = v;
                break;

            case 5:
                r = v;
                g = p;
                b = q;
                break;
            }
        }
        // now assign everything....
        rgb[0] = r;
        rgb[1] = g;
        rgb[2] = b;
    }
}



