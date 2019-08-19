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
package gov.nasa.arc.verve.freeflyer.workbench.parts.standard;

import gov.nasa.arc.irg.freeflyer.rapid.CommandPublisher;
import gov.nasa.arc.irg.freeflyer.rapid.FreeFlyerStrings;
import gov.nasa.arc.irg.freeflyer.rapid.frequent.AstrobeeBattMinutesListener;
import gov.nasa.arc.irg.freeflyer.rapid.frequent.EpsStateHolder;
import gov.nasa.arc.irg.freeflyer.rapid.frequent.FrequentTelemetryListener;
import gov.nasa.arc.irg.freeflyer.rapid.state.AggregateAstrobeeState;
import gov.nasa.arc.irg.freeflyer.rapid.state.AstrobeeStateListener;
import gov.nasa.arc.irg.freeflyer.rapid.state.AstrobeeStateManager;
import gov.nasa.arc.irg.iss.ui.IssUiActivator;
import gov.nasa.arc.irg.plan.ui.io.WorkbenchConstants;
import gov.nasa.arc.irg.util.connection.IConnectionListener;
import gov.nasa.arc.irg.util.ui.ColorProvider;
import gov.nasa.arc.verve.freeflyer.workbench.helpers.SelectedAgentConnectedListener;
import gov.nasa.arc.verve.freeflyer.workbench.helpers.SelectedAgentConnectedRegistry;
import gov.nasa.arc.verve.freeflyer.workbench.scenario.FreeFlyerScenario;
import gov.nasa.arc.verve.freeflyer.workbench.utils.Berth;
import gov.nasa.arc.verve.freeflyer.workbench.utils.GuiUtils;
import gov.nasa.arc.verve.freeflyer.workbench.widget.helpers.CommandButton;
import gov.nasa.dds.system.DdsTask;
import gov.nasa.rapid.idl.ext.astrobee.message.MessageTypeExtAstro;
import gov.nasa.rapid.v2.e4.Rapid;
import gov.nasa.rapid.v2.e4.agent.ActiveAgentSet;
import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.e4.agent.IActiveAgentSetListener;
import gov.nasa.rapid.v2.e4.message.IRapidMessageListener;
import gov.nasa.rapid.v2.e4.message.MessageType;
import gov.nasa.rapid.v2.e4.message.collector.RapidMessageCollector;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import rapid.ACCESSCONTROL;
import rapid.ACCESSCONTROL_METHOD_REQUESTCONTROL;
import rapid.MOBILITY_METHOD_STOPALLMOTION;
import rapid.ext.astrobee.DATA;
import rapid.ext.astrobee.DATA_METHOD_STOP_RECORDING;
import rapid.ext.astrobee.MOBILITY;

