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
package gov.nasa.arc.verve.freeflyer.workbench.parts.teleop;

import gov.nasa.arc.irg.plan.freeflyer.config.OperatingLimitsConfigList;
import gov.nasa.arc.irg.plan.freeflyer.config.OperatingLimitsConfigList.OperatingLimitsConfig;
import gov.nasa.arc.irg.plan.ui.io.OperatingLimitsConfigListLoader;
import gov.nasa.arc.verve.freeflyer.workbench.widget.helpers.CommandButton;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

/**
 * Specify set of operating limits (from list in `OperatingLimitsConfigurations.json`)
 * and send as parameters to `SETTINGS_METHOD_SET_OPERATING_LIMITS` command
 *  
 *  Example teleopCommandConfig in TeleopCommandsConfiguration.json: 
 *  {
	  "type" : "OperatingLimitsTeleopCommand",
	  "label": "Operating Limits",
	  "buttonText": "Set"
	}
 *
 * @author ddwheele
 *
 */
public class OperatingLimitsTeleopCommand extends AbstractTeleopCommandConfig {
	protected Combo firstCombo;
	private OperatingLimitsConfigList operatingLimitsConfigList;

	@Override
	public void createWidget(Composite parent) {
		super.createWidget(parent);
		createNameLabel(parent);
		createOptionsCombo(parent);
		createSendButton(parent);
	}

	protected void createOptionsCombo(Composite parent) {
		firstCombo = new Combo(parent, SWT.READ_ONLY);
		firstCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		try {
			operatingLimitsConfigList = OperatingLimitsConfigListLoader.getStandardConfig();
		} catch (Exception e) {
			showErrorDialog(shell, "Error Reading Operating Limits Config File", e.getMessage());
		}

		firstCombo.setItems(operatingLimitsConfigList.getArrayOfNames());
		firstCombo.select(0);
	}

	@Override
	protected void createSendButton(Composite parent) {
		button = new CommandButton(parent, SWT.None);
		button.setText(buttonText);

		button.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		button.setButtonLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		button.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String opLimitsName = firstCombo.getText();

				OperatingLimitsConfig opLimitConfig = operatingLimitsConfigList.getConfigNamed(opLimitsName);

				commandPublisher.sendSetOperatingLimitsCommand(
						opLimitConfig.getProfileName(),
						opLimitConfig.getFlightMode(),
						opLimitConfig.getTargetLinearVelocity(), 
						opLimitConfig.getTargetLinearAccel(), 
						opLimitConfig.getTargetAngularVelocity(), 
						opLimitConfig.getTargetAngularAccel(), 
						opLimitConfig.getCollisionDistance());
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)  {/**/}
		});
	}

}
