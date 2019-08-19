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
package gov.nasa.arc.verve.freeflyer.workbench.parts.advanced;

import gov.nasa.arc.irg.freeflyer.rapid.CommandPublisher;
import gov.nasa.arc.irg.freeflyer.rapid.FreeFlyerStrings;
import gov.nasa.arc.irg.freeflyer.rapid.LogPoster;
import gov.nasa.arc.irg.freeflyer.rapid.state.AggregateAstrobeeState;
import gov.nasa.arc.irg.freeflyer.rapid.state.AstrobeeStateListener;
import gov.nasa.arc.irg.freeflyer.rapid.state.AstrobeeStateManager;
import gov.nasa.arc.irg.freeflyer.rapid.state.TelemetryStateGds.TelemetryFrequency;
import gov.nasa.arc.irg.plan.ui.io.EnlargeableButton;
import gov.nasa.arc.verve.freeflyer.workbench.helpers.SelectedAgentConnectedRegistry;
import gov.nasa.arc.verve.freeflyer.workbench.utils.GuiUtils;
import gov.nasa.rapid.v2.e4.agent.Agent;

import java.util.Collections;
import java.util.HashMap;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import rapid.ext.astrobee.SETTINGS_TELEMETRY_TYPE_COMM_STATUS;
import rapid.ext.astrobee.SETTINGS_TELEMETRY_TYPE_CPU_STATE;
import rapid.ext.astrobee.SETTINGS_TELEMETRY_TYPE_DISK_STATE;
import rapid.ext.astrobee.SETTINGS_TELEMETRY_TYPE_EKF_STATE;
import rapid.ext.astrobee.SETTINGS_TELEMETRY_TYPE_GNC_STATE;
import rapid.ext.astrobee.SETTINGS_TELEMETRY_TYPE_PMC_CMD_STATE;
import rapid.ext.astrobee.SETTINGS_TELEMETRY_TYPE_POSITION;

public class DataToGdsPart implements AstrobeeStateListener {
	protected Agent agent;
	private Label agentNameLabel;
	private int entireWidth = 3;
	private String titleString = "Data to GDS";
	protected AstrobeeStateManager astrobeeStateManager;
	protected String myId;
	private Composite parent;
	
	private String[] setTelemetryHeadings = {"Telemetry", "Current Freq", "Change to (Hz)", ""};
	private String initialFrequency = "5" +"\t";

	private HashMap<String, Label> currentTelemetryRate = new HashMap<String, Label>();
	private HashMap<String, Text> telemetryRateInput = new HashMap<String, Text>();
	private HashMap<String, EnlargeableButton> setTelemetryRateButton = new HashMap<String, EnlargeableButton>();

	private String[] possibleTelemetries = {SETTINGS_TELEMETRY_TYPE_POSITION.VALUE,
			SETTINGS_TELEMETRY_TYPE_EKF_STATE.VALUE,
			SETTINGS_TELEMETRY_TYPE_COMM_STATUS.VALUE,
			SETTINGS_TELEMETRY_TYPE_DISK_STATE.VALUE,
			SETTINGS_TELEMETRY_TYPE_CPU_STATE.VALUE,
			SETTINGS_TELEMETRY_TYPE_GNC_STATE.VALUE,
			SETTINGS_TELEMETRY_TYPE_PMC_CMD_STATE.VALUE};

