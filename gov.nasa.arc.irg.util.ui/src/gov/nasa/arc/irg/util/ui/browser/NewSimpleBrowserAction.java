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
package gov.nasa.arc.irg.util.ui.browser;


import gov.nasa.arc.irg.util.ui.ViewID;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PartInitException;

public class NewSimpleBrowserAction extends Action implements IWorkbenchWindowActionDelegate 
{
	private IWorkbenchWindow window = null;

	public void dispose() {
		this.window = null;
	}

	public void init(IWorkbenchWindow w) {
		window = w;
	}

	public void run(IAction action) {
		try {
			window.getActivePage().showView(SimpleBrowser.ID, ViewID.getUniqueSecondaryID(SimpleBrowser.ID), IWorkbenchPage.VIEW_ACTIVATE);
		} 
		catch (PartInitException e) {
			e.printStackTrace();
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// do nothing
	}

}
