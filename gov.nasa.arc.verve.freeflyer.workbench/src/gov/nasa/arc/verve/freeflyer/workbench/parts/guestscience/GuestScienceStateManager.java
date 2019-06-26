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
package gov.nasa.arc.verve.freeflyer.workbench.parts.guestscience;

import gov.nasa.arc.irg.freeflyer.rapid.FreeFlyerStrings;
import gov.nasa.arc.irg.freeflyer.rapid.state.GuestScienceApkGdsRunning;
import gov.nasa.arc.irg.plan.freeflyer.config.GuestScienceApkGds.GuestScienceCommandGds;
import gov.nasa.arc.verve.freeflyer.workbench.utils.AgentsFromCommandLine;
import gov.nasa.rapid.idl.ext.astrobee.message.MessageTypeExtAstro;
import gov.nasa.rapid.v2.e4.Rapid;
import gov.nasa.rapid.v2.e4.agent.ActiveAgentSet;
import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.e4.agent.IActiveAgentSetListener;
import gov.nasa.rapid.v2.e4.message.IRapidMessageListener;
import gov.nasa.rapid.v2.e4.message.MessageType;
import gov.nasa.rapid.v2.e4.message.collector.RapidMessageCollector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.MApplication;

import rapid.ext.astrobee.GuestScienceApk;
import rapid.ext.astrobee.GuestScienceConfig;
import rapid.ext.astrobee.GuestScienceData;
import rapid.ext.astrobee.GuestScienceDataType;
import rapid.ext.astrobee.GuestScienceState;

// Container for all data coming from GS config, GS state, and GS data messages
public class GuestScienceStateManager implements IRapidMessageListener, IActiveAgentSetListener {
	private static final Logger logger = Logger.getLogger(GuestScienceStateManager.class);
	protected Map<Agent,Vector<GuestScienceApkGdsRunning>> apkMap;
	protected Map<Agent,Boolean> doneInitializingApkMap;
	protected Map<Agent, TreeMap<String, TreeMap<String, TreeMap<String, String>>>> guestScienceDataMap;
	protected Agent[] workbenchSelectedAgents, allAgents;

	protected int NUM_BEES;
	protected Set<GuestScienceStateListener> listeners;
	protected String participantId = Rapid.PrimaryParticipant;
	protected IEclipseContext context;
	protected MessageType[] sampleType;
	protected String blankDash = "-";

	// for if we get a sample before a config
	protected Map<Agent,GuestScienceState> savedStates;

	@Inject
	public GuestScienceStateManager(MApplication application) {
		context = application.getContext();
		apkMap = new HashMap<Agent,Vector<GuestScienceApkGdsRunning>>();
		doneInitializingApkMap = new HashMap<Agent, Boolean>();
		guestScienceDataMap = new TreeMap<Agent,TreeMap<String,TreeMap<String,TreeMap<String,String>>>>();
		savedStates = new HashMap<Agent,GuestScienceState>();

		listeners = new HashSet<GuestScienceStateListener>();

		sampleType = new MessageType[] {
				MessageTypeExtAstro.GUEST_SCIENCE_CONFIG_TYPE,
				MessageTypeExtAstro.GUEST_SCIENCE_STATE_TYPE,
				MessageTypeExtAstro.GUEST_SCIENCE_DATA_TYPE,
		};

		NUM_BEES = AgentsFromCommandLine.INSTANCE.getNumAgents();
		workbenchSelectedAgents = new Agent[NUM_BEES];
		allAgents = new Agent[NUM_BEES];
		ActiveAgentSet.INSTANCE.addListener(this);

		context.set(GuestScienceStateManager.class, this);

		for(int i=0; i<NUM_BEES; i++) {
			allAgents[i] = AgentsFromCommandLine.INSTANCE.getAgent(i);
		}
	}

	public GuestScienceApkGdsRunning getGuestScienceApk(Agent agent, String shortName) {
		if(agent == null || shortName == null) {
			throw new IllegalArgumentException("Agent and shortName cannot be null");
		}
		Vector<GuestScienceApkGdsRunning> apkList = apkMap.get(agent);
		for(GuestScienceApkGdsRunning gsagr : apkList) {
			if(gsagr.getShortName().equals(shortName)) {
				return gsagr;
			}
		}
		return null;
	}

	public String[] getApkNamesForAstrobee(Agent agent) {
		Set<String> apkNames = new TreeSet<String>();
		Vector<GuestScienceApkGdsRunning> vector = apkMap.get(agent);
		if(vector != null) {
			for(GuestScienceApkGdsRunning info : vector) {
				apkNames.add(info.getShortName());
			}
		}
		if(apkNames.size() > 0) {
			return apkNames.toArray(new String[1]);
		}
		return new String[]{""};
	}

