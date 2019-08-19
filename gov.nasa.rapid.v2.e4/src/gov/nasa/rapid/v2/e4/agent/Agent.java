/*******************************************************************************
 * Copyright (c) 2013 United States Government as represented by the 
 * Administrator of the National Aeronautics and Space Administration. 
 * All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package gov.nasa.rapid.v2.e4.agent;

import gov.nasa.rapid.v2.e4.Rapid;
import gov.nasa.util.ProcessInfo;
import gov.nasa.util.StrUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

/**
 * Convenience collection for AgentID constant values. Implements an interface very similar to Enum. This used to be an enum but was
 * changed to a class for runtime extensibility. Code using Agent should not require changes (with the obvious exception of "switch"
 * statements).
 */
public class Agent implements Comparable<Agent> {
    private static final Logger logger          = Logger.getLogger(Agent.class);
    private static int          s_instanceCount = 0;
    private static HashMap<String, Agent> s_nameMap = new HashMap<String, Agent>();

    private static Agent s_egoAgent = null;

    public static class Tag {
        public static final String HUMAN            = "human";
        public static final String ROVER            = "rover";
        public static final String FREE_FLYER       = "freeFlyer";
        public static final String HUMANOID         = "humanoid";
        public static final String TORSO            = "torso";
        public static final String ARM              = "arm";
        public static final String SIMULATOR        = "simulator";

        public static final String ASTRONAUT        = "astronaut";
        public static final String OPERATOR         = "operator";
        public static final String CAMERA           = "camera";
        public static final String ATHLETE          = "athlete";
        public static final String C2               = "centaur2";
        public static final String K10              = "k10";
        public static final String KN               = "kn";
        public static final String KREX             = "krex";
        public static final String KREX2            = "krex2";
        public static final String R2               = "robonaut2";
        public static final String SEV              = "sev";
        public static final String SPHERES          = "spheres";

        public static final String ASTROBEE			= "AstroBee";
        public static final String SMARTDOCK		= "SmartDock";
      
        public static final String NED              = "ned";
        public static final String NWU              = "nwu";
        public static final String NWU_TRUE_NORTH   = "nwu_true_north";
        public static final String ZUP              = "zUp"; // << TODO: deprecate this
        public static final String ENU              = "enu";
        public static final String ENU_TRUE_NORTH   = "enu_true_north";
    }

    public static final Agent GenericSim     = new Agent("GenericSim", Tag.SIMULATOR);
    public static final Agent Astronaut1     = new Agent("Astronaut1", Tag.HUMAN, Tag.ASTRONAUT);
    public static final Agent Astronaut2     = new Agent("Astronaut2", Tag.HUMAN, Tag.ASTRONAUT);
    public static final Agent EV1            = new Agent("EV1", Tag.HUMAN, Tag.ASTRONAUT);
    public static final Agent EV2            = new Agent("EV2", Tag.HUMAN, Tag.ASTRONAUT);
    public static final Agent EV3            = new Agent("EV3", Tag.HUMAN, Tag.ASTRONAUT);
    public static final Agent EV4            = new Agent("EV4", Tag.HUMAN, Tag.ASTRONAUT);

    public static final Agent K10Red         = new Agent("K10Red",   Tag.K10, Tag.KN, Tag.ROVER, Tag.NED);
    public static final Agent K10Black       = new Agent("K10Black", Tag.K10, Tag.KN, Tag.ROVER, Tag.NED);
    public static final Agent KRex           = new Agent("KRex",     Tag.KREX, Tag.KN, Tag.ROVER, Tag.NED);
    public static final Agent KRex2          = new Agent("KRex2",    Tag.KREX2, Tag.KN, Tag.ROVER, Tag.NED);

    public static final Agent AthleteA       = new Agent("AthleteA",    Tag.ATHLETE, Tag.ROVER, Tag.NED);
    public static final Agent AthleteB       = new Agent("AthleteB",    Tag.ATHLETE, Tag.ROVER, Tag.NED);
    public static final Agent AthleteSim     = new Agent("AthleteSim",  Tag.ATHLETE, Tag.ROVER, Tag.NED, Tag.SIMULATOR);
    public static final Agent TriathleteA    = new Agent("TriathleteA", Tag.ATHLETE, Tag.ROVER, Tag.NED);
    public static final Agent TriathleteB    = new Agent("TriathleteB", Tag.ATHLETE, Tag.ROVER, Tag.NED);

