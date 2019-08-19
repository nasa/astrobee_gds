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
package gov.nasa.arc.verve.freeflyer.workbench.parts.engineering;

import gov.nasa.arc.irg.freeflyer.rapid.CommandPublisher;
import gov.nasa.arc.irg.freeflyer.rapid.FreeFlyerStrings;
import gov.nasa.arc.irg.freeflyer.rapid.state.AggregateAstrobeeState;
import gov.nasa.arc.irg.freeflyer.rapid.state.AstrobeeStateListener;
import gov.nasa.arc.irg.freeflyer.rapid.state.AstrobeeStateManager;
import gov.nasa.arc.verve.freeflyer.workbench.widget.helpers.CommandButton;
import gov.nasa.dds.system.DdsTask;
import gov.nasa.rapid.idl.ext.astrobee.message.MessageTypeExtAstro;
import gov.nasa.rapid.v2.e4.Rapid;
import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.e4.message.IRapidMessageListener;
import gov.nasa.rapid.v2.e4.message.MessageType;
import gov.nasa.rapid.v2.e4.message.collector.RapidMessageCollector;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import rapid.ext.astrobee.EkfState;
import rapid.ext.astrobee.GncFamCmdState;

public class NumbersView implements IRapidMessageListener, AstrobeeStateListener {
	protected MessageType[] sampleType;
	//protected MessageType configType;
	private final String m_participantId = Rapid.PrimaryParticipant;
	final int ekfNum = 1;
	final int gncNum = 1;
	final int cells = ekfNum + gncNum;
	private Text[] text = new Text[cells];
	private final String[] labels = {"Confidence, ml_count, of_count, cov_diag sqrt(13,14,15)", "attitude_error, position_error"};
	private String[] data = new String[cells];
	protected Agent agent;
	protected final String newline  = "\n";
	protected final int ekfCharacterLimit = 200 * 20 * 4;
	protected final int gncCharacterLimit = 200 * 20 * 2;
	protected AstrobeeStateManager astrobeeStateManager;
	protected String myId;
	CommandButton stopButton;
	Label beeNameLabel;
	
	@Inject 
	public NumbersView(Composite parent) {
		myId = Agent.getEgoAgent().name();

		createComposite(parent);

		for(int i=0; i<cells; i++) {
			data[i] = new String();
		}

		sampleType = new MessageType[] {
				MessageTypeExtAstro.EKF_STATE_TYPE,
				MessageTypeExtAstro.GNC_FAM_CMD_STATE_TYPE
		};

		subscribe();
	}

	@Override
	public void onRapidMessageReceived(Agent agent, MessageType msgType,
			Object msgObj, Object cfgObj) {
		if(msgType.equals(MessageTypeExtAstro.EKF_STATE_TYPE)) {
			EkfState ekfState = (EkfState)msgObj;
			StringBuilder builder = new StringBuilder();
			String timestamp = Long.toString(ekfState.hdr.timeStamp);
			
			builder.append(timestamp + "\t\t" + ekfState.confidence);
			builder.append("\t\t" + ekfState.ml_count);
			builder.append("\t\t" + ekfState.of_count);

			float a = ekfState.cov_diag.userData.getFloat(12);
			float b = ekfState.cov_diag.userData.getFloat(13);
			float c = ekfState.cov_diag.userData.getFloat(14);
			double d = Math.sqrt(a + b + c);
			
			builder.append("\t\t" + String.format("%1.3e",d));
			builder.append(newline);
			builder.append(data[0]);
			data[0] = builder.toString();
			
			updateEkfData();
		} 
		if(msgType.equals(MessageTypeExtAstro.GNC_FAM_CMD_STATE_TYPE)) {
			GncFamCmdState gncState = (GncFamCmdState)msgObj;
			StringBuilder builder = new StringBuilder();
			String timestamp = Long.toString(gncState.hdr.timeStamp);
			
			double a0 = gncState.attitude_error.userData[0];
			double a1 = gncState.attitude_error.userData[1];
			double a2 = gncState.attitude_error.userData[2];
			double att = Math.sqrt(a0 + a1 + a2);
			
			builder.append(timestamp + "\t\t" + String.format("%1.3e",att));

			double p0 = gncState.position_error.userData[0];
			double p1 = gncState.position_error.userData[1];
			double p2 = gncState.position_error.userData[2];
			double pos = Math.sqrt(p0 + p1 + p2);
			
			builder.append("\t\t" + String.format("%1.3e",pos));
			builder.append(newline);
			builder.append(data[1]);
			data[1] = builder.toString();
			
			updateGncData();
		} 
	}