	// get String[] of all selected ABs in top part of Workbench
	public String[] getApkNamesForSelectedAstrobees() {
		Set<String> apkNames = new TreeSet<String>();
		for(int i=0; i<NUM_BEES; i++) {
			if(workbenchSelectedAgents[i] != null) {
				Vector<GuestScienceApkGdsRunning> treeSet = apkMap.get(workbenchSelectedAgents[i]);
				if(treeSet != null) {
					for(GuestScienceApkGdsRunning info : treeSet) {
						apkNames.add(info.getShortName());
					}
				}
			}
		}
		if(apkNames.size() > 0) {
			return apkNames.toArray(new String[1]);
		}
		return new String[]{""};
	}

	public String getCommandBody(Agent agent, String apkName, String cmdName) {
		GuestScienceApkGdsRunning gsagr = getGuestScienceApk(agent, apkName); 
		GuestScienceCommandGds cmd = gsagr.getGuestScienceCommandFromName(cmdName);
		return cmd.getCommand();
	}

	public boolean isApkRunning(int number, String longName){
		Vector<GuestScienceApkGdsRunning> vector = apkMap.get(allAgents[number]);
		for(GuestScienceApkGdsRunning info : vector) {
			if(info.getApkName().equals(longName)) {
				return info.isRunning();
			}
		}
		return false;
	}

	public boolean isApkRunning(Agent agent, String shortName) {
		Vector<GuestScienceApkGdsRunning> vector = apkMap.get(agent);
		if(shortName.isEmpty() || vector == null){
			return false;
		}
		for(GuestScienceApkGdsRunning info : vector) {
			if(info.getShortName().equals(shortName) || info.getApkName().equals(shortName)) {
				return info.isRunning();
			}
		}
		throw new IllegalArgumentException("No apk named "+shortName+" on agent "+ agent); 
	}

	public List<GuestScienceApkGdsRunning> getApkInfoFromAstrobee(int number){
		List<GuestScienceApkGdsRunning> info = new ArrayList<GuestScienceApkGdsRunning>();
		if(allAgents.length > number && allAgents[number] != null) {
			Vector<GuestScienceApkGdsRunning> vector = apkMap.get(allAgents[number]);
			if(vector != null) {
				for(GuestScienceApkGdsRunning apk : vector) {
					info.add(apk);
				}
			}
		}
		return info;
	}

	public Map<String, TreeMap<String, TreeMap<String, String>>> getGuestScienceDataFromAstrobee(Agent agent) {
		for(int i = 0; i < allAgents.length; i++){
			if(allAgents[i] != null && allAgents[i].name().equals(agent.name())){
				return getGuestScienceDataFromAstrobee(i);
			}
		}
		return new TreeMap<String, TreeMap<String, TreeMap<String, String>>>();

	}

	public Map<String, TreeMap<String, TreeMap<String, String>>> getGuestScienceDataFromAstrobee(int agentNumber){
		return guestScienceDataMap.get(allAgents[agentNumber]);
	}

	public String getApkLongName(Agent agent, String shortName) {
		Vector<GuestScienceApkGdsRunning> vector = apkMap.get(agent);
		for(GuestScienceApkGdsRunning info : vector) {
			if(info.getShortName().equals(shortName)) {
				return info.getApkName();
			}
		}
		throw new IllegalArgumentException("No apk named "+shortName+" on agent "+ agent); 
	}

	public String getAPKShortName(Agent a , String longName){
		Vector<GuestScienceApkGdsRunning> vector = apkMap.get(a);
		for(GuestScienceApkGdsRunning info : vector) {
			if(info.getApkName().equals(longName)) {
				return info.getShortName();
			}
		}
		return "";
	}

	public String getAPKShortName(int number, String longName){
		Vector<GuestScienceApkGdsRunning> vector = apkMap.get(allAgents[number]);
		for(GuestScienceApkGdsRunning info : vector) {
			if(info.getApkName().equals(longName)) {
				return info.getShortName();
			}
		}
		return "";
	}

