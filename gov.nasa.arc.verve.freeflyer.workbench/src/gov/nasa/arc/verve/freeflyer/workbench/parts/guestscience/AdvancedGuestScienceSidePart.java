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

import gov.nasa.arc.irg.freeflyer.rapid.CommandPublisher;
import gov.nasa.arc.irg.freeflyer.rapid.FreeFlyerStrings;
import gov.nasa.arc.irg.freeflyer.rapid.state.AggregateAstrobeeState;
import gov.nasa.arc.irg.freeflyer.rapid.state.AstrobeeStateGds.ExecutionState;
import gov.nasa.arc.irg.freeflyer.rapid.state.AstrobeeStateGds.OperatingState;
import gov.nasa.arc.irg.freeflyer.rapid.state.AstrobeeStateListener;
import gov.nasa.arc.irg.freeflyer.rapid.state.GuestScienceApkGdsRunning;
import gov.nasa.arc.irg.freeflyer.rapid.state.GuestScienceAstrobeeStateManager;
import gov.nasa.arc.irg.plan.freeflyer.config.GuestScienceApkGds.GuestScienceCommandGds;
import gov.nasa.arc.verve.freeflyer.workbench.dialog.LoadMultiplePlansDialog;
import gov.nasa.arc.verve.freeflyer.workbench.utils.Berth;
import gov.nasa.arc.verve.freeflyer.workbench.widget.helpers.CommandButtonForGuestScienceTab;
import gov.nasa.rapid.idl.ext.astrobee.message.MessageTypeExtAstro;
import gov.nasa.rapid.v2.e4.agent.ActiveAgentSet;
import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.e4.agent.IActiveAgentSetListener;
import gov.nasa.rapid.v2.e4.message.MessageType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import rapid.ACCESSCONTROL;
import rapid.ACCESSCONTROL_METHOD_REQUESTCONTROL;
import rapid.MOBILITY_METHOD_STOPALLMOTION;
import rapid.ext.astrobee.MOBILITY;
import rapid.ext.astrobee.PLAN;
import rapid.ext.astrobee.PLAN_METHOD_RUN_PLAN;

public class AdvancedGuestScienceSidePart implements AstrobeeStateListener, GuestScienceStateListener, IActiveAgentSetListener {
	protected String myId;
	protected String accessControlName;
	protected CommandButtonForGuestScienceTab grabControlButton;
	protected CommandButtonForGuestScienceTab loadButton, runButton, stopButton;
	protected Combo apkCombo;
	protected CommandButtonForGuestScienceTab startApkButton, stopApkButton;
	protected Combo commandApkCombo, templateCombo;
	protected Text commandText;
	protected CommandButtonForGuestScienceTab sendGuestScienceCommandButton;
	protected final int NUM_BEES = 3;
	protected Agent[] selected = new Agent[NUM_BEES];
	protected final String NO_SELECTION_STRING = "-";
	protected CommandPublisher[] commandPublishers = new CommandPublisher[NUM_BEES];
	protected GuestScienceAstrobeeStateManager[] managers = new GuestScienceAstrobeeStateManager[NUM_BEES];
	protected GuestScienceStateManager guestScienceStateManager;
	protected IEclipseContext context;
	protected Map<Agent,Boolean> astrobeeInStateToAcceptUpload = new HashMap<Agent,Boolean>(); // selection + access control not included
	protected Map<Agent,Boolean> haveControlOf = new HashMap<Agent,Boolean>();
	protected Map<Agent,Boolean> astrobeeReadyToRun = new HashMap<Agent,Boolean>(); // selection + access control not included
	protected Map<Agent,AggregateAstrobeeState> astrobeeAggregateState = new HashMap<Agent, AggregateAstrobeeState>();
	protected CommandPublisher commandPublisher;
	protected Berth currentBerth;

	protected final String MULTI_GRAB_CONTROL_TOOLTIP = "Grab Access Control of Selected Bee(s)";

	protected final String MULTI_LOAD_BUTTON_LABEL = "Select and Load Plans...";
	protected final String SINGLE_LOAD_BUTTON_LABEL = "Select and Load Plan...";
	
	protected final String MULTI_RUN_BUTTON_LABEL =	"Run Plans";
	protected final String SINGLE_RUN_BUTTON_LABEL = "Run Plan";

