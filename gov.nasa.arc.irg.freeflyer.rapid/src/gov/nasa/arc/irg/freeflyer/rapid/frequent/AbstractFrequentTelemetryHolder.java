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
package gov.nasa.arc.irg.freeflyer.rapid.frequent;

import gov.nasa.arc.irg.freeflyer.rapid.FreeFlyerStrings;
import gov.nasa.dds.system.DdsTask;
import gov.nasa.rapid.v2.e4.Rapid;
import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.e4.message.IRapidMessageListener;
import gov.nasa.rapid.v2.e4.message.MessageType;
import gov.nasa.rapid.v2.e4.message.collector.RapidMessageCollector;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;

public abstract class AbstractFrequentTelemetryHolder implements IRapidMessageListener {
	protected String participantId = Rapid.PrimaryParticipant;
	protected Agent agent;
	protected Vector<FrequentTelemetryListener> listeners = new Vector<FrequentTelemetryListener>();
	protected MessageType sampleType;
	protected MessageType configType;
	protected Object sampleObject;
	protected Object configObject;
	protected Thread notifyThread;
	protected int SECONDS_BETWEEN_NOTIFICATIONS = 1;
	protected IEclipseContext topContext;
	
	protected void init() {
		createNotifyThread();
	}
	
	@Inject @Optional
	public void acceptAgent(@Named(FreeFlyerStrings.PRIMARY_BEE) Agent agent) {
		if(agent == null) {
			return;
		}
		unsubscribe();
		this.agent = agent;
		subscribe();
		if(!listeners.isEmpty() && !notifyThread.isAlive()) {
			notifyThread.start();
		}
	}
	
	@Override
	public void onRapidMessageReceived(Agent agent, MessageType msgType,
			Object msgObj, Object cfgObj) {
		 if(msgType.equals(sampleType)) { 
			sampleObject = msgObj;
		} else if(msgType.equals(configType)) {
			configObject = msgObj;
		}
	}
	
	protected void createNotifyThread() {
		notifyThread = new Thread() {
			@Override
			public synchronized void run() {
				while(listeners.size() > 0) {
					try {
						notifyListeners();
						Thread.sleep(SECONDS_BETWEEN_NOTIFICATIONS * 1000);
					} catch (InterruptedException e) { 
						return;
					}
				}
			}
		};
	}

	public void addListener(FrequentTelemetryListener l) {
		listeners.addElement(l);
		if(agent != null && !notifyThread.isAlive()) {
			notifyThread.start();
		}
	}

	public void removeListener(FrequentTelemetryListener l) {
		listeners.remove(l);
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
							AbstractFrequentTelemetryHolder.this);
				}
			}
		});
	}

	protected List<MessageType> getMessageTypes() {
		List<MessageType> ret = new ArrayList<MessageType>();
		ret.add(sampleType);
		if(configType != null) {
			ret.add(configType);
		}
		return ret;
	}

	public void unsubscribe() {
		if(getAgent() != null) {
			//			logger.debug("unsubscribe from all on "+getAgent().name() + " for AbstractTelemetryTablePart");
			RapidMessageCollector.instance().removeRapidMessageListener(getParticipantId(), getAgent(), this);
		}
	}
	
	public void preDestroy() {
		notifyThread.interrupt();
		unsubscribe();
	}

	protected void notifyListeners() {
		for(FrequentTelemetryListener asl : listeners) {
			asl.onSampleUpdate(sampleObject);
		}
	}

	public String getParticipantId() {
		return participantId;
	}
	public Agent getAgent() {
		return agent;
	}
}
