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

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.logging.Logger;

import com.ardor3d.bounding.BoundingSphere;
import com.ardor3d.image.Texture2D;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Vector2;
import com.ardor3d.math.type.ReadOnlyColorRGBA;
import com.ardor3d.renderer.IndexMode;
import com.ardor3d.renderer.queue.RenderBucketType;
import com.ardor3d.renderer.state.BlendState;
import com.ardor3d.renderer.state.BlendState.DestinationFunction;
import com.ardor3d.renderer.state.BlendState.SourceFunction;
import com.ardor3d.renderer.state.CullState;
import com.ardor3d.renderer.state.LightState;
import com.ardor3d.renderer.state.MaterialState;
import com.ardor3d.renderer.state.RenderState;
import com.ardor3d.renderer.state.TextureState;
import com.ardor3d.scenegraph.FloatBufferData;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.hint.CullHint;
import com.ardor3d.util.geom.BufferUtils;



/**
 * ported from Java3d
 * XXX polygon winding appears to be inverted since the port... fix it!
 * 
 * Textured ring geometry. Although polygons are planar, normals can be 
 * tweaked to give the appearance of concavity. 
 * 
 * @author mallan
 *
 */
public class TexRing extends Mesh {
    static Logger logger = Logger.getLogger(TexRing.class.getName());  
	
	float[]		m_radius 	= new float[] { 0.6f, 1.0f };
	ColorRGBA	m_clr	 	= new ColorRGBA( 1,1,0,1 );
	int[]		m_tess	 	= new int[] { 2, 16 };
	float		m_concave	=  0;
	float		m_alpha		=  1.0f;
	boolean		m_flipFace	= false;
	float		m_texMul	=  1.0f;
	Texture2D	m_texture	= null;
		
    float[] m_crd = null;
    float[] m_nrm = null;
    float[] m_txc = null;
    short[] m_idx = null;

	public TexRing(String name, int tessRings, int tessSteps) {
		super(name);
		m_tess[0] = tessRings;
		m_tess[1] = tessSteps;
	}
	
	public void setRadius(Vector2 radius) {
	    setRadius(radius.getXf(), radius.getYf());
	}
	
	public void setRadius(float inner, float outer) {
		m_radius[0] = inner;
		m_radius[1]	= outer;
		if(inner > outer) {
			m_flipFace = true;
		}
		updateData();
	}
	
	/**
	 * tweak the normals to give the illusion of concavity. Range of -2 to 2 
	 * @param concavity range ~ -2 to 2
	 */
	public void setConcave(float concavity) {
		m_concave = concavity;
	    updateData();
	}
	
	/**
	 * multiplier for v direction of tex (around ring)
	 */
	public void setTexMultiplier(float val) {
		m_texMul = val;
		updateData();
	}
	
	/**
	 * set alpha. If alpha = 1, alpha test only. If < 1, 
	 * additive blend.
	 * @param a
	 */
	public void setAlpha(float a) {
		BlendState bs = (BlendState) this.getLocalRenderState(RenderState.StateType.Blend);
		if(bs != null) {
			if( a > 0.99f) {
				bs.setReference(0.3f);
				bs.setBlendEnabled(false);
				getSceneHints().setRenderBucketType(RenderBucketType.Opaque);
				getSceneHints().setCullHint(CullHint.Inherit);
			}
			else if ( a < 0.02 ) {
                getSceneHints().setCullHint(CullHint.Always);
			}
			else {
				bs.setReference(0.01f);
				bs.setBlendEnabled(true);
				bs.setSourceFunction(SourceFunction.SourceAlpha);
				bs.setDestinationFunction(DestinationFunction.One);
				getSceneHints().setRenderBucketType(RenderBucketType.Transparent);
				getSceneHints().setCullHint(CullHint.Inherit);
			}
		}
		MaterialState ms = (MaterialState) this.getLocalRenderState(RenderState.StateType.Material);
		if(ms != null) {  
		    ColorRGBA tmpClr = ColorRGBA.fetchTempInstance();

			tmpClr.set(ms.getDiffuse());
			tmpClr.setAlpha(a);
			ms.setDiffuse(tmpClr);
			
			ColorRGBA.releaseTempInstance(tmpClr);
		}
	}
	    
