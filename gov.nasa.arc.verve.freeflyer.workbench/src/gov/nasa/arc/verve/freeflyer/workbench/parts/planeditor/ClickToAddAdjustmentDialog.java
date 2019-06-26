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
package gov.nasa.arc.verve.freeflyer.workbench.parts.planeditor;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class ClickToAddAdjustmentDialog extends AbstractAdjustmentDialog {

	@Inject
	public ClickToAddAdjustmentDialog(@Named(IServiceConstants.ACTIVE_SHELL) Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected void createButtons(Composite parent) {
		Button moveButton = new Button(parent, SWT.None);
		moveButton.setText("Move Plane");
		moveButton.setLayoutData(new GridData(GridData.BEGINNING));
		moveButton.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
				// no-op
			}

			public void widgetSelected(SelectionEvent e) {
				handleEnter();
			}
		});

		Button cancelButton = new Button(parent, SWT.None);
		cancelButton.setText("Cancel Click to Add");
		GridData spacerData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		spacerData.horizontalSpan = 4;
		cancelButton.setLayoutData(spacerData);
		cancelButton.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
				// no-op
			}

			public void widgetSelected(SelectionEvent e) {
				delegateCommandStack.exitClickToAddMode();
			}
		});
	}

}
