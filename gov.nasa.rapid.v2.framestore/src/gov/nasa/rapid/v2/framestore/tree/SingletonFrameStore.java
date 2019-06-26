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
package gov.nasa.rapid.v2.framestore.tree;

import java.util.HashMap;
import java.util.Map;

/**
 * Singleton FrameStore.
 * 
 * The SingletonFrameStore simply allows to share a global framestore across plugins
 * without having to pass a reference around.
 * 
 * The SingletonFrameStore creation is thread safe and guarantees a single instance
 * of the framestore.
 * 
 * @author Lorenzo Flueckiger
 * 
 */
public class SingletonFrameStore extends FrameStore {

    /**
     * Name of the default root frame that is inserted in the framestore at creation
     */
    public static final String ROOT_FRAME_NAME = "ROOT";

    protected static SingletonFrameStore s_framestore = null;
    
    protected static FrameTreeNode s_rootframe;
    
    protected Map<Object, Boolean> m_modified = new HashMap<Object, Boolean>(1);

    /**
     * Get an instance of the SingletonFrameStore
     * @return  the unique FrameStore (existing one or freshly created)
     */
    synchronized public static SingletonFrameStore getInstance() {
        if ( s_framestore == null ) {
            s_framestore = new SingletonFrameStore();
            s_rootframe = new FrameTreeNode(ROOT_FRAME_NAME);
            try {
                s_framestore.add(s_rootframe, null);
            }
            catch ( Exception e ) {
                System.err.println("StaticFrameStore: failed to register the root frame!");
            }
        }
        return s_framestore;
    }

    /**
     * Get the root frame of this FrameStore
     * @return  root frame
     */
    public FrameTreeNode getRootFrame() {
        return s_rootframe;
    }

    public void markDirty() {
        for (Object who : m_modified.keySet()) {
            m_modified.put(who, true);
        }       
    }
    
    public boolean isDirty(Object who) {
        Boolean dirty = m_modified.get(who);
        if ( dirty != null ) {
            return dirty;
        }
        m_modified.put(who, true);
        return true;
    }
    
    public void clearDirty(Object who) {
        m_modified.put(who, false);
    }

    protected SingletonFrameStore() {
        // Disable external FrameStore creation
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

}