    public static final Agent Centaur2       = new Agent("Centaur2",      Tag.C2, Tag.ROVER, Tag.ZUP);
    public static final Agent Centaur2Sim    = new Agent("Centaur2Sim",   Tag.C2, Tag.ROVER, Tag.ZUP, Tag.SIMULATOR);
    public static final Agent LerA           = new Agent("LerA",          Tag.SEV, Tag.ROVER, Tag.ZUP);
    public static final Agent LerB           = new Agent("LerB",          Tag.SEV, Tag.ROVER, Tag.ZUP);
    public static final Agent LerASim        = new Agent("LerASim",       Tag.SEV, Tag.ROVER, Tag.ZUP, Tag.SIMULATOR);
    public static final Agent LerBSim        = new Agent("LerBSim",       Tag.SEV, Tag.ROVER, Tag.ZUP, Tag.SIMULATOR);
    public static final Agent Robonaut2A     = new Agent("Robonaut2A",    Tag.R2, Tag.HUMANOID, Tag.TORSO, Tag.ZUP);
    public static final Agent Robonaut2B     = new Agent("Robonaut2B",    Tag.R2, Tag.HUMANOID, Tag.TORSO, Tag.ZUP);
    public static final Agent Robonaut2ASim  = new Agent("Robonaut2ASim", Tag.R2, Tag.HUMANOID, Tag.TORSO, Tag.ZUP, Tag.SIMULATOR);

    public static final Agent Spheres0       = new Agent("Spheres0",       Tag.SPHERES, Tag.FREE_FLYER, Tag.NED);
    public static final Agent Spheres1       = new Agent("Spheres1",       Tag.SPHERES, Tag.FREE_FLYER, Tag.NED);
    public static final Agent Spheres2       = new Agent("Spheres2",       Tag.SPHERES, Tag.FREE_FLYER, Tag.NED);
    public static final Agent SpheresBlack   = new Agent("SpheresBlack",   Tag.SPHERES, Tag.FREE_FLYER, Tag.NED);
    public static final Agent SpheresBlue    = new Agent("SpheresBlue",    Tag.SPHERES, Tag.FREE_FLYER, Tag.NED);
    public static final Agent SpheresBlue2   = new Agent("SpheresBlue2",   Tag.SPHERES, Tag.FREE_FLYER, Tag.NED);
    public static final Agent SpheresOrange  = new Agent("SpheresOrange",  Tag.SPHERES, Tag.FREE_FLYER, Tag.NED);
    public static final Agent SpheresOrange2 = new Agent("SpheresOrange2", Tag.SPHERES, Tag.FREE_FLYER, Tag.NED);
    public static final Agent SpheresRed     = new Agent("SpheresRed",     Tag.SPHERES, Tag.FREE_FLYER, Tag.NED);

    public static final Agent FreeFlyerSim	 = new Agent("FreeFlyerSim", Tag.ASTROBEE, Tag.FREE_FLYER);
    public static final Agent FreeFlyerA	 = new Agent("FreeFlyerA",   Tag.ASTROBEE, Tag.FREE_FLYER);
    public static final Agent FreeFlyerB	 = new Agent("FreeFlyerB",   Tag.ASTROBEE, Tag.FREE_FLYER);
    public static final Agent FreeFlyerC	 = new Agent("FreeFlyerC",   Tag.ASTROBEE, Tag.FREE_FLYER);
    public static final Agent Bumble		 = new Agent("Bumble",		 Tag.ASTROBEE, Tag.FREE_FLYER);
    public static final Agent Honey			 = new Agent("Honey",		 Tag.ASTROBEE, Tag.FREE_FLYER);
    public static final Agent Queen			 = new Agent("Queen",		 Tag.ASTROBEE, Tag.FREE_FLYER);
    public static final Agent BSharp		 = new Agent("Bsharp",		 Tag.ASTROBEE, Tag.FREE_FLYER);
    public static final Agent BSharp2		 = new Agent("BSharp",		 Tag.ASTROBEE, Tag.FREE_FLYER);
    public static final Agent Melissa		 = new Agent("Melissa",		 Tag.ASTROBEE, Tag.FREE_FLYER);
    public static final Agent Killer	     = new Agent("Killer",		 Tag.ASTROBEE, Tag.FREE_FLYER);
    public static final Agent Killer2	     = new Agent("killer",		 Tag.ASTROBEE, Tag.FREE_FLYER);
    public static final Agent WannaBee		 = new Agent("WannaBee",	 Tag.ASTROBEE, Tag.FREE_FLYER);
    public static final Agent P4D			 = new Agent("P4D",		 	 Tag.ASTROBEE, Tag.FREE_FLYER);
    public static final Agent P4C			 = new Agent("P4C",		 	 Tag.ASTROBEE, Tag.FREE_FLYER);
    
