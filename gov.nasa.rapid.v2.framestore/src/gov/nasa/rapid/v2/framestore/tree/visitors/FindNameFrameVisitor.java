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
package gov.nasa.rapid.v2.framestore.tree.visitors;

import gov.nasa.rapid.v2.framestore.tree.FrameTreeNode;

public class FindNameFrameVisitor extends AbstractFrameVisitor {
    protected String m_name;
    protected FrameTreeNode m_node = null;
    
    public FindNameFrameVisitor(String name) {
        m_name = name;
    }
    
    public void setName(String name) {
        m_name = name;
        m_node = null;
    }
    
    public String getName() {
        return m_name;
    }
    
    @Override
    public boolean visit(FrameTreeNode node) {
        if(m_name.equals(node.getFrame().getName())) {
            m_node = node;
            return true;
        }
        return false;
    }
    
    /** 
     * get the found node
     * @return null if node was not found
     */
    public FrameTreeNode getNode() {
        return m_node;
    }

}
