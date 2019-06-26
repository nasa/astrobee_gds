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
import gov.nasa.arc.irg.freeflyer.rapid.CompressedFilePublisher;
import gov.nasa.arc.irg.freeflyer.rapid.FreeFlyerStrings;
import gov.nasa.arc.irg.freeflyer.rapid.LogEntry;
import gov.nasa.arc.irg.freeflyer.rapid.LogPoster;
import gov.nasa.arc.irg.freeflyer.rapid.runningplan.RunningPlanInfo;
import gov.nasa.arc.irg.freeflyer.rapid.state.AggregateAstrobeeState;
import gov.nasa.arc.irg.freeflyer.rapid.state.AstrobeeStateListener;
import gov.nasa.arc.irg.freeflyer.rapid.state.AstrobeeStateManager;
import gov.nasa.arc.irg.plan.freeflyer.plan.FreeFlyerPlan;
import gov.nasa.arc.irg.plan.model.PlanBuilder;
import gov.nasa.arc.irg.plan.modulebay.ModuleBayPlan;
import gov.nasa.arc.irg.plan.ui.io.EnlargeableButton;
import gov.nasa.arc.irg.plan.ui.io.WorkbenchConstants;
import gov.nasa.arc.irg.util.ui.ColorProvider;
import gov.nasa.arc.verve.freeflyer.workbench.utils.Berth;
import gov.nasa.arc.verve.freeflyer.workbench.widget.helpers.CommandButton;
import gov.nasa.dds.system.DdsTask;
import gov.nasa.rapid.idl.ext.astrobee.message.MessageTypeExtAstro;
import gov.nasa.rapid.v2.e4.Rapid;
import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.e4.message.IRapidMessageListener;
import gov.nasa.rapid.v2.e4.message.MessageType;
import gov.nasa.rapid.v2.e4.message.collector.RapidMessageCollector;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;

import rapid.ACCESSCONTROL;
import rapid.ACCESSCONTROL_METHOD_REQUESTCONTROL;
import rapid.ext.astrobee.CompressedFileAck;
import rapid.ext.astrobee.PLAN;
import rapid.ext.astrobee.PLAN_METHOD_PAUSE_PLAN;
import rapid.ext.astrobee.PLAN_METHOD_RUN_PLAN;
import rapid.ext.astrobee.PLAN_METHOD_SET_PLAN;
import rapid.ext.astrobee.PlanStatus;

public class BeeCommandingOnRunPlanTab implements AstrobeeStateListener, IRapidMessageListener {
	private static final Logger logger = Logger.getLogger(BeeCommandingOnRunPlanTab.class);
	protected String participantId = Rapid.PrimaryParticipant;
	protected CommandButton runButton, pauseButton, skipButton;
	protected EnlargeableButton chooseFileButton;
	protected String chooseFileButtonText = "Select Plan...";
	protected Label chosenFileLabel;
	protected CommandButton loadButton;
	protected String planSuffix = "fplan";
	protected boolean validPlanChosen = false;
	protected Label validLabel;
	protected Color orange, green, white;
	protected boolean astrobeeInStateToAcceptUpload = false;
	protected final String PARTICIPANT_ID = "FreeFlyerParticipant";
	protected final String LOAD_PLAN_LOG_STRING = "Load Plan";
	protected FreeFlyerPlan loadedPlan;
	protected IEclipseContext context;
	protected File selectedPlanFile;
	protected final String planValidString = "Plan Valid";
	protected final String planInvalidString = "Plan Invalid";
	protected final String spacer = "\t";
	protected final String noPlanString = "No Plan Selected";
	protected RunningPlanInfo runningPlanInfo;
	private final String runString = "   Run    ";
	private final String pauseString = "  Pause  ";
	private final String skipString = "Skip Step";
	
	protected final String CHOOSE_FILE_TOOLTIP = "Select a Plan from the File System";
	protected final String LOAD_TOOLTIP = "Load the Plan to Astrobee";
	protected final String RUN_TOOLTIP = "Run the Loaded Plan";
	protected final String PAUSE_TOOLTIP = "Pause the Running Plan";
	protected final String SKIP_STEP_TOOLTIP = "Skip a Step in the Loaded Plan";
	
