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
package gov.nasa.arc.irg.iss.ui.view.log;

import gov.nasa.arc.irg.iss.ui.IssButtonEventLoggingConfigurator;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartSashContainerElement;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class DetachedLogView extends IssLogView {
	private static final Logger buttonLogger = Logger.getLogger(IssButtonEventLoggingConfigurator.BUTTON_LOGGER_NAME);

	public static final String ID = "gov.nasa.arc.irg.iss.ui.view.log.DetachedLogView";
	
	protected String recordedTimestamp;

	private boolean once = false;
	
	@Override
	public void setFocus() {
		if (!once) {
			once = true;
			
			 EModelService s = (EModelService) getSite().getService(EModelService.class);
		      MPartSashContainerElement p = (MPart) getSite().getService(MPart.class);
		     
		      if (p.getCurSharedRef() != null){
		        p = p.getCurSharedRef();
				Point location = Display.getDefault().getCursorLocation();
		        s.detach(p, location.x -600, location.y -600, 600, 600);
		      }
		
//			IViewReference ref = getSite().getPage().findViewReference(ID, "1");
//			
//			((WorkbenchPage)getSite().getPage()).getActivePerspective().getPresentation().detachPart(ref);
//			getViewSite().getShell().setSize(600, 600);
//			Point location = Display.getDefault().getCursorLocation();
//			getViewSite().getShell().setLocation(location.x -600, location.y -600);
			
			final Shell shell = getViewSite().getShell();
		    shell.addListener(SWT.Close, new Listener() {
		        public void handleEvent(Event event) {
		            logEvent(String.valueOf(event.time));
		        }
		    });
		    
		    shell.pack();
			super.setFocus();
		}
	} 
	
	
    protected void logEvent(String timestamp) {
    	// check for double logging
    	if (!timestamp.equals(recordedTimestamp))  {
    		// capture the closed button event
    		buttonLogger.log(Level.INFO, DetachedLogView.class.getName() + " | Log View Closed.");
    		recordedTimestamp = timestamp;
    	}
    }
}
