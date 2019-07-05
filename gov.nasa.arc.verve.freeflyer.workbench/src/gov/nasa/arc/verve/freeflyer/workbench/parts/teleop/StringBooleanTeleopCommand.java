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

import gov.nasa.arc.verve.freeflyer.workbench.utils.GuiUtils;
import gov.nasa.arc.verve.freeflyer.workbench.widget.helpers.CommandButton;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * Creates a widget for a command that takes a String and a boolean as arguments.
 * The boolean does not depend on the string.
 * 
 * Example teleopCommandConfig in TeleopCommandsConfiguration.json:
 * {
	 "type" : "StringBooleanTeleopCommand",
	  "label": "Camera to Stream",
	  "secondColumnLabel": "Streaming",
	  "command": "setCameraStreaming",
	  "stringParamName": "cameraName",
	  "stringOptions": ["Dock", "Navigation", "Science"],
	  "boolParamName": "stream",
	  "buttonText": "Send",
	  "subsystem": "Camera"
	}
 * 
 * @author ddwheele
 *
 */
public class StringBooleanTeleopCommand extends AbstractTeleopCommandConfig {
	protected String secondColumnLabel;
	protected String stringParamName;
	protected String boolParamName;
	protected String[] stringOptions;
	protected String[] bools = {"True", "False"};
	protected Combo firstCombo;
	protected Combo secondCombo;
	
	public StringBooleanTeleopCommand() {
		// for JSON
	}
	
	@Override
	public void createWidget(Composite parent) {
		createNameLabel(parent);
		createSecondColumnLabel(parent);

		new Label(parent, SWT.NONE);

		createFirstOptionsCombo(parent);
		createSecondOptionsCombo(parent);

		createSendButton(parent);
	}
	
	protected void createFirstOptionsCombo(Composite parent) {
		firstCombo = new Combo(parent, SWT.READ_ONLY);
		firstCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		firstCombo.setItems(titleCaseStringArray(stringOptions));
		firstCombo.select(0);
	}
	
	protected String[] titleCaseStringArray(String[] in) {
		String[] ret = new String[in.length];
		for(int i=0; i<in.length; i++) {
			ret[i] = GuiUtils.toTitleCase(in[i]);
		}
		return ret;
	}

	protected void createSecondOptionsCombo(Composite parent) {
		secondCombo = new Combo(parent, SWT.READ_ONLY);
		secondCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		secondCombo.setItems(bools);
		secondCombo.select(0);
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
				String camera = firstCombo.getText();
				String a= secondCombo.getText();
				boolean stream = secondCombo.getText().equals("True");
				commandPublisher.sendGenericStringBooleanCommand(command, subsystem, 
						stringParamName, camera, boolParamName, stream);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)  {/**/}
		});
	}

	protected void createSecondColumnLabel(Composite parent) {
		Label l = new Label(parent, SWT.NONE);
		l.setText(secondColumnLabel);
	}

	public String getSecondColumnLabel() {
		return secondColumnLabel;
	}

	public void setSecondColumnLabel(String secondColumnLabel) {
		this.secondColumnLabel = secondColumnLabel;
	}

	public String getStringParamName() {
		return stringParamName;
	}

	public void setStringParamName(String stringParamName) {
		this.stringParamName = stringParamName;
	}

	public String getBoolParamName() {
		return boolParamName;
	}

	public void setBoolParamName(String boolParamName) {
		this.boolParamName = boolParamName;
	}

	public String[] getStringOptions() {
		return stringOptions;
	}

	public void setStringOptions(String[] stringOptions) {
		this.stringOptions = stringOptions;
	}
}