	public void updateEkfData() {
		Display.getDefault().asyncExec(new Runnable() { // avoid invalid thread
			// access
			@Override
			public void run() {
				if(text[0] != null || !text[0].isDisposed()) {
					for(int i=0; i<ekfNum; i++) {
						if(data[i].length() > ekfCharacterLimit) {
							data[i] = data[i].substring(0, ekfCharacterLimit);
						}

						text[i].setText(data[i]);
					}
				}
			}
		});
	}
	
	public void updateGncData() {
		Display.getDefault().asyncExec(new Runnable() { // avoid invalid thread
			// access
			@Override
			public void run() {
				if(text[0] != null || !text[0].isDisposed()) {
					for(int i=ekfNum; i<cells; i++) {
						if(data[i].length() > gncCharacterLimit) {
							data[i] = data[i].substring(0, gncCharacterLimit);
						}

						text[i].setText(data[i]);
					}
				}
			}
		});
	}


	@Inject @Optional
	public void onAgentSelected(@Named(FreeFlyerStrings.PRIMARY_BEE) Agent a) {
		if(a == null) {
			return;
		}
		agent = a;
		if(astrobeeStateManager != null) {
			astrobeeStateManager.addListener(this);
		}
		beeNameLabel.setText(agent.name());
		subscribe();
	}

	public void subscribe() {
		if (getParticipantId() == null || getParticipantId().isEmpty() || getAgent() == null){
			return;
		}

		final Agent agent = getAgent();
		final String id = getParticipantId();

		DdsTask.dispatchExec(new Runnable() {
			@Override
			public void run() {
				for(MessageType mt : getMessageTypes()) {
					if (mt == null) continue;
					RapidMessageCollector.instance().addRapidMessageListener(id, 
							agent, 
							mt, 
							NumbersView.this);
				}
			}
		});
	}

	private void createComposite(Composite topComposite) {
		GridLayout topLayout;
		GridData gridData;

		topComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		topLayout = new GridLayout(cells, false);
		topLayout.verticalSpacing = 2;
		topComposite.setLayout(topLayout);
		
		beeNameLabel = new Label(topComposite, SWT.NONE);
		beeNameLabel.setText("No Bee Yet!");
		gridData = new GridData(SWT.FILL, SWT.BEGINNING, false, false, 1, 1);
		beeNameLabel.setLayoutData(gridData);
		
		stopButton = new CommandButton(topComposite, SWT.None);
		stopButton.setText("Stop EKF and GNC Telemetry");
		gridData = new GridData(SWT.FILL, SWT.BEGINNING, true, false, cells-1, 1);
		stopButton.setLayoutData(gridData);
		stopButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				CommandPublisher publisher = CommandPublisher.getInstance(agent);
				publisher.sendSetTelemetryRateCommand("EkfState", 0);
				publisher.sendSetTelemetryRateCommand("GncState", 0);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) { /**/ }
		});
		
		Label label;
		for(int i=0; i<cells; i++) {
			label = new Label(topComposite, SWT.LEFT);
			label.setText(labels[i]);
			gridData = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
			label.setLayoutData(gridData);
		}

		for(int i=0; i<cells; i++) {
			text[i] = new Text(topComposite, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.READ_ONLY);
			gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
			text[i].setLayoutData(gridData);
		}
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

	public void subscribe(MessageType messageType, Agent agent) {
		RapidMessageCollector.instance().addRapidMessageListener(getParticipantId(), 
				agent, 
				messageType, 
				this);
	}

	public void unsubscribe(MessageType messageType, Agent agent) {
		RapidMessageCollector.instance().removeRapidMessageListener(getParticipantId(), 
				agent, 
				messageType, 
				this);
	}

	private String getParticipantId() {
		return m_participantId;
	}
	
	public void onAstrobeeStateChange(AggregateAstrobeeState stateKeeper) {
		if(text[0] == null || text[0].isDisposed()) {
			return;
		}
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if(stateKeeper.getAccessControl().equals(myId)) {
					stopButton.setEnabled(true);
				} else {
					stopButton.setEnabled(false);
				}
					
			}
		});
	}
	
	@Inject @Optional
	public void acceptAstrobeeStateManager(AstrobeeStateManager asm) {
		astrobeeStateManager = asm;
 
		if(agent != null) {
			astrobeeStateManager.addListener(this);
		}
	}
	
	@PreDestroy
	public void preDestroy() {
		astrobeeStateManager.removeListener(this);
	}
}
