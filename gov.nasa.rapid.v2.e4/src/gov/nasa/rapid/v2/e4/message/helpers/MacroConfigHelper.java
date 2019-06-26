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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import rapid.KeyTypeValueTriple;
import rapid.MacroCommand;
import rapid.MacroConfig;
import rapid.ParameterUnion;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class MacroConfigHelper {
    
    protected MacroConfig                   m_macroConfig = null;
    protected LinkedHashMap<String,MacroCommand>  m_cmdMap = Maps.newLinkedHashMap();
    protected HashMap<String,ParameterList>   m_cmdMetaMap = Maps.newHashMap();
    
    public MacroConfigHelper(MacroConfig macroConfig) {
        init(macroConfig);
    }
    
    public void init(MacroConfig macroConfig) {
        m_macroConfig = macroConfig;
        m_cmdMap.clear();
        m_cmdMetaMap.clear();
        for(int i = 0; i < macroConfig.commands.userData.size(); i++) {
            MacroCommand cmd = (MacroCommand)macroConfig.commands.userData.get(i);
            m_cmdMap.put(cmd.cmdIdSuffix, cmd);
            ParameterList meta = new ParameterList(macroConfig.commandMetaDataSpec.userData);
            meta.set(cmd.metaData.userData);
            m_cmdMetaMap.put(cmd.cmdIdSuffix, meta);
        }
    }
    
    List<MacroCommand> getMacroCommands() {
        ArrayList<MacroCommand> retVal = Lists.newArrayList();
        return retVal;
    }
        
    public MacroConfig getMacroConfig() {
        return m_macroConfig;
    }
    
    public MacroCommand getMacroCommand(String cmdIdSuffix) {
        return m_cmdMap.get(cmdIdSuffix);
    }
    
    public ParameterList getCommandMeta(String cmdIdSuffix) {
        return m_cmdMetaMap.get(cmdIdSuffix);
    }
    
    @Override
    public String toString() {
        return toString(m_macroConfig);
    }
    
    public static String toString(MacroConfig config) {
        StringBuilder sb = new StringBuilder();
        int version = config.hdr.serial;
        sb.append("MacroConfig: ").append(config.name);
        sb.append(" (version ").append(version);
        if(version > 47 && version < 123) {
            char c = (char)version;
            sb.append(" / ").append(c);
        }
        sb.append(")").append("\n");
        for(int i = 0; i < config.commands.userData.size(); i++) {
            MacroCommand cmd = (MacroCommand)config.commands.userData.get(i);
            sb.append(String.format("  % 3d) ", i));
            sb.append(cmd.subsysName).append("::").append(cmd.cmdName).append("(");
            for(int j = 0; j < cmd.arguments.userData.size(); j++) {
                ParameterUnion pu = (ParameterUnion)cmd.arguments.userData.get(j);
                if(j > 0)
                    sb.append(", ");
                sb.append(ParamHelper.valueString(pu));
            }
            sb.append(")\n");
        }
        if(config.metaData.userData.size() > 0) {
            sb.append("MetaData:\n");
            for(int i = 0; i < config.metaData.userData.size(); i++) {
                KeyTypeValueTriple ktv = (KeyTypeValueTriple)config.metaData.userData.get(i);
                String md = String.format("%20s = %s", ktv.key, ParamHelper.valueString(ktv.value));
                sb.append("    ").append(md).append("\n");
            }
        }

        return sb.toString();
    }

}
