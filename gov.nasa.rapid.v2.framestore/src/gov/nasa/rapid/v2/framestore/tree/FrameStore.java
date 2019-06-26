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

import gov.nasa.rapid.v2.framestore.FrameStoreException;
import gov.nasa.rapid.v2.framestore.tree.updaters.IFrameUpdater;
import gov.nasa.rapid.v2.framestore.tree.visitors.AbstractFrameVisitor;
import gov.nasa.rapid.v2.framestore.tree.visitors.IFrameVisitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.ardor3d.math.Transform;
import com.ardor3d.math.type.ReadOnlyTransform;

public class FrameStore {
    /**
     * @brief Get a copy of the frame tree.
     * The vector holds a set of FrameTreeNode objects, which describe
     * all tree(s) of the FrameStore.  The FrameStore is copied in
     * pre-order, so the first element of the vector is the root node
     * of the first tree.
     *
     * Note that the FrameTreeNodes have a completely different
     * interface than the FrameStore. FrameTreeNodes don't have
     * locking, so this is a static snapshot of the tree.
     */
    void clone_tree(FrameTree tree, FrameTreeNode rootFrame) {
        // TODO Implement clone_tree method!
        // ask hans about this...
        System.err.println("FrameStore.clone_tree method currently not implemented!");
    }

    /**
     * @brief Get a copy of the frame tree.
     * The rootFrame is required to be non-NULL, otherwise
     * FrameStoreException is thrown.
     */
    public synchronized FrameTreeNode cloneTree(FrameTreeNode rootFrame) {
        return rootFrame.clone(null);
    }


    /**
     * Get name of frame.
     * @param frame
     */
    public synchronized String getNameOf(FrameTreeNode frame) {
        return frame.getData().getName();
    }


    /**
     * Get fully qualified name of frame, including path of all parent frames.
     * @param frame
     */
    public static synchronized String getFullNameOf(FrameTreeNode frame) {
        return FrameTree.getFullNameOf(frame);
    }


    /**
     * @return list of fully qualified names of all frames.
     */
    public synchronized List<String> getFrameNames() {
        final ArrayList<String> names = new ArrayList<String>();
        IFrameVisitor visitor = new AbstractFrameVisitor() {
            @Override
            public boolean visit(FrameTreeNode node) {
                names.add(FrameTree.getFullNameOf(node));
                return false;
            }
        };
        for( FrameTreeNode root : m_rootNodes ) {
            root.traversePreOrder(visitor);
        }
        return names;
    }

    /**
     * Apply visitor to all root nodes in the forest
     * @param visitor
     */
    public synchronized void applyVisitorPreOrder(IFrameVisitor visitor) {
        for( FrameTreeNode root : m_rootNodes ) {
            root.traversePreOrder(visitor);
        }
    }

    /**
     * Return the parent Frame
     */
    public synchronized FrameTreeNode getParent(FrameTreeNode frame) {
        return frame.getParent();
    }


    /**
     * Get the list of direct children of a frame.
     */
    public synchronized List<FrameTreeNode> getChildren(FrameTreeNode frame) {
        if( frame == null ) {
            return new ArrayList<FrameTreeNode>(m_rootNodes);
        }
        else {
            return new ArrayList<FrameTreeNode>(frame.getChildren());
        }
    }


    /**
     * Lookup a frame by name.
     * @param name
     *
     * Note our special lookup naming semantics: Frame names are
     * alphanumeric only. . and / are reserved characters.  A frame
     * name can be specified by giving it's absolute or relative path
     * in Unix file-system convention: /rootNode/myNode or
     * ../../grandParent/uncleFrame.
     *
     * A special wild-card is the ... which starts a breadth-first
     * expansion of the tree.  So .../myNode will return the first
     * node named myNode beneath the scope-node in a bread-first
     * expansion.  As the ordering of children is not defined, it is
     * not guaranteed which node is returned if multiple nodes with
     * the same name are specified at the same depth level.
     *
     * @param scope
     * If a non-NULL scope frame is passed as second parameter, the
     * search is restricted to the sub-tree spawned by this frame.
     */
    public synchronized FrameTreeNode lookup(String name, FrameTreeNode scope)  {
        String searchName = name;
        if (scope == null && name.length() > 0 && name.charAt(0) != '/') {
            // try to explicitly resolve the root frames 
            searchName = "/" + name; // << XXX hans, I don't get this
            for(FrameTreeNode root : m_rootNodes) {
                FrameTreeNode node = FrameTree.lookup(root, searchName);
                if (node != null)
                    return node;
            }
        }

        searchName = name;
        // if not explicitly state otherwise, we search for .../name
        if(name.length() > 0  && name.charAt(0) != '/') {
            if(!name.startsWith(".../")) {
                searchName = ".../" + name;
            }
        }
        if (scope == null || (searchName.length() > 0  && searchName.charAt(0) == '/')) {
            for(FrameTreeNode root : m_rootNodes) {
                FrameTreeNode node = FrameTree.lookup(root, searchName);
                if (node != null)
                    return node;
            }
            return null;
        }

        return FrameTree.lookup(scope, searchName);
    }

