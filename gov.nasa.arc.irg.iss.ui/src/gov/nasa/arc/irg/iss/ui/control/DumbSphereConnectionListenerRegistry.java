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
package gov.nasa.arc.irg.iss.ui.control;

import java.util.HashSet;

public class DumbSphereConnectionListenerRegistry {

	public static final DumbSphereConnectionListenerRegistry INSTANCE = new DumbSphereConnectionListenerRegistry();
	
	protected HashSet<IDumbSphereConnectionListener> m_listeners = new HashSet<IDumbSphereConnectionListener>();
	
	public HashSet<IDumbSphereConnectionListener> getCommandListeners() {
		return m_listeners;
	}
	
	public void add(IDumbSphereConnectionListener listener) {
		m_listeners.add(listener);
	}
	
	public void remove(IDumbSphereConnectionListener listener) {
		m_listeners.remove(listener);
	}
}
