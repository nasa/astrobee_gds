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
package gov.nasa.arc.verve.freeflyer.workbench.utils;

import gov.nasa.arc.irg.freeflyer.rapid.CompressedFilePublisher;
import gov.nasa.arc.irg.freeflyer.rapid.FreeFlyerStrings;
import gov.nasa.arc.irg.freeflyer.rapid.LogEntry;
import gov.nasa.arc.irg.freeflyer.rapid.LogPoster;
import gov.nasa.arc.irg.plan.modulebay.ModuleBayPlan;
import gov.nasa.dds.system.DdsTask;
import gov.nasa.rapid.idl.ext.astrobee.message.MessageTypeExtAstro;
import gov.nasa.rapid.v2.e4.Rapid;
import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.e4.message.IRapidMessageListener;
import gov.nasa.rapid.v2.e4.message.MessageType;
import gov.nasa.rapid.v2.e4.message.collector.RapidMessageCollector;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import rapid.Ack;
import rapid.AckCompletedStatus;
import rapid.AckStatus;
import rapid.Command;
import rapid.ext.astrobee.CompressedFile;
import rapid.ext.astrobee.CompressedFileAck;

public class AckListener implements IRapidMessageListener {
	private static final Logger logger = Logger.getLogger(AckListener.class);
	private static AckListener INSTANCE;
	private String m_participantId = Rapid.PrimaryParticipant;
	private final MessageType[] m_msgTypes   = new MessageType[] { 
			MessageType.ACK_TYPE,
			MessageTypeExtAstro.CURRENT_PLAN_COMPRESSED_TYPE,
			MessageTypeExtAstro.COMPRESSED_FILE_ACK_TYPE,
			MessageTypeExtAstro.COMMAND_ECHO_TYPE
	};
	private static Map<Agent,Map<String,Command>> commandIdToName = new HashMap<Agent,Map<String,Command>>();
	private volatile Map<Agent,Set<Ack>> seenAcks = new HashMap<Agent,Set<Ack>>();
	private volatile Set<Command> seenEchoes = new HashSet<>();

	public MApplication application;
	@Inject @Named(IServiceConstants.ACTIVE_SHELL)
	public Shell shell;

	private Map<String, Ack> unknownCommandAcks = new HashMap<>();

	@Inject
	public AckListener(MApplication app) {
		for(Agent agent : AgentsFromCommandLine.INSTANCE.getAgentsList()) {
			commandIdToName.put(agent, new HashMap<String,Command>());
			seenAcks.put(agent, new HashSet<Ack>());
		}
		
		application = app;
		INSTANCE = this;
	}

	public static AckListener getStaticInstance() {
		if(INSTANCE == null) {
			System.err.println("No AckListener created");
		}
		return INSTANCE;
	}

	@Inject @Optional
	public void setAgent(@Named(FreeFlyerStrings.PRIMARY_BEE) Agent agent) {
		if(agent == null) {
			return;
		}
		subscribe(agent);
	}

	@Inject @Optional
	public void setGuestScienceAgent1(@Named(FreeFlyerStrings.SELECTED_GUEST_SCIENCE_1) Agent agent) {
		if(agent == null) {
			return;
		}
		subscribe(agent);
	}

	@Inject @Optional
	public void setGuestScienceAgent2(@Named(FreeFlyerStrings.SELECTED_GUEST_SCIENCE_2) Agent agent) {
		if(agent == null) {
			return;
		}
		subscribe(agent);
	}

	@Inject @Optional
	public void setGuestScienceAgent3(@Named(FreeFlyerStrings.SELECTED_GUEST_SCIENCE_3) Agent agent) {
		if(agent == null) {
			return;
		}
		subscribe(agent);
	}

	@Inject @Optional
	public void setOverviewAgent(@Named(FreeFlyerStrings.SELECTED_OVERVIEW_BEE) Agent agent) {
		if(agent == null) {
			return;
		}
		subscribe(agent);
	}

	private void showFailureDialogIfNecessary(Agent agent, Ack ack) {
		if(ack.status.equals(AckStatus.ACK_COMPLETED)
				&& (ack.completedStatus.equals(AckCompletedStatus.ACK_COMPLETED_EXEC_FAILED)
						|| ack.completedStatus.equals(AckCompletedStatus.ACK_COMPLETED_BAD_SYNTAX))) {
			showFailureDialog(agent, ack);
		}
	}

