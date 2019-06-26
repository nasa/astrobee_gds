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
package gov.nasa.arc.ff.ocu.part;

import gov.nasa.arc.irg.plan.ui.io.EnlargeableButton;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class MiniButtonBar {
	protected IWorkbench workbench;
	protected Composite  composite;
	protected EnlargeableButton     restoreButton;
	protected EnlargeableButton     helpButton;
	protected EnlargeableButton     exitButton;
	
	@Inject @Singleton
	public MiniButtonBar(Composite parent) {
		composite = new Composite(parent, SWT.LEFT);
		
		init();
	}
	
	protected void init() {
		setGridLayout(3);
		
		makeDefaultHelpAndExitButtons();
	}
	
	protected void setGridLayout(int columns) {
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL
				| GridData.FILL_VERTICAL));
		GridLayout gridLayout;
		gridLayout = new GridLayout(columns, true);
		composite.setLayout(gridLayout);
	}
	
	protected void makeDefaultHelpAndExitButtons() {
		restoreButton = new EnlargeableButton(composite, SWT.NONE);
		restoreButton.setText("Restore\nDefault Layout");
		restoreButton.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		helpButton = new EnlargeableButton(composite, SWT.NONE);
		helpButton.setText("Help");
		helpButton.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		exitButton = new EnlargeableButton(composite, SWT.NONE);
		exitButton.setText("Exit");
		exitButton.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		exitButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				workbench.close();
			}
		});
	}
	
	@PostConstruct
	public void postConstruct(IWorkbench iw) {
		workbench = iw;
	}

}
