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
package gov.nasa.arc.verve.robot.rapid.parts.concepts;

import gov.nasa.arc.verve.ardor3d.scenegraph.shape.TexQuad;
import gov.nasa.arc.verve.robot.rapid.RapidRobot;
import gov.nasa.rapid.v2.e4.message.MessageType;
import rapid.ParameterUnion;
import rapid.ext.RTransMetaSequence;
import rapid.ext.TRAJ2D_META_GOOD;
import rapid.ext.TRAJ2D_META_OBSTACLE;
import rapid.ext.TRAJ2D_META_STOP_OBSTACLE;
import rapid.ext.TRAJ2D_META_STOP_PATH_DEVIATION;
import rapid.ext.TRAJ2D_META_STOP_UNKOWN;
import rapid.ext.TRAJ2D_META_UNKOWN;

import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Transform;
import com.ardor3d.math.type.ReadOnlyColorRGBA;
import com.ardor3d.renderer.state.RenderState.StateType;
import com.ardor3d.renderer.state.TextureState;

/**
 * 
 * @author mallan
 */
public class RapidRobotPartTrajectory2DAllStop extends RapidRobotPartTrajectory2D {
    //private static final Logger logger = Logger.getLogger(RapidRobotPartAllStopTrajectory2D.class);
    // XXX these values are from roversw/knAlgo/src/knLocalMapEval/LocalMapEval.h
    // XXX they should be defined in RAPID ext IDL in the future
    //private final int LMAP_GOOD     = 0;
    //private final int LMAP_OBSTACLE = 1;
    //private final int LMAP_UNKNOWN  = 2;
    final static ReadOnlyColorRGBA GOOD     = new ColorRGBA(0.20f, 0.60f, 0.60f, 1f);
    final static ReadOnlyColorRGBA UNKNOWN  = new ColorRGBA(0.75f, 0.38f, 0.56f, 1f);
    final static ReadOnlyColorRGBA OBSTACLE = new ColorRGBA(0.75f, 0.50f, 0.38f, 1f);
    final static ReadOnlyColorRGBA STOP_UNK = new ColorRGBA(1.00f, 0.00f, 0.50f, 1f);
    final static ReadOnlyColorRGBA STOP_OBS = new ColorRGBA(1.00f, 0.30f, 0.00f, 1f);
    final static ReadOnlyColorRGBA PATH_DEV = new ColorRGBA(1.00f, 1.00f, 0.00f, 1f);
    
    protected int m_worstMeta = 0;
    
    enum Modification {
        Good         (GOOD,      1.0f),
        Unknown      (UNKNOWN,   1.5f),
        Obstacle     (OBSTACLE,  1.5f),
        StopUnknown  (STOP_UNK,  2.5f),
        StopObstacle (STOP_OBS,  2.5f),
        PathDeviation(PATH_DEV,  2.5f)
        ;
        
        public final ReadOnlyColorRGBA normalClr;
        public final float normalSize;
        
        private Modification(ReadOnlyColorRGBA clra, float normalSize) {
            this.normalClr  = clra;
            this.normalSize = normalSize;
        }
    }

    protected final Modification[] m_mods;

    /**
     * 
     * @param partName
     * @param parent
     * @param participantId
     * @param sampleType
     */
    public RapidRobotPartTrajectory2DAllStop(String partName, RapidRobot parent, String participantId, float zOffset, MessageType sampleType, boolean useRobotZByDefault) {
        super(partName, parent, participantId, zOffset, sampleType, useRobotZByDefault);
        m_mods = new Modification[6];
        m_mods[TRAJ2D_META_GOOD.VALUE] = Modification.Good;
        m_mods[TRAJ2D_META_UNKOWN.VALUE] = Modification.Unknown;
        m_mods[TRAJ2D_META_OBSTACLE.VALUE] = Modification.Obstacle;
        m_mods[TRAJ2D_META_STOP_UNKOWN.VALUE] = Modification.StopUnknown;
        m_mods[TRAJ2D_META_STOP_OBSTACLE.VALUE] = Modification.StopObstacle;
        m_mods[TRAJ2D_META_STOP_PATH_DEVIATION.VALUE] = Modification.PathDeviation;
    }
    
    @Override
    protected void modifyReset() {
        m_worstMeta = 0;
    }

    @Override
    protected void modifyQuad(Transform xfm, TexQuad quad, RTransMetaSequence meta) {
        if(m_config != null) {
            Modification mod = m_mods[m_worstMeta];
            quad.setDefaultColor(mod.normalClr);
            final float size = mod.normalSize*m_quadSize;
            if(quad.getXSize() != size) {
                quad.setSize(size);
            }
            TextureState ts = (TextureState)quad.getLocalRenderState(StateType.Texture);
            if(mod.ordinal() < Modification.StopUnknown.ordinal()) {
                if(ts != m_tsNorm) 
                    quad.setRenderState(m_tsNorm);
            }
            else {
                if(ts != m_tsFlash) 
                    quad.setRenderState(m_tsFlash);
            }
            m_worstMeta = 0;
        }
        quad.setTransform(xfm);
    }
    
    @Override
    protected ReadOnlyColorRGBA modifyLineVertex(Transform xfm, RTransMetaSequence meta) {
        // the first meta value should be the LocalMapEval Classification enum value
        ParameterUnion param = (ParameterUnion)meta.userData.get(0);
        int state = param.i;
        if(state > m_worstMeta) {
            m_worstMeta = state;
        }
        return m_mods[state].normalClr;
    }
}