	public synchronized void ingestGuestScienceConfig(Agent agent, GuestScienceConfig config) {
		//		logger.info("########## Got config for " + agent );
		Vector<GuestScienceApkGdsRunning> vector = apkMap.get(agent);
		if(vector == null) {
			vector = new Vector<GuestScienceApkGdsRunning>();
			apkMap.put(agent, vector);
		} else {
			vector.clear();
		}

		for(int i=0; i<config.apkStates.userData.size(); i++) {
			GuestScienceApk gsa = (GuestScienceApk) config.apkStates.userData.get(i);
			GuestScienceApkGdsRunning gsagr = new GuestScienceApkGdsRunning(gsa);
			vector.add(gsagr);
		}

		doneInitializingApkMap.put(agent, true);

		if(savedStates.containsKey(agent)) {
			//			logger.info("########## Got config for " + agent + " after we got state, ingesting state now");
			ingestGuestScienceState(agent, savedStates.get(agent));
		}
	}

	public synchronized void ingestGuestScienceState(Agent agent, GuestScienceState gsas) {
		//		logger.info("########## ingesting State for " + agent);
		if(doneInitializingApkMap.get(agent) != null && !doneInitializingApkMap.get(agent)) {
			savedStates.put(agent, gsas);
			return;
		}

		Vector<GuestScienceApkGdsRunning> vector = apkMap.get(agent);
		if(vector == null) {
			logger.error("This should be unreachable");
			return;
		}

		int vectorSize = vector.size();
		int runningApks = gsas.runningApks.userData.size();

		if(vectorSize != runningApks) {
			logger.error("Mismatched number of apks for " + agent.name() 
					+ ": " + vectorSize + " != " + runningApks);
			return;
		}

		for(int i=0; i<vector.size(); i++) {
			boolean running = gsas.runningApks.userData.getBoolean(i);
			vector.get(i).setRunning(running);
		}
	}

	public void ingestGuestScienceData(Agent agent, GuestScienceData data){
		if(apkMap == null || apkMap.isEmpty()) {
			return;
		}
		try {
			for(GuestScienceApkGdsRunning info : apkMap.get(agent)){
				if(info == null) {
					continue;
				}
				if(info.getApkName().equals(data.apkName)){ // <-- why are we checking this? if we got it, we got it
					if(guestScienceDataMap.get(agent) == null){
						guestScienceDataMap.put(agent, new TreeMap<String,TreeMap<String,TreeMap<String,String>>>());
					}
					storeGuestScienceJsonDataForDisplay(data, agent);
				}

			}
		} catch (NullPointerException e) {
			logger.error("Caught an NPE");
		}
	}

	protected void storeGuestScienceJsonDataForDisplay(GuestScienceData in, Agent agent) {
		if(!in.type.equals(GuestScienceDataType.GUEST_SCIENCE_JSON)) {
			return;
		}

		TreeMap<String,TreeMap<String,TreeMap<String,String>>> agentMap = guestScienceDataMap.get(agent);

		String apkName = in.apkName;
		final String shortName = getAPKShortName(agent, apkName);
		String topic = in.topic;

		Map<String,String> topicMap = getTopicMap(shortName, topic, agentMap);

		final Iterator<Entry<String, JsonNode>> entry =  getJsonIterator(in.data);
		while(entry.hasNext()){
			final Entry<String,JsonNode> jsonNode = entry.next();
			topicMap.put(jsonNode.getKey(),jsonNode.getValue().toString());
		}
	}

	// call it getASummary()
	public String getASummary(Agent agent,String apkName){
		TreeMap<String, TreeMap<String, TreeMap<String, String>>> agentMap = guestScienceDataMap.get(agent);
		if(agentMap == null){
			return "";
		}
		TreeMap<String,TreeMap<String,String>> apkNameMap = agentMap.get(apkName);
		if(apkNameMap == null) {
			return "";
		}

		for(Entry<String,TreeMap<String,String>> topicMapEntry : apkNameMap.entrySet()) {
			for(Entry<String,String> pairEntry : topicMapEntry.getValue().entrySet()) {
				if(pairEntry.getKey().equals("Summary")) {
					return pairEntry.getValue();
				}
			}
		}
		return "";
	}

	protected Iterator<Entry<String, JsonNode>> getJsonIterator(rapid.OctetSequence2K data) {
		final byte[] bytes = new byte[data.userData.size()];
		for(int i = 0 ; i < data.userData.size(); i++){
			bytes[i] = (byte)data.userData.get(i);
		}
		String jsonString = new String(bytes);

		final ObjectMapper mapper = new ObjectMapper(new JsonFactory());
		try {
			return mapper.readTree(jsonString).getFields();
		}catch(Exception e){
			logger.error(e.getMessage());
			return null;
		}
	}

