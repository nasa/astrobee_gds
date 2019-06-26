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
package gov.nasa.rapid.v2.e4.message.cache;


import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.e4.message.MessageType;
import gov.nasa.rapid.v2.e4.message.helpers.MacroConfigHelper;

import java.util.Arrays;
import java.util.Map;

import rapid.MacroConfig;

import com.google.common.collect.Maps;

/**
 * The MacroConfig message has one keyed fields (name) to allow
 * multiple instances of MacroConfig messages to share the same topic. 
 * @author mallan
 */
public class MacroConfigCache {
    //private static final Logger logger = Logger.getLogger(MacroConfigCache.class);

    protected final Agent                 m_agent;
    protected final MessageType           m_macroConfigType;
    protected final Map<String,MacroConfigHelper> m_cache = Maps.newHashMap();

    public MacroConfigCache(Agent agent) {
        this(agent, MessageType.MACRO_CONFIG_TYPE);
    }
    
    public MacroConfigCache(Agent agent, MessageType macroConfigType) {
        m_agent = agent;
        m_macroConfigType = macroConfigType;
    }

    public void add(MacroConfig config) {
        String key = config.name;
        m_cache.put(key, new MacroConfigHelper(config));
    }

    public void remove(String macroName) {
        m_cache.remove(macroName);
    }
    
    public void clear() {
        m_cache.clear();
    }
    
    /**
     * @return sorted list of Macro names
     */
    public String[] getMacroNames() {
        String[] retVal = m_cache.keySet().toArray(new String[m_cache.keySet().size()]);
        Arrays.sort(retVal);
        return retVal;
    }

    /** 
     * look up a MacroConfig from name
     */
    public MacroConfigHelper get(String key) {
        MacroConfigHelper retVal = m_cache.get(key);
        return retVal;
    }

}
