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

import gov.nasa.arc.irg.plan.converters.CameraNameToIntConverter;
import gov.nasa.arc.irg.plan.converters.IntToCameraNameConverter;
import gov.nasa.arc.irg.plan.freeflyer.command.SetCamera;
import gov.nasa.arc.irg.plan.freeflyer.config.OptionsForOneCamera;
import gov.nasa.arc.irg.plan.ui.io.SetCameraPresetsListLoader;
import gov.nasa.arc.irg.plan.util.PlanConstants;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class SetCameraWidget extends AbstractFreeFlyerWidget {
	protected Combo cameraCombo;
	protected Combo presetCombo;
	
	public SetCameraWidget(Composite parent, int style) {
		super(parent, style);
	}

	@Override
	void setupCustomControls() {
		setupCameraComboAndLabel(this);
		setupPresetComboAndLabel();
	}
	
	protected void setupCameraComboAndLabel(Composite parent) {
		Label l = new Label(this, SWT.NONE);
		l.setText("Camera");
		addChildLabel(l);
		
		cameraCombo = new Combo(this, SWT.READ_ONLY);
		GridData gdata = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gdata.horizontalSpan = 2;
		cameraCombo.setLayoutData(gdata);
		
		cameraCombo.setItems(PlanConstants.CAMERA_NAME_STRINGS);
		addChildControl(cameraCombo);
		
		
		cameraCombo.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				try {
				String[] presetNames = SetCameraPresetsListLoader.getNamesOfPresetsFor(cameraCombo.getText());
				presetCombo.setItems(presetNames);
				
				OptionsForOneCamera opts = SetCameraPresetsListLoader.getOptionsFor(cameraCombo.getText());
				// this might break databinding, but not sure how else to do it
				if(getModel() instanceof SetCamera) {
					((SetCamera)getModel()).setOptions(opts);
				}
				} catch (Exception ex) {
					showErrorDialog(shell, "Error Reading Set Camera Presets Config File", ex.getMessage());
				}
			}
		});
	}
	
	protected void setupPresetComboAndLabel() {
		Label l = new Label(this, SWT.NONE);
		l.setText("Preset");
		addChildLabel(l);
		
		presetCombo = new Combo(this, SWT.READ_ONLY);
		GridData gdata = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gdata.horizontalSpan = 2;
		presetCombo.setLayoutData(gdata);
		presetCombo.setText("No Presets");
		addChildControl(presetCombo);
		
		presetCombo.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				//				
			}
		});
	}

	@Override
	public boolean bindUI(Realm realm) {
		if (getModel() == null){
			setBound(false);
			return false;
		}

		updateNonBoundFields();

		boolean result = true;
		result &= bindSpecial("cameraName", cameraCombo, new IntToCameraNameConverter(), new CameraNameToIntConverter());
		result &= bind("presetIndex", presetCombo);
		return result;
	}
}
