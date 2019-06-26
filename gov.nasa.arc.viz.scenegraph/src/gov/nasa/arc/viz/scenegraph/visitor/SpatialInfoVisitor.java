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
package gov.nasa.arc.viz.scenegraph.visitor;

import com.ardor3d.renderer.IndexMode;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.MeshData;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.scenegraph.hint.DataMode;
import com.ardor3d.scenegraph.visitor.Visitor;

/**
 * 
 */
@SuppressWarnings("unchecked")
public class SpatialInfoVisitor implements Visitor {
    //private static final Logger logger = Logger.getLogger(SpatialInfoVisitor.class);

    protected int   m_numMeshes = 0;
    protected int   m_numNodes  = 0;
    protected int   m_numOthers = 0;

    protected int   m_numIdentityXfms = 0;
    protected int[] m_numIndexModes = new int[IndexMode.values().length];
    protected int[] m_numPrimitives = new int[IndexMode.values().length];
    protected int[] m_numDataModes  = new int[DataMode.values().length];

    /**
     * @param countClass count the objects which are instances of countClass
     */
    public SpatialInfoVisitor() {
    }

    public void visit(final Spatial spatial) {
        if(spatial.getTransform().isIdentity()) {
            m_numIdentityXfms++;
        }
        if(spatial instanceof Mesh) {
            m_numMeshes++;
            Mesh mesh = (Mesh)spatial;
            MeshData meshData = mesh.getMeshData();
            int[] indexLengths = meshData.getIndexLengths();
            if(indexLengths == null) {
                IndexMode mode = meshData.getIndexMode(0);
                m_numIndexModes[mode.ordinal()]++;
                m_numPrimitives[mode.ordinal()] += meshData.getPrimitiveCount(0);
            }
            else {
                int numSections = indexLengths.length;
                for(int s = 0; s < numSections; s++) {
                    IndexMode mode = meshData.getIndexMode(s);
                    m_numIndexModes[mode.ordinal()]++;
                    m_numPrimitives[mode.ordinal()] += meshData.getPrimitiveCount(s);
                }
            }
            DataMode dm = mesh.getSceneHints().getLocalDataMode();
            m_numDataModes[dm.ordinal()]++;
        }
        else if(spatial instanceof Node) {
            m_numNodes++;
            //Node node = (Node)spatial;
        }
        else {
            m_numOthers++;
        }
    }

    public String getInfo() {
        int total = m_numMeshes+m_numNodes+m_numOthers;
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Branch contains %d Meshes, %d Nodes, and %d other spatials\n", m_numMeshes, m_numNodes, m_numOthers));
        sb.append(String.format("  of %d transforms, %d are identity (%f%%)\n", total, m_numIdentityXfms, 100.0*m_numIdentityXfms/total)); 
        sb.append("  IndexModes:\n");
        for(IndexMode mode : IndexMode.values()) {
            final String modeString = mode.toString();
            final int ordinal = mode.ordinal();
            final int nim = m_numIndexModes[ordinal];
            final int np  = m_numPrimitives[ordinal];
            sb.append(String.format("    %20s : %d total, %d primitives\n", modeString, nim, np));
        }
        sb.append("  DataModes:\n");
        for(DataMode mode : DataMode.values()) {
            final String modeString = mode.toString();
            final int ordinal = mode.ordinal();
            final int ndm = m_numDataModes[ordinal];
            sb.append(String.format("    %20s : %d total\n", modeString, ndm));
        }
        return sb.toString();
    }
    
    public void reset() {
        m_numMeshes = 0;
        m_numNodes  = 0;
        m_numOthers = 0;

        m_numIdentityXfms = 0;
        for(int i = 0; i < m_numIndexModes.length; i++) {
            m_numIndexModes[0] = 0;
            m_numPrimitives[0] = 0;
        }
        for(int i = 0; i < m_numDataModes.length; i++) {
            m_numDataModes[0] = 0;
        }
    }
    
}
