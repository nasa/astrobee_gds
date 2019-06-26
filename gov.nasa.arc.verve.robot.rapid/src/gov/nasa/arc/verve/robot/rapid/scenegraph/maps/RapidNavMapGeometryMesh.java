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

import java.util.HashMap;

import org.apache.log4j.Logger;

import rapid.ext.NAVMAP_CERTAINTY;
import rapid.ext.NAVMAP_GOODNESS;
import rapid.ext.NAVMAP_HEIGHT;
import rapid.ext.NAVMAP_NORMALS;
import rapid.ext.NAVMAP_NUM_OCTET_LAYERS;
import rapid.ext.NAVMAP_NUM_SHORT_LAYERS;
import rapid.ext.NAVMAP_ROUGHNESS;
import rapid.ext.NavMapConfig;
import rapid.ext.NavMapSample;
import rapid.ext.OctetMapLayer;
import rapid.ext.ShortMapLayer;

import com.ardor3d.math.FastMath;
import com.ardor3d.math.type.ReadOnlyTransform;
import com.rti.dds.infrastructure.ShortSeq;

/**
 * 
 * @author mallan
 *
 */
public class RapidNavMapGeometryMesh extends NavMapGeometryMesh {
    private static final Logger logger = Logger.getLogger(RapidNavMapGeometryMesh.class);

    protected int m_lastConfigSerial = -13;

    protected boolean m_clampToTerrain = false;
    final String HEIGHT           = NAVMAP_HEIGHT.VALUE;
    final String GOODNESS         = NAVMAP_GOODNESS.VALUE;
    final String ROUGHNESS        = NAVMAP_ROUGHNESS.VALUE;
    final String CERTAINTY        = NAVMAP_CERTAINTY.VALUE;
    final String NORMALS          = NAVMAP_NORMALS.VALUE;
    protected HashMap<String,Integer> m_shortNameMap = new HashMap<String,Integer>(NAVMAP_NUM_SHORT_LAYERS.VALUE);
    protected HashMap<String,Integer> m_octetNameMap = new HashMap<String,Integer>(NAVMAP_NUM_OCTET_LAYERS.VALUE);

    protected float m_minCertainty = 0.5f;

    public RapidNavMapGeometryMesh() {
        super(RapidNavMapGeometryMesh.class.getSimpleName());
    }

    public boolean isClampToTerrain() {
        return m_clampToTerrain;
    }
    public void setClampToTerrain(boolean state) {
        m_doReinit = true;
        m_clampToTerrain = state;
    }

    protected void updateNameMaps(NavMapConfig config) {
        m_shortNameMap.clear();
        for(int i = 0; i < config.shortLayerNames.userData.size(); i++) {
            m_shortNameMap.put((String)config.shortLayerNames.userData.get(i), i);
        }
        m_octetNameMap.clear();
        for(int i = 0; i < config.octetLayerNames.userData.size(); i++) {
            m_octetNameMap.put((String)config.octetLayerNames.userData.get(i), i);
        }
    }

    static double stopwatchValue = 0;
    static int    stopwatchCount = 0;

