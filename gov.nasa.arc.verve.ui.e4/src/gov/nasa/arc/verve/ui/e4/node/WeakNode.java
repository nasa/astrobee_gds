package gov.nasa.arc.verve.ui.e4.node;
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

import gov.nasa.arc.verve.common.Activator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.swt.graphics.Image;

import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.Spatial;

public class WeakNode extends WeakSpatial {
	public WeakNode(Node spatial){
		super(spatial);
	}
	
	/**
	 * WARNING: do NOT hang on to the returned node!!!
	 * @return
	 */
	public Node getNode() {
		if (m_spatial != null){
			return (Node)m_spatial.get();
		}
		return null;
	}
	
	@Override
	public boolean isNode() {
		return true;
	}
	
	@Override
	public boolean hasChildren() {
		Node node = (Node)getSpatial();
		if (node == null){
			return false;
		}
		return !node.getChildren().isEmpty();
	}
	
	@Override
	public List<WeakSpatial> getChildren() {
		Node node = (Node)getSpatial();
		if (node == null){
			return Collections.EMPTY_LIST;
		}
		List<WeakSpatial> result = new ArrayList<WeakSpatial>();
		for (Spatial s : node.getChildren()){
			if (s instanceof Node){
				result.add(new WeakNode((Node)s));
			} else {
				result.add(new WeakSpatial(s));
			}
		}
		return result;
	}
	
	public boolean isNetworkLink(){
		Spatial spatial = getSpatial();
		if (spatial != null){
			return spatial.getClass().getSimpleName().equals("KmlNetworkLink");
		}
		return false;
	}
	
	/**
	 * Get the icon image for the tree
	 * @return
	 */
	@Override
    public Image getImage(){
		String icon = "folder";
		if(isGroundOverlay()){
			icon = "ground_overlay";
		} else if (isNetworkLink()) {
			icon = "network_link";
		}
		return Activator.getImageFromRegistry(icon);
	}
}
