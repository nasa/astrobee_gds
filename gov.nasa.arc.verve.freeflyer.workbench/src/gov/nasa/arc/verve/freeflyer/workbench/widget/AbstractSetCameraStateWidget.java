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
package gov.nasa.arc.verve.freeflyer.workbench.widget;

import gov.nasa.arc.irg.plan.util.PlanConstants;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public abstract class AbstractSetCameraStateWidget extends AbstractFreeFlyerWidget {
	protected Combo cameraCombo;
	protected Button startCheckbox;
	
	public AbstractSetCameraStateWidget(Composite parent, int style) {
		super(parent, style);
	}

	protected void setupCameraComboAndLabel(Composite parent) {
		Label l = new Label(this, SWT.NONE);
		l.setText("Camera");
		addChildLabel(l);
		
		cameraCombo = new Combo(this, SWT.READ_ONLY);
		GridData gdata = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gdata.horizontalSpan = 2;
		cameraCombo.setLayoutData(gdata);
		
		cameraCombo.setItems(PlanConstants.CAMERA_NAME_STRINGS);
		addChildControl(cameraCombo);
	}
}
