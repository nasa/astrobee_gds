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

import gov.nasa.arc.verve.freeflyer.workbench.widget.helpers.CommandButton;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

/**
 * Widget to send a command with no parameters.
 * Command must be specified in TeleopCommandConfigurations.json.
 * 
 * Example teleopCommandConfig in TeleopCommandsConfiguration.json:
 * {
	  "type" : "NoParamsTeleopCommand",
	  "label": "Idle Propulsion",
	  "buttonText": "Idle",
	  "command": "idlePropulsion",
	  "subsystem": "Mobility"
	}
 * 
 * @author ddwheele
 *
 */
public class NoParamsTeleopCommand extends AbstractTeleopCommandConfig {
	
	public NoParamsTeleopCommand() {
		// for JSON
	}
	
	public NoParamsTeleopCommand(String label, String buttonText, String command) {
		this.label = label;
		this.buttonText = buttonText;
		this.command = command;
	}

	@Override
	public void createWidget(Composite parent) {
		createNameLabel(parent);
		
		createSendButton(parent);
	}
	
	protected void createSendButton(Composite parent) {
		button = new CommandButton(parent, SWT.None);
		button.setText(buttonText);
		
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
		gd.horizontalSpan = 2;
		button.setLayoutData(gd);
		button.setButtonLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		button.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				commandPublisher.sendGenericNoParamsCommand(command, subsystem);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)  {/**/}
		});
	}
}
