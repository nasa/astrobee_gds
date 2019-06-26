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

import java.nio.FloatBuffer;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.ardor3d.bounding.BoundingBox;
import com.ardor3d.math.MathUtils;
import com.ardor3d.renderer.IndexMode;
import com.ardor3d.renderer.Renderer;
import com.ardor3d.scenegraph.FloatBufferData;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.MeshData;
import com.ardor3d.scenegraph.event.DirtyType;
import com.ardor3d.scenegraph.hint.DataMode;
import com.ardor3d.util.geom.BufferUtils;

/**
 * Continuous mesh to represent nav map. Mesh vertices are located
 * at the centers of the nav map cells. 
 * Tex coords for the first unit (0) are in cell size coordinates
 * Tex coords for the second unit (1) are in data size/texture size coordinates
 * @author mallan
 *
 */
public abstract class NavMapGeometryMesh extends Mesh implements INavMapGeometry {
    //private static final Logger logger = Logger.getLogger(NavMapGeometryMesh.class);
    protected float             m_alphaTestValue = 0.44f;

    protected ReadWriteLock     m_mapDataLock = new ReentrantReadWriteLock();
    protected HeightMapData     m_mapData = new HeightMapData();
    protected HeightMapData     m_oldData = new HeightMapData();
    protected boolean           m_dirty   = false;
    protected boolean           m_invertWinding = false;
    protected boolean           m_doReinit = false;
    protected BoundingBox       m_bound = new BoundingBox();

    /**
     * 
     */
    //===============================================================
    public NavMapGeometryMesh() {
        this(NavMapGeometryMesh.class.getSimpleName());
    }

    public NavMapGeometryMesh(String name) {
        super(name);
        getMeshData().setVertexBuffer(BufferUtils.createFloatBuffer(0));
        setModelBound(m_bound);
        getSceneHints().setDataMode(DataMode.Arrays);
        //getSceneHints().setRenderBucketType(RenderBucketType.Transparent);
    }

    public Mesh asMesh() {
        return this;
    }

    public void setStaticDataDirty() {
        m_doReinit = true;
    }

    public boolean isInvertWinding() {
        return m_invertWinding;
    }

    public void setInvertWinding(boolean state) {
        m_invertWinding = state;
        m_doReinit = true;
    }

    /**
     * A client should request a HeightMapData object 
     * using borrowHeightMapData() and fill out all
     * of the public fields. When complete, YOU MUST 
     * return the data using returnHeightMapData() to
     * release the write lock. This should be done in 
     * a try-finally block.
     */
    public HeightMapData borrowHeightMapData() {
        m_mapDataLock.writeLock().lock();
        return m_mapData;
    }

    public void returnHeightMapData() {
        m_mapDataLock.writeLock().unlock();
        m_dirty = true;
    }

    protected void updateStaticMeshData() {
        final HeightMapData hmd = m_mapData;
        final HeightMapData old = m_oldData;
        boolean reinit = !hmd.dataFieldsEqual(old);
        if(reinit) {
            initStaticHeightMapData(hmd);
        }
    }

    protected void updateMeshData() {
        m_mapDataLock.readLock().lock();
        try {
            final HeightMapData hmd = m_mapData;
            //final HeightMapData old = m_oldData;
            //m_doReinit = !hmd.dataFieldsEqual(old);

            if(m_doReinit) {
                //initStaticHeightMapData(hmd);
                initMeshData(hmd);
            }
            setMeshData(hmd);
        }
        finally {
            m_oldData.cloneDataFields(m_mapData);
            m_mapDataLock.readLock().unlock();
        }
    }

    @Override
    public void draw(Renderer r) {
        if(m_dirty) {
            updateMeshData();
            m_dirty = false;
        }
        if(m_mapData.totalCells > 0) {
            super.draw(r);
        }
    }

    /**
     * invalidate map so it won't draw
     */
    public void invalidate() {
        m_mapData.totalCells = 0;
        m_dirty = false;
    }

