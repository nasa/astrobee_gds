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

import gov.nasa.rapid.v2.framestore.tree.visitors.IFrameVisitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CopyOnWriteArrayList;

import com.ardor3d.math.type.ReadOnlyTransform;

/**
 * A frame-tree is expected to adhere to the following requirements:
 *  * Frame names of children of a node are unique.
 *  * Frame names do not contain the character '/'.
 *  * Frames are not named ".", "..", nor "...".
 *  * The transform has an invertible matrix.
 *
 * These requirements are not checked explicitly in the FrameTreeNode interface.
 * Only the FrameStore interface explicitly checks these requirements and
 * flags errors if frames are added to the frame-store, that do not meet
 * these assumptions.
 */
public class FrameTreeNode {
    protected Frame         m_data;
    protected FrameTreeNode m_parent;
    protected final ArrayList<FrameTreeNode> m_children = new ArrayList<FrameTreeNode>();

    /**
     * Create a new FrameTreeNode and assigned it a parent and a copy of a frame.
     * @param parent Parent frame. Is null , then a root node is created
     * @param data
     */
    private FrameTreeNode(FrameTreeNode parent, Frame data) {
        m_parent = parent;
        m_data   = new Frame(data);
    }

    /**
     * Create a new FrameTreeNode with null parent and assigned frame.
     * @param frame
     */
    public FrameTreeNode(Frame frame) {
        m_parent = null;
        m_data   = frame;
    }

    /**
     * Create a new FrameTreeNode with null parent and a new Frame with name frameName
     * @param frameName name for new Frame
     */
    public FrameTreeNode(String frameName) {
        m_parent = null;
        m_data   = new Frame(frameName);
    }

    /** 
     * Create a new node with the given name and assigned it a parent
     * @param parent
     * @param frameName
     */
    protected FrameTreeNode(FrameTreeNode parent, String frameName)
    {
        m_data = new Frame(frameName);
        if ( parent != null ) {
            parent.attachChild(this);
        }
    }

    /**
     * Create a new node with the given name, parent and transform
     * @param parent
     * @param frameName
     * @param xfm
     */
    protected FrameTreeNode(FrameTreeNode parent, String frameName, ReadOnlyTransform xfm) {
        m_data = new Frame(frameName, xfm);
        if ( parent != null ) {
            parent.attachChild(this);
        }       
    }
    
    /**
     * Create a new FrameTreeNode with null parent and a new Frame with name frameName
     * @param frameName name for new Frame
     */
    public FrameTreeNode(String frameName, ReadOnlyTransform xfm) {
        m_parent = null;
        m_data   = new Frame(frameName, xfm);
    }

    /**
     * Set the Frame data to toCopy
     * Only payload data is copied, not children or parent
     * @todo Mark: explain the rationale behind this. Shouldn't this
     * method call something like setFrame, or have a different signature
     * like set(Frame)
     * @return this
     */
    public FrameTreeNode set(FrameTreeNode toCopy) {
        m_data.set(toCopy.getData());
        return this;
    }

    /**
     * Clone node and its offsprings. The new node is becoming a child of parent.
     * Default is NULL, which creates a new root node.
     * 
     * @param parent
     */
    public FrameTreeNode clone(FrameTreeNode parent) {
        FrameTreeNode node = new FrameTreeNode(parent, m_data);
        for (FrameTreeNode child : m_children) {
            child.clone(parent);
        }
        return node;
    }

    /** for compatibility with c++ api */
    public Frame getData() {
        return m_data;
    }
    public Frame getFrame() {
        return m_data;
    }

    /**
     * Attach a child node. If the node has an existing parent, it is re-parented to this.
     * FIXME what do we do when a child with the same name is attached? 
     * Throw an exception or merge?
     * @return node if attach was successful, else null
     */
    public FrameTreeNode attachChild(FrameTreeNode node) {
        synchronized(m_children) {
            if( !(node == this || m_children.contains(node)) ) {
                node.detachFromParent();
                m_children.add(node);
                node.m_parent = this;
                return node;
            }
        }
        throw new RuntimeException("Child already exists");
        //return null;
    }

    /**
     * Merge a child. If a matching child exists, perform a 
     * FrameTree.mergeFrameTrees, otherwise, attachChild();
     * @param node
     * @return
     */
    public FrameTreeNode mergeChild(FrameTreeNode node) {
        FrameTreeNode child = findChild(node.getFrame().getName());
        if(child == null) {
            attachChild(node);
            return node;
        }
        else {
            FrameTree.mergeFrameTrees(node, child);
            return child;
        }
    }

    /**
     * Find a child with matching name
     * @param name
     * @return
     */
    public FrameTreeNode findChild(String name) {
        synchronized(m_children) {
            for(FrameTreeNode child : m_children) {
                if(child.getFrame().getName().equals(name))
                    return child;
            }
        }
        return null;
    }

    /**
     * Remove node from list of children and set it's parent to null
     */
    public boolean removeChild(FrameTreeNode node) {
        synchronized(m_children) {
            if( m_children.contains(node) ) {
                m_children.remove(node);
                node.m_parent = null;
            }
        }
        return false;
    }

    public void detachFromParent() {
        if(m_parent != null) {
            m_parent.removeChild(this);
        }
    }

    /**
     * Set the parent of the node.
     * This call re-parents the sub-tree. If the node had a parent before,
     * it gets removed from the previous parent's list of children.
     *
     * Setting the parent to NULL makes the node a root-node.
     */
    public void setParent(FrameTreeNode newParent) {
        detachFromParent();
        if(newParent != null) {
            newParent.attachChild(this);
        }
    }

