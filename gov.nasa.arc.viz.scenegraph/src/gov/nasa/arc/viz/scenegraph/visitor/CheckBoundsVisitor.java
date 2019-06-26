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
package gov.nasa.arc.viz.scenegraph.visitor;

import org.apache.log4j.Logger;

import com.ardor3d.bounding.BoundingVolume;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.scenegraph.visitor.Visitor;

/**
 * Print out spatials that have NaN or null bounds
 */
@SuppressWarnings("unchecked")
public class CheckBoundsVisitor implements Visitor {
	private static Logger logger = Logger.getLogger(CheckBoundsVisitor.class);
	/**
	 * count the number of Spatial objects in a subgraph
	 */
	public CheckBoundsVisitor() {
		// foo
	}	

	public void visit(final Spatial spatial) {
        final String name = spatial.getName();
        final String type = spatial.getClass().getSimpleName();
		BoundingVolume bound = spatial.getWorldBound();
		if(bound == null) {
			logger.warn("null bound for "+name+" ("+type+")");
		}
		else {
			double vol = spatial.getWorldBound().getVolume();
            //logger.warn(vol + " - " +name);
			if(vol != vol) {
				logger.warn("NaN Bounds Found in "+name+" ("+type+")");
			}
		}
	}
	
}