    public static final Agent SmartDock		 = new Agent("SmartDock", 	 Tag.ASTROBEE, Tag.SMARTDOCK);
    
    public static final Agent LangleyCrane   = new Agent("LangleyCrane",   Tag.ARM);
    public static final Agent LangleyCamera  = new Agent("LangleyCamera",  Tag.CAMERA);

    public static final Agent RapidCameraA   = new Agent("RapidCameraA",   Tag.CAMERA);
    public static final Agent RapidCameraB   = new Agent("RapidCameraB",   Tag.CAMERA);
    public static final Agent RapidCameraC   = new Agent("RapidCameraC",   Tag.CAMERA);
    public static final Agent RapidCameraD   = new Agent("RapidCameraD",   Tag.CAMERA);
    public static final Agent SONYSNCRZ30N   = new Agent("SONYSNCRZ30N",   Tag.CAMERA);

    public static final Agent RpEtu          = new Agent("RpEtu", Tag.NWU_TRUE_NORTH);
    //public static final Agent RpHusky        = new Agent("RpHusky", Tag.NWU_TRUE_NORTH);
    public static final Agent RpHusky        = new Agent("RpHusky",   Tag.ENU);
    public static final Agent RpKRex2        = new Agent("RpKRex2",   Tag.NED);
    public static final Agent Rp1aKRex2      = new Agent("Rp1aKRex2", Tag.NED);

    public static final Agent Leaf0          = new Agent("Leaf0", Tag.ENU_TRUE_NORTH);
    public static final Agent Leaf1          = new Agent("Leaf1", Tag.ENU_TRUE_NORTH);
    public static final Agent Leaf2          = new Agent("Leaf2", Tag.ENU_TRUE_NORTH);
    public static final Agent Leaf3          = new Agent("Leaf3", Tag.ENU_TRUE_NORTH);
    public static final Agent Leaf4          = new Agent("Leaf4", Tag.ENU_TRUE_NORTH);
    public static final Agent Leaf5          = new Agent("Leaf5", Tag.ENU_TRUE_NORTH);

    // -- agent members
    private String                   m_name;
    private final int                m_ordinal;
    private final ArrayList<String>  m_tags    = new ArrayList<String>();

    public static final String REPOSITORY_NAME_PREFIX = "rapid/";

    /**
     * ctor
     * @throws IllegalStateException if agentName already exists
     */
    protected Agent(String agentName, String... tags) {
        synchronized(s_nameMap) {
            if (s_nameMap.containsKey(agentName)) {
                throw new IllegalStateException("Agent with name \"" + agentName + "\" already exists.");
            }
            // add agent to name map
            s_nameMap.put(agentName, this);
            m_name    = agentName;
            m_ordinal = s_instanceCount++;
            for(String tag : tags) {
                if(tag.length() > 0)
                    m_tags.add(tag);
            }
            Collections.sort(m_tags);
        }
    }

    public static synchronized void setEgoAgent(Agent agent) {
        s_egoAgent = agent;
    }

    /**
     * get the "self" agent. If no agent has been sent using setEgoAgent(), 
     * the ego agent will be set to newOperatorAgent()
     * @return
     */
    public static synchronized Agent getEgoAgent() {
        if(s_egoAgent == null) {
            s_egoAgent = newOperatorAgent();
        }
        return s_egoAgent;
    }

    /**
     * If an agent is requested with no tags, first check DiscoveredAgentRepository 
     * if a IDynamicAgentRecognizer has been registered for this agentName and 
     * creates if applicable. If not, creates new Agent with no tags.
     * @throws IllegalStateException is agentName already exists
     */
    public static synchronized Agent newAgent(String agentName) throws IllegalStateException {
        Agent agent = DiscoveredAgentRepository.INSTANCE.checkForDynamicAgent(agentName);
        if(agent == null) { // create new agent if dynamic agent creation failed
            return newAgent(agentName, "");
        }
        return agent;
    }

    /**
     * @throws IllegalStateException is agentName already exists
     */
    public static synchronized Agent newAgent(String agentName, String... tags) throws IllegalStateException {
        Agent agent = new Agent(agentName, tags);
        return agent;
    }

    /**
     * Example of creating a new Agent (for example, for client applications).
     * 
     * @return a new Agent
     */
    public static Agent newOperatorAgent() {
        Agent retVal = null;
        while (retVal == null) {
            String username = ProcessInfo.username();
            String hostname = ProcessInfo.hostname();
            //long   appId    = appId();
            // use only username@hostname so access control is more reasonable
            String agentName = username + "@" + hostname; // + ":" + appId;
            try {
                retVal = newAgent(agentName, Tag.HUMAN, Tag.OPERATOR);
                logger.debug("New Operator Agent: " + agentName);
            } 
            catch (Exception e) {
                logger.fatal("Agent creation failed", e);
            }
        }
        return retVal;
    }