	protected final String CHOSEN_FILE_LABEL_TOOLTIP = "Plan to Load to Astrobee";
	protected final String PLAN_VALID_TOOLTIP = "";//"Selected Plan is Valid";
	protected final String PLAN_INVALID_TOOLTIP = "";//"Selected Plan is Invalid";
	protected final String DESCRIPTION_TOOLTIP = "Description of the Selected Plan";
	
	protected final Color colorWhite = ColorProvider.get(255,255,255);
	protected CommandButton grabControlButton;
	protected AstrobeeStateManager astrobeeStateManager;
	protected Agent agent = null;
	protected MessageType[] sampleType;
	protected boolean agentValid = false;
	protected CommandPublisher commandPublisher;
	protected String myId = Agent.getEgoAgent().name();
	protected Text descriptionText;
	
	protected boolean waitingToSendSetCommand = false;
	protected boolean fakeCurrentPlanMessage = false;
	
	protected Berth selectedBerth = null;
	protected boolean planJustLoaded = false;
	protected ModuleBayPlan selectedPlan;
	protected String loadedPlanName;
	
	protected AggregateAstrobeeState savedAggregateAstrobeeState;
	
	@Inject 
	public BeeCommandingOnRunPlanTab(Composite parent, MApplication application, Display display) {
		
		orange = display.getSystemColor(SWT.COLOR_MAGENTA);
		green = display.getSystemColor(SWT.COLOR_DARK_GREEN);
		white = display.getSystemColor(SWT.COLOR_WHITE);
		this.context = application.getContext();

		sampleType = new MessageType[] {
				MessageTypeExtAstro.COMPRESSED_FILE_ACK_TYPE, 
				MessageTypeExtAstro.PLAN_STATUS_TYPE
		};
		
		fakeCurrentPlanMessage = WorkbenchConstants.isFlagPresent(WorkbenchConstants.FAKE_CURRENT_PLAN_MESSAGE);

		createControls(parent);
	}
	
	protected void createControls(Composite parent) {
		int cellsAcross = 3;
		parent.setLayout(new GridLayout(cellsAcross,false));
		
		createInitializationComposite(parent);
		
		Label verticalSeparator = new Label(parent, SWT.SEPARATOR | SWT.VERTICAL);
		GridDataFactory.fillDefaults().grab(false, true).applyTo(verticalSeparator);
		
		createRobotCommandingComposite(parent);
	}
	
	protected void createInitializationComposite(Composite parent) {
		Composite innerComposite = setupInnerComposite(parent, 1, GridData.HORIZONTAL_ALIGN_BEGINNING);
		GridDataFactory.fillDefaults().grab(false, false).applyTo(innerComposite);
		
		createGrabControlButton(innerComposite);
	}
	
	protected void createRobotCommandingComposite(Composite parent) {
		Composite innerComposite = setupInnerComposite(parent, 1, GridData.FILL_HORIZONTAL);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(innerComposite);

		createLoadPlanRow(innerComposite);
		createRunPlanRow(innerComposite);
		createNotesArea(innerComposite);
	}
	
	protected void createLoadPlanRow(Composite parent) {
		Composite innerComposite = setupInnerComposite(parent, 2, GridData.HORIZONTAL_ALIGN_BEGINNING);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(innerComposite);
		
		createChooseFileButton(innerComposite);
		createChosenFileLabel(innerComposite);
		createValidTextLabel(innerComposite);
	}
	
	protected void createRunPlanRow(Composite parent) {
		Composite innerComposite = setupInnerCompositeEvenSpacingNoHeightMargin(parent, 4, GridData.HORIZONTAL_ALIGN_BEGINNING);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(innerComposite);
		
		createLoadButton(innerComposite);
		createRunButton(innerComposite);
		createPauseButton(innerComposite);
		createSkipButton(innerComposite);
	}
	
