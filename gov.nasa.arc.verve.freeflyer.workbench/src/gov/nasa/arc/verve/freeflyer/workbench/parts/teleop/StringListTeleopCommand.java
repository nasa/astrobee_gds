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
 * Widget to send a command that takes a String as a parameter.
 * Allowable strings should should be specified in TeleopCommandConfigurations.json
 * 
 * @author ddwheele
 *
 */
public class StringListTeleopCommand extends AbstractTeleopCommandConfig {

	protected String paramName;
	protected String[] paramOptions;
	protected Combo paramOptionsCombo;
	
	public StringListTeleopCommand() {
		// for JSON
	}
	
	@Override
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

	@Override
	protected void createSendButton(Composite parent) {
		button = new CommandButton(parent, SWT.None);
		button.setText(buttonText);
		
		button.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		button.setButtonLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		button.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String param = paramOptionsCombo.getText();
				commandPublisher.sendGenericOneStringCommand(command, subsystem, paramName, param);
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

	public String[] getParamOptions() {
		return paramOptions;
	}

	public void setParamOptions(String[] paramOptions) {
		this.paramOptions = paramOptions;
	}
}
