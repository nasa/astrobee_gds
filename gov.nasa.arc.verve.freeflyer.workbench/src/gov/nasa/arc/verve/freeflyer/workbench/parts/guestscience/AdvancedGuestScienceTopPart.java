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

import gov.nasa.arc.irg.freeflyer.rapid.FreeFlyerStrings;
import gov.nasa.arc.irg.freeflyer.rapid.runningplan.GuestScienceRunningPlanInfoOne;
import gov.nasa.arc.irg.freeflyer.rapid.runningplan.GuestScienceRunningPlanInfoThree;
import gov.nasa.arc.irg.freeflyer.rapid.runningplan.GuestScienceRunningPlanInfoTwo;
import gov.nasa.arc.irg.freeflyer.rapid.state.GuestScienceAstrobeeStateManager;
import gov.nasa.arc.irg.plan.ui.io.EnlargeableButton;
import gov.nasa.arc.verve.freeflyer.workbench.dialog.DetailedMultiHealthAndStatusDialog;
import gov.nasa.arc.verve.freeflyer.workbench.utils.AgentsFromCommandLine;
import gov.nasa.arc.verve.freeflyer.workbench.utils.GuiUtils;
import gov.nasa.rapid.idl.ext.astrobee.message.MessageTypeExtAstro;
import gov.nasa.rapid.v2.e4.agent.ActiveAgentSet;
import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.e4.agent.IActiveAgentSetListener;
import gov.nasa.rapid.v2.e4.message.MessageType;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class AdvancedGuestScienceTopPart implements IActiveAgentSetListener {
	private static Logger logger = Logger.getLogger(AdvancedGuestScienceTopPart.class);
	
	protected IEclipseContext context;
	protected final int numAstrobees = 3;
	protected AdvancedAstrobeeRow[] astrobeeRows;
	protected GuestScienceAstrobeeStateManager[] managers;
	protected GuestScienceStateManager guestScienceStateManager;
	protected EnlargeableButton healthDetailsButton;
	protected int numSelected = 0;
	protected final int NUM_BEES = 3;
	protected Agent[] selected = new Agent[NUM_BEES];
	
	protected final String COMM_TITLE_TOOLTIP = "Connection to Astrobee";
	protected final String NAME_TOOLTIP = "Astrobee Name";
	protected final String HEALTH_TOOLTIP = "Status of Astrobee Subsystems";
	protected final String DETAILS_BUTTON_TOOLTIP = "View Health and Status Data for Selected Astrobee(s)";
	
	@Inject 
	public AdvancedGuestScienceTopPart(Composite parent, MApplication application) {
		this.context = application.getContext();
		createControls( parent);
		ActiveAgentSet.INSTANCE.addListener(this);
	}

	protected void createControls(Composite parent) {
		GridLayout gl = new GridLayout(1, false);
		parent.setLayout(gl);

		makeAndPopulateMainComposite(parent);
		makeHealthAndStatusDetailsButton(parent);
		activeAgentSetChanged();
		
		context.set(AdvancedGuestScienceTopPart.class, this);
	}
	
	protected void makeHealthAndStatusDetailsButton(Composite parent) {
		Composite innerComposite = GuiUtils.setupInnerCompositeEvenSpacing(parent, getCellsAcross(), GridData.FILL_BOTH);

		healthDetailsButton = new EnlargeableButton(innerComposite, SWT.NONE);
		healthDetailsButton.setText("Health Details");
		healthDetailsButton.setToolTipText(DETAILS_BUTTON_TOOLTIP);
		GridData ld = new GridData(SWT.FILL, SWT.BEGINNING, true, true);
		ld.horizontalSpan = getDetailsButtonWidth();
		healthDetailsButton.setLayoutData(ld);
		healthDetailsButton.setButtonLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, true));
		
		healthDetailsButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				makeDetailsDialog();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// no-op
			}
		});
		healthDetailsButton.setEnabled(isDetailsButtonEnabledByDefault());
		Label spacer = new Label(innerComposite, SWT.None);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).grab(true, true).span(getCellsAcross() - getDetailsButtonWidth(),1).applyTo(spacer);
	}
	
	protected int getDetailsButtonWidth() {
		return GSSpaceHelper.detailsButtonWidth;
	}
	
	protected boolean isDetailsButtonEnabledByDefault() {
		return false;
	}
	
	protected void makeDetailsDialog() {
		DetailedMultiHealthAndStatusDialog dialog = ContextInjectionFactory.make(
				DetailedMultiHealthAndStatusDialog.class, context);
		dialog.create();
		dialog.setBlockOnOpen(false);
		dialog.open();
	}

	protected void makeAndPopulateMainComposite(Composite parent) {
		Composite mainComposite = new Composite(parent, SWT.None);
		GridLayout glMain = new GridLayout(getCellsAcross(), true);
		mainComposite.setLayout(glMain);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).grab(true, false).span(1,1).applyTo(mainComposite);

		setupRowOfTitles(mainComposite);
		astrobeeRows = new AdvancedAstrobeeRow[AgentsFromCommandLine.INSTANCE.getNumAgents()];
		managers = new GuestScienceAstrobeeStateManager[AgentsFromCommandLine.INSTANCE.getNumAgents()];
		guestScienceStateManager = context.get(GuestScienceStateManager.class);
		
		for(int i=0; i<AgentsFromCommandLine.INSTANCE.getNumAgents(); i++) {
			managers[i] = new GuestScienceAstrobeeStateManager(context);
//			managers[i].acceptAgent(AgentsFromCommandLine.INSTANCE.getAgent(i));
			astrobeeRows[i] = makeAstrobeeRow(mainComposite, AgentsFromCommandLine.INSTANCE.getAgent(i), FreeFlyerStrings.SELECTED_GUEST_SCIENCE[i]);
//			guestScienceStateManager.addListener(astrobeeRows[i]);
//			managers[i].addListener(astrobeeRows[i], MessageType.ACCESSCONTROL_STATE_TYPE);
//			managers[i].addListener(astrobeeRows[i], MessageTypeExtAstro.AGENT_STATE_TYPE);
//			managers[i].addListener(astrobeeRows[i], MessageTypeExtAstro.FAULT_STATE_TYPE);
//			managers[i].addListener(astrobeeRows[i], MessageTypeExtAstro.PLAN_STATUS_TYPE);
//			managers[i].addListener(astrobeeRows[i], MessageTypeExtAstro.CURRENT_PLAN_COMPRESSED_TYPE);
//			managers[i].addBattPercentListener(astrobeeRows[i]);
			
		
		}
	}
	
	@Inject @Optional
	public void acceptGuestScienceRunningPlanInfoOne(
			@Named(FreeFlyerStrings.GUEST_SCIENCE_PLAN_INFO_1) GuestScienceRunningPlanInfoOne gsrpi) {
		managers[0].setGuestScienceRunningPlanInfo(gsrpi);
	}
	
	@Inject @Optional
	public void acceptGuestScienceRunningPlanInfoTwo(
			@Named(FreeFlyerStrings.GUEST_SCIENCE_PLAN_INFO_2) GuestScienceRunningPlanInfoTwo gsrpi) {
		managers[1].setGuestScienceRunningPlanInfo(gsrpi);
	}
	
	@Inject @Optional
	public void acceptGuestScienceRunningPlanInfoThree(
			@Named(FreeFlyerStrings.GUEST_SCIENCE_PLAN_INFO_3) GuestScienceRunningPlanInfoThree gsrpi) {
		managers[2].setGuestScienceRunningPlanInfo(gsrpi);
	}
	
	protected AdvancedAstrobeeRow makeAstrobeeRow(Composite c, Agent a, String contextString) {
		return new AdvancedAstrobeeRow(c, a, context, contextString);
	}
	
	protected int getCellsAcross() {
		return GSSpaceHelper.advancedCellsAcross;
	}

	protected void setupRowOfTitles(Composite c) {
		Composite rowComposite = new Composite(c, SWT.None);
		GridLayout glLeft = new GridLayout(GSSpaceHelper.advancedCellsAcross, true);
		glLeft.marginWidth = 0;
		rowComposite.setLayout(glLeft);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).grab(true, false).span(GSSpaceHelper.advancedCellsAcross,1).applyTo(rowComposite);
		
		Label spacer = new Label(rowComposite, SWT.NONE);
		spacer.setText("Name");
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).grab(true, true).span(GSSpaceHelper.selection_width,1).applyTo(spacer);

		Label commTitle = new Label(rowComposite, SWT.NONE);
		commTitle.setText("Comm");
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BOTTOM).grab(true, true).span(GSSpaceHelper.comm_width,1).applyTo(commTitle);

		Label faultTitle = new Label(rowComposite, SWT.NONE);
		faultTitle.setText("Health");
		GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.BOTTOM).grab(true, true).span(GSSpaceHelper.health_width,1).applyTo(faultTitle);
		
		Label battTitle = new Label(rowComposite, SWT.NONE);
		battTitle.setText("Batt");
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BOTTOM).grab(true, true).span(GSSpaceHelper.batt_width,1).applyTo(battTitle);

		Label controlTitle = new Label(rowComposite, SWT.NONE);
		controlTitle.setText("Control");
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BOTTOM).grab(true, true).span(GSSpaceHelper.control_width,1).applyTo(controlTitle);
	
		Label apkSummaryTitle = new Label(rowComposite, SWT.NONE);
		apkSummaryTitle.setText("Summary");
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BOTTOM).grab(true, true).span(GSSpaceHelper.summary_width,1).applyTo(apkSummaryTitle);

		Label planTitle = new Label(rowComposite, SWT.NONE);
		planTitle.setText("Plan");
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BOTTOM).grab(true, true).span(GSSpaceHelper.plan_width,1).applyTo(planTitle);

		Label planStatusTitle = new Label(rowComposite, SWT.NONE);
		planStatusTitle.setText("Plan Status");
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BOTTOM).grab(true, true).span(GSSpaceHelper.plan_status_width,1).applyTo(planStatusTitle);
		
		Label planStepTitle = new Label(rowComposite, SWT.NONE);
		planStepTitle.setText("Plan Step");
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BOTTOM).grab(true, true).span(GSSpaceHelper.plan_step_width,1).applyTo(planStepTitle);
		
		Label apkTitle = new Label(rowComposite, SWT.NONE);
		apkTitle.setText("APK");
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BOTTOM).grab(true, true).span(GSSpaceHelper.apk_width,1).applyTo(apkTitle);
		
		Label apkStatusTitle = new Label(rowComposite, SWT.NONE);
		apkStatusTitle.setText("APK Status");
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BOTTOM).grab(true, true).span(GSSpaceHelper.apk_status_width,1).applyTo(apkStatusTitle);
			}
	
	@PreDestroy
	protected void preDestroy() {
		for(int i=0; i<astrobeeRows.length; i++) {
			managers[i].preDestroy();
			managers[i].removeListener(astrobeeRows[i]);
			guestScienceStateManager.removeListener(astrobeeRows[i]);
		}
	}

	public void activeAgentSetChanged() {
		connectIfNecessary();
		
		for(int i=0; i<astrobeeRows.length; i++) {
			if(ActiveAgentSet.contains(astrobeeRows[i].getAgent())) {
				astrobeeRows[i].setAgentConnected();
			}else{
				astrobeeRows[i].setAgentDisconnected();
			}
		}
	}
	
	protected void connectIfNecessary() {
		for(int i=0; i<AgentsFromCommandLine.INSTANCE.getNumAgents(); i++) {
			Agent a = AgentsFromCommandLine.INSTANCE.getAgent(i);
			if(ActiveAgentSet.contains(a)) {
				managers[i].acceptAgent(AgentsFromCommandLine.INSTANCE.getAgent(i));
				
				try {
					context.set(FreeFlyerStrings.GUEST_SCIENCE_ASTROBEE_STATE_MANAGER[i], managers[i]);
				} catch(Exception e) {}
				
				guestScienceStateManager.addListener(astrobeeRows[i]);
				managers[i].addListener(astrobeeRows[i], MessageType.ACCESSCONTROL_STATE_TYPE);
				managers[i].addListener(astrobeeRows[i], MessageTypeExtAstro.AGENT_STATE_TYPE);
				managers[i].addListener(astrobeeRows[i], MessageTypeExtAstro.FAULT_STATE_TYPE);
				managers[i].addListener(astrobeeRows[i], MessageTypeExtAstro.PLAN_STATUS_TYPE);
				managers[i].addBattPercentListener(astrobeeRows[i]);
			}
		}
	}

	protected void incrementSelected() {
		if(numSelected == 0) {
			if(healthDetailsButton != null) {
				healthDetailsButton.setEnabled(true);
			}
		}
		numSelected++;
	}
	
	protected void decrementSelected() {
		numSelected--;
		if(numSelected < 1) {
			if(healthDetailsButton != null) {
				healthDetailsButton.setEnabled(isDetailsButtonEnabledByDefault());
			}
		}
	}
	
	@Inject @Optional
	public void acceptGuestScienceAgent1(@Named(FreeFlyerStrings.SELECTED_GUEST_SCIENCE_1) Agent a) {
		if(selected[0] == null && a != null) {
			incrementSelected();
		}
		else if(selected[0] != null && a == null) {
			decrementSelected();
		}
		selected[0] = a;
	}

	@Inject @Optional
	public void acceptGuestScienceAgent2(@Named(FreeFlyerStrings.SELECTED_GUEST_SCIENCE_2) Agent a) {
		if(selected[1] == null && a != null) {
			incrementSelected();
		}
		else if(selected[1] != null && a == null) {
			decrementSelected();
		}
		selected[1] = a;
	}

	@Inject @Optional
	public void acceptGuestScienceAgent3(@Named(FreeFlyerStrings.SELECTED_GUEST_SCIENCE_3) Agent a) {
		if(selected[2] == null && a != null) {
			incrementSelected();
		}
		else if(selected[2] != null && a == null) {
			decrementSelected();
		}
		selected[2] = a;
	}

	@Override
	public void activeAgentAdded(Agent agent, String participantId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void activeAgentRemoved(Agent agent) {
		// TODO Auto-generated method stub
		
	}
}