	protected void createNotesArea(Composite parent) {
		Composite innerComposite = setupInnerComposite(parent, 1, GridData.HORIZONTAL_ALIGN_BEGINNING);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(innerComposite);
		
		Label label = new Label(innerComposite, SWT.None);
		label.setText("Description");
		
		descriptionText = new Text(innerComposite, SWT.MULTI | SWT.WRAP | SWT.READ_ONLY);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(descriptionText);
		descriptionText.setBackground(colorWhite);
		descriptionText.setToolTipText(DESCRIPTION_TOOLTIP);
	}
	
	@Inject
	@Optional
	public void acceptAstrobeeStateManager(AstrobeeStateManager asm) {
		astrobeeStateManager = asm;
		if(agent != null) {
			astrobeeStateManager.addListener(this, MessageType.ACCESSCONTROL_STATE_TYPE);
			astrobeeStateManager.addListener(this, MessageTypeExtAstro.AGENT_STATE_TYPE);
		}
	}
	
	protected void createValidTextLabel(Composite parent) {
		new Label(parent, SWT.None);
		
		validLabel = new Label(parent, SWT.None);
        GridData validGd = new GridData(SWT.BEGINNING, SWT.FILL, false, false);
        validGd.widthHint = 75;
        validLabel.setLayoutData(validGd);
		validLabel.setText(spacer);
	}
	
	protected void createChooseFileButton(Composite parent) {
		chooseFileButton = new EnlargeableButton(parent, SWT.NONE);
		chooseFileButton.setText(chooseFileButtonText);
		chooseFileButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		chooseFileButton.setButtonLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		chooseFileButton.setToolTipText(CHOOSE_FILE_TOOLTIP);
		chooseFileButton.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
				//
			}

