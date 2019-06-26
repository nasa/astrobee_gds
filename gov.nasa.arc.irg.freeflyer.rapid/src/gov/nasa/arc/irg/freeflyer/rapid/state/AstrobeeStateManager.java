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
package gov.nasa.arc.irg.freeflyer.rapid.state;

import gov.nasa.arc.irg.freeflyer.rapid.CommandPublisher;
import gov.nasa.arc.irg.freeflyer.rapid.FreeFlyerStrings;
import gov.nasa.arc.irg.plan.modulebay.ModuleBayPlan;
import gov.nasa.dds.system.DdsTask;
import gov.nasa.rapid.idl.ext.astrobee.message.MessageTypeExtAstro;
import gov.nasa.rapid.v2.e4.Rapid;
import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.e4.message.IRapidMessageListener;
import gov.nasa.rapid.v2.e4.message.MessageType;
import gov.nasa.rapid.v2.e4.message.collector.RapidMessageCollector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.swt.widgets.Display;

import rapid.AccessControlState;
import rapid.PositionConfig;
import rapid.PositionSample;
import rapid.ext.astrobee.AgentState;
import rapid.ext.astrobee.ArmState;
import rapid.ext.astrobee.CompressedFile;
import rapid.ext.astrobee.DataToDiskState;
import rapid.ext.astrobee.DataTopicsList;
import rapid.ext.astrobee.FaultConfig;
import rapid.ext.astrobee.FaultState;
import rapid.ext.astrobee.InertialProperties;
import rapid.ext.astrobee.PlanStatus;
import rapid.ext.astrobee.SETTINGS;
import rapid.ext.astrobee.SETTINGS_METHOD_SET_ZONES;
import rapid.ext.astrobee.TelemetryConfig;
import rapid.ext.astrobee.TelemetryState;

import com.ardor3d.math.Quaternion;
import com.ardor3d.math.Vector3;

/**
 * This class handles listening to RAPID for state updates, and distributing
 * information to listeners.
 * NOTE: not all MessageTypes are repeated through to listeners
 */
public class AstrobeeStateManager implements IRapidMessageListener  {
	private static final Logger logger = Logger.getLogger(AstrobeeStateManager.class);
	protected String participantId = Rapid.PrimaryParticipant;
	protected Agent agent;
	protected MessageType[] sampleType;
	protected MessageType configType;
	protected List<AstrobeeStateListener> generalListeners;// interested in all topics
	protected Map<MessageType, List<AstrobeeStateListener>> listeners;
	protected AggregateAstrobeeState aggregateState;
	protected AggregateAstrobeeState lastSentAggregateState;
	protected AstrobeeStateAdapter adapter;
	protected CommandPublisher commandPublisher;

	protected IEclipseContext topContext;

	@Inject
	public AstrobeeStateManager(IEclipseContext context) {
		generalListeners = new CopyOnWriteArrayList<AstrobeeStateListener>();
		listeners = new ConcurrentHashMap<MessageType, List<AstrobeeStateListener>>();
		MApplication application = context.get(MApplication.class);
		topContext = application.getContext();

		init();
	}

	protected void init() {
		sampleType = new MessageType[] {
				MessageType.ACCESSCONTROL_STATE_TYPE,
				MessageTypeExtAstro.AGENT_STATE_TYPE,
				MessageTypeExtAstro.ARM_STATE_TYPE,
				MessageTypeExtAstro.COMPRESSED_FILE_ACK_TYPE,
				MessageTypeExtAstro.CURRENT_PLAN_COMPRESSED_TYPE,
				MessageTypeExtAstro.CURRENT_ZONES_COMPRESSED_TYPE,
				MessageTypeExtAstro.DATA_TOPICS_LIST_TYPE,
				MessageTypeExtAstro.DATA_TO_DISK_STATE_TYPE,
				MessageTypeExtAstro.FAULT_CONFIG_TYPE,
				MessageTypeExtAstro.FAULT_STATE_TYPE,
				MessageTypeExtAstro.INERTIAL_PROPERTIES_TYPE,
				MessageTypeExtAstro.PLAN_STATUS_TYPE,
				MessageTypeExtAstro.TELEMETRY_CONFIG_TYPE,
				MessageTypeExtAstro.TELEMETRY_STATE_TYPE,
				MessageType.POSITION_CONFIG_TYPE,
				MessageType.POSITION_SAMPLE_TYPE
		};
		aggregateState = AggregateAstrobeeState.getInstance();
		lastSentAggregateState = new AggregateAstrobeeState();
		adapter = new AstrobeeStateAdapter(aggregateState);
		topContext.set(AstrobeeStateManager.class, this);
	}

