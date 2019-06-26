/*******************************************************************************
 * Copyright (c) 2011 United States Government as represented by the 
 * Administrator of the National Aeronautics and Space Administration.
 * All rights reserved.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package gov.nasa.arc.irg.freeflyer.rapid;

import gov.nasa.rapid.v2.e4.message.MessageType;

import java.util.HashMap;
import java.util.Set;

import rapid.AgentConfig;
import rapid.AgentState;
import rapid.PositionConfig;
import rapid.PositionSample;

public class FreeFlyerMessageType extends MessageType {
	private static final HashMap<String,FreeFlyerMessageType> s_messageMap = new HashMap<String,FreeFlyerMessageType>();

  	 // FreeFlyer topics
    public static final String EFK_POSE_POSITION_CONFIG   = "EkfPosePositionConfig";
    public static final String EFK_POSE_POSITION_SAMPLE   = "EkfPosePositionSample";
    public static MessageType  EFK_POSE_POSITION_CONFIG_TYPE = new FreeFlyerMessageType(Category.Config, EFK_POSE_POSITION_CONFIG,                     null, PositionConfig.class, FreeFlyerConstants.POSITION_CONFIG_EKF_POSE_TOPIC, "RapidPositionConfigProfile");
    public static MessageType  EFK_POSE_POSITION_SAMPLE_TYPE = new FreeFlyerMessageType(Category.Sample, EFK_POSE_POSITION_SAMPLE, EFK_POSE_POSITION_CONFIG, PositionSample.class, FreeFlyerConstants.POSITION_SAMPLE_EKF_POSE_TOPIC, "RapidPositionSampleProfile");

    public static final String ALVAR_OVERHEAD_POSITION_CONFIG   = "AlvarOverheadPositionConfig";
    public static final String ALVAR_OVERHEAD_POSITION_SAMPLE   = "AlvarOverheadPositionSample";
    public static MessageType  ALVAR_OVERHEAD_POSITION_CONFIG_TYPE = new FreeFlyerMessageType(Category.Config, ALVAR_OVERHEAD_POSITION_CONFIG,                           null, PositionConfig.class, FreeFlyerConstants.POSITION_CONFIG_ALVAR_OVERHEAD_TOPIC, "RapidPositionConfigProfile");
    public static MessageType  ALVAR_OVERHEAD_POSITION_SAMPLE_TYPE = new FreeFlyerMessageType(Category.Sample, ALVAR_OVERHEAD_POSITION_SAMPLE, ALVAR_OVERHEAD_POSITION_CONFIG, PositionSample.class, FreeFlyerConstants.POSITION_SAMPLE_ALVAR_OVERHEAD_TOPIC, "RapidPositionSampleProfile");

    public static final String VIZ_POSITION_CONFIG   = "VizPositionConfig";
    public static final String VIZ_POSITION_SAMPLE   = "VizPositionSample";
    public static MessageType  VIZ_POSITION_CONFIG_TYPE = new FreeFlyerMessageType(Category.Config, VIZ_POSITION_CONFIG,                null, PositionConfig.class, FreeFlyerConstants.POSITION_CONFIG_VIZ_TOPIC, "RapidPositionConfigProfile");
    public static MessageType  VIZ_POSITION_SAMPLE_TYPE = new FreeFlyerMessageType(Category.Sample, VIZ_POSITION_SAMPLE, VIZ_POSITION_CONFIG, PositionSample.class, FreeFlyerConstants.POSITION_SAMPLE_VIZ_TOPIC, "RapidPositionSampleProfile");
 
    public static final String HEALTH_AGENT_CONFIG  = "HealthAgentConfig";
    public static final String HEALTH_AGENT_STATE   = "HealthAgentState";
    public static MessageType  HEALTH_AGENT_CONFIG_TYPE = new FreeFlyerMessageType(Category.Config, HEALTH_AGENT_CONFIG,               null, AgentConfig.class, FreeFlyerConstants.AGENT_STATE_CONFIG_HEALTH_TOPIC, "RapidAgentConfigProfile");
    public static MessageType  HEALTH_AGENT_STATE_TYPE  = new FreeFlyerMessageType(Category.Sample, HEALTH_AGENT_STATE, HEALTH_AGENT_CONFIG, AgentState.class,  FreeFlyerConstants.AGENT_STATE_SAMPLE_HEALTH_TOPIC, "RapidAgentStateProfile");
 
    public static final String DEBUG_AGENT_CONFIG  = "DebugAgentConfig";
    public static final String DEBUG_AGENT_STATE   = "DebugAgentState";
    public static MessageType  DEBUG_AGENT_CONFIG_TYPE = new FreeFlyerMessageType(Category.Config, DEBUG_AGENT_CONFIG,              null, AgentConfig.class, FreeFlyerConstants.AGENT_STATE_CONFIG_DEBUG_TOPIC, "RapidAgentConfigProfile");
    public static MessageType  DEBUG_AGENT_STATE_TYPE  = new FreeFlyerMessageType(Category.Sample, DEBUG_AGENT_STATE, DEBUG_AGENT_CONFIG, AgentState.class,  FreeFlyerConstants.AGENT_STATE_SAMPLE_DEBUB_TOPIC, "RapidAgentStateProfile");
    
	protected final String name;
	protected final Class  dataType;
	
	/**
	 * ctor. Auto-adds new type to message map
	 * @param name
	 * @param dataType
	 */
    public FreeFlyerMessageType(Category category, String name, String configName, Class dataType, String topic, String qosProfile) {
    	super(category, name, configName, dataType, topic, qosProfile);
    	this.name = name;
		this.dataType = dataType;
		s_messageMap.put(name, this);
	}
	
	@Override
	public boolean equals(Object o) {
	    if(o instanceof String) {
	        return name.equals(o);
	    }
	    return super.equals(o);
	}
	
	/** 
	 * get message type from message name
	 * @param name
	 * @return
	 */
	public static FreeFlyerMessageType get(String name) {
		return s_messageMap.get(name);
	}
	
	/**
	 * remove type from message map
	 * @param name
	 * @return
	 */
	public static boolean remove(String name) {
		FreeFlyerMessageType exists = s_messageMap.remove(name);
		return (exists != null);
	}
	
	public static boolean contains(String name) {
		return s_messageMap.keySet().contains(name);
	}
	
	/**
	 * get all message names
	 * @return
	 */
	public static Set<String> getMessageNames() {
		return s_messageMap.keySet();
	}
	
    @Override
	public String toString() {
        return name;
    }

    @Override
	public String name() {
        return name;
    }

    public String getName() {
        return name;
    }

    @Override
	public Class getDataTypeClass() {
		return dataType;
	}
     
    public static MessageType getTypeFromTopic(String topic) {
        for(FreeFlyerMessageType type : s_messageMap.values()) {
            if(type.topic.equals(topic)) {
                return type;
            }
        }
        return null;
    }
    
    /**
     * @param name
     * @return MessageType corresponding to name if it exists. If name is null, return null
     * without throwing an exception
     * @throws IllegalArgumentException
     */
    public static MessageType valueOf(String name) throws IllegalArgumentException {
        if(name == null) 
            return null;
        MessageType retVal = s_messageMap.get(name);
        if(retVal == null) {
            throw new IllegalArgumentException("FreeFlyerMessageType "+name+" does not exist");
        }
        return retVal;
    }
}
