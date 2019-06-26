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
package gov.nasa.arc.verve.utils.rapid.scenegraph;

import gov.nasa.rapid.v2.framestore.tree.FrameTreeNode;

import java.nio.FloatBuffer;
import java.util.List;

import com.ardor3d.bounding.BoundingSphere;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Transform;
import com.ardor3d.math.type.ReadOnlyColorRGBA;
import com.ardor3d.math.type.ReadOnlyVector3;
import com.ardor3d.scenegraph.Line;
import com.ardor3d.scenegraph.hint.LightCombineMode;
import com.ardor3d.util.geom.BufferUtils;

/**
 * Draws lines between this node and its children
 * @author mallan
 *
 */
public class ChildLines extends Line {
    private int             m_nChildren = 0;
    private ColorRGBA       m_c0   = new ColorRGBA();
    private ColorRGBA       m_c1   = new ColorRGBA();
    public ChildLines(String name, FrameTreeNode node, ReadOnlyColorRGBA c) {
        super(name);
        m_c1.set(c);
        float s = 0.3f;
        m_c0.set(c.getRed()*s,
                c.getGreen()*s, 
                c.getBlue()*s,
                c.getAlpha());
        init(node);
        getSceneHints().setLightCombineMode(LightCombineMode.Off);
    }

    public void update(FrameTreeNode node) {
        List<FrameTreeNode> children = node.getChildren();
        if(m_nChildren != children.size()) {
            m_nChildren = children.size();
            if(m_nChildren > 0) {
                initChildren(children);
            }
            else {
                initEmpty();
            }
        }
        else {
            updateChildPositions(children);
        }
    }

    protected void init(FrameTreeNode node) {
        List<FrameTreeNode> children = node.getChildren();
        m_nChildren = node.getNumChildren();
        if(m_nChildren > 0) {
            initChildren(children);
        }
        else {
            initEmpty();
        }
        setModelBound(new BoundingSphere());
    }

    protected void initEmpty() {
        FloatBuffer vtx,clr;
        vtx = BufferUtils.createFloatBuffer(2*3);
        vtx.put(0).put(0).put(0);
        vtx.put(0).put(0).put(0);
        clr = BufferUtils.createFloatBuffer(2*4);
        clr.put(0).put(0).put(0).put(0);
        clr.put(0).put(0).put(0).put(0);
        getMeshData().setVertexBuffer(vtx);
        getMeshData().setColorBuffer(clr);
        //generateIndices();
    }

    protected void initChildren(List<FrameTreeNode> children) {
        int nChildren = children.size();
        FloatBuffer vtx,clr;
        vtx = BufferUtils.createFloatBuffer(2*nChildren*3);
        clr = BufferUtils.createFloatBuffer(2*nChildren*4);
        for(int i = 0; i < nChildren; i++) {
            clr.put(m_c0.getRed()).put(m_c0.getGreen()).put(m_c0.getBlue()).put(m_c0.getAlpha());
            clr.put(m_c1.getRed()).put(m_c1.getGreen()).put(m_c1.getBlue()).put(m_c1.getAlpha());
        }
        getMeshData().setVertexBuffer(vtx);
        getMeshData().setColorBuffer(clr);
        updateChildPositions(children);
        //generateIndices();
    }

    protected void updateChildPositions(List<FrameTreeNode> children) {
        if(children.size() > 0) {
            FloatBuffer vtx = getMeshData().getVertexBuffer();
            vtx.rewind();
            Transform xfm = new Transform();
            ReadOnlyVector3 pos;
            for( FrameTreeNode child : children ) { 
                child.getFrame().getTransform(xfm);
                pos = xfm.getTranslation();
                vtx.put(0).put(0).put(0);
                //vtx.put((float)m44.m03).put((float)m44.m13).put((float)m44.m23);
                vtx.put(pos.getXf()).put(pos.getYf()).put(pos.getZf());
            }
        }
    }

}