    @Override
    public void updateFromTelemetry(Object telemetry, ReadOnlyTransform siteToWorld, ReadOnlyTransform mapFrame, float zOffset, float zSign) {
        final NavMapHolder holder = (NavMapHolder)telemetry;
        final NavMapSample sample = holder.sample;
        final NavMapConfig config = holder.config;
        // XXX FIXME we don't want to do this every update - temporary workaround for CSpace map geometry
        updateNameMaps(config);

        if(config == null) {
            return;
        }
        else if(config.hdr.serial != m_lastConfigSerial) {
            updateNameMaps(config);
            m_lastConfigSerial = config.hdr.serial;
            setStaticDataDirty();
        }

        try {
            Integer index;
            HeightMapData hmd = borrowHeightMapData();

            hmd.numXCells = config.numCells[0];
            hmd.numYCells = config.numCells[1];
            hmd.xCellSize = config.cellSize[0];
            hmd.yCellSize = config.cellSize[1];
            hmd.offset.set(config.offset[0], config.offset[1], 0);
            hmd.totalCells = hmd.numXCells*hmd.numYCells;
            if(hmd.zCoords == null || hmd.zCoords.length != hmd.totalCells) {
                hmd.zCoords = new float[hmd.totalCells];
            }
            updateStaticMeshData();

            OctetMapLayer octetLayer     = null;
            OctetMapLayer normalsLayer   = null;
            OctetMapLayer certaintyLayer = null;
            ShortMapLayer heightLayer    = null;

            //init sparsity to invalid value
            hmd.vertSubsample = -1;
            hmd.normSubsample = -1;

            boolean doGeometryNormals = true;
            if(doGeometryNormals) {
                index = m_octetNameMap.get(NORMALS);
                octetLayer = (OctetMapLayer) ((index == null) ? null : sample.octetLayers.userData.get(index));
                if(octetLayer != null) {
                    normalsLayer = octetLayer;
                    hmd.normSubsample = octetLayer.density;
                }
            }

            // get height layer
            index = m_shortNameMap.get(HEIGHT);
            heightLayer = (ShortMapLayer) ((index == null) ? null : sample.shortLayers.userData.get(index));

            if(isClampToTerrain() || heightLayer == null) {
                //long start = System.nanoTime();
                hmd.setZCoordsFromBaseMap(siteToWorld, mapFrame, zOffset, zSign);
                hmd.vertSubsample = 1;
                //                synchronized(RapidNavMapGeometryMesh.class) {
                //                    stopwatchValue += System.nanoTime()-start;
                //                    if(++stopwatchCount >= 50) {
                //                        logger.debug("setZCoords time = "+0.000001*(stopwatchValue/stopwatchCount));
                //                        stopwatchValue = 0;
                //                        stopwatchCount = 0;
                //                    }
                //                }
            }
            else {
                hmd.vertSubsample = heightLayer.density;
                if(hmd.vertSubsample < 1) {
                    logger.error("height layer density is invalid: "+heightLayer.density);
                    hmd.vertSubsample = 1;
                }
                final float offset = (float)heightLayer.offset;
                final float scale  = heightLayer.scale;
                final int vss = hmd.vertSubsample;
                for(int y = 0; y < hmd.numYCells; y+=vss) {
                    int yiD =  y*hmd.numXCells;
                    int yiS =  (y/vss)*(hmd.numXCells/vss);
                    for(int x = 0; x < hmd.numXCells; x+=vss) {
                        final int idxD = yiD + x;
                        final int idxS = yiS + x/vss;
                        hmd.zCoords[idxD] = offset + scale*heightLayer.data.userData.getShort(idxS); 
                    }
                }
                // get certainty layer
                index = m_octetNameMap.get(CERTAINTY);
                certaintyLayer = (OctetMapLayer)((index == null) ? null : sample.octetLayers.userData.get(index));
                if(certaintyLayer != null) {
                    cleanupHeightEdges(hmd, heightLayer.data.userData, certaintyLayer, offset, scale);
                }
            }


            //-- if we are attempting geometry normals but the sparsity doesn't match, invalidate
            if(doGeometryNormals && (hmd.vertSubsample != hmd.normSubsample) ) {
                normalsLayer = null;
            }

            //-- per vertex normals -- assumes full density normals and two elements (x,y) for now
            if(normalsLayer != null && heightLayer != null) {
                if(normalsLayer.data.userData.size() > 0) {
                    if(hmd.normals == null || hmd.normals.length != 3*hmd.totalCells) {
                        hmd.normals = new float[3*hmd.totalCells];
                    }
                }
                else {
                    hmd.normals = null;
                }
                float tmp, nx, ny, nz;
                if(hmd.normals != null) {
                    final float normScale = normalsLayer.scale;
                    final int nss = hmd.normSubsample;
                    for(int y = 0; y < hmd.numYCells; y+=nss) {
                        final int yiD = y*hmd.numXCells;
                        final int yiS = (y/nss)*(hmd.numXCells/nss);
                        for(int x = 0; x < hmd.numXCells; x+=nss) {
                            int srci = 2*(yiS + x/nss);
                            int dsti = 3*(yiD + x);
                            nx  = zSign * normalsLayer.data.userData.getByte(srci+0)*normScale;
                            ny  = zSign * normalsLayer.data.userData.getByte(srci+1)*normScale;
                            tmp = 1 - (nx*nx + ny*ny);
                            nz  = zSign * (float)FastMath.sqrt(tmp);
                            hmd.normals[dsti+0] = nx;
                            hmd.normals[dsti+1] = ny;
                            hmd.normals[dsti+2] = nz;
                        }
                    }
                }
            }
        }
        catch (Throwable t) {
            logger.error("error receiving NavMap data", t);
        }
        finally {
            returnHeightMapData();
        }
    }

