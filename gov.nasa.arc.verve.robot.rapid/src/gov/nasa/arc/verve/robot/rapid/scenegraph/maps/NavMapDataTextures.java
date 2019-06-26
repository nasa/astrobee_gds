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
package gov.nasa.arc.verve.robot.rapid.scenegraph.maps;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.HashMap;

import com.ardor3d.image.Image;
import com.ardor3d.image.ImageDataFormat;
import com.ardor3d.image.PixelDataType;
import com.ardor3d.image.Texture.MagnificationFilter;
import com.ardor3d.image.Texture.MinificationFilter;
import com.ardor3d.image.Texture.WrapMode;
import com.ardor3d.image.Texture2D;
import com.ardor3d.image.TextureStoreFormat;
import com.ardor3d.util.TextureKey;

/**
 * 
 * @author mallan
 *
 */
public abstract class NavMapDataTextures {

    protected HashMap<String,LayerData> m_layerData = new HashMap<String,LayerData>();
    protected String[]  m_textureNames = new String[0];

    MinificationFilter  m_minFilter = MinificationFilter.BilinearNoMipMaps;
    MagnificationFilter m_magFilter = MagnificationFilter.NearestNeighbor;

    public class LayerData {
        public Texture2D texture;
        public int[]     srcSize = new int[2];
        public int[]     texSize = new int[2];
        public int       numComponents;
        public int       subsample;
        public double    offset;
        public double    scale;
    }

    /**
     * call to populate texture data from telemetry
     * @param telemetry
     */
    public abstract void updateTextures(Object telemetry);
    

    public String[] getLayerTextureNames() {
        return m_textureNames;
    }

    public MagnificationFilter getMagnificationFilter() {
        return m_magFilter;
    }
    public void setMagnificationFilter(final MagnificationFilter magFilter) {
        m_magFilter = magFilter;
        for(LayerData ld : m_layerData.values()) {
            if(ld.texture != null) {
                ld.texture.setMagnificationFilter(m_magFilter);
            }
        }
    }

    public void reset() {
        m_textureNames = new String[0];
    }

    /**
     * Set the layer names that will be stored as textures. 
     * Textures are reset, and the names array is held and sorted
     * @param names array is held and sorted
     */
    public void setLayerTextureNames(String... names) {
        reset();
        m_textureNames = names;
        Arrays.sort(names);
    }

    public boolean isLayerTextureName(String name) {
        return (Arrays.binarySearch(m_textureNames, name) >= 0);
    }

    public LayerData getLayerData(String layerName) {
        return m_layerData.get(layerName);
    }

    /**
     * 
     * @param numComponents
     * @param width
     * @param height
     * @return
     */
    protected Texture2D createByteTexture(int numComponents, int width, int height) {
        final int dataSz = 1;
        ByteBuffer buffer = ByteBuffer.allocateDirect(numComponents * dataSz * width * height).order(ByteOrder.nativeOrder());

        Image image;
        TextureStoreFormat storeFormat;
        switch(numComponents) {
        case 1: 
            //image = new Image(ImageDataFormat.Red, PixelDataType.Byte, width, height, buffer, null);
            //storeFormat = TextureStoreFormat.R16F;
            image = new Image(ImageDataFormat.Luminance, PixelDataType.UnsignedByte, width, height, buffer, null);
            storeFormat = TextureStoreFormat.Luminance8;
            break;
        case 2:
            //image = new Image(ImageDataFormat.RG, PixelDataType.UnsignedByte, width, height, buffer, null);
            //storeFormat = TextureStoreFormat.RG16F;
            image = new Image(ImageDataFormat.LuminanceAlpha, PixelDataType.UnsignedByte, width, height, buffer, null);
            storeFormat = TextureStoreFormat.Luminance8Alpha8;
            break;
        default:
            image = null;
            storeFormat = null;
        }

        if(image != null) {
            Texture2D retVal = new Texture2D();
            retVal.setImage(image);
            retVal.setMinificationFilter(m_minFilter);
            retVal.setMagnificationFilter(m_magFilter);
            retVal.setTextureKey(TextureKey.getRTTKey(retVal.getMinificationFilter()));
            retVal.setTextureStoreFormat(storeFormat);
            retVal.setWrap(WrapMode.EdgeClamp);
            return retVal;
        }
        return null;
    }

    protected Texture2D createShortTexture(int numComponents, int width, int height) {
        final int dataSz = 2;
        ByteBuffer buffer = ByteBuffer.allocateDirect(numComponents * dataSz * width * height).order(ByteOrder.nativeOrder());

        Image image;
        TextureStoreFormat storeFormat;
        switch(numComponents) {
        case 1: 
            //image = new Image(ImageDataFormat.Red, PixelDataType.Short, width, height, buffer, null);
            //storeFormat = TextureStoreFormat.R16F;
            image = new Image(ImageDataFormat.Luminance, PixelDataType.Short, width, height, buffer, null);
            storeFormat = TextureStoreFormat.Luminance16;
            break;
        case 2:
            //image = new Image(ImageDataFormat.RG, PixelDataType.Short, width, height, buffer, null);
            //storeFormat = TextureStoreFormat.RG16F;
            image = new Image(ImageDataFormat.LuminanceAlpha, PixelDataType.Short, width, height, buffer, null);
            storeFormat = TextureStoreFormat.Luminance16Alpha16;
            break;
        default:
            image = null;
            storeFormat = null;
        }

        if(image != null) {
            Texture2D retVal = new Texture2D();
            retVal.setImage(image);
            retVal.setMinificationFilter(m_minFilter);
            retVal.setMagnificationFilter(m_magFilter);
            retVal.setTextureKey(TextureKey.getRTTKey(retVal.getMinificationFilter()));
            retVal.setTextureStoreFormat(storeFormat);
            retVal.setWrap(WrapMode.EdgeClamp);
            return retVal;
        }
        return null;
    }

}
