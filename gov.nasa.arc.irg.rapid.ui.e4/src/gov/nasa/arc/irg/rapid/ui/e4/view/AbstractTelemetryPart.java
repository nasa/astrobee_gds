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
package gov.nasa.arc.irg.rapid.ui.e4.view;

import gov.nasa.arc.irg.rapid.ui.e4.MessageBundle;
import gov.nasa.arc.irg.util.NameValue;
import gov.nasa.rapid.v2.e4.Rapid;
import gov.nasa.rapid.v2.e4.agent.ActiveAgentSet;
import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.e4.agent.DiscoveredAgentRepository;
import gov.nasa.rapid.v2.e4.agent.IActiveAgentSetListener;
import gov.nasa.rapid.v2.e4.message.IRapidMessageListener;
import gov.nasa.rapid.v2.e4.message.MessageType;
import gov.nasa.rapid.v2.e4.message.MessageTypeExt;
import gov.nasa.rapid.v2.e4.message.collector.RapidMessageCollector;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.PreDestroy;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public abstract class AbstractTelemetryPart implements IRapidMessageListener, IActiveAgentSetListener {
	private static final Logger logger = Logger.getLogger(AbstractTelemetryPart.class);
	protected String selectedPartition = "";

	protected HashMap<MessageType, MessageBundle> messageCache = new HashMap<MessageType, MessageBundle>();
	protected String participantId = Rapid.PrimaryParticipant;
	protected Agent agent = null;
	protected Combo partitionsCombo;
	protected Combo topicsCombo;
	protected int topicsComboHorizontalSpan = 1;

	protected Timer timer = new Timer(false); // this is the timer that will run the throttled updates
	protected TimerTask updateTask = null;   // this is the actual update

	// if an agent has subtopics for the MessageType of this View, this is the list of them
	//	protected Map<Agent, List<String>> m_agentTopics = new HashMap<Agent, List<String>>();
	protected List<String> topicsList = new ArrayList<String>();
	protected final String SEPARATOR = MessageType.topicSeparator;

	protected MessageType baseSampleType;
	protected MessageType baseConfigType;

	protected MessageType sampleType;
	protected MessageType configType;
	
	/**
	 * generally this is for formatting numbers, which should be right aligned if they are in a table.
	 */
	public static DecimalFormat decimalFormat = new DecimalFormat("###.##"); 
	{
		decimalFormat.setDecimalSeparatorAlwaysShown(true);
		decimalFormat.setMinimumFractionDigits(2);
		decimalFormat.setMaximumFractionDigits(2);
		decimalFormat.setPositivePrefix(" ");
	}

	protected abstract Object getConfigObject();
	protected abstract void setConfigObject(Object config);
	protected abstract Class getConfigClass();
	protected abstract String getMementoKey();
	protected abstract NameValue[] getNameValues(Object input);
	protected abstract boolean configIdMatchesSampleId(Object configObj, Object eventObj);
	protected abstract void onSubtopicSelected(String subtopic);

	public AbstractTelemetryPart() {
		ActiveAgentSet.INSTANCE.addListener(this);
	}

	protected void onCreatePartControlComplete() {
		setupTimerTask();
	}

	protected void createPreTableParts(Composite container) {
		GridData gdOneWide = new GridData(SWT.BEGINNING, SWT.BEGINNING, true, false);

		partitionsCombo = new Combo(container, SWT.READ_ONLY);
		partitionsCombo.setSize(85, 20);
		populatePartitionsCombo();
		partitionsCombo.setLayoutData(gdOneWide);
		partitionsCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectedPartition = partitionsCombo.getText();
				agent = Agent.valueOf(selectedPartition);
				populateSubtopicsCombo();
			}
		});

		topicsCombo = new Combo(container, SWT.READ_ONLY);
		topicsCombo.setSize(85, 20);
		GridData gdTopicsCombo = new GridData(SWT.BEGINNING, SWT.BEGINNING, true, false);
		gdTopicsCombo.horizontalSpan = topicsComboHorizontalSpan;
		topicsCombo.setLayoutData(gdTopicsCombo);
		topicsCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				unsubscribe();
				String subtopic = topicsCombo.getText();
				sampleType = getTheMessageType(subtopic);
				if(sampleType != null) {
					configType = getValueOf(sampleType.getConfigName());
				}
				subscribe();
				onSubtopicSelected(subtopic);
			}
		});
		container.layout(true);
		container.pack();
	}
	
	// override this to get FreeFlyerMessageTypes
	protected MessageType getValueOf(String typeName) {
		MessageType standardType = MessageType.valueOf(typeName);
		if(standardType != null) {
			return standardType;
		}
		standardType = MessageTypeExt.valueOf(typeName);
		if(standardType != null) {
			return standardType;
		}
		logger.error("I don't know that MessageType: " + typeName);
		
		return null;
	}
	
	// override this to get FreeFlyerMessageTypes
	protected MessageType getTheMessageType(String topic) {
			MessageType standardType = MessageType.getTypeFromTopic(topic);
			if(standardType != null) {
				return standardType;
			}
			standardType = MessageTypeExt.getTypeFromTopic(topic);
			if(standardType != null) {
				return standardType;
			}
			logger.error("I don't know that MessageType: " + topic);
			
			return null;
	}

	protected void populateSubtopicsCombo() {
		populateTopicsList();
		if(!topicsList.isEmpty()) {
			String subtopicsArray[] = topicsList.toArray(new String[topicsList.size()]);
			topicsCombo.setItems(subtopicsArray);
			topicsCombo.pack();
		}
	}

	protected void populatePartitionsCombo() {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				String namesArray[] = makePartitionsList();
				partitionsCombo.setItems(namesArray);
				partitionsCombo.pack();
			}
		});
	}

	// make sure to call createPreTableParts(container)
	// and onCreatePartControlComplete();

	abstract public void createPartControl(Composite parent);

	protected String[] makePartitionsList() {
		Agent[] agents = ActiveAgentSet.asArray();

		String[] agentStrings = new String[agents.length];
		for(int i=0; i<agents.length; i++) {
			agentStrings[i] = agents[i].name();
		}
		return agentStrings;
	}

	/**
	 * Format a float to ###.##
	 * @param value
	 * @return
	 */
	public String format(Number value){
		String result = decimalFormat.format(value);
		return result;
	}

	@Override
	public void onRapidMessageReceived(Agent agent, MessageType msgType,
			Object eventObj, Object configObj) {
		if (msgType.equals(getConfigType())){
			if (eventObj != null && eventObj.getClass().equals(getConfigClass())){
				setConfigObject(eventObj);
			}
		} else {

			if(msgType.equals(getSampleType())) {

				if (configObj != null && configObj.getClass().equals(getConfigClass())){
					setConfigObject(configObj);
				}

				messageCache.put(msgType, new MessageBundle(agent, msgType, eventObj, getConfigObject())); 
			}
		}
	}

	protected void populateTopicsList() {
		topicsList.clear();

		for (String topic : DiscoveredAgentRepository.INSTANCE.getTopicsFor(selectedPartition)) {
			// check if the topic matches our base type
			if(topic.contains(baseSampleType.getTopicName())) {
				topicsList.add(topic);
			}
		}
	}

	public void subscribe() {
		if (getParticipantId() == null || getParticipantId().isEmpty() || getAgent() == null){
			return;
		}
		//			logger.debug("subscribe on "+getAgent().name() + " for view " + getTitle());
		for(MessageType mt : getMessageTypes()) {
			if (mt != null){
				RapidMessageCollector.instance().addRapidMessageListener(getParticipantId(), 
						getAgent(), 
						mt, 
						this);
			}
		}
	}

	protected List<MessageType> getMessageTypes() {
		List<MessageType> ret = new ArrayList<MessageType>();
		ret.add(getSampleType());
		ret.add(getConfigType());
		return ret;
	}

	public void unsubscribe() {
		if(getAgent() != null) {
			logger.debug("unsubscribe from all on "+getAgent().name() + " for AbstractTelemetryTablePart");
			RapidMessageCollector.instance().removeRapidMessageListener(getParticipantId(), getAgent(), this);
		}
	}
	/**
	 * create and schedule the 1 second update
	 * Be sure to call this at creatPartControl
	 */
	protected void setupTimerTask() {
		updateTask = new TimerTask() {
			// grab display because OSX chokes if Display.getDefault() is called during shutdown
			final Display display = Display.getDefault();
			@Override
			public void run() {
				try {
					// this gives org.eclipse.swt.SWTException: Invalid thread access
					//Display display = Display.getDefault();
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
			}
		};
		timer.schedule(updateTask, 0, 1000 * getRefreshSeconds());
	}

	@PreDestroy
	public void preDestroy() {
		unsubscribe();
		ActiveAgentSet.INSTANCE.removeListener(this);
	}
	
	abstract public Boolean call() throws Exception;

	/**
	 * Retrieve the last unprocessed message bundle of this type.  
	 * This also removes it from the cache.
	 * @param messageType
	 * @return
	 */
	protected MessageBundle getLastMessageToProcess(MessageType messageType){
		MessageBundle found = messageCache.remove(messageType);
		//			 if (found != null){
		//				 System.out.println("FOUND " + found.toString());
		//			 }
		return found;
	}

	public String getParticipantId() {
		return participantId;
	}

	public Agent getAgent() {
		return agent;
	}

	/**
	 * @return how many seconds between UI refresh
	 */
	protected int getRefreshSeconds() {
		return 1;
	}

	public void activeAgentSetChanged() {
		populatePartitionsCombo();
	}

	protected MessageType getSampleType() {
		return sampleType;
	}

	protected MessageType getConfigType() {
		return configType;
	}
	
	@Override
	public void activeAgentRemoved(Agent agent) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void activeAgentAdded(Agent agent, String participantId) {
		// TODO Auto-generated method stub
		
	}
}