	public AggregateAstrobeeState getAggregateAstrobeeState() {
		return aggregateState;
	}

	@Inject @Optional
	public void acceptAgent(@Named(FreeFlyerStrings.PRIMARY_BEE) Agent agent) {
		if(agent == null) {
			return;
		}
		unsubscribe();
		aggregateState.clearAll();
		this.agent = agent;
		commandPublisher = CommandPublisher.getInstance(agent);
		subscribe();
		GrabControlManager.get(agent);
	}

	protected void sendSetZonesCommand() {
		commandPublisher.sendGenericNoParamsCommand(SETTINGS_METHOD_SET_ZONES.VALUE, SETTINGS.VALUE);
	}

	@Override
	public void onRapidMessageReceived(Agent agent, MessageType msgType,
			Object msgObj, Object cfgObj) {
		aggregateState.setValid(true);
		if(msgType.equals(MessageType.ACCESSCONTROL_STATE_TYPE)) { 
			AccessControlState accessControlState = (AccessControlState) msgObj;
			aggregateState.ingestAccessControlState(accessControlState);
			notifyListeners(MessageType.ACCESSCONTROL_STATE_TYPE);
		}
		else if(msgType.equals(MessageTypeExtAstro.AGENT_STATE_TYPE)) { // slow
			AgentState agentState = (AgentState) msgObj;
			aggregateState.ingestAgentState(agentState);
			notifyListeners(MessageTypeExtAstro.AGENT_STATE_TYPE);
		}
		else if(msgType.equals(MessageTypeExtAstro.ARM_STATE_TYPE)) { // slow
			ArmState armState = (ArmState) msgObj;
			aggregateState.ingestArmState(armState);
			notifyListeners(MessageTypeExtAstro.ARM_STATE_TYPE);
		}
		else if(msgType.equals(MessageTypeExtAstro.CURRENT_PLAN_COMPRESSED_TYPE)) { 
			CompressedFile compFile = (CompressedFile) msgObj;
			ModuleBayPlan currentPlan = aggregateState.ingestCurrentPlanCompressedFile(compFile);

			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					topContext.set(FreeFlyerStrings.FREE_FLYER_PLAN, null);
					topContext.set(FreeFlyerStrings.FREE_FLYER_PLAN, currentPlan);
				}
			});
			
			notifyListeners(MessageTypeExtAstro.CURRENT_PLAN_COMPRESSED_TYPE);
		}
		else if(msgType.equals(MessageTypeExtAstro.CURRENT_ZONES_COMPRESSED_TYPE)) { 
			CompressedFile compFile = (CompressedFile) msgObj;
			aggregateState.ingestCurrentZonesCompressedFile(compFile);
		
			notifyListeners(MessageTypeExtAstro.CURRENT_ZONES_COMPRESSED_TYPE);
		}
		else if(msgType.equals(MessageTypeExtAstro.DATA_TO_DISK_STATE_TYPE)) {
			DataToDiskState diskState = (DataToDiskState) msgObj;
			aggregateState.ingestDataToDiskState(diskState);
			notifyListeners(MessageTypeExtAstro.DATA_TO_DISK_STATE_TYPE);
		}
		else if(msgType.equals(MessageTypeExtAstro.DATA_TOPICS_LIST_TYPE)) {
			DataTopicsList diskConfig = (DataTopicsList) msgObj;
			aggregateState.ingestDataTopicsList(diskConfig);
			notifyListeners(MessageTypeExtAstro.DATA_TOPICS_LIST_TYPE);
		}
		else if(msgType.equals(MessageTypeExtAstro.FAULT_CONFIG_TYPE)) {
			FaultConfig faultConfig = (FaultConfig) msgObj;
			aggregateState.ingestFaultConfig(faultConfig);
			notifyListeners(MessageTypeExtAstro.FAULT_CONFIG_TYPE);
		}
		else if(msgType.equals(MessageTypeExtAstro.FAULT_STATE_TYPE)) { 
			FaultState faultState = (FaultState) msgObj;
			aggregateState.ingestFaultState(faultState);
			notifyListeners(MessageTypeExtAstro.FAULT_STATE_TYPE);
		}
		else if(msgType.equals(MessageTypeExtAstro.INERTIAL_PROPERTIES_TYPE)) {
			InertialProperties inertialProperties = (InertialProperties) msgObj;
			aggregateState.ingestInertialProperties(inertialProperties);
			notifyListeners(MessageTypeExtAstro.INERTIAL_PROPERTIES_TYPE);
		}
		else if(msgType.equals(MessageTypeExtAstro.PLAN_STATUS_TYPE)) {
			PlanStatus planStatus = (PlanStatus) msgObj;
			aggregateState.ingestPlanStatus(planStatus);
			notifyListeners(MessageTypeExtAstro.PLAN_STATUS_TYPE);
		}
		else if(msgType.equals(MessageType.POSITION_CONFIG_TYPE)) {
			PositionConfig positionConfig = (PositionConfig) msgObj;
			aggregateState.ingestPositionConfig(positionConfig);
		}
		else if(msgType.equals(MessageType.POSITION_SAMPLE_TYPE)) {
			PositionSample ps = (PositionSample) msgObj;
			aggregateState.ingestPositionSample(ps);
		}
		else if(msgType.equals(MessageTypeExtAstro.TELEMETRY_CONFIG_TYPE)) {
			TelemetryConfig tc = (TelemetryConfig) msgObj;
			aggregateState.ingestTelemetryConfig(tc);
			notifyListeners(MessageTypeExtAstro.TELEMETRY_CONFIG_TYPE);
		}
		else if(msgType.equals(MessageTypeExtAstro.TELEMETRY_STATE_TYPE)) { // slow
			TelemetryState telemetry = (TelemetryState) msgObj;
			aggregateState.ingestTelemetryState(telemetry);
			notifyListeners(MessageTypeExtAstro.TELEMETRY_STATE_TYPE);
		}
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
							AstrobeeStateManager.this);
				}
			}
		});
	}

	protected List<MessageType> getMessageTypes() {
		List<MessageType> ret = new ArrayList<MessageType>();
		for(int i=0; i<getSampleType().length; i++) {
			ret.add(getSampleType()[i]);
		}
		ret.add(getConfigType());
		return ret;
	}

	public void unsubscribe() {
		if(getAgent() != null) {
			//			logger.debug("unsubscribe from all on "+getAgent().name() + " for AbstractTelemetryTablePart");
			RapidMessageCollector.instance().removeRapidMessageListener(getParticipantId(), getAgent(), this);
		}
	}

	public String getParticipantId() {
		return participantId;
	}
	public Agent getAgent() {
		return agent;
	}
	protected MessageType[] getSampleType() {
		return sampleType;
	}

	protected MessageType getConfigType() {
		return configType;
	}

	public void addListener(AstrobeeStateListener l) {
		generalListeners.add(l);
		l.onAstrobeeStateChange(aggregateState); //I don't think we need this
	}
	
	public void addListener(AstrobeeStateListener l, List<MessageType> msgTypes) {
		for(MessageType msgType : msgTypes) {
			addListener(l, msgType);
		}
	}

	public void addListener(AstrobeeStateListener l, MessageType msgType) {
		List<AstrobeeStateListener> list = listeners.get(msgType);
		if(list == null) {
			list = new CopyOnWriteArrayList<AstrobeeStateListener>();
		}
		list.add(l);
		listeners.put(msgType, list);
		l.onAstrobeeStateChange(aggregateState);
	}

	public void removeListener(AstrobeeStateListener l) {
		generalListeners.remove(l);
		for(MessageType msgType : listeners.keySet()) {
			List<AstrobeeStateListener> oldList = listeners.get(msgType);
			List<AstrobeeStateListener> newList = new CopyOnWriteArrayList<AstrobeeStateListener>();
			for(AstrobeeStateListener asl : oldList) {
				if(!asl.equals(l)) {
					newList.add(asl);
				}
			}
			listeners.put(msgType, newList);
		}
	}

	protected void notifyListeners(MessageType msgType) {
		if(aggregateState.equals(lastSentAggregateState)) {
			return;
		}

		notifySpecificListeners(msgType);
		notifyGeneralListeners();
		
		lastSentAggregateState.copyFrom(aggregateState);
	}

	protected void notifySpecificListeners(MessageType msgType) {
		List<AstrobeeStateListener> list = listeners.get(msgType);
		if(list != null) {
			for(AstrobeeStateListener asl : list) {
				asl.onAstrobeeStateChange(aggregateState);
			}
		}
	}

	protected void notifyGeneralListeners() {
		for(AstrobeeStateListener asl : generalListeners) {
			asl.onAstrobeeStateChange(aggregateState);
		}
	}

	public AstrobeeStateAdapter getAdapter() {
		return adapter;
	}

	public Vector3 getAstrobeePosition() {
		return aggregateState.getPositionGds().getXYZ();
	}

	public Quaternion getAstrobeeOrientation() {
		return aggregateState.getPositionGds().getQuaternion();
	}

	public void startRequestingControl() {
		GrabControlManager.get(agent).startRequestingControl();
	}
	
	public void startSendingZones() {
		SendZonesManager.get(agent).sendCompressedZones();
	}
}
