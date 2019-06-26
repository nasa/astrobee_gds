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

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import com.ardor3d.image.Image;

public class TexUtil {

    public static void blackAndWhiteUnsignedByte(Image image, float stripe) {
        int w = image.getWidth();
        int h = image.getHeight();
        ByteBuffer bb = image.getData().get(0);
        bb.rewind();
        float val = 0;
        float scale = 255;
        for(int y = 0; y < h; y++) {
            for(int x = 0; x < w; x++) {
                if(x < w/3 && y < h/3) { // "top" corner is black 
                    val = 0;
                }
                else { // the rest is striped
                    float s = y * stripe;
                    float v = (float)Math.sin(s);
                    val = 0.5f + v*0.5f;
                }
                bb.put((byte)(val*scale));
            }
        }
        bb.rewind();
    }

    public static void blackAndWhiteShort(Image image, float stripe) {
        int w = image.getWidth();
        int h = image.getHeight();
        ByteBuffer bb = image.getData().get(0);
        bb.rewind();
        double val = 0;
        double scale = Short.MAX_VALUE;
        for(int y = 0; y < h; y++) {
            for(int x = 0; x < w; x++) {
                if(x < w/3 && y < h/3) { // "top" corner is black 
                    val = 0;
                }
                else { // the rest is striped
                    double s = y * stripe;
                    double v = Math.sin(s);
                    //val = 0.5f + v*0.5f;
                    val = v;
                }
                bb.putShort((short)(val*scale));
            }
        }
        bb.rewind();
    }

	/**
	 * assumes BufferedImage is TYPE_INT_ARGB
	 * @param img
	 */
	@SuppressWarnings("cast")
	public static void fillTestPattern(BufferedImage img) {
		int txSzW = img.getWidth();
		int txSzH = img.getHeight();
		
		int val = 0;
		int s1, s2, r, g, b;
		int a = 255 << 24;
		for(int h = 0; h < txSzH; h++) {
			for(int w = 0; w < txSzW; w++) {
				s1 = (int)(255.0 * (double)w/(double)txSzW);
				s2 = (int)(255.0 * (double)h/(double)txSzH);
				
				final double chk = 0.9;
				if(h%2 == 0) {
					if(w%2 == 0) {
						s1 = (int)(s1 * chk);
						s2 = (int)(s2 * chk);
					}
				}
				else {
					if((w+1)%2 == 0) {
						s1 = (int)(s1 * chk);
						s2 = (int)(s2 * chk);
					}
				}
				
				if(h%8 == 0) {
					b = s1;
					g = s1 << 8;
					r = (255-s1) << 16;
				}
				else {
					b = 255 - s1;
					g = s2 << 8;
					r = s2 << 16;
				}
				val = r | g | b | a;
				
				img.setRGB(w, h, val);
			}
		}
	}
	
	/**
	 * assumes BufferedImage is TYPE_INT_ARGB
	 * @param img
	 */
	public static void fillColor(BufferedImage img, double red, double green, double blue, double alpha) {
		int txSzW = img.getWidth();
		int txSzH = img.getHeight();
		int val = 0;
		int r, g, b, a;
		for(int h = 0; h < txSzH; h++) {
			for(int w = 0; w < txSzW; w++) {
				b = ((int)(255*blue));
				g = ((int)(255*green)) << 8;
				r = ((int)(255*red))   << 16;
				a = ((int)(255*alpha)) << 24;
				val = r | g | b | a;
				img.setRGB(w, h, val);
			}
		}
	}
	
	/**
	 * Make a Gaussian blur kernel.
	 */
	public static float[] createGaussianBlurKernel(float radius) {
		int r = (int)Math.ceil(radius);
		int rows = r*2+1;
		float[] matrix = new float[rows];
		float sigma = radius/3;
		float sigma22 = 2*sigma*sigma;
		float sigmaPi2 = (float)(2*Math.PI*sigma);
		float sqrtSigmaPi2 = (float)Math.sqrt(sigmaPi2);
		float radius2 = radius*radius;
		float total = 0;
		int index = 0;
		for (int row = -r; row <= r; row++) {
			float distance = row*row;
			if (distance > radius2)
				matrix[index] = 0;
			else
				matrix[index] = (float)Math.exp(-(distance)/sigma22) / sqrtSigmaPi2;
			total += matrix[index];
			index++;
		}
		for (int i = 0; i < rows; i++)
			matrix[i] /= total;

		return matrix;
	}

}
