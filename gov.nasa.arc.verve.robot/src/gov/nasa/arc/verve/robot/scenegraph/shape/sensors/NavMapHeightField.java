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

import gov.nasa.arc.verve.robot.AbstractRobot;

import java.nio.FloatBuffer;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.ardor3d.bounding.BoundingBox;
import com.ardor3d.bounding.BoundingVolume;
import com.ardor3d.image.Texture;
import com.ardor3d.image.Texture.WrapMode;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.MathUtils;
import com.ardor3d.math.Vector3;
import com.ardor3d.renderer.IndexMode;
import com.ardor3d.renderer.Renderer;
import com.ardor3d.renderer.state.BlendState;
import com.ardor3d.renderer.state.BlendState.TestFunction;
import com.ardor3d.renderer.state.CullState;
import com.ardor3d.renderer.state.CullState.Face;
import com.ardor3d.renderer.state.MaterialState;
import com.ardor3d.renderer.state.MaterialState.MaterialFace;
import com.ardor3d.renderer.state.RenderState.StateType;
import com.ardor3d.renderer.state.TextureState;
import com.ardor3d.scenegraph.FloatBufferData;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.MeshData;
import com.ardor3d.scenegraph.hint.DataMode;
import com.ardor3d.scenegraph.hint.PickingHint;
import com.ardor3d.util.geom.BufferUtils;

/**
 * 
 * @author mallan
 *
 */
public class NavMapHeightField extends Mesh {
    private static Texture      s_cellTexture    = null;
    protected float             m_alphaTestValue = 0.44f;
    protected NavMapTexture     m_navMapTexture  = new NavMapTexture();

    public class HeightMapData {
        public       int      numXCells;
        public       int      numYCells;
        public       int      numXCellsNxtPwr2;
        public       int      numYCellsNxtPwr2;
        protected    int      totalVerts;
        public       float    xCellSize;
        public       float    yCellSize;
        public final Vector3  offset  = new Vector3();
        public       float[]  xCoords = null;
        public       float[]  yCoords = null;
        public       float[]  zCoords = null;
        public boolean[]      zIsFine = null;
        public       float[]  normals = null;
        public       float[]  colors  = null;
    }
    protected ReadWriteLock     m_mapDataLock = new ReentrantReadWriteLock();
    protected HeightMapData     m_mapData = new HeightMapData();
    protected HeightMapData     m_oldData = new HeightMapData();
    protected boolean           m_dirty   = false;
    protected boolean           m_invertWinding = false;
    protected boolean           m_doReinit = false;

    protected final int         BOUND_SIZE = 71;
    protected FloatBuffer       m_boundPoints = BufferUtils.createFloatBuffer(BOUND_SIZE*3);

    final static MaterialState s_diffuseMaterial = new MaterialState();
    final static MaterialState s_emissiveMaterial = new MaterialState();

    //    /** TravMap.setTranslation() is disabled */ 
    //    @Deprecated @Override
    //    public void setTranslation(ReadOnlyVector3 vec) {
    //        throw new RuntimeException("setTranslation is disabled");
    //    }
    //    /** TravMap.setTranslation() is disabled */ 
    //    @Deprecated @Override
    //    public void setTranslation(double x, double y, double z) {
    //        throw new RuntimeException("setTranslation is disabled");
    //    }
    //    /** TravMap.setTransform() is disabled */
    //    @Deprecated @Override
    //    public void setTransform(ReadOnlyTransform trans) {
    //        throw new RuntimeException("setTransform is disabled");
    //    }

    static {
        s_diffuseMaterial.setDiffuse(ColorRGBA.WHITE);
        s_diffuseMaterial.setSpecular(new ColorRGBA(0.1f, 0.1f, 0.1f, 1.0f));
        s_diffuseMaterial.setAmbient(ColorRGBA.BLACK);
        s_diffuseMaterial.setEmissive(ColorRGBA.BLACK);
        s_diffuseMaterial.setShininess(40);
        s_diffuseMaterial.setColorMaterialFace(MaterialFace.FrontAndBack);

        s_emissiveMaterial.setDiffuse(ColorRGBA.BLACK);
        s_emissiveMaterial.setSpecular(ColorRGBA.BLACK);
        s_emissiveMaterial.setAmbient(ColorRGBA.BLACK);
        s_emissiveMaterial.setEmissive(ColorRGBA.WHITE);
        s_emissiveMaterial.setColorMaterialFace(MaterialFace.FrontAndBack);
    }

    /**
     * 
     */
    //===============================================================
    public NavMapHeightField() {
        this("NavMapHeightField");
    }

