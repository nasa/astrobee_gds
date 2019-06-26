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

import gov.nasa.arc.irg.plan.freeflyer.config.GuestScienceApkGds;
import gov.nasa.arc.irg.plan.ui.io.GuestScienceConfigListLoader;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class StartGuestScienceWidget extends AbstractGuestScienceWidget {
	protected Label powerLabel;

	public StartGuestScienceWidget(Composite parent, int style) {
		super(parent, style);
	}

	@Override
	void setupCustomControls() {
		super.setupCustomControls();
		guestScienceCombo.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				try {
					GuestScienceApkGds selectedConfig;
					selectedConfig = GuestScienceConfigListLoader.getGuestScienceConfigFromName(guestScienceCombo.getText());
					if(selectedConfig == null) {
						return;
					}
					powerLabel.setText(Float.toString(selectedConfig.getPower()));
				} catch (Exception ex) {
					showErrorDialog(shell, "Error Reading Guest Science Config File", ex.getMessage());
				}
			}
		});

		Label l = new Label(this, SWT.NONE);
		l.setText("Power");
		addChildLabel(l);

		powerLabel = new Label(this, SWT.NONE);
		powerLabel.setText("0.0\t");
		addChildLabel(powerLabel);

		Label powerUnitsLabel = new Label(this, SWT.NONE);
		powerUnitsLabel.setText("W");
		addChildLabel(powerUnitsLabel);
	}
}
