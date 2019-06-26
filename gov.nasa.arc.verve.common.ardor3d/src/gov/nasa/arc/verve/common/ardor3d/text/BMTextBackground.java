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

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import com.ardor3d.image.Texture;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.type.ReadOnlyVector2;
import com.ardor3d.renderer.IndexMode;
import com.ardor3d.renderer.Renderer;
import com.ardor3d.renderer.state.BlendState;
import com.ardor3d.renderer.state.CullState;
import com.ardor3d.renderer.state.CullState.PolygonWind;
import com.ardor3d.renderer.state.OffsetState;
import com.ardor3d.renderer.state.TextureState;
import com.ardor3d.renderer.state.ZBufferState;
import com.ardor3d.scenegraph.FloatBufferData;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.hint.CullHint;
import com.ardor3d.scenegraph.hint.LightCombineMode;
import com.ardor3d.util.geom.BufferUtils;

/**
 * 
 * @author mallan
 */
public class BMTextBackground extends Mesh implements BMTextChangeListener {
    final BMText    _text;
    final ColorRGBA _bckClr = new ColorRGBA(ColorRGBA.WHITE);
    final ColorRGBA _tmpClr = new ColorRGBA(ColorRGBA.WHITE);

    float _texBorderWidth = 0.25f;
    float _borderSize = 0.5f;
    float _defaultAlpha = 1;
    
    final protected IntBuffer   _indexBuffer;
    final protected FloatBuffer _vertexBuffer;
    final protected FloatBuffer _texCrdBuffer;
    final protected FloatBufferData _texCoords;

    public BMTextBackground(String name, BMText text, Texture texture) {
        super(name);
        _text = text;

        _indexBuffer = BufferUtils.createIntBuffer(28);
        _vertexBuffer = BufferUtils.createVector3Buffer(16);
        _texCrdBuffer = BufferUtils.createVector2Buffer(16);
        _texCoords = new FloatBufferData(_texCrdBuffer, 2);

        _indexBuffer.rewind();
        _indexBuffer.put( 9).put( 5).put(10).put( 6);
        _indexBuffer.put( 4).put( 0).put( 5).put( 1).put( 6).put( 2);
        _indexBuffer.put( 2).put( 3).put( 6).put( 7).put(10).put(11);
        _indexBuffer.put(11).put(15).put(10).put(14).put( 9).put(13);
        _indexBuffer.put(13).put(12).put( 9).put( 8).put( 5).put(4 );

        getMeshData().setIndexBuffer(_indexBuffer);
        getMeshData().setIndexLengths(new int[] { 4, 6, 6, 6, 6} );
        getMeshData().setIndexMode(IndexMode.TriangleStrip);
        getMeshData().setVertexBuffer(_vertexBuffer);
        getMeshData().setTextureCoords(_texCoords, 0);
        
        BlendState bs = new BlendState();
        bs.setBlendEnabled(true);
        setRenderState(bs);

        OffsetState os = new OffsetState();
        os.setTypeEnabled(OffsetState.OffsetType.Fill, true);
        os.setFactor(1);
        os.setUnits(2);
        setRenderState(os);

        CullState cs = new CullState();
        cs.setCullFace(CullState.Face.Back);
        cs.setPolygonWind(PolygonWind.ClockWise);
        setRenderState(cs);

        TextureState ts = new TextureState();
        ts.setTexture(texture);
        setRenderState(ts);
        
        ZBufferState zs = new ZBufferState();
        zs.setWritable(true);
        //zs.setWritable(false);
        setRenderState(zs);
        
        setDefaultColor(_bckClr);
        setModelBound(null);
        
        getSceneHints().setLightCombineMode(LightCombineMode.Off);
        getSceneHints().setCullHint(CullHint.Never);

        //-- add self as change listener
        _text.addChangeListener(this);
    }
    
    public void setBackgroundColor(ColorRGBA color) {
        setDefaultColor(_bckClr.set(color));
    }
    
