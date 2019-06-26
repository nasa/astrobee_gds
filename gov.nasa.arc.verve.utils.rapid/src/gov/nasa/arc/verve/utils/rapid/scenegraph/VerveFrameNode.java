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

import gov.nasa.arc.verve.ardor3d.scenegraph.shape.AxisLines;
import gov.nasa.arc.verve.common.ardor3d.text.BMFont;
import gov.nasa.arc.verve.common.ardor3d.text.BMFontManager;
import gov.nasa.arc.verve.common.ardor3d.text.BMText;
import gov.nasa.arc.verve.common.ardor3d.text.BMText.AutoFade;
import gov.nasa.rapid.v2.framestore.tree.FrameTreeNode;

import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Matrix4;
import com.ardor3d.math.Transform;
import com.ardor3d.math.type.ReadOnlyColorRGBA;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.scenegraph.hint.CullHint;

public class VerveFrameNode extends Node {
    private static Logger logger = Logger.getLogger(VerveFrameNode.class);

    protected final ChildLines m_childLines;
    protected final AxisLines  m_frameLines;
    protected       BMText     m_text;

    protected final ColorRGBA  m_color      = new ColorRGBA();

    //protected final Matrix4d   m_m44V       = new Matrix4d();
    protected final Matrix4    m_m44A       = new Matrix4();
    protected final Transform  m_xfm        = new Transform();
    protected final boolean    m_isRoot;

    protected boolean m_showRelationships = true;
    protected boolean m_showFrames  = true;
    protected boolean m_showText    = false;
    protected boolean m_showTextAtX = false;

    private static final Random random = new Random();

    //private float m_size = 0.2f;
    private float m_size = 0.5f;

    private final FrameTreeNode m_ftNode;

    /**
     * 
     * @param node
     * @param color
     */
    public VerveFrameNode(FrameTreeNode node, ReadOnlyColorRGBA color) {
        this(node, color, false);
    }

    public VerveFrameNode(FrameTreeNode node, ReadOnlyColorRGBA color, boolean isChild) {
        super(node.getFrame().getName());
        m_isRoot = !isChild;
        m_ftNode = node;
        m_color.set( (color == null) ? ColorRGBA.WHITE : color);
        m_frameLines = new AxisLines("frame", m_size, m_color);
        m_frameLines.setLineWidth(1);
        m_childLines = new ChildLines("lines", node, m_color);
        m_childLines.setLineWidth(2);
        attachChild(m_frameLines);
        attachChild(m_childLines);

        updateFrameTransform();
        
        setShowText(m_showText);

        for(FrameTreeNode child : node.getChildren()) {
            attachChild(new VerveFrameNode(child, color, true));
        }	
    }

    public boolean isShowRelationships() {
        return m_showRelationships;
    }

    public void setShowRelationships(boolean state) {
        List<Spatial> children = getChildren();
        for(Spatial spat : children) {
            if(spat instanceof VerveFrameNode) {
                VerveFrameNode vfn = (VerveFrameNode)spat;
                vfn.setShowRelationships(state);
            }
        }
        m_showRelationships = state;
        if(state) {
            m_childLines.getSceneHints().setCullHint(CullHint.Inherit);
        }
        else {
            m_childLines.getSceneHints().setCullHint(CullHint.Always);
        }
    }

    public boolean isShowFrames() {
        return m_showFrames;
    }

    public void setShowFrames(boolean state) {
        List<Spatial> children = getChildren();
        for(Spatial spat : children) {
            if(spat instanceof VerveFrameNode) {
                VerveFrameNode vfn = (VerveFrameNode)spat;
                vfn.setShowFrames(state);
            }
        }
        m_showFrames = state;
        if(state) {
            m_frameLines.getSceneHints().setCullHint(CullHint.Inherit);
        }
        else {
            m_frameLines.getSceneHints().setCullHint(CullHint.Always);
        }
    }

    public boolean isShowText() {
        return m_showText;
    }

    public void setShowText(boolean state) {
        List<Spatial> children = getChildren();
        for(Spatial spat : children) {
            if(spat instanceof VerveFrameNode) {
                VerveFrameNode vfn = (VerveFrameNode)spat;
                vfn.setShowText(state);
            }
        }
        m_showText = state;
        if(state) {
            if(m_text == null) {
                BMFont font = BMFontManager.sansExtraSmall();
                m_text = new BMText("text", 
                                    getName(),
                                    font,
                                    BMText.Align.Center,
                                    BMText.Justify.Center);
                m_text.setAutoFade(AutoFade.Off);
                m_text.setAutoRotate(true);
                m_text.setFixedOffset(0, random.nextDouble());
                attachChild(m_text);
                if(m_showTextAtX) {
                    m_text.setTranslation(m_size, 0, 0);
                }
            }
        }
        else {
            if(m_text != null) {
                detachChild(m_text);
                m_text = null;
            }
        }
    }

    public boolean isShowTextAtX() {
        return m_showTextAtX;
    }

    public void setShowTextAtX(boolean state) {
        List<Spatial> children = getChildren();
        for(Spatial spat : children) {
            if(spat instanceof VerveFrameNode) {
                VerveFrameNode vfn = (VerveFrameNode)spat;
                vfn.setShowTextAtX(state);
            }
        }
        m_showTextAtX = state;
        if(m_text != null) {
            if(state) {
                m_text.setTranslation(m_size, 0, 0);
            }
            else {
                m_text.setTranslation(0, 0, 0);
            }
        }
    }

    public void updateFrameTransform() {
        if(!m_isRoot) {
            //m_ftNode.getFrame().getTransform(m_m44V);
            m_ftNode.getFrame().getTransform(m_xfm);
            try {
                //m_xfm.fromHomogeneousMatrix(RapidVerve.toArdor(m_m44V, m_m44A));
                setTransform(m_xfm);
            }
            catch(Throwable t) {
                logger.debug("updateFrameTransform error", t);
                logger.debug("Node = "+m_ftNode.getFrame().getName());
                //logger.debug("Ardor m44 = "+m_m44A);
                //logger.debug("Vecmath m44 = "+m_m44V);
            }
        }
    }

    /** 
     * update this frame and all child frames
     */
    public void updateFrame() {
        updateFrameTransform();
        m_childLines.update(m_ftNode);

        for(Spatial child : getChildren()) {
            if(child instanceof VerveFrameNode) {
                ((VerveFrameNode)child).updateFrame();
            }
        }
    }
}
