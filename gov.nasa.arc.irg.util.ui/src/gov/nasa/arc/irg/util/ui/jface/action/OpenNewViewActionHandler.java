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
package gov.nasa.arc.irg.util.ui.jface.action;

import gov.nasa.arc.irg.util.ui.ViewID;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IParameter;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

public class OpenNewViewActionHandler extends AbstractHandler {

    Logger logger = Logger.getLogger(OpenNewViewActionHandler.class);
    
    @SuppressWarnings("unused")
    public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			IParameter[] parameters= event.getCommand().getParameters();
			String viewId = null;
			boolean openNewView = true;
			if (parameters != null){
				for (IParameter param : parameters){
					if (param.getName().equals("viewID")){
						viewId = param.getId();
					}
					if (param.getName().equals("unique")){
						openNewView = false;
					}
				}
			}
			if (viewId != null){
				String secondaryId = null;
				if (openNewView){
					secondaryId = ViewID.getUniqueSecondaryID(viewId);
				}
				IWorkbenchPage activePage = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage();
				IViewPart newView = activePage.showView(viewId, secondaryId, IWorkbenchPage.VIEW_ACTIVATE);
				activePage.bringToTop(newView); // views sometimes show up on bottom of view stack w/o this
			}
		} 
		catch (PartInitException e) {
			logger.error(e);
		} 
		catch (NotDefinedException e) {
			logger.error(e);
		}
		return null;
	}
}
