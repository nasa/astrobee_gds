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
package gov.nasa.arc.verve.ardor3d.scenegraph.shape;

import com.ardor3d.bounding.BoundingBox;
import com.ardor3d.math.Vector2;
import com.ardor3d.math.Vector3;
import com.ardor3d.renderer.IndexMode;
import com.ardor3d.scenegraph.FloatBufferData;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.util.geom.BufferUtils;

/**
 * Ported from Java3d
 * 
 * Cylinder object with a 'lathe' method that allows
 * you to permute the vertices 
 * 
 * @author mallan
 *
 */
public abstract class LathedCylinder extends Mesh {

	protected float		m_radius	= 0.5f;
	protected float		m_top		= 1f;
	protected float		m_bot		=-1f;
	protected boolean	m_topCap	= true;
	protected boolean	m_botCap	= true;
	protected int		m_numCols	= 16; // circumference
	protected int		m_numRows	= 2;  // length
	protected float[]	m_txMul		= new float[] { 1, 1 };
	
	protected float		m_rng	= Math.abs(m_top - m_bot);
	
	protected int[]		m_vmap = new int[] { 0,1,2 };
	
	public enum MajorAxis {
		X,
		Y,
		Z
	}
	
	public LathedCylinder(String name) {
		super(name);
	}
	
	public LathedCylinder(String name, float radius) {
		super(name);
		setRadius(radius);
	}
	
	public float getRadius() {
	    return m_radius;
	}
	
    /** NOTE call has no effect after initialize() is called */
	public void setEndCaps(boolean low, boolean hi) {
		m_botCap = low;
		m_topCap = hi;
	}
	
	/** NOTE: call has no effect after initialize() is called */
	public void setExtents(float low, float hi) {
		m_bot = low;
		m_top = hi;
		m_rng = Math.abs(m_top - m_bot);
	}
	
    /** NOTE call has no effect after initialize() is called */
	public void setRadius(float radius) {
		m_radius = radius;
	}
	
    /** NOTE call has no effect after initialize() is called */
	public void setTesselation(int aroundCircumference, int alongLength) {
		m_numCols = 1+aroundCircumference;
		m_numRows = alongLength;
	}
	
    /** NOTE call has no effect after initialize() is called */
	public void setTexCoordMultiplier( float circum, float length ) {
		m_txMul[1] = circum;
		m_txMul[0] = length;
	}
	
    /** NOTE call has no effect after initialize() is called */
	public void setMajorAxis(MajorAxis ma) {
		switch(ma) {
		case X:
			m_vmap[0] = 2;
			m_vmap[1] = 0;
			m_vmap[2] = 1;
			break;
		case Y:
			m_vmap[0] = 1;
			m_vmap[1] = 2;
			m_vmap[2] = 0;
			break;
		case Z:
			m_vmap[0] = 0;
			m_vmap[1] = 1;
			m_vmap[2] = 2;
			break;
		}
	}
	