    /**
     * @param numXCells number of cells on X axis
     * @param numYCells number of cells in Y axis
     * @param xCellSize width of cells in meters
     * @param yCellSize height of cells in meters
     * @param offset offset of the map corner from origin
     */
    protected void initStaticHeightMapData(HeightMapData hmd) {
        hmd.totalCells = hmd.numXCells * hmd.numYCells;
        hmd.numXCellsNxtPwr2 = MathUtils.nearestPowerOfTwo(hmd.numXCells);
        hmd.numYCellsNxtPwr2 = MathUtils.nearestPowerOfTwo(hmd.numYCells);

        final int   numXCells = hmd.numXCells;
        final int   numYCells = hmd.numYCells;
        final float xCellSize = hmd.xCellSize;
        final float yCellSize = hmd.yCellSize;
        final float xCellSize2 = hmd.xCellSize/2f;
        final float yCellSize2 = hmd.yCellSize/2f;

        float x,y;
        hmd.xCoords = new float[hmd.totalCells];
        hmd.yCoords = new float[hmd.totalCells];

        // x = out 
        // y = right
        int idx;
        for(int col = 0; col < numYCells; col++) {
            y = col * yCellSize;
            for(int row = 0; row < numXCells; row++) {
                x = row * xCellSize;
                idx = col*numXCells + row;
                hmd.xCoords[idx] = x + hmd.offset.getXf() + xCellSize2; // add config x offset and account for 1/2 cell bound
                hmd.yCoords[idx] = y + hmd.offset.getYf() + yCellSize2; // add config y offset and account for 1/2 cell bound
            }
        }
    }

    /**
     * 
     */
    protected void initMeshData(HeightMapData hmd) {
        //logger.debug("initMeshData:"+getName());
        m_doReinit = false;
        final int vss = hmd.vertSubsample;
        final int ssYCells = hmd.numYCells/vss;
        final int ssXCells = hmd.numXCells/vss;
        final int ssTotalCells = ssXCells*ssYCells;

        final MeshData mdata = this.getMeshData();
        final int numStrips     = ssYCells-1;
        final int vertsPerStrip = ssXCells * 2;
        final int numIndexes    = numStrips * vertsPerStrip;
        final int[] stripIndexCounts = new int[numStrips];
        IndexMode[] indexMode = new IndexMode[numStrips];
        for(int i = 0; i < numStrips; i++) {
            stripIndexCounts[i] = vertsPerStrip;
            indexMode[i] = IndexMode.TriangleStrip;
        }

        final int[] indexes = new int[numIndexes];
        int index = 0;
        int indexA;
        int indexB;
        for(int y = 0; y < ssYCells-1; y++) {
            for(int x = 0; x < ssXCells; x++) {
                indexA = (y+0) * ssXCells + x;
                indexB = (y+1) * ssXCells + x;
                if(!m_invertWinding) { 
                    indexes[index+0] = indexA;
                    indexes[index+1] = indexB;
                }
                else {
                    indexes[index+0] = indexB;
                    indexes[index+1] = indexA;
                }
                index += 2;
            }
        }
        mdata.setIndexLengths(stripIndexCounts);
        mdata.setIndexModes(indexMode);
        mdata.setIndexBuffer(BufferUtils.createIntBuffer(indexes));

        int vertSize = ssTotalCells*3;
        int txCrdSize = ssTotalCells*2;
        if(mdata.getVertexBuffer().capacity() < vertSize) {
            mdata.setVertexBuffer(BufferUtils.createFloatBuffer(vertSize));
            mdata.setTextureCoords(new FloatBufferData(BufferUtils.createFloatBuffer(txCrdSize),2), 0);
            mdata.setTextureCoords(new FloatBufferData(BufferUtils.createFloatBuffer(txCrdSize),2), 1);
        }
        mdata.getVertexBuffer().limit(vertSize);
        mdata.getTextureBuffer(0).limit(txCrdSize);
        mdata.getTextureBuffer(1).limit(txCrdSize);

        // set the tex coords
        FloatBuffer txBuf;
        int unit;
        // cell size texture
        unit = 0;
        mdata.getTextureCoords(unit).getBuffer().limit(txCrdSize);
        txBuf = mdata.getTextureCoords(unit).getBuffer();
        txBuf.rewind();
        for(int vi = 0; vi < hmd.numYCells; vi += vss) {
            for(int ui = 0; ui < hmd.numXCells; ui += vss) {
                final int idxS = vi * hmd.numXCells + ui;
                final float u = -0.5f + hmd.xCoords[idxS]/hmd.xCellSize;
                final float v = -0.5f + hmd.yCoords[idxS]/hmd.yCellSize;
                txBuf.put(u).put(v);
            }
        }

        // data size texture
        float uMax = hmd.numXCells/(float)hmd.numXCellsNxtPwr2;
        float vMax = hmd.numYCells/(float)hmd.numYCellsNxtPwr2;
        final float hxcs = 0.5f/hmd.numXCellsNxtPwr2; // half x cell size
        final float hycs = 0.5f/hmd.numYCellsNxtPwr2; // half y cell size
        unit = 1; 
        txBuf = mdata.getTextureCoords(unit).getBuffer();
        txBuf.rewind();
        for(int vi = 0; vi < hmd.numYCells; vi+=vss) {
            for(int ui = 0; ui < hmd.numXCells; ui+=vss) {
                final float u = hxcs + ui/(float)hmd.numXCells * uMax;
                final float v = hycs + vi/(float)hmd.numYCells * vMax;
                txBuf.put(u).put(v);
            }
        }

        mdata.setColorBuffer(null);

        if(hmd.normals != null) {
            final int nrmSize = ssTotalCells*3;
            if(mdata.getNormalBuffer() == null || mdata.getNormalBuffer().capacity() < nrmSize) {
                mdata.setNormalBuffer(BufferUtils.createFloatBuffer(nrmSize));
            }
            mdata.getNormalBuffer().limit(nrmSize);
        }
        else {
            if(mdata.getNormalBuffer() != null) {
                mdata.setNormalBuffer(null);
            }
        }
    }

