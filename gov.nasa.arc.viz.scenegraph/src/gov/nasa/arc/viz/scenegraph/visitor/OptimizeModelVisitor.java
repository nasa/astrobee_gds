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

import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedList;

import com.ardor3d.extension.model.util.nvtristrip.NvTriangleStripper;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.scenegraph.visitor.Visitor;
import com.ardor3d.util.geom.GeometryTool;
import com.ardor3d.util.geom.MeshCombiner;
import com.ardor3d.util.geom.GeometryTool.MatchCondition;

public class OptimizeModelVisitor implements Visitor {
    NvTriangleStripper nvTriStripper = new NvTriangleStripper();

    EnumSet<MatchCondition> geometryConditions = EnumSet.of(MatchCondition.Color, 
                                                            MatchCondition.UVs, 
                                                            MatchCondition.Normal);

    HashSet<Node> meshParents = new HashSet<Node>();


    public OptimizeModelVisitor() {
    }

    @Override
    public void visit(Spatial spatial) {
        nvTriStripper.setListsOnly(false);
        nvTriStripper.setReorderVertices(true);
        nvTriStripper.setStitchStrips(true);
        nvTriStripper.setMinStripSize(2);
        nvTriStripper.setCacheSize(24);
        if(spatial instanceof Mesh) {
            Mesh mesh = (Mesh)spatial;
            meshParents.add(mesh.getParent());
            GeometryTool.minimizeVerts(mesh, geometryConditions);
            mesh.acceptVisitor(nvTriStripper, false);
        }
    }

    /** use at your own risk: mesh combiner doesn't work very well */
    public void combineMeshes() {
        for(Node node : meshParents) {
            LinkedList<Mesh> meshes = new LinkedList<Mesh>();
            Spatial[] children = node.getChildren().toArray(new Spatial[node.getChildren().size()]);
            for(Spatial child : children) {
                if(child instanceof Mesh) {
                    meshes.add((Mesh)child);
                }
                if(meshes.size() > 1) {
                    for(Mesh mesh : meshes) {
                        node.detachChild(mesh);
                    }
                    Mesh combined = MeshCombiner.combine(meshes);
                    node.attachChild(combined);
                }
            }
        }
    }

}
