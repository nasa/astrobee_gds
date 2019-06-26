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
package gov.nasa.rapid.v2.e4.util;

import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.e4.exception.NotARapidTypeException;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import rapid.DataType;
import rapid.Header;
import rapid.MIME_IMAGE_BMP;
import rapid.MIME_IMAGE_EXR;
import rapid.MIME_IMAGE_JPEG;
import rapid.MIME_IMAGE_PBM;
import rapid.MIME_IMAGE_PGM;
import rapid.MIME_IMAGE_PNG;
import rapid.MIME_IMAGE_PNM;
import rapid.MIME_IMAGE_PPM;
import rapid.MIME_IMAGE_RGB;
import rapid.MIME_IMAGE_TIFF;
import rapid.MIME_IMAGE_XPM;
import rapid.ParameterUnion;
import rapid.Transform3D;

public class RapidUtil {
    private static final Map<Class,Field> s_hdrFieldMap = new HashMap<Class,Field>();
    protected static String dateFormat = "yyyy-MM-dd HH:mm:ss.S";
    
    /**
     * 
     * @return current time in microseconds. Simply takes Java System.currentTimeMillis() and multiplies by 1000
     */
    public static long currentTimeMicros() {
        return 1000*System.currentTimeMillis();
    }
    
    /**
     * Simple method to turn timestamp in microseconds into a date string
     * @param microseconds
     * @return date formatted as yyyy-MM-dd HH:mm:ss.S
     */
    public static String dateString(long microseconds) {
        return new SimpleDateFormat(dateFormat).format(new Date(microseconds/1000));
    }
    
    /**
     * 
     * @param hdr
     * @param assetName
     * @param srcName
     * @param serial
     * @param statusCode
     * @param timeStamp timestamp in MICROSECONDS from UNIX epoch (1970)
     */
    public static void setHeader(Header hdr, String assetName, String srcName, int serial, int statusCode, long timeStamp) {
        hdr.assetName  = assetName;
        hdr.srcName    = srcName;
        hdr.serial     = serial;
        hdr.statusCode = statusCode;
        hdr.timeStamp  = timeStamp;
    }
    
    public static void setHeader(Header hdr, String assetName, String srcName, int serial, int statusCode) {
        setHeader(hdr, assetName, srcName, serial, statusCode, currentTimeMicros());
    }
    public static void setHeader(Header hdr, String assetName, String srcName, int serial) {
        setHeader(hdr, assetName, srcName, serial, 0, currentTimeMicros());
    }
    public static void setHeader(Header hdr, Agent agent, String srcName, int serial) {
        setHeader(hdr, agent.name(), srcName, serial, 0, currentTimeMicros());
    }

    public static void setHeader(Header hdr, Agent agent, Agent srcAgent, int serial) {
        setHeader(hdr, agent.name(), srcAgent.name(), serial, 0, currentTimeMicros());
    }

    public static String toString(Header hdr) {
        StringBuffer retVal = new StringBuffer("");
        retVal.append("Header:"
                + "\n  srcName    = " + hdr.srcName 
                + "\n  assetName  = " + hdr.assetName
                + "\n  statusCode = " + hdr.statusCode 
                + "\n  timeStamp  = " + hdr.timeStamp + " (" + dateString(hdr.timeStamp) +")" 
                + "\n  serial     = " + hdr.serial );
        return retVal.toString();
    }

    /**
     * Using reflection, get the Header instance from a RAPID message
     * object. Field information is cached in order to improve performance.
     * @param obj a RAPID message instance
     * @return
     * @throws NotARapidTypeException if the type does not have a RAPID Header field or the field is not accessible
     */
    public static Header getHeader(Object obj) throws NotARapidTypeException {
        Field hdrField = s_hdrFieldMap.get(obj.getClass());
        if(hdrField == null) {
            try {
                hdrField = obj.getClass().getField("hdr");
                s_hdrFieldMap.put(obj.getClass(), hdrField);
            }
            catch (Exception e) {
                throw new NotARapidTypeException("Cannot access Header field", e);
            }
        }
        try {
            return (Header)hdrField.get(obj);
        }
        catch(Throwable t) {
            throw new NotARapidTypeException("Could not obtain Header object", t);
        }
    }
    
    /**
     * TODO: fill in all mime types
     * @param mimeType
     * @return
     */
    public static String mimeExtension(String mimeType) {
        if(MIME_IMAGE_JPEG.VALUE.equals(mimeType)) return ".jpg";
        if(MIME_IMAGE_PNG.VALUE.equals(mimeType))  return ".png";
        if(MIME_IMAGE_BMP.VALUE.equals(mimeType))  return ".bmp";
        if(MIME_IMAGE_TIFF.VALUE.equals(mimeType)) return ".tif";
        if(MIME_IMAGE_PNM.VALUE.equals(mimeType))  return ".pnm";
        if(MIME_IMAGE_PBM.VALUE.equals(mimeType))  return ".pbm";
        if(MIME_IMAGE_PGM.VALUE.equals(mimeType))  return ".pgm";
        if(MIME_IMAGE_PPM.VALUE.equals(mimeType))  return ".ppm";
        if(MIME_IMAGE_RGB.VALUE.equals(mimeType))  return ".rgb";
        if(MIME_IMAGE_XPM.VALUE.equals(mimeType))  return ".xpm";
        if(MIME_IMAGE_EXR.VALUE.equals(mimeType))  return ".exr";
        return ".unknown";
    }
    
    public static boolean isValid(Transform3D xfm) {
        if(xfm.xyz.userData[0] != xfm.xyz.userData[0]) return false;
        if(xfm.xyz.userData[1] != xfm.xyz.userData[1]) return false;
        if(xfm.xyz.userData[2] != xfm.xyz.userData[2]) return false;
        for(int i = 0; i < 9; i++) {
            if(xfm.rot.userData[i] != xfm.rot.userData[i])
                return false;
        }
        return true;
    }
    
    public static String valueString(ParameterUnion param) {
        switch(param._d.ordinal()) {
        case DataType._RAPID_BOOL:      return Boolean.toString(param.b());
        case DataType._RAPID_DOUBLE:    return String.format("%.4f", param.d());
        case DataType._RAPID_FLOAT:     return String.format("%.4f", param.f());
        case DataType._RAPID_INT:       return Integer.toString(param.i());
        case DataType._RAPID_LONGLONG:  return Long.toString(param.ll);
        case DataType._RAPID_STRING:    return param.s();
        case DataType._RAPID_VEC3d:     return String.format("[%.2f,%.2f,%.2f]", param.vec3d.userData[0], param.vec3d.userData[1], param.vec3d.userData[2]);
        case DataType._RAPID_MAT33f:    return String.format("[%.2f,%.2f,%.2f, %.2f,%.2f,%.2f, %.2f,%.2f,%.2f]", 
                                                             param.mat33f.userData[0], param.mat33f.userData[1], param.mat33f.userData[2], 
                                                             param.mat33f.userData[3], param.mat33f.userData[4], param.mat33f.userData[5], 
                                                             param.mat33f.userData[6], param.mat33f.userData[7], param.mat33f.userData[8]); 
        }
        return "[ERROR]";
    }
    
}