    /**
     * @see FrameStore.lookup(String name, FrameTreeNode scope)
     */
    public FrameTreeNode lookup(String name)  {
        return lookup(name, null);
    }
    
    /**
     * Add a new frame to the frame store.
     * @param name name of frame, must not be null or empty
     * @param parent may be null
     * @param xfm frame transform, may be null (transform will be set to identity)
     * @throws FrameStoreException if parent is not part of the FrameStore, or name is illegal
     */
    public synchronized FrameTreeNode add(String name, FrameTreeNode parent, ReadOnlyTransform xfm) throws FrameStoreException {
        if(name == null || name.length() == 0) {
            throw new FrameStoreException("Illegal name: must not be null or empty");
        }
        FrameTreeNode node = new FrameTreeNode(new Frame(name, xfm));
        return add(node, parent);
    }

	/**
	 * Add a branch to the frame store. The FrameStore takes ownership of the passed sub-tree.
	 * 
	 * @param node
	 * @param parent
	 * @throws FrameStoreException
	 *             if node is null or already part of the FrameStore. Or, if parent is not part of the FrameStore
	 */
	public synchronized FrameTreeNode add(FrameTreeNode node, FrameTreeNode parent) throws FrameStoreException {
		if (node == null)
			throw new FrameStoreException("node cannot be null");
		if (isMember(node))
			throw new FrameStoreException("node already a member of this FrameStore");
		if (parent != null && !isMember(parent))
			throw new FrameStoreException("parent is not a member of this FrameStore");

		assertUnique(node.getData().getName(), parent);

		if (parent == null) {
			m_rootNodes.add(node);
		} else {
			node.setParent(parent);
		}
		return node;
	}

	/**
	 * Merging a tree with the the frame store. The root node names of tree and mergeRoot must match, otherwise this is not a valid
	 * merge.
	 * 
	 * @param tree
	 *            the FrameStore takes ownership of the passed sub-tree.
	 * @param mergeRoot
	 *            The start-node for the merge operation. The root is required to have the same name as the node. If null, a merge
	 *            will be attempted with the first tree managed by the FrameStore that has a matching root name. If no root names
	 *            match, tree will be added to the list of root nodes (i.e. the forest)
	 * @return true if tree was merged, false if tree was added to forest
	 * @throws FrameStoreException
	 */
	public synchronized boolean mergeTree(FrameTreeNode tree, FrameTreeNode mergeRoot) throws FrameStoreException {
		if (mergeRoot != null) {
			mergeFrameTrees(tree, mergeRoot);
			return true;
		} else {
			for (FrameTreeNode root : m_rootNodes) {
				if (root.getFrame().getName().equals(tree.getFrame().getName())) {
					FrameTree.mergeFrameTrees(tree, root);
					return true;
				}
			}
		}
		// just add the tree to the forest
		m_rootNodes.add(tree);
		return false;
	}

	/**
	 * Merge two unmanaged trees
	 * 
	 * @throws FrameStoreException
	 *             root nodes of trees to be merged must match
	 */
	public static void mergeFrameTrees(FrameTreeNode srcTree, FrameTreeNode tgtTree) throws FrameStoreException {
	    final String srcName = srcTree.getFrame().getName();
	    final String tgtName = tgtTree.getFrame().getName();
	    if (srcName.equals(tgtName)) {
			FrameTree.mergeFrameTrees(srcTree, tgtTree);
		} 
	    else {
	        throw new FrameStoreException("invalid merge: tree root names do not match");
	    }
	}

