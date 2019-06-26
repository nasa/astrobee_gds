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
package gov.nasa.arc.verve.common.ardor3d.text;

import com.ardor3d.math.Matrix3;
import com.ardor3d.renderer.queue.RenderBucketType;
import com.ardor3d.renderer.state.BlendState;
import com.ardor3d.renderer.state.CullState;
import com.ardor3d.renderer.state.ZBufferState;
import com.ardor3d.scenegraph.hint.CullHint;
import com.ardor3d.scenegraph.hint.LightCombineMode;
import com.ardor3d.scenegraph.hint.TextureCombineMode;

public class Text2D extends BMText {
	
    public Text2D(final String name, final String text, final BMFont font) {
        super(name, text, font);
        setAutoFade(AutoFade.Off);
        setAutoScale(AutoScale.Off);
        setAutoRotate(false);
        setFontScale(font.getSize());
        Matrix3 rot = new Matrix3( 
        		0, 0, 1, 
        		1, 0, 0, 
        		0, 1, 0);
        setRotation(rot);

        final ZBufferState zState = new ZBufferState();
        zState.setEnabled(false);
        zState.setWritable(false);
        setRenderState(zState);

        final CullState cState = new CullState();
        cState.setEnabled(false);
        setRenderState(cState);

        final BlendState blend = new BlendState();
        blend.setBlendEnabled(true);
        blend.setSourceFunction(BlendState.SourceFunction.SourceAlpha);
        blend.setDestinationFunction(BlendState.DestinationFunction.OneMinusSourceAlpha);
        blend.setTestEnabled(true);
        blend.setReference(0f);
        blend.setTestFunction(BlendState.TestFunction.GreaterThan);
        setRenderState(blend);

        getSceneHints().setRenderBucketType(RenderBucketType.Ortho);
        getSceneHints().setCullHint(CullHint.Never);
        getSceneHints().setLightCombineMode(LightCombineMode.Off);
        getSceneHints().setTextureCombineMode(TextureCombineMode.Replace);
        
        updateModelBound();
    }

}
