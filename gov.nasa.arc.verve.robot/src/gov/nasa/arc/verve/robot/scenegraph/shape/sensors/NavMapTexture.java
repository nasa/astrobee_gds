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
package gov.nasa.arc.verve.robot.scenegraph.shape.sensors;

import gov.nasa.arc.verve.robot.scenegraph.shape.sensors.NavMapHeightField.HeightMapData;

import java.nio.ByteBuffer;

import com.ardor3d.image.Image;
import com.ardor3d.image.ImageDataFormat;
import com.ardor3d.image.PixelDataType;
import com.ardor3d.image.Texture2D;
import com.ardor3d.image.TextureStoreFormat;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.type.ReadOnlyColorRGBA;
import com.ardor3d.util.TextureKey;
import com.ardor3d.util.geom.BufferUtils;

public class NavMapTexture extends Texture2D {    
    protected Image m_image = new Image();

    /**
     * 
     */
    public NavMapTexture() {
        setMinificationFilter(MinificationFilter.BilinearNoMipMaps);
        setMagnificationFilter(MagnificationFilter.NearestNeighbor);
        //setMagnificationFilter(MagnificationFilter.Bilinear);
        setTextureKey(TextureKey.getRTTKey(getMinificationFilter()));
        setTextureStoreFormat(TextureStoreFormat.RGBA8);
        setWrap(WrapMode.EdgeClamp);
        defaultImage(m_image);
        setImage(m_image);
    }
    
    public void update(HeightMapData hmd) {
        ByteBuffer buffer = m_image.getData(0);
        int size = hmd.numXCellsNxtPwr2 * hmd.numYCellsNxtPwr2 * 4;
        if(buffer.capacity() < size) {
            buffer = BufferUtils.createByteBuffer(size);
        }
        buffer.limit(size);
        int yi;
        for(int y = 0; y < hmd.numYCells; y++) {
            yi = y*hmd.numXCells;
            buffer.position(4*y*hmd.numXCellsNxtPwr2);
            for(int x = 0; x < hmd.numXCells; x++) {
                final int idx = 4*(yi + x);
                buffer.put((byte)(255*hmd.colors[idx+0]))
                      .put((byte)(255*hmd.colors[idx+1]))
                      .put((byte)(255*hmd.colors[idx+2]))
                      .put((byte)(255*hmd.colors[idx+3]));
            }
        }
        buffer.rewind();
        m_image.setWidth(hmd.numXCellsNxtPwr2);
        m_image.setHeight(hmd.numYCellsNxtPwr2);
        m_image.setData(buffer);
        setDirty();
    }

    /**
     * 
     */
    protected static void defaultImage(Image image) {
        image.setDataType(PixelDataType.UnsignedByte);
        //image.setDataType(PixelDataType.UnsignedByte);
        image.setDataFormat(ImageDataFormat.RGBA);
        int w = 128;
        int h = 256;
        ReadOnlyColorRGBA clr;
        image.setHeight(w);
        image.setWidth(h);
        ByteBuffer buffer = BufferUtils.createByteBuffer(w*h*4);
        for(int y = 0; y < h; y++) {
            for(int x = 0; x < w; x++) {
                clr = ColorRGBA.BLACK;
                if(x < 5) clr = ColorRGBA.GREEN;
                else {
                    if( (x/4)%2 == 1 ) clr = ColorRGBA.YELLOW;
                    else {
                        if( (y/6)%2 == 1) clr = ColorRGBA.BROWN;
                        else clr = ColorRGBA.GRAY;
                    }
                    if(y < 5)          clr = ColorRGBA.RED;
                    else if( y > h-5 ) clr = ColorRGBA.BLUE;
                }
                buffer.put((byte)(clr.getRed()  *255));
                buffer.put((byte)(clr.getGreen()*255));
                buffer.put((byte)(clr.getBlue() *255));
                buffer.put((byte)(clr.getAlpha()*255));
            }
        }
        image.setData(buffer);
    }
}
