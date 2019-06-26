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

import gov.nasa.arc.irg.freeflyer.rapid.CommandPublisher;
import gov.nasa.arc.irg.freeflyer.rapid.CompressedFilePublisher;
import gov.nasa.arc.irg.freeflyer.rapid.FreeFlyerStrings;
import gov.nasa.arc.irg.freeflyer.rapid.state.AggregateAstrobeeState;
import gov.nasa.arc.irg.freeflyer.rapid.state.AstrobeeStateListener;
import gov.nasa.arc.irg.freeflyer.rapid.state.GuestScienceAstrobeeStateManager;
import gov.nasa.arc.irg.plan.model.Plan;
import gov.nasa.arc.irg.plan.model.PlanBuilder;
import gov.nasa.arc.irg.plan.modulebay.ModuleBayPlan;
import gov.nasa.arc.irg.plan.ui.io.EnlargeableButton;
import gov.nasa.arc.irg.plan.ui.io.WorkbenchConstants;
import gov.nasa.arc.irg.util.ui.ColorProvider;
import gov.nasa.arc.verve.freeflyer.workbench.widget.helpers.CommandButtonForGuestScienceTab;
import gov.nasa.rapid.idl.ext.astrobee.message.MessageTypeExtAstro;
import gov.nasa.rapid.v2.e4.Rapid;
import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.e4.message.IRapidMessageListener;
import gov.nasa.rapid.v2.e4.message.MessageType;
import gov.nasa.rapid.v2.e4.message.collector.RapidMessageCollector;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import rapid.ext.astrobee.CompressedFileAck;
import rapid.ext.astrobee.PLAN;
import rapid.ext.astrobee.PLAN_METHOD_SET_PLAN;

public class LoadMultiplePlansDialog extends Dialog implements AstrobeeStateListener, IRapidMessageListener {
	protected String participantId = Rapid.PrimaryParticipant;
	protected String myId;
	protected EnlargeableButton[] chooseFileButton;
	protected Label[] chosenFileLabel, validLabel;
	protected File[] selectedPlanFile;
	protected String chooseFileButtonText = "File ...";
	protected String planSuffix = "fplan";
	protected int numSelected = 0;
	protected Agent[] agent;
	protected GuestScienceAstrobeeStateManager[] manager;
	protected int managersReceived = 0;
	protected final String planValidString = "Plan Valid";
	protected final String planInvalidString = "Plan Invalid";
	protected final Color colorWhite = ColorProvider.get(255,255,255);
	protected final Color green = ColorProvider.get(59,181,74);
	protected Color orange = ColorProvider.INSTANCE.orange;
	protected CommandButtonForGuestScienceTab loadButton;
	protected boolean[] validPlanChosen;
	protected Map<Agent,Boolean> haveControlOf;
	protected Map<Agent,Boolean> waitingToSendSetCommand;
	protected Map<Agent,Boolean> astrobeeInStateToAcceptUpload;

