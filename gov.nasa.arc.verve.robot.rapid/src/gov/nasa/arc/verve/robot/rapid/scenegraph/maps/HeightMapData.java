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

import gov.nasa.arc.verve.common.VerveBaseMap;

import org.apache.log4j.Logger;

import com.ardor3d.math.Transform;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyTransform;

public class HeightMapData {   
    private static final Logger logger = Logger.getLogger(HeightMapData.class);
    public       int      numXCells;
    public       int      numYCells;
    public       int      numXCellsNxtPwr2;
    public       int      numYCellsNxtPwr2;
    protected    int      totalCells;
    public       int      vertSubsample;
    public       int      normSubsample;
    public       float    xCellSize;
    public       float    yCellSize;
    public final Vector3  offset  = new Vector3();
    public       float[]  xCoords = null;
    public       float[]  yCoords = null;
    public       float[]  zCoords = null;

    public       float[]  normals = null;

    public HeightMapData cloneDataFields(HeightMapData other) {
        this.numXCells        = other.numXCells;
        this.numYCells        = other.numYCells;
        this.numXCellsNxtPwr2 = other.numXCellsNxtPwr2;
        this.numYCellsNxtPwr2 = other.numYCellsNxtPwr2;
        this.totalCells       = other.totalCells;
        this.vertSubsample    = other.vertSubsample;
        this.normSubsample    = other.normSubsample;
        this.xCellSize        = other.xCellSize;
        this.yCellSize        = other.yCellSize;
        return this;
    }

    public boolean dataFieldsEqual(HeightMapData other) {
        return (this.numXCells        == other.numXCells &&
                this.numYCells        == other.numYCells &&
                this.numXCellsNxtPwr2 == other.numXCellsNxtPwr2 &&
                this.numYCellsNxtPwr2 == other.numYCellsNxtPwr2 &&
                this.totalCells       == other.totalCells &&
                this.vertSubsample    == other.vertSubsample &&
                this.normSubsample    == other.normSubsample &&
                this.xCellSize        == other.xCellSize &&
                this.yCellSize        == other.yCellSize);
    }

    //private static final Logger logger = Logger.getLogger(HeightMapData.class);

    /**
     * General purpose way to populate Z values from base map
     */
    public void setZCoordsFromBaseMap(ReadOnlyTransform siteToWorld, ReadOnlyTransform mapFrame, float zOffset, float zSign) {        
        if(xCoords != null) {
            if(VerveBaseMap.hasBaseMap() && siteToWorld != null) {
                try {
                    Transform xfm = new Transform();
                    siteToWorld.multiply(mapFrame, xfm);
                    Vector3 tmpVec = new Vector3();
                    Vector3 wldVec = new Vector3();
                    float   mapHeight = 0;
                    for(int y = 0; y < numYCells; y++) {
                        final int yi = y*numXCells;
                        for(int x = 0; x < numXCells; x++) {
                            final int i = yi + x;
                            tmpVec.set(xCoords[i], yCoords[i], 0);
                            xfm.applyForward(tmpVec, wldVec);
                            mapHeight = VerveBaseMap.getHeightAt(wldVec.getXf(), wldVec.getYf());
                            if(mapHeight == mapHeight) {
                                zCoords[i] = -(mapHeight - wldVec.getZf()) + zOffset; 
                            }
                            else {
                                zCoords[i] = 0;
                            }
                        }
                    }
                }
                catch(Throwable t) {
                    logger.error("Error setting HeightMapData to VerveBaseMap", t);
                }
            }
            else {
                for(int y = 0; y < numYCells; y++) {
                    final int yi = y*numXCells;
                    for(int x = 0; x < numXCells; x++) {
                        final int i = yi + x;
                        zCoords[i] = zOffset;
                    }
                }
            }
        }
    }

    //    /**
    //     * General purpose way to populate Z values from base map
    //     */
    //    public void setZCoordsFromBaseMap(ReadOnlyTransform siteToWorld, ReadOnlyTransform mapFrame, float zOffset, float zSign) {
    //        if(xCoords != null) {
    //            if(VerveBaseMap.hasBaseMap() && siteToWorld != null) {
    //                Vector3 wrlCoord = new Vector3();
    //                Vector3 mapCoord = new Vector3();
    //                Vector3 tmpCoord = new Vector3();
    //                float wrlZ;
    //                float mapZ;
    //                for(int y = 0; y < numYCells; y++) {
    //                    final int yi = y*numXCells;
    //                    for(int x = 0; x < numXCells; x++) {
    //                        final int i = yi + x;
    //                        tmpCoord.set(xCoords[i], yCoords[i], 0);
    //                        mapFrame.applyForward(tmpCoord, mapCoord);
    //                        siteToWorld.applyInverse(mapCoord, wrlCoord);
    //                        mapZ = wrlCoord.getZf();
    //                        wrlZ = VerveBaseMap.getHeightAt(wrlCoord.getXf(), wrlCoord.getYf());
    //                        if(wrlZ != wrlZ) {
    //                            wrlZ = 0;
    //                        }
    //                        //wrlCoord.setZ(zSign*wrlZ + mapZ + zOffset);
    //                        wrlCoord.setZ(wrlZ + zSign*zOffset - mapZ);
    //                        siteToWorld.applyForward(wrlCoord, mapCoord);
    //                        //mapCoord.set(wrlCoord);
    //                        zCoords[i] = mapCoord.getZf();
    //                    }
    //                }
    //            }
    //            else {
    //                for(int y = 0; y < numYCells; y++) {
    //                    final int yi = y*numXCells;
    //                    for(int x = 0; x < numXCells; x++) {
    //                        final int i = yi + x;
    //                        zCoords[i] = zOffset;
    //                    }
    //                }
    //            }
    //        }
    //    }
}
