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
package gov.nasa.arc.verve.freeflyer.workbench.parts.guestscience;

import gov.nasa.arc.irg.freeflyer.rapid.faults.GdsFaultState;
import gov.nasa.arc.irg.freeflyer.rapid.frequent.AstrobeeBattMinutesListener;
import gov.nasa.arc.irg.freeflyer.rapid.state.AggregateAstrobeeState;
import gov.nasa.arc.irg.freeflyer.rapid.state.AstrobeeStateListener;
import gov.nasa.arc.irg.freeflyer.rapid.state.GuestScienceAstrobeeState;
import gov.nasa.arc.irg.iss.ui.IssUiActivator;
import gov.nasa.arc.irg.plan.ui.io.WorkbenchConstants;
import gov.nasa.arc.irg.util.ui.ColorProvider;
import gov.nasa.arc.verve.freeflyer.workbench.utils.GuiUtils;
import gov.nasa.rapid.v2.e4.agent.Agent;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

// The GuestScienceTopPart creates this class and adds it as a Listener to everything
public class AdvancedAstrobeeRow implements AstrobeeStateListener, AstrobeeBattMinutesListener, GuestScienceStateListener {
	protected Label controlLabel, battLabel, apkStatusLabel, summaryLabel;
	protected Combo apkCombo;
	protected Label planLabel, planStatusLabel, planStepLabel;
	protected Label healthLed, commLed;
	protected Button selectorButton;
	protected final Color colorWhite = ColorProvider.get(255,255,255);
	protected final Color colorGreen = ColorProvider.get(59,181,74);
	protected final Color colorGray = ColorProvider.get(200,200,200);//.INSTANCE.WIDGET_BACKGROUND;
	protected final Color colorCyan = ColorProvider.INSTANCE.cyan;
	protected final Color colorOrange = ColorProvider.get(255, 165, 0);
	protected Image	greenImage, orangeImage, grayImage, cyanImage;
	protected Agent agent;
	protected IEclipseContext context;
	protected String contextString;
	protected Composite rowComposite;
	protected GuestScienceAstrobeeState abridgedAstrobeeState;
	protected GuestScienceStateManager manager; // might want to get this just once??
	protected boolean myAgentIsAlive = false;
	protected int battMinutes;
	protected boolean compositeSelected = false;

	protected final String CYAN_TOOLTIP = "Astrobee Disconnected";
	protected final String COMM_GREEN_TOOLTIP = "Astrobee Connected";
	protected final String FAULT_LABEL_GREEN_TOOLTIP = "All Subsystems Nominal";
	protected final String FAULT_LABEL_ORANGE_TOOLTIP = "One or More Subsystems are Disabled";
	final String SELECTOR_BUTTON_TOOLTIP = "Check to Control ";

	public AdvancedAstrobeeRow(Composite parent, Agent agent, IEclipseContext context, String contextString) {
		this.agent = agent;
		this.context = context;
		this.contextString = contextString;

		rowComposite = new Composite(parent, SWT.None);
		GridLayout glLeft = new GridLayout(getCellsAcross(), true);
		glLeft.marginWidth = 0;
		rowComposite.setLayout(glLeft);
		GridData titleGd = new GridData(SWT.FILL, SWT.TOP, true, false);
		titleGd.horizontalSpan = getCellsAcross();
		rowComposite.setLayoutData(titleGd);
		rowComposite.addPaintListener(new PaintListener() {
			boolean redraw = false;
			
			@Override
			public void paintControl(PaintEvent e) {
				if(compositeSelected == true){
					e.gc.drawLine(0, 0, rowComposite.getBounds().width, 0);
					e.gc.drawLine(0, 0,0, rowComposite.getBounds().height-1);
					e.gc.drawLine(0, rowComposite.getBounds().height-1, rowComposite.getBounds().width, rowComposite.getBounds().height-1);
					e.gc.drawLine(rowComposite.getBounds().width-1, 0,rowComposite.getBounds().width-1, rowComposite.getBounds().height-1);
					if(!redraw){
						rowComposite.redraw();
						redraw = true;
					}	
				}else
					if(redraw){
						rowComposite.redraw();
						redraw = false;
					}
			}
		});
		loadConnectionImages();

		createComposite(rowComposite);
	}

