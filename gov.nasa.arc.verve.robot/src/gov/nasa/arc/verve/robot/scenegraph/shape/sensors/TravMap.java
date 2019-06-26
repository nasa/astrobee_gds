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

import org.apache.log4j.Logger;

import com.ardor3d.bounding.BoundingSphere;
import com.ardor3d.image.Texture;
import com.ardor3d.image.Texture.WrapMode;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyTransform;
import com.ardor3d.math.type.ReadOnlyVector3;
import com.ardor3d.renderer.IndexMode;
import com.ardor3d.renderer.Renderer;
import com.ardor3d.renderer.state.BlendState;
import com.ardor3d.renderer.state.BlendState.TestFunction;
import com.ardor3d.renderer.state.RenderState.StateType;
import com.ardor3d.renderer.state.TextureState;
import com.ardor3d.renderer.state.ZBufferState;
import com.ardor3d.scenegraph.FloatBufferData;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.MeshData;
import com.ardor3d.scenegraph.hint.LightCombineMode;
import com.ardor3d.scenegraph.hint.PickingHint;
import com.ardor3d.util.geom.BufferUtils;

/**
 * This handles the drawing of the Traversibility Map. 
 * Note that Spatial.setTranslation() is disabled for this class 
 * because m_mapOffset uses the local transform (for rendering
 * efficiency). Origin and/or rotation offsets should be done in
 * a parent Node
 */
public abstract class TravMap extends Mesh {	
	private static Logger logger = Logger.getLogger(TravMap.class);
	protected int      m_totalVerts	    = 0;	
	protected int      m_numXCells		= 0;	
	protected int      m_numYCells		= 0;	
	boolean            m_initialized	= false;

	protected float    m_alphaTestValue = 0.45f;

	static Texture     s_texture	= null;

	protected Vector3[][]      m_mapCoords		= null;
	protected Vector3          m_mapOffset      = new Vector3();
	protected ColorRGBA[][]    m_mapColors		= null;

	/**
	 * 
	 *
	 */
	//===============================================================
	protected TravMap() {
		// set up some bogus mesh data until we've initialized the 
		// mesh... if this isn't done, picking gets messed up?
		getMeshData().setVertexBuffer(BufferUtils.createFloatBuffer(0));
		BoundingSphere bound = new BoundingSphere(5, Vector3.ZERO);
		this.setModelBound(bound);

		setRenderState(new BlendState());
		setRenderState(new TextureState());

		ZBufferState zs = new ZBufferState();
		zs.setWritable(true);
		zs.setFunction(ZBufferState.TestFunction.LessThan);  
		setRenderState(zs);
	}

	public boolean isInitialized() {
		return m_initialized;
	}

	@Override
	public void draw(Renderer r) {
		if(m_initialized) // XXX HACK XXX
			super.draw(r);
	}

	/**
	 * @param xCells number of cells on X axis
	 * @param yCells number of cells in Y axis
	 * @param xCellSize width of cells in meters
	 * @param yCellSize height of cells in meters
	 * @param offset offset of the map corner from origin
	 */
	public void initialize(int xCells, int yCells, float xCellSize, float yCellSize, Vector3 offset) {
		m_numXCells	= xCells;
		m_numYCells	= yCells;
		m_totalVerts = xCells * yCells;

		float x,y;
		float r,g;
		// create and initialize 2D Vertex3f arrays
		m_mapCoords  = new Vector3[xCells][];
		m_mapColors  = new ColorRGBA[xCells][];

		for(int row = 0; row < xCells; row++) {
			//x = offset.getXf() + row * xCellSize;
			x = row * xCellSize;
			r = (float)row/(float)xCells;
			m_mapCoords[row]  = new Vector3[yCells];
			m_mapColors[row]  = new ColorRGBA [yCells];

			for(int col = 0; col < yCells; col++) {
				//y = offset.getYf() + col * yCellSize;
				y = col * yCellSize;
				g = (float)col/(float)yCells;
				// swap x & y to match rover frame
				//m_mapCoords[row][col]  = new Vector3( x, y, offset.getZf() );
				m_mapCoords[row][col]  = new Vector3( x, y, 0);
				m_mapColors[row][col]  = new ColorRGBA( r, g, 0, 1 );
			}
		}
		m_mapOffset.set(offset);
		super.setTranslation(m_mapOffset);

		initVertices();
		initRenderStates();

		getSceneHints().setPickingHint(PickingHint.Collidable, false);
		getSceneHints().setPickingHint(PickingHint.Pickable, false);

		m_initialized = true;
	}

	/** TravMap.setTranslation() is disabled, DO NOT CALL
	 * If you need to transform the TravMap, place it under a Node
	 */ 
	@Deprecated @Override
	public void setTranslation(ReadOnlyVector3 vec) {
		throw new RuntimeException("TravMap.setTranslation is disabled");
	}
	/** TravMap.setTranslation() is disabled, DO NOT CALL
	 * If you need to transform the TravMap, place it under a Node
	 */ 
	@Deprecated @Override
	public void setTranslation(double x, double y, double z) {
		throw new RuntimeException("TravMap.setTranslation is disabled");
	}
	/** TravMap.setTransform() is disabled, DO NOT CALL
	 * If you need to transform the TravMap, place it under a Node
	 */
	@Deprecated @Override
	public void setTransform(ReadOnlyTransform trans) {
		throw new RuntimeException("TravMap.setTransform is disabled");
	}