	/**
	 * Override this method to permute the vertices of the cylinder
	 * @param vert
	 * @param tex
	 * @param rowpos
	 * @param colpos
	 */
	abstract protected void lathe(Vector3 vert, Vector2 tex, float rowpos, float colpos);
	
	
	/**
	 * 
	 *
	 */
	public void initialize() {
		getMeshData().setIndexMode(IndexMode.TriangleStrip);
		int numCols	  	= m_numCols;
		int numRows   	= m_numRows;
		int vertsPerStrip = 2 * (numCols+1);

		Vector3[][] crd = new Vector3[numRows][numCols];
		Vector3[][]	nrm = new Vector3[numRows][numCols];
		Vector2[][] txc = new Vector2[numRows][numCols];
		
		float[]	val = new float[3];
		float rl,cl;
		for(int r = 0; r < numRows; r++) {
			rl = (float)r/(float)(numRows-1);
			for(int c = 0; c < numCols; c++) {
				crd[r][c] = new Vector3();
				nrm[r][c] = new Vector3(0,0,0);
				txc[r][c] = new Vector2();
				
				cl = (float)c/(float)(numCols-1);
				
				val[0] = m_radius * (float)Math.sin(cl*2*Math.PI);
				val[1] = m_radius * (float)Math.cos(cl*2*Math.PI);
				val[2] = m_bot + rl * m_rng;
					
				crd[r][c].set(val[m_vmap[0]],
						      val[m_vmap[1]],
						      val[m_vmap[2]]);
				
				txc[r][c].set(rl * m_txMul[0], 
							  cl * m_txMul[1]);
				
				lathe( crd[r][c], txc[r][c], rl, cl );
			}
		}
		Vector3 va = new Vector3();
		Vector3 vb = new Vector3();
		Vector3 norm = new Vector3();
		int ia,ib,ci;
		for(int r = 0; r < (numRows-1); r++) {
			for(int c = 0; c < numCols; c++) {
				ia = (r+1)%numRows;
				ib = (c+1)%numCols;
				
				va.set(crd[r][c]);
				vb.set(crd[r][c]);
				va.subtractLocal(crd[ia][c]);
				vb.subtractLocal(crd[r][ib]);
				//va.sub(crd[r][c], crd[ia][c]);
				//vb.sub(crd[r][c], crd[r][ib]);
				
				//norm.cross(va, vb);
				norm.set(va);
				norm.crossLocal(vb);
				norm.normalizeLocal();
				
				nrm[r][c].addLocal(norm);
				nrm[ia][c].addLocal(norm);
				nrm[r][ib].addLocal(norm);
			}
		}
		for(int r = 0; r < numRows; r++) {
			nrm[r][0].addLocal( nrm[r][numCols-1]);
			nrm[r][numCols-1].addLocal( nrm[r][0]);
			for(int c = 0; c < numCols; c++) {
				nrm[r][c].normalizeLocal();
			}
		}
		
		int capStrips = 0;
		int capVerts  = 0;
		if(m_topCap == true) {
			capStrips++;
		}
		if(m_botCap == true) {
			capStrips++;
		}
		capVerts  = capStrips * vertsPerStrip;

		int numStrips	= m_numRows-1;
		int numVerts	= numStrips * vertsPerStrip;

		int currentStrip = 0;
		int vertStart = 0;
		int texcStart = 0;
		int thisPair;
		float[] fcrd = new float[3 * (numVerts+capVerts)];
		float[] fnrm = new float[3 * (numVerts+capVerts)];
		float[] ftxc = new float[2 * (numVerts+capVerts)];
		for(int r = 0; r < numStrips; r++) {
			currentStrip = r;
			vertStart = (r*(numCols+1)) * 6;
			texcStart = (r*(numCols+1)) * 4;
			for(int c = 0; c < numCols+1; c++) {
				ci = c%numCols;
				thisPair = vertStart + c * 6;
				fcrd[thisPair+0] = (float)crd[r+0][ci].getX();
				fcrd[thisPair+1] = (float)crd[r+0][ci].getY();
				fcrd[thisPair+2] = (float)crd[r+0][ci].getZ();
				
				fcrd[thisPair+3] = (float)crd[r+1][ci].getX();
				fcrd[thisPair+4] = (float)crd[r+1][ci].getY();
				fcrd[thisPair+5] = (float)crd[r+1][ci].getZ();
				
				fnrm[thisPair+0] = (float)nrm[r+0][ci].getX();
				fnrm[thisPair+1] = (float)nrm[r+0][ci].getY();
				fnrm[thisPair+2] = (float)nrm[r+0][ci].getZ();
				
				fnrm[thisPair+3] = (float)nrm[r+1][ci].getX();
				fnrm[thisPair+4] = (float)nrm[r+1][ci].getY();
				fnrm[thisPair+5] = (float)nrm[r+1][ci].getZ();
				
				thisPair = texcStart + c * 4;
				ftxc[thisPair+0] = (float)txc[r+0][ci].getX();
				ftxc[thisPair+1] = (float)txc[r+0][ci].getY();
				
				ftxc[thisPair+2] = (float)txc[r+1][ci].getX();
				ftxc[thisPair+3] = (float)txc[r+1][ci].getY();
			}
		}
		
		if(m_botCap == true) {
			currentStrip++;
			vertStart = (currentStrip*(numCols+1)) * 6;
			texcStart = (currentStrip*(numCols+1)) * 4;
			for(int c = 0; c < numCols+1; c++) {
				int r = 0;
				ci = c%numCols;
				thisPair = vertStart + c * 6;
				val[0] = (float) crd[r][ci].getX();
				val[1] = (float) crd[r][ci].getY();
				val[2] = (float) crd[r][ci].getZ();
				fcrd[thisPair+3] = val[0];
				fcrd[thisPair+4] = val[1];
				fcrd[thisPair+5] = val[2];
				for(int i = 0; i < 3; i++) {
					if(m_vmap[i] != 2)
						val[i] = 0;
				}
				fcrd[thisPair+0] = val[0];
				fcrd[thisPair+1] = val[1];
				fcrd[thisPair+2] = val[2];
				
				
				val[0] = fnrm[thisPair+3] = 0;
				val[1] = fnrm[thisPair+4] = 0;
				val[2] = fnrm[thisPair+5] =-1;
				fnrm[thisPair+0] = val[m_vmap[0]];
				fnrm[thisPair+1] = val[m_vmap[1]];
				fnrm[thisPair+2] = val[m_vmap[2]];
				fnrm[thisPair+3] = val[m_vmap[0]];
				fnrm[thisPair+4] = val[m_vmap[1]];
				fnrm[thisPair+5] = val[m_vmap[2]];
				
				thisPair = texcStart + c * 4;
				ftxc[thisPair+0] = (float) txc[r][ci].getX();
				ftxc[thisPair+1] = (float) txc[r][ci].getY();
				ftxc[thisPair+2] = (float) txc[r][ci].getX();
				ftxc[thisPair+3] = (float) txc[r][ci].getY();
			}
		}
		if(m_topCap == true) {
			currentStrip++;
			vertStart = (currentStrip*(numCols+1)) * 6;
			texcStart = (currentStrip*(numCols+1)) * 4;
			for(int c = 0; c < numCols+1; c++) {
				int r = numRows-1;
				ci = c%numCols;
				thisPair = vertStart + c * 6;
				val[0] = (float)crd[r][ci].getX();
				val[1] = (float)crd[r][ci].getY();
				val[2] = (float)crd[r][ci].getZ();
				fcrd[thisPair+0] = val[0];
				fcrd[thisPair+1] = val[1];
				fcrd[thisPair+2] = val[2];
				for(int i = 0; i < 3; i++) {
					if(m_vmap[i] != 2)
						val[i] = 0;
				}
				fcrd[thisPair+3] = val[0];
				fcrd[thisPair+4] = val[1];
				fcrd[thisPair+5] = val[2];
				
				val[0] = fnrm[thisPair+3] = 0;
				val[1] = fnrm[thisPair+4] = 0;
				val[2] = fnrm[thisPair+5] = 1;
				fnrm[thisPair+0] = val[m_vmap[0]];
				fnrm[thisPair+1] = val[m_vmap[1]];
				fnrm[thisPair+2] = val[m_vmap[2]];
				fnrm[thisPair+3] = val[m_vmap[0]];
				fnrm[thisPair+4] = val[m_vmap[1]];
				fnrm[thisPair+5] = val[m_vmap[2]];
				
				thisPair = texcStart + c * 4;
				ftxc[thisPair+0] = (float)txc[r][ci].getX();
				ftxc[thisPair+1] = (float)txc[r][ci].getY();
				ftxc[thisPair+2] = (float)txc[r][ci].getX();
				ftxc[thisPair+3] = (float)txc[r][ci].getY();
			}
		}

		int[] stripCounts = new int[numStrips+capStrips];
	    IndexMode[] modes = new IndexMode[numStrips+capStrips];
		for(int sc = 0; sc < numStrips+capStrips; sc++) {
			stripCounts[sc] = vertsPerStrip;
			modes[sc] = IndexMode.TriangleStrip;
		}
		//int totalVerts = numVerts + capVerts;
		getMeshData().setVertexBuffer(BufferUtils.createFloatBuffer(fcrd));
		getMeshData().setNormalBuffer(BufferUtils.createFloatBuffer(fnrm));
		getMeshData().setTextureCoords(new FloatBufferData(BufferUtils.createFloatBuffer(ftxc),2), 0);
//		short[] idx = new short[totalVerts];
//		for(short i = 0; i < totalVerts; i++) {
//			idx[i] = i;
//		}
//        getMeshData().setIndexBuffer(BufferUtils.createShortBuffer(idx));
        getMeshData().setIndexModes(modes);
        getMeshData().setIndexLengths(stripCounts);
		
		BoundingBox bound = new BoundingBox();
		bound.computeFromPoints(getMeshData().getVertexBuffer());
		this.setModelBound(bound);
	}

}
