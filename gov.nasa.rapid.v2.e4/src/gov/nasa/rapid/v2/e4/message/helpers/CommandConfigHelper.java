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
package gov.nasa.rapid.v2.e4.message.helpers;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import rapid.CommandConfig;
import rapid.CommandDef;
import rapid.Subsystem;
import rapid.SubsystemType;

public class CommandConfigHelper {

    protected int                             m_serialId    = 0;
    protected final Map<String,CommandDefMap> m_subsysDef   = new HashMap<String,CommandDefMap>();
    protected final Map<String,String>        m_availSubsys = new HashMap<String,String>();

    public CommandConfigHelper(CommandConfig cmdConfig) {
        m_serialId = cmdConfig.hdr.serial;
        int len = cmdConfig.availableSubsystems.userData.size();
        for(int i = 0; i < len; i++) {
            Subsystem ss = (Subsystem)cmdConfig.availableSubsystems.userData.get(i);
            m_availSubsys.put(ss.name, ss.subsystemTypeName);
        }
        len = cmdConfig.availableSubsystemTypes.userData.size();
        for(int i = 0; i < len; i++) {
            SubsystemType sst = (SubsystemType)cmdConfig.availableSubsystemTypes.userData.get(i);
            m_subsysDef.put(sst.name, new CommandDefMap(sst.commands));
        }
    }
    
    public Set<String> getAvailableSubsystems() {
        return m_availSubsys.keySet();
    }
    
    public String getSubsystemType(String subsysName) {
        return m_availSubsys.get(subsysName);
    }

    public boolean hasCommand(String subsysName, String cmdName) {
        return getCommandDef(subsysName, cmdName) != null;
    }
    
    public CommandDef getCommandDef(String subsysName, String cmdName) {
        String sstName = m_availSubsys.get(subsysName);
        if(sstName != null) {
            CommandDefMap cdm = m_subsysDef.get(sstName);
            if(cdm != null) {
                return cdm.get(cmdName);
            }
        }
        return null;
    }
    
    public int getSerialId() {
        return m_serialId;
    }
    
    /**
     * assign local data to cmdConfig
     * @param cmdConfig
     */
    public void assign(CommandConfig cmdConfig) {
        cmdConfig.hdr.serial = m_serialId;
        cmdConfig.availableSubsystems.userData.clear();
        for(String key : m_availSubsys.keySet()) {
            Subsystem ss = new Subsystem();
            ss.name = key;
            ss.subsystemTypeName = m_availSubsys.get(key);
            cmdConfig.availableSubsystems.userData.add(ss);
        }
        cmdConfig.availableSubsystemTypes.userData.clear();
        for(String key : m_subsysDef.keySet()) {
            SubsystemType sst = new SubsystemType();
            sst.name = key;
            m_subsysDef.get(key).assign(sst.commands);
            cmdConfig.availableSubsystemTypes.userData.add(sst);
        }
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("availableSubsystems:\n");
        for(String key : m_availSubsys.keySet()) {
            builder.append("    "+key+" (subsystemType="+m_availSubsys.get(key)+")\n");
        }
        builder.append("subsystemTypes:\n");
        for(String key : m_subsysDef.keySet()) {
            builder.append("    "+key+"\n");
            CommandDefMap cmdMap = m_subsysDef.get(key);
            for(String cmdKey : cmdMap.keySet()) {
                builder.append("      "+CommandDefHelper.toString(cmdMap.get(cmdKey))+"\n");
            }
        }
        return builder.toString();
    }
}