	public void setAlphaTestValue(float ref) {
		m_alphaTestValue = ref;
		BlendState bs = (BlendState) getLocalRenderState(StateType.Blend); 
		if(bs != null) {
			bs.setReference(m_alphaTestValue);
			if(m_alphaTestValue < 0.02f)
				bs.setTestEnabled(false);
			else
				bs.setTestEnabled(true);
		}
	}

	public float getAlphaTestValue() {
		return m_alphaTestValue;
	}

	/**
	 */
	public void initRenderStates() {
		getSceneHints().setLightCombineMode(LightCombineMode.Off);

		BlendState bs = (BlendState) getLocalRenderState(StateType.Blend); 
		bs.setTestEnabled(true);
		bs.setTestFunction(TestFunction.GreaterThan);
		bs.setReference(m_alphaTestValue);
		this.setRenderState(bs);

		TextureState ts = (TextureState) getLocalRenderState(StateType.Texture);
		if(s_texture == null) { // XXX FIXME Replace with generated grid
			try { 
			    s_texture = AbstractRobot.getTex("cell.png");
			    s_texture.setWrap(WrapMode.Repeat);
//				s_texture = DataBundleHelper.loadTexture("robot", 
//						"images/cell.png",
//						Texture.WrapMode.Repeat, 
//						Texture.MinificationFilter.Trilinear, 
//						Texture.MagnificationFilter.Bilinear, 
//						0, false);
				ts.setTexture(s_texture);
			} catch (Throwable e) { /*ignore*/ }
		}
		else {
			ts.setTexture(s_texture);
		}
		this.setRenderState(ts);
	}

	/**
	 */
	public void initVertices() {
		try {	
			int numStrips     = m_numXCells-1;
			int vertsPerStrip = m_numYCells * 2;
			int numIndexes    = numStrips * vertsPerStrip;
			int[] stripIndexCounts = new int[numStrips];
			IndexMode[] indexMode = new IndexMode[numStrips];
			for(int i = 0; i < numStrips; i++) {
				stripIndexCounts[i] = vertsPerStrip;
				indexMode[i] = IndexMode.TriangleStrip;
			}

			MeshData mdata = this.getMeshData();
			mdata.setIndexLengths(stripIndexCounts);
			mdata.setIndexModes(indexMode);
			
			float[] txc   = new float[m_totalVerts*2];
			int[] idx     = new int[numIndexes];

			int index = 0;
			int indexA;
			int indexB;
			for(int x = 0; x < numStrips; x++) {
				for(int y = 0; y < m_numYCells; y++) {
					indexA =  x    * m_numYCells + y;
					indexB = (x+1) * m_numYCells + y;
					idx[index+0] = indexB;
					idx[index+1] = indexA;
					index += 2;
				}
			}

			index = 0;
			for(int x = 0; x < m_numXCells; x++) {
				for(int y = 0; y < m_numYCells; y++) {
					txc[index+0] = x;
					txc[index+1] = y;
					index += 2;
				}
			}

			mdata.setVertexBuffer(BufferUtils.createFloatBuffer(m_totalVerts*3));
			mdata.setColorBuffer(BufferUtils.createFloatBuffer(m_totalVerts*4));
			mdata.setTextureCoords(new FloatBufferData(BufferUtils.createFloatBuffer(txc),2), 0);
			mdata.setIndexBuffer(BufferUtils.createIntBuffer(idx));
			updateBuffers();

			updateModelBound();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void updateBuffers() {
		FloatBuffer crdBuf = getMeshData().getVertexBuffer();
		FloatBuffer clrBuf = getMeshData().getColorBuffer();
		crdBuf.rewind();
		clrBuf.rewind();

		int xCells = m_mapCoords.length;
		int yCells = m_mapCoords[0].length;
		for(int xi = 0; xi < xCells; xi++) {
			for(int yi = 0; yi < yCells; yi++) {
				crdBuf.put(m_mapCoords[xi][yi].getXf());
				crdBuf.put(m_mapCoords[xi][yi].getYf());
				crdBuf.put(m_mapCoords[xi][yi].getZf());

				clrBuf.put(m_mapColors[xi][yi].getRed());
				clrBuf.put(m_mapColors[xi][yi].getGreen());
				clrBuf.put(m_mapColors[xi][yi].getBlue());
				clrBuf.put(m_mapColors[xi][yi].getAlpha());
			}
		}
	}

	public void updateMeshData() {
		// call non-disabled super 
		super.setTranslation(m_mapOffset);
		if(m_initialized) {
			updateBuffers();
		}
	}

	public void resetMapData() {
		if(m_initialized) {
			logger.debug("resetMapData");
			m_mapOffset.set(0,0,0);
			super.setTranslation(m_mapOffset);
			int xCells = m_mapCoords.length;
			int yCells = m_mapCoords[0].length;
			for(int xi = 0; xi < xCells; xi++) {
				for(int yi = 0; yi < yCells; yi++) {
					m_mapCoords[xi][yi].setZ(0);
					m_mapColors[xi][yi].set(0,0,0,0);
				}
			}
		}
	}

}
