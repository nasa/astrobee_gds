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
package gov.nasa.arc.irg.georef.ui.dialogs;


import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * Launch the Lat/Long UTM conversion dialog
 * @author tecohen
 *
 */
public class LaunchLatLongUtmDialogAction implements
		IWorkbenchWindowActionDelegate {

	public void dispose() {
	    //
	}

	public void init(IWorkbenchWindow window) {
	    //
	}

	public void run(IAction action) {
		Shell shell = Display.getCurrent().getActiveShell();
		LatLongUtmDialog dlg = new LatLongUtmDialog(shell);
		dlg.setBlockOnOpen(true);
		dlg.open();
	}

	public void selectionChanged(IAction action, ISelection selection) {
	    //
	}

}
