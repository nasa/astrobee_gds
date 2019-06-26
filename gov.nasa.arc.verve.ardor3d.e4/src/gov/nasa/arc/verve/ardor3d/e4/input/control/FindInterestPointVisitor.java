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
package gov.nasa.arc.verve.ardor3d.e4.input.control;

import gov.nasa.arc.verve.common.interest.InterestPointProvider;

import java.util.LinkedList;
import java.util.List;

import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.scenegraph.visitor.Visitor;

/**
 * Search for InterestPointProviders and add them to a list. 
 * If the InterestPointName is null, it will *not* be included
 * in the list. 
 */
public class FindInterestPointVisitor implements Visitor {

	LinkedList<InterestPointProvider> m_list = new LinkedList<InterestPointProvider>();

	public List<InterestPointProvider> execute(Node root) {
		m_list.clear();
		root.acceptVisitor(this, false);
		return m_list;
	}

	public List<InterestPointProvider> get() {
		return m_list;
	}

    @Override
	public void visit(final Spatial spatial) {
		if(InterestPointProvider.class.isAssignableFrom(spatial.getClass()) ) {
			InterestPointProvider ipp = (InterestPointProvider)spatial;
			if(ipp.getInterestPointName() != null) {
				m_list.add((InterestPointProvider)spatial);
			}
		}
	}
}