	protected final String MUTLI_STOP_BUTTON_LABEL = "Station Keep Bees";
	
	protected final String SINGLE_STOP_BUTTON_LABEL = "Station Keep Bee";

	protected final String GUEST_SCIENCE_COMBO_TOOLTIP = "Select Guest Science Application";
	protected final String COMMAND_COMBO_TOOLTIP = "Select Guest Science Command";
	protected final String SEND_COMMAND_BUTTON_TOOLTIP = "Send Command to Selected Bee(s)";

	@Inject private MPart part;
	
	@Inject
	public AdvancedGuestScienceSidePart(Composite parent, GuestScienceStateManager gssm, MApplication app) {
		myId = Agent.getEgoAgent().name();
		constructComposite(parent);
		guestScienceStateManager = gssm;
		context = app.getContext();
		guestScienceStateManager.addListener(this);
		ActiveAgentSet.INSTANCE.addListener(this);
	}

	protected void constructComposite(Composite parent) {
		GridLayout gl0 = new GridLayout(1, false);
		parent.setLayout(gl0);

		createTopTitleAndButtons(parent);
		createPlanGroup(parent);
		createApkGroup(parent);
		createManualCommandingGroup(parent);
	}

	protected void createTopTitleAndButtons(Composite parent) {
		Composite innerComposite = setupInnerComposite(parent, 2, GridData.FILL_HORIZONTAL);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).grab(true, false).applyTo(innerComposite);

		Group group = new Group(parent, SWT.SHADOW_IN);
		GridData nameData = new GridData(SWT.FILL, SWT.TOP, true, false);
		group.setLayoutData(nameData);
		GridLayout gl = new GridLayout(1, true);
		group.setLayout(gl);

