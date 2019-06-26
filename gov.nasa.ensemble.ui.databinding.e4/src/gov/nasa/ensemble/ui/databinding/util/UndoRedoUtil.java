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
package gov.nasa.ensemble.ui.databinding.util;

import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.operations.RedoActionHandler;
import org.eclipse.ui.operations.UndoActionHandler;

public class UndoRedoUtil {

	/**
	 * set up action handlers that operate on the current context
	 */
	public static void createViewUndoRedo(IWorkbenchPartSite site) {
		UndoActionHandler undoAction = new UndoActionHandler(site, IOperationHistory.GLOBAL_UNDO_CONTEXT);
		RedoActionHandler redoAction = new RedoActionHandler(site, IOperationHistory.GLOBAL_UNDO_CONTEXT);
		
		if (site instanceof IViewSite){
			IViewSite viewSite = (IViewSite)site;
			IActionBars actionBars = viewSite.getActionBars();
			actionBars.setGlobalActionHandler(ActionFactory.UNDO.getId(),undoAction);
			actionBars.setGlobalActionHandler(ActionFactory.REDO.getId(),redoAction);
		}
	}
}