	protected Map<String,String> getTopicMap(String apkName, String topic,
			Map<String,TreeMap<String,TreeMap<String,String>>> agentMap) {
		TreeMap<String,TreeMap<String,String>> apkNameMap = agentMap.get(apkName);
		if(apkNameMap == null) {
			apkNameMap = new TreeMap<String,TreeMap<String,String>>();
			agentMap.put(apkName, apkNameMap);
		}

		TreeMap<String,String> topicMap = apkNameMap.get(topic);
		if(topicMap == null) {
			topicMap = new TreeMap<String,String>();
			apkNameMap.put(topic, topicMap);
		}
		return topicMap;
	}

	@Inject @Optional
	public void acceptGuestScienceAgent1(@Named(FreeFlyerStrings.SELECTED_GUEST_SCIENCE_1) Agent a) {
		workbenchSelectedAgents[0] = a;
	}

	@Inject @Optional
	public void acceptGuestScienceAgent2(@Named(FreeFlyerStrings.SELECTED_GUEST_SCIENCE_2) Agent a) {
		workbenchSelectedAgents[1] = a;
	}

	@Inject @Optional
	public void acceptGuestScienceAgent3(@Named(FreeFlyerStrings.SELECTED_GUEST_SCIENCE_3) Agent a) {
		workbenchSelectedAgents[2] = a;
	}

	public String getShortNameOfFirstApk(Agent agent) {
		Vector<GuestScienceApkGdsRunning> treeSet = apkMap.get(agent);
		if(treeSet != null) {
			return treeSet.get(0).getShortName();
		}
		return blankDash;
	}

	public void deleteJsonEntry(Agent agent){
		guestScienceDataMap.get(agent).clear();
	}

	public String getStatusOfApkNamed(Agent agent, String apkName) {
		if(apkName != null) {

			Vector<GuestScienceApkGdsRunning> apkInfo = apkMap.get(agent);

			if(apkInfo != null) {
				for(GuestScienceApkGdsRunning gsagr : apkInfo) {
					if(apkName.equals(gsagr.getShortName())) {
						return gsagr.getStatusString();
					}
				}
			}
		} 
		return blankDash;
	}

	public String getStatusOfFirstApk(Agent agent) {
		Vector<GuestScienceApkGdsRunning> treeSet = apkMap.get(agent);
		if(treeSet != null) {
			return treeSet.get(0).getStatusString();
		}
		return blankDash;
	}

	public void addListener(GuestScienceStateListener l) {
		listeners.add(l);
		l.onGuestScienceConfigChange(this);
		l.onGuestScienceStateChange(this);
		l.onGuestScienceDataChange(this, "", "");
	}

	public void removeListener(GuestScienceStateListener l) {
		listeners.remove(l);
	}

	public void onRapidMessageReceived(Agent agent, MessageType msgType,
			Object msgObj, Object cfgObj) {
		if(msgType.equals(MessageTypeExtAstro.GUEST_SCIENCE_STATE_TYPE)) {
			GuestScienceState apkState = (GuestScienceState)msgObj;
			ingestGuestScienceState(agent, apkState);
			notifyListenersOfStateChange();
			return;
		} else if(msgType.equals(MessageTypeExtAstro.GUEST_SCIENCE_CONFIG_TYPE)){
			GuestScienceConfig config = (GuestScienceConfig)msgObj;
			ingestGuestScienceConfig(agent,config);
			notifyListenersOfConfigChange();
		} else if(msgType.equals(MessageTypeExtAstro.GUEST_SCIENCE_DATA_TYPE)){
			GuestScienceData data = (GuestScienceData)msgObj;
			ingestGuestScienceData(agent,data);
			notifyListenersOfDataChange(data.apkName, data.topic);
		}
	}

	public void notifyListenersOfStateChange() {
		for(GuestScienceStateListener l : listeners) {
			l.onGuestScienceStateChange(this);
		}
	}

	public void notifyListenersOfConfigChange() {
		for(GuestScienceStateListener l : listeners) {
			l.onGuestScienceConfigChange(this);
		}
	}

	public void notifyListenersOfDataChange(String apkName, String topic) {
		for(GuestScienceStateListener l : listeners) {
			l.onGuestScienceDataChange(this, apkName, topic);
		}
	}

	protected List<MessageType> getMessageTypes() {
		List<MessageType> ret = new ArrayList<MessageType>();
		for(int i=0; i<sampleType.length; i++) {
			ret.add(sampleType[i]);
		}
		return ret;
	}

	@Override
	public void activeAgentSetChanged() {
		for(Agent a : ActiveAgentSet.values()) {
			for(MessageType mt : getMessageTypes()) {
				if (mt != null){
					RapidMessageCollector.instance().addRapidMessageListener(participantId, 
							a, 
							mt, 
							this);
				}
			}
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