public class TopBar implements IConnectionListener, AstrobeeBattMinutesListener, IActiveAgentSetListener, 
		AstrobeeStateListener, FrequentTelemetryListener, SelectedAgentConnectedListener {
	private static final Logger logger = Logger.getLogger(TopBar.class);
	private StyledText  time;
	private final Timer timer = new Timer(false); // this is the timer that will run the throttled updates
	private TimerTask updateTimerTask = null;   // this is the actual update
	private Label connectedTitle, connectedLed;
	protected Label dockConnectedTitle, dockConnectedLed;
	private Label battLabel;
	private Label controlLabel;
	protected CommandButton grabControlButton, stationKeepButton, stopRecordingButton;
	protected CommandPublisher commandPublisher;
	protected MessageType[] sampleType;
	
	protected final String ASTROBEE_SELECTOR_TOOLTIP = "Select an Astrobee to Control";
	protected final String DOCK_CONNECTED_TOOLTIP = "Connected to Docking Station";
	protected final String DOCK_DISCONNECTED_TOOLTIP = "Disconnected from Docking Station";
	private final String GRAB_CONTROL_STRING = "Grab Control";
	private final String STOP_RECORDING_STRING = "Stop Recording Data";
	protected boolean waitingToSendSetCommand = false;
	protected String participantId = Rapid.PrimaryParticipant;
	
	private final Color white, cyan, orange;

	@Inject
	FreeFlyerScenario freeFlyerScenario;

	ExecutorService threadPool = Executors.newSingleThreadExecutor();
	protected HashMap<String,Long> menuItems = new HashMap<String,Long>();
	protected String[] oldList;
	Berth berth = new Berth();
	private Combo partitionsCombo;
	protected String SELECTED_STRING = "Select Bee ...";

	protected final String unknownString = " ";

	protected Image	greenImage, grayImage, cyanImage;
	protected Object m_imageLock = new Object();
	AstrobeeStateManager astrobeeStateManager;

	protected int savedBattValue = Integer.MAX_VALUE;
	protected String savedController = "";

	//	protected boolean astrobeeSelected = false;
	// need to force update if the values are the same, but AB has disconnected and reconnected
	protected boolean forceUpdateOfControlField = true;
	protected boolean forceUpdateOfBattField = true;
	protected String myId;
	protected EpsStateHolder epsStateHolder;
	protected boolean selectedAgentIsConnected = false;

	@Inject
	protected MApplication application;

	@Inject UISynchronize sync;

	private static SimpleDateFormat dateFormatUTC = new SimpleDateFormat("ddMMMyy HH:mm:ss");
	{
		dateFormatUTC.setTimeZone(TimeZone.getTimeZone("UTC"));
	}

	@Inject 
	public TopBar(final Composite parent, final Display display, UISynchronize sync) {
		this.sync = sync;
		myId = Agent.getEgoAgent().name();

		sampleType = new MessageType[] {
				MessageTypeExtAstro.COMPRESSED_FILE_ACK_TYPE,
		};

		white = display.getSystemColor(SWT.COLOR_WHITE);
		cyan = display.getSystemColor(SWT.COLOR_CYAN);
		orange = ColorProvider.get(255, 165, 0);

		loadConnectionImages();

		final Composite insideComposite = new Composite(parent, SWT.NONE);
		createComposite(insideComposite);

		updateTimerTask = new TimerTask() {

			@Override
			public void run() {
				try {
					if (display != null && !display.isDisposed()){
						display.asyncExec(new Runnable() {
							public void run() {
								updateTime();
							}
						});
					}
				} catch (final SWTException e){
					// hi
				}
			}
		};
		// update time once per second
		timer.schedule(updateTimerTask, 0, 1000);
		ActiveAgentSet.INSTANCE.addListener(this);
	}

	protected void createComposite(final Composite parent) {
		int cellsAcross = 10;

		parent.setLayout(new GridLayout(cellsAcross,false));

		createPartitionsCombo(parent);
		createCommCircle(parent);
		createBattLabel(parent);
		createControlLabel(parent);
		createGrabControlButton(parent);
		createStationKeepButton(parent);
		createStopRecordingButton(parent);

		createCenterSpacer(parent);

		createRightSideFields(parent);

		parent.layout();
		SelectedAgentConnectedRegistry.addListener(this);
	}

	protected void createRightSideFields(final Composite parent) {
		createDockCommCircle(parent);
		createGpsLabel(parent);
	}

	protected void createPartitionsCombo(final Composite parent) {
		partitionsCombo = new Combo(parent, SWT.READ_ONLY);
		partitionsCombo.setData("gov.nasa.arc.irg.iss.ui.widget.key",
				"Partitions");
		partitionsCombo.setToolTipText(ASTROBEE_SELECTOR_TOOLTIP);

		populatePartitionsCombo();

		partitionsCombo.pack(true);
		partitionsCombo.select(0);
		final GridData gd = new GridData(SWT.FILL, SWT.FILL, false, false);
		gd.widthHint = 150;
		partitionsCombo.setLayoutData(gd);

		partitionsCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				putNewlySelectedItemInContextAsPrimaryBee();
			}
		});
		parent.layout(true);
		parent.pack();
		parent.update();
	}

	public void putNewlySelectedItemInContextAsPrimaryBee(){
		if (SELECTED_STRING.equals(partitionsCombo.getText()) || partitionsCombo.getText().isEmpty()) {
			return;
		}
		onSelectedAgentConnected();

		// don't think I need next line anymore
		TopBarDataHolder.getInstance().setSavedBee(Agent.valueOf(partitionsCombo.getText()));

		freeFlyerScenario.setPrimaryRobot(TopBarDataHolder.getInstance().getSavedBee());
		application.getContext().remove(FreeFlyerStrings.PRIMARY_BEE);
		application.getContext().set(FreeFlyerStrings.PRIMARY_BEE,TopBarDataHolder.getInstance().getSavedBee());
	}

	@Override
	public void activeAgentSetChanged() {
		populatePartitionsCombo();
		updateDockLed();
	}

	/** returns true if it selected the bee (ie, was only one available and no savedBee) */
	protected boolean selectOnlyBee() {
		if(TopBarDataHolder.getInstance().getSavedBee() == null) { 
			if(partitionsCombo.getItemCount() ==2) {
				partitionsCombo.select(1); // the first one is "Select Astrobee..."
				TopBarDataHolder.getInstance().setSavedBee(Agent.valueOf(partitionsCombo.getItem(1)));
				return true;
			}
		}
		return false;
	}

	/** returns true if found savedBee to select */
	protected boolean selectSavedBee() {
		if(TopBarDataHolder.getInstance().getSavedBee() == null) { 
			return false;
		}
		String toMatch = TopBarDataHolder.getInstance().getSavedBeeName();
		for(int i=0; i<partitionsCombo.getItemCount(); i++) {
			if(partitionsCombo.getItem(i).equals(toMatch)) {
				partitionsCombo.select(i);
				return true;
			}

		}
		return false;
	}

	protected void populatePartitionsCombo() {
		//logger.info("inside populatePartitionsCombo()");
		final String agentNamesArray[] = makeListOfAgentNames();

		if(sync == null)
			return;

		sync.asyncExec(new Runnable() {
			public synchronized void run() {

				//				System.out.println("Setting names to:");
				//				for(String s : agentNamesArray) {
				//					System.out.println(s);
				//				}

				partitionsCombo.setItems(agentNamesArray);


				boolean selectedSomething = selectSavedBee();

				if(!selectedSomething) {
					selectedSomething = selectOnlyBee();
				}

				if(selectedSomething) {
					putNewlySelectedItemInContextAsPrimaryBee();
				} else {
					partitionsCombo.select(0); // select the select string
				}

				partitionsCombo.pack(true);
			}
		});
	}

	@Inject
	@Optional
	public void onAgentSelected(@Named(FreeFlyerStrings.PRIMARY_BEE) final Agent a) {
		if(a == null) {
			return;
		}
		TopBarDataHolder.getInstance().setSavedBee(a);
		setAgentFromTopBarDataHolderInCombo();
		forceUpdateOfControlField = true;
		forceUpdateOfBattField = true;
		commandPublisher = CommandPublisher.getInstance(a);
	}

	protected void setAgentFromTopBarDataHolderInCombo() {
		final String toMatch = TopBarDataHolder.getInstance().getSavedBeeName();
		final String[] agents = partitionsCombo.getItems();
		for (int i = 0; i < agents.length; i++) {
			if (agents[i].equals(toMatch)) {
				partitionsCombo.select(i);
				break;
			}
		}
	}

	protected void setAgentInCombo(final String s) {
		if(partitionsCombo == null)
			return;
		final String[] agents = partitionsCombo.getItems();
		for (int i = 0; i < agents.length; i++) {
			if (agents[i].equals(s)) {
				partitionsCombo.select(i);
				break;
			}
		}
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

		agentStrings.remove(SELECTED_STRING);
		agentStrings.add(0, SELECTED_STRING);

		return agentStrings.toArray(new String[agentStrings.size()]);
	}

	protected void updateTime(){
		if (time.isDisposed()){
			return;
		}
		time.setText(dateFormatUTC.format(new Date()));
	}

	@Override
	public void onConnect() {

		final Display display = Display.getDefault();
		if (display != null && !display.isDisposed()){
			display.asyncExec(new Runnable() {
				public void run() {
					if (TopBarDataHolder.getInstance().getSavedBee() != null) {
						if(!TopBarDataHolder.getInstance().getSavedBeeName().equals(Agent.SmartDock.name())){
							application.getContext().set(FreeFlyerStrings.PRIMARY_BEE,TopBarDataHolder.getInstance().getSavedBee());
						}
					}
				}
			});
		}
	}

	protected void updateDockLed(){
		if(ActiveAgentSet.contains(Agent.SmartDock)){
			setDockLabelToConnected();
		}else{
			setDockLedToDisconnected();
		}
	}

	@Override
	public void onDisconnect() {
		logger.debug("onDisconnect()");
		final Display display = Display.getDefault();
		if (display != null && !display.isDisposed()){
			display.asyncExec(new Runnable() {
				public void run() {
					setConnectedLabelToDisconnected();
					controlLabel.setBackground(cyan);
					battLabel.setBackground(cyan);
					selectedAgentIsConnected = false;
				}
			});
		}
	}

	@Inject
	@Optional
	public void acceptAstrobeeStateManager(final AstrobeeStateManager asm) {
		astrobeeStateManager = asm;
		astrobeeStateManager.addListener(this, MessageType.ACCESSCONTROL_STATE_TYPE);

	}

	@Inject @Optional
	public void acceptEpsStateHolder(final EpsStateHolder epsStateHolder) {
		this.epsStateHolder = epsStateHolder;
		epsStateHolder.addListener(this);
	}

	@PreDestroy
	public void preDestroy() {
		astrobeeStateManager.removeListener(this);
		epsStateHolder.removeListener(this);
		ActiveAgentSet.INSTANCE.removeListener(this);
	}

	public void onAstrobeeStateChange(final AggregateAstrobeeState aggregateAstrobeeState) {
		if(!aggregateAstrobeeState.isValid()) {
			return;
		}

		// update control fields
		if( ( !aggregateAstrobeeState.getAccessControl().equals(savedController)) || forceUpdateOfControlField )  {
			updateController(aggregateAstrobeeState.getAccessControl());
		}

		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if(grabControlButton == null || grabControlButton.isDisposed()) {
					return;
				}
				if(aggregateAstrobeeState.getAccessControl() == null || !selectedAgentIsConnected) {
					return;
				}
				if(aggregateAstrobeeState.getAccessControl().equals(myId)) {
					grabControlButton.setCompositeEnabled(false);
				} else {
					grabControlButton.setCompositeEnabled(true);
					return;
				}
				
				if(aggregateAstrobeeState.isRecordingData()) {
					stopRecordingButton.setCompositeEnabled(true);
				} else {
					stopRecordingButton.setCompositeEnabled(false);
				}

				if(aggregateAstrobeeState.getAstrobeeState() == null || aggregateAstrobeeState.getAstrobeeState().getOperatingState() == null) {
					return;
				}

				switch(aggregateAstrobeeState.getAstrobeeState().getMobilityState()) {
				case FLYING:
				case DRIFTING:
				case STOPPING:
					stationKeepButton.setCompositeEnabled(true);
					break;
				case PERCHING:
				case DOCKING:
					if(aggregateAstrobeeState.getAstrobeeState().getSubMobilityState() == 0) {
						// perched or docked
						stationKeepButton.setCompositeEnabled(false);
					} else {
						// still in the process of perching or docking
						stationKeepButton.setCompositeEnabled(true);
					}
					break;
				default:
					stationKeepButton.setCompositeEnabled(false);
					break;
				}
			}
		});
	}

	protected void updateBatteryLevel() {
		// update battery fields
		int battValue = epsStateHolder.getEpsState().getBatteryMinutes();

		if( savedBattValue != battValue || forceUpdateOfBattField ) {
			updateBattField(battValue);
		}
	}

	protected void updateController(final String newController) {
		if(savedController.equals(newController) && !forceUpdateOfControlField) {
			return;
		}
		savedController = newController;
		final Display display = Display.getDefault();
		if (display != null && !display.isDisposed()){
			display.asyncExec(new Runnable() {
				public void run() {
					controlLabel.setText(newController);
					// if we got this message, it's connected
					if(selectedAgentIsConnected) {
						controlLabel.setBackground(white);
						forceUpdateOfControlField = false;
					}
				}
			});
		}
	}

	protected void updateBattField(final int newBattValue) {
		if(savedBattValue == newBattValue && !forceUpdateOfBattField) {
			return;
		}

		final Display display = Display.getDefault();
		if (display != null && !display.isDisposed()){
			display.asyncExec(new Runnable() {
				public void run() {
					battLabel.setText(GuiUtils.convertMinutesToHHM0(newBattValue));
					if(!selectedAgentIsConnected) {
						battLabel.setBackground(cyan);
					} 
					else {
						if(newBattValue < WorkbenchConstants.LOW_BATT_THRESHOLD) {
							battLabel.setBackground(orange);
							battLabel.setToolTipText(WorkbenchConstants.LOW_BATT_TOOLTIP);
						} else {
							battLabel.setBackground(white);
							battLabel.setToolTipText(WorkbenchConstants.BATT_TOOLTIP);
						}
						forceUpdateOfBattField = false;
					}
					savedBattValue = newBattValue;

				}
			});
		}
	}

	@Override
	public void onSelectedAgentConnected() {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				selectedAgentIsConnected = true;
				setConnectedLedToConnected();
				// XXX shouldn't I turn things white or update them or something?
				controlLabel.setBackground(white);
				battLabel.setBackground(white);
				// XXX will want next time around ...unless we get late messages after this
				forceUpdateOfControlField = true;
				forceUpdateOfBattField = true;
			}
		});
	}

	@Override
	public void onSelectedAgentDisconnected() {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				setConnectedLabelToDisconnected();
				controlLabel.setBackground(cyan);
				battLabel.setBackground(cyan);

				selectedAgentIsConnected = false;
				savedController = "";
			}
		});
	}

	@Override
	public void onBattMinutesChange(int battPercent) {
		updateBattField(battPercent);
	}

	@Override
	public void onSampleUpdate(final Object sample) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				if(controlLabel == null || controlLabel.isDisposed()) {
					return;
				}
				updateBatteryLevel();
			}
		});
	}

	@Override
	public void onConfigUpdate(final Object config) {
		// TODO Auto-generated method stub

	}

	protected void loadConnectionImages() {
		synchronized(m_imageLock) {
			greenImage = IssUiActivator.getImageFromRegistry("greenCircle");
			grayImage = IssUiActivator.getImageFromRegistry("grayCircle");
			cyanImage = IssUiActivator.getImageFromRegistry("cyanCircle");
		}
	}

	protected void setDockLabelToConnected() {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				if(dockConnectedLed != null) {
					dockConnectedLed.setImage(greenImage);
					dockConnectedLed.setToolTipText(DOCK_CONNECTED_TOOLTIP);
				}
			}
		});
	}

	protected void setDockLedToDisconnected() {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				if(dockConnectedLed != null) {
					dockConnectedLed.setImage(cyanImage);
					dockConnectedLed.setToolTipText(DOCK_DISCONNECTED_TOOLTIP);
				}
			}
		});
	}

	protected void setConnectedLedToConnected() {
		connectedLed.setImage(greenImage);
		connectedLed.setToolTipText(WorkbenchConstants.CONNECTED_TOOLTIP);
	}

	protected void setConnectedLabelToDisconnected() {
		connectedLed.setImage(cyanImage);
		connectedLed.setToolTipText(WorkbenchConstants.DISCONNECTED_TOOLTIP);
	}

	protected void createCommCircle(final Composite parent) {
		final Composite innerComposite = setupInnerComposite(parent, GridData.HORIZONTAL_ALIGN_BEGINNING);

		connectedTitle = new Label(innerComposite, SWT.None);
		connectedTitle.setText("Comm ");
		connectedTitle.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		connectedLed = new Label(innerComposite, SWT.None);
		setConnectedLabelToDisconnected();
		connectedLed.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));
	}

	protected void createDockCommCircle(final Composite parent) {
		final Composite innerComposite = setupInnerComposite(parent, GridData.HORIZONTAL_ALIGN_END);

		dockConnectedTitle = new Label(innerComposite, SWT.None);
		dockConnectedTitle.setText("Docking Station ");
		final GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_END);
		dockConnectedTitle.setLayoutData(gd);
		dockConnectedLed = new Label(innerComposite, SWT.None);
		// if you use setDockLedToDisconnected(); at first it ends up disconnected
		dockConnectedLed.setImage(cyanImage);
		dockConnectedLed.setToolTipText(DOCK_DISCONNECTED_TOOLTIP);
		dockConnectedLed.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));
	}

	protected void createControlLabel(final Composite parent) {
		final Composite innerComposite = setupInnerComposite(parent, GridData.HORIZONTAL_ALIGN_BEGINNING);

		final Label controlTitle = new Label(innerComposite, SWT.None);
		controlTitle.setText("Control ");
		//		controlTitle.setToolTipText(WorkbenchConstants.CONTROL_TOOLTIP);
		final GridData titleGd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		titleGd.horizontalAlignment = SWT.END;
		controlTitle.setLayoutData(titleGd);

		controlLabel = new Label(innerComposite, SWT.NONE);
		controlLabel.setText(unknownString);
		controlLabel.setBackground(cyan);
		final GridData controlGD = new GridData(SWT.FILL, SWT.CENTER, true, false);
		controlGD.horizontalAlignment = SWT.BEGINNING;
		controlGD.widthHint = 200;
		controlLabel.setToolTipText(WorkbenchConstants.CONTROL_TOOLTIP);
		controlLabel.setLayoutData(controlGD);
	}

	protected void createBattLabel(final Composite parent) {
		final Composite innerComposite = setupInnerComposite(parent, GridData.HORIZONTAL_ALIGN_BEGINNING);

		final Label battTitle = new Label(innerComposite, SWT.None);
		battTitle.setText("Est Batt ");

		battLabel = new Label(innerComposite, SWT.RIGHT);
		battLabel.setText(unknownString);
		battLabel.setBackground(cyan);
		final GridData battGD = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		battGD.widthHint = 50;
		battLabel.setLayoutData(battGD);
		battLabel.setToolTipText(WorkbenchConstants.BATT_TOOLTIP);
	}

	protected void createGpsLabel(final Composite parent) {
		final Composite innerComposite = setupInnerComposite(parent, GridData.HORIZONTAL_ALIGN_END);

		final Label gpsTitle = new Label(innerComposite, SWT.NONE);
		final GridData titleGd = new GridData(GridData.HORIZONTAL_ALIGN_END);
		gpsTitle.setLayoutData(titleGd);
		gpsTitle.setText("GPS ");
		time = new StyledText(innerComposite, SWT.RIGHT);
		final GridData timeGD = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		time.setLayoutData(timeGD);
		time.setBackground(white);
		time.setText("ddMmmYy hh:mm:ss");
	}

	protected Composite setupInnerComposite(final Composite parent, final int alignment) {
		final Composite innerComposite = new Composite(parent, SWT.None);
		innerComposite.setLayout(new GridLayout(2,false));
		final GridData compositeGd = new GridData(alignment);
		innerComposite.setLayoutData(compositeGd);
		return innerComposite;
	}

	protected void createCenterSpacer(final Composite parent) {
		final Label centerSpacer = new Label(parent, SWT.None); 
		final GridData spacerGd = new GridData(SWT.FILL, SWT.FILL, true, true);
		centerSpacer.setLayoutData(spacerGd);
	}

	private void createGrabControlButton(Composite parent) {
		grabControlButton = new CommandButton(parent, SWT.NONE);
		grabControlButton.setText(GRAB_CONTROL_STRING);
		grabControlButton.setButtonLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		grabControlButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		grabControlButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				astrobeeStateManager.startRequestingControl();

				commandPublisher.sendGenericNoParamsCommand(
						ACCESSCONTROL_METHOD_REQUESTCONTROL.VALUE,
						ACCESSCONTROL.VALUE);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// no-op
			}
		});
	}

	protected void createStationKeepButton(Composite parent) {
		stationKeepButton = new CommandButton(parent, SWT.NONE);
		stationKeepButton.setText(WorkbenchConstants.STOP_BUTTON_TEXT);
		stationKeepButton.setButtonLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		stationKeepButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		stationKeepButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				commandPublisher.sendGenericNoParamsCommand(
						MOBILITY_METHOD_STOPALLMOTION.VALUE,
						MOBILITY.VALUE);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// no-op
			}
		});
	}

	protected void createStopRecordingButton(Composite parent) {
		stopRecordingButton = new CommandButton(parent, SWT.NONE);
		stopRecordingButton.setText(STOP_RECORDING_STRING);
		stopRecordingButton.setButtonLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		stopRecordingButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		stopRecordingButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				commandPublisher.sendGenericNoParamsCommand(
						DATA_METHOD_STOP_RECORDING.VALUE,
						DATA.VALUE);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// no-op
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
