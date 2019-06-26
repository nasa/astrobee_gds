/******************************************************************************
 * Copyright © 2019, United States Government, as represented by the 
 * Administrator of the National Aeronautics and Space Administration. All 
 * rights reserved.
 * 
 * The Astrobee Control Station platform is licensed under the Apache License, 
 * Version 2.0 (the "License"); you may not use this file except in compliance 
 * with the License. You may obtain a copy of the License at
 * 
 * 	http://www.apache.org/licenses/LICENSE-2.0. 
 *   
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT 
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the 
 * License for the specific language governing permissions and limitations 
 * under the License.
 *****************************************************************************/
package gov.nasa.arc.irg.freeflyer.rapid.connection;


import gov.nasa.arc.irg.util.connection.IConnectionListener;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DDSConnectionListenerRegistry {
	public static final DDSConnectionListenerRegistry INSTANCE = new DDSConnectionListenerRegistry();
	
	public enum Endpoint {PUBLISHER, SUBSCRIBER}
	
	protected Set<IConnectionListener> m_listeners = Collections.newSetFromMap( new ConcurrentHashMap<IConnectionListener, Boolean>());
	protected boolean m_pubMatch = false;
	protected boolean m_subMatch = false;
	protected boolean m_isConnected = false;
	
	public void statusChanged(Endpoint endpoint, boolean connected) {
		switch(endpoint) {
		case PUBLISHER:
			m_pubMatch = connected;
		break;
		case SUBSCRIBER:
			m_subMatch = connected;
			break;
		}
		
		m_isConnected = (m_pubMatch & m_subMatch);
		notifyListeners();
	}
	
	
	protected void notifyListeners() {
		if (m_isConnected){
			for(IConnectionListener isl : m_listeners) {
				isl.onConnect();
			}
		} else {
			for(IConnectionListener isl : m_listeners) {
				isl.onDisconnect();
			}
		}
	}
	
	
	public void add(IConnectionListener listener){
		if(!m_listeners.contains(listener)) {
			m_listeners.add(listener);
			if(m_isConnected) {
				listener.onConnect();
			} else {
				listener.onDisconnect();
			}
		}
	}
	
	public void remove(IConnectionListener listener){
		m_listeners.remove(listener);
	}
}