			public void widgetSelected(SelectionEvent e) {
				FileDialog dlg = new FileDialog( chooseFileButton.getShell(),  SWT.OPEN  );
				dlg.setFilterExtensions( new String[]{"*." + planSuffix} );
				dlg.setText("Select a Plan");
				String path = dlg.open();
				if (path == null) return;
				chosenFileLabel.setText(path);
				validateChosenPlanAndEnableUpload(path);
			}
		});
	}

	protected void updateStatusOfLoadButton() {
		loadButton.setCompositeEnabled(validPlanChosen && astrobeeInStateToAcceptUpload && !planJustLoaded);
	}
	
	public void validateChosenPlanAndEnableUpload(String newfilename) {
		selectedPlanFile = new File(newfilename);
		PlanBuilder<ModuleBayPlan> planBuilder = PlanBuilder.getPlanBuilder(selectedPlanFile, ModuleBayPlan.class, true);
		
		if(planBuilder == null) {
			return;
		}
		
		selectedPlan = planBuilder.getPlan();
		
		if(selectedPlan != null && selectedPlan.isValid()) {
			if(selectedPlan.getNotes() == null) {
				descriptionText.setText("");
			} else {
				descriptionText.setText(selectedPlan.getNotes());
			}
			
			if(loadedPlan != null && !selectedPlan.getName().equals(loadedPlan.getName())) {
				planJustLoaded = false;
			}
			
			validPlanChosen = true;
			validLabel.setText(planValidString);
			validLabel.setToolTipText(PLAN_VALID_TOOLTIP);
			validLabel.setForeground(green);
			updateStatusOfLoadButton();
			return;
		}
		else {
			validPlanChosen = false;
		}
		validLabel.setText(planInvalidString);
		validLabel.setToolTipText(PLAN_INVALID_TOOLTIP);
		validLabel.setForeground(orange);
	}
	
	protected void createChosenFileLabel(Composite parent) {
		chosenFileLabel = new Label(parent, SWT.SINGLE | SWT.BORDER);
		GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd.grabExcessHorizontalSpace = true;
		chosenFileLabel.setLayoutData(gd);
		chosenFileLabel.setBackground(colorWhite);
		chosenFileLabel.setToolTipText(CHOSEN_FILE_LABEL_TOOLTIP);
	}
	
	protected void createLoadButton(Composite c) {
		loadButton = new CommandButton(c, SWT.BEGINNING);
		loadButton.setText("Load");
		loadButton.setToolTipText(LOAD_TOOLTIP);
		loadButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		loadButton.setButtonLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		loadButton.setBackground(c.getBackground());

		loadButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(selectedPlanFile != null && selectedPlanFile.exists()){
					try {  
						CompressedFilePublisher.getInstance(agent).compressAndSendFile(
								WorkbenchConstants.SENDING_PLAN_STRING,
								MessageTypeExtAstro.COMPRESSED_FILE_TYPE,
								selectedPlanFile);
						
						if(fakeCurrentPlanMessage) {
							context.set(FreeFlyerPlan.class, null);
							context.set(FreeFlyerPlan.class, loadedPlan);
						}
						waitingToSendSetCommand = true;

					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) { /**/ }
		});
	}
	
	protected void createRunButton(Composite parent) {
		runButton = new CommandButton(parent, SWT.NONE);
		runButton.setText(runString);
		runButton.setToolTipText(RUN_TOOLTIP);
		runButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		runButton.setButtonLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		runButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				MessageBox messageBox = new MessageBox(parent.getShell(), SWT.ICON_WARNING | SWT.YES | SWT.NO);
		        if(astrobeeStateManager != null && !astrobeeStateManager.getAggregateAstrobeeState().getCurrentPlanName().isEmpty() 
		        		&& selectedPlanFile != null
		        		&& !astrobeeStateManager.getAggregateAstrobeeState().getCurrentPlanName().equals(FilenameUtils.getBaseName(selectedPlanFile.getName()))){
		        	 messageBox.setText("Confirm Run");
				        messageBox.setMessage("Are you sure you want to run "+astrobeeStateManager.getAggregateAstrobeeState().getCurrentPlanName()+".fplan and not "
				        		+selectedPlanFile.getName() + "? Click Yes to run "+astrobeeStateManager.getAggregateAstrobeeState().getCurrentPlanName()+".fplan.");
				        int buttonID = messageBox.open();
				        switch(buttonID) {
				          case SWT.YES:
				        	  commandPublisher.sendGenericNoParamsCommand(
										PLAN_METHOD_RUN_PLAN.VALUE,
										PLAN.VALUE);
				          case SWT.NO:
				            break;
				        }
		        }else{
		        	 commandPublisher.sendGenericNoParamsCommand(
								PLAN_METHOD_RUN_PLAN.VALUE,
								PLAN.VALUE);
		        }
				
				
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// no-op
			}
		});
	}

	protected void createPauseButton(Composite parent) {
		pauseButton = new CommandButton(parent, SWT.NONE);
		pauseButton.setText(pauseString);
		pauseButton.setToolTipText(PAUSE_TOOLTIP);
		pauseButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		pauseButton.setButtonLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		pauseButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				commandPublisher.sendGenericNoParamsCommand(
						PLAN_METHOD_PAUSE_PLAN.VALUE,
						PLAN.VALUE);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// no-op
			}
		});
	}

	protected void createSkipButton(Composite parent) {
		skipButton = new CommandButton(parent, SWT.NONE);
		skipButton.setText(skipString);
		skipButton.setToolTipText(SKIP_STEP_TOOLTIP);
		skipButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		skipButton.setButtonLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		skipButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				if(runningPlanInfo == null) {
					LogPoster.postToLog(LogEntry.COMMAND, "ERROR - no current plan detected.", agent.name());
					return;
				}
				commandPublisher.sendSkipPlanStepCommand();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// no-op
			}
		});
	}

	protected void createGrabControlButton(Composite parent) {
		Composite innerComposite = setupInnerComposite(parent, 1, GridData.FILL_HORIZONTAL);
		
		grabControlButton = new CommandButton(innerComposite, SWT.NONE);
		grabControlButton.setText("Grab Control");
		grabControlButton.setToolTipText(WorkbenchConstants.GRAB_CONTROL_TOOLTIP);
		grabControlButton.setButtonLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		grabControlButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
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
	
	protected Composite setupInnerComposite(Composite parent, int cellsAcross, int alignment) {
		Composite innerComposite = new Composite(parent, SWT.None);
		innerComposite.setLayout(new GridLayout(cellsAcross,false));
		GridData compositeGd = new GridData(alignment);
		innerComposite.setLayoutData(compositeGd);
		return innerComposite;
	}
	
	protected Composite setupInnerCompositeEvenSpacingNoHeightMargin(Composite parent, int cellsAcross, int alignment) {
		Composite innerComposite = new Composite(parent, SWT.None);
		GridLayout gl = new GridLayout(cellsAcross,true);
		gl.marginHeight = 0;
		innerComposite.setLayout(gl);
		
		GridData compositeGd = new GridData(alignment);
		innerComposite.setLayoutData(compositeGd);
		return innerComposite;
	}
	
	@Inject @Optional
	public void onAgentSelected(@Named(FreeFlyerStrings.PRIMARY_BEE) Agent a) {
		if(a == null) {
			return;
		}
		agent = a; // have to do this because we might be the other control panel
		if(a != null) {
			agentValid = true;
		}
		
		subscribe();
		
		commandPublisher = CommandPublisher.getInstance(agent);
		if(astrobeeStateManager != null) {
			astrobeeStateManager.addListener(this, MessageType.ACCESSCONTROL_STATE_TYPE);
			astrobeeStateManager.addListener(this, MessageTypeExtAstro.AGENT_STATE_TYPE);
		} else {
			System.err.println("BeeCommandingOnRunPlanTab does not have an AstrobeeStateManager");
		}
	}
	
	public void unsubscribe() {
		if(getAgent() != null) {
			RapidMessageCollector.instance().removeRapidMessageListener(participantId, getAgent(), this);
		}
	}

	public void subscribe() {
		if (getAgent() == null){
			return;
		}
		
		final Agent agent = getAgent();
		final String id = participantId;
		
		DdsTask.dispatchExec(new Runnable() {
			@Override
			public void run() {
				for(MessageType mt : getMessageTypes()) {
					if (mt == null) continue;
					RapidMessageCollector.instance().addRapidMessageListener(id, 
							agent, 
							mt, 
							BeeCommandingOnRunPlanTab.this);
				}
			}
		});
	}
	
	protected List<MessageType> getMessageTypes() {
		List<MessageType> ret = new ArrayList<MessageType>();
		for(int i=0; i<getSampleType().length; i++) {
			ret.add(getSampleType()[i]);
		}
		return ret;
	}
	
	protected MessageType[] getSampleType() {
		return sampleType;
	}

	public Agent getAgent() {
		return agent;
	}

	@PreDestroy
	public void preDestroy() {
		astrobeeStateManager.removeListener(this);
		unsubscribe();
	}

	@Override
	public void onAstrobeeStateChange(AggregateAstrobeeState aggregateAstrobeeState) {
		if(runButton == null) {
			return;
		}
		savedAggregateAstrobeeState = aggregateAstrobeeState;
		updateButtonEnablements();
	}
	
	protected void updateButtonEnablements() {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if(savedAggregateAstrobeeState.getAccessControl() == null) {
					return;
				}
				boolean iHaveControl = savedAggregateAstrobeeState.getAccessControl().equals(myId);
				if(!iHaveControl) {
					grabControlButton.setCompositeEnabled(true);
					loadButton.setCompositeEnabled(false);
					runButton.setCompositeEnabled(false);
					pauseButton.setCompositeEnabled(false);
					skipButton.setCompositeEnabled(false);
					astrobeeInStateToAcceptUpload = false;
					return;
				} else {
					grabControlButton.setCompositeEnabled(false);
				}

				if(savedAggregateAstrobeeState.getAstrobeeState() == null || savedAggregateAstrobeeState.getAstrobeeState().getOperatingState() == null) {
					return;
				}

				switch(savedAggregateAstrobeeState.getAstrobeeState().getOperatingState()) {
				case READY:
					skipButton.setCompositeEnabled(false);
					pauseButton.setCompositeEnabled(false);
					astrobeeInStateToAcceptUpload = true;
					updateStatusOfLoadButton();
					break;
				case FAULT:
					astrobeeInStateToAcceptUpload = false;
					runButton.setCompositeEnabled(false);
					pauseButton.setCompositeEnabled(false);
					skipButton.setCompositeEnabled(false);
					loadButton.setCompositeEnabled(false);
					return;
				case TELEOPERATION:
					astrobeeInStateToAcceptUpload = false;
					runButton.setCompositeEnabled(false);
					pauseButton.setCompositeEnabled(false);
					skipButton.setCompositeEnabled(false);
					loadButton.setCompositeEnabled(false);
					return;
				case PLAN_EXECUTION: // true
				default:
					break;
				}

				switch(savedAggregateAstrobeeState.getAstrobeeState().getPlanExecutionState()) {
				case IDLE: 
					runButton.setCompositeEnabled(false);
					pauseButton.setCompositeEnabled(false);
					skipButton.setCompositeEnabled(false);
					astrobeeInStateToAcceptUpload = true;
					break;
				case EXECUTING:
					runButton.setCompositeEnabled(false);
					pauseButton.setCompositeEnabled(true);
					skipButton.setCompositeEnabled(false);
					loadButton.setCompositeEnabled(false);
					astrobeeInStateToAcceptUpload = false;
					break;
				case PAUSED:
					astrobeeInStateToAcceptUpload = true;
					if(!waitingToSendSetCommand) {
						updateStatusOfLoadButton();
						runButton.setCompositeEnabled(true);
						pauseButton.setCompositeEnabled(false);
						if(loadedPlan != null && savedAggregateAstrobeeState != null) {
							if(loadedPlan.getNextExecutableElement(savedAggregateAstrobeeState.getCurrentPlanCommand()) == null) {
								skipButton.setCompositeEnabled(false);
							} else {
								skipButton.setCompositeEnabled(true);
							}
						}
					}
					else if(waitingToSendSetCommand){
						runButton.setCompositeEnabled(false);
					}
					break;
				case ERROR:
					break;
				default:
					break;
				}
			}
		});
	}

	@Override
	public void onRapidMessageReceived(Agent agent, MessageType msgType,
			Object msgObj, Object cfgObj) {
		if(msgObj instanceof CompressedFileAck){
			// assume it is the right one - can put in checks later
			if(waitingToSendSetCommand) {
				sendSetPlanCmd();
				waitingToSendSetCommand = false;
			}
		}
		if(msgType.equals(MessageTypeExtAstro.PLAN_STATUS_TYPE)) {
			if(loadedPlan == null || msgObj == null) {
				return;
			}
			PlanStatus planStatus = (PlanStatus)msgObj;
			ingestPlanStatus(planStatus);
		}
	}
	
	protected void ingestPlanStatus(PlanStatus planStatus) {
		if(planStatus == null) {
			return;
		}
		loadedPlanName = planStatus.planName;
		if(selectedPlan != null
				&& selectedPlan.getName().equals(loadedPlanName) 
				&& planStatus.currentPoint == 0 
				&& planStatus.currentCommand == -1) {
			planJustLoaded = true;
		} else {
			planJustLoaded = false;
		}
		try {
			final Display display = Display.getDefault();
			try {
				if (!display.isDisposed()){
					display.asyncExec(new Runnable() {
						public void run() {
							updateStatusOfLoadButton();
						}
					});
				}
			} catch (SWTException e){
				logger.error("SWTException", e);
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}
	
	@Inject @Optional
	public void acceptRunningPlanInfo(@Named(FreeFlyerStrings.RUNNING_PLAN_INFO) RunningPlanInfo rpi) {
		if(rpi == null) {
			return;
		}
		runningPlanInfo = rpi;
		loadedPlan = runningPlanInfo.getPlan();
		ingestPlanStatus(runningPlanInfo.getPlanStatus());
		updateButtonEnablements();
	}
	
	void sendSetPlanCmd(){
		commandPublisher.sendGenericNoParamsCommand(
				PLAN_METHOD_SET_PLAN.VALUE, 
				PLAN.VALUE);
	}
}