    protected short heightLookup(HeightMapData hmd, ShortSeq rawHeight, int index) {
        final int ss = hmd.vertSubsample;
        if(ss != 1) {
            final int x = index%hmd.numXCells;
            final int y = index/hmd.numXCells;
            final int ssi = (y/ss)*(hmd.numXCells/ss)+x/ss;
            return rawHeight.getShort(ssi);
        }
        return rawHeight.getShort(index);
    }

    protected boolean isUncertain(HeightMapData hmd, OctetMapLayer certainty, ShortSeq rawHeight, int index) {
        boolean retVal = true;
        final int ss = certainty.density;
        final short h = heightLookup(hmd, rawHeight, index);
        final byte cb;
        final float cf;
        if(ss != 1) {
            final int x = index%hmd.numXCells;
            final int y = index/hmd.numXCells;
            index = (y/ss)*(hmd.numXCells/ss)+x/ss;
        }
        // cleanup bad maps (if height is 0, set certainty to 0)
        if(false && h == 0) {
            certainty.data.userData.setByte(index, (byte)0);
        }
        cb = certainty.data.userData.getByte(index);
        cf = cb * certainty.scale;
        retVal = cf < m_minCertainty;
        return retVal;
    }


    /**
     * 
     * FIXME: assuming that any height of 0 is a bad value
     * 
     * @param hmd
     * @param rawHeight
     * @param offset
     * @param scale
     */
    protected void cleanupHeightEdges(HeightMapData hmd, ShortSeq rawHeight, OctetMapLayer certainty, float offset, float scale) {
        if(true) {
            final int vss  = hmd.vertSubsample;
            //short badval = 0;
            for(int y = vss; y < hmd.numYCells-vss; y+=vss) {
                int y0 =  (y-vss)*hmd.numXCells;
                int y1 =        y*hmd.numXCells;
                int y2 =  (y+vss)*hmd.numXCells;
                float goodz = 0;
                for(int x = vss; x < hmd.numXCells-vss; x+=vss) {
                    final int i01 =  y0 + x;
                    final int i00 = i01 - vss;
                    final int i02 = i01 + vss;
                    final int i11 =  y1 + x;
                    final int i10 = i11 - vss;
                    final int i12 = i11 + vss;
                    final int i21 =  y2 + x;
                    final int i20 = i21 - vss;
                    final int i22 = i21 + vss;
                    final short d11 = heightLookup(hmd, rawHeight, i11);
                    //if(d11 != badval) {
                    if(!isUncertain(hmd, certainty, rawHeight, i11)) {
                        goodz = hmd.zCoords[i11] = offset + scale*d11;
                        if(isUncertain(hmd, certainty, rawHeight, i00)) { hmd.zCoords[i00] = goodz; }
                        if(isUncertain(hmd, certainty, rawHeight, i01)) { hmd.zCoords[i01] = goodz; }
                        if(isUncertain(hmd, certainty, rawHeight, i02)) { hmd.zCoords[i02] = goodz; }
                        if(isUncertain(hmd, certainty, rawHeight, i10)) { hmd.zCoords[i10] = goodz; }
                        if(isUncertain(hmd, certainty, rawHeight, i12)) { hmd.zCoords[i12] = goodz; }
                        if(isUncertain(hmd, certainty, rawHeight, i20)) { hmd.zCoords[i20] = goodz; }
                        if(isUncertain(hmd, certainty, rawHeight, i21)) { hmd.zCoords[i21] = goodz; }
                        if(isUncertain(hmd, certainty, rawHeight, i22)) { hmd.zCoords[i22] = goodz; }
                    }
                }
            }
            {
                int y = 0;
                int y1 =        y*hmd.numXCells;
                int y2 =  (y+vss)*hmd.numXCells;
                float goodz = 0;
                for(int x = vss; x < hmd.numXCells-vss; x += vss) {
                    final int i11 =  y1 + x;
                    final int i10 = i11 - vss;
                    final int i12 = i11 + vss;
                    final int i21 =  y2 + x;
                    final int i20 = i21 - vss;
                    final int i22 = i21 + vss;
                    final short d11 = heightLookup(hmd, rawHeight, i11);
                    if(!isUncertain(hmd, certainty, rawHeight, i11)) {
                        goodz = hmd.zCoords[i11] = offset + scale*d11;
                        if(isUncertain(hmd, certainty, rawHeight, i10)) { hmd.zCoords[i10] = goodz; }
                        if(isUncertain(hmd, certainty, rawHeight, i12)) { hmd.zCoords[i12] = goodz; }
                        if(isUncertain(hmd, certainty, rawHeight, i20)) { hmd.zCoords[i20] = goodz; }
                        if(isUncertain(hmd, certainty, rawHeight, i21)) { hmd.zCoords[i21] = goodz; }
                        if(isUncertain(hmd, certainty, rawHeight, i22)) { hmd.zCoords[i22] = goodz; }
                    }
                }
            }
            {   
                int y = hmd.numYCells-vss;
                int y0 =  (y-vss)*hmd.numXCells;
                int y1 =        y*hmd.numXCells;
                float goodz = 0;
                for(int x = vss; x < hmd.numXCells-vss; x += vss) {
                    final int i01 =  y0 +   x;
                    final int i00 = i01 - vss;
                    final int i02 = i01 + vss;
                    final int i11 =  y1 +   x;
                    final int i10 = i11 - vss;
                    final int i12 = i11 + vss;
                    final short d11 = heightLookup(hmd, rawHeight, i11);
                    if(!isUncertain(hmd, certainty, rawHeight, i11)) {
                        goodz = hmd.zCoords[i11] = offset + scale*d11;
                        if(isUncertain(hmd, certainty, rawHeight, i00)) { hmd.zCoords[i00] = goodz; }
                        if(isUncertain(hmd, certainty, rawHeight, i01)) { hmd.zCoords[i01] = goodz; }
                        if(isUncertain(hmd, certainty, rawHeight, i02)) { hmd.zCoords[i02] = goodz; }
                        if(isUncertain(hmd, certainty, rawHeight, i10)) { hmd.zCoords[i10] = goodz; }
                        if(isUncertain(hmd, certainty, rawHeight, i12)) { hmd.zCoords[i12] = goodz; }
                    }
                }
            }
            for(int y = vss; y < hmd.numYCells-vss; y+=vss) {
                int y0 =  (y-vss)*hmd.numXCells;
                int y1 =        y*hmd.numXCells;
                int y2 =  (y+vss)*hmd.numXCells;
                float goodz = 0;
                {   
                    final int x = 0;
                    final int i01 =  y0 + x;
                    final int i02 = i01 + vss;
                    final int i11 =  y1 + x;
                    final int i12 = i11 + vss;
                    final int i21 =  y2 + x;
                    final int i22 = i21 + vss;
                    final short d11 = heightLookup(hmd, rawHeight, i11);
                    if(!isUncertain(hmd, certainty, rawHeight, i11)) {
                        goodz = hmd.zCoords[i11] = offset + scale*d11;
                        if(isUncertain(hmd, certainty, rawHeight, i01)) { hmd.zCoords[i01] = goodz; }
                        if(isUncertain(hmd, certainty, rawHeight, i02)) { hmd.zCoords[i02] = goodz; }
                        if(isUncertain(hmd, certainty, rawHeight, i12)) { hmd.zCoords[i12] = goodz; }
                        if(isUncertain(hmd, certainty, rawHeight, i21)) { hmd.zCoords[i21] = goodz; }
                        if(isUncertain(hmd, certainty, rawHeight, i22)) { hmd.zCoords[i22] = goodz; }
                    }
                }
            }
            for(int y = vss; y < hmd.numYCells-vss; y++) {
                int y0 =  (y-vss)*hmd.numXCells;
                int y1 =        y*hmd.numXCells;
                int y2 =  (y+vss)*hmd.numXCells;
                float goodz = 0;
                { 
                    int x = hmd.numXCells-vss;
                    final int i01 =  y0 + x;
                    final int i00 = i01 - vss;
                    final int i11 =  y1 + x;
                    final int i10 = i11 - vss;
                    final int i21 =  y2 + x;
                    final int i20 = i21 - vss;
                    final short d11 = heightLookup(hmd, rawHeight, i11);
                    if(!isUncertain(hmd, certainty, rawHeight, i11)) {
                        goodz = hmd.zCoords[i11] = offset + scale*d11;
                        if(isUncertain(hmd, certainty, rawHeight, i00)) { hmd.zCoords[i00] = goodz; }
                        if(isUncertain(hmd, certainty, rawHeight, i01)) { hmd.zCoords[i01] = goodz; }
                        if(isUncertain(hmd, certainty, rawHeight, i10)) { hmd.zCoords[i10] = goodz; }
                        if(isUncertain(hmd, certainty, rawHeight, i20)) { hmd.zCoords[i20] = goodz; }
                        if(isUncertain(hmd, certainty, rawHeight, i21)) { hmd.zCoords[i21] = goodz; }
                    }
                }
            }
        }    
    }

