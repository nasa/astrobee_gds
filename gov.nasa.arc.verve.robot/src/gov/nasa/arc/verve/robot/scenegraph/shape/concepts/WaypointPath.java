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
package gov.nasa.arc.verve.robot.scenegraph.shape.concepts;

import gov.nasa.arc.verve.robot.AbstractRobot;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Vector;

import com.ardor3d.image.Texture.WrapMode;
import com.ardor3d.image.Texture2D;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Matrix4;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.Vector4;
import com.ardor3d.renderer.IndexMode;
import com.ardor3d.renderer.Renderer;
import com.ardor3d.renderer.state.BlendState;
import com.ardor3d.renderer.state.MaterialState;
import com.ardor3d.renderer.state.TextureState;
import com.ardor3d.scenegraph.FloatBufferData;
import com.ardor3d.scenegraph.Line;
import com.ardor3d.util.geom.BufferUtils;

/**
 * ported from Java3d
 * @author mallan
 *
 */
public class WaypointPath extends Line {
	float[]				m_vrt		= null;
	float[]				m_txc		= null;
	float[]				m_clr		= null;

	FloatBuffer			m_vrtBuffer	= null;
	FloatBuffer			m_clrBuffer	= null;
	FloatBufferData		m_texCoords = null;

	int					m_numVerts	= 0;
	Matrix4             m_texMatrix = new Matrix4();
	Vector4             m_texOffset = new Vector4( 0,0,0,1 );
	int					m_lineWidth	= 3;

	float				m_upOffset	= 0.05f;

	final ColorRGBA	 	boost	    = new ColorRGBA(0.2f, 0.2f, 0.2f,1);
	Texture2D			m_texture	= null;

	boolean				m_verticesNeedUpdate = true;

	public class UpdateInfo {
		int        index;
		Vector3    pos		= null;
		ColorRGBA  color	= null;
		public UpdateInfo(int index) {
			this.index = index;
		}
	}
	Vector<UpdateInfo>	m_updateList = new Vector<UpdateInfo>();

	static int	s_instance = 0;
	int 		m_instance;
	public WaypointPath(String name) {
		m_instance = s_instance++;
		setName((name != null) ? name : "WaypointPath"+m_instance);
	}

	@Override
	public void draw(Renderer r) {
		if(m_numVerts > 1) // XXX HACKISH XXX
			super.draw(r);
	}


	public void init(ArrayList<WaypointIcon> waypointInfos) {
		m_numVerts = 0;
		createGeometry(waypointInfos);

		this.setLineWidth(m_lineWidth);
		this.setAntialiased(true);
		this.getMeshData().setIndexMode(IndexMode.LineStrip);

		try { 
		    m_texture = AbstractRobot.getTex("ChaseLight.png");
		    m_texture.setWrap(WrapMode.Repeat);
			//m_texture = DataBundleHelper.loadTexture("robot", "images/ChaseLight.png");
			TextureState ts = new TextureState();
			ts.setTexture(m_texture, 0);
			ts.setEnabled(true);
			this.setRenderState(ts);
		} 
		catch (Throwable e) {
			e.printStackTrace();
		}

		BlendState blend = new BlendState();
		blend.setBlendEnabled(true);
		blend.setEnabled(true);
		this.setRenderState(blend);

		MaterialState matl = new MaterialState();
		matl.setAmbient (ColorRGBA.BLACK);
		matl.setDiffuse (ColorRGBA.BLACK);
		matl.setSpecular(ColorRGBA.BLACK);
		matl.setEmissive(ColorRGBA.YELLOW);
		matl.setColorMaterial(MaterialState.ColorMaterial.Emissive);
		matl.setEnabled(true);
		this.setRenderState(matl);

		//BoundingBox bound = new BoundingBox();
		//bound.computeFromPoints(this.getMeshData().getVertexBuffer());
		setModelBound(null);
		getSceneHints().setAllPickingHints(false);
	}

