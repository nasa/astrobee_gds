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
package gov.nasa.arc.verve.common.ardor3d.util;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ardor3d.math.Transform;
import com.ardor3d.scenegraph.Spatial;

public class SpatialPath {
    private static final Logger logger = Logger.getLogger(SpatialPath.class);
    
    List<Spatial> m_path = new LinkedList<Spatial>();
    Transform  tmpXfm = new Transform();

    public SpatialPath() {
        // nada
    }

    /** 
     * 
     * @param leaf end point of path
     * @param stopAtParent parent to stop at (may be null)
     * @param includeLeaf include leaf transform in final transform
     */
    public SpatialPath(Spatial leaf, Spatial stopAtParent, boolean includeLeaf) {
        findPath(leaf, stopAtParent, includeLeaf);
    }

    public void prepend(Spatial spatial) {
        m_path.add(0, spatial);
    }
    /** 
     * 
     * @param leaf end point of path
     * @param stopAtParent parent to stop at (may be null)
     * @param includeLeaf include leaf transform in final transform
     */
    public int findPath(Spatial leaf, Spatial stopAtParent, boolean includeLeaf) {
        m_path.clear();
        Spatial spatial = leaf;
        if(!includeLeaf)
            spatial = leaf.getParent();
        while(spatial != null) {
            m_path.add(0, spatial);
            if(spatial.equals(stopAtParent)) {
                return m_path.size();
            }
            spatial = spatial.getParent();
        }
        if(stopAtParent == null) {
            return m_path.size();
        }
        logger.warn(stopAtParent.getName()+" is not ancestor of "+leaf.getName());
        m_path.clear();
        return 0;
    }

    /**
     * get transform from parent to leaf
     * @param store
     * @return
     */
    public Transform getTransform(Transform store) {
        if(store == null) {
            store = new Transform();
        }
        store.setIdentity();
        tmpXfm.setIdentity();

        for(Spatial spatial : m_path) {
            spatial.getTransform().multiply(store, tmpXfm);
            store.set(tmpXfm);
        }
        return store;
    }
}
