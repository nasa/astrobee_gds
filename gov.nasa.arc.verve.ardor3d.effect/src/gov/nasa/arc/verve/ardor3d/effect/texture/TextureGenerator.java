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
package gov.nasa.arc.verve.ardor3d.effect.texture;

import java.nio.ByteBuffer;
import java.util.Random;

import com.ardor3d.image.Image;
import com.ardor3d.image.ImageDataFormat;
import com.ardor3d.image.PixelDataType;
import com.ardor3d.image.Texture2D;
import com.ardor3d.image.Texture.MagnificationFilter;
import com.ardor3d.image.Texture.MinificationFilter;
import com.ardor3d.image.Texture.WrapMode;
import com.ardor3d.renderer.state.TextureState;

public class TextureGenerator {
    static Random random = new Random(System.currentTimeMillis());
    
    public static TextureState createCheckerTextureState(final int texSz) {
        final int bufSz = texSz * texSz * 2; // format is luminance alpha
        final byte[] texData = new byte[bufSz];
        for (int i = 0; i < texSz; i++) {
            for (int j = 0; j < texSz; j++) {
                final int idx = 2 * (i * texSz + j);
                texData[idx + 0] = (byte) (100 + ((i + j) % 2 * 155));
                texData[idx + 1] = (byte) ((i == j) ? 0x00 : 0xFF);
            }
        }
        final Image img = new Image(ImageDataFormat.LuminanceAlpha, PixelDataType.UnsignedByte, texSz, texSz, ByteBuffer.wrap(texData), null);
        final Texture2D tex = new Texture2D();
        tex.setImage(img);
        tex.setMinificationFilter(MinificationFilter.NearestNeighborNoMipMaps);
        tex.setMagnificationFilter(MagnificationFilter.NearestNeighbor);

        final TextureState ts = new TextureState();
        ts.setTexture(tex);

        return ts;
    }

    public static TextureState createColorRampTextureState(final int texSz) {
        // create a texture image for the box
        final int bufSz = texSz * texSz * 4; // format is standard RGBA
        final byte[] texData = new byte[bufSz];
        for (int i = 0; i < texSz; i++) {
            for (int j = 0; j < texSz; j++) {
                final int idx = 4 * (i * texSz + j);
                texData[idx + 0] = (byte) (255 * ((float) (i + 0) / (float) texSz));
                texData[idx + 1] = (byte) (255 * ((float) (j + 0) / (float) texSz));
                texData[idx + 2] = (byte) (255);
                texData[idx + 3] = (byte) ((i % (texSz - 1) == 0 || j % (texSz - 1) == 0) ? 0x00 : 0xFF);
            }
        }
        final Image img = new Image(ImageDataFormat.RGBA, PixelDataType.UnsignedByte, texSz, texSz, ByteBuffer.wrap(texData), null);
        final Texture2D tex = new Texture2D();
        tex.setMinificationFilter(MinificationFilter.NearestNeighborNoMipMaps);
        tex.setMagnificationFilter(MagnificationFilter.Bilinear);
        tex.setImage(img);

        final TextureState ts = new TextureState();
        ts.setTexture(tex);

        return ts;
    }

    /** crappy fractal noise */
    public static TextureState createNoiseTextureState(final int texSz) {
        
        final int bufSz = texSz * texSz;
        final byte[]  texData = new byte[bufSz];
        
        int srcSz = 4;
        float[] srcData = new float[srcSz*srcSz];
        for(int i = 0; i < srcData.length; i++) {
            srcData[i] = 0.5f;
        }
        addNoise(srcData, 0.5f);
        
        float amt = 0.1f;
        while(srcSz < texSz) {
            srcData = scale2x(srcData);
            srcSz *= 2;
            addNoise(srcData, amt);
            amt *= 0.99f;
        }
        clamp(srcData);
        
        for(int i = 0; i < bufSz; i++) {
            texData[i] = (byte)(srcData[i]*255);
        }
        
        final Image img = new Image(ImageDataFormat.Luminance, PixelDataType.UnsignedByte, texSz, texSz, ByteBuffer.wrap(texData), null);
        final Texture2D tex = new Texture2D();
        tex.setImage(img);
        tex.setMinificationFilter(MinificationFilter.Trilinear);
        tex.setMagnificationFilter(MagnificationFilter.Bilinear);
        tex.setWrap(WrapMode.Repeat);
        //tex.setMagnificationFilter(MagnificationFilter.NearestNeighbor);

        final TextureState ts = new TextureState();
        ts.setTexture(tex);

        return ts;
    }

    static void addNoise(float[] buffer, float amount) {
        for(int i = 0; i < buffer.length; i++) {
            float n = amount * (1 - 2*random.nextFloat());
            buffer[i] += n;
        }
    }
    static void clamp(float[] buffer) {
        for(int i = 0; i < buffer.length; i++) {
            if(buffer[i] < 0) buffer[i] = 0;
            if(buffer[i] > 1) buffer[i] = 1;
        }
    }
    
    static int wrap(int i, int max) {
        if(i < 0)    i = max-1;
        if(i >= max) i = 0;
        return i;
    }
    // simple, inefficient and poor quality scale
    static float[] scale2x(float[] buffer) {
        int sz1 = (int)Math.sqrt(buffer.length);
        int sz2 = sz1*2;
        float[] width = new float[sz2*sz1];
        for(int r1 = 0; r1 < sz1; r1++) {
            for(int c1 = 0; c1 < sz1; c1++) {
                int cur = r1*sz1 + wrap(c1+0,sz1);
                int nxt = r1*sz1 + wrap(c1+1,sz1);
                int prv = r1*sz1 + wrap(c1-1,sz1);
                int i0 = r1*sz2 + 2*c1;
                int i1 = i0+1;
                width[i0] = 0.67f*buffer[cur] + 0.33f*buffer[prv];
                width[i1] = 0.67f*buffer[cur] + 0.33f*buffer[nxt];
            }
        }
        float[] retVal = new float[sz2*sz2];
        for(int r1 = 0; r1 < sz1; r1++) {
            for(int c2 = 0; c2 < sz2; c2++) {
                int cur = wrap(r1+0,sz1)*sz2 + c2;
                int top = wrap(r1-1,sz1)*sz2 + c2;
                int bot = wrap(r1+1,sz1)*sz2 + c2;
                int i0 = (2*r1)*sz2 + c2;
                int i1 = i0+sz2;
                retVal[i0] = 0.67f*width[cur] + 0.33f*width[top];
                retVal[i1] = 0.67f*width[cur] + 0.33f*width[bot];
            }
        }
        return retVal;
    }
}