    /**
     * Set the size of the border around the text. Note that
     * the size will be multiplied by the font size to get the 
     * actual size so the border will be the same size relative
     * to the text. 
     * i.e. if size==1, the border will be 12 units for a 12 point font,
     * or 20 for a 20 point font. 
     * @param size
     */
    public void setBorderSize(float size) {
        _borderSize = size;
        textSizeChanged(_text, _text.getSize());
    }
    public float getBorderSize() {
        return _borderSize;
    }
    
    /**
     * Set the amount of texture to reserve for the border. 
     * For a border with of 0.25, 0-0.25 and 0.75-1 of the 
     * texture will be used for the border, the middle 0.5 will
     * be stretched to fit the text. 
     * This comment doesn't make much sense, does it?
     */
    public void setTexBorderWidth(float texBorderWidth) {
        _texBorderWidth = texBorderWidth;
        textSizeChanged(_text, _text.getSize());
    }
    public float getTexBorderWidth() {
        return _texBorderWidth;
    }

    @Override
    public synchronized void draw(final Renderer r) {
        this.setWorldRotation(_text.getWorldRotation());
        this.setWorldTranslation(_text.getWorldTranslation());
        this.setWorldScale(_text.getWorldScale());
        super.draw(r);
    }

    @Override
    public void textSizeChanged(BMText text, ReadOnlyVector2 size) {
        ReadOnlyVector2 fixedOffset = text.getFixedOffset();
        BMText.Align align = text.getAlign();
        float x = size.getXf() * align.horizontal;
        float y = size.getYf() * align.vertical;
        x += fixedOffset.getX();
        y += fixedOffset.getY();
        float xs = x+size.getXf();
        float ys = y+size.getYf();
        float xb,yb;
        xb = yb = _borderSize*text.getFont().getSize();
        
        _vertexBuffer.rewind();
        _vertexBuffer.put(x -xb).put(0).put(y-yb);
        _vertexBuffer.put(x    ).put(0).put(y-yb);
        _vertexBuffer.put(xs   ).put(0).put(y-yb);
        _vertexBuffer.put(xs+xb).put(0).put(y-yb);

        _vertexBuffer.put(x -xb).put(0).put(y);
        _vertexBuffer.put(x    ).put(0).put(y);
        _vertexBuffer.put(xs   ).put(0).put(y);
        _vertexBuffer.put(xs+xb).put(0).put(y);

        _vertexBuffer.put(x -xb).put(0).put(ys);
        _vertexBuffer.put(x    ).put(0).put(ys);
        _vertexBuffer.put(xs   ).put(0).put(ys);
        _vertexBuffer.put(xs+xb).put(0).put(ys);

        _vertexBuffer.put(x -xb).put(0).put(ys+yb);
        _vertexBuffer.put(x    ).put(0).put(ys+yb);
        _vertexBuffer.put(xs   ).put(0).put(ys+yb);
        _vertexBuffer.put(xs+xb).put(0).put(ys+yb);
        
        float ub,vb;
        ub = vb = _texBorderWidth;
        _texCrdBuffer.rewind();
        _texCrdBuffer.put(0   ).put(0);
        _texCrdBuffer.put(ub  ).put(0);
        _texCrdBuffer.put(1-ub).put(0);
        _texCrdBuffer.put(1   ).put(0);

        _texCrdBuffer.put(0   ).put(vb);
        _texCrdBuffer.put(ub  ).put(vb);
        _texCrdBuffer.put(1-ub).put(vb);
        _texCrdBuffer.put(1   ).put(vb);

        _texCrdBuffer.put(0   ).put(1-vb);
        _texCrdBuffer.put(ub  ).put(1-vb);
        _texCrdBuffer.put(1-ub).put(1-vb);
        _texCrdBuffer.put(1   ).put(1-vb);

        _texCrdBuffer.put(0   ).put(1);
        _texCrdBuffer.put(ub  ).put(1);
        _texCrdBuffer.put(1-ub).put(1);
        _texCrdBuffer.put(1   ).put(1);
    }

    @Override
    public void textAlphaChanged(BMText text, float alpha) {
        _tmpClr.set(_bckClr.getRed(),
                _bckClr.getGreen(), 
                _bckClr.getBlue(), 
                _bckClr.getAlpha()*alpha);
        setDefaultColor(_tmpClr);
    }
}
