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
package gov.nasa.arc.viz.scenegraph.operation;

import java.util.HashMap;
import java.util.Map.Entry;

import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.scenegraph.hint.CullHint;

/**
 * HideOperation takes a list of spatial names to leave visible
 * and hides all other Meshes. The prior visibility state
 * is stored so it can be restored later.
 * @author mallan
 *
 */
public class HideOperation {
	class StoredState {
		public CullHint cullHint;
		public StoredState(CullHint cullHint) {
			this.cullHint = cullHint;
		}
	}

	HashMap<Spatial,StoredState> m_storedStateMap = new HashMap<Spatial,StoredState>();
	String[] m_visibleNodeNames = null;

	public HideOperation() {
	    //
	}

	public void setVisibleNodeNames(String ...names) {
		m_visibleNodeNames = names;
	}

	/**
	 * if name matches, stop recursion
	 * @param current
	 */
	protected void recurse(Spatial current) {
		StoredState state = new StoredState(current.getSceneHints().getCullHint());
		boolean matches = false;
		for(String name : m_visibleNodeNames) {
			matches = name.equals(current.getName());
		}
		if(!matches) {
			if(current instanceof Mesh) {
				m_storedStateMap.put(current, state);
				current.getSceneHints().setCullHint(CullHint.Always);
			}
			if(current instanceof Node) {
				Node currentNode = (Node)current;
				for(Spatial child : currentNode.getChildren()) {
					recurse(child);
				}
			}
		}
	}

	public void execute(Node root) {
		recurse(root);
	}

	/**
	 * restore prior state
	 */
	public void restore() {
		for(Entry<Spatial,StoredState> entry : m_storedStateMap.entrySet()) {
			Spatial spatial   = entry.getKey();
			StoredState state = entry.getValue();
			spatial.getSceneHints().setCullHint(state.cullHint);
		}
		m_storedStateMap.clear();
	}

}
