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
package gov.nasa.arc.verve.robot.parts;

import gov.nasa.arc.verve.robot.AbstractRobot;
import gov.nasa.arc.verve.robot.scenegraph.visitors.SetAlphaVisitor;

import com.ardor3d.math.ColorRGBA;
import com.ardor3d.renderer.state.BlendState;
import com.ardor3d.renderer.state.RenderState.StateType;
import com.ardor3d.scenegraph.Node;

/**
 * 
 *
 */
public class RobotPartTransparency extends AbstractRobotPart {
    //private static final Logger logger = Logger.getLogger(RobotPartTransparency.class);
    
    protected float      m_alpha = 0.4f;
    protected ColorRGBA  m_emiss = new ColorRGBA(0.2f, 0.2f, 0.2f, 1.0f);
    protected BlendState m_alphaBlend = new BlendState();
    protected BlendState m_savedBlend = null;
    
    protected final SetAlphaVisitor m_visitor = new SetAlphaVisitor();
    
    public RobotPartTransparency(String partId, AbstractRobot parent) {
        super(partId, parent);
        m_isVisible = false;
        
        m_alphaBlend.setSourceFunction(BlendState.SourceFunction.SourceAlpha);
        m_alphaBlend.setDestinationFunction(BlendState.DestinationFunction.OneMinusSourceAlpha);
        m_alphaBlend.setReference(0.1f);
        m_alphaBlend.setTestEnabled(true);
        m_alphaBlend.setBlendEnabled(true);
    }

    public void setAlpha(float alpha) {
        m_alpha = alpha;
    }
    public float getAlpha() {
        return m_alpha;
    }
    
    @Override
    public void attachToNodesIn(Node model) throws IllegalStateException {
    	m_node = getRobot().getRobotNode().getModelNode();
    	//logger.debug("m_node = "+m_node.getName());
        m_savedBlend = (BlendState)m_node.getLocalRenderState(StateType.Blend);
    }
    
    @Override
    public void handleFrameUpdate(long currentTime) {
        //empty
    }

    @Override
    public void setVisible(boolean visible) {
        if(m_node != null) {
            if(visible) {
                m_savedBlend = (BlendState)m_node.getLocalRenderState(StateType.Blend);
                setNewAlpha(m_isVisible==false);
                m_node.setRenderState(m_alphaBlend);
            }
            else {
                restoreAlpha();
                if(m_savedBlend == null) {
                    m_node.clearRenderState(StateType.Blend);
                }
                else {
                    m_node.setRenderState(m_savedBlend);
                }
            }
            m_isVisible = visible;
        }
    }

    protected void restoreAlpha() {
        m_visitor.restoreSavedMaterials();
    }

    protected void setNewAlpha(boolean save) {
        m_visitor.setSaveMaterials(save);
        m_visitor.setAlpha(m_alpha);
        m_visitor.setEmissiveColor(m_emiss);
        m_node.acceptVisitor(m_visitor, false);
    }

    @Override 
    public void connectTelemetry() {
        // unneeded
    }
    
    @Override 
    public void disconnectTelemetry() {
        // unneeded
    }
    
    @Override
    public void reset() {
        //
    }
    

}
