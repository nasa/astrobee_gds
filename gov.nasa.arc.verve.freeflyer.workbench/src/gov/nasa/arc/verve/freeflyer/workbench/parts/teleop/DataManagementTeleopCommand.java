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
import org.eclipse.swt.widgets.Label;

/**
 * Creates a widget for commands that take the same string as a parameter.
 * 
 * Strings in the first combo are the parameter.
 * Strings in the second combo are the command name.
 * Any command can take any parameter.
 *
 * Example teleopCommandConfig in TeleopCommandsConfiguration.json: 
 * {
	  "type" : "DataManagementTeleopCommand",
	  "label": "Data Type",
	  "secondColumnLabel": "Action",
	  "buttonText": "Send",
	  "paramName": "dataMethod",
	  "names":["Immediate", "Delayed"],
	  "actionNames":["Download", "Stop Download", "Clear"],
	  "actionCommands":["downloadData", "stopDownload", "clearData"],
	  "subsystem": "Data"
    }
 * @author ddwheele
 *
 */
public class DataManagementTeleopCommand extends AbstractTeleopCommandConfig {
	protected String secondColumnLabel;
	protected String paramName;
	protected String[] names;
	protected String[] actionNames;
	protected String[] actionCommands;
	protected Combo firstCombo;
	protected Combo secondCombo;
	
	public DataManagementTeleopCommand() {
		// for JSON
	}
	
	@Override
	public void createWidget(Composite parent) {
		super.createWidget(parent);
		createNameLabel(parent);
		createSecondColumnLabel(parent);

		new Label(parent, SWT.NONE);

		createFirstOptionsCombo(parent);
		createSecondOptionsCombo(parent);

		createSendButton(parent);
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
				String dataType = firstCombo.getText();
				String level = secondCombo.getText();

				String commandName = translateActionNameToCommand(level);
				commandPublisher.sendGenericOneStringCommand(commandName, subsystem, paramName, dataType);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)  {/**/}
		});
	}
	
	protected String translateActionNameToCommand(String level) {
		if(level != null) {
			for(int i=0; i<actionNames.length; i++) {
				if(level.equals(actionNames[i])) {
					return actionCommands[i];
				}
			}
		}
		return "mismatched command";
	}
	
	protected void createFirstOptionsCombo(Composite parent) {
		firstCombo = new Combo(parent, SWT.READ_ONLY);
		firstCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		firstCombo.setItems(names);
		firstCombo.select(0);
	}

	protected void createSecondOptionsCombo(Composite parent) {
		secondCombo = new Combo(parent, SWT.READ_ONLY);
		secondCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		secondCombo.setItems(actionNames);
		secondCombo.select(0);
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

	public String[] getNames() {
		return names;
	}

	public void setNames(String[] names) {
		this.names = names;
	}

	public String[] getActionNames() {
		return actionNames;
	}

	public void setActionNames(String[] actionNames) {
		this.actionNames = actionNames;
	}

	public String[] getActionCommands() {
		return actionCommands;
	}

	public void setActionCommands(String[] actionCommands) {
		this.actionCommands = actionCommands;
	}

	public String getParamName() {
		return paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}
}
