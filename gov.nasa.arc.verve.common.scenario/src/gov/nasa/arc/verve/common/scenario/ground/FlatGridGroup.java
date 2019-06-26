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
package gov.nasa.arc.verve.common.scenario.ground;

import gov.nasa.arc.verve.common.scenario.ScenarioActivator;
import gov.nasa.arc.verve.common.scenario.ScenarioPreferences;
import gov.nasa.arc.verve.common.ardor3d.shape.grid.FlatGridQuad;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

import com.ardor3d.image.Texture2D;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.renderer.state.BlendState;
import com.ardor3d.renderer.state.BlendState.TestFunction;
import com.ardor3d.renderer.state.RenderState.StateType;
import com.ardor3d.renderer.state.ZBufferState;
import com.ardor3d.scenegraph.Node;


/**
 * @author mallan
 *
 */
public class FlatGridGroup extends Node 
{
    public static FlatGridGroup INSTANCE = new FlatGridGroup();
    protected FlatGridQuad[] m_quad = null;
    protected boolean        m_depthWrite;

    /** access FlatGridGroup through singleton INSTANCE */
    protected FlatGridGroup() {
        this(FlatGridGroup.class.getSimpleName());
    }

    /** access FlatGridGroup through singleton INSTANCE */
    protected FlatGridGroup(String name) 
    {
        super(name);
        BlendState bs = new BlendState();
        bs.setTestEnabled(true);
        bs.setTestFunction(TestFunction.GreaterThan);
        bs.setReference(0.07f);
        bs.setBlendEnabled(true);
        this.setRenderState(bs);

        ZBufferState zs = new ZBufferState();
        this.setRenderState(zs);
        setDepthWriteEnable(ScenarioPreferences.getFlatGridDepthWriteEnable());


        final int numGrids = ScenarioPreferences.getNumFlatGrids();
        m_quad = new FlatGridQuad[numGrids];

        //float[] off = new float[3];
        for(int i = 0; i < numGrids; i++) {
            if(ScenarioPreferences.getFlatGridEnabled(i) == true) {
                Texture2D gridTex = ScenarioPreferences.getFlatGridGridTexture(i);
                Texture2D baseTex = ScenarioPreferences.getFlatGridBaseTexture(i);
                float sz 		= ScenarioPreferences.getFlatGridSize(i);
                double[] utmOff	= ScenarioPreferences.getFlatGridOffset(i);
                ColorRGBA color = ScenarioPreferences.getFlatGridColor(i);
                m_quad[i] = new FlatGridQuad("flatGrid"+i, sz, color, gridTex, baseTex);
                this.attachChild(m_quad[i]);
                m_quad[i].setTranslation(utmOff[0], utmOff[1], utmOff[2]);
            }
        }

        initializePreferenceListeners();
    }

    public void setDepthWriteEnable(boolean status) {
        m_depthWrite = status;
        ZBufferState zs = (ZBufferState)this.getLocalRenderState(StateType.ZBuffer);
        zs.setWritable(status);
    }

    public FlatGridQuad getQuad(int index) {
        FlatGridQuad retVal = null;
        if(index >= 0 && index < m_quad.length) {
            retVal = m_quad[index];
        }
        return retVal;
    }

    public void setGridEnabled(int index, boolean state) {
        ScenarioPreferences.setFlatGridEnabled(index, state);
        if(state == true) {
            if(m_quad[index] == null) {
                Texture2D gridTex = ScenarioPreferences.getFlatGridGridTexture(index);
                Texture2D baseTex = ScenarioPreferences.getFlatGridBaseTexture(index);
                float sz        = ScenarioPreferences.getFlatGridSize(index);
                double[] utmOff = ScenarioPreferences.getFlatGridOffset(index);
                ColorRGBA color = ScenarioPreferences.getFlatGridColor(index);
                m_quad[index] = new FlatGridQuad("flatGrid"+index, sz, color, gridTex, baseTex);
                m_quad[index].setTranslation(utmOff[0], utmOff[1], utmOff[2]);
            }
            attachChild(m_quad[index]);
        }
        else {
            detachChild(m_quad[index]);
        }
    }

    public boolean isGridEnabled(int index) {
        return ScenarioPreferences.getFlatGridEnabled(index);
    }

    public void updateFromPreferences() {
        setDepthWriteEnable(ScenarioPreferences.getFlatGridDepthWriteEnable());
        final int numGrids = ScenarioPreferences.getNumFlatGrids();
        for(int i = 0; i < numGrids; i++) {
            if(ScenarioPreferences.getFlatGridEnabled(i) == true) {
                Texture2D gridTex = ScenarioPreferences.getFlatGridGridTexture(i);
                Texture2D baseTex = ScenarioPreferences.getFlatGridBaseTexture(i);
                float sz        = ScenarioPreferences.getFlatGridSize(i);
                double[] utmOff = ScenarioPreferences.getFlatGridOffset(i);
                ColorRGBA color = ScenarioPreferences.getFlatGridColor(i);
                if(m_quad[i] == null) {
                    m_quad[i] = new FlatGridQuad("flatGrid"+i, sz, color, gridTex, baseTex);
                    attachChild(m_quad[i]);
                    m_quad[i].setTranslation(utmOff[0], utmOff[1], utmOff[2]);
                }
                else {
                    attachChild(m_quad[i]);
                    m_quad[i].setTranslation(utmOff[0], utmOff[1], utmOff[2]);
                    m_quad[i].setDefaultColor(color);
                    m_quad[i].setGridTexture(gridTex);
                    m_quad[i].setBaseTexture(baseTex);
                    m_quad[i].resize(sz, sz);
                }
            }
            else {
                if(m_quad[i] != null) {
                    detachChild(m_quad[i]);
                }
            }
        }
    }

    protected void initializePreferenceListeners() {
        final FlatGridGroup gridGroup = this;

        IPropertyChangeListener flatGridListener; 
        flatGridListener = new IPropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent event) {
                gridGroup.updateFromPreferences();
            }
        };
        ScenarioActivator.getDefault().getPreferenceStore().addPropertyChangeListener(flatGridListener);
    }
}


