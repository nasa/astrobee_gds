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

import gov.nasa.arc.viz.scenegraph.NamedFrame;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.scenegraph.visitor.Visitor;

/**
 * Create a list of all the named frames in a subgraph.
 * One the list exists, a specific frame can be queried 
 * by alias. 
 */
public class FindNamedFrameVisitor implements Visitor {
    protected Map<String,NamedFrame> m_frameHash = new HashMap<String,NamedFrame>();
    
    public FindNamedFrameVisitor() {
    	//
    }
    
    public void visit(final Spatial spatial) {
    	if(spatial instanceof NamedFrame) {
    		NamedFrame nf = (NamedFrame)spatial;
    		for(String str : nf.getAliases()) {
    			m_frameHash.put(str, nf);
    		}
    	}
    }
    
    public Set<String> getAllFrameNames() {
    	return m_frameHash.keySet();
    }
    
    public NamedFrame getNamedFrame(String name) {
    	return m_frameHash.get(name);
    }
    
    
}



