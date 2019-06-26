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
package gov.nasa.arc.viz.scenegraph;

import java.util.HashSet;
import java.util.Set;

import com.ardor3d.scenegraph.Node;

/**
 * 
 * @author mallan
 *
 */
public class NamedFrame extends Node {
	Set<String> m_aliases = new HashSet<String>();
	
	public NamedFrame(String name) {
		super(name);
		m_aliases.add(name);
	}
	
	public boolean addAlias(String name) {
		return m_aliases.add(name);
	}
	
	public boolean removeAlias(String name) {
		return m_aliases.remove(name);
	}
	
	public Set<String> getAliases() {
		return m_aliases;
	}
	
	/**
	 * check aliases for a match
	 * @param name
	 * @return
	 */
	public boolean nameMatches(String name) {
		return m_aliases.contains(name);
	}
	
	
}
