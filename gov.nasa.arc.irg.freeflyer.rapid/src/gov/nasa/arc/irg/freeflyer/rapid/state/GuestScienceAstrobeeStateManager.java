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
import gov.nasa.arc.irg.freeflyer.rapid.frequent.AstrobeeBattMinutesListener;
import gov.nasa.arc.irg.freeflyer.rapid.runningplan.AbstractGuestScienceRunningPlanInfo;
import gov.nasa.rapid.idl.ext.astrobee.message.MessageTypeExtAstro;
import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.e4.message.MessageType;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.contexts.IEclipseContext;

import rapid.ext.astrobee.CompressedFile;
import rapid.ext.astrobee.EpsState;
import rapid.ext.astrobee.PlanStatus;

/** 
 * This class will keep track of the health and status needed for one line in the
 * Guest Science Health and Status table, and the Guest Science state needed to
 * populate the other panes.
 */
public class GuestScienceAstrobeeStateManager extends AstrobeeStateManager {
	private static final Logger logger = Logger.getLogger(GuestScienceAstrobeeStateManager.class);
	protected Thread battMinutesNotifyThread;
	protected int SECONDS_BETWEEN_NOTIFICATIONS = 1;
	protected List<AstrobeeBattMinutesListener> battMinutesListeners;
	protected int battMinutes = Integer.MAX_VALUE;
	protected int lastSentBattMinutes = Integer.MAX_VALUE;
	private PlanStatus savedPlanStatus;
	private AbstractGuestScienceRunningPlanInfo guestScienceRunningPlanInfo;
	
	public GuestScienceAstrobeeStateManager(IEclipseContext context) {
		super(context);
		battMinutesListeners = new CopyOnWriteArrayList<AstrobeeBattMinutesListener>();
	}
	
	public void setGuestScienceRunningPlanInfo(AbstractGuestScienceRunningPlanInfo gsrpi) {
		guestScienceRunningPlanInfo = gsrpi;
	}
	
	public AbstractGuestScienceRunningPlanInfo getGuestScienceRunningPlanInfo() {
		return guestScienceRunningPlanInfo;
	}
	
	public void addBattPercentListener(AstrobeeBattMinutesListener l) {
		battMinutesListeners.add(l);
	}
	
	public void removeBattPercentListener(AstrobeeBattMinutesListener l) {
		battMinutesListeners.remove(l);
	}
	
	@Override
	protected void init() {
		sampleType = new MessageType[] {
				MessageType.ACCESSCONTROL_STATE_TYPE,
				MessageTypeExtAstro.AGENT_STATE_TYPE,
				MessageTypeExtAstro.ARM_STATE_TYPE,
				MessageTypeExtAstro.COMPRESSED_FILE_ACK_TYPE,
				MessageTypeExtAstro.CURRENT_PLAN_COMPRESSED_TYPE,
				MessageTypeExtAstro.EPS_STATE_TYPE,
				MessageTypeExtAstro.FAULT_CONFIG_TYPE,
				MessageTypeExtAstro.FAULT_STATE_TYPE,
				MessageTypeExtAstro.GUEST_SCIENCE_CONFIG_TYPE,
				MessageTypeExtAstro.GUEST_SCIENCE_STATE_TYPE,
				MessageTypeExtAstro.PLAN_STATUS_TYPE,
				MessageTypeExtAstro.ZONES_COMPRESSED_TYPE
		};
		aggregateState = new GuestScienceAstrobeeState();
		lastSentAggregateState = new GuestScienceAstrobeeState();
		adapter = new AstrobeeStateAdapter(aggregateState);
		createAndStartEpsNotifyThread();
	}
	
	@Override
	// this is called manually from GuestScienceTopPart
	public void acceptAgent(Agent agent) {
		if(agent == null || this.agent != null) {
			return;
		}
		((GuestScienceAstrobeeState)aggregateState).setAgent(agent);
		aggregateState.clearAll();
		this.agent = agent;
		commandPublisher = CommandPublisher.getInstance(agent);
		subscribe();
		GrabControlManager.get(agent);
	}

	@Override
	public void onRapidMessageReceived(Agent agent, MessageType msgType,
			Object msgObj, Object cfgObj) {
		if(msgType.equals(MessageTypeExtAstro.CURRENT_PLAN_COMPRESSED_TYPE)) {
			CompressedFile compFile = (CompressedFile) msgObj;
			if(aggregateState != null) {
				aggregateState.ingestCurrentPlanCompressedFile(compFile);

				// need to notify listeners to plan
				notifyListeners(MessageTypeExtAstro.CURRENT_PLAN_COMPRESSED_TYPE);
				if(guestScienceRunningPlanInfo != null) {
					guestScienceRunningPlanInfo.acceptGuestSciencePlan(((GuestScienceAstrobeeState)aggregateState).getPlan());
				}
			}
			return;
		}
		if(msgType.equals(MessageTypeExtAstro.EPS_STATE_TYPE)) {
			EpsState epsState = (EpsState) msgObj;
			ingestEpsState(epsState);
		}
		if(msgType.equals(MessageTypeExtAstro.PLAN_STATUS_TYPE)) {

			if(msgObj.equals(savedPlanStatus)) {
				System.out.println("GOT A DUPLICATE");
				return;
			}
			savedPlanStatus = (PlanStatus)msgObj;

			if(guestScienceRunningPlanInfo != null) {
				if(savedPlanStatus.planName.equals("")) {
					guestScienceRunningPlanInfo.clear();
				} else {
					guestScienceRunningPlanInfo.setPlanStatus(savedPlanStatus);
				}
			}
		}
		if(msgType.equals(MessageTypeExtAstro.ZONES_COMPRESSED_TYPE)) {
			System.out.println("Got the Zones in GuestScienceAstrobeeStateManager");
		}
		// other message responses same as parent class
		super.onRapidMessageReceived(agent, msgType, msgObj, cfgObj);
	}
	
	protected void ingestCurrentZonesCompressedFile(CompressedFile compressedFile) {
		
	}

	protected void ingestEpsState(EpsState state) {
		battMinutes = state.estimatedMinutesRemaining;
	}

	protected void createAndStartEpsNotifyThread() {
		battMinutesNotifyThread = new Thread() {
			@Override
			public synchronized void run() {
				while(true) {
					try {
						notifyBattMinutesListeners();
						Thread.sleep(SECONDS_BETWEEN_NOTIFICATIONS * 1000);
					} catch (InterruptedException e) { 
						return;
					}
				}
			}
		};
		battMinutesNotifyThread.start();
	}

	protected void notifyBattMinutesListeners() {
		if(lastSentBattMinutes == battMinutes) {
			return;
		}

		for(AstrobeeBattMinutesListener abpl : battMinutesListeners) {
			abpl.onBattMinutesChange(battMinutes);
		}
		lastSentBattMinutes = battMinutes;
	}
	
	public void preDestroy() {
		battMinutesNotifyThread.interrupt();
	}

}
