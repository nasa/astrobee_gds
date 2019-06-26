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
import gov.nasa.arc.irg.freeflyer.rapid.state.AstrobeeStateListener;
import gov.nasa.arc.irg.freeflyer.rapid.state.GuestScienceAstrobeeStateManager;
import gov.nasa.arc.irg.plan.ui.io.EnlargeableButton;
import gov.nasa.arc.irg.plan.ui.io.WorkbenchConstants;
import gov.nasa.arc.irg.util.ui.ColorProvider;
import gov.nasa.arc.verve.freeflyer.workbench.helpers.SelectedAgentConnectedListener;
import gov.nasa.arc.verve.freeflyer.workbench.helpers.SmartDockAgentConnectedRegistry;
import gov.nasa.arc.verve.freeflyer.workbench.utils.AckListener;
import gov.nasa.arc.verve.freeflyer.workbench.widget.helpers.CommandButtonForDock;
import gov.nasa.rapid.idl.ext.astrobee.message.MessageTypeExtAstro;
import gov.nasa.rapid.v2.e4.Rapid;
import gov.nasa.rapid.v2.e4.agent.ActiveAgentSet;
import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.e4.agent.IActiveAgentSetListener;
import gov.nasa.rapid.v2.e4.message.IRapidMessageListener;
import gov.nasa.rapid.v2.e4.message.MessageType;
import gov.nasa.rapid.v2.e4.message.collector.RapidMessageCollector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import rapid.ACCESSCONTROL_METHOD_REQUESTCONTROL;
import rapid.ADMIN_METHOD_SHUTDOWN;
import rapid.ext.astrobee.ACCESSCONTROL;
import rapid.ext.astrobee.DockState;