	@Inject
	public LoadMultiplePlansDialog(@Optional @Named(IServiceConstants.ACTIVE_SHELL) Shell parentShell,
			@Optional @Named(FreeFlyerStrings.SELECTED_GUEST_SCIENCE_1) Agent a1,
			@Optional @Named(FreeFlyerStrings.SELECTED_GUEST_SCIENCE_2) Agent a2,
			@Optional @Named(FreeFlyerStrings.SELECTED_GUEST_SCIENCE_3) Agent a3) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE); 

		myId = Agent.getEgoAgent().name();

		numSelected = countSelectedAgents(a1, a2, a3);
		storeSelectedAgents(a1, a2, a3);

		setUpArraysForNumberSelected(numSelected);

		subscribeToRapid();
	}

	protected void setUpArraysForNumberSelected(int num) {
		chooseFileButton = new EnlargeableButton[num];
		chosenFileLabel = new Label[num];
		validLabel = new Label[num];
		selectedPlanFile = new File[num];
		manager = new GuestScienceAstrobeeStateManager[num];
		validPlanChosen = new boolean[num];

		haveControlOf = new HashMap<Agent,Boolean>();
		waitingToSendSetCommand = new HashMap<Agent,Boolean>();
		astrobeeInStateToAcceptUpload = new HashMap<Agent,Boolean>();
		for(int i=0; i<num; i++) {
			haveControlOf.put(agent[i], true);
			waitingToSendSetCommand.put(agent[i], false);
			astrobeeInStateToAcceptUpload.put(agent[i], true); // or button to open dialog disabled.
			validPlanChosen[i] = false;
		}
	}

	protected void subscribeToRapid() {
		for(int i=0; i<numSelected; i++) {
			RapidMessageCollector.instance().addRapidMessageListener(participantId, 
					agent[i], 
					MessageTypeExtAstro.COMPRESSED_FILE_ACK_TYPE, 
					this);
		}
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite inner = new Composite(parent, SWT.None);
		GridLayout gl = new GridLayout(5, false); // we end up with one more column than the number here ?!?!?!?!
		inner.setLayout(gl);

		for(int i=0; i<numSelected; i++) {
			createFirstRow(inner, i);
			Label agentNameLabel = new Label(inner, SWT.None);
			agentNameLabel.setText(agent[i].name());
			createChooseFileButton(inner, i);
			createChosenFileLabel(inner, i);
		}

		createBottomButtons(inner);
		return parent;
	}

	// need for valid label
	protected void createFirstRow(Composite parent, int i) {
		Label leftSpacer = new Label(parent, SWT.None);
		GridData gd = new GridData(SWT.FILL, SWT.TOP, true, false);
		gd.horizontalSpan = 2;
		gd.grabExcessHorizontalSpace = true;
		leftSpacer.setLayoutData(gd);

		validLabel[i] = new Label(parent, SWT.NONE);
		validLabel[i].setText("\t\t\t");

		Label rightSpacer = new Label(parent, SWT.None);
		GridData gdR = new GridData(SWT.FILL, SWT.TOP, true, false);
		gdR.horizontalSpan = 1;
		gdR.grabExcessHorizontalSpace = true;
		rightSpacer.setLayoutData(gdR);
		rightSpacer.setText("\t\t\t\t");
		
		Label rightRightSpacer = new Label(parent, SWT.None);
		gdR = new GridData(SWT.FILL, SWT.TOP, true, false);
		gdR.horizontalSpan = 1;
		gdR.grabExcessHorizontalSpace = true;
		rightRightSpacer.setLayoutData(gdR);
		rightRightSpacer.setText("\t\t\t\t");
	}

	protected void createChooseFileButton(Composite parent, int i) {
		chooseFileButton[i] = new EnlargeableButton(parent, SWT.NONE);
		chooseFileButton[i].setText(chooseFileButtonText);
		chooseFileButton[i].setBackground(ColorProvider.get(59,82,164));
		chooseFileButton[i].addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				FileDialog dlg = new FileDialog( chooseFileButton[i].getShell(),  SWT.OPEN  );
				dlg.setFilterExtensions( new String[]{"*." + planSuffix} );
				dlg.setText("Select a Plan");
				String path = dlg.open();
				if (path == null) return;
				//String[] token = path.split(File.separator);
				//String shortname = token[token.length - 1];
				chosenFileLabel[i].setText(path);
				validateChosenPlanAndEnableUpload(path, i);
			}

			public void widgetDefaultSelected(SelectionEvent e) { //
			}
		});
	}

	public void validateChosenPlanAndEnableUpload(String newfilename, int i) {
		selectedPlanFile[i] = new File(newfilename);
		PlanBuilder<ModuleBayPlan> planBuilder = PlanBuilder.getPlanBuilder(selectedPlanFile[i], ModuleBayPlan.class, true);
		Plan plan = null;

		if(planBuilder != null) {
			plan = planBuilder.getPlan();
		}
		if(plan != null && plan.isValid()) {
			validPlanChosen[i] = true;
			validLabel[i].setText(planValidString);
			validLabel[i].setForeground(green);
			updateLoadButtonEnablement();
			return;
		}
		validLabel[i].setText(planInvalidString);
		validLabel[i].setForeground(orange);
	}

	protected void createChosenFileLabel(Composite parent, int i) {
		chosenFileLabel[i] = new Label(parent, SWT.SINGLE | SWT.BORDER);
		GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd.horizontalSpan = 3;
		gd.grabExcessHorizontalSpace = true;
		chosenFileLabel[i].setLayoutData(gd);
		chosenFileLabel[i].setBackground(colorWhite);
		chosenFileLabel[i].setText("\t\t\t\t\t\t\t\t");
	}

	protected void createBottomButtons(Composite parent) {
		Label l = new Label(parent, SWT.None);
		GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd.horizontalSpan = 4;
		gd.grabExcessHorizontalSpace = true;
		l.setLayoutData(gd);
		
		loadButton = new CommandButtonForGuestScienceTab(parent, SWT.NONE);
		loadButton.setText("Load Plans");
		loadButton.setCompositeEnabled(false);
		loadButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				for(int i=0; i<numSelected; i++) {
					if(selectedPlanFile[i].exists()){
						try {
							CompressedFilePublisher.getInstance(agent[i]).compressAndSendFile(
									WorkbenchConstants.SENDING_PLAN_STRING,
									MessageTypeExtAstro.COMPRESSED_FILE_TYPE, 
									selectedPlanFile[i]);
							waitingToSendSetCommand.put(agent[i], true);
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// no-op
			}
		});
		gd = new GridData(SWT.END, SWT.CENTER, true, false);
//		gd.horizontalSpan = 4;
		gd.grabExcessHorizontalSpace = false;
		loadButton.setLayoutData(gd);
	}

	void sendSetPlanCMD(Agent agent){
		CommandPublisher cp = CommandPublisher.getInstance(agent);
		cp.sendGenericNoParamsCommand(
				PLAN_METHOD_SET_PLAN.VALUE, 
				PLAN.VALUE);
	}

	@Inject @Optional
	public void acceptManager1(@Named(FreeFlyerStrings.GUEST_SCIENCE_ASTROBEE_STATE_MANAGER_1) GuestScienceAstrobeeStateManager m) {
		ingestAbridgedStateManager(m);
	}

	@Inject @Optional
	public void acceptManager2(@Named(FreeFlyerStrings.GUEST_SCIENCE_ASTROBEE_STATE_MANAGER_2) GuestScienceAstrobeeStateManager m) {
		ingestAbridgedStateManager(m);
	}

	@Inject @Optional
	public void acceptManager3(@Named(FreeFlyerStrings.GUEST_SCIENCE_ASTROBEE_STATE_MANAGER_3) GuestScienceAstrobeeStateManager m) {
		ingestAbridgedStateManager(m);
	}

	protected void ingestAbridgedStateManager(GuestScienceAstrobeeStateManager m) {
		if(m == null) {
			return;
		}
		for(int i=0; i<numSelected; i++) {
			if(m.getAgent().equals(agent[i])) {
				m.addListener(this, MessageType.ACCESSCONTROL_STATE_TYPE);
				m.addListener(this, MessageTypeExtAstro.AGENT_STATE_TYPE);
				manager[managersReceived++] = m;
				return;
			}
		}
	}

	public void onAstrobeeStateChange(AggregateAstrobeeState aggregateState) {
		if(chooseFileButton[0] == null || chooseFileButton[0].isDisposed()) {
			return;
		}
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if(aggregateState.getAccessControl() == null) {
					return;
				}
				Agent agent = aggregateState.getAgent();
				boolean iHaveControl = aggregateState.getAccessControl().equals(myId);
				if(!iHaveControl) {
					haveControlOf.put(agent, false);
				} else {
					haveControlOf.put(agent, true);
				}

				if(aggregateState.getAstrobeeState() == null || aggregateState.getAstrobeeState().getOperatingState() == null) {
					return;
				}

				switch(aggregateState.getAstrobeeState().getOperatingState()) {
				case READY:
					astrobeeInStateToAcceptUpload.put(agent, true);
					break;
				case TELEOPERATION:
				case PLAN_EXECUTION:
				case FAULT:
				case AUTO_RETURN:
					astrobeeInStateToAcceptUpload.put(agent, false);
					updateLoadButtonEnablement();
					return;
				}

				switch(aggregateState.getAstrobeeState().getPlanExecutionState()) {
				case IDLE: 
					astrobeeInStateToAcceptUpload.put(agent, true);
					break;
				case EXECUTING:
				case ERROR:
					astrobeeInStateToAcceptUpload.put(agent, false);
					break;
				case PAUSED:
					astrobeeInStateToAcceptUpload.put(agent, true);
					//					if(!someAstrobeeWaitingForSetCommand()) {
					//						loadButton.setCompositeEnabled(true);
					//					}
					break;
				}
				updateLoadButtonEnablement();
			}
		});
	}

	public void onRapidMessageReceived(Agent agent, MessageType msgType,
			Object msgObj, Object cfgObj) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if(msgObj instanceof CompressedFileAck){
					// assume it is the right one - can put in checks later
					if(waitingToSendSetCommand.get(agent)) {
						sendSetPlanCMD(agent);
						waitingToSendSetCommand.put(agent, false);
						if(!someAstrobeeWaitingForSetCommand()) {
							cancelPressed();
						}
					}
				}
			}
		});
	}

	protected int countSelectedAgents(Agent a1, Agent a2, Agent a3) {
		int count = 0;
		if(a1 != null) {
			count++;
		}
		if(a2 != null) {
			count++;
		}
		if(a3 != null) {
			count++;
		}
		return count;
	}

	protected void storeSelectedAgents(Agent a1, Agent a2, Agent a3) {
		agent = new Agent[numSelected];
		int ind = 0;

		if(a1 != null) {
			agent[ind++] = a1;
		}
		if(a2 != null) {
			agent[ind++] = a2;
		}
		if(a3 != null) {
			agent[ind++] = a3;
		}
	}

	protected boolean someAstrobeeWaitingForSetCommand() {
		boolean ret = false;
		for(Boolean b: waitingToSendSetCommand.values()) {
			ret |= b;
		}
		return ret;
	}

	protected void updateLoadButtonEnablement() {
		boolean enableLoadButton = haveControlOfAllAstrobees() && allPlansValid() && allAstrobeesInStateToAcceptUpload();
		loadButton.setCompositeEnabled(enableLoadButton);
	}

	protected boolean haveControlOfAllAstrobees() {
		for(Boolean b : haveControlOf.values()) {
			if(!b) {
				return false;
			}
		}
		return true;
	}

	protected boolean allAstrobeesInStateToAcceptUpload() {
		for(Boolean b : astrobeeInStateToAcceptUpload.values()) {
			if(!b) {
				return false;
			}
		}
		return true;
	}

	protected boolean allPlansValid() {
		boolean ret = true;
		for(int i=0; i<numSelected; i++) {
			ret &= validPlanChosen[i];
		}
		return ret;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Load Plans to Bee(s)");
		newShell.setEnabled(true);
	}

	@Override
	protected void cancelPressed() {
		for(int i=0; i<numSelected; i++) {
			if(manager[i] != null) {
				manager[i].removeListener(this);
				RapidMessageCollector.instance().removeRapidMessageListener(
						participantId, agent[i], this);
			}
		}
		close();
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		// don't want the default buttons
	}
}
