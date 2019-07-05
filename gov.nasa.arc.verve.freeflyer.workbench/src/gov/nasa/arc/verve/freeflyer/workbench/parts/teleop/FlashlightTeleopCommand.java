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
 * Specifies front or back flashlight, and provides presets for various 
 * brightness levels
 * 
 * Example teleopCommandConfig in TeleopCommandsConfiguration.json: 
 * {
	  "type" : "FlashlightTeleopCommand",
	  "label": "Flashlight",
	  "secondColumnLabel": "Brightness",
	  "command": "setFlashlightBrightness",
	  "buttonText": "Set",
	  "names":["Front", "Back"],
	  "brightnessNames":["High", "Medium", "Low", "Off"],
	  "brightnessValues":[1.0, 0.6, 0.3, 0.0],
	   "subsystem": "Settings"
    }
 *  
 * @author ddwheele
 *
 */

public class FlashlightTeleopCommand extends AbstractTeleopCommandConfig {
	protected String secondColumnLabel;
	protected String[] names;
	protected String[] brightnessNames;
	protected float[] brightnessValues;
	protected Combo firstCombo;
	protected Combo secondCombo;

	public FlashlightTeleopCommand() {
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

	protected void createSecondColumnLabel(Composite parent) {
		Label l = new Label(parent, SWT.NONE);
		l.setText(secondColumnLabel);
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
				String name = firstCombo.getText();
				String level = secondCombo.getText();

				float brightness = translateLevelNameToFloat(level);
				commandPublisher.sendSetFlashlightBrightnessCommand(name, brightness);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)  {/**/}
		});
	}

	protected float translateLevelNameToFloat(String level) {
		if(level != null) {
			for(int i=0; i<brightnessNames.length; i++) {
				if(level.equals(brightnessNames[i])) {
					return brightnessValues[i];
				}
			}
		}
		return 0;
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
		secondCombo.setItems(brightnessNames);
		secondCombo.select(0);
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

	public String[] getBrightnessNames() {
		return brightnessNames;
	}

	public void setBrightnessNames(String[] brightnessNames) {
		this.brightnessNames = brightnessNames;
	}

	public float[] getBrightnessValues() {
		return brightnessValues;
	}

	public void setBrightnessValues(float[] brightnessValues) {
		this.brightnessValues = brightnessValues;
	}

}
