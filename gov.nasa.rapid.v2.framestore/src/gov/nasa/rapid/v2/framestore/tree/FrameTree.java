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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Queue;

import org.apache.log4j.Logger;

import com.ardor3d.math.Transform;
import com.ardor3d.math.type.ReadOnlyTransform;

public class FrameTree {
	private static Logger logger = Logger.getLogger(FrameTree.class);
	
    /**
     * Get fully qualified name of frame, including path of all parent frames.
     * @param frame
     */
    public static String getFullNameOf(FrameTreeNode frame) {
        List<FrameTreeNode> ancestry = frame.getAncestry();
        StringBuilder builder = new StringBuilder();
        for(FrameTreeNode ancestor : ancestry) {
            builder.append("/").append(ancestor.getData().getName());
        }
        return builder.toString();
    }

	/**
	 * Get transform of the source frame relative to the wrt_frame (wrt = with respect to).
	 * 
	 * @param wrtFrame
	 *            The coordinate frame to convert to. If NULL is passed as source frame, source->parent() is assumed and
	 *            source->getFrame().getTransform() is returned.
	 * @param source
	 *            The source coordinate frame. If NULL, the identity-matrix is returned.
	 * @param retVal
	 *            store, if NULL, a new matrix is first created
	 * @return transform of source with respect to wrtFrame
	 */
    public static Transform getTransform(FrameTreeNode wrtFrame, FrameTreeNode source, Transform retVal) {
        if ( retVal == null ) {
            retVal = new Transform();
        }
        retVal.setIdentity();
        if (source != null) {
            if (wrtFrame == null) {
                return source.getData().getTransform(retVal);
            }
            if (wrtFrame != source) {
                List<FrameTreeNode> wrtFrameAncestors = new LinkedList<FrameTreeNode>();
                List<FrameTreeNode> sourceAncestors = new LinkedList<FrameTreeNode>();
                FrameTreeNode ancestor = wrtFrame.getLastCommonAncestor(source, wrtFrameAncestors, sourceAncestors);
                Transform tmp = new Transform();
                if (ancestor != null) {
                    // from the origin frame to the ancestor *** ask Hans about these comments...
                    {
                        int ancestorIndex = wrtFrameAncestors.indexOf(ancestor);
                        ListIterator<FrameTreeNode> nodeIt = wrtFrameAncestors.listIterator(ancestorIndex);
                        nodeIt.next();
                        while(nodeIt.hasNext()) {
                            FrameTreeNode node = nodeIt.next();
                            retVal.multiply(node.getData().getTransform(), tmp);
                            retVal.set(tmp);
                        }                            
                        retVal.invert(retVal);
                    }
                    // from the last common ancestor to the wrt frame
                    {
                        int ancestorIndex = sourceAncestors.indexOf(ancestor);
                        ListIterator<FrameTreeNode> nodeIt = sourceAncestors.listIterator(ancestorIndex);
                        nodeIt.next();
                        while(nodeIt.hasNext()) {
                            FrameTreeNode node = nodeIt.next();
                            retVal.multiply(node.getData().getTransform(), tmp);
                            retVal.set(tmp);
                        }
                    }
                }
            }
        }
        return retVal;
    }

    /**
     * @param wrtFrame
     * @param source
     * @return transform of source with respect to wrtFrame
     */
    public static Transform getTransform(FrameTreeNode wrtFrame, FrameTreeNode source) {
        Transform retVal = new Transform();
        return getTransform(wrtFrame, source, retVal);
    }

    /**
     * Get transform of location, expressed relative to the source frame, 
     * relative to the wrt_frame (wrt = with respect to). 
     */
    static Transform getTransformOf(FrameTreeNode wrtFrame, FrameTreeNode source, ReadOnlyTransform location) {
        Transform retVal = getTransform(wrtFrame, source);
        retVal = retVal.multiply(location, null);
        return retVal;
    }

    /**
     *  Set transform of the frame to the location specified relative to the source frame.
     */
    static void setTransform(FrameTreeNode frame, FrameTreeNode wrtFrame, ReadOnlyTransform loc) {
        if (frame != null) {
            if (wrtFrame == null) {
                frame.getData().setTransform(loc);
            }
            else {
                frame.getData().setTransform(getTransformOf(frame.getParent(), wrtFrame, loc));
            }
        }
    }

	/**
	 * TODO Lookup frame by name See the FrameTreeNode typedef for the requirements on Frame naming.
	 * 
	 * @param startFrame
	 *            The node relative to which the search starts.
	 * @param path
	 *            An expression describing the path to the searched frame.
	 *            
     *  * '/' is the frame-name delimiter. '.' is used for directory wildcards.
     *  Frame names can not contain these characters.
     *  * A leading "/" starts the lookup from the start_frame.root().
     *  * ".", "..", and "..." are treated as following
     *    * ".": this frame - being ignored in practice
     *    * "..": parent frame
     *    * "...": 0 to n frames down in breadth-first order
     */
    public static FrameTreeNode lookup(FrameTreeNode startFrame, String path) {
    	//logger.debug("lookup "+path);
        if(startFrame == null || path == null || path.length() == 0) {
            return null;
        }
        ArrayList<String> elements = new ArrayList<String>(Arrays.asList(path.split("/")));
        elements.remove("");
        elements.remove(".");
        if(elements.size() == 0) {
            return null;
        }

        ListIterator<String> iterator = elements.listIterator();
        if( path.startsWith("/") ) {
        	String firstElement;
            firstElement = iterator.next();
            while(firstElement.length() < 1 && iterator.hasNext()) {
            	firstElement = iterator.next(); // sometimes first element is "" ?!?
            }
            if( !firstElement.equals(startFrame.getFrame().getName()) ) {
                return null;
            }
        }
        return matchNode(startFrame, elements, iterator);
    }