	public Agent getAgent() {
		return agent;
	}

	public void setAgentDisconnected() {
		myAgentIsAlive = false;
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if(selectorButton != null) {
					// if we do this, and an AB dies and doesn't come back,
					// we are stuck trying to talk to it forever.
					//selectorButton.setEnabled(false);
				}
				
				commLed.setImage(cyanImage);
				if(!controlLabel.getText().trim().isEmpty())
					controlLabel.setBackground(colorCyan);
				else
					controlLabel.setBackground(colorGray);
				controlLabel.setToolTipText(CYAN_TOOLTIP);

				if(!battLabel.getText().trim().isEmpty())
					battLabel.setBackground(colorCyan);
				else
					battLabel.setBackground(colorGray);
				battLabel.setToolTipText(CYAN_TOOLTIP);

				if(summaryLabel != null) {
					if(!summaryLabel.getText().trim().isEmpty())
						summaryLabel.setBackground(colorCyan);
					else
						summaryLabel.setBackground(colorGray);
					summaryLabel.setToolTipText(CYAN_TOOLTIP);
				}

				if(planLabel != null) {
					if(!planLabel.getText().trim().isEmpty())
						planLabel.setBackground(colorCyan);
					else
						planLabel.setBackground(colorGray);
					planLabel.setToolTipText(CYAN_TOOLTIP);
				}

				if(planStatusLabel != null) {
					if(!planStatusLabel.getText().trim().isEmpty())
						planStatusLabel.setBackground(colorCyan);
					else
						planStatusLabel.setBackground(colorGray);
					planStatusLabel.setToolTipText(CYAN_TOOLTIP);
				}

				if(planStepLabel != null){
					if(!planStepLabel.getText().trim().isEmpty())
						planStepLabel.setBackground(colorCyan);
					else
						planStepLabel.setBackground(colorGray);
					planStepLabel.setToolTipText(CYAN_TOOLTIP);
				}
				if(apkStatusLabel != null){
					if(!apkStatusLabel.getText().trim().isEmpty())
						apkStatusLabel.setBackground(colorCyan);
					else
						apkStatusLabel.setBackground(colorGray);
					apkStatusLabel.setToolTipText(CYAN_TOOLTIP);
				}
				
				healthLed.setImage(cyanImage);
				healthLed.setToolTipText(CYAN_TOOLTIP);
			}
		});
	}

	public void setAgentConnected() {
		myAgentIsAlive = true;
		updateAllTextFieldsFromAbridgedAstrobeeState();
	}

	protected int getCellsAcross() {
		return GSSpaceHelper.advancedCellsAcross;
	}

	protected void updateAllTextFieldsFromAbridgedAstrobeeState() {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if(!controlLabel.isDisposed()) {
					if(selectorButton != null) {
						selectorButton.setEnabled(true);
					}
					
					controlLabel.setText(abridgedAstrobeeState.getAccessControl());
					if(planLabel != null) {
						planLabel.setText(abridgedAstrobeeState.getCurrentPlanName());
						if(!planLabel.getText().trim().isEmpty())
							planLabel.setBackground(colorWhite);
						else
							planLabel.setBackground(colorGray);
						planLabel.setToolTipText(WorkbenchConstants.PLAN_LABEL_TOOLTIP);
					}
					if(planStatusLabel != null) {
						planStatusLabel.setText(GuiUtils.toTitleCase(abridgedAstrobeeState.getAstrobeeState().getPlanExecutionStateName()));
						if(!planStatusLabel.getText().trim().isEmpty())
							planStatusLabel.setBackground(colorWhite);
						else
							planStatusLabel.setBackground(colorGray);
						planStatusLabel.setToolTipText(WorkbenchConstants.PLAN_STATUS_TOOLTIP);
					}
					if(planStepLabel != null) {
						planStepLabel.setText(abridgedAstrobeeState.getCurrenPlanStepString());
						if(!planStepLabel.getText().trim().isEmpty())
							planStepLabel.setBackground(colorWhite);
						else
							planStepLabel.setBackground(colorGray);
					}

					GdsFaultState faultState = abridgedAstrobeeState.getGdsFaultState();
					if(faultState.isAnySubsystemDisabled()) {
						healthLed.setImage(orangeImage);
						healthLed.setToolTipText(FAULT_LABEL_ORANGE_TOOLTIP);
					} else {
						healthLed.setImage(greenImage);
						healthLed.setToolTipText(FAULT_LABEL_GREEN_TOOLTIP);
					}

					commLed.setImage(greenImage);
					commLed.setToolTipText(COMM_GREEN_TOOLTIP);
					if(!controlLabel.getText().trim().isEmpty())
						controlLabel.setBackground(colorWhite);
					else
						controlLabel.setBackground(colorGray);
					controlLabel.setToolTipText(WorkbenchConstants.CONTROL_TOOLTIP);
					
					if(battMinutes < WorkbenchConstants.LOW_BATT_THRESHOLD) {
						battLabel.setBackground(colorOrange);
						battLabel.setToolTipText(WorkbenchConstants.LOW_BATT_TOOLTIP);
					} else {
						if(!battLabel.getText().trim().isEmpty())
							battLabel.setBackground(colorWhite);
						else
							battLabel.setBackground(colorGray);
						battLabel.setToolTipText(WorkbenchConstants.BATT_TOOLTIP);
					}

					if(summaryLabel != null) {
						if(!summaryLabel.getText().trim().isEmpty())
							summaryLabel.setBackground(colorWhite);
						else
							summaryLabel.setBackground(colorGray);
						summaryLabel.setToolTipText(WorkbenchConstants.SUMMARY_LABEL_TOOLTIP);
					}

					if(apkStatusLabel != null) {
						apkStatusLabel.setBackground(colorWhite);
					}
				}
			}
		});
	}

	public void onAstrobeeStateChange(AggregateAstrobeeState aggregateAstrobeeState) {
		if(!(aggregateAstrobeeState instanceof GuestScienceAstrobeeState)) {
			return;
		} else {
			abridgedAstrobeeState = (GuestScienceAstrobeeState) aggregateAstrobeeState;
		}

		if(!myAgentIsAlive) {
			return;
		}

		updateAllTextFieldsFromAbridgedAstrobeeState();
	}

	protected void createSelectorButton(Composite parent) {
		selectorButton = new Button(parent, SWT.CHECK);
		selectorButton.setText(agent.name());
		selectorButton.setToolTipText(SELECTOR_BUTTON_TOOLTIP + agent.name());
		GridData selectorGd = new GridData(SWT.FILL, SWT.TOP, true, false);
		selectorGd.horizontalSpan = GSSpaceHelper.selection_width;
		selectorButton.setLayoutData(selectorGd);
		selectorButton.setEnabled(false);
		selectorButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				if (selectorButton.getSelection()) {
					context.set(contextString, agent);
					compositeSelected = true;
					//rowComposite.setBackground(colorGreen); PDRT request
				} else {
					context.set(contextString, null);
					compositeSelected = false;
					//rowComposite.setBackground(colorGray); PDRT request
				}
			}
		});
	}

	protected void createComposite(Composite parent) {
		createSelectorButton(parent);

		createCommLed(parent);
		createHealthLed(parent);
		createBattLabel(parent);
		createControlLabel(parent);

		createSummaryLabel(parent);
		createPlanAndPlanStatusLabels(parent);
		planStepLabel(parent);
		createApkComboAndApkStatusLabels(parent);

	}

	protected void createCommLed(Composite parent) {
		commLed = new Label(parent, SWT.None);
		GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.BEGINNING).grab(true, false).span(GSSpaceHelper.comm_width,1).applyTo(commLed);
		commLed.setImage(cyanImage);
	}

	protected void createHealthLed(Composite parent) {
		healthLed = new Label(parent, SWT.None);
		GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.BEGINNING).grab(true, false).span(GSSpaceHelper.health_width,1).applyTo(healthLed);
		healthLed.setImage(grayImage);
	}

	protected void createControlLabel(Composite parent) {
		controlLabel = new Label(parent, SWT.NONE);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).grab(true, false).span(GSSpaceHelper.control_width,1).applyTo(controlLabel);
		controlLabel.setBackground(colorGray);
		controlLabel.setToolTipText(WorkbenchConstants.CONTROL_TOOLTIP);
	}

	protected void createBattLabel(Composite parent) {
		battLabel = new Label(parent, SWT.RIGHT);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).grab(true, false).span(GSSpaceHelper.batt_width,1).applyTo(battLabel);
		battLabel.setBackground(colorGray);
		battLabel.setToolTipText(WorkbenchConstants.BATT_TOOLTIP);
	}

	protected void createSummaryLabel(Composite parent) {
		summaryLabel = new Label(parent, SWT.NONE);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).grab(true, false).span(GSSpaceHelper.summary_width,1).applyTo(summaryLabel);
		summaryLabel.setBackground(colorGray);
		summaryLabel.setToolTipText(WorkbenchConstants.SUMMARY_LABEL_TOOLTIP);
	}

	protected void createPlanAndPlanStatusLabels(Composite parent) {
		planLabel = new Label(parent, SWT.NONE);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).grab(true, false).span(GSSpaceHelper.plan_width,1).applyTo(planLabel);
		planLabel.setBackground(colorGray);
		planLabel.setToolTipText(WorkbenchConstants.PLAN_LABEL_TOOLTIP);

		planStatusLabel = new Label(parent, SWT.NONE);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).grab(true, false).span(GSSpaceHelper.plan_status_width,1).applyTo(planStatusLabel);
		planStatusLabel.setBackground(colorGray);
		planStatusLabel.setToolTipText(WorkbenchConstants.PLAN_STATUS_TOOLTIP);
	}

	protected void planStepLabel(Composite parent) {
		planStepLabel = new Label(parent, SWT.NONE);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).grab(true, false).span(GSSpaceHelper.plan_step_width,1).applyTo(planStepLabel);
		planStepLabel.setBackground(colorGray);
	}

	protected void createApkComboAndApkStatusLabels(Composite parent) {
		apkCombo = new Combo(parent, SWT.READ_ONLY);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).grab(true, false).span(GSSpaceHelper.apk_width,1).applyTo(apkCombo);

		apkCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String status = manager.getStatusOfApkNamed(agent, apkCombo.getText());
				apkStatusLabel.setText(status);
			}
		});

		apkStatusLabel = new Label(parent, SWT.NONE);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).grab(true, false).span(GSSpaceHelper.apk_status_width,1).applyTo(apkStatusLabel);
		apkStatusLabel.setBackground(colorGray);
	}

	public void onGuestScienceStateChange(GuestScienceStateManager manager) {
		this.manager = manager;
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if(apkStatusLabel != null) {
					String status = manager.getStatusOfApkNamed(agent, apkCombo.getText());
					apkStatusLabel.setText(status);
				}
				String summaryText = manager.getASummary(agent, apkCombo.getText());
				summaryLabel.setText(summaryText);
			}
		});
	}

	public void onBattMinutesChange(int battMinutes) {
		this.battMinutes = battMinutes;
		updateBatteryLevel();
	}

	protected void updateBatteryLevel() {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if(!battLabel.isDisposed()) {

					battLabel.setText(GuiUtils.convertMinutesToHHM0(battMinutes));
					if(battMinutes < WorkbenchConstants.LOW_BATT_THRESHOLD) {
						battLabel.setBackground(colorOrange);
						battLabel.setToolTipText(WorkbenchConstants.LOW_BATT_TOOLTIP);
					} else {
						battLabel.setBackground(colorWhite);
						battLabel.setToolTipText(WorkbenchConstants.BATT_TOOLTIP);
					}
				}
			}
		});
	}

	public void onGuestScienceConfigChange(GuestScienceStateManager manager) {
		this.manager = manager;
		String[] apkNames = manager.getApkNamesForAstrobee(agent);
		if(apkNames == null) {
			return;
		}
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				apkCombo.setItems(apkNames);
				apkCombo.select(0);
				apkCombo.pack(true);
				apkCombo.redraw();
			}
		});
	}

	public void onGuestScienceDataChange(GuestScienceStateManager manager, String apkName, String topic) {
		this.manager = manager;
	}

	protected void loadConnectionImages() {
		greenImage = IssUiActivator.getImageFromRegistry("greenCircle");
		orangeImage = IssUiActivator.getImageFromRegistry("orangeCircle");
		grayImage = IssUiActivator.getImageFromRegistry("grayCircle");
		cyanImage = IssUiActivator.getImageFromRegistry("cyanCircle");
	}
}
