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

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

/** 
 * Widget for a command that is just sending true or false.
 * Command must be specified in TeleopCommandConfigurations.json.
 * Sends true on click, false on unclick.
 * 
 * Example teleopCommandConfig in TeleopCommandsConfiguration.json:
 * {
	  "type" : "ToggleBooleanTeleopCommand",
	  "label": "Gripper",
	  "command": "gripperControl",
	  "paramName": "open",
	  "buttonText": "Open",
	  "buttonPushedText": "Close",
	  "subsystem": "Arm"
	}
 * 
 * @author ddwheele
 *
 */
public class ToggleBooleanTeleopCommand extends AbstractTeleopCommandConfig {

	protected String buttonPushedText;
	protected String paramName;
	
	public ToggleBooleanTeleopCommand() {
		// for JSON
	}

	@Override
	public void createWidget(Composite parent) {
		createNameLabel(parent);

		createSendButton(parent);
	}

	public String getButtonPushedText() {
		return buttonPushedText;
	}

	public void setButtonPushedText(String buttonPushedText) {
		this.buttonPushedText = buttonPushedText;
	}

	@Override
	protected void createSendButton(Composite parent) {
		button = new CommandButton(parent, SWT.TOGGLE);
		button.setText(buttonText);
		
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).span(2,1).applyTo(button);
		button.setButtonLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		button.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(button.getSelection()) {
					button.setText(buttonPushedText);
					commandPublisher.sendGenericBooleanCommand(command, subsystem, paramName, true);
					
				} else {
					button.setText(buttonText);
					commandPublisher.sendGenericBooleanCommand(command, subsystem, paramName, false);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)  {/**/}
		});
	}

	public String getParamName() {
		return paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}
}
