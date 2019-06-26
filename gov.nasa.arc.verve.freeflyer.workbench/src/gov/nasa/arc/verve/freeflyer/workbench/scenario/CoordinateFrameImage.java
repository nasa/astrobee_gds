/******************************************************************************
 * Copyright © 2019, United States Government, as represented by the 
 * Administrator of the National Aeronautics and Space Administration. All 
 * rights reserved.
 * 
 * The Astrobee Control Station platform is licensed under the Apache License, 
 * Version 2.0 (the "License"); you may not use this file except in compliance 
 * with the License. You may obtain a copy of the License at
 * 
 * 	http://www.apache.org/licenses/LICENSE-2.0. 
 *   
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT 
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the 
 * License for the specific language governing permissions and limitations 
 * under the License.
 *****************************************************************************/
package gov.nasa.arc.verve.freeflyer.workbench.scenario;

import gov.nasa.arc.verve.ardor3d.scenegraph.shape.TexQuad;
import gov.nasa.arc.verve.common.DataBundleHelper;

import java.io.IOException;
import java.net.URL;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com.ardor3d.image.Texture2D;
import com.ardor3d.math.Matrix3;
import com.ardor3d.math.Vector3;
import com.ardor3d.renderer.Renderer;
import com.ardor3d.renderer.queue.RenderBucketType;
import com.ardor3d.renderer.state.BlendState;
import com.ardor3d.renderer.state.TextureState;
import com.ardor3d.renderer.state.ZBufferState;
import com.ardor3d.renderer.state.BlendState.DestinationFunction;
import com.ardor3d.renderer.state.BlendState.SourceFunction;
import com.ardor3d.scenegraph.hint.CullHint;
import com.ardor3d.scenegraph.hint.LightCombineMode;
import com.ardor3d.util.Ardor3dException;

public class CoordinateFrameImage extends TexQuad {
    private static Logger logger = Logger.getLogger(CoordinateFrameImage.class);

    Matrix3 m_rot = new Matrix3();
    Vector3 m_dir = new Vector3();
    double  m_offset;
    Vector3 m_axisOrigin = new Vector3();
    float m_size = 1;
    
	public CoordinateFrameImage(String name, String filePath, float radius) {
		super(name, radius*2, false);
        m_offset = radius + 2;
        getSceneHints().setRenderBucketType(RenderBucketType.Ortho);
        getSceneHints().setCullHint(CullHint.Never);
        getSceneHints().setLightCombineMode(LightCombineMode.Off);

        ZBufferState zs = new ZBufferState();
        zs.setFunction(ZBufferState.TestFunction.Always);
        zs.setWritable(false);
        setRenderState(zs);

        BlendState bs = new BlendState();
        bs.setBlendEnabled(true);
        bs.setSourceFunction(SourceFunction.SourceAlpha);
        bs.setDestinationFunction(DestinationFunction.One); //MinusSourceAlpha);
        setRenderState(bs);

        try {
        	Bundle bdl = FrameworkUtil.getBundle(this.getClass());
        	URL url = FileLocator.find(bdl, new Path(filePath), null);
        	//TODO make better texture
            Texture2D texture = DataBundleHelper.loadTexture(url);
            
            TextureState ts = new TextureState();
            ts.setTexture(texture);
            setRenderState(ts);
        } 
        catch (IOException e) {
            logger.warn("Error loading compass texture", e);
        }

        getSceneHints().setAllPickingHints(false);
	}
	
	public void setAxisOrigin(Vector3 ao) {
		m_axisOrigin = ao; // this is in world coordinates
	}
    @Override
    public void draw(final Renderer r) {
        try {
        	_worldTransform.setTranslation(m_axisOrigin.getX(),m_axisOrigin.getY(),m_axisOrigin.getZ());

        }
        catch(Ardor3dException e) {
            _worldTransform.setIdentity();
        }
        super.draw(r);
    }
}
