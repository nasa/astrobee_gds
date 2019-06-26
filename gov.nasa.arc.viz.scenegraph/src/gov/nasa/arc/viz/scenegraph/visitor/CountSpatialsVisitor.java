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

import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.scenegraph.visitor.Visitor;

/**
 * This visitor simply walks a subgraph and counts the
 * number of spatials. Optionally, it can count instances 
 * of a particular class derived from Spatial
 */
@SuppressWarnings("unchecked")
public class CountSpatialsVisitor implements Visitor {
	protected int m_count = 0;
	protected Class m_countClass;

	/**
	 * count the number of Spatial objects in a subgraph
	 */
	public CountSpatialsVisitor() {
		this(Spatial.class);
	}	
	/**
	 * @param countClass count the objects which are instances of countClass
	 */
	public CountSpatialsVisitor(Class countClass) {
		m_countClass = countClass;
	}

	public void setClass(Class clazz) {
		m_countClass = clazz;
	}

	public void visit(final Spatial spatial) {
		if(m_countClass.isInstance(spatial)) {
			m_count++;
		}
	}
	
	public void reset() {
		m_count = 0;
	}
	
	public int getCount() {
		return m_count;
	}
}