		makeGrabControlButton(group);
	}

	protected void createPlanGroup(Composite parent) {
		Group group = new Group(parent, SWT.SHADOW_IN);
		group.setText("Plans");
		GridData nameData = new GridData(SWT.FILL, SWT.TOP, true, false);
		group.setLayoutData(nameData);
		GridLayout gl = new GridLayout(2, true);
		group.setLayout(gl);

		createLoadButton(group);
		createRunButton(group);
		createStopButton(group);
	}

	protected void createApkGroup(Composite parent) {
		Group group = new Group(parent, SWT.SHADOW_IN);
		group.setText("APKs");
		GridData nameData = new GridData(SWT.FILL, SWT.TOP, true, false);
		group.setLayoutData(nameData);
		GridLayout gl = new GridLayout(2, true);
		group.setLayout(gl);

		apkCombo = new Combo(group, SWT.READ_ONLY);
		apkCombo.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		apkCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				enableStartStopApkButtons();
			}
		});

		createStartApkButton(group);

		new Label(group, SWT.NONE);

		createStopApkButton(group);
	}
	
	protected void makeGrabControlButton(Composite parent) {
		grabControlButton = new CommandButtonForGuestScienceTab(parent, SWT.NONE);
		grabControlButton.setText("Grab Control");
		grabControlButton.setToolTipText(MULTI_GRAB_CONTROL_TOOLTIP);
		GridData gcData = new GridData(SWT.FILL, SWT.TOP, true, false);
		grabControlButton.setLayoutData(gcData);
		grabControlButton.setButtonLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		grabControlButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				for(int i=0; i<selected.length; i++) {
					if(selected[i] != null && managers[i] != null && commandPublishers[i] != null) {
						managers[i].startRequestingControl();
						commandPublishers[i].sendGenericNoParamsCommand(
								ACCESSCONTROL_METHOD_REQUESTCONTROL.VALUE,
								ACCESSCONTROL.VALUE);
					}
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)  {/**/}
		});
	}

	protected void createLoadButton(Composite group) {
		loadButton = new CommandButtonForGuestScienceTab(group, SWT.NONE);
		loadButton.setText(MULTI_LOAD_BUTTON_LABEL);
		GridData gd = new GridData(SWT.FILL, SWT.TOP, true, false);
		gd.horizontalSpan = 2;
		loadButton.setLayoutData(gd);
		loadButton.setButtonLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		loadButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				LoadMultiplePlansDialog dialog  = ContextInjectionFactory.make(LoadMultiplePlansDialog.class, context);

				dialog.create();
				dialog.setBlockOnOpen(false);
				dialog.open();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)  {/**/}
		});
	}

	protected void createRunButton(Composite group) {
		runButton = new CommandButtonForGuestScienceTab(group, SWT.NONE);
		runButton.setText(MULTI_RUN_BUTTON_LABEL);
		runButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		runButton.setButtonLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		runButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				for(int i=0; i<selected.length; i++) {
					if(selected[i] != null) {
						commandPublishers[i].sendGenericNoParamsCommand(
								PLAN_METHOD_RUN_PLAN.VALUE,
								PLAN.VALUE);
					}
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)  {/**/}
		});
	}

	protected void createStopButton(Composite group) {
		stopButton = new CommandButtonForGuestScienceTab(group, SWT.NONE);
		stopButton.setText(MUTLI_STOP_BUTTON_LABEL);
		stopButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		stopButton.setButtonLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		stopButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				for(int i=0; i<selected.length; i++) {
					if(selected[i] != null) {
						commandPublishers[i].sendGenericNoParamsCommand(
								MOBILITY_METHOD_STOPALLMOTION.VALUE,
								MOBILITY.VALUE);
					}
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)  {/**/}
		});

	}

	protected void createStartApkButton(Composite group) {
		startApkButton = new CommandButtonForGuestScienceTab(group, SWT.NONE);
		startApkButton.setText("Start APK");
		startApkButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		startApkButton.setButtonLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		startApkButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				for(int i=0; i<selected.length; i++) {
					if(selected[i] != null) {
						String apkName = guestScienceStateManager.getApkLongName(selected[i], apkCombo.getText());
						commandPublishers[i].sendStartGuestScienceCommand(apkName);
					}
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)  {/**/}
		});
	}

	protected void createStopApkButton(Composite group) {
		stopApkButton = new CommandButtonForGuestScienceTab(group, SWT.NONE);
		stopApkButton.setText("Stop APK");
		stopApkButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		stopApkButton.setButtonLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		stopApkButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				for(int i=0; i<selected.length; i++) {
					if(selected[i] != null) {
						String apkName = guestScienceStateManager.getApkLongName(selected[i], apkCombo.getText());
						commandPublishers[i].sendStopGuestScienceCommand(apkName);
					}
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)  {/**/}
		});
	}

	protected String[] setOfCommandsToStrings(Set<GuestScienceCommandGds> input) {
		Vector<String> ret = new Vector<String>();

		for(GuestScienceCommandGds cmd : input) {
			ret.add(cmd.getName());
		}

		return ret.toArray(new String[input.size()]);
	}

	// return any of the selected Agents
	protected Agent getASelectedAgent() {
		for(int i=0; i<selected.length; i++) {
			if(selected[i] != null) {
				return selected[i];
			}
		}
		return null;
	}

	protected void populateAndEnableTemplateCombo() {
		// Find the commands that are available for that APK
		Set<GuestScienceCommandGds> commands = findTemplatesInCommon(commandApkCombo.getText());
		
		if(commands.isEmpty()) {
			templateCombo.removeAll();
			templateCombo.setEnabled(false);
		} else {
			templateCombo.setItems(setOfCommandsToStrings(commands));
			templateCombo.setEnabled(true);
		}
		
		sendGuestScienceCommandButton.setCompositeEnabled(false);
	}
	
	protected void createManualCommandingGroup(Composite parent) {
		Group group = new Group(parent, SWT.SHADOW_IN);
		group.setText("Manual Commanding");
		GridDataFactory.fillDefaults().grab(true, false).span(2, 1).applyTo(group);

		GridLayout gl = new GridLayout(2, true);
		group.setLayout(gl);

		new Label(group, SWT.NONE).setText("APK");
		new Label(group, SWT.NONE).setText("Template");

		commandApkCombo = new Combo(group, SWT.READ_ONLY);
		commandApkCombo.setEnabled(false);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).grab(true, false).applyTo(commandApkCombo);
		commandApkCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				populateAndEnableTemplateCombo();
			}
		});

		templateCombo = new Combo(group, SWT.READ_ONLY);
		templateCombo.setEnabled(false);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).grab(true, false).applyTo(templateCombo);
		templateCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String template = guestScienceStateManager.getCommandBody(getASelectedAgent(), commandApkCombo.getText(), templateCombo.getText());
				commandText.setText(template);
				enableSendCustomApkCommandButton();
			}
		});

		Label commandLabel = new Label(group, SWT.NONE);
		commandLabel.setText("Command");
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).grab(true, false).span(2,1).applyTo(commandLabel);

		commandText = new Text(group, SWT.BORDER);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).grab(true, false).span(2,1).applyTo(commandText);
		commandText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				enableSendCustomApkCommandButton();
			}
		});

		createSendGuestScienceCommandButton(group);
	}

	protected void createSendGuestScienceCommandButton(Composite group) {
		sendGuestScienceCommandButton = new CommandButtonForGuestScienceTab(group, SWT.NONE);
		sendGuestScienceCommandButton.setText("Send Command");
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).grab(true, false).applyTo(sendGuestScienceCommandButton);
		sendGuestScienceCommandButton.setButtonLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		sendGuestScienceCommandButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				for(int i=0; i<selected.length; i++) {
					if(selected[i] != null) {
						String apkName = guestScienceStateManager.getApkLongName(selected[i], commandApkCombo.getText());
						commandPublishers[i].sendGuestScienceCommand(apkName, commandText.getText());
					}
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)  {/**/}
		});
	}

	/** Given an apk name, return the commands for that apk that overlap for all selected Astrobees */
	protected Set<GuestScienceCommandGds> findTemplatesInCommon(String apkName) {
		Vector<HashSet<GuestScienceCommandGds>> allCmdsOnAllAstrobees = new Vector<HashSet<GuestScienceCommandGds>>();

		for(int i=0; i<selected.length; i++) {
			if(selected[i] != null) {
				GuestScienceApkGdsRunning apkInfo;
				apkInfo = guestScienceStateManager.getGuestScienceApk(selected[i], commandApkCombo.getText());

				HashSet<GuestScienceCommandGds> cmdsOnOneAstrobee = new HashSet<GuestScienceCommandGds>();
				cmdsOnOneAstrobee.addAll(apkInfo.getGuestScienceCommands());
				allCmdsOnAllAstrobees.add(cmdsOnOneAstrobee);
			}
		}

		if(allCmdsOnAllAstrobees.size() == 2) {
			allCmdsOnAllAstrobees.get(0).retainAll(allCmdsOnAllAstrobees.get(1));

		} else if(allCmdsOnAllAstrobees.size() == 3) {
			allCmdsOnAllAstrobees.get(0).retainAll(allCmdsOnAllAstrobees.get(1));
			allCmdsOnAllAstrobees.get(0).retainAll(allCmdsOnAllAstrobees.get(2));
		}
		return allCmdsOnAllAstrobees.get(0);
	}

	protected String getNamesOfSelected() {
		StringBuilder sb = new StringBuilder();
		boolean nameBefore = false;

		for(int i=0; i<selected.length; i++) {
			if(selected[i] != null) {
				if(nameBefore) {
					sb.append(", ");
				}
				sb.append(selected[i].name());
				nameBefore = true;
			}
		}
		if(!nameBefore) {
			return NO_SELECTION_STRING;
		}
		return sb.toString();
	}

	@Inject @Optional
	public void acceptManager1(@Named(FreeFlyerStrings.GUEST_SCIENCE_ASTROBEE_STATE_MANAGER_1) GuestScienceAstrobeeStateManager m) {
		managers[0] = m;
		commandPublishers[0] = CommandPublisher.getInstance(m.getAgent());
		initializeAgentMapsAndListenToManager(m);
	}

	@Inject @Optional
	public void acceptManager2(@Named(FreeFlyerStrings.GUEST_SCIENCE_ASTROBEE_STATE_MANAGER_2) GuestScienceAstrobeeStateManager m) {
		managers[1] = m;
		commandPublishers[1] = CommandPublisher.getInstance(m.getAgent());
		initializeAgentMapsAndListenToManager(m);
	}

	@Inject @Optional
	public void acceptManager3(@Named(FreeFlyerStrings.GUEST_SCIENCE_ASTROBEE_STATE_MANAGER_3) GuestScienceAstrobeeStateManager m) {
		managers[2] = m;
		commandPublishers[2] = CommandPublisher.getInstance(m.getAgent());
		initializeAgentMapsAndListenToManager(m);
	}

	protected void initializeAgentMapsAndListenToManager(GuestScienceAstrobeeStateManager m) {
		astrobeeInStateToAcceptUpload.put(m.getAgent(), false);
		haveControlOf.put(m.getAgent(), false);
		astrobeeReadyToRun.put(m.getAgent(), false);
		m.addListener(this, MessageType.ACCESSCONTROL_STATE_TYPE);
		m.addListener(this, MessageTypeExtAstro.AGENT_STATE_TYPE);
	}

	@Inject @Optional
	public void acceptGuestScienceAgent1(@Named(FreeFlyerStrings.SELECTED_GUEST_SCIENCE_1) Agent a) {
		selected[0] = a;
		onAstrobeeSelectionChange(a);
	}

	@Inject @Optional
	public void acceptGuestScienceAgent2(@Named(FreeFlyerStrings.SELECTED_GUEST_SCIENCE_2) Agent a) {
		selected[1] = a;
		onAstrobeeSelectionChange(a);
	}

	@Inject @Optional
	public void acceptGuestScienceAgent3(@Named(FreeFlyerStrings.SELECTED_GUEST_SCIENCE_3) Agent a) {
		selected[2] = a;
		onAstrobeeSelectionChange(a);
	}

	protected void onAstrobeeSelectionChange(Agent a) {
		part.setLabel("Commanding for " + getNamesOfSelected());

		updateOperatingState(a);
		updateButtonEnablements();
		repopulateApkCombos();
		updateLoadAndRunButtonLabels();
	}
	
	protected void updateLoadAndRunButtonLabels() {
		if(numberOfSelectedAstrobees() == 1) {
			loadButton.setText(SINGLE_LOAD_BUTTON_LABEL);
			runButton.setText(SINGLE_RUN_BUTTON_LABEL);
		} else {
			loadButton.setText(MULTI_LOAD_BUTTON_LABEL);
			runButton.setText(MULTI_RUN_BUTTON_LABEL);
		}
	}
	
	protected boolean stringArraysAreEqual(String[] arr1, String[] arr2) {
		if(arr1.length != arr2.length) {
			return false;
		}
		for(int i=0; i<arr1.length; i++) {
			if(!arr1[i].equals(arr2[i])) {
				return false;
			}
		}
		return true;
	}

	protected void repopulateApkCombos() {
		// for the selected Astrobees
		String[] oldListOfApks = commandApkCombo.getItems();
		
		String[] newListOfApks = guestScienceStateManager.getApkNamesForSelectedAstrobees();
		
		if(stringArraysAreEqual(oldListOfApks, newListOfApks)) {
			return;
		}
		
		if(newListOfApks[0].isEmpty()) {
			return;
		}
		
		if(apkCombo != null) {
			apkCombo.setItems(newListOfApks);
		}
		commandApkCombo.setItems(newListOfApks);
	}

	protected void updateButtonEnablements() {

		if(atLeastOneAstrobeeSelected()) {
			//pick the lowest astrobee state
			int minimumLevelOfControl = 3;
			for(Agent a : selected){
				if(a == null) {
					continue;
				}
				if(astrobeeAggregateState.containsKey(a)){
					accessControlName = astrobeeAggregateState.get(a).getAccessControl();

					if(!accessControlName.equals(myId) && minimumLevelOfControl > 1){ // don't have control
						disableAllButtons();
						grabControlButton.setCompositeEnabled(true);
						enableAndNameStopButton();
						minimumLevelOfControl = 1;
					}else if(minimumLevelOfControl > 2){ // have control
						disableAllButtons();
						enableAppropriateButtonsForHavingControl();
						minimumLevelOfControl = 2;
					}
				}
			} 
		} else {
			disableAllButtons();
		}
	}
	
	protected void enableAndNameStopButton() {
		stopButton.setCompositeEnabled(true);
		
		if(numberOfSelectedAstrobees() > 1) {
			stopButton.setText(MUTLI_STOP_BUTTON_LABEL);
		} else if(numberOfSelectedAstrobees() > 0) {
			stopButton.setText(SINGLE_STOP_BUTTON_LABEL);
		}  else if(numberOfSelectedAstrobees() == 0) {
			stopButton.setCompositeEnabled(false);
		}
	}
	
	protected void enableAppropriateButtonsForHavingControl() {
		enableAndNameStopButton();
		
		if(apkCombo != null) {
			apkCombo.setEnabled(true);
		}
		commandApkCombo.setEnabled(true);
		
		if(!commandApkCombo.getText().isEmpty()) {
			templateCombo.setEnabled(true);
		} else {
			templateCombo.setEnabled(false);
		}
		
		if(allSelectedAstrobeesInStateToAcceptUpload()) {
			loadButton.setCompositeEnabled(true);
		} else {
			loadButton.setCompositeEnabled(false);
		}

		if(allSelectedAstrobeesReadyToRun()) {
			runButton.setCompositeEnabled(true);
		} else {
			runButton.setCompositeEnabled(false);
		}
		enableStartStopApkButtons();
		enableSendCustomApkCommandButton();
		repopulateApkCombos();
	}

	protected void disableAllButtons() {
		grabControlButton.setCompositeEnabled(false);
		sendGuestScienceCommandButton.setCompositeEnabled(false);
		loadButton.setCompositeEnabled(false);
		runButton.setCompositeEnabled(false);
		stopButton.setCompositeEnabled(false);
		if(startApkButton != null) {
			startApkButton.setCompositeEnabled(false);
			stopApkButton.setCompositeEnabled(false);
		}
		if(apkCombo != null) {
			apkCombo.setEnabled(false);
		}
		if(commandApkCombo != null) {
			commandApkCombo.setEnabled(false);
	
			templateCombo.setEnabled(false);
		}
	}

	protected boolean haveControlOfAllSelected() {
		for(int i=0; i<selected.length; i++) {
			if(selected[i] != null) {
				if(haveControlOf.get(selected[i]) == null || haveControlOf.get(selected[i]) != true) {
					return false;
				}
			}
		}
		return true;
	}

	protected boolean allSelectedAstrobeesInStateToAcceptUpload() {
		if(!atLeastOneAstrobeeSelected()) {
			return false;
		}
		for(int i=0; i<selected.length; i++) {
			if(selected[i] != null) {
				Boolean yes = astrobeeInStateToAcceptUpload.get(selected[i]);
				if(yes == null || yes == false) {
					return false;
				}
			}
		}
		return true;
	}

	protected boolean allSelectedAstrobeesReadyToRun() {
		if(!atLeastOneAstrobeeSelected()) {
			return false;
		}
		for(int i=0; i<selected.length; i++) {
			if(selected[i] != null) {
				Boolean yes = astrobeeReadyToRun.get(selected[i]);
				if(yes == null || yes == false) {
					return false;
				}

			}
		}
		return true;
	}

	protected boolean atLeastOneAstrobeeSelected() {
		for(int i=0; i<selected.length; i++) {
			if(selected[i] != null) {
				return true;
			}
		}
		return false;
	}
	
	protected int numberOfSelectedAstrobees() {
		int numSelected = 0;
		for(int i=0; i<selected.length; i++) {
			if(selected[i] != null) {
				numSelected++;
			}
		}
		return numSelected;
	}

	protected void enableSendCustomApkCommandButton() {
		if(commandText == null) {
			if(!commandApkCombo.getText().isEmpty() ) {
				sendGuestScienceCommandButton.setCompositeEnabled(true);
			} else {
				sendGuestScienceCommandButton.setCompositeEnabled(false);
			}
		}
		else {
			if(!commandApkCombo.getText().isEmpty() && !commandText.getText().isEmpty()) {
				sendGuestScienceCommandButton.setCompositeEnabled(true);
			} else {
				sendGuestScienceCommandButton.setCompositeEnabled(false);
			}
		}
	}

	protected void enableStartStopApkButtons() {
		if(startApkButton == null || stopApkButton == null) {
			return;
		}

		boolean enableStart = false;
		boolean enableStop = false;

		if(!apkCombo.getText().equals("")) {
			for(int i=0; i<selected.length; i++) {
				if(selected[i] != null) {
					boolean running = guestScienceStateManager.isApkRunning(selected[i], apkCombo.getText());
					if(running) {
						enableStop = true;
					} else {
						enableStart = true;
					}
				}
			}
		}
		startApkButton.setCompositeEnabled(enableStart);
		stopApkButton.setCompositeEnabled(enableStop);
	}

	public void onAstrobeeStateChange(AggregateAstrobeeState aggregateState) {

		astrobeeAggregateState.put(aggregateState.getAgent(), aggregateState);

		if(grabControlButton == null || grabControlButton.isDisposed()) {
			return;
		}

		Display.getDefault().asyncExec(new Runnable() {
			public void run() {

				accessControlName = aggregateState.getAccessControl();

				if( !atLeastOneAstrobeeSelected() ) { 
					return;
				}

				Agent agent = aggregateState.getAgent();
				boolean iHaveControl = aggregateState.getAccessControl().equals(myId);
				if(!iHaveControl) {
					if(haveControlOf.get(agent)) {
						haveControlOf.put(agent, false);
					}
				} else {
					if(!haveControlOf.get(agent)) {
						haveControlOf.put(agent, true);
					}
				}

				if(aggregateState.getAstrobeeState() == null || aggregateState.getAstrobeeState().getOperatingState() == null) {
					return;
				}
				updateOperatingState(agent);
				updateButtonEnablements();

			}
		});
	}

	public void updateOperatingState(Agent agent){
		OperatingState operatingState = null;
		ExecutionState planExecutionState = null;
		for(GuestScienceAstrobeeStateManager asm : managers){
			if(asm != null){
				if(asm.getAgent().equals(agent)){
					operatingState = asm.getAggregateAstrobeeState().getAstrobeeState().getOperatingState();
					planExecutionState = asm.getAggregateAstrobeeState().getAstrobeeState().getPlanExecutionState();
					break;
				}
			}
		}

		if(operatingState != null){

			switch(operatingState) {
			case READY:
				if(!astrobeeInStateToAcceptUpload.get(agent)) {
					astrobeeInStateToAcceptUpload.put(agent, true);
				}
				break;

			case PLAN_EXECUTION:
			case TELEOPERATION:
			case FAULT:
			case AUTO_RETURN:
				if(astrobeeReadyToRun.get(agent)
						|| astrobeeInStateToAcceptUpload.get(agent)) {
					astrobeeReadyToRun.put(agent, false);
					astrobeeInStateToAcceptUpload.put(agent, false);
				}
				break;
			}
		}

		if(planExecutionState != null){

			switch(planExecutionState) {
			case IDLE:
				if(astrobeeReadyToRun.get(agent)
						|| !astrobeeInStateToAcceptUpload.get(agent)) {
					astrobeeReadyToRun.put(agent, false);
					astrobeeInStateToAcceptUpload.put(agent, true);
				}
				break;
			case EXECUTING:
			case ERROR:
				if(astrobeeReadyToRun.get(agent)
						|| astrobeeInStateToAcceptUpload.get(agent)) {
					astrobeeReadyToRun.put(agent, false);
					astrobeeInStateToAcceptUpload.put(agent, false);
				}
				break;
			case PAUSED:
				if(!astrobeeReadyToRun.get(agent)
						|| !astrobeeInStateToAcceptUpload.get(agent)) {
					astrobeeReadyToRun.put(agent, true);
					astrobeeInStateToAcceptUpload.put(agent, true);
				}
				break;
			}
		}
	}

	public void onGuestScienceStateChange(GuestScienceStateManager manager) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				enableStartStopApkButtons();
			}
		});
	}

	@PreDestroy
	public void preDestroy() {
		for(int i=0; i<NUM_BEES; i++) {
			if(managers[i] != null) {
				managers[i].removeListener(this);
			}
		}
		guestScienceStateManager.removeListener(this);
	}

	public void onGuestScienceConfigChange(GuestScienceStateManager manager) {
		// TODO Auto-generated method stub

	}

	public void onGuestScienceDataChange(GuestScienceStateManager manager, String apkName, String topic) {
		// TODO Auto-generated method stub

	}

	protected Composite setupInnerComposite(Composite parent, int cellsAcross, int alignment) {
		Composite innerComposite = new Composite(parent, SWT.None);
		innerComposite.setLayout(new GridLayout(cellsAcross,false));
		GridData compositeGd = new GridData(alignment);
		innerComposite.setLayoutData(compositeGd);
		return innerComposite;
	}

	@Override
	public void activeAgentSetChanged() {
		for(Agent a : selected){
			if(a == null)
				continue;
			if(!ActiveAgentSet.contains(a)) {
				astrobeeInStateToAcceptUpload.put(a, false);
				astrobeeReadyToRun.put(a, false);
			}
		}
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