    /**
     * 
     * @param hmd
     */
    protected void setMeshData(HeightMapData hmd) {
        MeshData mdata = this.getMeshData();
        FloatBuffer floatBuffer;
        final int vss = hmd.vertSubsample;

        float xMin,yMin,zMin,xMax,yMax,zMax;
        xMin = yMin = zMin =  99999999;
        xMax = yMax = zMax = -99999999;

        if(true) {
            floatBuffer = mdata.getVertexBuffer();
            floatBuffer.rewind();
            final float zoffset = hmd.offset.getZf(); // add config z offset
            for(int y = 0; y < hmd.numYCells; y+=vss) {
                final int yiS = y*hmd.numXCells;
                for(int x = 0; x < hmd.numXCells; x+=vss) {
                    final int idxS = yiS + x;
                    floatBuffer.put(hmd.xCoords[idxS]).put(hmd.yCoords[idxS]).put(hmd.zCoords[idxS]+ zoffset);
                    if(hmd.xCoords[idxS] < xMin) xMin = hmd.xCoords[idxS];
                    else if(hmd.xCoords[idxS] > xMax) xMax = hmd.xCoords[idxS];
                    if(hmd.yCoords[idxS] < yMin) yMin = hmd.yCoords[idxS];
                    else if(hmd.yCoords[idxS] > yMax) yMax = hmd.yCoords[idxS];
                    if(hmd.zCoords[idxS] < zMin) zMin = hmd.zCoords[idxS];
                    else if(hmd.zCoords[idxS] > zMax) zMax = hmd.zCoords[idxS];
                }
            }
        }
        final float xExt = 0.5f*(xMax-xMin);
        final float yExt = 0.5f*(yMax-yMin);
        final float zExt = 0.5f*(zMax-zMin);
        m_bound.setCenter(xMin+xExt, yMin+yExt, zMin+zExt);
        m_bound.setXExtent(xExt);
        m_bound.setYExtent(yExt);
        m_bound.setZExtent(zExt);
        setModelBound(m_bound, false);
        markDirty(DirtyType.Bounding);

        if(hmd.normals != null && (floatBuffer = mdata.getNormalBuffer()) != null) {
            floatBuffer.rewind();
            for(int y = 0; y < hmd.numYCells; y+=vss) {
                final int yiS = y*hmd.numXCells;
                for(int x = 0; x < hmd.numXCells; x+=vss) {
                    final int idxS = 3 * (yiS + x);
                    floatBuffer.put(hmd.normals[idxS+0]).put(hmd.normals[idxS+1]).put(hmd.normals[idxS+2]);
                }
            }
        }
    }
}
