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

/**
 * Interface to operate on FrameTreeNodes during a traversal
 * @see FrameTreeNode.traverseBreadthFirst(IFrameVisitor visitor)
 * @see FrameTreeNode.traversePreOrder(IFrameVisitor visitor)
 * @see FrameTreeNode.traversePostOrder(IFrameVisitor visitor)
 */
public interface IFrameVisitor {
    
    /** 
     * @return true <b>only</b> if stop condition has been met
     */
    boolean visit(FrameTreeNode node);
    
    /** in a depth first traversal, called before descent */
    void down(FrameTreeNode node);
    /** in a depth first traversal, called on ascent */
    void up(FrameTreeNode node);
}
