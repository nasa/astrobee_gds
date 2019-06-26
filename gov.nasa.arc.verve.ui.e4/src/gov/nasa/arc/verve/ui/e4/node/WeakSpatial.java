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
import gov.nasa.arc.verve.common.node.INodeChangedListener;
import gov.nasa.arc.verve.common.node.INodeChanger;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;

import org.eclipse.swt.graphics.Image;

import com.ardor3d.scenegraph.Line;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.scenegraph.hint.CullHint;
import com.ardor3d.ui.text.BMText;

public class WeakSpatial {

	protected WeakReference<Spatial> m_spatial = null;
	
	public WeakSpatial(Spatial spatial){
		m_spatial = new WeakReference<Spatial>(spatial);
	}
	
	/**
	 * WARNING: do NOT hang onto the returned spatial!
	 * @return
	 */
	public Spatial getSpatial() {
		if (m_spatial != null){
			return m_spatial.get();
		}
		return null;
	}
	
	@Override
	public String toString() {
		Spatial spatial = getSpatial();
		if (spatial != null){
			return spatial.toString();
		}
		return "";
	}
	public void addNodeChangeListener(INodeChangedListener listener){
		Spatial spatial = getSpatial();
		if (spatial != null && spatial instanceof INodeChanger){
			((INodeChanger)spatial).addNodeChangeListener(listener);
		}
	}
	
	public WeakNode getParent() {
		Spatial spatial = getSpatial();
		if (spatial != null && spatial.getParent() != null){
			return new WeakNode(spatial.getParent());
		}
		return null;
	}
	public String getName() {
		Spatial spatial = getSpatial();
		if (spatial != null){
			return spatial.getName();
		}
		return null;
	}
	
	public boolean isNode(){
		Spatial spatial = getSpatial();
		if (spatial != null){
			return spatial instanceof Node;
		}
		return false;
	}
	
	public boolean isBMText() {
		Spatial spatial = getSpatial();
		if (spatial != null){
			return spatial instanceof BMText;
		}
		return false;
	}
	
	public boolean isMesh() {
		Spatial spatial = getSpatial();
		if (spatial != null){
			return spatial instanceof Mesh;
		}
		return false;
	}
	
	public boolean isLine() {
		Spatial spatial = getSpatial();
		if (spatial != null){
			return spatial instanceof Line;
		}
		return false;
	}
	
	public boolean isGroundOverlay() {
		Spatial spatial = getSpatial();
		if (spatial != null){
			return spatial.getClass().getSimpleName().contains("GroundOverlay");
		}
		return false;
	}
	
	public List<WeakSpatial> getChildren() {
		return Collections.EMPTY_LIST;
	}
	
	public boolean hasChildren() {
		return false;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof WeakSpatial){
			WeakSpatial ws = (WeakSpatial)obj;
			Spatial otherSpatial = ws.getSpatial();
			Spatial mySpatial = getSpatial();
			if (otherSpatial != null && mySpatial != null && otherSpatial.equals(mySpatial)){
				return true;
			}
		} else if (obj instanceof Spatial){
			Spatial otherSpatial = (Spatial)obj;
			Spatial mySpatial = getSpatial();
			if (otherSpatial != null && mySpatial != null) {
				return otherSpatial.equals(mySpatial);
			}
		}
		return false;
	}
	
	public boolean isCullAlways() {
		Spatial spatial = getSpatial();
		if (spatial != null){
			return spatial.getSceneHints().getCullHint() == CullHint.Always;
		}
		return true;
	}
	
	public static boolean isCullAlways(Spatial spatial){
		if (spatial != null){
			return spatial.getSceneHints().getCullHint() == CullHint.Always;
		}
		return true;
	}
	
	public void setCullHint(CullHint hint){
		if (hint == null){
			return;
		}
		Spatial spatial = getSpatial();
		if (spatial != null){
			spatial.getSceneHints().setCullHint(hint);
		}
	}
	
	public void setAllPickingHints(boolean enabled){
		Spatial spatial = getSpatial();
		if (spatial != null){
			spatial.getSceneHints().setAllPickingHints(enabled);
		}
	}

	@Override
	public int hashCode() {
		Spatial s = getSpatial();
		if (s != null){
			return s.hashCode();
		}
		return super.hashCode();
	}
	
	/**
	 * Get the icon image for the tree
	 * @return
	 */
	public Image getImage(){
		String icon = "layer_small";
	    if(isBMText()){
	    	icon = "label";
	    } else if(isMesh()) {
	    	icon = "layer_shape";
	    } else if(isLine()) {
	    	icon = "layer_shape_polyline";
	    }

		return Activator.getImageFromRegistry(icon);
	}
}
