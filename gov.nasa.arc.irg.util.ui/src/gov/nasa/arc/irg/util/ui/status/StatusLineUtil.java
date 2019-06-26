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
package gov.nasa.arc.irg.util.ui.status;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

public class StatusLineUtil {
	//private static final Logger logger = Logger.getLogger(StatusLineUtil.class);
	
	public static StatusLineUtil INSTANCE = new StatusLineUtil();
	protected String m_currentStatus = "";
	protected Set<IStatusLineChangedListener> m_changeListeners = new HashSet<IStatusLineChangedListener>();
	
	protected StatusLineUtil() {
		addListener(new BottomStatusLineUpdater()); 
	}
	
	public static void setStatus(String status){
		INSTANCE.setCurrentStatus(status);
	}
	
	public String getCurrentStatus() {
		return m_currentStatus;
	}
	
	public synchronized void setCurrentStatus(String currentStatus) {
		m_currentStatus = currentStatus;
		notifyListeners();
	}
	
	protected void notifyListeners() {
		for (IStatusLineChangedListener l : m_changeListeners){
			l.statusChanged(getCurrentStatus());
		}
	}
	
	public void addListener(IStatusLineChangedListener listener){
		if (listener != null){
			m_changeListeners.add(listener);
		}
	}
	
	public void removeListener(IStatusLineChangedListener listener){
		m_changeListeners.remove(listener);
	}
	
	public class BottomStatusLineUpdater implements IStatusLineChangedListener {

		@Override
		public void statusChanged(String status) {
			try {
				IWorkbenchPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart();
				if (part == null){
					return;
				}
				if (part instanceof IViewPart){
					IViewPart ivp = (IViewPart)part;
					ivp.getViewSite().getActionBars().getStatusLineManager().setMessage(status);
				} else if (part instanceof IEditorPart){
					IEditorPart iep = (IEditorPart)part;
					iep.getEditorSite().getActionBars().getStatusLineManager().setMessage(status);
				}
			} catch (Exception e){
				// we don't really care...
			}
		}
		
	}
}
