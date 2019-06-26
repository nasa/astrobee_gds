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

import gov.nasa.arc.irg.plan.ui.io.WorkbenchConstants;
import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.e4.agent.Agent.Tag;
import gov.nasa.rapid.v2.e4.agent.DiscoveredAgentRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class AgentsFromCommandLine {
	private final String[] cmdLineAgentParamNames = {
			WorkbenchConstants.AGENT_1_PARAM_STRING,
			WorkbenchConstants.AGENT_2_PARAM_STRING,
			WorkbenchConstants.AGENT_3_PARAM_STRING
	};

	protected String[] colorsList = {WorkbenchConstants.HONEY, WorkbenchConstants.QUEEN, 
			WorkbenchConstants.BUMBLE, WorkbenchConstants.YELLOW, WorkbenchConstants.GREEN, 
			WorkbenchConstants.BLUE,
			WorkbenchConstants.ORANGE, WorkbenchConstants.PURPLE, WorkbenchConstants.PINK}; //<-- we don't have these colors of models.

	protected Map<Agent,String> agentsToColors = new HashMap<Agent,String>();
	protected Vector<Agent> agentsVector; // should always be length 3 due to defaults
	protected int numAgentsSpecifiedOnCommandLine = 0;
	
	protected String fakeAgent = "EV1";

	public static AgentsFromCommandLine INSTANCE = new AgentsFromCommandLine();

	public AgentsFromCommandLine() {
		makeAgentsList();
	}

	public Agent getAgent(int i) {
		return agentsVector.get(i);
	}

	public String getColor(int i) {
		return agentsToColors.get(agentsVector.get(i));
	}

	public int getNumAgents() {
		return agentsVector.size();
	}

	public Vector<Agent> getAgentsList() {
		return agentsVector;
	}
	
	private void matchAgentToColor(Agent agent) {
		if(Agent.BSharp.equals(agent)) {
			agentsToColors.put(agent, WorkbenchConstants.PURPLE);
		} else if(Agent.Bumble.equals(agent)) {
			agentsToColors.put(agent, WorkbenchConstants.BUMBLE);
		} else if(Agent.Honey.equals(agent)) {
			agentsToColors.put(agent, WorkbenchConstants.HONEY);
		} else if(Agent.Killer.equals(agent)) {
			agentsToColors.put(agent, WorkbenchConstants.ORANGE);
		} else if(Agent.Melissa.equals(agent)) {
			agentsToColors.put(agent, WorkbenchConstants.PINK);
		} else {
			agentsToColors.put(agent, WorkbenchConstants.WHITE);
		}
	}

	/** read in desired agents from command line, default to Honey, Queen, Bumble if none specified */
	private void makeAgentsList() {
		agentsVector = new Vector<Agent>();
		agentsVector.add(Agent.valueOf(fakeAgent)); 
		agentsVector.add(Agent.valueOf(fakeAgent));
		agentsVector.add(Agent.valueOf(fakeAgent));

		for(int i=0; i<cmdLineAgentParamNames.length; i++) {
			Agent agent = getParameterFromCommandLineArgs(cmdLineAgentParamNames[i]);
			
			if(agent != null) {
				agentsVector.set(i,agent);
				matchAgentToColor(agent);
				numAgentsSpecifiedOnCommandLine++;
			}
		}
		
		// fill in defaults of Honey, Queen, and Bumble to make list of three
		if(numAgentsSpecifiedOnCommandLine < 1) {
			addBumble();
			addHoney();
			addQueen();
		}
		else if(numAgentsSpecifiedOnCommandLine < 2) {
			if(agentsVector.contains(Agent.Honey)) {
				addBumble();
				addQueen();
			} else if(agentsVector.contains(Agent.Bumble)) {
				addHoney();
				addQueen();
			} else {
				addBumble();
				addHoney();
			}
		}
		else if(numAgentsSpecifiedOnCommandLine < 3) {
			if(!agentsVector.contains(Agent.Queen)) {
				addQueen(); 
			} else if(!agentsVector.contains(Agent.Bumble)) {
				addBumble();
			}
			else if(!agentsVector.contains(Agent.Honey)) {
				addHoney();
			}
		}
	}

	private void addHoney() {
		int index = agentsVector.indexOf(Agent.valueOf(fakeAgent));
		agentsVector.set(index, Agent.Honey);
		agentsToColors.put(Agent.Honey, WorkbenchConstants.HONEY);
	}

	private void addQueen() {
		int index = agentsVector.indexOf(Agent.valueOf(fakeAgent));
		agentsVector.set(index, Agent.Queen);
		agentsToColors.put(Agent.Queen, WorkbenchConstants.QUEEN);
	}
	
	private void addBumble() {
		int index = agentsVector.indexOf(Agent.valueOf(fakeAgent));
		agentsVector.set(index, Agent.Bumble);
		agentsToColors.put(Agent.Bumble, WorkbenchConstants.BUMBLE);
	}

	/** if input is in command line args, return the next token */
	private Agent getParameterFromCommandLineArgs(String paramName) {
		String agentName = WorkbenchConstants.getStringValueOfParameter(paramName);
		if(agentName == null) {
			return null;
		}
		try {
			return Agent.valueOf(agentName);
		} catch(IllegalArgumentException e) {
			final Agent agent = Agent.newAgent(agentName, Tag.ASTROBEE, Tag.FREE_FLYER);
			DiscoveredAgentRepository.INSTANCE.rediscoverAgents();
			return agent;
		}
	}
}
