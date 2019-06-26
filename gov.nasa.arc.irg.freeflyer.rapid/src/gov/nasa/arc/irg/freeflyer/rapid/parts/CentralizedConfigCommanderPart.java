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
package gov.nasa.arc.irg.freeflyer.rapid.parts;

import gov.nasa.arc.irg.freeflyer.rapid.FreeFlyerStrings;
import gov.nasa.arc.irg.freeflyer.rapid.centralized.CentralizedCommandConfigWidget;
import gov.nasa.arc.irg.freeflyer.rapid.centralized.CentralizedCommandPublisher;
import gov.nasa.dds.system.DdsTask;
import gov.nasa.rapid.v2.e4.Rapid;
import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.e4.message.IRapidMessageListener;
import gov.nasa.rapid.v2.e4.message.MessageType;
import gov.nasa.rapid.v2.e4.message.collector.RapidMessageCollector;

import java.util.HashMap;
import java.util.Iterator;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import rapid.CommandConfig;
import rapid.CommandDef;
import rapid.CommandDefSeq;
import rapid.Subsystem;
import rapid.SubsystemSeq;
import rapid.SubsystemType;
import rapid.SubsystemTypeSeq;

public class CentralizedConfigCommanderPart implements IRapidMessageListener {
	private static final Logger logger = Logger.getLogger(CentralizedConfigCommanderPart.class);

	private Combo m_subsystemsCombo;
	private String m_participantId = Rapid.PrimaryParticipant;
	protected String m_selectedPartition = "";
	protected Agent m_agent = null;
	private HashMap<String,SubsystemType> m_subsystemTypes = new HashMap<String,SubsystemType>();
	private CentralizedCommandConfigWidget m_widget;
	private Composite m_parent;
	private int m_gridSquares = 9;
	private Label m_agentLabel;
	@Inject
	public CentralizedCommandPublisher m_commandPublisher;
	
	@Inject 
	public CentralizedConfigCommanderPart(Composite parent) {
		m_parent = parent;
		makeTopPart();
	}

	public void makeTopPart() {
		GridLayout gl = new GridLayout(m_gridSquares, false);
		m_parent.setLayout(gl);
		
		GridData gdThreeWide = new GridData(SWT.BEGINNING, SWT.BEGINNING, true, false);
		gdThreeWide.horizontalSpan = 3;

		m_agentLabel = new Label(m_parent, SWT.None);
		m_agentLabel.setText(FreeFlyerStrings.UNCONNECTED_STRING);

		m_subsystemsCombo = new Combo(m_parent, SWT.READ_ONLY);
		m_subsystemsCombo.setSize(85, 20);
		m_subsystemsCombo.setLayoutData(gdThreeWide);
		m_subsystemsCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// now we need to make the part based on what was selected
				String subsystemName = m_subsystemsCombo.getText();
				String typeName = (String)m_subsystemsCombo.getData(subsystemName);
				SubsystemType st = m_subsystemTypes.get(typeName);
				StringBuilder builder = new StringBuilder();
				builder.append(typeName + ":\n");

				CommandDefSeq cmds = st.commands.userData;
				
				setSelectedElement(null, null);
				setSelectedElement(cmds, subsystemName);
			}
		});
		
		m_parent.layout(true);
		m_parent.pack();
		m_parent.update();
	}
	
	public void commandRequested(CommandDef cdef, String subsystemName, Object o) {
		m_commandPublisher.commandRequested(cdef, subsystemName, o);
	}
	
	public void setSelectedElement(CommandDefSeq cmds, String subsystemName) {
		try {
			if (cmds == null){
				if (m_widget != null){

					m_widget.dispose();
					m_widget = null;
					m_parent.layout(true);
					m_parent.pack();
					m_parent.update();
					return;
				}
			} else {
				if (m_widget != null) {
					m_widget.dispose();
				} 

				m_widget = new CentralizedCommandConfigWidget(m_parent, cmds, m_gridSquares, subsystemName, this);
				m_widget.setVisible(true);
				m_parent.layout(true);
				m_parent.pack();
				m_parent.update();
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	private void populateSubsystemTypesList(final Iterator<SubsystemType> subsystemTypes) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {

				while(subsystemTypes.hasNext()) {
					SubsystemType st = subsystemTypes.next();
					m_subsystemTypes.put(st.name, st);
				}
				m_subsystemsCombo.pack();
			}
		});
	}

	private void populateSubsystemsCombo(final Iterator<Subsystem> subsystemNames) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {

				while(subsystemNames.hasNext()) {
					Subsystem ss = subsystemNames.next();

					m_subsystemsCombo.add(ss.name);
					m_subsystemsCombo.setData(ss.name, ss.subsystemTypeName);

				}
				m_subsystemsCombo.pack();
			}
		});
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
					RapidMessageCollector.instance().addRapidMessageListener(id, 
							agent, 
							MessageType.COMMAND_CONFIG_TYPE, 
							CentralizedConfigCommanderPart.this);
			}
		});
	}

	public void unsubscribe() {
		if(getAgent() != null) {
			logger.debug("unsubscribe from all on "+getAgent().name() + " for AbstractTelemetryTablePart");
			RapidMessageCollector.instance().removeRapidMessageListener(getParticipantId(), getAgent(), this);
		}
	}

	public String getParticipantId() {
		return m_participantId;
	}

	public Agent getAgent() {
		return m_agent;
	}

	public void onRapidMessageReceived(Agent agent, MessageType msgType,
			Object msgObj, Object cfgObj) {
		if(msgType.equals(MessageType.COMMAND_CONFIG_TYPE)) {
			CommandConfig config = (CommandConfig)msgObj;
			SubsystemSeq subsystems = config.availableSubsystems.userData;
			SubsystemTypeSeq subsystemTypes = config.availableSubsystemTypes.userData;

			populateSubsystemTypesList(subsystemTypes.iterator());
			populateSubsystemsCombo(subsystems.iterator());
		}
	}
	
	@Inject @Optional
	public void followedAgentChanged(@Named(FreeFlyerStrings.PRIMARY_BEE) Agent followed) {
		if(followed == null) {
			return;
		}
		unsubscribe();
		m_agent = followed;
		m_agentLabel.setText(FreeFlyerStrings.CONNECTED_STRING + m_agent.toString());
		subscribe();
	}
}
