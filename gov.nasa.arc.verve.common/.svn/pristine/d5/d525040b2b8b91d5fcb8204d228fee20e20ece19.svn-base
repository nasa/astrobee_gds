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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.List;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import org.apache.log4j.Logger;

import com.ardor3d.image.Image;
import com.ardor3d.image.ImageDataFormat;
import com.ardor3d.image.PixelDataType;
import com.ardor3d.image.util.AWTImageUtil;

public class VerveDebugImageWriter {
    private static Logger logger = Logger.getLogger(VerveDebugImageWriter.class);

    protected static ImageWriteParam jpegParams;
    protected static ImageWriter     jpegWriter = null;

    static {
        Iterator iter = ImageIO.getImageWritersByFormatName("jpg");
        ImageWriter jpegWriter = (ImageWriter)iter.next();
        jpegParams = jpegWriter.getDefaultWriteParam();
        jpegParams.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        jpegParams.setCompressionQuality(0.90f);
    }


    /**
     * inefficient debug method to write out an image from a float array to temp directory
     * @param buffer
     * @param width
     * @param height
     * @param description 
     */
    public static void writeImage(float[] buffer, int width, int height, String description) {
        float min = Float.MAX_VALUE;
        float max = -min;
        for(int i = 0; i < buffer.length; i++) {
            if( !(Float.isNaN(buffer[i]) || Float.isInfinite(buffer[i])) ) {
                if(buffer[i] < min) min = buffer[i];
                if(buffer[i] > max) max = buffer[i];
            }
        }

        byte val2;
        byte[] byteData = new byte[buffer.length*4];
        for (int i = 0; i < buffer.length; i++) {
            if(Float.isNaN(buffer[i])) {
                byteData[i*4+0] = 0;
                byteData[i*4+1] = (byte)0xFF;
                byteData[i*4+2] = 0;
                byteData[i*4+3] = (byte)0x55;
            }
            else if(Float.isInfinite(buffer[i])) {
                byteData[i*4+0] = (byte)0xFF;
                byteData[i*4+1] = 0;
                byteData[i*4+2] = 0;
                byteData[i*4+3] = (byte)0x55;
            }
            else {
                val2 = (byte)(255*(buffer[i]-min)/(max-min));
                byteData[i*4+0] = val2;
                byteData[i*4+1] = val2;
                byteData[i*4+2] = val2;
                byteData[i*4+3] = (byte)0xFF;
            }
        }

        writeRgbaByteArray(byteData, width, height, description, null); // XXX add path param
    }

    /**
     *  write an rgb byte buffer to an rgba png
     */
    public static void writeRgbImage(byte[] buffer, int width, int height, String description) {
        writeRgbImage(buffer, width, height, description, null);
    }

    /**
     *  write an rgb byte buffer to an rgba png
     */
    public static void writeRgbImage(byte[] buffer, int width, int height, String description, String path) {
        byte[] byteData = new byte[width*height*4];
        int pixels = width*height;
        for(int i = 0; i < pixels; i++) {
            int in  = i*3;
            int out = i*4;
            byteData[out+0] = buffer[in+0];
            byteData[out+1] = buffer[in+1];
            byteData[out+2] = buffer[in+2];
            byteData[out+3] = (byte)255;
        }
        writeRgbaByteArray(byteData, width, height, description, path);
    }

    /**
     *  write an luminance byte buffer to an rgba png
     */
    public static void writeLuminanceImage(byte[] buffer, int width, int height, String description, String path) {
        byte[] byteData = new byte[width*height*4];
        int pixels = width*height;
        int components = buffer.length/pixels;
        for(int i = 0; i < pixels; i++) {
            int in  = i*components;
            int out = i*4;
            byteData[out+0] = buffer[in+0];
            byteData[out+1] = buffer[in+0];
            byteData[out+2] = buffer[in+0];
            byteData[out+3] = (byte)255;
        }
        writeRgbaByteArray(byteData, width, height, description, path);
    }

    public static void writeLuminanceImage(byte[] buffer, int width, int height, String description) {
        writeLuminanceImage(buffer, width, height, description, null);
    }

    /**
     *  write an rgba byte array to an rgba png
     */
    public static void writeRgbaByteArray(byte[] rgbaByteArray, int width, int height, String description, String path) {
        ByteBuffer buffer = ByteBuffer.wrap(rgbaByteArray);
        writeRgbaByteBuffer(buffer, width, height, description, path);
    }

    /**
     *  backwards compatibility call; simply passes call to writeByteBuffer
     */
    public static void writeRgbaByteBuffer(ByteBuffer byteBuffer, int width, int height, String description, String path) {
        writeByteBuffer(byteBuffer, width, height, description, path);
    }

    public static void writeByteBuffer(ByteBuffer byteBuffer, int width, int height, 
                                       String description, String path) {
        writeByteBuffer(byteBuffer, width, height, description, path, "png");
    }

    /**
     *  write an rgba byte buffer to an rgba png
     */
    public static void writeByteBuffer(ByteBuffer rgbaByteBuffer, int width, int height, 
                                       String description, String path,
                                       String ext) {
        try {
            int numComponents = rgbaByteBuffer.limit()/(width*height);
            Image img = new Image();
            img.setData(rgbaByteBuffer);
            img.setWidth(width);
            img.setHeight(height);
            img.setDataType(PixelDataType.UnsignedByte);
            ImageDataFormat format;
            switch(numComponents) {
            case 3: format = ImageDataFormat.RGB; break;
            case 4: format = ImageDataFormat.RGBA; break;
            default:
                logger.error("Unsupported number of components = "+numComponents);
                return;
            }
            img.setDataFormat(format);

            List<BufferedImage> awtImg = AWTImageUtil.convertToAWT(img);
            if(path == null) {
                path = System.getProperty("java.io.tmpdir");
            }
            File outfile = new File(path+"/"+description+"."+ext);
            ImageIO.setUseCache(false);
            if(ext.equals("jpg")) {
                if(jpegWriter == null) {
                    Iterator iter = ImageIO.getImageWritersByFormatName("jpg");
                    jpegWriter = (ImageWriter)iter.next();
                }
                ImageOutputStream outStream = ImageIO.createImageOutputStream(outfile); 
                jpegWriter.setOutput(outStream);
                IIOImage image = new IIOImage(awtImg.get(0), null, null);
                jpegWriter.write(null, image, jpegParams);
                jpegWriter.reset();
                outStream.close();
            }
            else {
                ImageIO.write(awtImg.get(0), ext, outfile);
            }
            logger.info("wrote debug image: "+outfile.getAbsolutePath());            
        } 
        catch (IOException e) {
            logger.error(e);
        }
    }

    public static void writeRgbaFloatArray(float[] buffer, int width, int height, String description, String path) {
        byte[] rgbaByteArray = new byte[buffer.length];
        for(int i = 0; i < buffer.length; i++) {
            rgbaByteArray[i] = (byte)(255*buffer[i]);
        }
        writeRgbaByteArray(rgbaByteArray, width, height, description, path);
    }


}
