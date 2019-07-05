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
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

/**
 * Widget that lets you pick from strings in a dropdown, then sends an int of the (first) numeral in the string you picked
 * Designed for the Berth parameter in the Dock command
 * 
 * Class should extend StringListTeleopCommand, but JSON won't deserialize a subtype of a subtype.  Lame.
 * 
 * Example teleopCommandConfig in TeleopCommandsConfiguration.json:
 * {
	  "type" : "StringListIntTeleopCommand",
	  "label": "Manual Dock",
	  "command": "dock",
	  "paramName": "berthMethod",
	  "buttonText": "Dock",
	  "paramOptions": ["Berth 1","Berth 2"],
	  "subsystem": "Mobility"
	}
 * 
 * @author ddwheele
 *
 */
public class StringListIntTeleopCommand extends AbstractTeleopCommandConfig {
	protected String paramName;
	protected String[] paramOptions;
	protected Combo paramOptionsCombo;
	
	public StringListIntTeleopCommand() {
		// for JSON
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
				int param = findTheNumber(paramOptionsCombo.getText());
				commandPublisher.sendGenericOneIntCommand(command, subsystem, paramName, param);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)  {/**/}
		});
	}
	
	protected int findTheNumber(String string) {
		String[] split = string.split(" ");
		
		for(int i=0; i<split.length; i++) {
			try {
				int parsed = Integer.parseInt(split[i]);
				return parsed;
			} catch (NumberFormatException e) {
				// wasn't that
			}
		}
		System.out.println("No number found in StringListIntTeleopCommand option");
		return 0;
	}
	
	public void createWidget(Composite parent) {
		createNameLabel(parent);
		
		createParamOptionsCombo(parent);
		
		createSendButton(parent);
	}
	
	protected void createParamOptionsCombo(Composite parent) {
		paramOptionsCombo = new Combo(parent, SWT.READ_ONLY);
		paramOptionsCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		paramOptionsCombo.setItems(paramOptions);
		paramOptionsCombo.select(0);
	}

	public String getParamName() {
		return paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	public String[] getParamOptions() {
		return paramOptions;
	}

	public void setParamOptions(String[] paramOptions) {
		this.paramOptions = paramOptions;
	}
}
