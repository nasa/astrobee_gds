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
package gov.nasa.arc.verve.ardor3d.scenegraph.util;

import java.util.LinkedList;
import java.util.List;

import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.Spatial;

public class NodeUtil {
	
	/**
	 * Find all children of 'root' with names exactly matching 'name'.
	 * Does not check root for name equality. 
	 */
	public static List<Spatial> findChildrenByName(String name, Node root) {
		List<Spatial> retVal = new LinkedList<Spatial>();
		return findChildrenByName(name, root, retVal, 0);
	}
	
	/** 
	 * Recursively add children of 'root' with names exactly matching 'name' to 'inList'.
	 * Does not check root for name equality. 
	 */
	public static List<Spatial> findChildrenByName(String name, Node root, List<Spatial> inList, int depth) {
		List<Spatial> childList = root.getChildren();
		String childName;
		if(childList != null) {
			for(Spatial child : childList) {
				childName = child.getName();
				if(childName != null && childName.equals(name)) {
					inList.add(child);
				}
				if(child instanceof Node) {
					findChildrenByName(name, (Node)child, inList, depth+1);
				}
			}
		}
		return inList;
	}

	/**
	 * Find first Spatial under 'node' of a particular Class type
	 */
	@SuppressWarnings("unchecked")
	public static final Spatial find(Class klass, Node parent) {
		if (parent == null)
			return(null);
		if (klass.isInstance(parent))
			return(parent);
		for (int i = 0; i < parent.getNumberOfChildren(); ++i) {
			Spatial child = parent.getChild(i);
			if (klass.isInstance(child))
				return(child);
			if (child instanceof Node) {
				child = find(klass, (Node)child);
				if (child != null)
					return(child);
			}
		}
		return(null);
	}

	public static final Spatial find(String name, Node parent) {
		if (parent == null)
			return(null);
		Spatial child = parent.getChild(name);
		if (child != null)
			return(child);
		for (int i = 0; i < parent.getNumberOfChildren(); ++i) {
			child = parent.getChild(i);
			if (child instanceof Node) {
				child = find(name, (Node)child);
				if (child != null)
					return(child);
			}
		}
		return(null);
	}


	/**
	 * Get the full scene path name of this Spatial 
	 */
	public static String getPathString(Spatial spatial) {
		String retVal = "";
		String str = spatial.getName();
		if(str == null) str = "NULL";
		retVal = str;
		
		Spatial thisSpatial = spatial.getParent();
		while(thisSpatial != null) {
			str = thisSpatial.getName();
			if(str == null) str = "NULL";
			retVal = str + "." + retVal;
			thisSpatial = thisSpatial.getParent();
		}
		return retVal;
	}
}