	public synchronized void onRapidMessageReceived(Agent agent, MessageType msgType,
			Object msgObj, Object cfgObj) {

		if(msgType.equals(MessageTypeExtAstro.COMMAND_ECHO_TYPE)) {
			Command cmd = (Command) msgObj;
			if(seenEchoes.contains(cmd)) {
				return;
			} else {
				seenEchoes.add(cmd);
			}
			commandIdToName.get(agent).put(cmd.cmdId, cmd);

			if(unknownCommandAcks.containsKey(cmd.cmdId)) {
				LogPoster.updateLog(cmd.cmdId, cmd);
				//Ack ack = unknownCommandAcks.get(cmd.cmdId);
				// why would I show failure dialog just for an unknown command?
				//showFailureDialogIfNecessary(agent, ack);
				unknownCommandAcks.remove(cmd.cmdId);
			}
		}
		else if(msgType.equals(MessageType.ACK_TYPE)) {
			Ack ack = (Ack) msgObj;
			if(seenAcks.get(agent).contains(ack)) {
				return;
			} else {
				seenAcks.get(agent).add(ack);
			}
			showFailureDialogIfNecessary(agent, ack);

			LogPoster.postAckToLog(getCommandName(agent,ack.cmdId), ack, agent.name());

			if(commandIdToName.get(ack.cmdId) == null) {
				unknownCommandAcks.put(ack.cmdId, ack);
			}
		}
		else if(msgType.equals(MessageTypeExtAstro.CURRENT_PLAN_COMPRESSED_TYPE)) {
			CompressedFile compFile = (CompressedFile) msgObj;
			ModuleBayPlan currentPlan = CompressedFilePublisher.uncompressCurrentPlanCompressedFile(compFile);
			//logger.info(this.toString() + " received " + compFile.toString());
			LogPoster.postToLog(LogEntry.FILE, "Done Loading Plan " + currentPlan.getName(), agent.name());
		}
		else if(msgType.equals(MessageTypeExtAstro.COMPRESSED_FILE_ACK_TYPE)) {
			CompressedFileAck cfAck = (CompressedFileAck) msgObj;			
			LogPoster.postToLog(LogEntry.ACK, "Received compressed file " + cfAck.id, agent.name());
		}
	}

	private void showFailureDialog(final Agent agent, final Ack ack)  {
		// create a dialog with ok and cancel buttons and a question icon
		if(shell == null) {
			logger.error("No shell injected");
			return;
		}

		Display.getDefault().asyncExec(new Runnable() {
			public void run() {

				String commandName = getCommandName(agent, ack.cmdId);

				// actually, we do want to see these
//				if(commandName.equals(LogPoster.UNKNOWN_COMMAND_STRING)) {
//					return;
//				}

				String reason = ack.message;
				MessageBox dialog = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK);
				dialog.setText("Command Failed");
				dialog.setMessage(commandName + " " + ack.cmdId + " failed because: " + reason);

				// open dialog and await user selection
				int returnCode = dialog.open(); 
			}
		});
	}

	// make note of command so we can identify what Acks go with it
	// necessary b/c not all commands are echoed
	//	public void onCommandSent(Command cmd) {
	//		commandIdToName.put(cmd.cmdId, cmd);
	//	}

	private String getCommandName(Agent agent, String commandId) {
		Command cmd = commandIdToName.get(agent).get(commandId);
		if(cmd == null) {
			return LogPoster.UNKNOWN_COMMAND_STRING;
		}
		return LogPoster.getLogString(cmd);
	}

	public void subscribe(Agent agent) {
		if (getParticipantId() == null || getParticipantId().isEmpty() || agent == null){
			return;
		}

		final String id = getParticipantId();

		if (!commandIdToName.containsKey(agent)) {
			commandIdToName.put(agent, new HashMap<String,Command>());
		}

		if (!seenAcks.containsKey(agent)) {
			seenAcks.put(agent, new HashSet<Ack>());
		}

		DdsTask.dispatchExec(new Runnable() {
			@Override
			public void run() {
				for(MessageType mt : getMessageTypes()) {
					if (mt == null) continue;
					RapidMessageCollector.instance().addRapidMessageListener(id, 
							agent, 
							mt, 
							AckListener.this);
				}
			}
		});
	}

	public void unsubscribe(Agent agent) {
		if(agent != null) {
			RapidMessageCollector.instance().removeRapidMessageListener(getParticipantId(), agent, this);
		}
	}

	public String getParticipantId() {
		return m_participantId;
	}

	public MessageType[] getMessageTypes() {
		return m_msgTypes;
	}
}
