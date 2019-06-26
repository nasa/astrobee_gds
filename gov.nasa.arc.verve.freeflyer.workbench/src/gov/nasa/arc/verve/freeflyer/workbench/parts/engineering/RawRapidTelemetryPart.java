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

import gov.nasa.arc.irg.freeflyer.rapid.FreeFlyerMessageType;
import gov.nasa.rapid.v2.e4.Rapid;
import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.e4.message.IRapidMessageListener;
import gov.nasa.rapid.v2.e4.message.MessageType;
import gov.nasa.rapid.v2.e4.message.MessageTypeExt;
import gov.nasa.rapid.v2.e4.message.collector.RapidMessageCollector;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

public class RawRapidTelemetryPart implements IRapidMessageListener{
	private static final Logger logger = Logger.getLogger(RawRapidTelemetryPart.class);
	private Text myText;
	private String m_participantId = Rapid.PrimaryParticipant;
	private List<StringTriple> m_stringTriples = new ArrayList<StringTriple>();
	private final String separate = "----------------------------------------\n";
	private final String nothingSelected="Select a topic in Received Topics Part to see incoming RAPID messages.";

	@Inject 
	public RawRapidTelemetryPart(Composite parent, MApplication mapp) {
		myText= new Text(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.READ_ONLY);
		myText.setText(nothingSelected);

		IEclipseContext iec = mapp.getContext();
		iec.set(RawRapidTelemetryPart.class, this);
	}

	public void clear() {
		myText.setText(nothingSelected);
		m_stringTriples.clear();
	}

	synchronized public boolean addTopic(String topic, String partition) {
		MessageType mt = getTheMessageType(topic);
		if(mt == null) {
			logger.error("No luck with MessageType "+topic);
			return false;
		}
		Agent agent = Agent.valueOf(partition);
		m_stringTriples.add(new StringTriple(agent, topic, mt));
		subscribe(mt, agent);
		updateText();
		return true;
	}

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

	synchronized public void removeTopic(String topic, String partition) {
		MessageType mt = MessageType.getTypeFromTopic(topic);
		StringTriple toRemove = null;
		Agent agent = Agent.valueOf(partition);
		for(StringTriple st : m_stringTriples) {
			if(st.matchesAgentAndTopic(agent, mt)) {
				toRemove = st;
				break;
			}
		}
		if(toRemove != null) {
			m_stringTriples.remove(toRemove);
			unsubscribe(mt, agent);
			updateText();
		}
	}

	synchronized private void updateText() {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				StringBuilder builder = new StringBuilder();
				for(StringTriple s : m_stringTriples) {
					builder.append(separate);
					builder.append(s + "\n");
				}
				if(!myText.isDisposed()) {
					if(m_stringTriples.isEmpty()) {
						myText.setText(nothingSelected);
					} else {
						myText.setText(builder.toString());
					}
				}
			}
		});
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

	synchronized public void onRapidMessageReceived(Agent agent, MessageType msgType,
			Object msgObj, Object cfgObj) {
		for(StringTriple st : m_stringTriples) {
			//			if(msgType.equals(st.getMessageType())) {
			if(st.matchesAgentAndTopic(agent, msgType)) {
				st.setLastMessageString(msgObj.toString());
				updateText();
			}
		}
	}

	// class to store m_topic, MessageType, and last message
	private class StringTriple {
		Agent m_agent;
		String m_topic;
		MessageType m_type;
		String m_lastMessageString;

		StringTriple(Agent agent, String topic, MessageType type) {
			m_agent = agent;
			m_topic = topic;
			m_type = type;
			m_lastMessageString = ".";
		}

		void setLastMessageString(String msg) {
			m_lastMessageString = msg;
		}

		boolean matchesAgentAndTopic(Agent agent, MessageType type) {
			if(m_agent.equals(agent) && m_type.equals(type)) {
				return true;
			}
			return false;
		}

		@Override
		public
		String toString() {
			return m_agent.toString() + "  : " +  m_topic + " : " + m_type.getDataTypeClass().getSimpleName() + "\n" + m_lastMessageString;
		}
	}
}
