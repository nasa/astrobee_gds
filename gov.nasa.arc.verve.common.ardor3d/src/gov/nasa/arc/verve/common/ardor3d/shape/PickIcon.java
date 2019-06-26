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
package gov.nasa.arc.verve.common.ardor3d.shape;

import java.nio.FloatBuffer;

import com.ardor3d.bounding.BoundingSphere;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Matrix3;
import com.ardor3d.renderer.Camera;
import com.ardor3d.renderer.ContextManager;
import com.ardor3d.renderer.IndexMode;
import com.ardor3d.renderer.Renderer;
import com.ardor3d.renderer.queue.RenderBucketType;
import com.ardor3d.renderer.state.BlendState;
import com.ardor3d.renderer.state.ZBufferState;
import com.ardor3d.renderer.state.RenderState.StateType;
import com.ardor3d.scenegraph.Line;
import com.ardor3d.scenegraph.hint.LightCombineMode;
import com.ardor3d.util.geom.BufferUtils;

/**
 * 
 */
public class PickIcon extends Line {    
    public PickIcon() {
        super("pickPoint");
        init(0.15f);
        
        _meshData.setIndexMode(IndexMode.LineLoop);

        BlendState bs = new BlendState();
        bs.setBlendEnabled(true);
        bs.setSourceFunction(BlendState.SourceFunction.OneMinusDestinationColor);
        bs.setDestinationFunction(BlendState.DestinationFunction.Zero);
        
        setRenderState(bs);
        
        ZBufferState zs = new ZBufferState();
        zs.setWritable(false);
        zs.setFunction(ZBufferState.TestFunction.Always);
        setRenderState(zs);

        setAntialiased(true);
        setLineWidth(2);
        
        setDefaultColor(new ColorRGBA(0.8f, 0.8f, 0.8f, 0.8f));
        getSceneHints().setLightCombineMode(LightCombineMode.Off);
        
        getSceneHints().setRenderBucketType(RenderBucketType.PostBucket);
    }
    
    Matrix3 rot = new Matrix3();
    @Override
	public void draw(final Renderer r) {
    	final Camera cam = ContextManager.getCurrentContext().getCurrentCamera();
    	rot.setColumn(0, cam.getLeft());
    	rot.setColumn(1, cam.getUp());
    	rot.setColumn(2, cam.getDirection());
    	setRotation(rot);
    	super.draw(r);
    }
    
    final static int vertexCount = 4;
    private void init(float size) {
        FloatBuffer vtxBuffer = BufferUtils.createFloatBuffer(vertexCount*3);
        updateSize(size, vtxBuffer);
        getMeshData().setVertexBuffer(vtxBuffer);
        setModelBound(new BoundingSphere());
    }
    
    private static FloatBuffer updateSize(float size, FloatBuffer vtxBuffer) {
        vtxBuffer.rewind();
        final float s = size/2;
        for(int i = 0; i < vertexCount; i++) {
        	double a = 2*Math.PI * (i/(double)vertexCount);
        	float x = s * (float)Math.sin(a);
        	float y = s * (float)Math.cos(a);
        	vtxBuffer.put( x).put( y).put( 0);
        }
        
        return vtxBuffer;
    }
        
    public void setBlendEnabled(boolean state) {
        BlendState bs = (BlendState)getLocalRenderState(StateType.Blend);
        bs.setBlendEnabled(state);
    }
}
