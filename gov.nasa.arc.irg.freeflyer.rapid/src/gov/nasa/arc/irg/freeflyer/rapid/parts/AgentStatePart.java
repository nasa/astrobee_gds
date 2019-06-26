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

import gov.nasa.arc.irg.freeflyer.rapid.FreeFlyerMessageType;
import gov.nasa.arc.irg.rapid.ui.e4.AgentStateWidget;
import gov.nasa.arc.irg.rapid.ui.e4.MessageBundle;
import gov.nasa.arc.irg.rapid.ui.e4.view.AbstractTelemetryPart;
import gov.nasa.arc.irg.util.NameValue;
import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.e4.message.MessageType;
import gov.nasa.rapid.v2.e4.message.MessageTypeExt;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import rapid.AgentConfig;
import rapid.AgentState;

public class AgentStatePart extends AbstractTelemetryPart {
	private static final Logger logger = Logger.getLogger(AgentStatePart.class);
	protected AgentConfig m_configObject = null;
	protected Composite m_container;
	protected AgentStateWidget m_widget;

	@Inject 
	public AgentStatePart(Composite parent) {
		m_container = new Composite(parent, SWT.NONE);
		topicsComboHorizontalSpan = 3;
		createPartControl(m_container);
		baseSampleType = MessageType.AGENT_STATE_TYPE;
		baseConfigType = MessageType.AGENT_CONFIG_TYPE;
	}

	@Override
	protected void setConfigObject(Object config) {
		if (config == null){
			m_configObject = null;
			return;
		}
		if (config.equals(m_configObject)) {
			return;
		}
		if (config instanceof AgentConfig){
			m_configObject = (AgentConfig)config;
		}
		setupAgentStateWidget();
	}

	@Override
	protected Class getConfigClass() {
		return AgentConfig.class;
	}

	@Override
	protected Object getConfigObject() {
		return m_configObject;
	}
	@Override
	protected String getMementoKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected NameValue[] getNameValues(Object input) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean configIdMatchesSampleId(Object configObj, Object eventObj) {
		if (configObj == null){
			return false;
		}
		final AgentConfig config = (AgentConfig)configObj;
		int configID = config.hdr.serial;

		final AgentState sample = (AgentState)eventObj;
		int sampleID = sample.hdr.serial;
		// XXX Huh???
		if (configID == sampleID){
			//        	m_timestamp.updateText(getSimpleTime(sample.hdr.timeStamp));
			return true;
		}
		return false;
	}

	@Override
	public void createPartControl(Composite c) {

		m_container.setLayout(new GridLayout(4,false));

		createPreTableParts(m_container);		
		// >> uncomment this line to automatically call call()
		//setupTimerTask();
	}

	@Override
	protected void onSubtopicSelected(String subtopic) {
		// nada
	}

	protected void setupAgentStateWidget() {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				try {
					if (m_configObject == null){
						if (m_widget != null){

							m_widget.dispose();
							m_widget = null;
							m_container.layout(true);
							m_container.pack();
							m_container.update();
							return;
						}
					} else {
						if (m_widget != null) {
							m_widget.dispose();
						} 
						m_widget = new AgentStateWidget(m_container, m_configObject, sampleType);
						m_widget.setVisible(true);
						m_container.layout(true);
						m_container.pack();
						m_container.update();
					}
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
			}
		});
	}

	@Override
	protected MessageType getTheMessageType(String topic) {
		MessageType standardType = MessageType.getTypeFromTopic(topic);
		if(standardType != null) {
			return standardType;
		}
		standardType = MessageTypeExt.getTypeFromTopic(topic);
		if(standardType != null) {
			return standardType;
		}
		standardType = FreeFlyerMessageType.getTypeFromTopic(topic);
		if(standardType != null) {
			return standardType;
		}
		logger.error("I don't know that MessageType: " + topic);

		return null;
	}

	@Override
	protected MessageType getValueOf(String typeName) {
		MessageType standardType = MessageType.valueOf(typeName);
		if(standardType != null) {
			return standardType;
		}
		standardType = MessageTypeExt.valueOf(typeName);
		if(standardType != null) {
			return standardType;
		}
		standardType = FreeFlyerMessageType.valueOf(typeName);
		if(standardType != null) {
			return standardType;
		}
		logger.error("I don't know that MessageType: " + typeName);

		return null;
	}

	// >> just delete this override and put setupTimerTask back in createPartControl
	// >> to go back to updating every second
	@Override
	public void onRapidMessageReceived(Agent agent, MessageType msgType,
			Object eventObj, Object configObj) {
		super.onRapidMessageReceived(agent, msgType, eventObj, configObj);
		try {
			final Display display = Display.getDefault();

			try {
				if (!display.isDisposed()){
					display.asyncExec(new Runnable() {
						public void run() {
							try {
								call();
							} catch (Exception e) {
								logger.error("call", e);
							}
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

	@Override
	public Boolean call() throws Exception {
		// Needs to send latest values to AgentStateWidget
		MessageBundle latestEvent = null;
		if (getAgent() != null){
			latestEvent = getLastMessageToProcess(getSampleType());
		}

		if(latestEvent != null){
			if(latestEvent.configObj == null){
				logger.warn(" - SubscriptionView received message with no config");
				return Boolean.FALSE;
			}

			// Setup the content if things match up
			if (configIdMatchesSampleId(latestEvent.configObj, latestEvent.eventObj)){
				if(m_widget != null && !m_widget.isDisposed()) {
					m_widget.updateData(latestEvent.messageType, latestEvent.eventObj);
					return Boolean.TRUE;
				}
			}
		}
		return Boolean.FALSE;
	}

}
