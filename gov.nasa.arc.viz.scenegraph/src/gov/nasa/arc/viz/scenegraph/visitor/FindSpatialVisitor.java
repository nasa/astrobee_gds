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

import java.util.ArrayList;
import java.util.List;

import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.scenegraph.visitor.Visitor;

/**
 * Create a list of all the named frames in a subgraph.
 * One the list exists, a specific frame can be queried 
 * by alias. 
 */
public class FindSpatialVisitor implements Visitor {
    protected List<Spatial> m_foundList = new ArrayList<Spatial>();
    protected String m_name = "";

    public FindSpatialVisitor() {
        //
    }

    public FindSpatialVisitor(String name) {
        m_name = name;
    }

    public void reset() {
        m_foundList.clear();
    }

    public String getName() {
        return m_name;
    }
    public FindSpatialVisitor setName(String name) {
        m_name = name;
        reset();
        return this;
    }

    public void visit(final Spatial spatial) {
        if(spatial.getName() != null && spatial.getName().equals(m_name)) {
            m_foundList.add(spatial);
        }
    }

    public List<Spatial> getFoundSpatials() {
        return m_foundList;
    }
    
    public Spatial getFirst() {
        Spatial retVal = null;
        if(m_foundList.size() > 0)  {
            retVal = m_foundList.get(0);
        }
        return retVal;
    }

    public int numFound() {
        return m_foundList.size();
    }
}