    /**
     * 
     * @param texture
     */
	public void setTexture(Texture2D texture) {
		TextureState ts = (TextureState) this.getLocalRenderState(RenderState.StateType.Texture);
		if(ts == null) {
			ts = new TextureState();
			this.setRenderState(ts);
		}
		ts.setTexture(texture, 0);
		ts.setEnabled(true);
	}
	
	/**
	 * 
	 * @param ms
	 * @param clr
	 */
	protected void setupMaterialState(MaterialState ms, ReadOnlyColorRGBA clr) {
		if(ms != null) {
			final float d  = 0.3f;
			final float s  = 0.1f;
			final float sa = 0.2f;
			final float e  = 0.7f;
			//ColorRGBA c = new ColorRGBA();
			//ms.setDiffuse(null);
			final float R = clr.getRed();
			final float G = clr.getGreen();
			final float B = clr.getBlue();
			ms.setDiffuse (new ColorRGBA(d*R, d*G, d*B, 1));
			ms.setSpecular(new ColorRGBA(sa+s*R, sa+s*G, sa+s*B, 1));
			ms.setEmissive(new ColorRGBA(e*R, e*G, e*B, 1));
			ms.setAmbient (new ColorRGBA(0, 0, 0, 1));
			ms.setShininess(60);
		}
	}
	
    public void createDefaultRenderStates() {
        ReadOnlyColorRGBA color = getDefaultColor();
        MaterialState ms = new MaterialState();
        setupMaterialState(ms, color);
        ms.setEnabled(true);
        this.setRenderState(ms);

        CullState cs = new CullState();
        cs.setCullFace(CullState.Face.None);
        cs.setEnabled(true);
        this.setRenderState(cs);

        LightState ls =new LightState();
        ls.setTwoSidedLighting(true);
        ls.setSeparateSpecular(true);
        ls.setEnabled(true);
        this.setRenderState(ls);
        
        BlendState bs = new BlendState();
        bs.setBlendEnabled(false);
        bs.setTestEnabled(true);
        bs.setTestFunction(BlendState.TestFunction.GreaterThan);
        bs.setReference(0.95f);
        this.setRenderState(bs);
        setAlpha(color.getAlpha());
    }
    
	/**
	 * initalize geometry
	 */
	public void initialize() {		
		int	numStrips	= m_tess[0];
		int numPairs	= m_tess[1]+1;
		int totalVerts	= numStrips * numPairs * 2;
		
        m_idx = new short[totalVerts];
		m_crd = new float[totalVerts*3];
		m_nrm = new float[totalVerts*3];
		m_txc	= new float[totalVerts*2];

		calculateVertexData(numStrips, numPairs, totalVerts);
		
		getMeshData().setIndexMode(IndexMode.TriangleStrip);
		getMeshData().setVertexBuffer(BufferUtils.createFloatBuffer(m_crd));
		getMeshData().setNormalBuffer(BufferUtils.createFloatBuffer(m_nrm));
		getMeshData().setTextureCoords(new FloatBufferData(BufferUtils.createFloatBuffer(m_txc),2),0);
		getMeshData().setIndexBuffer(BufferUtils.createShortBuffer(m_idx));
		
		BoundingSphere bound = new BoundingSphere();
		//bound.computeFromPoints(this.getMeshData().getVertexBuffer());
		setModelBound(bound);
	}
	
	/**
	 * 
	 * @param i
	 * @param nrm
	 */
	void normalize(int i, float[] nrm) {
		float len = (float)Math.sqrt(	
				nrm[i+0]*nrm[i+0]+
				nrm[i+1]*nrm[i+1]+
				nrm[i+2]*nrm[i+2]);
		nrm[i+0] /= len;
		nrm[i+1] /= len;
		nrm[i+2] /= len;
	}
	
