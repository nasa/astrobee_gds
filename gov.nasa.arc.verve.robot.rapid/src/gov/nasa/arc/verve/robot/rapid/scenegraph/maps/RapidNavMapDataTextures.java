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

import gov.nasa.rapid.v2.e4.message.holders.NavMapHolder;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

import org.apache.log4j.Logger;

import rapid.ext.NavMapConfig;
import rapid.ext.NavMapSample;
import rapid.ext.OctetMapLayer;
import rapid.ext.ShortMapLayer;

import com.ardor3d.image.Texture2D;
import com.ardor3d.math.MathUtils;

/**
 * 
 * @author mallan
 *
 */
public class RapidNavMapDataTextures extends NavMapDataTextures {
    private static final Logger logger = Logger.getLogger(RapidNavMapDataTextures.class);
    protected String[]  m_shortLayerNames = null;
    protected String[]  m_octetLayerNames = null;

    @Override
    public void reset() {
        super.reset();
        m_shortLayerNames = null;
        m_octetLayerNames = null;
    }

    /**
     *
     */
    @Override
    public void updateTextures(Object telemetry) {
        final NavMapHolder holder = (NavMapHolder)telemetry;
        final NavMapSample sample = holder.sample;
        final NavMapConfig config = holder.config;

        if(m_shortLayerNames == null) { // we are uninitialized
            initializeTextures(sample, config);
        }
        //-- short layers
        for(int i = 0; i < m_shortLayerNames.length; i++) {
            if(isLayerTextureName(m_shortLayerNames[i])) {
                final ShortMapLayer layer = (ShortMapLayer)sample.shortLayers.userData.get(i);
                LayerData li         = m_layerData.get(m_shortLayerNames[i]);
                li.offset            = layer.offset;
                li.scale             = layer.scale;
                Texture2D tex        = li.texture;
                ShortBuffer buf      = tex.getImage().getData(0).asShortBuffer();
                buf.rewind();
                for(int sy = 0; sy < li.srcSize[1]; sy++) {
                    final int syi = sy*li.srcSize[0];
                    final int tyi = sy*li.texSize[0];
                    buf.position(tyi*li.numComponents);
                    for(int sx = 0; sx < li.srcSize[0]; sx++) {
                        final int sidx = (syi+sx)*li.numComponents;
                        for(int c = 0; c < li.numComponents; c++) {
                            final int d = layer.data.userData.getShort(sidx+c);
                            buf.put((short)d);
                        }
                    }
                }
                tex.setDirty();
            }
        }
        //-- octet layers
        for(int i = 0; i < config.octetLayerNames.userData.size(); i++) {
            if(isLayerTextureName(m_octetLayerNames[i])) {
                final OctetMapLayer layer = (OctetMapLayer)sample.octetLayers.userData.get(i);
                LayerData li         = m_layerData.get(m_octetLayerNames[i]);
                li.offset            = layer.offset;
                li.scale             = layer.scale;
                Texture2D tex        = li.texture;
                ByteBuffer buf       = tex.getImage().getData(0);
                //final int bufMax     = buf.limit();
                buf.rewind();
                for(int sy = 0; sy < li.srcSize[1]; sy++) {
                    final int syi = sy*li.srcSize[0];
                    final int tyi = sy*li.texSize[0];
                    final int pos = tyi*li.numComponents;
                    buf.position(pos);
                    for(int sx = 0; sx < li.srcSize[0]; sx++) {
                        final int sidx = (syi+sx)*li.numComponents;
                        for(int c = 0; c < li.numComponents; c++) {
                            final int d = 128+layer.data.userData.getByte(sidx+c);
                            buf.put((byte)(d&0xFF));
                        }
                    }
                }
                tex.setDirty();
            }
        }
    }

