package gov.nasa.arc.verve.ui.e4.panel.tree;
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

import gov.nasa.arc.verve.ui.e4.ImageRegistryKeeper;
import gov.nasa.arc.verve.ui.e4.node.WeakSpatial;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.ardor3d.scenegraph.Spatial;

public class SceneGraphTreeLabelProvider
			extends LabelProvider {

	public SceneGraphTreeLabelProvider() {
		//
	}

	@Override
	public Image getImage(Object element) {
	    String icon = "UNKNOWN16";
	    if (element instanceof WeakSpatial){
	    	WeakSpatial ws = (WeakSpatial)element;
	    	return ws.getImage();
	    }
         
	    return ImageRegistryKeeper.getInstance().getImageFromRegistry(icon);
	}

	@Override
	public String getText(Object element) {
	    String retVal = "";
	    if(element instanceof WeakSpatial) {
	    	WeakSpatial weakSpatial = (WeakSpatial)element;
	        retVal = weakSpatial.getName();
	        if(retVal == null) {
	            retVal = "["+element.getClass().getSimpleName()+"]";
	        }
	    }else if (element instanceof Spatial) {
	    	Spatial spatial = (Spatial)element;
	    	retVal = spatial.getName();
	    	if (retVal == null){
	    		retVal = "["+element.getClass().getSimpleName()+"]";
	    	}
	    }
		return retVal;
	}
}
