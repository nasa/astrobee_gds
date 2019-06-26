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

import gov.nasa.arc.verve.common.VerveDebugImageWriter;
import gov.nasa.rapid.v2.e4.message.holders.NavMapHolder;

import org.apache.log4j.Logger;

import rapid.ext.OctetMapLayer;
import rapid.ext.ShortMapLayer;

public class MapDebug {
    private final static Logger logger = Logger.getLogger(MapDebug.class);

    public static void writeMapAsImages(NavMapHolder holder, int count) {
        int numXCells = holder.config.numCells[0];
        int numYCells = holder.config.numCells[1];
        for(int i = 0; i < holder.sample.shortLayers.userData.size(); i++) {
            ShortMapLayer layer = (ShortMapLayer)holder.sample.shortLayers.userData.get(i);
            String name = (String)holder.config.shortLayerNames.userData.get(i);
            String tile = "-Tile["+holder.sample.tileId[0]+"_"+holder.sample.tileId[1]+"]";
            String cntString = String.format("-%04d", count);
            writeLayerAsImage(layer, numXCells, numYCells, name+tile+cntString);
        }
        for(int i = 0; i < holder.sample.octetLayers.userData.size(); i++) {
            OctetMapLayer layer = (OctetMapLayer)holder.sample.octetLayers.userData.get(i);
            String name = (String)holder.config.octetLayerNames.userData.get(i);
            String tile = "-Tile["+holder.sample.tileId[0]+"_"+holder.sample.tileId[1]+"]";
            writeLayerAsImage(layer, numXCells, numYCells, name+tile);
        }
    }
    
    public static void writeLayerAsImage(ShortMapLayer layer, int numXCells, int numYCells, String name) {
        int ss = layer.density;
        if(ss < 1) {
            logger.error("layer density is invalid: "+layer.density);
            ss = 1;
        }
        final int nXss = numXCells/ss;
        final int nYss = numYCells/ss;
        final float[] values = new float[nXss*nYss];
        final float offset = (float)layer.offset;
        final float scale  = layer.scale;
        for(int y = 0; y < nYss; y++) {
            int yi =  y*nXss;
            for(int x = 0; x < nXss; x++) {
                final int idx = yi + x;
                values[idx] = offset + scale*layer.data.userData.getShort(idx); 
            }
        }
        VerveDebugImageWriter.writeImage(values, nXss, nYss, name);
    }
    
    public static void writeLayerAsImage(OctetMapLayer layer, int numXCells, int numYCells, String name) {
        int ss = layer.density;
        if(ss < 1) {
            logger.error("layer density is invalid: "+layer.density);
            ss = 1;
        }
        final int nXss = numXCells/ss;
        final int nYss = numYCells/ss;
        final float[] values = new float[nXss*nYss];
        final float offset = (float)layer.offset;
        final float scale  = layer.scale;
        for(int y = 0; y < nYss; y++) {
            int yi =  y*nXss;
            for(int x = 0; x < nXss; x++) {
                final int idx = yi + x;
                values[idx] = offset + scale*layer.data.userData.getByte(idx); 
            }
        }
        VerveDebugImageWriter.writeImage(values, nXss, nYss, name);
    }
}
