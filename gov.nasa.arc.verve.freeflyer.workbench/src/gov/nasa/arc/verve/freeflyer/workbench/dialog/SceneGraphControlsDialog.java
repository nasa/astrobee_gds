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
package gov.nasa.arc.verve.freeflyer.workbench.dialog;

import gov.nasa.arc.irg.freeflyer.rapid.FreeFlyerStrings;
import gov.nasa.arc.irg.plan.ui.io.WorkbenchConstants;
import gov.nasa.arc.verve.freeflyer.workbench.helpers.ScenarioToggleListener;
import gov.nasa.arc.verve.freeflyer.workbench.scenario.FreeFlyerScenario;
import gov.nasa.arc.verve.freeflyer.workbench.utils.GuiUtils;
import gov.nasa.rapid.v2.e4.agent.Agent;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class SceneGraphControlsDialog extends Dialog implements ScenarioToggleListener {
	private Button text, defaultMat, usos_iss, planTrace, resetPoseHistoryButton;
	private Button handrails, configKeepouts, configKeepins, primaryKeepouts, primaryKeepins;
	private Button[] modules, cameras;
	private boolean showGraniteLab;
	@Inject
	FreeFlyerScenario scenario;

	@Inject
	public SceneGraphControlsDialog(@Named(IServiceConstants.ACTIVE_SHELL) Shell parentShell, FreeFlyerScenario scenario) {
		super(parentShell);
		setShellStyle(SWT.CLOSE | SWT.MODELESS | SWT.BORDER | SWT.TITLE);
		this.scenario = scenario;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		showGraniteLab = WorkbenchConstants.worldIsGraniteLab();

		if(showGraniteLab) {
			setupGranite(parent);
		} else {
			setupIss(parent);
		}
		return parent;
	}
	
	@Inject @Optional
	public void acceptPrimaryBee(@Named(FreeFlyerStrings.PRIMARY_BEE) Agent newPrimary) {
		primaryKeepouts.setEnabled(true);
		primaryKeepins.setEnabled(true);
	}

	private void setupGranite(Composite parent) {
		GridLayout gl = new GridLayout();
		gl.numColumns = 2;
		parent.setLayout(gl);

		GridData gd = new GridData(SWT.BEGINNING, SWT.BEGINNING, true, false);
		gd.verticalSpan = 6;
		parent.setLayoutData(gd);

		Label label = new Label(parent, SWT.NONE);
		label.setText("Toggle scene elements");
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Font bigFont = GuiUtils.makeBigFont(parent, label);
		label.setFont(bigFont);

		new Label(parent, SWT.NONE);
		createPlanTraceCheckbox(parent);
		createFOVCamerasCheckboxes(parent);
		createKeepoutsCheckbox(parent);
		createKeepinsCheckbox(parent);
		createResetPoseHistoryButton(parent);
	}

	private void setupIss(Composite parent) {
		GridLayout gl = new GridLayout();
		parent.setLayout(gl);

		GridData gd = new GridData(SWT.BEGINNING, SWT.BEGINNING, true, false);
		gd.verticalSpan = 6;
		parent.setLayoutData(gd);

		Label label = new Label(parent, SWT.NONE);
		label.setText("Toggle scene elements");
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Font bigFont = GuiUtils.makeBigFont(parent, label);
		label.setFont(bigFont);

		new Label(parent, SWT.NONE);
		createTextCheckbox(parent);
		createUsosCheckbox(parent);
		createPlanTraceCheckbox(parent);
		createHandrailsCheckbox(parent);
		createKeepoutsCheckbox(parent);
		createKeepinsCheckbox(parent);

		gl.numColumns = 2;
		scenario.addToggleListener(this);

		createFOVCamerasCheckboxes(parent);
		
		createResetPoseHistoryButton(parent);
	}

	@Override
	public boolean close() {
		scenario.removeToggleListener(this);
		return super.close();
	}

	private void createTextCheckbox(Composite parent) {
		text = new Button(parent, SWT.CHECK);
		text.setText("Show text");
		text.addSelectionListener(new SelectionListener () {
			@Override
			public void widgetSelected(SelectionEvent e) {			
				if(text.getSelection()) {
					scenario.showText();
				} else {
					scenario.hideText();
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) { /**/ }
		});
		text.setSelection(true);
	}
	
	private void createHandrailsCheckbox(Composite parent) {
		handrails = new Button(parent, SWT.CHECK);
		handrails.setText("Show handrails");
		handrails.addSelectionListener(new SelectionListener () {
			@Override
			public void widgetSelected(SelectionEvent e) {				
				if(handrails.getSelection()) {
					scenario.showHandrails();
				} else {
					scenario.hideHandrails();
				}		
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) { /**/ }
		});
		handrails.setSelection(true);
	}
	
	private void createKeepoutsCheckbox(Composite parent) {
		configKeepouts = new Button(parent, SWT.CHECK);
		configKeepouts.setText("Show keepouts from config file");
		configKeepouts.addSelectionListener(new SelectionListener () {
			@Override
			public void widgetSelected(SelectionEvent e) {				
				if(configKeepouts.getSelection()) {
					scenario.showConfigKeepouts();
				} else {
					scenario.hideConfigKeepouts();
				}		
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) { /**/ }
		});
		configKeepouts.setSelection(true);
		
		primaryKeepouts = new Button(parent, SWT.CHECK);
		primaryKeepouts.setText("Show keepouts of selected robot");
		primaryKeepouts.addSelectionListener(new SelectionListener () {
			@Override
			public void widgetSelected(SelectionEvent e) {				
				if(primaryKeepouts.getSelection()) {
					scenario.showKeepoutsOfPrimaryRobot();
				} else {
					scenario.hideKeepoutsOfPrimaryRobot();
				}		
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) { /**/ }
		});
		primaryKeepouts.setSelection(false);
		primaryKeepouts.setEnabled(false);
	}
	
	private void createKeepinsCheckbox(Composite parent) {
		configKeepins = new Button(parent, SWT.CHECK);
		configKeepins.setText("Show keepins from config file");
		configKeepins.addSelectionListener(new SelectionListener () {
			@Override
			public void widgetSelected(SelectionEvent e) {				
				if(configKeepins.getSelection()) {
					scenario.showConfigKeepins();
				} else {
					scenario.hideConfigKeepins();
				}		
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) { /**/ }
		});
		configKeepins.setSelection(false);
		
		primaryKeepins = new Button(parent, SWT.CHECK);
		primaryKeepins.setText("Show keepins of selected robot");
		primaryKeepins.addSelectionListener(new SelectionListener () {
			@Override
			public void widgetSelected(SelectionEvent e) {				
				if(primaryKeepins.getSelection()) {
					scenario.showKeepinsOfPrimaryRobot();
				} else {
					scenario.hideKeepinsOfPrimaryRobot();
				}		
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) { /**/ }
		});
		primaryKeepins.setSelection(false);
		primaryKeepins.setEnabled(false);
	}
	
	private void createPlanTraceCheckbox(Composite parent) {
		planTrace = new Button(parent, SWT.CHECK);
		planTrace.setText("Show plan trace");
		planTrace.addSelectionListener(new SelectionListener () {
			@Override
			public void widgetSelected(SelectionEvent e) {				
				if(planTrace.getSelection()) {
					scenario.showRunPlanTrace();
				} else {
					scenario.hideRunPlanTrace();
				}		
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) { /**/ }
		});
		planTrace.setSelection(true);
	}

	private void createUsosCheckbox(Composite parent) {
		usos_iss = new Button(parent, SWT.CHECK);
		usos_iss.setText("Show ISS model");
		usos_iss.addSelectionListener(new SelectionListener () {
			@Override
			public void widgetSelected(SelectionEvent e) {				
				if(usos_iss.getSelection()) {
					scenario.showUsos();
				} else {
					scenario.hideUsos();
				}		
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) { /**/ }
		});
		usos_iss.setSelection(true);
	}

	private void createFOVCamerasCheckboxes(Composite parent) {
		String[] names = scenario.getFOVCameraNames();

		cameras = new Button[names.length];
		for(int i = 0; i < names.length; i++) {
			cameras[i] = new Button(parent, SWT.CHECK);
			cameras[i].setText(names[i]);

			final int index = i;
			cameras[i].addSelectionListener(new SelectionListener() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					if(cameras[index].getSelection()) {
						scenario.showFOVCamera(names[index]);
					} else {
						scenario.hideFOVCamera(names[index]);
					}
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) { }
			});
		}
	}

	private void createResetPoseHistoryButton(Composite parent) {
		resetPoseHistoryButton = new Button(parent, SWT.PUSH);

		resetPoseHistoryButton.setText("Reset Pose History");

		resetPoseHistoryButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				scenario.resetPoseHistory();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) { }
		});
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		// create OK button - cancel doesn't mean anything for this dialog
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
	}


	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Scene Graph Control");
	}

	@Override
	public void setText(boolean selected) {
		text.setSelection(selected);
	}

	@Override
	public boolean isTextSelected() {
		return text.getSelection();
	}

	@Override
	public void setDefault(boolean selected) {
		defaultMat.setSelection(selected);
	}

	@Override
	public boolean isDefaultSelected() {
		return defaultMat.getSelection();
	}

	@Override
	public void setUsos(boolean selected) {
		usos_iss.setSelection(selected);
	}

	@Override
	public boolean isUsosSelected() {
		return usos_iss.getSelection();
	}

	@Override
	public void setPlanTrace(boolean selected) {
		planTrace.setSelection(selected);
	}

	@Override
	public boolean isPlanTraceSelected() {
		return planTrace.getSelection();
	}

	@Override
	public void setModule(boolean selected, int i) {
		modules[i].setSelection(selected);
	}

	@Override
	public boolean isModuleSelected(int i) {
		return modules[i].getSelection();
	}

	@Override
	public void setCamera(boolean selected, String name) {
		for(Button camera : cameras) {
			if(camera.getText().equals(name)) {
				camera.setSelection(selected);
			}
		}
	}

	@Override
	public boolean isCameraSelected(String name) {
		for(Button camera : cameras) {
			if(camera.getText().equals(name)) {
				return camera.getSelection();
			}
		}
		return false;
	}

}