    /**
     * @return parent node
     */
    public FrameTreeNode getParent() {
        return m_parent;
    }

    /**
     * @return true of parent == null
     */
    public boolean isRoot() {
        return (m_parent == null);
    }

    /**
     * @return true if node has no children
     */
    public boolean isLeaf() {
        return (m_children.size() == 0);
    }

    /**
     * Search upward in tree to find root. 
     * Linear time. 
     */
    public FrameTreeNode getRoot() {
        FrameTreeNode node = this;
        while( node.isRoot() == false ) {
            node = node.getParent();
        }
        return node;
    }

    /**
     * @return true if this is ancestor of node
     */
    public boolean isAncestorOf(FrameTreeNode node) {
        while(node != null) {
            node = node.getParent();
            if(node == this) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get an ordered list of ancestors which begins at root and 
     * ends at (and includes) this node
     * @return
     */
    public List<FrameTreeNode> getAncestry() {
        return getAncestry(new LinkedList<FrameTreeNode>());
    }
    public List<FrameTreeNode> getAncestry(List<FrameTreeNode> store) {
        store.clear();
        FrameTreeNode node = this;
        while(node != null) {
            store.add(0, node);
            node = node.getParent();
        }
        return store;
    }


    /**
     * Calculate the last common ancestor.
     * @return null if the nodes are not connected.  If either node is
     * a direct ancestor of the other, that node is returned.
     */
    public FrameTreeNode getLastCommonAncestor(FrameTreeNode that) {
        return getLastCommonAncestor(that, new LinkedList<FrameTreeNode>(), new LinkedList<FrameTreeNode>());
    }
    public FrameTreeNode getLastCommonAncestor(FrameTreeNode that, List<FrameTreeNode> thisAncestors, List<FrameTreeNode> thatAncestors) {
        this.getAncestry(thisAncestors);
        that.getAncestry(thatAncestors);
        Iterator<FrameTreeNode> thisIt = thisAncestors.iterator();
        Iterator<FrameTreeNode> thatIt = thatAncestors.iterator();

        FrameTreeNode thisNode = thisIt.next();
        FrameTreeNode thatNode = thatIt.next();
        if(thisNode != thatNode) { // no common root
            return null;
        }
        FrameTreeNode last = thisNode;
        while( thisIt.hasNext() && thatIt.hasNext() ) {
            thisNode = thisIt.next();
            thatNode = thatIt.next();
            if(thisNode != thatNode) {
                return last;
            }
            last = thisNode;
        }
        return last;
    }

    /**
     * @return a copy of this node's list of children
     */
    public List<FrameTreeNode> getChildren() {
        return getChildren(false);
    }
    public List<FrameTreeNode> getChildren(boolean sortByName) {
        ArrayList<FrameTreeNode> retVal;
        synchronized(m_children) {
            retVal = new ArrayList<FrameTreeNode>(m_children);
        }
        if(sortByName) {
            Collections.sort(retVal, FrameTreeNodeNameComparator.instance);
        }
        return retVal;
    }

    /**
     * get the number of direct decendants of this node
     * @return
     */
    public int getNumChildren() {
        return m_children.size();
    }

    /**
     * Recursively count number of nodes underneath this node
     * @return
     */
    public int getNumOffspring() {
        int numOffspring = 0;
        for(FrameTreeNode child : m_children) {
            numOffspring += 1 + child.getNumOffspring();
        }
        return numOffspring;
    }


    /**
     * Breadth first traversal. The visitor's up() and down() methods 
     * are not called. 
     * @return true only if the visitor has a stop condition and the condition is met
     */
    public boolean traverseBreadthFirst(IFrameVisitor visitor) {
        Queue<FrameTreeNode> queue = new LinkedList<FrameTreeNode>();
        queue.add(this);
        FrameTreeNode node;
        while( (node = queue.poll()) != null ) {
            if(visitor.visit(node)) {
                return true;
            }
            synchronized(m_children) {
                queue.addAll(node.m_children);
            }
        }
        return false;
    }


    /**
     * Depth first, pre-order traversal
     * @return true only if the visitor has a stop condition and the condition is met
     */
    public boolean traversePreOrder(IFrameVisitor visitor) {
        return traversePreOrder(visitor, false);
    }
    public synchronized boolean traversePreOrder(IFrameVisitor visitor, boolean sortByName) {
        if(visitor.visit(this)) {
            return true;
        }
        List<FrameTreeNode> children;
        synchronized(m_children) {
            if(sortByName) {
                children = getChildren(true);
            }
            else {
                children = m_children;
            }

            for(FrameTreeNode node : children) {
                visitor.down(this);
                if(node.traversePreOrder(visitor, sortByName)) {
                    return true;
                }
                visitor.up(this);
            }
        }
        return false;
    }

    /**
     * Depth first, post-order traversal
     * @return true only if the visitor has a stop condition and the condition is met
     */
    public synchronized boolean traversePostOrder(IFrameVisitor visitor) {
        for(FrameTreeNode node : m_children) {
            visitor.down(this);
            if(node.traversePostOrder(visitor)) {
                return true;
            }
            visitor.up(this);
        }
        return visitor.visit(this);
    }

    /**
     * If the contained Frame name matches this one, return equal
     */
    @Override 
    public boolean equals(Object o) {
        if(o instanceof FrameTreeNode) {
            FrameTreeNode that = (FrameTreeNode)o;
            if(this.getFrame().getName().equals(that.getFrame().getName())){
                return true;
            }
            return false;
        }
        else {
            return super.equals(o);
        }
    }
}