    /**
     * TODO: The name is requested before participant creation, so appId doesn't exist yet.
     * 
     * @return
     */
    protected static long appId() {
        return ProcessInfo.processId(Rapid.pidFallback);
    }

    /**
     * Add a new agent to the set at runtime.
     * 
     * @param agentName
     * @param agentType
     * @returns true upon success, false if name already exists
     */
    public static boolean add(String agentName, String... tags) {
        try {
            newAgent(agentName, tags);
        } catch (Throwable t) {
            logger.warn("Failed to add new Agent: "+t.getMessage());
            return false;
        }
        return true;
    }

    /**
     * @returns name of agent
     */
    public String name() {
        return m_name;
    }

    /**
     * @returns name of Agent as it would appear in RobotRepository
     */
    public String repositoryName() {
        return REPOSITORY_NAME_PREFIX+m_name;
    }

    /**
     * @returns name of agent
     */
    @Override
    public String toString() {
        return m_name;
    }

    /**
     * @returns List of this Agent's tags. Do not modify this list. 
     */
    public List<String> getTags() {
        return m_tags;
    }

    /**
     * @return true if agent has the specified tag
     */
    public boolean hasTag(String tag) {
        return !(Collections.binarySearch(m_tags, tag) < 0);
    }

    /**
     * get all agents that have been tagged with the specified tag
     */
    public static List<Agent> getAgentsWithTag(String tag) {
        ArrayList<Agent> retVal = new ArrayList<Agent>(s_instanceCount);
        for(Agent agent : s_nameMap.values()) {
            if(agent.hasTag(tag)) {
                retVal.add(agent);
            }
        }
        Collections.sort(retVal);
        return retVal;
    }

    /**
     * @returns Agent enum from name string
     * @throws IllegalArgumentException if name does not map to valid Agent
     */
    public static Agent valueOf(String name) {
        if(name.startsWith(REPOSITORY_NAME_PREFIX)) {
            name = name.replace(REPOSITORY_NAME_PREFIX, "");
        }
        Agent retVal = s_nameMap.get(name);
        if(retVal == null) {
            throw new IllegalArgumentException("No Agent with name \""+name+"\"");
        }
        return retVal;
    }

    /**
     * @return true if name matches an Agent name
     */
    public static boolean exists(String name) {
        if(name.startsWith(REPOSITORY_NAME_PREFIX)) {
            name = name.replace(REPOSITORY_NAME_PREFIX, "");
        }
        return s_nameMap.containsKey(name);
    }

    public int ordinal() {
        return m_ordinal;
    }

    @Override
    public int compareTo(Agent other) {
        return this.ordinal() - other.ordinal();
    }

    /**
     * @return a list of all Agents
     */
    public static List<Agent> values() {
        ArrayList<Agent> retVal = new ArrayList<Agent>(s_nameMap.values());
        Collections.sort(retVal);
        return retVal;
    }

    /** 
     * convert an array of Agents to a single delimited string 
     */
    public static String toString(Agent[] agents, String delim) {
        return StrUtil.arrayToString(agents, delim);
    }

    /** 
     * convert a Collection of Agents to a single delimited string 
     */
    public static String toString(Collection<Agent> agents, String delim) {
        return StrUtil.arrayToString(agents.toArray(new Agent[agents.size()]), delim);
    }

    /** 
     * convert a string containing a delimted list of Agent names to an array of Agents 
     */
    public static Agent[] toArray(String agentsString, String delim) {
        List<Agent> list = toList(agentsString, delim);
        return list.toArray(new Agent[list.size()]);
    }

    /** 
     * convert a string containing a delimted list of Agent names to a List of Agents 
     */
    public static List<Agent> toList(String agentsString, String delim) {
        LinkedList<Agent> list = new LinkedList<Agent>();
        StringTokenizer tokenizer = new StringTokenizer(agentsString, delim);
        while(tokenizer.hasMoreTokens()) {
            String agentName = tokenizer.nextToken();
            try {
                list.add(Agent.valueOf(agentName));
            }
            catch(IllegalArgumentException e) {
                logger.warn("Error converting \""+agentName+"\" to Agent", e);
            }
        }
        return list;
    }

    public static final AlphabeticalAgentComparator alphabeticalComparator = new AlphabeticalAgentComparator();
    public static class AlphabeticalAgentComparator implements Comparator<Agent> {
        @Override
        public int compare(Agent arg0, Agent arg1) {
            return arg0.name().compareTo(arg1.name());
        }
    }
}
