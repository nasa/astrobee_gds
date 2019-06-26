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

import gov.nasa.arc.verve.ardor3d.scenegraph.shape.TexQuad;

import com.ardor3d.image.Texture.MagnificationFilter;
import com.ardor3d.image.Texture.MinificationFilter;
import com.ardor3d.image.Texture.WrapMode;
import com.ardor3d.image.Texture2D;
import com.ardor3d.renderer.queue.RenderBucketType;
import com.ardor3d.renderer.state.BlendState;
import com.ardor3d.renderer.state.BlendState.DestinationFunction;
import com.ardor3d.renderer.state.BlendState.SourceFunction;
import com.ardor3d.renderer.state.TextureState;
import com.ardor3d.renderer.state.ZBufferState;
import com.ardor3d.scenegraph.hint.CullHint;
import com.ardor3d.scenegraph.hint.LightCombineMode;
import com.ardor3d.util.TextureManager;
import com.ardor3d.util.resource.URLResourceSource;


/**
 * 
 * @author mallan
 *
 */
public class CrosshairIcon extends TexQuad {
    //private static final Logger logger = Logger.getLogger(CrosshairIcon.class);

    public CrosshairIcon(String name, float size, boolean isFlipped) {
        super(name, size, isFlipped);
        getSceneHints().setRenderBucketType(RenderBucketType.Ortho);
        getSceneHints().setLightCombineMode(LightCombineMode.Off);
        getSceneHints().setCullHint(CullHint.Never);

        ZBufferState zs = new ZBufferState();
        zs.setFunction(ZBufferState.TestFunction.Always);
        zs.setWritable(false);
        setRenderState(zs);

        getSceneHints().setAllPickingHints(false);
        setModelBound(null);

        BlendState bs = new BlendState();
        bs.setBlendEnabled(true);
        bs.setSourceFunction(SourceFunction.SourceAlpha);
        bs.setDestinationFunction(DestinationFunction.OneMinusSourceAlpha);
        setRenderState(bs);

        URLResourceSource rs = new URLResourceSource(this.getClass().getResource("verve-crosshair.png"));
        Texture2D tex = (Texture2D)TextureManager.load(rs, 
                                                       MinificationFilter.Trilinear, 
                                                       false);
        tex.setMagnificationFilter(MagnificationFilter.Bilinear);
        tex.setWrap(WrapMode.EdgeClamp);
        TextureState ts = new TextureState();
        ts.setTexture(tex);
        setRenderState(ts);
    }

}