	/**
	 * 
	 */
	public void updateData() {
	    if(m_idx == null)
	        return;
	    int numStrips   = m_tess[0];
	    int numPairs    = m_tess[1]+1;
	    int totalVerts  = numStrips * numPairs * 2;
	    if(totalVerts != m_idx.length) { // allocate new buffers
	        m_idx = new short[totalVerts];
	        m_crd = new float[totalVerts*3];
	        m_nrm = new float[totalVerts*3];
	        m_txc = new float[totalVerts*2];
	        this.getMeshData().setVertexBuffer(BufferUtils.createFloatBuffer(m_crd));
	        this.getMeshData().setNormalBuffer(BufferUtils.createFloatBuffer(m_nrm));
	        this.getMeshData().setTextureCoords(new FloatBufferData(BufferUtils.createFloatBuffer(m_txc),2),0);
	        this.getMeshData().setIndexBuffer(BufferUtils.createShortBuffer(m_idx));
	    }
	    calculateVertexData(numStrips, numPairs, totalVerts);
	    
        FloatBuffer crdBuf = getMeshData().getVertexBuffer();
        FloatBuffer nrmBuf = getMeshData().getNormalBuffer();
        FloatBufferData tc = getMeshData().getTextureCoords(0);
        FloatBuffer txcBuf = tc.getBuffer();
        ShortBuffer idxBuf = (ShortBuffer)getMeshData().getIndexBuffer();
        
        crdBuf.rewind();
        nrmBuf.rewind();
        txcBuf.rewind();
        idxBuf.rewind();
        
        crdBuf.put(m_crd);
        nrmBuf.put(m_nrm);
        txcBuf.put(m_txc);
        idxBuf.put(m_idx);
        
        updateModelBound();
	}

	/**
	 * 
	 * @param numStrips
	 * @param numPairs
	 * @param totalVerts
	 */
	@SuppressWarnings("cast")
    private void calculateVertexData(int numStrips, int numPairs, int totalVerts) {
        float astep = (float)( (Math.PI*2)/m_tess[1] );
        float sstep = 1.0f/numStrips;
        float rrange= m_radius[1] - m_radius[0];
        float rstep = rrange/numStrips;
        float nadd0;
        float nadd1;
        float xa,ya;
        float r0,r1;
        float tc;
        float up = 1;
        if(m_flipFace == true) {
            up = -up;
        }
        int i;
        for(int s = 0; s < numStrips; s++) {
            nadd0 = m_concave * ((s+0) - (numStrips*0.5f)) / numStrips;
            nadd1 = m_concave * ((s+1) - (numStrips*0.5f)) / numStrips;
            for(int a = 0; a < numPairs; a++) {
                xa = (float) Math.cos(a * astep);
                ya = (float) Math.sin(a * astep);
                i = (s*numPairs*6) + a*6;
                r0 = m_radius[0] + (s+0)*rstep;
                r1 = m_radius[0] + (s+1)*rstep;
                
                m_crd[i+0] = xa*r0; m_crd[i+1] = ya*r0; m_crd[i+2] = 0;
                m_crd[i+3] = xa*r1; m_crd[i+4] = ya*r1; m_crd[i+5] = 0;
                
                m_nrm[i+0] = nadd0*xa;    m_nrm[i+1] = nadd0*ya;    m_nrm[i+2] = up;
                m_nrm[i+3] = nadd1*xa;    m_nrm[i+4] = nadd1*ya;    m_nrm[i+5] = up;
                normalize(i+0, m_nrm);
                normalize(i+3, m_nrm);
                
                i = (s*numPairs*4) + a*4;
                tc = (a*m_texMul)/((float)m_tess[1]);
                m_txc[i+0] = (s+0)*sstep; m_txc[i+1] = tc;
                m_txc[i+2] = (s+1)*sstep; m_txc[i+3] = tc;
            }
        }
        
        for(i = 0; i < totalVerts; i++) {
            m_idx[i] = (short)i;
        }
	}
}