	/**
	 * 
	 * @param waypointInfos
	 * @return
	 */
	@SuppressWarnings("null")
    synchronized void createGeometry(ArrayList<WaypointIcon> waypointInfos) {
		if(waypointInfos.size() < 2) {
			m_numVerts = 0;
		}
		else {			
			m_numVerts = (waypointInfos.size()*2) - 2;
			m_vrt	= new float[m_numVerts*3];
			m_txc	= new float[m_numVerts*2];
			m_clr	= new float[m_numVerts*4];			

			float 		thisLen = 0;
			float 		lastLen = 0;
			Vector3 	thisPos = null;
			Vector3 	lastPos = null;
			ColorRGBA	thisClr = new ColorRGBA();
			ColorRGBA	lastClr = new ColorRGBA();
			int v = 0;
			int c = 0;
			int t = 0;
			for(WaypointIcon wpi : waypointInfos) {
				m_updateList.add(new UpdateInfo(-1));
				//wpi.getStatusProvider().getColorFromStatus(wpi.m_status, thisClr);
				wpi.getStatusProvider().getBaseColor(thisClr);
				if(lastPos == null) {
					thisPos = new Vector3();
					lastPos = new Vector3(wpi.m_pos);
					lastClr.set(thisClr);
				}
				else {
					thisPos.set(wpi.m_pos);
					m_clr[c+0] = boost.getRed()   + lastClr.getRed();
					m_clr[c+1] = boost.getGreen() + lastClr.getGreen();
					m_clr[c+2] = boost.getBlue()  + lastClr.getBlue();
					m_clr[c+3] = 1;
					m_clr[c+4] = boost.getRed()   + thisClr.getRed();
					m_clr[c+5] = boost.getGreen() + thisClr.getGreen();
					m_clr[c+6] = boost.getBlue()  + thisClr.getBlue();
					m_clr[c+7] = 1;
					c += 8;

					m_vrt[v+0] = lastPos.getXf();
					m_vrt[v+1] = lastPos.getYf();
					m_vrt[v+2] = lastPos.getZf()+m_upOffset;
					m_vrt[v+3] = thisPos.getXf();
					m_vrt[v+4] = thisPos.getYf();
					m_vrt[v+5] = thisPos.getZf()+m_upOffset;
					v += 6;

					lastPos.subtractLocal(wpi.m_pos);
					thisLen -= lastPos.length();
					lastPos.set(wpi.m_pos);

					m_txc[t+0] = 0.5f;
					m_txc[t+1] = lastLen;
					m_txc[t+2] = 0.5f;
					m_txc[t+3] = thisLen;
					t += 4;

					lastLen = thisLen;
					lastClr.set(thisClr);
				}
			}
			//this.getMeshData().setVertexCount(m_numVerts);
			m_vrtBuffer = BufferUtils.createFloatBuffer(m_vrt);
			m_clrBuffer = BufferUtils.createFloatBuffer(m_clr);
			m_texCoords = new FloatBufferData(BufferUtils.createFloatBuffer(m_txc),2);

			this.getMeshData().setVertexBuffer( m_vrtBuffer );
			this.getMeshData().setColorBuffer(  m_clrBuffer );
			this.getMeshData().setTextureCoords(m_texCoords, 0);
			int[] idx = new int[m_numVerts];
			for(int i = 0; i < m_numVerts; i++) {
				idx[i] = i;
			}
			this.getMeshData().setIndexBuffer(BufferUtils.createIntBuffer(idx));
			this.getMeshData().setIndexLengths(new int[] { m_numVerts });
		}
	}

	public WaypointPath.UpdateInfo getUpdateElement(int index) {
		if(m_numVerts > 1) {
			return m_updateList.elementAt(index);
		}
		return null;
	}

	public void updateElements() {
		m_verticesNeedUpdate = true;
	}

	public synchronized void handleUpdate(long time) {
		if(m_verticesNeedUpdate) {
			updateVertices();
			m_verticesNeedUpdate = false;
		}
		int	speed	= 1000 * 2;
		m_texOffset.setY( ((double)(time%speed))/speed );
		m_texMatrix.setRow(3, m_texOffset);
		
		if(m_texture != null) {
			m_texture.setTextureMatrix(m_texMatrix);
		}
	}

	/**
	 * go through the update list and move the vertices, change color if necessary
	 */
	void updateVertices() {
		if(m_numVerts > 1) {
			for(UpdateInfo info : m_updateList) {
				for(int p = -1; p <= 0; p++) {

					int vi	= (info.index * 2) + p;

					if(vi >= 0 && vi < m_numVerts) {

						int ci	= vi * 3;
						if(info.pos != null) {
							m_vrt[ci+0] = info.pos.getXf();
							m_vrt[ci+1] = info.pos.getYf();
							m_vrt[ci+2] = info.pos.getZf() + m_upOffset;
						}

						int li = vi * 4;
						if(info.color != null) {
							m_clr[li+0] = boost.getRed()   + info.color.getRed();
							m_clr[li+1] = boost.getGreen() + info.color.getGreen();
							m_clr[li+2] = boost.getBlue()  + info.color.getBlue();
							m_clr[li+3] = 1;
						}
					}
				}
				// reset the info struct
				info.pos	= null;
				info.color	= null;
			}
			// this is probably really slow... 
			try {
				m_vrtBuffer.rewind();
				m_clrBuffer.rewind();
				m_vrtBuffer.put(m_vrt);
				m_clrBuffer.put(m_clr);
			}
			catch(Throwable t) {
				System.out.println(this.getClass().getSimpleName()+".updateVertices() vrtBuffer capacity = "+m_vrtBuffer.capacity()+", array.length="+m_vrt.length+".");
				System.out.println(this.getClass().getSimpleName()+".updateVertices() clrBuffer capacity = "+m_clrBuffer.capacity()+", array.length="+m_clr.length+".");
				t.printStackTrace();
			}
		}
	}
}