    //    /**
    //     * 
    //     * FIXME: assuming that any height of 0 is a bad value
    //     * 
    //     * @param hmd
    //     * @param rawHeight
    //     * @param offset
    //     * @param scale
    //     */
    //    protected void cleanupHeightEdges(HeightMapData hmd, ShortSeq rawHeight, ByteSeq certainty, float offset, float scale) {
    //        if(true) {
    //            final int vss  = hmd.vertSubsample;
    //            short badval = 0;
    //            for(int y = vss; y < hmd.numYCells-vss; y+=vss) {
    //                int y0 =  (y-vss)*hmd.numXCells;
    //                int y1 =        y*hmd.numXCells;
    //                int y2 =  (y+vss)*hmd.numXCells;
    //                float goodz = 0;
    //                for(int x = vss; x < hmd.numXCells-vss; x+=vss) {
    //                    final int i01 =  y0 + x;
    //                    final int i00 = i01 - vss;
    //                    final int i02 = i01 + vss;
    //                    final int i11 =  y1 + x;
    //                    final int i10 = i11 - vss;
    //                    final int i12 = i11 + vss;
    //                    final int i21 =  y2 + x;
    //                    final int i20 = i21 - vss;
    //                    final int i22 = i21 + vss;
    //                    final short d11 = heightLookup(hmd, rawHeight, i11);
    //                    if(d11 != badval) {
    //                        goodz = hmd.zCoords[i11] = offset + scale*d11;
    //                        if(heightLookup(hmd, rawHeight, i00) == badval) { hmd.zCoords[i00] = goodz; }
    //                        if(heightLookup(hmd, rawHeight, i01) == badval) { hmd.zCoords[i01] = goodz; }
    //                        if(heightLookup(hmd, rawHeight, i02) == badval) { hmd.zCoords[i02] = goodz; }
    //                        if(heightLookup(hmd, rawHeight, i10) == badval) { hmd.zCoords[i10] = goodz; }
    //                        if(heightLookup(hmd, rawHeight, i12) == badval) { hmd.zCoords[i12] = goodz; }
    //                        if(heightLookup(hmd, rawHeight, i20) == badval) { hmd.zCoords[i20] = goodz; }
    //                        if(heightLookup(hmd, rawHeight, i21) == badval) { hmd.zCoords[i21] = goodz; }
    //                        if(heightLookup(hmd, rawHeight, i22) == badval) { hmd.zCoords[i22] = goodz; }
    //                    }
    //                }
    //            }
    //            {
    //                int y = 0;
    //                int y1 =        y*hmd.numXCells;
    //                int y2 =  (y+vss)*hmd.numXCells;
    //                float goodz = 0;
    //                for(int x = vss; x < hmd.numXCells-vss; x += vss) {
    //                    final int i11 =  y1 + x;
    //                    final int i10 = i11 - vss;
    //                    final int i12 = i11 + vss;
    //                    final int i21 =  y2 + x;
    //                    final int i20 = i21 - vss;
    //                    final int i22 = i21 + vss;
    //                    final short d11 = heightLookup(hmd, rawHeight, i11);
    //                    if(d11 != badval) {
    //                        goodz = hmd.zCoords[i11] = offset + scale*d11;
    //                        if(heightLookup(hmd, rawHeight, i10) == badval) { hmd.zCoords[i10] = goodz; }
    //                        if(heightLookup(hmd, rawHeight, i12) == badval) { hmd.zCoords[i12] = goodz; }
    //                        if(heightLookup(hmd, rawHeight, i20) == badval) { hmd.zCoords[i20] = goodz; }
    //                        if(heightLookup(hmd, rawHeight, i21) == badval) { hmd.zCoords[i21] = goodz; }
    //                        if(heightLookup(hmd, rawHeight, i22) == badval) { hmd.zCoords[i22] = goodz; }
    //                    }
    //                }
    //            }
    //            {   
    //                int y = hmd.numYCells-vss;
    //                int y0 =  (y-vss)*hmd.numXCells;
    //                int y1 =        y*hmd.numXCells;
    //                float goodz = 0;
    //                for(int x = vss; x < hmd.numXCells-vss; x += vss) {
    //                    final int i01 =  y0 +   x;
    //                    final int i00 = i01 - vss;
    //                    final int i02 = i01 + vss;
    //                    final int i11 =  y1 +   x;
    //                    final int i10 = i11 - vss;
    //                    final int i12 = i11 + vss;
    //                    final short d11 = heightLookup(hmd, rawHeight, i11);
    //                    if(d11 != badval) {
    //                        goodz = hmd.zCoords[i11] = offset + scale*d11;
    //                        if(heightLookup(hmd, rawHeight, i00) == badval) { hmd.zCoords[i00] = goodz; }
    //                        if(heightLookup(hmd, rawHeight, i01) == badval) { hmd.zCoords[i01] = goodz; }
    //                        if(heightLookup(hmd, rawHeight, i02) == badval) { hmd.zCoords[i02] = goodz; }
    //                        if(heightLookup(hmd, rawHeight, i10) == badval) { hmd.zCoords[i10] = goodz; }
    //                        if(heightLookup(hmd, rawHeight, i12) == badval) { hmd.zCoords[i12] = goodz; }
    //                    }
    //                }
    //            }
    //            for(int y = vss; y < hmd.numYCells-vss; y+=vss) {
    //                int y0 =  (y-vss)*hmd.numXCells;
    //                int y1 =        y*hmd.numXCells;
    //                int y2 =  (y+vss)*hmd.numXCells;
    //                float goodz = 0;
    //                {   
    //                    final int x = 0;
    //                    final int i01 =  y0 + x;
    //                    final int i02 = i01 + vss;
    //                    final int i11 =  y1 + x;
    //                    final int i12 = i11 + vss;
    //                    final int i21 =  y2 + x;
    //                    final int i22 = i21 + vss;
    //                    final short d11 = heightLookup(hmd, rawHeight, i11);
    //                    if(d11 != badval) {
    //                        goodz = hmd.zCoords[i11] = offset + scale*d11;
    //                        if(heightLookup(hmd, rawHeight, i01) == badval) { hmd.zCoords[i01] = goodz; }
    //                        if(heightLookup(hmd, rawHeight, i02) == badval) { hmd.zCoords[i02] = goodz; }
    //                        if(heightLookup(hmd, rawHeight, i12) == badval) { hmd.zCoords[i12] = goodz; }
    //                        if(heightLookup(hmd, rawHeight, i21) == badval) { hmd.zCoords[i21] = goodz; }
    //                        if(heightLookup(hmd, rawHeight, i22) == badval) { hmd.zCoords[i22] = goodz; }
    //                    }
    //                }
    //            }
    //            for(int y = vss; y < hmd.numYCells-vss; y++) {
    //                int y0 =  (y-vss)*hmd.numXCells;
    //                int y1 =        y*hmd.numXCells;
    //                int y2 =  (y+vss)*hmd.numXCells;
    //                float goodz = 0;
    //                { 
    //                    int x = hmd.numXCells-vss;
    //                    final int i01 =  y0 + x;
    //                    final int i00 = i01 - vss;
    //                    final int i11 =  y1 + x;
    //                    final int i10 = i11 - vss;
    //                    final int i21 =  y2 + x;
    //                    final int i20 = i21 - vss;
    //                    final short d11 = heightLookup(hmd, rawHeight, i11);
    //                    if(d11 != badval) {
    //                        goodz = hmd.zCoords[i11] = offset + scale*d11;
    //                        if(heightLookup(hmd, rawHeight, i00) == badval) { hmd.zCoords[i00] = goodz; }
    //                        if(heightLookup(hmd, rawHeight, i01) == badval) { hmd.zCoords[i01] = goodz; }
    //                        if(heightLookup(hmd, rawHeight, i10) == badval) { hmd.zCoords[i10] = goodz; }
    //                        if(heightLookup(hmd, rawHeight, i20) == badval) { hmd.zCoords[i20] = goodz; }
    //                        if(heightLookup(hmd, rawHeight, i21) == badval) { hmd.zCoords[i21] = goodz; }
    //                    }
    //                }
    //            }
    //        }    
    //    }
}