    /**
     * 
     * @param node
     * @param elements
     * @param iterator
     * @return
     */
    @SuppressWarnings("null")
    protected static FrameTreeNode matchNode(FrameTreeNode node, final List<String> elements, final ListIterator<String> iterator)
    {
        if(node == null) {
            logger.warn("FrameTree.matchNode entered with node == null");
            return null;
        }
        final FrameTreeNode orig = node;
        String element = null;
        String previous;
        while(iterator.hasNext()) {
        	previous = element;
            element  = iterator.next();
            if(node == null) {
                logger.warn("FrameTree.matchNode node == null in loop. element="+element+", previous="+previous+", orig="+orig.getFrame().getName());
                return null;
            }        	
            if( !iterator.hasNext() && element.equals(node.getFrame().getName()) ){
                return node;
            }
            else if( element.length() == 0 || element.equals(".")) {
                // do nothing
            }
            else if(element.equals("..")) {
                // one up if not root
                if(!node.isRoot())
                    node = node.getParent();
            }
            else if(element.equals("...")) {
                ArrayList<FrameTreeNode> foundNodes = new ArrayList<FrameTreeNode>();
                // do a breadth first, recursive search for the rest of the path
                Queue<FrameTreeNode> searchQueue = new LinkedList<FrameTreeNode>();
                searchQueue.add(node);
                while( !searchQueue.isEmpty() ) {
                    ListIterator<String> searchIterator = elements.listIterator(iterator.nextIndex());
                    FrameTreeNode searchNode = searchQueue.poll();
                    FrameTreeNode foundNode = matchNode(searchNode, elements, searchIterator);
                    if(foundNode != null) {
                    	// XXX matchNode will return foundNode if searchNode matches, and if one of searchNode.children matches
                        if(!foundNodes.contains(foundNode)) {
                        	foundNodes.add(foundNode);
                        }
                    }
                    searchQueue.addAll(searchNode.getChildren());
                }
                if(foundNodes.size() == 1) {
                    return foundNodes.get(0);
                }
                else if(foundNodes.size() > 1) {
                    // FIXME do we return first, null, or throw an exception in this case? 
                    logger.warn("... search not unique; "+foundNodes.size()+" matches found. Returning first match.");
                    for(String e : elements) {
                    	logger.warn("element /"+e);
                    }
                    for(FrameTreeNode n : foundNodes) {
                    	logger.warn("    foundNode: "+n+" = "+getFullNameOf(n));
                    }
                    return foundNodes.get(0);
                }
                else {
                    return null;
                }
            }
            else {
                // match one-by-one
                FrameTreeNode match = null;
                List<FrameTreeNode> children = node.getChildren();
                for( FrameTreeNode child : children ) {
                    if(child.getFrame().getName().equals(element)) {
                        match = child;
                        break;
                    }
                }
                if(match == null) {
                    return null;
                }
                node = match;
            }
        }
        return node;
    }


    /**
     * Merging srcTree into tgtTree.
     * Source and target tree need to start with the same root node.
     * Frame tree nodes with the same name are considered a match.
     * The transformation matrix for a matched node stays in the target tree stays untouched.
     * New branches are swallowed by the target tree.
     * @internal
     */
    static void mergeFrameTrees(FrameTreeNode srcTree, FrameTreeNode tgtTree) {
        // source and target must share common root name
        if(!tgtTree.getData().getName().equals(srcTree.getData().getName())) {
            return;
        }

        List<FrameTreeNode> srcChildren = srcTree.getChildren();
        if(srcChildren.size() > 0) {
            List<FrameTreeNode> tgtChildren = tgtTree.getChildren();
            FrameTreeNodeNameComparator c = new FrameTreeNodeNameComparator();
            Collections.sort(srcChildren, c);
            Collections.sort(tgtChildren, c);

            ListIterator<FrameTreeNode> tgtIt = tgtChildren.listIterator();
            FrameTreeNode tgtChild = null;
            if(tgtIt.hasNext()) {
                tgtChild = tgtIt.next();
            }
            for(FrameTreeNode srcChild : srcChildren) {
                while(tgtChild != null && srcChild.getData().getName().compareTo(tgtChild.getData().getName()) > 0 ) {
                    if(tgtIt.hasNext())
                        tgtChild = tgtIt.next();
                    else 
                        tgtChild = null;
                }
                if(tgtChild == null || srcChild.getData().getName().compareTo(tgtChild.getData().getName()) < 0) {
                    srcChild.setParent(tgtTree);
                }
                else if(srcChild.getData().getName().compareTo(tgtChild.getData().getName()) == 0) {
                    mergeFrameTrees(srcChild, tgtChild);
                }
            }
        }
    }
}
