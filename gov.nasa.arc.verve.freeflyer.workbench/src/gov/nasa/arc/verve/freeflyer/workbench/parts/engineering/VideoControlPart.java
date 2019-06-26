package gov.nasa.arc.verve.freeflyer.workbench.parts.engineering;

import gov.nasa.arc.irg.freeflyer.rapid.CommandPublisher;
import gov.nasa.arc.irg.freeflyer.rapid.FreeFlyerStrings;
import gov.nasa.arc.irg.freeflyer.rapid.state.AggregateAstrobeeState;
import gov.nasa.arc.irg.freeflyer.rapid.state.AstrobeeStateListener;
import gov.nasa.arc.irg.freeflyer.rapid.state.AstrobeeStateManager;
import gov.nasa.arc.irg.plan.freeflyer.config.InertiaConfigList.InertiaConfig;
import gov.nasa.arc.verve.freeflyer.workbench.widget.helpers.CommandButton;
import gov.nasa.rapid.idl.ext.astrobee.message.MessageTypeExtAstro;
import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.e4.message.IRapidMessageListener;
import gov.nasa.rapid.v2.e4.message.MessageType;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import rapid.ACCESSCONTROL;
import rapid.ACCESSCONTROL_METHOD_REQUESTCONTROL;
import rapid.ext.astrobee.CompressedFileAck;
import rapid.ext.astrobee.PlanStatus;

public class VideoControlPart implements AstrobeeStateListener {
	private CommandButton configureButton;
	protected CommandPublisher commandPublisher;
	private Label agentNameLabel;
	protected Agent agent = null;
	protected MessageType[] sampleType;
	private String titleString = "Video Control";
	protected AstrobeeStateManager astrobeeStateManager;
	protected boolean agentValid = false;
	protected boolean waitingToSendKillCommands = false;

	protected String myId = Agent.getEgoAgent().name();

	@Inject 
	public VideoControlPart(Composite parent) {
		agentNameLabel = new Label(parent, SWT.None);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 1;
		agentNameLabel.setLayoutData(data);
		agentNameLabel.setText(titleString);


		configureButton = new CommandButton(parent, SWT.NONE);
		configureButton.setText("Stop Camera Streaming");
		configureButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		configureButton.setButtonLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		configureButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				waitingToSendKillCommands = true;
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
		configureButton.setCompositeEnabled(true);
	}

	@Inject @Optional
	public void onAgentSelected(@Named(FreeFlyerStrings.PRIMARY_BEE) Agent a) {
		if(a == null) {
			return;
		}
		agentNameLabel.setText(a.name() + " " + titleString );
		commandPublisher = CommandPublisher.getInstance(a);

		agent = a; // have to do this because we might be the other control panel
		if(a != null) {
			agentValid = true;
		}

		commandPublisher = CommandPublisher.getInstance(agent);
		if(astrobeeStateManager != null) {
			astrobeeStateManager.addListener(this, MessageType.ACCESSCONTROL_STATE_TYPE);
		} else {
			System.err.println("VideoControlPart does not have an AstrobeeStateManager");
		}
	}

	@Inject
	@Optional
	public void acceptAstrobeeStateManager(AstrobeeStateManager asm) {
		astrobeeStateManager = asm;
		if(agent != null) {
			astrobeeStateManager.addListener(this, MessageType.ACCESSCONTROL_STATE_TYPE);
		}
	}

	@Override
	public void onAstrobeeStateChange(AggregateAstrobeeState aggregateAstrobeeState) {
		if(configureButton == null) {
			return;
		}
		if(aggregateAstrobeeState.getAccessControl() == null) {
			return;
		}
		boolean iHaveControl = aggregateAstrobeeState.getAccessControl().equals(myId);
		if(iHaveControl && waitingToSendKillCommands ) {
			sendCommands();
		}
	}

	protected void sendCommands() {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				String command = "setCameraStreaming";
				String subsystem = "Camera";
				String stringParamName = "cameraName";
				String boolParamName = "stream";
				String[] cameras = {"Dock", "Navigation", "Science"};

				for(int i=0; i<cameras.length; i++) {
					commandPublisher.sendGenericStringBooleanCommand(command, subsystem, 
							stringParamName, cameras[i], boolParamName, false);
				}
				waitingToSendKillCommands = false;
			}
		});
	}
}