    protected LayerData checkShortLayerData(ShortMapLayer layer, NavMapConfig config, LayerData li) {
        boolean doInit = false;
        if(li == null) {
            li = new LayerData();
            doInit = true;
        }
        final int width      = config.numCells[0];
        final int height     = config.numCells[1];
        final int density    = layer.density; // we assume density is NOT a dynamic value
        final int srcWidth   = width/density;
        final int srcHeight  = height/density;
        final int srcSize    = srcWidth*srcHeight;
        final int texWidth   = MathUtils.nearestPowerOfTwo(srcWidth);
        final int texHeight  = MathUtils.nearestPowerOfTwo(srcHeight);
        int numComponents = layer.data.userData.size()/srcSize;
        if(density > 1) {
            numComponents = (int)(0.1+Math.sqrt(layer.data.userData.size()/srcSize));
            logger.debug("numComponents="+numComponents);
        }
        if(li.texture != null) {
            if( !(li.texSize[0]==texWidth && li.texSize[1]==texHeight && 
                    li.numComponents==numComponents) ) {
                doInit = true;
            }
        }
        if(doInit) {
            li.texture = createShortTexture(numComponents, texWidth, texHeight);
        }
        li.srcSize[0]    = srcWidth;
        li.srcSize[1]    = srcHeight;
        li.texSize[0]    = texWidth;
        li.texSize[1]    = texHeight;
        li.numComponents = numComponents;
        li.subsample     = layer.density;
        li.offset        = layer.offset;
        li.scale         = layer.scale;
        if(li.texture == null) {
            throw new IllegalStateException("li.texture is null");
        }
        return li;
    }

    protected LayerData checkOctetLayerData(OctetMapLayer layer, NavMapConfig config, LayerData li) {
        boolean doInit = false;
        if(li == null) {
            li = new LayerData();
            doInit = true;
        }
        final int width     = config.numCells[0];
        final int height    = config.numCells[1];
        final int density   = layer.density; // XXX we assume density is NOT a dynamic value
        final int srcWidth  = width/density;
        final int srcHeight = height/density;
        final int srcSize   = srcWidth*srcHeight;
        final int texWidth  = MathUtils.nearestPowerOfTwo(srcWidth);
        final int texHeight = MathUtils.nearestPowerOfTwo(srcHeight);
        int numComponents = layer.data.userData.size()/srcSize;
        if(li.texture != null) {
            if( !(li.texSize[0]==texWidth && li.texSize[1]==texHeight && li.numComponents==numComponents) ) {
                doInit = true;
            }
        }
        if(doInit) {
            li.texture = createByteTexture(numComponents, texWidth, texHeight);
        }
        li.srcSize[0]    = srcWidth;
        li.srcSize[1]    = srcHeight;
        li.texSize[0]    = texWidth;
        li.texSize[1]    = texHeight;
        li.numComponents = numComponents;
        li.subsample     = layer.density;
        li.offset        = layer.offset;
        li.scale         = layer.scale;
        if(li.texture == null) {
            throw new IllegalStateException("li.texture is null");
        }
        return li;
    }

    /**
     * @param sample
     * @param config
     */
    protected void initializeTextures(NavMapSample sample, NavMapConfig config) {
        m_shortLayerNames = new String[config.shortLayerNames.userData.size()];
        m_octetLayerNames = new String[config.octetLayerNames.userData.size()];
        //-- short layers
        for(int i = 0; i < config.shortLayerNames.userData.size(); i++) {
            final String name    = m_shortLayerNames[i] = (String)config.shortLayerNames.userData.get(i);
            if(isLayerTextureName(name)) {
                final ShortMapLayer layer = (ShortMapLayer)sample.shortLayers.userData.get(i);
                LayerData li = checkShortLayerData(layer, config, m_layerData.get(name));
                m_layerData.put(name, li);
                //logger.debug("NavMap short layer "+name+": subsample="+li.subsample+", numComponents="+li.numComponents);
            }
        }
        //-- octet layers
        for(int i = 0; i < config.octetLayerNames.userData.size(); i++) {
            final String name   = m_octetLayerNames[i] = (String)config.octetLayerNames.userData.get(i);
            if(isLayerTextureName(name)) {
                //logger.debug("layer="+name);
                final OctetMapLayer layer = (OctetMapLayer)sample.octetLayers.userData.get(i);
                LayerData li = checkOctetLayerData(layer, config, m_layerData.get(name));
                m_layerData.put(name, li);
                //logger.debug("NavMap byte layer "+name+": subsample="+li.subsample+", numComponents="+li.numComponents);
            }
        }
    }


}