	/**
	 * Delete frame from tree.
	 * 
	 * @param frame
	 * @param recursive
	 *            If recursive is set to false, all children of the frame will be added as root-frames to the FrameStore.
	 */
	public synchronized void del(FrameTreeNode frame, boolean recursive) {
		// TODO Need to implement Frame Deletion!
	    System.err.println("FrameStore.del method currently not implemented!");
	}

	/**
	 * Reparent a frame. If the node had a parent before, it gets removed from the previous parent's list of children.
	 * 
	 * @param frame
	 * @param parent
	 */
	public synchronized void setParent(FrameTreeNode frame, FrameTreeNode parent) {
		frame.setParent(parent);
	}

	/**
	 * Return root node of specified frame. The frame-store can hold multiple-root nodes.
	 * 
	 * @param frame
	 */
	public synchronized FrameTreeNode getRoot(FrameTreeNode frame) {
		return frame.getRoot();
	}

    //! @{ Public predicates.

	/**
	 * Test if frame is a root frame. That is, does not have a parent.
	 * 
	 * @param frame
	 */
	public synchronized boolean isRoot(FrameTreeNode frame) {
		return frame.isRoot();
	}

    //! @{ Public predicates.

	/**
	 * Test if frame is a leaf frame. That is, does not have any children.
	 * 
	 * @param frame
	 */
	public synchronized boolean isLeaf(FrameTreeNode frame) {
		return frame.isLeaf();
	}

	/**
	 * Test if frame is somewhere up in the chain of parents of pop.
	 * 
	 * @param frame
	 * @param pop
	 */
	public synchronized boolean isAncestorOf(FrameTreeNode frame, FrameTreeNode pop) {
		return frame.isAncestorOf(pop);
	}

	/**
	 * Test if the frame belongs to this FrameStore instance.
	 */
	public synchronized boolean isMember(FrameTreeNode node) {
		if (node != null) {
			for (FrameTreeNode root : m_rootNodes) {
				if (root == node || root.isAncestorOf(node))
					return true;
			}
		}
		return false;
	}

    //! @}

	/**
	 * Return the location of source expressed relative to wrtFrame.
	 * 
	 * @param wrtFrame
	 * @param source
	 */
	public synchronized Transform getTransform(FrameTreeNode wrtFrame, FrameTreeNode source) {
		return FrameTree.getTransform(wrtFrame, source);
	}

	/**
	 * @return the transform from wrtFrame to loc.
	 * @param wrtFrame
	 * @param source
	 * @param loc
	 *            transform in the frame of source
	 */
	public synchronized Transform getTransformOf(FrameTreeNode wrtFrame, FrameTreeNode source, ReadOnlyTransform loc) {
		return FrameTree.getTransformOf(wrtFrame, source, loc);
	}

	/**
	 * Set the transform of frame to update, which is expressed relative to wrtFrame.
	 * 
	 * @param frame
	 * @param wrtFrame
	 * @param update
	 */
	public synchronized void setTransform(FrameTreeNode frame, FrameTreeNode wrtFrame, ReadOnlyTransform update) {
		FrameTree.setTransform(frame, wrtFrame, update);
	}

	/**
	 * Update the location of frame to update, expressed relative to parent.
	 * 
	 * @param frame
	 * @param update
	 */
	public synchronized void setLocationRel(FrameTreeNode frame, ReadOnlyTransform update) {
		FrameTree.setTransform(frame, null, update);
	}

    /**
     * Update a set of frames at once.
     */
    public synchronized void updateFrames(Collection<? extends IFrameUpdater> updates) {
        for(IFrameUpdater update : updates) {
            update.apply();
        }
    }

	/**
	 * assert uniqueness of name
	 * 
	 * @param name
	 * @param parent
	 * @throws FrameStoreException
	 */
	protected synchronized void assertUnique(String name, FrameTreeNode parent) throws FrameStoreException {
		if (parent == null) {
			for (FrameTreeNode node : m_rootNodes) {
				if (name.equals(node.getData().getName())) {
					throw new FrameStoreException("Name \"" + name + "\" is not unique as root node.");
				}
			}
		} else {
			for (FrameTreeNode node : parent.getChildren()) {
				if (name.equals(node.getData().getName())) {
					throw new FrameStoreException("Name \"" + name + "\" is not unique as child node of "
							+ FrameTree.getFullNameOf(parent));
				}
			}
		}
	}

    /** The vector of root nodes. */
    protected final ArrayList<FrameTreeNode> m_rootNodes = new ArrayList<FrameTreeNode>();
}