    public NavMapHeightField(String name) {
        super(name);
        getMeshData().setVertexBuffer(BufferUtils.createFloatBuffer(0));
        BoundingBox bound = new BoundingBox(Vector3.ZERO, 100, 100, 1);
        setModelBound(bound);
        getSceneHints().setPickingHint(PickingHint.Collidable, false);
        getSceneHints().setPickingHint(PickingHint.Pickable, false);
        getSceneHints().setDataMode(DataMode.Arrays);
        
        BlendState bs = new BlendState();
        bs.setBlendEnabled(true);
        bs.setTestEnabled(true);
        bs.setTestFunction(TestFunction.GreaterThan);
        bs.setReference(m_alphaTestValue);

        setRenderState(bs);

        CullState cs = new CullState();
        cs.setCullFace(Face.None);
        //cs.setCullFace(Face.Back);
        setRenderState(cs);

        TextureState ts = new TextureState();
        if(s_cellTexture == null) { 
            try { 
                Texture cellTexture = AbstractRobot.getTex("cell.png");
                cellTexture.setWrap(WrapMode.Repeat);
                ts.setTexture(cellTexture,0);
                s_cellTexture = cellTexture;
            } 
            catch (Throwable e) { 
                /*ignore*/ 
            }
        }
        else {
            ts.setTexture(s_cellTexture,0);
        }
        ts.setTexture(m_navMapTexture, 1);
        setRenderState(ts);

        setRenderState(s_emissiveMaterial);
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
    
    public NavMapTexture getNavMapTexture() {
        return m_navMapTexture;
    }

    /**
     * A client should request a HeightMapData object 
     * using borrowHeightMapData() and fill out all
     * of the public fields. When complete, YOU MUST 
     * return the data using returnHeightMapData() to
     * release the write lock
     */
    public HeightMapData borrowHeightMapData() {
        m_mapDataLock.writeLock().lock();
        return m_mapData;
    }

    public void returnHeightMapData() {
        //final HeightMapData hmd = m_mapData;
        m_mapDataLock.writeLock().unlock();
        m_dirty = true;

        //VerveDebug.writeRgbaFloatArray(m_mapData.colors, m_mapData.numXCells, m_mapData.numYCells, "NavMapColors", null);
    }
    
    public void updateMeshData() {
        m_mapDataLock.readLock().lock();
        try {
            final HeightMapData hmd = m_mapData;
            final HeightMapData old = m_oldData;
            //super.setTranslation(hmd.offset);
            if(!( hmd.numXCells == old.numXCells &&
                    hmd.numYCells == old.numYCells &&
                    hmd.xCellSize == old.xCellSize &&
                    hmd.yCellSize == old.yCellSize ) ) {
                m_doReinit = true;
            }
            // set totalVerts in case map was previously invalidated
            hmd.totalVerts = hmd.numXCells * hmd.numYCells;
            if(m_doReinit) {
                initStaticHeightMapData(hmd);
                initMeshData(hmd);
            }
            m_navMapTexture.update(hmd);
            setMeshData(hmd);
        }
        finally {
            m_oldData.numXCells = m_mapData.numXCells;
            m_oldData.numYCells = m_mapData.numYCells;
            m_oldData.xCellSize = m_mapData.xCellSize;
            m_oldData.yCellSize = m_mapData.yCellSize;
            m_mapDataLock.readLock().unlock();
        }
    }

    public void setAlphaTestValue(float ref) {
        m_alphaTestValue = ref;
        BlendState bs = (BlendState) getLocalRenderState(StateType.Blend); 
        bs.setReference(m_alphaTestValue);
        if(m_alphaTestValue < 0.02f)
            bs.setTestEnabled(false);
        else
            bs.setTestEnabled(true);
    }

    public float getAlphaTestValue() {
        return m_alphaTestValue;
    }

    @Override
    public void draw(Renderer r) {
        if(m_dirty) {
            updateMeshData();
            m_dirty = false;
        }
        if(m_mapData.totalVerts > 0) {
            super.draw(r);
        }
    }

    /**
     * invalidate map so it won't draw
     */
    public void invalidate() {
        m_mapData.totalVerts = 0;
        m_dirty = false;
    }

    /**
     * @param numXCells number of cells on X axis
     * @param numYCells number of cells in Y axis
     * @param xCellSize width of cells in meters
     * @param yCellSize height of cells in meters
     * @param offset offset of the map corner from origin
     */
    public void initStaticHeightMapData(HeightMapData hmd) {
        hmd.totalVerts = hmd.numXCells * hmd.numYCells;
        hmd.numXCellsNxtPwr2 = MathUtils.nearestPowerOfTwo(hmd.numXCells);
        hmd.numYCellsNxtPwr2 = MathUtils.nearestPowerOfTwo(hmd.numYCells);

        final int   numXCells = hmd.numXCells;
        final int   numYCells = hmd.numYCells;
        final float xCellSize = hmd.xCellSize;
        final float yCellSize = hmd.yCellSize;
        final float xCellSize2 = hmd.xCellSize/2f;
        final float yCellSize2 = hmd.yCellSize/2f;

        float x,y;
        hmd.xCoords = new float[hmd.totalVerts];
        hmd.yCoords = new float[hmd.totalVerts];

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
        m_doReinit = false;
        int idx;
        final MeshData mdata = this.getMeshData();
        final int numStrips     = hmd.numYCells-1;
        final int vertsPerStrip = hmd.numXCells * 2;
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
        for(int y = 0; y < hmd.numYCells-1; y++) {
            for(int x = 0; x < hmd.numXCells; x++) {
                indexA = (y+0) * hmd.numXCells + x;
                indexB = (y+1) * hmd.numXCells + x;
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

        int vertSize = hmd.totalVerts*3;
        int txCrdSize = hmd.totalVerts*2;
        if(mdata.getVertexBuffer().capacity() < vertSize) {
            mdata.setVertexBuffer(BufferUtils.createFloatBuffer(vertSize));
            mdata.setTextureCoords(new FloatBufferData(BufferUtils.createFloatBuffer(txCrdSize),2), 0);
            mdata.setTextureCoords(new FloatBufferData(BufferUtils.createFloatBuffer(txCrdSize),2), 1);
        }
        mdata.getVertexBuffer().limit(vertSize);

        // set the tex coords
        FloatBuffer txBuf;
        int unit;
        //final float xCellSize2 = hmd.xCellSize/2f;
        //final float yCellSize2 = hmd.yCellSize/2f;
        // cell grid texture
        unit = 0;
        mdata.getTextureCoords(unit).getBuffer().limit(txCrdSize);
        txBuf = mdata.getTextureCoords(unit).getBuffer();
        txBuf.rewind();
        for(int vi = 0; vi < hmd.numYCells; vi++) {
            for(int ui = 0; ui < hmd.numXCells; ui++) {
                idx = vi * hmd.numXCells + ui;
                final float u = -0.5f + hmd.xCoords[idx]/hmd.xCellSize;
                final float v = -0.5f + hmd.yCoords[idx]/hmd.yCellSize;
                txBuf.put(u).put(v);
            }
        }

        // color texture
        float uMax = hmd.numXCells/(float)hmd.numXCellsNxtPwr2;
        float vMax = hmd.numYCells/(float)hmd.numYCellsNxtPwr2;
        final float hxcs = 0.5f/hmd.numXCellsNxtPwr2;
        final float hycs = 0.5f/hmd.numYCellsNxtPwr2;
        unit = 1;
        mdata.getTextureCoords(unit).getBuffer().limit(txCrdSize);
        txBuf = mdata.getTextureCoords(unit).getBuffer();
        txBuf.rewind();
        for(int vi = 0; vi < hmd.numYCells; vi++) {
            for(int ui = 0; ui < hmd.numXCells; ui++) {
                idx = vi * hmd.numXCells + ui;
                final float u = hxcs + ui/(float)hmd.numXCells * uMax;
                final float v = hycs + vi/(float)hmd.numYCells * vMax;
                txBuf.put(u).put(v);
            }
        }

        if(hmd.colors != null) {
            int clrSize = hmd.totalVerts*4;
            if(mdata.getColorBuffer() == null || mdata.getColorBuffer().capacity() < clrSize) {
                mdata.setColorBuffer(BufferUtils.createFloatBuffer(clrSize));
            }
            mdata.getColorBuffer().limit(clrSize);
            mdata.getColorBuffer().rewind();
        }
        else {
            if(mdata.getColorBuffer() != null) {
                mdata.setColorBuffer(null);
            }
        }
        mdata.setColorBuffer(null);

        if(hmd.normals != null) {
            int nrmSize = hmd.totalVerts*3;
            if(mdata.getNormalBuffer() == null || mdata.getNormalBuffer().capacity() < nrmSize) {
                mdata.setNormalBuffer(BufferUtils.createFloatBuffer(nrmSize));
            }
            mdata.getNormalBuffer().limit(nrmSize);
            setRenderState(s_diffuseMaterial);
        }
        else {
            if(mdata.getNormalBuffer() != null) {
                mdata.setNormalBuffer(null);
            }
            setRenderState(s_emissiveMaterial);
        }

    }

    /**
     * 
     * @param hmd
     */
    protected void setMeshData(HeightMapData hmd) {
        MeshData mdata = this.getMeshData();
        FloatBuffer floatBuffer;

        if(true) {
            floatBuffer = mdata.getVertexBuffer();
            floatBuffer.rewind();
            final float zoffset = hmd.offset.getZf(); // add config z offset
            for(int i = 0; i < hmd.totalVerts; i++) {
                floatBuffer.put(hmd.xCoords[i]).put(hmd.yCoords[i]).put(hmd.zCoords[i]+ zoffset);
            }
        }

        if(hmd.normals != null && (floatBuffer = mdata.getNormalBuffer()) != null) {
            floatBuffer.rewind();
            floatBuffer.put(hmd.normals);
        }

        if(hmd.colors != null && (floatBuffer = mdata.getColorBuffer()) != null) {
            floatBuffer.rewind();
            floatBuffer.put(hmd.colors);
        }

        m_boundPoints.limit(BOUND_SIZE*3);
        m_boundPoints.rewind();
        int step = 1+(hmd.totalVerts / BOUND_SIZE);
        int t = 0;
        for(int idx = 0; idx < hmd.totalVerts; idx += step) {
            t++;
            m_boundPoints.put(hmd.xCoords[idx]).put(hmd.yCoords[idx]).put(hmd.zCoords[idx]);
        }
        m_boundPoints.limit(t*3);
        final BoundingVolume bound = getModelBound();
        bound.computeFromPoints(m_boundPoints);
    }
}