public class DockingStationStatusAndCommandingPart implements IRapidMessageListener,
SelectedAgentConnectedListener, IActiveAgentSetListener, AstrobeeStateListener {
	//private static final Logger logger = Logger.getLogger(DockingStationStatusAndCommandingPart.class);
	
	protected String myId;
	protected String participantId = Rapid.PrimaryParticipant;
	protected CommandButtonForDock wakeButton, wakeSafeButton;
	protected Combo wakeBerthCombo, liveBeesCombo;
	protected DockState dockState;

	protected EnlargeableButton grabControlButton, hibernateButton;

	protected Label berthOneOccupantLabel, berthOneStatusLabel;
	protected Label berthTwoOccupantLabel, berthTwoStatusLabel;

	protected final Color whiteColor = ColorProvider.get(255,255,255);
	protected final Color cyanColor = ColorProvider.INSTANCE.cyan;

	protected final String UNKNOWN_STRING = "Unknown";
	protected final String UNPOWERED_STRING = "Unpowered";
	protected final String VACANT_STRING = "Vacant";
	protected final String AWAKE_STRING = "Awake";
	protected final String HIBERNATING_STRING = "Hibernating";

	protected final int NUM_BEES = 3;
	protected Map<Agent,GuestScienceAstrobeeStateManager> managers = new HashMap<Agent,GuestScienceAstrobeeStateManager>();
	protected Map<Agent,Boolean> haveControlOf = new HashMap<Agent,Boolean>();
	protected Agent selectedLiveBee;
	protected IEclipseContext context;
	
	@Inject
	public DockingStationStatusAndCommandingPart(Composite parent, MApplication application) {
		myId = Agent.getEgoAgent().name();
		context = application.getContext();
		constructComposite(parent);
		SmartDockAgentConnectedRegistry.addListener(this);
		ActiveAgentSet.INSTANCE.addListener(this);
	}

	protected void constructComposite(Composite parent) {
		GridLayout gl0 = new GridLayout(1, false);
		parent.setLayout(gl0);

		createBerthOneGroup(parent);
		createBerthTwoGroup(parent);

		createWakeCommandGroup(parent);
		createHibernateCommandGroup(parent);
	}

	protected void createBerthOneGroup(Composite parent) {
		Group group = new Group(parent, SWT.SHADOW_IN);
		group.setText("Left Berth");
		GridData nameData = new GridData(SWT.FILL, SWT.TOP, true, false);
		group.setLayoutData(nameData);
		GridLayout gl = new GridLayout(2, true);
		group.setLayout(gl);

		Label occupantTitle = new Label(group, SWT.None);
		occupantTitle.setText("Occupant");

		Label statusTitle = new Label(group, SWT.None);
		statusTitle.setText("Status");

		berthOneOccupantLabel = new Label(group, SWT.None);
		berthOneOccupantLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		berthOneOccupantLabel.setBackground(whiteColor);

		berthOneStatusLabel = new Label(group, SWT.None);
		berthOneStatusLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		berthOneStatusLabel.setBackground(whiteColor);
	}

	protected void createBerthTwoGroup(Composite parent) {
		Group group = new Group(parent, SWT.SHADOW_IN);
		group.setText("Right Berth");
		GridData nameData = new GridData(SWT.FILL, SWT.TOP, true, false);
		group.setLayoutData(nameData);
		GridLayout gl = new GridLayout(2, true);
		group.setLayout(gl);

		Label occupantTitle = new Label(group, SWT.None);
		occupantTitle.setText("Occupant");

		Label statusTitle = new Label(group, SWT.None);
		statusTitle.setText("Status");

		berthTwoOccupantLabel = new Label(group, SWT.None);
		berthTwoOccupantLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		berthTwoOccupantLabel.setBackground(whiteColor);

		berthTwoStatusLabel = new Label(group, SWT.None);
		berthTwoStatusLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		berthTwoStatusLabel.setBackground(whiteColor);
	}

	protected void createWakeCommandGroup(Composite parent) {
		Group group = new Group(parent, SWT.SHADOW_IN);
		group.setText("Wake Commanding");
		GridData nameData = new GridData(SWT.FILL, SWT.TOP, true, false);
		group.setLayoutData(nameData);
		GridLayout gl = new GridLayout(2, true);
		group.setLayout(gl);

		constructWakeBerthCombo(group);
		constructWakeButton(group);
		if(WorkbenchConstants.isFlagPresent(WorkbenchConstants.SHOW_ENGINEERING_CONFIGURATION_STRING)) {
			new Label(group, SWT.NONE);
			constructWakeSafeButton(group);
		}
	}

	protected void createHibernateCommandGroup(Composite parent) {
		Group group = new Group(parent, SWT.SHADOW_IN);
		group.setText("Hibernate Commanding");
		GridData nameData = new GridData(SWT.FILL, SWT.TOP, true, false);
		group.setLayoutData(nameData);
		GridLayout gl = new GridLayout(2, true);
		group.setLayout(gl);

		createLiveBeesCombo(group);
		createGrabControlButton(group);
		new Label(group, SWT.None);
		createHibernateButton(group);
		
		populateLiveBeesCombo();
		updateButtonEnablements();
	}

	protected void createLiveBeesCombo(Composite parent) {
		liveBeesCombo = new Combo(parent, SWT.READ_ONLY);
		liveBeesCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		liveBeesCombo.setToolTipText("Select Astrobee");
		liveBeesCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				if ( liveBeesCombo.getText().isEmpty()) {
					return;
				}
				newLiveBeeSelected();
			}
		});
	}

	protected void newLiveBeeSelected() {
		updateButtonEnablements();
		context.set(FreeFlyerStrings.SELECTED_OVERVIEW_BEE, selectedLiveBee);
	}
	
	@Override
	public void activeAgentSetChanged() {
		populateLiveBeesCombo();
//		updateButtonEnablements();
	}

	protected void populateLiveBeesCombo() {

		final String agentNamesArray[] = makeListOfAgentNames();

		Display.getDefault().asyncExec(new Runnable() {
			public synchronized void run() {

				liveBeesCombo.setItems(agentNamesArray);
				liveBeesCombo.select(0); // select the first string
				newLiveBeeSelected();
			}
		});
	}

	/**
	 * 
	 * @return "Select Astrobee" followed by names of bees (no smartdock)
	 */
	protected String[] makeListOfAgentNames() {
		final List<Agent> agents = ActiveAgentSet.asList();
		final List<String> agentStrings = new ArrayList<String>();

		for(final Agent a : agents){
			if(!a.equals(Agent.SmartDock)){
				agentStrings.add(a.name());
			}
		}
		return agentStrings.toArray(new String[agentStrings.size()]);
	}

	protected void createGrabControlButton(Composite parent) {
		grabControlButton = new EnlargeableButton(parent, SWT.NONE);
		grabControlButton.setText("Grab Control");
		grabControlButton.setToolTipText(WorkbenchConstants.GRAB_CONTROL_TOOLTIP);
		grabControlButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		grabControlButton.setButtonLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		FontDescriptor boldDescriptor = FontDescriptor.createFrom(grabControlButton.getFont()).setStyle(SWT.BOLD);
		Font boldFont = boldDescriptor.createFont(grabControlButton.getDisplay());
		grabControlButton.setFont( boldFont );

		grabControlButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				managers.get(selectedLiveBee).startRequestingControl();
				CommandPublisher.getInstance(selectedLiveBee).sendGenericNoParamsCommand(
						ACCESSCONTROL_METHOD_REQUESTCONTROL.VALUE,
						ACCESSCONTROL.VALUE);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// no-op
			}
		});
		grabControlButton.setEnabled(false);
	}

	protected void createHibernateButton(Composite parent) {
		hibernateButton = new EnlargeableButton(parent, SWT.NONE);
		hibernateButton.setText(WorkbenchConstants.hibernateString);
		hibernateButton.setToolTipText(WorkbenchConstants.HIBERNATE_TOOLTIP);

		FontDescriptor boldDescriptor = FontDescriptor.createFrom(hibernateButton.getFont()).setStyle(SWT.BOLD);
		Font boldFont = boldDescriptor.createFont(hibernateButton.getDisplay());
		hibernateButton.setFont( boldFont );

		hibernateButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		hibernateButton.setButtonLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		hibernateButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				CommandPublisher.getInstance(selectedLiveBee).sendGenericNoParamsCommand(ADMIN_METHOD_SHUTDOWN.VALUE, ACCESSCONTROL.VALUE);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// no-op
			}
		});
		hibernateButton.setEnabled(false);
	}

	@Inject @Optional
	public void acceptManager1(@Named(FreeFlyerStrings.GUEST_SCIENCE_ASTROBEE_STATE_MANAGER_1) GuestScienceAstrobeeStateManager m) {
		initializeAgentMapsAndListenToManager(m);
	}

	@Inject @Optional
	public void acceptManager2(@Named(FreeFlyerStrings.GUEST_SCIENCE_ASTROBEE_STATE_MANAGER_2) GuestScienceAstrobeeStateManager m) {
		initializeAgentMapsAndListenToManager(m);
	}

	@Inject @Optional
	public void acceptManager3(@Named(FreeFlyerStrings.GUEST_SCIENCE_ASTROBEE_STATE_MANAGER_3) GuestScienceAstrobeeStateManager m) {
		initializeAgentMapsAndListenToManager(m);
	}

	protected void initializeAgentMapsAndListenToManager(GuestScienceAstrobeeStateManager m) {
		managers.put(m.getAgent(), m);
		haveControlOf.put(m.getAgent(), false);
		m.addListener(this, MessageType.ACCESSCONTROL_STATE_TYPE);
		//		m.addListener(this, MessageTypeExtAstro.AGENT_STATE_TYPE);
	}

	protected void constructWakeBerthCombo(Composite parent) {
		wakeBerthCombo = new Combo(parent, SWT.READ_ONLY);
		wakeBerthCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		wakeBerthCombo.setToolTipText("Select Astrobee to Wake");
	}

	protected void constructWakeButton(Composite parent) {
		wakeButton = new CommandButtonForDock(parent, SWT.NONE);
		wakeButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		wakeButton.setButtonLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		wakeButton.setText("Wake");
		wakeButton.setToolTipText("Wake Selected Astrobee");
		wakeButton.setCompositeEnabled(true);

		wakeButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int berth = 0;

				String toMatch = wakeBerthCombo.getText();
				if(toMatch.contains(berthOneOccupantLabel.getText())) {
					berth = 1;
				}
				else if(toMatch.contains(berthTwoOccupantLabel.getText())) {
					berth = 2;
				}
				CommandPublisher.getInstance(Agent.SmartDock).sendSmartDockWakeCommand(berth);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// no-op
			}
		});
	}
	
	protected void constructWakeSafeButton(Composite parent) {
		wakeSafeButton = new CommandButtonForDock(parent, SWT.NONE);
		wakeSafeButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		wakeSafeButton.setButtonLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		wakeSafeButton.setText("Wake Safe");
		wakeSafeButton.setToolTipText("Wake Selected Astrobee in Safe Mode");
		wakeSafeButton.setCompositeEnabled(true);

		wakeSafeButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int berth = 0;

				String toMatch = wakeBerthCombo.getText();
				if(toMatch.contains(berthOneOccupantLabel.getText())) {
					berth = 1;
				}
				else if(toMatch.contains(berthTwoOccupantLabel.getText())) {
					berth = 2;
				}
				CommandPublisher.getInstance(Agent.SmartDock).sendSmartDockWakeSafeCommand(berth);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// no-op
			}
		});
	}

	@Override
	public void onRapidMessageReceived(Agent agent, MessageType msgType,
			Object msgObj, Object cfgObj) {
		if(MessageTypeExtAstro.DOCK_STATE_TYPE.equals(msgType)) {
			dockState = (DockState) msgObj;
			updateFromDockState();
		}
	}

	protected void updateFromDockState() {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				updateBerthOneFields();
				updateBerthTwoFields();

				String[] wakeableBees = makeListOfWakeableBees();

				wakeBerthCombo.setItems(wakeableBees);
				if(wakeableBees.length > 0) {
					wakeBerthCombo.select(0);
					wakeButton.setCompositeEnabled(true);
				} else {
					wakeButton.setCompositeEnabled(false);
				}

			}
		});
	}

	protected void updateBerthOneFields() {
		if(dockState.berthOne.occupied) {
			if(dockState.berthOne.astrobeeName.isEmpty()) {
				// if we don't know the name, it is unpowered
				berthOneOccupantLabel.setText(UNKNOWN_STRING);
				berthOneStatusLabel.setText(UNPOWERED_STRING);
				return;
			} 

			berthOneOccupantLabel.setText(dockState.berthOne.astrobeeName);

			if(dockState.berthOne.awake) {
				berthOneStatusLabel.setText(AWAKE_STRING);
			} else {
				berthOneStatusLabel.setText(HIBERNATING_STRING);
			}

		} else {
			berthOneOccupantLabel.setText(VACANT_STRING);
			berthOneStatusLabel.setText(WorkbenchConstants.UNINITIALIZED_STRING);
		}
	}

	protected void updateBerthTwoFields() {
		if(dockState.berthTwo.occupied) {
			if(dockState.berthTwo.astrobeeName.isEmpty()) {
				berthTwoOccupantLabel.setText(UNKNOWN_STRING);
				berthTwoStatusLabel.setText(UNPOWERED_STRING);
				return;
			}

			berthTwoOccupantLabel.setText(dockState.berthTwo.astrobeeName);

			if(dockState.berthTwo.awake) {
				berthTwoStatusLabel.setText(AWAKE_STRING);
			} else {
				berthTwoStatusLabel.setText(HIBERNATING_STRING);
			}

		} else {
			berthTwoOccupantLabel.setText(VACANT_STRING);
			berthTwoStatusLabel.setText(WorkbenchConstants.UNINITIALIZED_STRING);
		}
	}

	protected String[] makeListOfWakeableBees() {
		Vector<String> vector = new Vector<String>();
		if(dockState.berthOne.occupied && !dockState.berthOne.awake) {
			if(!dockState.berthOne.astrobeeName.isEmpty()) {
				vector.add(dockState.berthOne.astrobeeName);
			}
		}

		if(dockState.berthTwo.occupied && !dockState.berthTwo.awake) {
			if(!dockState.berthTwo.astrobeeName.isEmpty()) {
				vector.add(dockState.berthTwo.astrobeeName);
			}
		}
		return vector.toArray(new String[vector.size()]);
	}

	public void unsubscribe() {
		RapidMessageCollector.instance().removeRapidMessageListener(participantId, Agent.SmartDock, this);
	}

	public void subscribe() {
		AckListener.getStaticInstance().subscribe(Agent.SmartDock);
		RapidMessageCollector.instance().addRapidMessageListener(participantId, 
				Agent.SmartDock, 
				MessageTypeExtAstro.DOCK_STATE_TYPE, 
				this);
	}

	@Override
	public void onSelectedAgentConnected() {
		subscribe();
		setAllFieldsToThisColor(whiteColor);
	}

	@Override
	public void onSelectedAgentDisconnected() {
		setAllFieldsToThisColor(cyanColor);
	}

	protected void setAllFieldsToThisColor(Color col) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				berthOneOccupantLabel.setBackground(col);
				berthOneStatusLabel.setBackground(col);
				berthTwoOccupantLabel.setBackground(col);
				berthTwoStatusLabel.setBackground(col);
			}
		});
	}

	// Must be called from Display thread
	protected void updateButtonEnablements() {
		if(liveBeesCombo.getText() == null || liveBeesCombo.getText().isEmpty() ) {
			grabControlButton.setEnabled(false);
			hibernateButton.setEnabled(false);
			selectedLiveBee = null;
			return;
		}
		selectedLiveBee = Agent.valueOf(liveBeesCombo.getText());
		if(selectedLiveBee == null) {
			grabControlButton.setEnabled(false);
			hibernateButton.setEnabled(false);
			return;
		}

		if(haveControlOf.get(selectedLiveBee) == null) {
			return;
		}

		if(haveControlOf.get(selectedLiveBee)) {
			grabControlButton.setEnabled(false);
			hibernateButton.setEnabled(true);
		} else {
			grabControlButton.setEnabled(true);
			hibernateButton.setEnabled(false);
		}
	}


	@Override
	public void onAstrobeeStateChange(AggregateAstrobeeState aggregateState) {

		if(grabControlButton == null || grabControlButton.isDisposed()) {
			return;
		}

		Display.getDefault().asyncExec(new Runnable() {
			public void run() {

				if(haveControlOf != null) {
					Agent agent = aggregateState.getAgent();
					boolean iHaveControl = aggregateState.getAccessControl().equals(myId);
					if(!iHaveControl) {
						if((haveControlOf.get(agent) != null) || haveControlOf.get(agent)) {
							haveControlOf.put(agent, false);
						}
					} else {
						if((haveControlOf.get(agent) != null) || !haveControlOf.get(agent)) {
							haveControlOf.put(agent, true);
						}
					}
				}

				if(aggregateState.getAstrobeeState() == null || aggregateState.getAstrobeeState().getOperatingState() == null) {
					return;
				}
				updateButtonEnablements();

			}
		});

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
