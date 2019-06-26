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

import gov.nasa.util.ThreadUtils;

import java.util.Map;

import org.apache.log4j.Logger;

import rapid.Command;
import rapid.CommandConfig;
import rapid.CommandDef;

import com.google.common.collect.Maps;

/**
 *
 */
public class CommandHelperCache {
    private static final Logger logger = Logger.getLogger(CommandHelperCache.class);

    protected final CommandConfigHelper m_cmdConfigHelper;
    protected final Map<CommandHelper.Key,CommandHelper> m_cache = Maps.newHashMap();

    public CommandHelperCache(CommandConfig cmdConfig) {
        m_cmdConfigHelper = new CommandConfigHelper(cmdConfig);
    }

    /**
     * If CommandHelper corresponding to cmd already exists, cmd is
     * set on the helper and returned. If not, a helper will be created 
     * and returned. 
     * @param cmd
     * @return
     */
    public CommandHelper get(Command cmd) {
        final String subsystem = cmd.subsysName;
        final String command = cmd.cmdName;
        CommandHelper retVal = null;
        CommandHelper.Key key = new CommandHelper.Key(cmd);
        retVal = m_cache.get(key);
        if(retVal == null) {
            CommandDef cmdDef = m_cmdConfigHelper.getCommandDef(subsystem, command);
            if(cmdDef != null) {
                retVal = new CommandHelper(cmdDef, cmd);
                m_cache.put(key, retVal);
            }
            else {
                logger.debug("No CommandDef found for "+subsystem+"::"+command+" - non existent command? "+
                        "(called from "+ThreadUtils.traceCaller()+")");
            }
        }
        else {
            retVal.setCommand(cmd);
        }
        return retVal;
    }
}
