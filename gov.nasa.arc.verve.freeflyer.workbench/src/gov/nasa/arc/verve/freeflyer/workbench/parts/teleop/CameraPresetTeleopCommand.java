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

import gov.nasa.arc.irg.plan.freeflyer.config.OptionsForOneCamera;
import gov.nasa.arc.verve.freeflyer.workbench.utils.GuiUtils;
import gov.nasa.arc.verve.freeflyer.workbench.widget.helpers.CommandButton;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * Lets user select a camera and a preset configuration for that camera.
 * 
 * Example teleopCommandConfig in TeleopCommandsConfiguration.json: 
 * {
	 "type" : "CameraPresetTeleopCommand",
	  "label": "Camera to Configure",
	  "secondColumnLabel": "Configuration",
	  "buttonText": "Set",
	  "optionsForOneCamera": [ 
	  	{
	  	"cameraName": "Dock",
	  	"preset": [ {
			"presetName": "High Definition Rec",
			"resolution" : "1024_768",
			"cameraMode" : "Recording",
			"frameRate" : 5,
			"bandwidth" : 640
	  		},
	  		{
			"presetName": "Low Definition Stream",
			"resolution" : "640_480",
			"cameraMode" : "Streaming",
			"frameRate" : 4,
			"bandwidth" : 92
	  		} 
	  	]
	  	},{
	  	"cameraName": "Navigation",
	  	"preset": [ {
			"presetName": "High Definition Rec+Stream",
			"resolution" : "1920_1080",
			"cameraMode" : "Both",
			"frameRate" : 5,
			"bandwidth" : 100
	  		},
	  		{
			"presetName": "Low Definition Rec+Stream",
			"resolution" : "640_480",
			"cameraMode" : "Both",
			"frameRate" : 25,
			"bandwidth" : 300
	  		} 
	  	]
	  	}
	  	],
	  "subsystem": "Camera"
	}
 */

public class CameraPresetTeleopCommand extends AbstractTeleopCommandConfig {
	protected String secondColumnLabel;

	protected Combo firstCombo;
	protected Combo secondCombo;

	protected List<OptionsForOneCamera> optionsForOneCamera;

	public CameraPresetTeleopCommand() {
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

	protected void createFirstOptionsCombo(Composite parent) {
		firstCombo = new Combo(parent, SWT.READ_ONLY);
		firstCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		firstCombo.setItems(getCameraNames());
		firstCombo.select(0);
		firstCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateSecondCombo();
			}
		});
	}

	protected void createSecondOptionsCombo(Composite parent) {
		secondCombo = new Combo(parent, SWT.READ_ONLY);
		secondCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		secondCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// enable the send button when something is selected
			}
		});
		updateSecondCombo();
	}
	
	protected void updateSecondCombo() {
		// Find the presets that are available for that camera
		OptionsForOneCamera cameraOptions = null;
		for(OptionsForOneCamera camOpt : optionsForOneCamera) {
			if(camOpt.getCameraName().equals(firstCombo.getText())) {
				cameraOptions = camOpt;
				break;
			}
		}
		if(cameraOptions != null) {
			secondCombo.setItems(cameraOptions.getPresetNames());
			secondCombo.select(0);
		}
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
				OptionsForOneCamera.CameraPreset preset =  getSelectedOptionForCamera();
				if(preset != null) {
					commandPublisher.sendSetCameraParamsCommand(firstCombo.getText(),
							preset.getResolution(),
							preset.getCameraMode(),
							preset.getFrameRate(),
							preset.getBandwidth());
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)  {/**/}
		});
	}
	
	protected OptionsForOneCamera.CameraPreset getSelectedOptionForCamera() {
		for(OptionsForOneCamera ofoc : optionsForOneCamera) {
			// find options for selected camera
			if(ofoc.getCameraName().equalsIgnoreCase(firstCombo.getText())) {
				for(OptionsForOneCamera.CameraPreset preset : ofoc.getPreset()) {
					if(preset.getPresetName().equalsIgnoreCase(secondCombo.getText())) {
						return preset;
					}
				}
			}
		}
		return null;
	}

	protected String[] getCameraNames() {
		int numCameras = optionsForOneCamera.size();
		String[] ret = new String[numCameras];
		for(int i=0; i<numCameras; i++) {
			ret[i] = GuiUtils.toTitleCase(optionsForOneCamera.get(i).getCameraName());
		}
		return ret;
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

	public List<OptionsForOneCamera> getOptionsForOneCamera() {
		return optionsForOneCamera;
	}

	public void setOptionsForOneCamera(List<OptionsForOneCamera> optionsForOneCamera) {
		this.optionsForOneCamera = optionsForOneCamera;
	}

}
