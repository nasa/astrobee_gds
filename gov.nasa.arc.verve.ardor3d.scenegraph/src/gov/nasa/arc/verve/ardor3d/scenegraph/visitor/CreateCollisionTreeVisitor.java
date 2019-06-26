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
package gov.nasa.arc.verve.ardor3d.scenegraph.visitor;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ardor3d.bounding.CollisionTree;
import com.ardor3d.bounding.CollisionTreeManager;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.scenegraph.visitor.Visitor;

/**
 * Walk through a subgraph and create a collision 
 * tree for each Mesh encountered. If creation of the
 * tree takes longer than a threshold value, inform the
 * cache manager that the mesh's tree should be protected
 * ONLY DO THIS ON THE MAIN THREAD, OR ON UNATTACHED
 * SUBGRAPHS
 */
public class CreateCollisionTreeVisitor implements Visitor {
    static final Logger logger = Logger.getLogger(CreateCollisionTreeVisitor.class);
    private final static CollisionTree.Type DEFAULT_TYPE = CollisionTree.Type.AABB;
    private final static int DEFAULT_THRESHOLD = 500;

    protected final int m_protectThreshold;
    protected final CollisionTree.Type m_type;
    
    class Holder {
        public Mesh mesh = null;
        public CollisionTree tree = null;
        public boolean protect = false;
    }
    List<Holder> treeList = new ArrayList<Holder>();

    /**
     * @param protectThreshold (msecs) If it takes longer than this threshold, protect the CollisionTree in the cache
     * @param type bounding type of CollisionTree
     */
    public CreateCollisionTreeVisitor(int protectThreshold, final CollisionTree.Type type) {
        m_protectThreshold = protectThreshold;
        m_type = type;
    }
    /**
     * Use default CollisionTree.Type
     * @param protectThreshold (msecs) If it takes longer than this threshold, protect the CollisionTree in the cache
     */
    public CreateCollisionTreeVisitor(int protectThreshold) {
        this(protectThreshold, DEFAULT_TYPE);
    }
    /** 
     * Use default creation time threshold and default CollisionTree.Type
     */
    public CreateCollisionTreeVisitor() {
        this(DEFAULT_THRESHOLD, DEFAULT_TYPE);
    }
   
    @Override
    public void visit(final Spatial spatial) {
        preVisit(spatial);
        if(spatial instanceof Mesh) {
            Mesh mesh = (Mesh)spatial;
            long before = System.currentTimeMillis();
            CollisionTreeManager.getInstance().generateCollisionTree(m_type, mesh, false);
            long elapsed = System.currentTimeMillis() - before;
            if(elapsed > m_protectThreshold) {
                CollisionTreeManager.getInstance().setProtected(mesh);            
            }
        }
        postVisit(spatial);
    }

    /** override this to get progress information */
    public void preVisit(Spatial spatial) {
        //
    }
    /** override this to get progress information */
    public void postVisit(Spatial spatial) {
        //
    }
}
