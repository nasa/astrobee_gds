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
package gov.nasa.arc.ff.ocu.handlers;

import gov.nasa.arc.irg.plan.ui.io.ConfigFileWrangler;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class LegalNoticesHandler {
	private Browser browser;
	
	
	@Execute
	public void execute(final Shell shell) {
		final Display display = shell.getDisplay();
		final Shell helpShell = new Shell(display, SWT.SHELL_TRIM);
		helpShell.setLayout(new GridLayout(2, false));

	    browser = new Browser(helpShell, SWT.NONE);
	    browser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
	   	browser.setUrl(ConfigFileWrangler.getInstance().getLegalDocUrlString());
		browser.refresh();
	   
	    helpShell.setText("Legal Notices");
	    helpShell.open();
	}
}
