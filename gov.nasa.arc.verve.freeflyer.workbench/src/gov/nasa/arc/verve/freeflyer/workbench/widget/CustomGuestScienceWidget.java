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
import gov.nasa.arc.irg.plan.freeflyer.config.GuestScienceApkGds.GuestScienceCommandGds;
import gov.nasa.arc.irg.plan.ui.io.GuestScienceConfigListLoader;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class CustomGuestScienceWidget extends AbstractGuestScienceWidget {
	protected Combo commandsCombo;
	protected Label powerLabel;

	public CustomGuestScienceWidget(Composite parent, int style) {
		super(parent, style);
	}

	@Override
	void setupCustomControls() {
		super.setupCustomControls();
		String initialPowerLabel = "0.0\t";
		guestScienceCombo.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				GuestScienceApkGds selectedConfig;
				try {
					selectedConfig = GuestScienceConfigListLoader.getGuestScienceConfigFromName(guestScienceCombo.getText());
					commandsCombo.setItems(GuestScienceConfigListLoader.getArrayOfCommandNames(selectedConfig));
				} catch (Exception ex) {
					showErrorDialog(shell, "Error Reading Guest Science Config", ex.getMessage());
				}
			}
		});

		Label l = new Label(this, SWT.NONE);
		l.setText("Command");
		addChildLabel(l);

		commandsCombo = new Combo(this, SWT.READ_ONLY);
		GridData gdata = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gdata.horizontalSpan = 2;
		commandsCombo.setLayoutData(gdata);
		commandsCombo.setText("No Commands");
		addChildControl(commandsCombo);

		commandsCombo.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				GuestScienceApkGds selectedConfig;
				try {
					selectedConfig = GuestScienceConfigListLoader.getGuestScienceConfigFromName(guestScienceCombo.getText());
					GuestScienceCommandGds selectedCommand;
					if(selectedConfig != null) {
						selectedCommand = selectedConfig.getGuestScienceCommandFromName(commandsCombo.getText());
						if(selectedCommand != null) {
							powerLabel.setText(Float.toString(selectedCommand.getPower()));
						}
					}
					else {
						powerLabel.setText(initialPowerLabel);
						return;
					}
				} catch (Exception ex) {
					showErrorDialog(shell, "Error Reading Guest Science Config", ex.getMessage());
				}

			}
		});

		Label pl = new Label(this, SWT.NONE);
		pl.setText("Power");
		addChildLabel(pl);

		powerLabel = new Label(this, SWT.NONE);
		powerLabel.setText(initialPowerLabel);
		addChildLabel(powerLabel);

		Label powerUnitsLabel = new Label(this, SWT.NONE);
		powerUnitsLabel.setText("W");
		addChildLabel(powerUnitsLabel);
	}

	@Override
	public boolean bindUI(Realm realm) {
		boolean result = super.bindUI(realm);
		result &= bind("commandIndex", commandsCombo);

		return result;
	}

}
