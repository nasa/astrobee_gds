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

import gov.nasa.arc.irg.freeflyer.rapid.CommandConfigWidget;
import gov.nasa.arc.verve.freeflyer.workbench.utils.AckListener;
import gov.nasa.rapid.v2.e4.Rapid;
import gov.nasa.rapid.v2.e4.agent.ActiveAgentSet;
import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.e4.agent.IActiveAgentSetListener;
import gov.nasa.rapid.v2.e4.message.IRapidMessageListener;
import gov.nasa.rapid.v2.e4.message.MessageType;
import gov.nasa.rapid.v2.e4.message.collector.RapidMessageCollector;

import java.util.HashMap;
import java.util.Iterator;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import rapid.CommandConfig;
import rapid.CommandDefSeq;
import rapid.Subsystem;
import rapid.SubsystemSeq;
import rapid.SubsystemType;
import rapid.SubsystemTypeSeq;

public class ConfigCommanderPart implements IRapidMessageListener, IActiveAgentSetListener {
	private static final Logger logger = Logger.getLogger(ConfigCommanderPart.class);
	private Combo m_partitionsCombo;
	private Combo m_subsystemsCombo;
	private String m_participantId = Rapid.PrimaryParticipant;
	protected Agent m_agent = null;
	private HashMap<String,SubsystemType> m_subsystemTypes = new HashMap<String,SubsystemType>();
	@Inject
	protected MApplication m_application;
	private CommandConfigWidget m_widget;
	private Composite m_parent;
	private int m_gridSquares = 2;
	protected AckListener ackListener;
	
	@Inject 
	public ConfigCommanderPart(Composite parent) {
		final ScrolledComposite sc1 = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		final Composite c1 = new Composite(sc1, SWT.NONE);
		sc1.setContent(c1);

		m_parent = c1;
		
		ActiveAgentSet.INSTANCE.addListener(this);
		ackListener = AckListener.getStaticInstance();
	}
	
	@PostConstruct
	public void postConstruct() {
		if( ackListener != null) {
			System.out.println("I have an AckListener in ConfigCommander");
		}
		makeTopPart();
	}

	public void makeTopPart() {
		GridLayout gl = new GridLayout(m_gridSquares, true);
		m_parent.setLayout(gl);
		
		m_partitionsCombo = new Combo(m_parent, SWT.READ_ONLY);
		GridDataFactory.fillDefaults().align(SWT.FILL,SWT.BEGINNING).grab(true, false).applyTo(m_partitionsCombo);
		populatePartitionsCombo();
		m_partitionsCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				unsubscribe();
				m_agent = Agent.valueOf(m_partitionsCombo.getText());
				subscribe();
			}
		});

		m_subsystemsCombo = new Combo(m_parent, SWT.READ_ONLY);
		GridDataFactory.fillDefaults().align(SWT.FILL,SWT.BEGINNING).grab(true, false).applyTo(m_subsystemsCombo);
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
		
		repackTheParent();
	}
	
	public void setSelectedElement(CommandDefSeq cmds, String subsystemName) {
		try {
			if (cmds == null){
				if (m_widget != null){

					m_widget.dispose();
					m_widget = null;
					repackTheParent();
					return;
				}
			} else {
				if (m_widget != null) {
					m_widget.dispose();
				} 

				m_widget = new CommandConfigWidget(m_parent, m_agent, cmds, subsystemName);
				m_widget.setVisible(true);
				repackTheParent();
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
				
				repackTheParent();
			}
		});
	}
	
	private void repackTheParent() {
		m_parent.setSize(m_parent.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		m_parent.pack();
		m_parent.layout();
		m_parent.update();
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
				
				repackTheParent();
			}
		});
	}

	private void populatePartitionsCombo() {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				String namesArray[] = makePartitionsList();
				m_partitionsCombo.setItems(namesArray);
				m_partitionsCombo.pack();
				
				repackTheParent();
			}
		});
	}

	protected String[] makePartitionsList() {
		Agent[] agents = ActiveAgentSet.asArray();

		String[] agentStrings = new String[agents.length];
		for(int i=0; i<agents.length; i++) {
			agentStrings[i] = agents[i].name();
		}
		return agentStrings;
	}

	public void subscribe() {
		if (getParticipantId() == null || getParticipantId().isEmpty() || getAgent() == null){
			return;
		}
		//		logger.debug("subscribe on "+getAgent().name() + " for view " + getTitle());

		RapidMessageCollector.instance().addRapidMessageListener(getParticipantId(), 
				getAgent(), 
				MessageType.COMMAND_CONFIG_TYPE, 
				this);
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

	public void activeAgentSetChanged() {
		populatePartitionsCombo();
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

	@Override
	public void activeAgentAdded(Agent agent, String participantId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void activeAgentRemoved(Agent agent) {
		// TODO Auto-generated method stub
		
	}
}