	@Inject
	public DataToGdsPart(Composite inParent) {
		final ScrolledComposite sc1 = new ScrolledComposite(inParent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		final Composite c1 = new Composite(sc1, SWT.NONE);
		sc1.setContent(c1);
		parent = c1;
	}

	@PostConstruct
	public void postConstruct() {
		makeComposite();
	}
	
	public void makeComposite() {
		GridLayout gl = new GridLayout(2, false);
		//gl.numColumns = 2;
		parent.setLayout(gl);

		myId = Agent.getEgoAgent().name();
		
		GuiUtils.makeHorizontalSeparator(parent, 2);
		createAgentNameLabel(parent);
		makeConfigureTelemetrySection(parent);

		repackTheParent();
	}
	
	private void repackTheParent() {
		parent.setSize(parent.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		parent.pack();
		parent.layout();
		parent.update();
	}
	
	@Inject @Optional
	public void acceptAstrobeeStateManager(AstrobeeStateManager asm) {
		astrobeeStateManager = asm;
 
		if(agent != null) {
			astrobeeStateManager.addListener(this);
		}
	}

	private void enableButtons() {
		for(EnlargeableButton b : setTelemetryRateButton.values()) {
			b.setEnabled(true);
		}
	}

	@Inject @Optional
	public void onAgentSelected(@Named(FreeFlyerStrings.PRIMARY_BEE) Agent a) {
		if(a == null) {
			return;
		}

		agent = a; 
		if(agentNameLabel != null ) {
			agentNameLabel.setText(agent.name() + " " + titleString );
		}
		
		if(astrobeeStateManager != null) {
			astrobeeStateManager.addListener(this);
		}
	}

	private void updateCommData(AggregateAstrobeeState stateKeeper) {
		if(agentNameLabel == null || agentNameLabel.isDisposed()) {
			return;
		}

		updateTelemetryData(AggregateAstrobeeState.getInstance().getTelemetryState().getTelemetryFrequencies());

		if(stateKeeper.getAccessControl().equals(myId)) {
			enableButtons();
		}
	}

	private void updateTelemetryData(TelemetryFrequency[] input) {
		for(int i=0; i<input.length; i++) {
			Label l = currentTelemetryRate.get(input[i].getRapidTelemetryType());
			float freq = input[i].getFrequency();
			String plain = Float.toString(freq);
			String padded = plain + " Hz" + "\t";
			l.setText(padded);
		}
	}

	protected void makeConfigureTelemetrySection(Composite parent) {
		Composite innerComposite = GuiUtils.setupInnerComposite(parent, setTelemetryHeadings.length, GridData.VERTICAL_ALIGN_FILL);

		for(int i=0; i<setTelemetryHeadings.length; i++) {
			Label l = new Label(innerComposite, SWT.None);
			l.setText(setTelemetryHeadings[i]);
			GridDataFactory.fillDefaults().grab(true, false).applyTo(l);
		}
		for(int i=0; i<possibleTelemetries.length; i++) {
			createATelemetryRow(innerComposite, possibleTelemetries[i]);
		}
	}

	protected void createATelemetryRow(Composite c, String name) {
		Label nameLabel = new Label(c, SWT.None);
		nameLabel.setText(name);

		Label currTelemRate = new Label(c, SWT.None);
		currTelemRate.setText("________");
		GridDataFactory.fillDefaults().align(SWT.END, SWT.BEGINNING).grab(true, false).applyTo(currTelemRate);
		currentTelemetryRate.put(name, currTelemRate);

		Text telemRateInput = new Text(c, SWT.BORDER);
		telemRateInput.setText(initialFrequency);
		GridDataFactory.fillDefaults().align(SWT.END, SWT.BEGINNING).grab(true, false).applyTo(telemRateInput);
		telemetryRateInput.put(name, telemRateInput);

		EnlargeableButton setButton = new EnlargeableButton(c, SWT.None);
		setButton.setText("Set");
		setButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		setButton.setButtonLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		setButton.addSelectionListener(new SelectionListener() {
			private final String telemetryName = name;
			@Override
			public void widgetSelected(SelectionEvent e) {
				CommandPublisher publisher = CommandPublisher.getInstance(agent);
				try {
					String withSpaces = telemetryRateInput.get(name).getText();
					Float rate = Float.valueOf(withSpaces.trim());
					publisher.sendSetTelemetryRateCommand(telemetryName, rate);
				} catch (NumberFormatException nfe) {
					// TODO popup real error message
					LogPoster.postToLog("Error", "Enter an integer for frequency", "workbench");
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) { /**/ }
		});
		setTelemetryRateButton.put(name, setButton);
	}

	protected class TelemFreqTreeContentProvider implements ITreeContentProvider {
		@Override
		public void dispose() { /**/ }

		@Override
		public void inputChanged(Viewer viewer, Object oldInput,
				Object newInput) { /**/ }

		@Override
		public Object[] getElements(Object inputElement) {
			if(inputElement instanceof TelemetryFrequency[]) {
				return (TelemetryFrequency[]) inputElement;
			}
			return null;
		}

		@Override
		public Object[] getChildren(Object parentElement) {	
			return Collections.EMPTY_LIST.toArray();
		}

		@Override
		public Object getParent(Object element) { 
			return null;
		}

		@Override
		public boolean hasChildren(Object element) { 
			return false;
		}
	}

	public void onAstrobeeStateChange(AggregateAstrobeeState stateKeeper) {
		if(agentNameLabel == null || agentNameLabel.isDisposed()) {
			return;
		}
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				updateCommData(stateKeeper);
			}
		});
	}

	private void createAgentNameLabel(Composite parent) {
		agentNameLabel = new Label(parent, SWT.None);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = entireWidth;
		agentNameLabel.setLayoutData(data);
		agentNameLabel.setText(titleString);
		Font bigFont = GuiUtils.makeBigFont(parent, agentNameLabel);
		agentNameLabel.setFont(bigFont);
	}

	@PreDestroy
	public void preDestroy() {
		astrobeeStateManager.removeListener(this);
	}
}
